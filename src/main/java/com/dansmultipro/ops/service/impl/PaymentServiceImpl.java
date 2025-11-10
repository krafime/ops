package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.constant.PaymentStatusConstant;
import com.dansmultipro.ops.constant.RoleTypeConstant;
import com.dansmultipro.ops.dto.general.CommonResDTO;
import com.dansmultipro.ops.dto.general.InsertResDTO;
import com.dansmultipro.ops.dto.payment.PaymentCreateReqDTO;
import com.dansmultipro.ops.dto.payment.PaymentPageDTO;
import com.dansmultipro.ops.dto.payment.PaymentResDTO;
import com.dansmultipro.ops.model.Payment;
import com.dansmultipro.ops.repo.*;
import com.dansmultipro.ops.service.PaymentService;
import com.dansmultipro.ops.util.AuthUtil;
import com.dansmultipro.ops.util.DateTimeUtil;
import com.dansmultipro.ops.util.PaymentCodeGenerator;
import com.dansmultipro.ops.util.UUIDUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl extends BaseService implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final PaymentTypeRepo paymentTypeRepo;
    private final ProductTypeRepo productTypeRepo;
    private final PaymentStatusRepo paymentStatusRepo;
    private final AuthUtil authUtil;


    public PaymentServiceImpl(PaymentRepo paymentRepo, UserRepo userRepo, PaymentTypeRepo paymentTypeRepo,
                              ProductTypeRepo productTypeRepo, PaymentStatusRepo paymentStatusRepo, AuthUtil authUtil) {
        this.authUtil = authUtil;
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.paymentTypeRepo = paymentTypeRepo;
        this.productTypeRepo = productTypeRepo;
        this.paymentStatusRepo = paymentStatusRepo;
    }

    @Override
    @CacheEvict(value = "paymenthistory", allEntries = true)
    public InsertResDTO createPayment(PaymentCreateReqDTO paymentReq) {
        var userId = authUtil.getLoginId();
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var paymentType = paymentTypeRepo.findById(UUIDUtil.toUUID(paymentReq.paymentTypeId()))
                .orElseThrow(() -> new IllegalArgumentException("Payment Type not found"));

        var productType = productTypeRepo.findById(UUIDUtil.toUUID(paymentReq.productTypeId()))
                .orElseThrow(() -> new IllegalArgumentException("Product Type not found"));

        var processingStatus = paymentStatusRepo.findByStatusCode(PaymentStatusConstant.PROCESSING.name())
                .orElseThrow(() -> new IllegalArgumentException("Payment Status not found"));

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
    public CommonResDTO updatePaymentStatus(String paymentId, String newStatus) {
        var payment = paymentRepo.findById(UUIDUtil.toUUID(paymentId))
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

        return new CommonResDTO("Payment status updated to " + newStatus);
    }

    @Override
    @CacheEvict(value = "paymenthistory", allEntries = true)
    public CommonResDTO cancelPayment(String paymentId) {
        var userId = authUtil.getLoginId();
        var payment = paymentRepo.findById(UUIDUtil.toUUID(paymentId))
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
    @Cacheable(value = "paymenthistory", key = "'payments:' + #page + ':' + #limit + ':' + (#status != null ? #status : 'all')")
    public PaymentPageDTO<PaymentResDTO> getPaymentHistory(Integer page, Integer limit, String status) {
        var userId = authUtil.getLoginId();
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }

        if (status != null && !status.isEmpty()) {
            return getPaymentHistoryByStatus(status, PageRequest.of(page - 1, limit));
        }

        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var userRole = user.getRoleType().getRoleCode();
        var pageable = PageRequest.of(page - 1, limit);

        // Simplify: Customer lihat punya dia, Admin/GTW lihat semua
        var payments = RoleTypeConstant.CUST.name().equals(userRole)
                ? paymentRepo.findByUserOrderByCreatedAtDesc(user, pageable)
                : paymentRepo.findAllByOrderByCreatedAtDesc(pageable);

        return mapToDTO(payments, page, limit);
    }

    @Cacheable(value = "paymenthistory", key = "'payments:status:' + #status + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    private PaymentPageDTO<PaymentResDTO> getPaymentHistoryByStatus(String status, Pageable pageable) {
        var userId = authUtil.getLoginId();
        if (userId == null) {
            throw new IllegalArgumentException("User not authenticated");
        }

        var user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var paymentStatus = paymentStatusRepo.findByStatusCode(status)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment status: " + status));

        var userRole = user.getRoleType().getRoleCode();

        // Simplify: Customer lihat punya dia dengan status X, Admin/GTW lihat semua dengan status X
        var payments = RoleTypeConstant.CUST.name().equals(userRole)
                ? paymentRepo.findByUserAndPaymentStatusOrderByCreatedAtDesc(user, paymentStatus, pageable)
                : paymentRepo.findByPaymentStatusOrderByCreatedAtDesc(paymentStatus, pageable);

        int page = pageable.getPageNumber() + 1;
        int limit = pageable.getPageSize();
        return mapToDTO(payments, page, limit);
    }

    private PaymentResDTO mapToDTO(Payment payment) {
        var createdAt = DateTimeUtil.formatToStandardString(payment.getCreatedAt());
        return new PaymentResDTO(
                payment.getId().toString(),
                payment.getPaymentCode(),
                payment.getPaymentType().getPaymentFee(),
                payment.getAmount(),
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
}

