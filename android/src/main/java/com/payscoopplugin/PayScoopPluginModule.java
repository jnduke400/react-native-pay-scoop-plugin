package com.payscoopplugin;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;

// Import the native plugin classes
import com.gtl.payscoopnativeplugin.components.PaymentPlugin;
import com.gtl.payscoopnativeplugin.config.PaymentConfig;
import com.gtl.payscoopnativeplugin.interfaces.PaymentCallback;
import com.payscoopplugin.PayScoopPluginSpec;

// Import SDK classes for order creation
import org.gtl.nativesdk.android.components.CreatePayScoopOrder;
import org.gtl.nativesdk.data.dto.order.PayScoopOrderRequestDTO;
import org.gtl.nativesdk.data.dto.order.Customer;
import org.gtl.nativesdk.data.dto.order.Metadata;
import org.gtl.nativesdk.data.interfaces.PayScoopOrderCallback;
import org.gtl.nativesdk.data.dto.order.orderResponse.PayScoopOrderResponseDTO;
import org.gtl.nativesdk.data.dto.order.orderResponse.failedOrder.PayScoopErrorResponseDTO;

public class PayScoopPluginModule extends PayScoopPluginSpec {
    public static final String NAME = "PayScoopPlugin";
    private ReactApplicationContext reactContext;

    public PayScoopPluginModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @Override
    public void createOrder(ReadableMap orderData, final Promise promise) {
        try {
            // Extract credentials first (should be passed in orderData or configured)
            String username = orderData.getString("username");
            String password = orderData.getString("password");

            if (username == null || password == null) {
                promise.reject("MISSING_CREDENTIALS", "Username and password are required");
                return;
            }

            // Extract order data from ReadableMap
            int amount = orderData.getInt("amount");
            String currency = orderData.getString("currency");
            String receipt = orderData.getString("receipt");
            String paymentMethod = orderData.getString("paymentMethod");
            String paymentChannel = orderData.getString("paymentChannel");

            ReadableMap customerMap = orderData.getMap("customer");
            Customer customer = Customer.builder()
                .firstName(customerMap.getString("firstName"))
                .lastName(customerMap.getString("lastName"))
                .email(customerMap.getString("email"))
                .build();

            Metadata.Builder metadataBuilder = Metadata.builder();
            if (orderData.hasKey("metadata")) {
                ReadableMap metadataMap = orderData.getMap("metadata");
                if (metadataMap.hasKey("promoCode")) {
                    metadataBuilder.promoCode(metadataMap.getString("promoCode"));
                }
                if (metadataMap.hasKey("deliveryNote")) {
                    metadataBuilder.deliveryNote(metadataMap.getString("deliveryNote"));
                }
            }

            PayScoopOrderRequestDTO scoopOrderDto = PayScoopOrderRequestDTO.builder()
                .amount(amount)
                .currency(currency)
                .receipt(receipt)
                .paymentMethod(paymentMethod)
                .paymentChannel(paymentChannel)
                .customer(customer)
                .metadata(metadataBuilder.build())
                .build();

            CreatePayScoopOrder createPayScoopOrder = new CreatePayScoopOrder();
            createPayScoopOrder.setConfig(username, password);

            createPayScoopOrder.createOrder(scoopOrderDto, new PayScoopOrderCallback() {
                @Override
                public void onPayScoopOrderSuccessResponse(PayScoopOrderResponseDTO payScoopOrderResponse) {
                    WritableNativeMap result = new WritableNativeMap();
                    result.putString("orderNumber", payScoopOrderResponse.getOrderNumber());
                    result.putString("sessionId", payScoopOrderResponse.getSessionId());
                    result.putString("message", payScoopOrderResponse.getMessage());
                    promise.resolve(result);
                }

                @Override
                public void onPayScoopOrderErrorResponse(PayScoopErrorResponseDTO payScoopErrorResponseDTO) {
                    promise.reject("ORDER_CREATION_FAILED",
                        payScoopErrorResponseDTO.getMessage() != null ?
                        payScoopErrorResponseDTO.getMessage() : "Order creation failed");
                }
            });

        } catch (Exception e) {
            promise.reject("ORDER_ERROR", e.getMessage());
        }
    }

    @Override
    public void showPaymentPage(ReadableMap config, final Promise promise) {
        try {
            PaymentConfig paymentConfig = convertToPaymentConfig(config);

            // Use the native PaymentPlugin directly
            PaymentPlugin.showPaymentPage(
                reactContext.getCurrentActivity(),
                paymentConfig,
                new PaymentCallback() {
                    @Override
                    public void onPaymentSuccess(String transactionId, String orderId) {
                        WritableNativeMap result = new WritableNativeMap();
                        result.putString("transactionId", transactionId);
                        result.putString("orderId", orderId);
                        promise.resolve(result);
                    }

                    @Override
                    public void onPaymentFailure(String errorCode, String errorMessage) {
                        promise.reject(errorCode, errorMessage);
                    }

                    @Override
                    public void onPaymentCancelled() {
                        promise.reject("PAYMENT_CANCELLED", "Payment was cancelled by user");
                    }
                }
            );

        } catch (Exception e) {
            promise.reject("PAYMENT_INIT_ERROR", e.getMessage());
        }
    }

//    @Override
//    public void checkPaymentStatus(String orderNumber, String sessionId, Promise promise) {
//        // Implement using the SDK's CheckPayScoopPaymentStatus if needed
//        promise.reject("NOT_IMPLEMENTED", "Payment status check not implemented");
//    }

    private PaymentConfig convertToPaymentConfig(ReadableMap config) {
        PaymentConfig paymentConfig = new PaymentConfig(
            config.getString("orderNumber"),
            config.getString("username"),
            config.getString("password"),
            config.getString("sessionId")
        );

        // Set optional fields
        if (config.hasKey("amount")) {
            paymentConfig.setAmount(config.getString("amount"));
        }
        if (config.hasKey("currency")) {
            paymentConfig.setCurrency(config.getString("currency"));
        }

        // Set colors
        if (config.hasKey("primaryColor")) {
            paymentConfig.setPrimaryColor(config.getString("primaryColor"));
        }
        if (config.hasKey("successColor")) {
            paymentConfig.setSuccessColor(config.getString("successColor"));
        }
        if (config.hasKey("errorColor")) {
            paymentConfig.setErrorColor(config.getString("errorColor"));
        }
        if (config.hasKey("textColor")) {
            paymentConfig.setTextColor(config.getString("textColor"));
        }
        if (config.hasKey("secondaryTextColor")) {
            paymentConfig.setSecondaryTextColor(config.getString("secondaryTextColor"));
        }
        if (config.hasKey("backgroundColor")) {
            paymentConfig.setBackgroundColor(config.getString("backgroundColor"));
        }

        return paymentConfig;
    }
}
