package com.spring.boot.custom_login.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_authentication")
@IdClass(UserAuthenticationModelId.class)
public class UserAuthenticationModel {

    @Column(name = "user_id", nullable = false)
    @Id
    int userId;

    @Column(name = "issued_at", nullable = false)
    @CreatedDate
    private Date issuedAt;

    @Column(name = "expired_at", nullable = false)
    private Date expiredAt;

    @Column(name = "api_key", nullable = false)
    @Id
    private String apiKey;

    public int getUser_id() {
        return userId;
    }

    public void setUser_id(int user_id) {
        this.userId = user_id;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
