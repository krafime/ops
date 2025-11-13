package com.dansmultipro.ops.integration;

import com.dansmultipro.ops.constant.PaymentStatusConstant;
import com.dansmultipro.ops.constant.PaymentTypeConstant;
import com.dansmultipro.ops.constant.ProductTypeConstant;
import com.dansmultipro.ops.constant.RoleTypeConstant;
import com.dansmultipro.ops.model.*;
import com.dansmultipro.ops.repo.*;
import com.dansmultipro.ops.util.AuthUtil;
import com.dansmultipro.ops.util.EmailUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public abstract class AbstractServiceIntegrationTest {

    static final UUID DEFAULT_ACTOR = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    RoleTypeRepo roleTypeRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ProductTypeRepo productTypeRepo;

    @Autowired
    PaymentTypeRepo paymentTypeRepo;

    @Autowired
    PaymentStatusRepo paymentStatusRepo;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    AuthUtil authUtil;

    @MockitoBean
    RabbitTemplate rabbitTemplate;

    @MockitoBean
    ConnectionFactory connectionFactory;

    @MockitoBean
    EmailUtil emailUtil;

    RoleType systemRole;
    RoleType customerRole;
    RoleType gatewayRole;
    RoleType superAdminRole;

    PaymentStatus processingStatus;
    PaymentStatus approvedStatus;
    PaymentStatus rejectedStatus;
    PaymentStatus cancelledStatus;

    ProductType defaultProductType;
    PaymentType defaultPaymentType;

    User systemUser;
    User customerUser;
    User gatewayUser;

    @BeforeEach
    void setupBaseData() {
        Mockito.reset(authUtil, rabbitTemplate, emailUtil, connectionFactory);

        paymentRepo.deleteAll();
        userRepo.deleteAll();
        roleTypeRepo.deleteAll();
        paymentTypeRepo.deleteAll();
        productTypeRepo.deleteAll();
        paymentStatusRepo.deleteAll();

        superAdminRole = createRole(RoleTypeConstant.SA);
        customerRole = createRole(RoleTypeConstant.CUST);
        gatewayRole = createRole(RoleTypeConstant.GTW);
        systemRole = createRole(RoleTypeConstant.SYS);

        // Product types (5)
        defaultProductType = createProductType(ProductTypeConstant.PLN.name(), ProductTypeConstant.PLN.getProductName());
        createProductType(ProductTypeConstant.PULSA.name(), ProductTypeConstant.PULSA.getProductName());
        createProductType(ProductTypeConstant.INTERNET.name(), ProductTypeConstant.INTERNET.getProductName());
        createProductType(ProductTypeConstant.BPJS.name(), ProductTypeConstant.BPJS.getProductName());
        createProductType(ProductTypeConstant.PDAM.name(), ProductTypeConstant.PDAM.getProductName());

        // Payment types (6)
        defaultPaymentType = createPaymentType(PaymentTypeConstant.QRIS.name(), PaymentTypeConstant.QRIS.getDisplayName(), new BigDecimal("500.00"));
        createPaymentType(PaymentTypeConstant.VIRTUAL_ACCOUNT.name(), PaymentTypeConstant.VIRTUAL_ACCOUNT.getDisplayName(), new BigDecimal("2500.00"));
        createPaymentType(PaymentTypeConstant.SHOPPE_PAY.name(), PaymentTypeConstant.SHOPPE_PAY.getDisplayName(), new BigDecimal("1000.00"));
        createPaymentType(PaymentTypeConstant.OVO.name(), PaymentTypeConstant.OVO.getDisplayName(), new BigDecimal("1000.00"));
        createPaymentType(PaymentTypeConstant.DANA.name(), PaymentTypeConstant.DANA.getDisplayName(), new BigDecimal("1000.00"));
        createPaymentType(PaymentTypeConstant.GOPAY.name(), PaymentTypeConstant.GOPAY.getDisplayName(), new BigDecimal("1000.00"));

        systemUser = createUser("System", "system@ops.test", systemRole);
        customerUser = createUser("Customer User", "customer@ops.local", customerRole);
        gatewayUser = createUser("Gateway User", "gateway@ops.local", gatewayRole);

        processingStatus = createStatus(PaymentStatusConstant.PROCESSING);
        approvedStatus = createStatus(PaymentStatusConstant.SUCCESS);
        rejectedStatus = createStatus(PaymentStatusConstant.FAILED);
        cancelledStatus = createStatus(PaymentStatusConstant.CANCELLED);
    }

    RoleType createRole(RoleTypeConstant type) {
        RoleType role = new RoleType();
        role.setRoleCode(type.name());
        role.setRoleName(type.getRoleName());
        initializeBase(role);
        return roleTypeRepo.saveAndFlush(role);
    }

    PaymentStatus createStatus(PaymentStatusConstant constant) {
        PaymentStatus status = new PaymentStatus();
        status.setStatusCode(constant.name());
        status.setStatusName(constant.getStatusName());
        initializeBase(status);
        return paymentStatusRepo.saveAndFlush(status);
    }

    ProductType createProductType(String code, String name) {
        ProductType productType = new ProductType();
        productType.setProductCode(code);
        productType.setProductName(name);
        initializeBase(productType);
        return productTypeRepo.saveAndFlush(productType);
    }

    PaymentType createPaymentType(String code, String name, BigDecimal fee) {
        PaymentType paymentType = new PaymentType();
        paymentType.setPaymentTypeCode(code);
        paymentType.setPaymentTypeName(name);
        paymentType.setPaymentFee(fee);
        initializeBase(paymentType);
        return paymentTypeRepo.saveAndFlush(paymentType);
    }

    User createUser(String fullName, String email, RoleType role) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoleType(role);
        initializeBase(user);
        return userRepo.saveAndFlush(user);
    }

    User createUser(String fullName, String email, RoleType role, boolean isActive) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoleType(role);
        initializeBase(user, isActive);
        return userRepo.saveAndFlush(user);
    }

    private void initializeBase(Object entity) {
        if (entity instanceof BaseModel baseEntity) {
            baseEntity.setId(UUID.randomUUID());
            baseEntity.setActive(true);
            baseEntity.setCreatedAt(LocalDateTime.now());
            baseEntity.setCreatedBy(AbstractServiceIntegrationTest.DEFAULT_ACTOR);
        }
    }

    private void initializeBase(Object entity, boolean isActive) {
        if (entity instanceof BaseModel baseEntity) {
            baseEntity.setId(UUID.randomUUID());
            baseEntity.setActive(isActive);
            baseEntity.setCreatedAt(LocalDateTime.now());
            baseEntity.setCreatedBy(AbstractServiceIntegrationTest.DEFAULT_ACTOR);
        }
    }
}

