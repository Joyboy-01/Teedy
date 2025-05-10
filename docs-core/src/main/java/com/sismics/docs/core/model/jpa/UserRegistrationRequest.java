package com.sismics.docs.core.model.jpa;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "T_USER_REG_REQUEST")
public class UserRegistrationRequest {
    @Id
    @Column(name = "URR_ID_C")
    private String id;

    @Column(name = "URR_USERNAME_C", unique = true)
    private String username;

    @Column(name = "URR_PASSWORD_C")
    private String password;

    @Column(name = "URR_EMAIL_C")
    private String email;

    @Column(name = "URR_CREATE_DATE_D")
    private Date createDate;

    @Column(name = "URR_STATUS_C")
    private String status; // pending/accepted/rejected

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getStatus() {
        return status;
    }
}
