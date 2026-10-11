package com.interiorai.security;
import com.interiorai.entity.Account;
import com.interiorai.exception.ApiException;
import org.springframework.security.core.context.SecurityContextHolder;
public final class CurrentAccount {
 private CurrentAccount(){}
 public static Account get(){Object p=SecurityContextHolder.getContext().getAuthentication()==null?null:SecurityContextHolder.getContext().getAuthentication().getPrincipal();if(p instanceof Account a)return a;throw ApiException.forbidden();}
 public static boolean admin(){return get().role==Account.Role.ADMIN;}
}
