package ricky.examples.forum.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ricky.examples.forum.entity.UserEntity;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class UserRepositoryTests {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void testFindAll() {
    log.debug("## testFindAll");
    List<UserEntity> users = userRepository.findAll();
    assertThat(users).isNotEmpty();
  }

}