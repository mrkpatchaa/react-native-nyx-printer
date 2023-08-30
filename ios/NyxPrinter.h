
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNNyxPrinterSpec.h"

@interface NyxPrinter : NSObject <NativeNyxPrinterSpec>
#else
#import <React/RCTBridgeModule.h>

@interface NyxPrinter : NSObject <RCTBridgeModule>
#endif

@end
