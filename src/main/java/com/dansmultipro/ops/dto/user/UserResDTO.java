package com.dansmultipro.ops.dto.user;

public class UserResDTO{
    private String id;
    private String email;
    private String fullname;
    private String roleCode;
    private int optLock;

    public UserResDTO(String id, String email, String fullname, String roleCode, int optLock) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
