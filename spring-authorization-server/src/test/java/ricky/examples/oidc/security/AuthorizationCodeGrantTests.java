package ricky.examples.oidc.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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
public class AuthorizationCodeGrantTests {

  @LocalServerPort private int port;

  @Autowired private RestTemplateBuilder restTemplateBuilder;

  @SuppressWarnings({"unchecked", "null", "rawtypes"})
  // @Test
  void testAuthorizationCodeGrant() throws URISyntaxException {
    log.debug("######### testAuthorizationCodeGrant #########");
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

    // Step 1: 사용자 로그인 및 Authorization Code 요청
    String authorizeUrl = "http://localhost:" + port + "/oauth2/authorize";
    String tokenUrl = "http://localhost:" + port + "/oauth2/token";

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth("petclinic-resource", "secret");
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
    loginParams.add("username", "user");
    loginParams.add("password", "password");

    HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<>(loginParams, headers);
    ResponseEntity<String> loginResponse =
        restTemplate.postForEntity(
            "http://localhost:" + port + "/login", loginRequest, String.class);

    assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    MultiValueMap<String, String> authorizeParams = new LinkedMultiValueMap<>();
    authorizeParams.add(OAuth2ParameterNames.RESPONSE_TYPE, "code");
    authorizeParams.add(OAuth2ParameterNames.CLIENT_ID, "petclinic-resource");
    authorizeParams.add(OAuth2ParameterNames.REDIRECT_URI, "http://127.0.0.1:8080/authorized");
    authorizeParams.add(OAuth2ParameterNames.SCOPE, "openid read write");

    URI authorizeUri =
        new URI(
            authorizeUrl
                + "?response_type=code&client_id=petclinic-resource&redirect_uri=http://127.0.0.1:8080/authorized&scope=openid"
                + " read write");

    ResponseEntity<Void> authorizeResponse =
        restTemplate.exchange(authorizeUri, HttpMethod.GET, new HttpEntity<>(headers), Void.class);
    URI location = authorizeResponse.getHeaders().getLocation();

    assertThat(location).isNotNull();
    String authorizationCode = extractAuthorizationCode(location);

    // Step 2: Authorization Code를 Access Token으로 교환
    MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
    tokenParams.add(OAuth2ParameterNames.GRANT_TYPE, "authorization_code");
    tokenParams.add(OAuth2ParameterNames.CODE, authorizationCode);
    tokenParams.add(OAuth2ParameterNames.REDIRECT_URI, "http://127.0.0.1:8080/authorized");
    tokenParams.add(OAuth2ParameterNames.CLIENT_ID, "petclinic-resource");

    HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams, headers);
    ResponseEntity<Map> tokenResponse =
        restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenRequest, Map.class);

    assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(tokenResponse.getBody()).containsKey("access_token");
    assertThat(tokenResponse.getBody()).containsKey("token_type");
    assertThat(tokenResponse.getBody().get("token_type")).isEqualTo("Bearer");
  }

  private String extractAuthorizationCode(URI location) {
    // 실제 응답에서 Authorization Code를 추출하는 로직 구현 필요
    String query = location.getQuery();
    for (String param : query.split("&")) {
      String[] pair = param.split("=");
      if ("code".equals(pair[0])) {
        return pair[1];
      }
    }
    throw new IllegalStateException("Authorization code not found in response");
  }
}
