import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  multiply(a: number, b: number): Promise<number>;
  printText(text: string, textFormat: Object): Promise<number>;
  printBarcode(content: string, width: number, height: number): Promise<number>;
  printQrCode(content: string, width: number, height: number): Promise<number>;
  printBitmap(inputBytes: byte[]): Promise<number>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('NyxPrinter');
