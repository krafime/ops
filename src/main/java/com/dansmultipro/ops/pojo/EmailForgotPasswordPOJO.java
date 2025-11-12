package com.dansmultipro.ops.pojo;

public class EmailForgotPasswordPOJO {
    String email;
    String customerName;
    String message;

    public EmailForgotPasswordPOJO(String email, String customerName, String message) {
        this.email = email;
        this.customerName = customerName;
        this.message = message;
    }

    public EmailForgotPasswordPOJO() {
    }

    public String getEmail() {
        return email;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMessage() {
        return message;
    }

}

