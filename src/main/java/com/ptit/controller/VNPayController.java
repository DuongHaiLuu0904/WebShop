package com.ptit.controller;

import com.ptit.dto.VNPayRequestDTO;
import com.ptit.dto.VNPayResponseDTO;
import com.ptit.service.VNPayService;
import com.ptit.service.OrderService;
import com.ptit.util.VNPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnpayService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody VNPayRequestDTO requestDTO,
            HttpServletRequest request) {
        try {

            // Validate required fields
            if (requestDTO.getOrderId() == null || requestDTO.getOrderId().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Order ID không được để trống");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (requestDTO.getAmount() <= 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Số tiền phải lớn hơn 0");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Set customer IP if not provided
            if (requestDTO.getCustomerIp() == null) {
                requestDTO.setCustomerIp(VNPayUtil.getIpAddress(request));
            }

            // Convert amount to VND cents (multiply by 100)
            requestDTO.setAmount(requestDTO.getAmount() * 100 * 25000);

            String paymentUrl = vnpayService.createPaymentUrl(requestDTO, request);

            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Có lỗi xảy ra khi tạo URL thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Form-based payment endpoint (redirect from checkout page)
    @PostMapping("/payment")
    public String initiatePayment(@RequestParam String orderId,
            @RequestParam double amount,
            @RequestParam String orderInfo,
            @RequestParam String address,
            HttpServletRequest request) {
        try {
            // Create VNPay request DTO
            VNPayRequestDTO requestDTO = new VNPayRequestDTO();
            requestDTO.setOrderId(orderId);
            requestDTO.setAmount((long) (amount * 100)); // Convert to VND cents
            requestDTO.setOrderInfo(orderInfo);
            requestDTO.setCustomerIp(VNPayUtil.getIpAddress(request));

            // Create payment URL
            String paymentUrl = vnpayService.createPaymentUrl(requestDTO, request);

            // Redirect to VNPay
            return "redirect:" + paymentUrl;

        } catch (Exception e) {
            // Redirect to error page if payment creation fails
            return "redirect:/cart/checkout?error=payment_failed";
        }
    }

    @GetMapping("/return")
    public String paymentReturn(HttpServletRequest request, Model model) {
        System.out.println("=== VNPay Return Handler ===");
        VNPayResponseDTO responseDTO = vnpayService.processReturn(request);

        model.addAttribute("vnpayResponse", responseDTO);

        // Redirect to appropriate page based on payment status
        if ("OK".equals(responseDTO.getStatus())) {
            System.out.println("Payment successful for temp order: " + responseDTO.getOrderId());

            // Tạo đơn hàng thực sự từ thông tin đã lưu
            // (Thông tin sẽ được xử lý trong success page từ sessionStorage)

            return "payment/success";
        } else {
            System.out.println("Payment failed for temp order: " + responseDTO.getOrderId() +
                    " - Status: " + responseDTO.getStatus() +
                    " - Message: " + responseDTO.getMessage());
            return "payment/failed";
        }
    }

    @PostMapping("/ipn")
    @ResponseBody
    public ResponseEntity<String> ipnReceiver(HttpServletRequest request) {
        try {
            VNPayResponseDTO responseDTO = vnpayService.processReturn(request);

            if ("OK".equals(responseDTO.getStatus())) {
                // Update order status in database here
                // Example: orderService.updateOrderStatus(responseDTO.getOrderId(), "PAID");

                return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
            } else {
                return ResponseEntity.ok("{\"RspCode\":\"97\",\"Message\":\"Confirm Fail\"}");
            }

        } catch (Exception e) {
            return ResponseEntity.ok("{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}");
        }
    }

    // API endpoint to get payment URL 
    @PostMapping("/get-payment-url")
    @ResponseBody
    public ResponseEntity<VNPayResponseDTO> getPaymentUrl(@RequestBody VNPayRequestDTO requestDTO,
            HttpServletRequest request) {
        try {
            VNPayResponseDTO responseDTO = new VNPayResponseDTO();

            if (requestDTO.getOrderId() == null || requestDTO.getAmount() <= 0) {
                responseDTO.setStatus("ERROR");
                responseDTO.setMessage("Thông tin đơn hàng không hợp lệ");
                return ResponseEntity.badRequest().body(responseDTO);
            }

            if (requestDTO.getCustomerIp() == null) {
                requestDTO.setCustomerIp(VNPayUtil.getIpAddress(request));
            }

            String paymentUrl = vnpayService.createPaymentUrl(requestDTO, request);

            responseDTO.setStatus("OK");
            responseDTO.setMessage("Tạo URL thanh toán thành công");
            responseDTO.setPaymentUrl(paymentUrl);

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            VNPayResponseDTO responseDTO = new VNPayResponseDTO();
            responseDTO.setStatus("ERROR");
            responseDTO.setMessage("Có lỗi xảy ra khi tạo URL thanh toán");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }
}
