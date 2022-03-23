package edu.si.ossearch.security.jwt.repository;

import edu.si.ossearch.security.jwt.models.RefreshToken;
import edu.si.ossearch.security.jwt.models.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Tag(name = "RefreshToken", description = "RefreshTokenRepository")
@Repository
@SecurityRequirement(name = "bearerAuth")
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Modifying
  int deleteByUser(User user);
}
