package com.dansmultipro.ops.dto.user;

public class UserResDTO{
    private String id;
    private String email;
    private String fullName;
    private String roleCode;
    private int optLock;

    public UserResDTO(String id, String email, String fullName, String roleCode, int optLock) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.roleCode = roleCode;
        this.optLock = optLock;
    }

    public UserResDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public int getOptLock() {
        return optLock;
    }

    public void setOptLock(int optLock) {
        this.optLock = optLock;
    }
}
