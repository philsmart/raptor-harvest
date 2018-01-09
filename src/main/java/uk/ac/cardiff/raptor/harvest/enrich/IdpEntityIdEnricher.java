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
import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

/**
<<<<<<< HEAD
 * Adds the idpEntityID to any
 * {@link ShibbolethIdpAuthenticationEvent#setServiceId(String)} from the input
 * {@link List} of {@link Event}s. Can be null, hence will not set. Only sets
 * the serviceId if one does not already exist.
 * 
=======
 * Adds the idpEntityID to any {@link ShibbolethIdpAuthenticationEvent} from the
 * input {@link List} of {@link Event}s. Can be null, hence will not set. Only
 * sets the values if one does not already exist.
 *
>>>>>>> 30b5806520f1ecbdd733222c390b9a2c310ce675
 * @author philsmart
 *
 */
@Component
@ConfigurationProperties(prefix = "harvest.shibbolethv2")
@ConditionalOnProperty(prefix = "harvest.shibbolethv2", name = "logfile")
@ThreadSafe
public class IdpEntityIdEnricher implements AttributeEnrichment {

	private static final Logger log = LoggerFactory.getLogger(IdpEntityIdEnricher.class);

	/**
<<<<<<< HEAD
	 * The entityID of the {@link Event}s originating Identity Provider. Can not be
	 * null. Will not overwrite an existing value. Only applies to
	 * {@link ShibbolethIdpAuthenticationEvent}s for the Shibboleth V2 parser.
	 */
	private String idpEntityId;

	@PostConstruct
	public void init() {
		Objects.requireNonNull(idpEntityId,
				"if using a Shibboleth v2.x harvester, the property harvest.shibbolethv2.idp-entity-id must be set");
		log.info("Auto-creating Shibboleth v2.x EntityID as serviceID enricher");
	}

=======
	 * The entityID of the {@link Event}s originating Identity Provider. Can be
	 * null for no-op functionality. Will not overwrite an existing value. Only
	 * applies to {@link ShibbolethIdpAuthenticationEvent}s for the Shibboleth
	 * V2 parser.
	 */
	private String idpEntityId;

	/**
	 * For each {@link Event} in {@code events}, enriches it iff it is a
	 * {@link ShibbolethIdpAuthenticationEvent} and the {@code idpEntityId} is
	 * *not* null, and the
	 * {@link ShibbolethIdpAuthenticationEvent#getServiceId()} of the event
	 * being processed *is* null.
	 *
	 * @param events
	 *            the {@link List} of {@link Event}s to process.
	 */
>>>>>>> 30b5806520f1ecbdd733222c390b9a2c310ce675
	@Override
	public void enrich(final List<Event> events) {
		log.info(
				"Enriching ShibbolethIdpAuthenticationEvents in the input list of size {}, has idp-entity-id specified [{}]",
				events.size(), idpEntityId != null);
		for (final Event event : events) {

			if (event instanceof ShibbolethIdpAuthenticationEvent) {

<<<<<<< HEAD
				if (((ShibbolethIdpAuthenticationEvent) event).getServiceId() == null && idpEntityId != null) {
					log.trace("Adding idpEntityIdp [{}] as serviceId to eventID [{}]", idpEntityId, event.getEventId());
=======
				if ((((ShibbolethIdpAuthenticationEvent) event).getServiceId() == null) && (idpEntityId != null)) {
					log.trace("Adding idpEntityIdp [{}] as serviceId to event [{}]", idpEntityId, event.getEventId());
>>>>>>> 30b5806520f1ecbdd733222c390b9a2c310ce675
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
