package com.practice.securitybeginner.security.domain;

import com.practice.securitybeginner.domain.ApplicationUser;
import com.practice.securitybeginner.enums.TokenStatus;
import com.practice.securitybeginner.enums.TokenType;
import com.practice.securitybeginner.interceptor.exception.AuthenticateException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.practice.securitybeginner.enums.ErrorCode.REISSUE_NOT_ALLOWED;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken {

  private String userId;
  private TokenType type;
  private String value;
  private TokenStatus status;
  private JwtTokenClaims claims;
  private LocalDateTime issuedAt;
  private LocalDateTime expiresAt;

  public static JwtToken createToken(TokenType type, ApplicationUser user, long expiredTimeMillis) {
    LocalDateTime now = LocalDateTime.now();
    return JwtToken.builder()
      .userId(user.getUserId())
      .type(type)
      .status(TokenStatus.ACTIVE)
      .issuedAt(now)
      .expiresAt(now.plusNanos(expiredTimeMillis * 1_000_000))
      .claims(JwtTokenClaims.from(type, user))
      .build();
  }

  public void setValue(String tokenValue) {
    this.value = tokenValue;
  }

  public boolean isValid() {
    return status == TokenStatus.ACTIVE && !isExpired();
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public void invalidate() {
    this.status = TokenStatus.REVOKED;
  }

  public boolean canReissue() {
    if (type == TokenType.ACCESS) {
      return false;
    }
    return isValid();
  }

  public JwtToken reissueAccessToken(long accessTokenExpiresIn) {
    if (!canReissue()) {
      throw new AuthenticateException(REISSUE_NOT_ALLOWED);
    }
    return createToken(TokenType.ACCESS, ApplicationUser.from(this), accessTokenExpiresIn);
  }

}
