// VNPay Integration JavaScript

function initiateVNPayPayment(orderData) {
    // Prepare payment request
    const paymentRequest = {
        orderId: orderData.orderId || generateOrderId(),
        amount: orderData.amount,
        orderInfo: orderData.orderInfo || `Thanh toán đơn hàng ${orderData.orderId}`,
        bankCode: orderData.bankCode || '', // Optional: specific bank
        language: 'vn'
    };

    console.log('Initiating VNPay payment:', paymentRequest);

    // Show loading
    showPaymentLoading(true);

    // Call API to get payment URL
    fetch('/vnpay/get-payment-url', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify(paymentRequest)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        showPaymentLoading(false);
        
        if (data.status === 'OK' && data.paymentUrl) {
            console.log('Redirecting to VNPay:', data.paymentUrl);
            // Redirect to VNPay
            window.location.href = data.paymentUrl;
        } else {
            console.error('VNPay error response:', data);
            showPaymentError(data.message || 'Có lỗi xảy ra khi tạo liên kết thanh toán');
            // Reset processing state in parent page
            if (window.setProcessingState) {
                window.setProcessingState(false);
            }
        }
    })
    .catch(error => {
        console.error('VNPay payment error:', error);
        showPaymentLoading(false);
        showPaymentError('Có lỗi xảy ra khi kết nối đến cổng thanh toán. Vui lòng thử lại!');
        // Reset processing state in parent page
        if (window.setProcessingState) {
            window.setProcessingState(false);
        }
    });
}

function generateOrderId() {
    return 'ORD_' + new Date().getTime() + '_' + Math.random().toString(36).substring(2, 8).toUpperCase();
}

function showPaymentLoading(show) {
    const loadingEl = document.getElementById('payment-loading');
    const checkoutButton = document.getElementById('checkout-button');
    const buttonText = document.getElementById('button-text');
    
    if (show) {
        if (loadingEl) loadingEl.style.display = 'block';
        if (checkoutButton) checkoutButton.disabled = true;
        if (buttonText) buttonText.textContent = 'Đang xử lý...';
    } else {
        if (loadingEl) loadingEl.style.display = 'none';
        if (checkoutButton) checkoutButton.disabled = false;
        // Reset button text will be handled by updateButtonText() in checkout.html
    }
}

function showPaymentError(message) {
    const errorEl = document.getElementById('payment-error');
    if (errorEl) {
        errorEl.textContent = message;
        errorEl.style.display = 'block';
        
        // Auto hide after 5 seconds
        setTimeout(() => {
            errorEl.style.display = 'none';
        }, 5000);
    } else {
        alert(message);
    }
}

// Example usage in checkout page
function setupVNPayButton() {
    const vnpayButton = document.getElementById('vnpay-button');
    if (vnpayButton) {
        vnpayButton.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Get order data from form or page
            const orderData = {
                orderId: document.getElementById('order-id')?.value || generateOrderId(),
                amount: parseInt(document.getElementById('total-amount')?.textContent?.replace(/[^\d]/g, '') || 0),
                orderInfo: document.getElementById('order-info')?.value || 'Thanh toán đơn hàng',
                bankCode: document.getElementById('bank-code')?.value || ''
            };
            
            // Validate
            if (!orderData.amount || orderData.amount <= 0) {
                showPaymentError('Số tiền thanh toán không hợp lệ');
                return;
            }
            
            // Initiate payment
            initiateVNPayPayment(orderData);
        });
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    setupVNPayButton();
});

// Bank selection helper
const vnpayBanks = {
    'VIETCOMBANK': 'Ngân hàng TMCP Ngoại Thương Việt Nam',
    'VIETINBANK': 'Ngân hàng TMCP Công Thương Việt Nam',
    'BIDV': 'Ngân hàng TMCP Đầu tư và Phát triển Việt Nam',
    'AGRIBANK': 'Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam',
    'TECHCOMBANK': 'Ngân hàng TMCP Kỹ Thương Việt Nam',
    'MBBANK': 'Ngân hàng TMCP Quân Đội',
    'VPBANK': 'Ngân hàng TMCP Việt Nam Thịnh Vượng',
    'SACOMBANK': 'Ngân hàng TMCP Sài Gòn Thương Tín',
    'HDBANK': 'Ngân hàng TMCP Phát triển Thành phố Hồ Chí Minh'
};

function populateBankSelect(selectElement) {
    if (selectElement) {
        selectElement.innerHTML = '<option value="">Chọn ngân hàng (tùy chọn)</option>';
        for (const [code, name] of Object.entries(vnpayBanks)) {
            const option = document.createElement('option');
            option.value = code;
            option.textContent = name;
            selectElement.appendChild(option);
        }
    }
}
