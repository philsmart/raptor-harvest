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

public class IdpEntityIdEnricherTest {

	private static final Logger log = LoggerFactory.getLogger(IdpEntityIdEnricherTest.class);

	@Test
	public void testEnrichShibbolethEntityIDNoExisting() {
		final IdpEntityIdEnricher enricher = new IdpEntityIdEnricher();
		enricher.setIdpEntityID("http://idp.test.org/shibboleth");

		final ShibbolethIdpAuthenticationEvent mockEvent = new ShibbolethIdpAuthenticationEvent();

		final List<Event> events = new ArrayList<Event>();
		events.add(mockEvent);
		enricher.enrich(events);

		assertThat(events.size()).isEqualTo(1);
		assertThat(events.get(0)).isInstanceOf(ShibbolethIdpAuthenticationEvent.class);
		final ShibbolethIdpAuthenticationEvent shibE = (ShibbolethIdpAuthenticationEvent) events.get(0);
		assertThat(shibE.getServiceId()).isEqualTo("http://idp.test.org/shibboleth");
	}

	@Test
	public void testEnrichShibbolethEntityIDWithExisting() {
		final IdpEntityIdEnricher enricher = new IdpEntityIdEnricher();
		enricher.setIdpEntityID("http://idp.test.org/shibboleth");

		final ShibbolethIdpAuthenticationEvent mockEvent = new ShibbolethIdpAuthenticationEvent();
		mockEvent.setServiceId("http://idp.test.org/original");

		final List<Event> events = new ArrayList<Event>();
		events.add(mockEvent);
		enricher.enrich(events);

		assertThat(events.size()).isEqualTo(1);
		assertThat(events.get(0)).isInstanceOf(ShibbolethIdpAuthenticationEvent.class);
		final ShibbolethIdpAuthenticationEvent shibE = (ShibbolethIdpAuthenticationEvent) events.get(0);
		assertThat(shibE.getServiceId()).isEqualTo("http://idp.test.org/original");
	}

	@Test
	public void testEnrichEzproxyNoException() {
		final IdpEntityIdEnricher enricher = new IdpEntityIdEnricher();
		enricher.setIdpEntityID("http://idp.test.org/shibboleth");

		final EzproxyAuthenticationEvent mockEvent = new EzproxyAuthenticationEvent();

		final List<Event> events = new ArrayList<Event>();
		events.add(mockEvent);
		enricher.enrich(events);

		assertThat(events.size()).isEqualTo(1);
		assertThat(events.get(0)).isInstanceOf(EzproxyAuthenticationEvent.class);

	}

}
