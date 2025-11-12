package com.dansmultipro.ops.specification;

import com.dansmultipro.ops.model.Payment;
import com.dansmultipro.ops.model.PaymentStatus;
import com.dansmultipro.ops.model.User;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecification {

    public static Specification<Payment> byUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    public static Specification<Payment> byStatus(PaymentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("paymentStatus"), status);
    }

    public static Specification<Payment> byCustomerCode(String customerCode) {
        return (root, query, cb) ->
                // Kondisi kalo bisa null, nanti auto null dan skip pas gabungin nya
                customerCode != null ?
                        cb.equal(root.get("customerCode"), customerCode) :
                        null;
    }

    public static Specification<Payment> byStatusCode(String statusCode) {
        return (root, query, cb) ->
                cb.equal(root.get("paymentStatus").get("statusCode"), statusCode);
    }

    public static Specification<Payment> all() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<Payment> userAndStatus(User user, PaymentStatus status) {
        return byUser(user).and(byStatus(status));
    }

    public static Specification<Payment> customerCodeAndStatus(String customerCode, String statusCode) {
        return byCustomerCode(customerCode).and(byStatusCode(statusCode));
    }
}

