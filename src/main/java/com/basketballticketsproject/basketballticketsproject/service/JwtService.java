package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final SecretKey SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
 public String getToken(Usuario user){
     String token = Jwts
             .builder()
             .subject(user.getUsername())
             .claim("authorities",
                     user.getAuthorities().stream()
                             .map(GrantedAuthority::getAuthority)
                             .collect(Collectors.toList()))
             .claim("usuario", devolverUsuarioParametros(user))
             .issuedAt(new Date(System.currentTimeMillis()))
             .expiration(new Date(System.currentTimeMillis() + 1000*60*24))
             .signWith(SecretKey).compact();

     return token;
 }
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpirated(token));
    }
    private Claims getAllClaims(String token){
     return Jwts.parser().verifyWith(SecretKey).build().parseSignedClaims(token).getPayload();
    }
    public <T> T getClaim(String token, Function<Claims, T> claimsTFuction){
        Claims claims = getAllClaims(token);
        return claimsTFuction.apply(claims);
    }
    private Date getExpiration(String token){
     return getClaim(token, Claims::getExpiration);
    }
    private Boolean isTokenExpirated(String token){
        return getExpiration(token).before(new Date());
    }
    private HashMap<Object,Object> devolverUsuarioParametros(Usuario user){
     HashMap<Object,Object> list = new HashMap<Object,Object>(6);
     list.put("id",user.getUser_id());
     list.put("isAdmin",user.is_admin());
     list.put("nombre",user.getNombre());
     list.put("apellidos",user.getApellidos());
     list.put("email", user.getEmail());
     list.put("partidosAsistidos",user.getPartidosAsistidos());
     return  list;
    }
}
