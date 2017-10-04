package uk.ac.cardiff.raptor.harvest.parse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;

public class ShibbolethLogParserTest {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParserTest.class);

	@Test
	public void testParseIdp3() throws IOException {

		final ShibbolethV3LogParser parser = LogParserFactory.newShibbolethV3LogParser();

		// create tmp file
		final Path tmpLogFile = Files.createTempFile("shibboleth-test-logfile", "log");

		// populate with a know line
		Files.write(tmpLogFile,
				"20170720T130156Z|urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect|_5eb8abee2b08cad5c2259d14b6fab17c|https://squiz.cardiff.ac.uk/shibboleth-sp|http://shibboleth.net/ns/profiles/saml2/sso/browser|https://idp.cardiff.ac.uk/shibboleth|urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST|_bd15bc375e163439e51f19b7cd9906a2|siscdg|urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport|CardiffIdentityNo,commonName,uid,eduPersonScopedAffiliation,eduPersonAffiliation,eduPersonTargetedID,displayName,surname,givenName,distinguishedName,eduPersonTargetedID.old,email|AAdzZWNyZXQx8X1daWNi93oEf8m5qSWTTl4d1I1pBDNQ2FIK0t3DMs7Sn6oskYfzrRM+4sqLo/dxf/yGaFoxk639Ck4XPVKQiSmUuDjo8JIzOqCJQLJnANjdveIR0vB0tP7X8i3EV81JEwA0fwTCgJVKNxSex3g=|_685d962193919df7b0a3aa0fa51fd324|"
						.getBytes(StandardCharsets.UTF_8));

		// parse result
		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();

		// check against known result.
		log.info("Has parsed {} events", events.size());
		events.forEach(e -> log.debug("Has Parsed {}", e));
		assertThat(events).hasSize(1);

		assertThat(events.iterator().next().getResourceId()).isEqualTo("https://squiz.cardiff.ac.uk/shibboleth-sp");

		// delete file on exit
		tmpLogFile.toFile().delete();
	}

	@Test
	public void testParseOmittedIdp3() throws IOException {

		final ShibbolethV3LogParser parser = LogParserFactory.newShibbolethV3LogParser();

		// create tmp file
		final Path tmpLogFile = Files.createTempFile("shibboleth-test-logfile", "log");

		// populate with a know line
		Files.write(tmpLogFile,
				"20170721T102200Z|urn:oasis:names:tc:SAML:2.0:bindings:SOAP|_d810126e1f972e8eff6f1a4f60c3dcfd|https://test.ukfederation.org.uk/entity|http://shibboleth.net/ns/profiles/saml2/query/artifact|https://test-idp.ukfederation.org.uk/idp/shibboleth|urn:oasis:names:tc:SAML:2.0:bindings:SOAP|_a87a86a763ca650a330e852f9ecffe1a||||||"
						.getBytes(StandardCharsets.UTF_8));

		// parse result
		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();

		// check against known result.
		log.info("Has parsed {} events", events.size());

		assertThat(events).hasSize(0);

		// delete file on exit
		tmpLogFile.toFile().delete();
	}

	@Test
	public void testParseIdp2() throws IOException {

		final ShibbolethLogParser parser = LogParserFactory.newShibbolethLogParser();

		// create tmp file
		final Path tmpLogFile = Files.createTempFile("shibboleth-test-logfile", "log");

		// populate with a know line
		Files.write(tmpLogFile,
				"20170109T000037Z|urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect|_04b126adf7d429c0dd155daba317bf88|https://squiz.cardiff.ac.uk/shibboleth-sp|urn:mace:shibboleth:2.0:profiles:saml2:sso|https://idp.cardiff.ac.uk/shibboleth|urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST|_58b88af3fbc972b3c6f42c157e54d4af|c1304440|urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport|uid,eduPersonAffiliation,surname,eduPersonScopedAffiliation,eduPersonTargetedID.old,distinguishedName,CardiffIdentityNo,transientId,email,givenName,eduPersonEntitlement,commonName,eduPersonTargetedID,displayName,|_7ed849cb8aa501dca40b9bd220d5caed||"
						.getBytes(StandardCharsets.UTF_8));

		// parse result
		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();

		// check against known result.
		log.info("Has parsed {} events", events.size());
		events.forEach(e -> log.debug("Has Parsed {}", e));
		assertThat(events).hasSize(1);

		// delete file on exit
		tmpLogFile.toFile().delete();
	}

}
