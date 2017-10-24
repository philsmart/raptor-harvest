package uk.ac.cardiff.raptor.harvest.enrich;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

public class ClientMetadataTest {

	private static final Logger log = LoggerFactory.getLogger(ClientMetadataTest.class);

	@Test
	public void setClientMetadataFresh() {

		final ClientMetadata client = new ClientMetadata();
		client.setEntityId("http://test-entity.co.uk/");
		client.setOrganisationName("Test Org");
		client.setServiceName("raptor-harvest test");

		final ShibbolethIdpAuthenticationEvent mockEvent = new ShibbolethIdpAuthenticationEvent();

		final List<Event> enrichEvents = new ArrayList<Event>();
		enrichEvents.add(mockEvent);
		client.enrich(enrichEvents);

		assertThat(enrichEvents.size()).isEqualTo(1);

		assertThat(enrichEvents.get(0).getEventMetadata()).isNotNull();

		assertThat(enrichEvents.get(0).getEventMetadata().getOrganisationName()).isEqualTo("Test Org");
		assertThat(enrichEvents.get(0).getEventMetadata().getRaptorEntityId()).isEqualTo("http://test-entity.co.uk/");
		assertThat(enrichEvents.get(0).getEventMetadata().getServiceName()).isEqualTo("raptor-harvest test");

	}

}
