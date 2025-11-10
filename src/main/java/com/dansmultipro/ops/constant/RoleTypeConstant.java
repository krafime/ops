package com.dansmultipro.ops.constant;

public enum RoleTypeConstant {
    SA("Super Admin"),
    GTW("User Gateway"),
    SYS("System Admin"),
    CUST("Customer");

    private final String roleName;

    RoleTypeConstant(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
