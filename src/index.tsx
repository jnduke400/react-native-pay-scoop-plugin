import PayScoopPlugin from './NativePayScoopPlugin';

export function multiply(a: number, b: number): number {
  return PayScoopPlugin.multiply(a, b);
}
