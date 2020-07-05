package com.spring.boot.custom_login.controllers;

import com.spring.boot.custom_login.config.AppConfig;
import com.spring.boot.custom_login.filters.Authorization;
import com.spring.boot.custom_login.model.UserModel;
import com.spring.boot.custom_login.repository.UserAuthenticationRepository;
import com.spring.boot.custom_login.repository.UserRepository;
import com.spring.boot.custom_login.routes.Routes;
import com.spring.boot.custom_login.util.TimeUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(value = Routes.BASE_URL  + Routes.USER)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;

    HashMap<String, Object> map;
    HttpStatus httpStatus;
    ResponseEntity response;

    private boolean checkIfUserNameAlreadyExists(String username){
        return userRepository.findByUserName(username) != null;
    }

    private String encodePassword(String password){
        return appConfig.passwordEncoder().encode(password);
    }

    private boolean matchPassword(CharSequence password, String hash) {
        return appConfig.passwordEncoder().matches(password, hash);
    }

    @PostMapping(value = Routes.REGISTER)
    public ResponseEntity register(HttpServletRequest httpServletRequest, @ModelAttribute UserModel userModel){
        map = new HashMap<>();
        if (!checkIfUserNameAlreadyExists(userModel.getUserName())){
            userModel.setActive(true);
            userModel.setPassword(encodePassword(userModel.getPassword()));
            userRepository.save(userModel);

            map.put("status", 0);
            map.put("success", "User has been created");
            httpStatus = HttpStatus.OK;
        }
        else{
            map.put("status", -5);
            map.put("error", "Username already exists");
            httpStatus = HttpStatus.CONFLICT;
        }

        return new ResponseEntity<>(map, httpStatus);
    }

    @PutMapping(value = Routes.CHANGE_PASSWORD + "/{id}")
    public ResponseEntity change_password(HttpServletRequest httpServletRequest, @PathVariable int id){

        Authorization authorization = new Authorization(userAuthenticationRepository);
        response = authorization.authorize(httpServletRequest);
        map = (HashMap<String, Object>) response.getBody();

        if ((int) map.get("status") == 0) {
            if (userRepository.existsById(id)) {
                UserModel userModel = userRepository.getOne(id);
                if (matchPassword(httpServletRequest.getParameter("old_password"), userModel.getPassword())) {
                    userModel.setPassword(encodePassword(httpServletRequest.getParameter("new_password")));
                    userRepository.save(userModel);

                    map.put("status", 0);
                    map.put("error", "Password changed successfully");
                    httpStatus = HttpStatus.OK;
                }
                else{
                    map.put("status", -3);
                    map.put("error", "Old password does not match");
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
            }
            else{
                map.put("status", -3);
                map.put("error", "No user exists with id = " + id);
                httpStatus = HttpStatus.NOT_FOUND;
            }
            response = new ResponseEntity<>(map, httpStatus);
        }

        return response;
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(HttpServletRequest httpServletRequest, @PathVariable("id") int id) {
        Authorization authorization = new Authorization(userAuthenticationRepository);
        response = authorization.authorize(httpServletRequest);
        map = (HashMap<String, Object>) response.getBody();

        if ((int) map.get("status") == 0) {
            if (userRepository.existsById(id)) {

                UserModel userModel = userRepository.getOne(id);
                userModel.setDeletedAt(TimeUtilities.getCurrentTimestamp());
                userRepository.save(userModel);

                map.put("status", 0);
                map.put("success", "User has been deleted");
            }
            else{
                map.put("status", -3);
                map.put("error", "No user exists with id = " + id);
                httpStatus = HttpStatus.NOT_FOUND;
                response = new ResponseEntity<>(map, httpStatus);
            }
        }

        return response;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity get(HttpServletRequest httpServletRequest, @PathVariable("id") int id) {

        Authorization authorization = new Authorization(userAuthenticationRepository);
        response = authorization.authorize(httpServletRequest);
        map = (HashMap<String, Object>) response.getBody();

        if ((int) map.get("status") == 0) {
            if (userRepository.existsById(id)) {
                map.put("status", 0);
                map.put("success", userRepository.findById(id));
            }
            else{
                map.put("status", -3);
                map.put("error", "No user exists with id = " + id);
                httpStatus = HttpStatus.NOT_FOUND;
                response = new ResponseEntity<>(map, httpStatus);
            }
        }
        return response;
    }

    @GetMapping(value = Routes.ALL)
    public ResponseEntity getAll(HttpServletRequest httpServletRequest) {

        Authorization authorization = new Authorization(userAuthenticationRepository);
        response = authorization.authorize(httpServletRequest);
        map = (HashMap<String, Object>) response.getBody();

        if ((int) map.get("status") == 0) {
            List<UserModel> allData = userRepository.findAll();
            if (allData != null && !allData.isEmpty()) {
                map.put("status", 0);
                map.put("success", allData);
            }
            else {
                map.put("status", -3);
                map.put("error", "No user exists");
            }
        }
        return response;
    }
}
