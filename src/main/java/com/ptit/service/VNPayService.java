package com.ptit.service;

import com.ptit.config.VNPayConfig;
import com.ptit.dto.VNPayRequestDTO;
import com.ptit.dto.VNPayResponseDTO;
import com.ptit.util.VNPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Autowired
    private VNPayConfig vnpayConfig;

    public String createPaymentUrl(VNPayRequestDTO requestDTO, HttpServletRequest request) {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = requestDTO.getOrderId();
        String vnp_IpAddr = VNPayUtil.getIpAddress(request);
        String vnp_TmnCode = vnpayConfig.getTmnCode();
        String orderType = "other";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(requestDTO.getAmount()));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (requestDTO.getBankCode() != null && !requestDTO.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", requestDTO.getBankCode());
        }

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", requestDTO.getOrderInfo());
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = requestDTO.getLanguage();
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }

        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sử dụng phương thức mới để build hash data
        String hashData = VNPayUtil.buildHashData(vnp_Params);
        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getHashSecret(), hashData);
        
        // Build query string cho URL với US_ASCII encoding
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                try {
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                query.append('&');
            }
        }
        
        // Remove last '&' and add secure hash
        if (query.length() > 0) {
            query.setLength(query.length() - 1);
        }
        
        String queryUrl = query.toString();
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnpayConfig.getVnpUrl() + "?" + queryUrl;

        return paymentUrl;
    }

    public VNPayResponseDTO processReturn(HttpServletRequest request) {
        System.out.println("=== VNPay processReturn ===");
        Map<String, String> fields = new HashMap<>();

        // Collect all parameters
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
                System.out.println(fieldName + " = " + fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
       
        // Remove SecureHash and SecureHashType from fields before calculating hash
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        String signValue = VNPayUtil.hashAllFields(fields, vnpayConfig.getHashSecret());
       
        VNPayResponseDTO responseDTO = new VNPayResponseDTO();

        if (signValue.equals(vnp_SecureHash)) {
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            if ("00".equals(vnp_ResponseCode)) {
                responseDTO.setStatus("OK");
                responseDTO.setMessage("Giao dịch thành công");
            } else {
                responseDTO.setStatus("FAILED");
                responseDTO.setMessage("Giao dịch không thành công");
            }
            responseDTO.setResponseCode(vnp_ResponseCode);
            responseDTO.setOrderId(request.getParameter("vnp_TxnRef"));
            responseDTO.setAmount(request.getParameter("vnp_Amount"));
            responseDTO.setOrderInfo(request.getParameter("vnp_OrderInfo"));
            responseDTO.setTransactionId(request.getParameter("vnp_TransactionNo"));
        } else {
            responseDTO.setStatus("INVALID");
            responseDTO.setMessage("Chữ ký không hợp lệ");
        }

        return responseDTO;
    }


    public String getReturnUrl() {
        return vnpayConfig.getReturnUrl();
    }

    public String getTmnCode() {
        return vnpayConfig.getTmnCode();
    }
}