package no.nav.foreldrepenger.oppslag.rest.sak;

import static io.github.resilience4j.retry.Retry.decorateSupplier;
import static java.util.stream.Collectors.joining;
import static no.nav.foreldrepenger.oppslag.util.RetryUtil.DEFAULT_RETRIES;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;

import io.github.resilience4j.retry.Retry;
import no.nav.foreldrepenger.oppslag.util.RetryUtil;

public class StsClient {

    private static final String TEMPLATE_STSENVELOPE_TXT = "/template/stsenvelope.txt";

    private static final String PASSWORDPLACEHOLDER = "%THEPASSWORD%";

    private static final String USERPLACEHOLDER = "%SOMESERVICEUSER%";

    private static final Logger LOG = LoggerFactory.getLogger(StsClient.class);

    private final RestOperations restOperations;
    private final URI stsUrl;
    private final String template;
    private final Retry retry;

    StsClient(RestOperations restOperations, URI stsUrl, String serviceUser, String servicePwd) {
        this(restOperations, stsUrl, serviceUser, servicePwd, retry());
    }

    public StsClient(RestOperations restOperations, URI stsUrl, String serviceUser, String servicePwd, Retry retry) {
        this.restOperations = restOperations;
        this.stsUrl = stsUrl;
        this.retry = retry;
        this.template = readTemplate(serviceUser, servicePwd);
    }

    String oidcToSamlToken(String oidcToken) {
        LOG.trace("Utfører OIDC til SAML veksling fra {}", stsUrl);
        String respons = postWithRetry(new HttpEntity<>(body(oidcToken), headers()));
        LOG.trace("OIDC til SAML token veksling OK");
        return samlAssertionFra(respons);
    }

    String injectToken(String oidcToken) {
        return template.replace("%OIDCTOKEN%", oidcToken);
    }

    private String postWithRetry(HttpEntity<String> request) {
        return decorateSupplier(retry, () -> restOperations.postForObject(stsUrl, request, String.class)).get();
    }

    private String body(String oidcToken) {
        return injectToken(encode(oidcToken));
    }

    private static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, TEXT_XML_VALUE);
        headers.set("SOAPAction", "http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue");
        return headers;
    }

    private String encode(String oidcToken) {
        return Base64.getEncoder().encodeToString(oidcToken.getBytes());
    }

    private static String readTemplate(String serviceUser, String servicePwd) {
        try (InputStream stream = StsClient.class.getResourceAsStream(TEMPLATE_STSENVELOPE_TXT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines()
                    .collect(joining("\n"))
                    .replace(USERPLACEHOLDER, serviceUser)
                    .replace(PASSWORDPLACEHOLDER, servicePwd);
        } catch (Exception e) {
            throw new IllegalStateException("Error while reading SOAP request template", e);
        }
    }

    static String samlAssertionFra(String envelope) {
        return envelope.substring(envelope.indexOf("<saml2:Assertion"), envelope.indexOf("</saml2:Assertion>") + 18);
    }

    private static Retry retry() {
        return RetryUtil.retry(DEFAULT_RETRIES, "STS", HttpServerErrorException.class, LOG);
    }

    @Override
    public String toString() {
        return "StsClient [restOperations=" + restOperations + ", stsUrl=" + stsUrl + ", template=" + template
                + ", retry=" + retry + "]";
    }
}
