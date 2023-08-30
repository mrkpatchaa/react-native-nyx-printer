import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-nyx-printer' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const NyxPrinterModule = isTurboModuleEnabled
  ? require('./NativeNyxPrinter').default
  : NativeModules.NyxPrinter;

const NyxPrinter = NyxPrinterModule
  ? NyxPrinterModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return NyxPrinter.multiply(a, b);
}
