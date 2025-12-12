import { TurboModuleRegistry, type TurboModule } from 'react-native';

export interface Spec extends TurboModule {
  // Create order method (now includes credentials)
  createOrder(orderData: {
    username: string;
    password: string;
    amount: number;
    currency: string;
    receipt: string;
    paymentMethod: string;
    paymentChannel: string;
    customer: {
      firstName: string;
      lastName: string;
      email: string;
    };
    metadata?: {
      promoCode?: string;
      deliveryNote?: string;
    };
  }): Promise<{
    orderNumber: string;
    sessionId: string;
    message: string;
  }>;

  // Show payment page method
  showPaymentPage(config: {
    orderNumber: string;
    username: string;
    password: string;
    sessionId: string;
    amount?: string;
    currency?: string;
    primaryColor?: string;
    successColor?: string;
    errorColor?: string;
    textColor?: string;
    secondaryTextColor?: string;
    backgroundColor?: string;
  }): Promise<{
    transactionId: string;
    orderId: string;
  }>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('PayScoopPlugin');
