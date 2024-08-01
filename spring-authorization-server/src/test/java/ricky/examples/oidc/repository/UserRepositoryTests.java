package ricky.examples.oidc.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ricky.examples.oidc.config.AuditorAwareConfig;
import ricky.examples.oidc.model.Gender;
import ricky.examples.oidc.model.User;

@Slf4j
@DataJpaTest
@Import(AuditorAwareConfig.class)
@ActiveProfiles({"docker", "debug"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTests {

  @Autowired private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setUsername("johndoe@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setGender(Gender.M);
    user.setBirthday("19900101");
    user.setPassword("{noop}password");
    user.setAuthProvider("Local");
    user.setEnabled(true);
  }

  @Test
  void testSaveUser() {
    // Act
    User savedUser = userRepository.save(user);
    log.debug("savedUser:{}", savedUser);

    // Assert
    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getUsername()).isEqualTo(user.getUsername());
  }

  @Test
  void testFindUserByUsername() {
    // Arrange
    userRepository.save(user);

    // Act
    Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

    // Assert
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    log.debug("foundUser:{}", foundUser.get());
  }

  @Test
  void testUpdateUser() {
    // Arrange
    User savedUser = userRepository.save(user);
    savedUser.setFirstName("Jane");
    savedUser.setLastName("Smith");

    // Act
    User updatedUser = userRepository.save(savedUser);

    // Assert
    assertThat(updatedUser).isNotNull();
    assertThat(updatedUser.getFirstName()).isEqualTo("Jane");
    assertThat(updatedUser.getLastName()).isEqualTo("Smith");
  }

  @Test
  void testDeleteUser() {
    // Arrange
    User savedUser = userRepository.save(user);
    UUID userId = savedUser.getId();

    // Act
    userRepository.deleteById(userId);
    Optional<User> deletedUser = userRepository.findById(userId);

    // Assert
    assertThat(deletedUser).isNotPresent();
  }
}
