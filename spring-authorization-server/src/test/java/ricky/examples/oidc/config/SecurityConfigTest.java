package ricky.examples.oidc.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"docker", "debug"})
class SecurityConfigTest {

  @Autowired MockMvc mockMvc;

  @SuppressWarnings("null")
  @Test
  void testOAuth2AuthorizationFlow() throws Exception {
    // 1. 사용자 인증 및 인가 코드 발급 요청
    MvcResult authorizationResult =
        mockMvc
            .perform(
                get("/oauth2/authorize")
                    .queryParam("response_type", "code")
                    .queryParam("client_id", "test-client")
                    .queryParam(
                        "redirect_uri", "http://localhost:8080/login/oauth2/code/test-client")
                    .queryParam("scope", "openid read write")
                    .queryParam("state", "test-state")
                    .with(user("ilovecorea@gmail.com").password("secret").roles("ADMIN", "USER")))
            .andExpect(status().isOk())
            .andReturn();

    // 2. 동의 페이지 처리
    // HTML 응답 본문에서 필요한 값을 추출
    String responseBody = authorizationResult.getResponse().getContentAsString();
    String state = extractValue(responseBody, "name=\"state\" value=\"(.*?)\"");
    String csrfToken = extractValue(responseBody, "name=\"_csrf\" value=\"(.*?)\"");

    // 필요한 동의 정보와 함께 POST 요청
    MultiValueMap<String, String> consentParams = new LinkedMultiValueMap<>();
    consentParams.add("client_id", "test-client");
    consentParams.add("state", state);
    consentParams.add("scope", "openid");
    consentParams.add("scope", "read");
    consentParams.add("scope", "write");
    consentParams.add("_csrf", csrfToken);

    MvcResult consentResult =
        mockMvc
            .perform(
                post("/oauth2/authorize")
                    .params(consentParams)
                    .with(user("ilovecorea@gmail.com").password("secret").roles("ADMIN", "USER")))
            .andExpect(status().is3xxRedirection())
            .andReturn();

    String consentLocation = consentResult.getResponse().getHeader("Location");
    assertThat(consentLocation).isNotNull();
    String code = consentLocation.split("code=")[1].split("&")[0];
    log.debug("## code:{}", code);

    // 3. 인가 코드를 사용하여 액세스 토큰 요청
    MvcResult tokenResult =
        mockMvc
            .perform(
                post("/oauth2/token")
                    .with(httpBasic("test-client", "secret"))
                    .param("grant_type", "authorization_code")
                    .param("code", code)
                    .param("redirect_uri", "http://localhost:8080/login/oauth2/code/test-client"))
            .andExpect(status().isOk())
            .andReturn();

    String tokenResponse = tokenResult.getResponse().getContentAsString();
    String accessToken = JsonPath.parse(tokenResponse).read("$.access_token");

    // 액세스 토큰이 유효한지 확인
    assertThat(accessToken).isNotNull();
    log.debug("accessToken:{}", accessToken);

    // 4. 발급된 액세스 토큰을 사용하여 보호된 리소스에 접근
    mockMvc
        .perform(get("/userinfo").header("Authorization", "Bearer " + accessToken))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  void testClientCredentialsGrant() throws Exception {
    // 1. client_credentials grant type을 사용하여 액세스 토큰 요청
    MvcResult clientCredentialsTokenResult =
        mockMvc
            .perform(
                post("/oauth2/token")
                    .with(httpBasic("test-client", "secret"))
                    .param("grant_type", "client_credentials")
                    .param("scope", "openid read write"))
            .andExpect(status().isOk())
            .andReturn();

    String clientCredentialsTokenResponse =
        clientCredentialsTokenResult.getResponse().getContentAsString();
    String clientCredentialsAccessToken =
        JsonPath.parse(clientCredentialsTokenResponse).read("$.access_token");

    // client_credentials grant로 발급된 액세스 토큰이 유효한지 확인
    assertThat(clientCredentialsAccessToken).isNotNull();
    log.debug("clientCredentialsAccessToken:{}", clientCredentialsAccessToken);

    // 발급된 client_credentials 액세스 토큰의 스코프 확인
    String scope = JsonPath.parse(clientCredentialsTokenResponse).read("$.scope");
    List<String> scopes = Arrays.asList(scope.split(" "));
    assertThat(scopes).containsExactlyInAnyOrder("openid", "read", "write");
  }

  private String extractValue(String html, String regex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(html);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
}
