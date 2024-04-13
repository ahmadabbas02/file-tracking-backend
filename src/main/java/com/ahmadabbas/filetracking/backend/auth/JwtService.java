package com.ahmadabbas.filetracking.backend.auth;

import com.ahmadabbas.filetracking.backend.advisor.AdvisorService;
import com.ahmadabbas.filetracking.backend.advisor.view.AdvisorUserView;
import com.ahmadabbas.filetracking.backend.student.StudentService;
import com.ahmadabbas.filetracking.backend.student.view.StudentView;
import com.ahmadabbas.filetracking.backend.user.Role;
import com.ahmadabbas.filetracking.backend.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final StudentService studentService;
    private final AdvisorService advisorService;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.issuer}")
    private String issuer;

    public String extractUserLoginId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String buildToken(
            Map<String, Object> extraClaims,
            Authentication authentication
    ) {
        return buildToken(extraClaims, authentication, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            Authentication authentication,
            long expiration
    ) {
        String subject = getSubjectFromAuthentication(authentication);
        return buildToken(extraClaims, subject, expiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String subject,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(
                        Date.from(Instant.now().plus(expiration, ChronoUnit.DAYS))
                )
                .issuer(issuer)
                .signWith(getSecretKey())
                .compact();
    }

    private String getSubjectFromAuthentication(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getRoles().contains(Role.STUDENT)) {
            StudentView student = studentService.getStudentViewByUserId(user.getId());
            return student.getId();
        } else if (user.getRoles().contains(Role.ADVISOR)) {
            AdvisorUserView advisor = advisorService.getAdvisorByUserId(user.getId());
            return advisor.getId();
        } else {
            return String.valueOf(user.getId());
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String loginId = extractUserLoginId(token);
        User user = (User) userDetails;
        if (isTokenExpired(token)) {
            return false;
        }
        if (user.getRoles().contains(Role.STUDENT)) {
            StudentView student = studentService.getStudentViewByUserId(user.getId());
            return loginId.equals(student.getId());
        } else if (user.getRoles().contains(Role.ADVISOR)) {
            AdvisorUserView advisor = advisorService.getAdvisorByUserId(user.getId());
            return loginId.equals(advisor.getId());
        } else {
            return loginId.equals(String.valueOf(user.getId()));
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
