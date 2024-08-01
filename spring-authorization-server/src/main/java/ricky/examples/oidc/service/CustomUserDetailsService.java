package ricky.examples.oidc.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ricky.examples.oidc.model.JwtUser;
import ricky.examples.oidc.model.User;
import ricky.examples.oidc.repository.UserRepository;

/** 사용자 인증 및 사용자 정보를 제공하는 서비스 클래스. */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * 사용자 이름으로 사용자를 로드합니다.
   *
   * @param username 조회할 사용자의 이름
   * @return UserDetails 사용자 세부 정보 객체
   * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("{} 으로 사용자를 조회 합니다.", username);
    Optional<User> userOptional = userRepository.findByUsernameWithRoels(username);
    User user =
        userOptional.orElseThrow(() -> new UsernameNotFoundException(username + " 사용자를 찾을 수 없습니다"));

    return new JwtUser(user);
  }
}
