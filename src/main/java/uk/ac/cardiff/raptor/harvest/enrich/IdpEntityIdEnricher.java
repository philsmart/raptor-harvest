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
@ConfigurationProperties(prefix = "harvets.shibboleth")
@ThreadSafe
public class IdpEntityIdEnricher implements AttributeEnrichment {

	private static final Logger log = LoggerFactory.getLogger(IdpEntityIdEnricher.class);

	/**
	 * The entityID of the {@link Event}s originating Identity Provider. Can be
	 * null, will not overwrite an existing value. Only applies to
	 * {@link ShibbolethIdpAuthenticationEvent}s.
	 */
	private String idpEntityID;

	@Override
	public void enrich(final List<Event> events) {
		log.info("Enriching ShibbolethIdpAuthenticationEvents in the input list of size {}", events.size());
		for (final Event event : events) {
			if (event instanceof ShibbolethIdpAuthenticationEvent) {
				if (((ShibbolethIdpAuthenticationEvent) event).getServiceId() == null && idpEntityID != null) {
					log.trace("Adding idpEntityIdp [{}] as serviceId to event [{}]", idpEntityID, event.getEventId());
					((ShibbolethIdpAuthenticationEvent) event).setServiceId(idpEntityID);
				}
			}
		}

	}

	@Override
	public String getName() {
		return "Identity Provider EntityID Enricher";
	}

	/**
	 * @return the idpEntityID
	 */
	public String getIdpEntityID() {
		return idpEntityID;
	}

	/**
	 * @param idpEntityID
	 *            the idpEntityID to set
	 */
	public void setIdpEntityID(final String idpEntityID) {
		this.idpEntityID = idpEntityID;
	}

}
