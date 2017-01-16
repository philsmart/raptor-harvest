package uk.ac.cardiff.raptor.harvest.enrich;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.auxiliary.EventMetadata;

@Component
@ConfigurationProperties(prefix = "service.metadata")
public class ClientMetadata implements AttributeEnrichment {

	private static final Logger log = LoggerFactory.getLogger(AttributeEnrichment.class);

	private String organisationName;

	private String entityId;

	private String serviceName;

	@Override
	public void enrich(final List<Event> events) {
		log.info("Adding ClientMetadata Information to {} events", events.size());
		for (final Event event : events) {
			final EventMetadata meta = new EventMetadata();
			meta.setEntityId(entityId);
			meta.setOrganisationName(organisationName);
			meta.setServiceName(serviceName);
			event.setEventMetadata(meta);
		}

	}

	@Override
	public String getName() {
		return "Client Metadata Attribute Enricher";
	}

	public String getOrganisationName() {
		return organisationName;
	}

	public void setOrganisationName(final String organisationName) {
		this.organisationName = organisationName;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(final String entityId) {
		this.entityId = entityId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

}
