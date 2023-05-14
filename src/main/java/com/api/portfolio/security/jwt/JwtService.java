package com.api.portfolio.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;

@Service
public class JwtService {

    protected static final String _KEY = "eJ7KLo2YEJRBmbO+e5Atb1H5mi/Dc8CHvJ1j2uN9x8M=";

    public String extractEmailOfUser(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claim = extractAllClaims(token);
        return claimsResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(_KEY);
        SecretKey key = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
        return key;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String emailOfUser = extractEmailOfUser(token);
        return (emailOfUser.equals(userDetails.getUsername()))&&!isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(
        Map<String, Object> extraClaims, 
        UserDetails userDetails
        ){
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60  * 24))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
}
