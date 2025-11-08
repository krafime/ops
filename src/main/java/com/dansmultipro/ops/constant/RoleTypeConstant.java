package com.dansmultipro.ops.constant;

public enum RoleTypeConstant {
    SA("Super Admin"),
    GTW("User Gateway"),
    CUST("Customer");

    private final String roleName;

    RoleTypeConstant(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
