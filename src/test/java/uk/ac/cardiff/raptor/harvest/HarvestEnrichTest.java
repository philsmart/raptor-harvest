package uk.ac.cardiff.raptor.harvest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.EzproxyAuthenticationEvent;
import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;
import uk.ac.cardiff.raptor.harvest.comms.EventPublisher;
import uk.ac.cardiff.raptor.harvest.enrich.AttributeEnrichment;
import uk.ac.cardiff.raptor.harvest.enrich.EzproxyServiceIdEnricher;
import uk.ac.cardiff.raptor.harvest.parse.EzproxyLogFileParser;
import uk.ac.cardiff.raptor.harvest.parse.LogParserFactory;
import uk.ac.cardiff.raptor.harvest.parse.ShibbolethV3LogParser;

public class HarvestEnrichTest {

	private static final Logger log = LoggerFactory.getLogger(HarvestEnrichTest.class);

	@Test
	public void testHarvestEnrichShibV3() {

	}

	/**
	 * Tests the parsing and enrichment of a single
	 * {@link ShibbolethIdpAuthenticationEvent} from a temporary log file. Uses a
	 * {@link SimplePushPipeline}, and custom {@link EventPublisher}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testHarvestEnrichShibbolethV3() throws IOException {

		final ShibbolethV3LogParser parser = LogParserFactory.newShibbolethV3LogParser();

		final Path tmpLogFile = Files.createTempFile("shibv3-test-logfile", "log");

		log.debug("Temporary log file is {}", tmpLogFile);

		// populate with a know line
		Files.write(tmpLogFile,
				"20170720T130156Z|urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect|_5eb8abee2b08cad5c2259d14b6fab17c|https://test.cardiff.ac.uk/shibboleth-sp|http://shibboleth.net/ns/profiles/saml2/sso/browser|https://idp.cardiff.ac.uk/shibboleth|urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST|_bd15bc375e163439e51f19b7cd9906a2|siscdg|urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport|CardiffIdentityNo,commonName,uid,eduPersonScopedAffiliation,eduPersonAffiliation,eduPersonTargetedID,displayName,surname,givenName,distinguishedName,eduPersonTargetedID.old,email|AAdzZWNyZXQx8X1daWNi93oEf8m5qSWTTl4d1I1pBDNQ2FIK0t3DMs7Sn6oskYfzrRM+4sqLo/dxf/yGaFoxk639Ck4XPVKQiSmUuDjo8JIzOqCJQLJnANjdveIR0vB0tP7X8i3EV81JEwA0fwTCgJVKNxSex3g=|_685d962193919df7b0a3aa0fa51fd324|"
						.getBytes(StandardCharsets.UTF_8));

		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();
		assertThat(events).hasSize(1);
		assertThat(events.iterator().next()).isInstanceOf(ShibbolethIdpAuthenticationEvent.class);

		final SimplePushPipeline pipeline = new SimplePushPipeline();

		final List<AttributeEnrichment> enrichers = new ArrayList<>();

		pipeline.setEnrichers(enrichers);

		pipeline.setEventPublisher(events1 -> {
			log.info("Publisher has event [{}]", events1);
			assertThat(events1.iterator().next()).isInstanceOf(ShibbolethIdpAuthenticationEvent.class);
			final ShibbolethIdpAuthenticationEvent authE = (ShibbolethIdpAuthenticationEvent) events1.iterator().next();
			assertThat(authE.getServiceId()).isEqualTo("https://idp.cardiff.ac.uk/shibboleth");
			assertThat(authE.getResourceId()).isEqualTo("https://test.cardiff.ac.uk/shibboleth-sp");
			assertThat(authE.getPrincipalName()).isEqualTo("siscdg");
		});

		pipeline.pushPipeline(new ArrayList<Event>(events));

		tmpLogFile.toFile().delete();

	}

	/**
	 * Tests the parsing and enrichment of a single
	 * {@link EzproxyAuthenticationEvent} from a temporary log file. Uses a
	 * {@link SimplePushPipeline}, and custom {@link EventPublisher}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testHarvestEnrichEzproxy() throws IOException {

		final EzproxyLogFileParser parser = LogParserFactory.newEzproxyLogFileParser("@testscope.com");

		final Path tmpLogFile = Files.createTempFile("ezproxy-test-logfile", "log");

		log.debug("Temporary log file is {}", tmpLogFile);

		// populate with a know line
		Files.write(tmpLogFile,
				"31.205.175.2 JOt2SNph8RJRtkb A2211362R@testscope.com [23/Jun/2017:00:04:42 +0000] \"GET https://abc.cardiff.ac.uk:443/connect?session=sJOt2SNph8RJRtkb&qurl=http%3a%2f%2fwww.sciencedirect.com%2fscience%2farticle%2fpii%2fS1618866711000963 HTTP/1.1\" 302 0"
						.getBytes(StandardCharsets.UTF_8));

		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();
		assertThat(events).hasSize(1);
		assertThat(events.iterator().next()).isInstanceOf(EzproxyAuthenticationEvent.class);

		final SimplePushPipeline pipeline = new SimplePushPipeline();

		final EzproxyServiceIdEnricher enricher = new EzproxyServiceIdEnricher();
		enricher.setServiceId("http://idp.test.org/ezproxy");
		final List<AttributeEnrichment> enrichers = new ArrayList<>();
		enrichers.add(enricher);
		pipeline.setEnrichers(enrichers);

		pipeline.setEventPublisher(events1 -> {
			log.info("Publisher has event [{}]", events1);
			assertThat(events1.iterator().next()).isInstanceOf(EzproxyAuthenticationEvent.class);
			final EzproxyAuthenticationEvent authE = (EzproxyAuthenticationEvent) events1.iterator().next();
			assertThat(authE.getServiceId()).isEqualTo("http://idp.test.org/ezproxy");
			assertThat(authE.getResourceId()).isEqualTo("http://www.sciencedirect.com");
			assertThat(authE.getPrincipalName()).isEqualTo("A2211362R");
		});

		pipeline.pushPipeline(new ArrayList<Event>(events));

		tmpLogFile.toFile().delete();

	}

}
