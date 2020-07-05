package com.spring.boot.custom_login.repository;

import com.spring.boot.custom_login.model.UserAuthenticationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthenticationModel, Integer> {
    UserAuthenticationModel findByApiKey(String apiKey);
    UserAuthenticationModel deleteByApiKey(String apiKey);
}
