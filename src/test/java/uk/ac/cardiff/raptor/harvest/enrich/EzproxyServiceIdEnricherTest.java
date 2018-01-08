package uk.ac.cardiff.raptor.harvest.enrich;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.EzproxyAuthenticationEvent;
import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

public class EzproxyServiceIdEnricherTest {

	private static final Logger log = LoggerFactory.getLogger(EzproxyServiceIdEnricherTest.class);

	@Test
	public void addServiceId() {
		final EzproxyServiceIdEnricher enricher = new EzproxyServiceIdEnricher();
		enricher.setServiceId("http://idp.test.org/ezproxy");

		final EzproxyAuthenticationEvent mockEvent = new EzproxyAuthenticationEvent();
		final List<Event> events = new ArrayList<Event>();
		events.add(mockEvent);
		enricher.enrich(events);

		assertThat(events.size()).isEqualTo(1);
		assertThat(events.get(0)).isInstanceOf(EzproxyAuthenticationEvent.class);
		final EzproxyAuthenticationEvent authE = (EzproxyAuthenticationEvent) events.get(0);
		assertThat(authE.getServiceId()).isEqualTo("http://idp.test.org/ezproxy");
	}

	@Test
	public void wrongEventType() {
		final EzproxyServiceIdEnricher enricher = new EzproxyServiceIdEnricher();
		enricher.setServiceId("http://idp.test.org/ezproxy");

		final ShibbolethIdpAuthenticationEvent mockEvent = new ShibbolethIdpAuthenticationEvent();
		final List<Event> events = new ArrayList<Event>();
		events.add(mockEvent);
		enricher.enrich(events);

		assertThat(events.size()).isEqualTo(1);
		assertThat(events.get(0)).isInstanceOf(ShibbolethIdpAuthenticationEvent.class);
		final ShibbolethIdpAuthenticationEvent authE = (ShibbolethIdpAuthenticationEvent) events.get(0);
		assertThat(authE.getServiceId()).isNull();
	}

}
