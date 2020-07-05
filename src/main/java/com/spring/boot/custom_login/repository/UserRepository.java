package com.spring.boot.custom_login.repository;

import com.spring.boot.custom_login.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {
    UserModel findByUserName(String username);
}
