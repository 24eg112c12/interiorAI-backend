package com.interiorai.exception;
import org.springframework.http.HttpStatus;
public class ApiException extends RuntimeException {
 public final HttpStatus status;
 public ApiException(HttpStatus status,String message){super(message);this.status=status;}
 public static ApiException notFound(String message){return new ApiException(HttpStatus.NOT_FOUND,message);}
 public static ApiException forbidden(){return new ApiException(HttpStatus.FORBIDDEN,"You do not have access to this resource.");}
 public static ApiException bad(String message){return new ApiException(HttpStatus.BAD_REQUEST,message);}
}
