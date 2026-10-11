package com.interiorai.security;
import com.interiorai.repository.LoginSessionRepository;
import jakarta.servlet.*;import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.io.IOException;import java.nio.charset.StandardCharsets;import java.security.MessageDigest;import java.time.Instant;import java.util.*;
@Component
public class BearerSessionFilter extends OncePerRequestFilter {
 private final LoginSessionRepository sessions;
 public BearerSessionFilter(LoginSessionRepository sessions){this.sessions=sessions;}
 public static String hash(String token){try {byte[] digest=MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));return HexFormat.of().formatHex(digest);}catch(Exception e){throw new IllegalStateException(e);}}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
  String value=request.getHeader("Authorization");
  if(value!=null && value.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication()==null){
   String token=value.substring(7);
   if(token.length()<200){sessions.findByTokenHash(hash(token)).filter(s->s.expiresAt.isAfter(Instant.now())&&s.account.enabled).ifPresent(s->{var account=s.account;var roles=List.of(new SimpleGrantedAuthority("ROLE_"+account.role.name()));Authentication auth=new UsernamePasswordAuthenticationToken(account,null,roles);SecurityContextHolder.getContext().setAuthentication(auth);});}
  }
  chain.doFilter(request,response);
 }
}
