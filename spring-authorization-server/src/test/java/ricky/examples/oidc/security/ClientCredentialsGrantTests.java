package ricky.examples.oidc.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"docker", "debug"})
public class ClientCredentialsGrantTests {

  @LocalServerPort private int port;

  @Autowired private RestTemplateBuilder restTemplateBuilder;

  @SuppressWarnings({"unchecked", "null", "rawtypes"})
  @Test
  void testClientCredentialsGrant() {
    // Arrange
    RestTemplate restTemplate =
        restTemplateBuilder
            .errorHandler(
                new DefaultResponseErrorHandler() {
                  @Override
                  public void handleError(ClientHttpResponse response) throws IOException {
                    log.error(
                        "Response error: {} {}",
                        response.getStatusCode(),
                        response.getStatusText());
                    super.handleError(response);
                  }
                })
            .build();
    String tokenEndpoint = "http://localhost:" + port + "/oauth2/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("petclinic-resource", "secret");
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add(OAuth2ParameterNames.GRANT_TYPE, "client_credentials");
    requestBody.add(OAuth2ParameterNames.SCOPE, "openid read write");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

    // Act
    log.debug("Sending request to token endpoint: {}", tokenEndpoint);
    log.debug("Request headers: {}", headers);
    log.debug("Request body: {}", requestBody);

    ResponseEntity<Map> response =
        restTemplate.exchange(tokenEndpoint, HttpMethod.POST, request, Map.class);

    // Assert
    log.debug("Response status code: {}", response.getStatusCode());
    log.debug("Response body: {}", response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsKey("access_token");
    assertThat(response.getBody()).containsKey("token_type");
    assertThat(response.getBody().get("token_type")).isEqualTo("Bearer");
  }
}
