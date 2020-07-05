package com.spring.boot.custom_login.controllers;

import com.google.common.hash.Hashing;
import com.spring.boot.custom_login.config.AppConfig;
import com.spring.boot.custom_login.filters.Authorization;
import com.spring.boot.custom_login.model.UserAuthenticationModel;
import com.spring.boot.custom_login.model.UserModel;
import com.spring.boot.custom_login.repository.UserAuthenticationRepository;
import com.spring.boot.custom_login.repository.UserRepository;
import com.spring.boot.custom_login.routes.Routes;
import com.spring.boot.custom_login.util.TimeUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;

@RestController
@RequestMapping(value = Routes.BASE_URL)
public class UserAuthenticationController {

    @Autowired
    private UserRepository user;

    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;

    @Autowired
    private AppConfig appConfig;

    private UserModel find(String username) {
        return user.findByUserName(username);
    }

    private boolean matchPassword(CharSequence password, String hash) {
        return appConfig.passwordEncoder().matches(password, hash);
    }

    private String generateKey(int user_id) {
        double random = Math.random();
        String apiKey = Hashing.sha256()
                .hashString(random + "" + user_id, StandardCharsets.UTF_8)
                .toString();

        try {
            UserAuthenticationModel authentication = new UserAuthenticationModel();
            authentication.setUser_id(user_id);
            authentication.setIssuedAt(TimeUtilities.getCurrentDate());
            authentication.setExpiredAt(TimeUtilities.getDateAfterOneDay());
            authentication.setApiKey(apiKey);
            userAuthenticationRepository.save(authentication);
            return apiKey;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping(value = Routes.LOGIN)
    public ResponseEntity login(@RequestParam String username, @RequestParam String password) {
        HashMap<String, Object> map = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.OK;
        UserModel user = find(username);
        if (user != null) {
            if (matchPassword(password, user.getPassword())) {
                map.put("status", 0);
                map.put("success", user.getName() + " has been logged in");
                map.put("name", user.getName());
                map.put("role_id", user.getRoleId());
                map.put("api_key", generateKey(user.getId()));
                httpStatus = HttpStatus.OK;

            }
            else {
                map.put("status", -3);
                map.put("error", "Password entered is invalid");
                httpStatus = HttpStatus.UNAUTHORIZED;
            }
        }
        else {
            map.put("status", -3);
            map.put("error", "No such username found");
            httpStatus = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(map, httpStatus);
    }

    @PostMapping(value = Routes.LOGOUT)
    public ResponseEntity logout(HttpServletRequest httpServletRequest) {
        HashMap<String, Object> map = new HashMap<>();
        HttpStatus httpStatus = HttpStatus.OK;
        ResponseEntity response;

        Authorization authorization = new Authorization(userAuthenticationRepository);
        response = authorization.authorize(httpServletRequest);
        map = (HashMap<String, Object>) response.getBody();

        if ((int) map.get("status") == 0) {
            String apiKey = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            userAuthenticationRepository.delete(userAuthenticationRepository.findByApiKey(apiKey));
            map.put("status", 0);
            map.put("success", "You have been logged out");
        }

        return response;
    }
}
