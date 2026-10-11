package com.interiorai.service;
import com.interiorai.dto.Requests;
import com.interiorai.entity.*;import com.interiorai.repository.*;
import com.interiorai.security.BearerSessionFilter;
import com.interiorai.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;import java.time.temporal.ChronoUnit;import java.util.*;
@Service
public class AuthService {
 private final AccountRepository users;private final LoginSessionRepository sessions;private final PasswordEncoder encoder;
 public AuthService(AccountRepository u,LoginSessionRepository s,PasswordEncoder e){users=u;sessions=s;encoder=e;}
 @Transactional public Account register(Requests.Register r,Account.Role role){String email=r.email().trim().toLowerCase(Locale.ROOT);if(users.findByEmailIgnoreCase(email).isPresent())throw new ApiException(HttpStatus.CONFLICT,"This email is already registered.");Account a=new Account();a.email=email;a.fullName=r.fullName().trim();a.passwordHash=encoder.encode(r.password());a.role=role;return users.save(a);}
 public Account login(Requests.Login r){Account a=users.findByEmailIgnoreCase(r.email().trim()).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password."));if(!a.enabled||!encoder.matches(r.password(),a.passwordHash))throw new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password.");return a;}
 @Transactional public Map<String,Object> issue(Account account){String raw=UUID.randomUUID().toString()+"."+UUID.randomUUID();LoginSession session=new LoginSession();session.account=account;session.tokenHash=BearerSessionFilter.hash(raw);session.expiresAt=Instant.now().plus(7,ChronoUnit.DAYS);sessions.save(session);return Map.of("token",raw,"user",user(account));}
 @Transactional public Map<String,Object> update(Account current,Requests.AccountUpdate input){Account a=users.findById(current.id).orElseThrow(()->ApiException.notFound("Account not found."));String nextEmail=input.email().trim().toLowerCase(Locale.ROOT);users.findByEmailIgnoreCase(nextEmail).ifPresent(other->{if(!other.id.equals(a.id))throw new ApiException(HttpStatus.CONFLICT,"Email is already used.");});a.fullName=input.fullName().trim();a.email=nextEmail;return user(users.save(a));}
 @Transactional public void changePassword(Account current,Requests.PasswordUpdate input){Account a=users.findById(current.id).orElseThrow(()->ApiException.notFound("Account not found."));if(!encoder.matches(input.oldPassword(),a.passwordHash))throw ApiException.bad("Current password is incorrect.");a.passwordHash=encoder.encode(input.newPassword());users.save(a);sessions.deleteAll(sessions.findByAccountId(a.id));}
 public Map<String,Object> user(Account a){return Map.of("id",a.id,"fullName",a.fullName,"email",a.email,"role",a.role.name());}
 @Transactional public void logout(String header){if(header!=null&&header.startsWith("Bearer "))sessions.findByTokenHash(BearerSessionFilter.hash(header.substring(7))).ifPresent(sessions::delete);}
}
