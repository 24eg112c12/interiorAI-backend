package com.interiorai.controller;
import com.interiorai.dto.Requests;import com.interiorai.entity.Account;import com.interiorai.security.CurrentAccount;import com.interiorai.service.AuthService;
import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;import org.springframework.http.*;import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService auth;public AuthController(AuthService a){auth=a;}
 @PostMapping("/register") public ResponseEntity<?> register(@Valid @RequestBody Requests.Register r){return ResponseEntity.status(201).body(auth.issue(auth.register(r,Account.Role.USER)));}
 @PostMapping("/login") public Map<String,Object> login(@Valid @RequestBody Requests.Login r){return auth.issue(auth.login(r));}
 @GetMapping("/me") public Map<String,Object> me(){return auth.user(CurrentAccount.get());}
 @PutMapping("/me") public Map<String,Object> update(@Valid @RequestBody Requests.AccountUpdate input){return auth.update(CurrentAccount.get(),input);}
 @PostMapping("/change-password") public Map<String,String> password(@Valid @RequestBody Requests.PasswordUpdate input){auth.changePassword(CurrentAccount.get(),input);return Map.of("message","Password changed. Please sign in again.");}
 @PostMapping("/logout") public Map<String,String> logout(@RequestHeader(value="Authorization",required=false)String bearer){auth.logout(bearer);return Map.of("message","Signed out.");}
}
