package com.spring.boot.custom_login.filters;

import com.spring.boot.custom_login.model.UserAuthenticationModel;
import com.spring.boot.custom_login.repository.UserAuthenticationRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.HashMap;

@Component
public class Authorization {

    private UserAuthenticationRepository userAuthenticationRepository;

    public Authorization(UserAuthenticationRepository userAuthenticationRepository) {
        this.userAuthenticationRepository = userAuthenticationRepository;
    }

    public ResponseEntity authorize(HttpServletRequest httpServletRequest) {
        HashMap<String, Object> map = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.OK;

        String apiKey = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (apiKey != null && !apiKey.equals("")){
            UserAuthenticationModel userAuthentication = userAuthenticationRepository.findByApiKey(apiKey);
            if (userAuthentication != null) {
                if (userAuthentication.getExpiredAt().compareTo(Calendar.getInstance().getTime()) > 0) {
                    map.put("status", 0);
                    httpStatus = HttpStatus.OK;
                }
                else {
                    userAuthenticationRepository.delete(userAuthentication);
                    map.put("status", -4);
                    map.put("error", "Authorization (API Key) has expired");
                    httpStatus = HttpStatus.UNAUTHORIZED;
                }
            }
            else{
                map.put("status", -3);
                map.put("error", "Authorization (API Key) is invalid or User is not logged in");
                httpStatus = HttpStatus.UNAUTHORIZED;
            }
        }
        else{
            map.put("status", -1);
            map.put("error", "Authorization (API Key) is required");
            httpStatus = HttpStatus.UNAUTHORIZED;
        }

        return new ResponseEntity<>(map, httpStatus);
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/api/login");
    }
}
