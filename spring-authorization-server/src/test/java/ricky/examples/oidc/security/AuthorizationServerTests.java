package ricky.examples.oidc.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@ActiveProfiles({"docker", "debug"})
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationServerTests {

  @Autowired private MockMvc mockMvc;

  @Value("${server.port}")
  private int serverPort;

  @Value("${spring.security.oauth2.authorizationserver.jwk-set-endpoint}")
  private String jwkSetEndpoint;

  private String accessToken;

  @BeforeEach
  public void setUp() throws Exception {
    // 액세스 토큰을 얻기 위해 먼저 토큰 엔드포인트를 호출합니다.
    MvcResult result =
        mockMvc
            .perform(
                post("/oauth2/token")
                    .param("grant_type", "password")
                    .param("username", "ilovecorea@gmail.com")
                    .param("password", "secret")
                    .param("client_id", "test-client")
                    .param("client_secret", "secret")
                    .param("scope", "openid"))
            .andExpect(status().isOk())
            .andReturn();

    // 응답에서 액세스 토큰을 추출합니다.
    String responseBody = result.getResponse().getContentAsString();
    accessToken = JsonPath.read(responseBody, "$.access_token");
    log.debug("## AccessToken:{}", accessToken);
  }

  @Test
  public void testAuthorizeEndpoint() throws Exception {
    mockMvc
        .perform(
            get("/oauth2/authorize")
                .param("response_type", "code")
                .param("client_id", "client")
                .param("redirect_uri", "http://localhost:8080/login/oauth2/code/custom")
                .param("scope", "openid"))
        .andExpect(status().isOk());
  }

  @Test
  public void testTokenEndpoint() throws Exception {
    mockMvc
        .perform(
            post("/oauth2/token")
                .param("grant_type", "authorization_code")
                .param("code", "[AUTH_CODE]")
                .param("redirect_uri", "http://localhost:8080/login/oauth2/code/custom")
                .param("client_id", "client")
                .param("client_secret", "secret"))
        .andExpect(status().isOk());
  }

  @Test
  public void testUserInfoEndpoint() throws Exception {
    mockMvc
        .perform(get("/oauth2/userinfo").header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk());
  }

  @Test
  public void testJwksEndpoint() throws Exception {
    mockMvc.perform(get(jwkSetEndpoint)).andExpect(status().isOk());
  }
}
