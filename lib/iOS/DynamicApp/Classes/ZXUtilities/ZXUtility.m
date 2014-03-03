//
//  Utility.m
//  ZyyxLibraries
//
//  Created by gotow on 10/02/26.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import "ZXUtility.h"
#import "define.h"


@implementation ZXUtility

+ (NSString *)descriptionOfCGRect:(CGRect)rect {
    return [NSString stringWithFormat:
            @"CGRect(x:%g, y:%g, width:%g, height:%g)",
            rect.origin.x, rect.origin.y, rect.size.width, rect.size.height];
}

+ (BOOL)rectIsPortrait:(CGRect)rect {
    return (rect.size.height >= rect.size.width) ? YES : NO;
}

+ (BOOL)rectIsLandscape:(CGRect)rect {
    return ![self rectIsPortrait:rect];
}

+ (CGFloat)scaleForRect:(CGRect)targetRect byRect:(CGRect)baseRect {
    CGFloat width_scale = targetRect.size.width / baseRect.size.width;
    CGFloat height_scale = targetRect.size.height / baseRect.size.height;

    return (width_scale > height_scale) ? height_scale : width_scale;
}

+ (CGRect)rectAsCenterForRect:(CGRect)targetRect inRect:(CGRect)inRect {
    CGRect rect = targetRect;
    CGSize base = inRect.size;
    rect.origin.x = base.width / 2 - rect.size.width / 2;
    rect.origin.y = base.height / 2 - rect.size.height / 2;
    return rect;
}


+ (BOOL)deviceIsHighResolution {
    UIScreen *screen = [UIScreen mainScreen];
    return ([screen respondsToSelector:@selector(scale)] && screen.scale >= 2.0);
}

+ (BOOL)deviceIs_iPad {
    UIDevice *device = [UIDevice currentDevice];

    if ([device respondsToSelector:@selector(userInterfaceIdiom)]
        && device.userInterfaceIdiom == UIUserInterfaceIdiomPad)
    {
        return YES;
    } else {
        return NO;
    }
}


+ (void)showAlertThatFeatureIsNotAvailable {
    static UIAlertView *alert;
    if (!alert) {
        alert = [[UIAlertView alloc] initWithTitle:@""
                                           message:@"This feature is not available."
                                          delegate:nil
                                 cancelButtonTitle:nil
                                 otherButtonTitles:@"OK", nil];
    }
    [alert show];
}


+ (UIImage *)blankImageForRect:(CGRect)rect {
    return [self blankImageForRect:rect withStroke:YES]; // backward compatibility.
}

+ (UIImage *)blankImageForRect:(CGRect)rect withStroke:(BOOL)shouldStroke {
    NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];

    NSData *data = nil;
    UIImage *image = nil;

    CGContextRef context = [self newBlankContextForRect:rect intoData:&data];
    if (context && data) {
        CGContextSetRGBFillColor(context, 1, 1, 1, 1); // fill white
        CGContextFillRect(context, rect);
        if (shouldStroke) {
            CGContextSetRGBStrokeColor(context, 0, 0, 0, 1); // stroke black
            CGContextStrokeRect(context, rect);
        }

        CGImageRef image_ref = CGBitmapContextCreateImage(context);
        image = [[UIImage alloc] initWithCGImage:image_ref];

        CGImageRelease(image_ref);
    }
    CGContextRelease(context);

    [pool release];
    return [image autorelease];
}


+ (CGContextRef)newBlankContextForRect:(CGRect)rect intoData:(NSData **)dataRef {
    size_t width = (size_t)rect.size.width;
    size_t height = (size_t)rect.size.height;
    size_t bitsPerComponent = 8; // 32bit RGB (8x(3+1) skip alpha info)
    size_t bytesPerRow = 4 * width;
    NSMutableData *data = [NSMutableData dataWithLength:(bytesPerRow * height)];
    CGColorSpaceRef space = CGColorSpaceCreateDeviceRGB();
    CGBitmapInfo info = (kCGImageAlphaPremultipliedLast & kCGBitmapAlphaInfoMask) | kCGBitmapByteOrderDefault;

    CGContextRef context = CGBitmapContextCreate([data mutableBytes], width, height,
                                                 bitsPerComponent, bytesPerRow, space, info);
    if (dataRef) {
        *dataRef = data;
    }
    CGColorSpaceRelease(space);
    return context;
}


+ (void)runApplicationTests {
#if TARGET_IPHONE_SIMULATOR
    id pool = [[NSAutoreleasePool alloc] init];

    NSString *theBundleSuffix = @"Tests.octest";
    NSMutableDictionary *theBundles = [NSMutableDictionary dictionary];
    NSString *theResoucePath = [[NSBundle mainBundle] resourcePath];
    NSArray *theFiles = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:theResoucePath error:NULL];
    if (theFiles) {
        for (NSString *aFile in theFiles) {
            if ([aFile hasSuffix:theBundleSuffix]) {
                NSBundle *aBundle = [NSBundle bundleWithPath:
                                     [theResoucePath stringByAppendingPathComponent:aFile]];
                if (aBundle) {
                    [theBundles setValue:aBundle forKey:aFile];
                }
            }
        }
    }

    for (NSString *aFile in theBundles) {
        NSBundle *theTests = [theBundles valueForKey:aFile];
        [theTests load];
        Class theSuiteClass = NSClassFromString(@"SenTestSuite");
        if (theSuiteClass) {
            id theSuite = [theSuiteClass performSelector:@selector(testSuiteForBundlePath:)
                                              withObject:[theTests bundlePath]];
            [theSuite performSelector:@selector(run)];
        }
    }

    [pool release];
#endif
}

+ (CGFloat)deviceOSVersion {
    return [[[UIDevice currentDevice] systemVersion] floatValue];
}


@end
