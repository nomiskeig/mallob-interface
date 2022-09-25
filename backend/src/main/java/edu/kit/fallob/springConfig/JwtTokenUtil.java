package edu.kit.fallob.springConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Kaloyan Enev
 * @version 1.0
 * A utility class used to create the token or get information about the user from it
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 100000L;

    private static final String AUTHORITIES = "authorities";

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Retrieve username from jwt token
     * @param token the jwtToken
     * @return the username from the token claims
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieve expiration date from jwt token
     * @param token the jwtToken
     * @return the expiration date from the token claims
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Retrieve the user Authorities from the token
     * @param token the jwtToken
     * @return the user authorities from the token claims
     */
    public String getAuthorities(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get(AUTHORITIES);
    }

    /**
     * Retrieve a claim from the token based on the claimsResolver
     * @param token the jwtToken
     * @param claimsResolver an already defined function
     * @return the claim
     * @param <T> a claim in any form
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieves all claims from the token, uses the secret key
     * @param token the jwtToken
     * @return all claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Check if the token has expired
     * @param token the jwtToken
     * @return true if expired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generates the jwtToken for the user (parses the user data from the UserDetails and calls helper method)
     * @param userDetails the encapsulated user data that is to be parsed
     * @return the jwtToken
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername(), userDetails.getAuthorities().toString());
    }

    /**
     * Defines the claims of the token, like Issuer, Expiration, Subject, and the ID
     * Signs the JWT using the HS512 algorithm and secret key.
     * @param claims claims to be added to the token
     * @param subject the username of the user
     * @param authorities the authorities of the user
     * @return the jwtToken
     */
    private String doGenerateToken(Map<String, Object> claims, String subject, String authorities) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)).claim(AUTHORITIES, authorities)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * Validates the token
     * @param token the jwtToken
     * @param userDetails the UserDetails, against which the token will be compared
     * @return true token is valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
