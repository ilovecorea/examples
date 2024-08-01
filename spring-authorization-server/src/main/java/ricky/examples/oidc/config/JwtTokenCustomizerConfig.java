package ricky.examples.oidc.config;

import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;
import static org.springframework.security.oauth2.server.authorization.OAuth2TokenType.ACCESS_TOKEN;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import ricky.examples.oidc.model.JwtUser;
import ricky.examples.oidc.service.CustomUserDetailsService;

@Configuration
public class JwtTokenCustomizerConfig {

  @Bean
  OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(
      CustomUserDetailsService userInfoService) {

    return context -> {
      if (ID_TOKEN.equals(context.getTokenType().getValue())
          || ACCESS_TOKEN.equals(context.getTokenType())) {
        String principalName = context.getPrincipal().getName();

        // 클라이언트 자격 증명인지 확인
        if (context.getAuthorizationGrantType().getValue().equals("client_credentials")) {
          Map<String, Object> info = new HashMap<>();
          info.put("client_id", principalName);

          // 필요한 경우 클라이언트에 대한 추가 정보를 claims에 추가
          context.getClaims().claims(claims -> claims.putAll(info));
        } else {
          JwtUser userInfo = (JwtUser) userInfoService.loadUserByUsername(principalName);
          Map<String, Object> info = new HashMap<>();
          info.put("roles", userInfo.getRoles());
          context.getClaims().claims(claims -> claims.putAll(info));
        }

        context.getJwsHeader().type("jwt");
      }
    };
  }
}
