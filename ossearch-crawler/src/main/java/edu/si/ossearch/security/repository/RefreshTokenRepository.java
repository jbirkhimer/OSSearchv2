package edu.si.ossearch.security.repository;

import edu.si.ossearch.security.models.RefreshToken;
import edu.si.ossearch.security.models.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Tag(name = "RefreshToken", description = "RefreshTokenRepository")
@Repository
@SecurityRequirement(name = "bearerAuth")
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  void deleteByExpiryDateLessThan(Instant now);

  @Modifying
  int deleteByUser(User user);
}
