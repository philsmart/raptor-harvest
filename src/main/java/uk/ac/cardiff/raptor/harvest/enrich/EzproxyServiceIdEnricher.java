package uk.ac.cardiff.raptor.harvest.enrich;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.EzproxyAuthenticationEvent;
import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

/**
 * Sets the {@link ShibbolethIdpAuthenticationEvent#setServiceId(String)} from
 * the input {@link List} of {@link Event}s to that specified in the config file
 * (property name harvest.ezproxy.service-id). Overwrites any serviceID that may
 * exist.
 * 
 * @author philsmart
 *
 */
@Component
@ConfigurationProperties(prefix = "harvest.ezproxy")
@ConditionalOnProperty(prefix = "harvest.ezproxy", name = "logfile")
@ThreadSafe
public class EzproxyServiceIdEnricher implements AttributeEnrichment {

	private static final Logger log = LoggerFactory.getLogger(IdpEntityIdEnricher.class);

	/**
	 * The serviceId to set on any {@link EzproxyAuthenticationEvent}s.
	 */
	private String serviceId;

	@PostConstruct
	public void init() {
		Objects.requireNonNull(serviceId,
				"if using an ezproxy harvester, the property harvest.ezproxy.service-id must be set");
		log.info("Auto-creating EzproxyServiceIdEnricher, with serviceID [{}]", serviceId);
	}

	@Override
	public void enrich(final List<Event> events) {
		log.info("Enriching EzproxyAuthenticationEvent in the input list of size {}, has serviceID specified [{}]",
				events.size(), serviceId != null);
		for (final Event event : events) {

			if (event instanceof EzproxyAuthenticationEvent) {

				if (serviceId != null) {
					log.trace("Adding serviceID [{}] to eventID [{}]", serviceId, event.getEventId());
					((EzproxyAuthenticationEvent) event).setServiceId(serviceId);
				}
			}
		}

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the serviceId
	 */
	public String getServiceId() {
		return serviceId;
	}

	/**
	 * @param serviceId
	 *            the serviceId to set
	 */
	public void setServiceId(final String serviceId) {
		this.serviceId = serviceId;
	}

}
