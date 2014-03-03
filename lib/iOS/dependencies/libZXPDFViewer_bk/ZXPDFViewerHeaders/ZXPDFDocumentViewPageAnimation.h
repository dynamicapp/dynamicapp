//
//  PDFDocumentViewPageAnimation.h
//  ZyyxLibraries
//
//  Created by gotow on 10/08/16.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFDocumentView.h"


// for animation
extern NSString * const ZXPDFDocumentViewPageAnimationContextNewViewKey;      // UIView
extern NSString * const ZXPDFDocumentViewPageAnimationContextCurrentViewKey;  // UIView
extern NSString * const ZXPDFDocumentViewPageAnimationContextDirectionKey;    // NSNumber(PDFDocumentViewPageDirection)
extern NSString * const ZXPDFDocumentViewPageAnimationContextCutBackPointKey; // NSValue(CGPoint);
// reserved for internal use of PDFDocumentView (typically not use in page animation)
extern NSString * const ZXPDFDocumentViewPageAnimationContextNewPageKey;      // PDFPageView
extern NSString * const ZXPDFDocumentViewPageAnimationContextOldPageKey;      // PDFPageView
extern NSString * const ZXPDFDocumentViewPageAnimationContextStateKey;        // NSNumber(PDFPageViewDoubleTruckState)


void ZXPDFDocumentViewPageAnimationInvokeCallback(id target,
                                                SEL didStopSelector,
                                                NSString *animationID,
                                                BOOL finished,
                                                void *context);
#define ZXPDFDocumentViewPageAnimationPageIsNotMoving(direction) \
    (direction == ZXPDFDocumentViewPageDoNotMove || direction == ZXPDFDocumentViewPageFadeInOut)



@protocol ZXPDFDocumentViewPageAnimation <NSObject>

// each parameters are similar to UIView's class methods for animation.
// e.g.
// [UIView beginAnimations:animationID context:(void *)context];
// [UIView setAnimationDelegate:target];
// [UIView setAnimationDidStopSelector:action];
//
// for these implementation.
// should be send 'action' to 'target' with similar format to UIView callback.
// and pass through 'animationID' and 'context' when invoke callback.
// when calling back, can use 'ZXPDFDocumentViewPageAnimationInvokeCallback()' as default implementation.
//
// format of 'action'
// - (void)animationDidStop:(NSString *)animationID finished:(NSNumber *)finished context:(void *)context

@required

- (void)invokeTriggerAnimationWithID:(NSString *)animationID
                             context:(NSDictionary *)context
                              target:(id)target
                     didStopSelector:(SEL)action;
- (void)invokeReplaceAnimationWithID:(NSString *)animationID
                             context:(NSDictionary *)context
                              target:(id)target
                     didStopSelector:(SEL)action;

@optional // if not supplied, do nothing.

- (void)invokeFakeTriggerAnimationWithID:(NSString *)animationID
                                 context:(NSDictionary *)context
                                  target:(id)target
                         didStopSelector:(SEL)action;
- (void)invokeCutBackAnimationWithID:(NSString *)animationID
                             context:(NSDictionary *)context
                              target:(id)target
                     didStopSelector:(SEL)action;

@end



// base implementation of PDFDocumentViewPageAnimation protocol.
// only implemeted fading in/out.
@interface ZXPDFDocumentViewBasePageAnimator : NSObject <ZXPDFDocumentViewPageAnimation> {
}
@end
