import PayScoopPlugin, { type Spec } from './NativePayScoopPlugin';

// Define the public input and output types for better consumer experience.
// We can rename them for clarity.

// Input Type for createOrder
export type OrderRequest = Parameters<Spec['createOrder']>[0];

// Output Type for createOrder
export type OrderResponse = Awaited<ReturnType<Spec['createOrder']>>;

// Input Type for showPaymentPage
export type PaymentConfig = Parameters<Spec['showPaymentPage']>[0];

// Output Type for showPaymentPage
export type PaymentResult = Awaited<ReturnType<Spec['showPaymentPage']>>;

/**
 * Creates a PayScoop Order on the backend.
 * @param orderData - Request details including credentials, amount, and customer info.
 * @returns A promise resolving to the created order number and session ID.
 */
export const createOrder = (
  orderData: OrderRequest
): Promise<OrderResponse> => {
  return PayScoopPlugin.createOrder(orderData);
};

/**
 * Opens the native payment page (e.g., as a dialog or new activity/view).
 * @param config - Configuration including the Order ID and Session ID obtained from createOrder.
 * @returns A promise resolving to the transaction details upon successful payment.
 */
export const showPaymentPage = (
  config: PaymentConfig
): Promise<PaymentResult> => {
  return PayScoopPlugin.showPaymentPage(config);
};

// You can optionally export the entire native module instance, but it's often cleaner to wrap it.
// export default NativePayScoopModule;

// NOTE: If you are supporting older RN versions (pre-TurboModules),
// you would add a fallback logic here using Platform.select.
