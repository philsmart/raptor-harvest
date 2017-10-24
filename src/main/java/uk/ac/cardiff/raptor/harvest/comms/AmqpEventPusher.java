package uk.ac.cardiff.raptor.harvest.comms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

/**
 * AMQP Implementation of the {@link EventPush} interface. Sends each
 * {@link Event} as a single AMQP Message encoded with JSON.
 * 
 * Threadsafe after construction.
 * 
 * @author philsmart
 *
 */

@ConfigurationProperties(prefix = "event.pusher.amqp")
@Component
@ThreadSafe
public class AmqpEventPusher implements EventPush {

	private static final Logger log = LoggerFactory.getLogger(AmqpEventPusher.class);

	/**
	 * The RabbitTemplate to send AMQP events. Is threadsafe, so can share between
	 * threads.
	 */
	private RabbitTemplate amqpTemplate;

	private String host;

	private String queue = "raptor.harvest.events";

	private String exchange = "raptor";

	private String username = "raptor-user";

	private String password = "raptor-pass";

	private boolean useSsl = false;

	/**
	 * Should always be true for correct execution, can be false for testing.
	 */
	private boolean pushEnabled = true;

	@PostConstruct
	public void setup() throws Exception {

		log.info(
				"Setting up an AMQPEventPusher using queue [{}], exchange [{}], username [{}], SSL [{}], host [{}], push-enabled [{}]",
				queue, exchange, username, useSsl, host, pushEnabled);

		amqpTemplate = new RabbitTemplate(connectionFactory());
		final RetryTemplate retryTemplate = new RetryTemplate();
		final ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(1000);
		backOffPolicy.setMultiplier(10.0);
		backOffPolicy.setMaxInterval(10000);
		retryTemplate.setBackOffPolicy(backOffPolicy);
		amqpTemplate.setRetryTemplate(retryTemplate);
		amqpTemplate.setMessageConverter(messageConverter());
		amqpTemplate.setExchange(exchange);

	}

	private Jackson2JsonMessageConverter messageConverter() {
		final Jackson2JsonMessageConverter json = new Jackson2JsonMessageConverter();
		json.setJsonObjectMapper(objectMapper());
		final DefaultClassMapper eventMapper = new DefaultClassMapper();

		eventMapper.setDefaultType(ShibbolethIdpAuthenticationEvent.class);
		json.setClassMapper(eventMapper);

		return json;
	}

	private ObjectMapper objectMapper() {
		final Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();
		bean.afterPropertiesSet();
		final ObjectMapper objectMapper = bean.getObject();
		objectMapper.registerModule(new com.fasterxml.jackson.datatype.joda.JodaModule());
		return objectMapper;
	}

	private ConnectionFactory connectionFactory() throws Exception {
		final RabbitConnectionFactoryBean rabbitCon = new RabbitConnectionFactoryBean();
		rabbitCon.setUseSSL(useSsl);
		rabbitCon.setUsername(username);
		rabbitCon.setPassword(password);
		rabbitCon.setHost(host);
		// rabbitCon.setPort(5672);
		rabbitCon.afterPropertiesSet();

		final CachingConnectionFactory factory = new CachingConnectionFactory(rabbitCon.getObject());

		factory.setUsername(username);
		factory.setPassword(password);
		factory.setHost(host);
		// factory.setPort(5672);
		return factory;
	}

	@Override
	@Nonnull
	public List<Event> push(final List<Event> events) {
		if (pushEnabled == false) {
			return Collections.emptyList();
		}
		log.info("AMQP Event Push has recieved {} events to send", events.size());

		final List<Event> failures = new ArrayList<Event>();

		for (final Event event : events) {
			try {
				amqpTemplate.convertAndSend(queue, event);
			} catch (final Exception e) {
				log.error("Failed to send event [{}]", event.getEventId(), e);
				failures.add(event);
			}
		}
		log.info("AMQP Event Push has {} failures", failures.size());
		return failures;

	}

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(final String queue) {
		this.queue = queue;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(final String exchange) {
		this.exchange = exchange;
	}

	/**
	 * @return the pushEnabled
	 */
	public boolean isPushEnabled() {
		return pushEnabled;
	}

	/**
	 * @param pushEnabled
	 *            the pushEnabled to set
	 */
	public void setPushEnabled(final boolean pushEnabled) {
		this.pushEnabled = pushEnabled;
	}

	/**
	 * @return the useSsl
	 */
	public boolean isUseSsl() {
		return useSsl;
	}

	/**
	 * @param useSsl
	 *            the useSsl to set
	 */
	public void setUseSsl(final boolean useSsl) {
		this.useSsl = useSsl;
	}

}
