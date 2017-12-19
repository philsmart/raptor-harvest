package uk.ac.cardiff.raptor.harvest.enrich;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

/**
 * Adds the idpEntityID to any {@link ShibbolethIdpAuthenticationEvent} from the
 * input {@link List} of {@link Event}s. Can be null, hence will not set. Only
 * sets the values if one does not already exist.
 * 
 * @author philsmart
 *
 */
@Component
@ConfigurationProperties(prefix = "harvest.shibbolethv2")
@ThreadSafe
public class IdpEntityIdEnricher implements AttributeEnrichment {

	private static final Logger log = LoggerFactory.getLogger(IdpEntityIdEnricher.class);

	/**
	 * The entityID of the {@link Event}s originating Identity Provider. Can be null
	 * for no-op functionality. Will not overwrite an existing value. Only applies
	 * to {@link ShibbolethIdpAuthenticationEvent}s for the Shibboleth V2 parser.
	 */
	private String idpEntityId;

	@Override
	public void enrich(final List<Event> events) {
		log.info(
				"Enriching ShibbolethIdpAuthenticationEvents in the input list of size {}, has idp-entity-id specified [{}]",
				events.size(), idpEntityId != null);
		for (final Event event : events) {

			if (event instanceof ShibbolethIdpAuthenticationEvent) {

				if (((ShibbolethIdpAuthenticationEvent) event).getServiceId() == null && idpEntityId != null) {
					log.trace("Adding idpEntityIdp [{}] as serviceId to event [{}]", idpEntityId, event.getEventId());
					((ShibbolethIdpAuthenticationEvent) event).setServiceId(idpEntityId);
				}
			}
		}

	}

	@Override
	public String getName() {
		return "Identity Provider EntityID Enricher for Shibboleth V2 log files";
	}

	/**
	 * 
	 * 
	 * /**
	 * 
	 * @return the idpEntityId
	 */
	public String getIdpEntityId() {
		return idpEntityId;
	}

	/**
	 * @param idpEntityId
	 *            the idpEntityId to set
	 */
	public void setIdpEntityId(final String idpEntityId) {
		this.idpEntityId = idpEntityId;
	}

}
