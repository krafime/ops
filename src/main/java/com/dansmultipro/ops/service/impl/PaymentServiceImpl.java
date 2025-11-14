package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.constant.PaymentStatusConstant;
import com.dansmultipro.ops.constant.RoleTypeConstant;
import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.payment.PaymentCreateReqDTO;
import com.dansmultipro.ops.dto.payment.PaymentPageDTO;
import com.dansmultipro.ops.dto.payment.PaymentResDTO;
import com.dansmultipro.ops.model.Payment;
import com.dansmultipro.ops.pojo.EmailStatusPOJO;
import com.dansmultipro.ops.repo.*;
import com.dansmultipro.ops.service.PaymentService;
import com.dansmultipro.ops.specification.PaymentSpecification;
import com.dansmultipro.ops.util.DateTimeUtil;
import com.dansmultipro.ops.util.EmailMessageBuilder;
import com.dansmultipro.ops.util.EmailUtil;
import com.dansmultipro.ops.util.PaymentCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.dansmultipro.ops.config.RabbitConfig.EMAIL_QUEUE_FAILED;
import static com.dansmultipro.ops.config.RabbitConfig.EMAIL_QUEUE_SUCCESS;

@Service
public class PaymentServiceImpl extends BaseService implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final PaymentTypeRepo paymentTypeRepo;
    private final ProductTypeRepo productTypeRepo;
    private final PaymentStatusRepo paymentStatusRepo;
    private final RabbitTemplate rabbitTemplate;
    private final EmailUtil emailUtil;
    private final EmailMessageBuilder emailMessageBuilder;
    private final DateTimeUtil dateTimeUtil;


    public PaymentServiceImpl(PaymentRepo paymentRepo, UserRepo userRepo, PaymentTypeRepo paymentTypeRepo,
                              ProductTypeRepo productTypeRepo, PaymentStatusRepo paymentStatusRepo, RabbitTemplate rabbitTemplate,
                              EmailUtil emailUtil, EmailMessageBuilder emailMessageBuilder, DateTimeUtil dateTimeUtil) {
        this.dateTimeUtil = dateTimeUtil;
        this.emailMessageBuilder = emailMessageBuilder;
        this.emailUtil = emailUtil;
        this.rabbitTemplate = rabbitTemplate;
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.paymentTypeRepo = paymentTypeRepo;
        this.productTypeRepo = productTypeRepo;
        this.paymentStatusRepo = paymentStatusRepo;
    }

    @Override
    @Transactional
    @CacheEvict(value = "paymenthistory", allEntries = true)
    public InsertResDTO createPayment(PaymentCreateReqDTO paymentReq) {
        var userId = authUtil.getLoginId();
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var paymentType = paymentTypeRepo.findById(toUUID(paymentReq.paymentTypeId()))
                .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));

        var productType = productTypeRepo.findById(toUUID(paymentReq.productTypeId()))
                .orElseThrow(() -> new IllegalArgumentException("Product Type not found"));

        var processingStatus = paymentStatusRepo.findByStatusCode(PaymentStatusConstant.PROCESSING.name())
                .orElseThrow(() -> new IllegalArgumentException("Payment Status not found"));

        // Check if customer code already has a PROCESSING payment
        var existingPayment = PaymentSpecification.customerCodeAndStatus(
                paymentReq.customerCode(),
                PaymentStatusConstant.PROCESSING.name()
        );

        if (paymentRepo.findOne(existingPayment).isPresent()) {
            throw new IllegalArgumentException("Customer code already has a PROCESSING payment");
        }

        var payment = new Payment();
        var amount = paymentReq.amount().add(paymentType.getPaymentFee());
        payment.setAmount(amount);
        payment.setPaymentCode(PaymentCodeGenerator.generatePaymentCode());
        payment.setUser(user);
        payment.setCustomerCode(paymentReq.customerCode());
        payment.setPaymentType(paymentType);
        payment.setProductType(productType);
        payment.setPaymentStatus(processingStatus);

        var savedPayment = paymentRepo.save(super.insert(payment));

        return new InsertResDTO(
                savedPayment.getId(),
                "Payment created successfully"
        );
    }

    @Override
    @CacheEvict(value = "paymenthistory", allEntries = true)
    @Transactional
    public CommonResDTO updatePaymentStatus(String paymentId, String newStatus) {
        var payment = paymentRepo.findById(toUUID(paymentId))
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (!PaymentStatusConstant.PROCESSING.name().equals(payment.getPaymentStatus().getStatusCode())) {
            throw new IllegalArgumentException("Only PROCESSING payments can be updated");
        }

        var newPaymentStatus = paymentStatusRepo.findByStatusCode(newStatus)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment status: " + newStatus));

        if (!newStatus.equals(PaymentStatusConstant.SUCCESS.name()) &&
                !newStatus.equals(PaymentStatusConstant.FAILED.name())) {
            throw new IllegalArgumentException("Payment can only be updated to SUCCESS or FAILED");
        }

        payment.setPaymentStatus(newPaymentStatus);
        paymentRepo.save(super.update(payment));

        // Send email notification
        buildAndSendEmailNotification(payment, newStatus);

        return new CommonResDTO("Payment status updated to " + newStatus);
    }

    @Override
    @CacheEvict(value = "paymenthistory", allEntries = true)
    @Transactional
    public CommonResDTO cancelPayment(String paymentId) {
        var userId = authUtil.getLoginId();
        var payment = paymentRepo.findById(toUUID(paymentId))
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (!payment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Only payment creator can cancel");
        }

        if (!PaymentStatusConstant.PROCESSING.name().equals(payment.getPaymentStatus().getStatusCode())) {
            throw new IllegalArgumentException("Only PROCESSING payments can be cancelled");
        }

        var cancelledStatus = paymentStatusRepo.findByStatusCode(PaymentStatusConstant.CANCELLED.name())
                .orElseThrow(() -> new IllegalArgumentException("Payment Status not found"));

        payment.setPaymentStatus(cancelledStatus);
        paymentRepo.save(super.update(payment));

        return new CommonResDTO("Payment cancelled successfully");
    }

    @Override
    @Cacheable(value = "paymenthistory", key = "#customerId + ':' + #status + ':' + #page + ':' + #limit")
    public PaymentPageDTO<PaymentResDTO> getPaymentHistory(Integer page, Integer limit, String status, String customerId) {
        var pageable = PageRequest.of(page - 1, limit);

        if (status != null && !status.isEmpty()) {
            return getPaymentHistoryByStatus(status, pageable, page, limit);
        }

        var custId = toUUID(customerId);

        var user = userRepo.findById(custId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        // Use Specification to filter by user
        var payments = paymentRepo.findAll(PaymentSpecification.byUser(user), pageable);

        return mapToDTO(payments, page, limit);
    }

    @Override
    @Cacheable(value = "paymenthistory", key = "'all' + ':' + #status + ':' + #page + ':' + #limit")
    public PaymentPageDTO<PaymentResDTO> getPaymentHistory(Integer page, Integer limit, String status) {
        var pageable = PageRequest.of(page - 1, limit);

        if (status != null && !status.isEmpty()) {
            return getPaymentHistoryByStatus(status, pageable, page, limit);
        }

        // Use Specification to get all payments
        var payments = paymentRepo.findAll(PaymentSpecification.all(), pageable);

        return mapToDTO(payments, page, limit);
    }

    private PaymentPageDTO<PaymentResDTO> getPaymentHistoryByStatus(String status, Pageable pageable, Integer page, Integer limit) {
        var userId = authUtil.getLoginId();
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }

        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var paymentStatus = paymentStatusRepo.findByStatusCode(status)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment status: " + status));

        var userRole = user.getRoleType().getRoleCode();

        // Use Specification untuk filter lebih flexible
        var specification = RoleTypeConstant.CUST.name().equals(userRole)
                ? PaymentSpecification.userAndStatus(user, paymentStatus)
                : PaymentSpecification.byStatus(paymentStatus);

        var payments = paymentRepo.findAll(specification, pageable);

        return mapToDTO(payments, page, limit);
    }

    @RabbitListener(queues = EMAIL_QUEUE_SUCCESS)
    public void receiveDataSuccess(EmailStatusPOJO pojo) throws MessagingException {
        emailUtil.sendEmail("Pembayaran Berhasil", pojo.message(), pojo.email());
    }

    @RabbitListener(queues = EMAIL_QUEUE_FAILED)
    public void receiveDataFailed(EmailStatusPOJO pojo) throws MessagingException {
        emailUtil.sendEmail("Pembayaran Gagal", pojo.message(), pojo.email());
    }

    private PaymentResDTO mapToDTO(Payment payment) {
        var createdAt = dateTimeUtil.formatToStandardString(payment.getCreatedAt());
        return new PaymentResDTO(
                payment.getId().toString(),
                payment.getPaymentCode(),
                payment.getPaymentType().getPaymentFee().toString(),
                payment.getAmount().toString(),
                payment.getPaymentType().getPaymentTypeName(),
                payment.getPaymentStatus().getStatusCode(),
                payment.getProductType().getProductName(),
                payment.getUser().getFullName(),
                payment.getUser().getEmail(),
                payment.getCustomerCode(),
                createdAt
        );
    }

    private PaymentPageDTO<PaymentResDTO> mapToDTO(Page<Payment> paymentPage, Integer page, Integer limit) {
        var paymentDTOs = paymentPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();

        return new PaymentPageDTO<>(
                page,
                limit,
                paymentPage.getTotalElements(),
                paymentDTOs
        );
    }

    private void buildAndSendEmailNotification(Payment payment, String status) {
        if (!PaymentStatusConstant.SUCCESS.name().equals(status) &&
                !PaymentStatusConstant.FAILED.name().equals(status)) {
            return;
        }

        var emailPOJO = createEmailPOJO(payment, status);
        if (emailPOJO == null) {
            return;
        }

        var queueName = PaymentStatusConstant.SUCCESS.name().equals(status)
                ? EMAIL_QUEUE_SUCCESS
                : EMAIL_QUEUE_FAILED;

        rabbitTemplate.convertAndSend(queueName, emailPOJO);
    }

    private EmailStatusPOJO createEmailPOJO(Payment payment, String status) {
        if (!PaymentStatusConstant.SUCCESS.name().equals(status) &&
                !PaymentStatusConstant.FAILED.name().equals(status)) {
            return null;
        }

        String message = emailMessageBuilder.buildPaymentHtml(
                payment.getUser().getFullName(),
                payment.getPaymentCode(),
                payment.getCustomerCode(),
                payment.getPaymentType().getPaymentFee(),
                payment.getPaymentType().getPaymentTypeName(),
                payment.getProductType().getProductName(),
                payment.getAmount(),
                status
        );

        return new EmailStatusPOJO(
                payment.getUser().getEmail(),
                payment.getUser().getFullName(),
                payment.getPaymentCode(),
                status,
                payment.getAmount().toString(),
                payment.getPaymentType().getPaymentTypeName(),
                payment.getProductType().getProductName(),
                payment.getCustomerCode(),
                payment.getPaymentType().getPaymentFee().toString(),
                message
        );
    }
}
