//
//  CanvasView.h
//  PaintTest
//
//  Created by gotow on 10/11/18.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>


@class ZXCanvasLineView;
@class ZXCanvasViewColoredPathContainer;


@interface ZXCanvasView : UIView {
    NSMutableArray *plotLines;
    CGFloat plotLineWidth;
    CGFloat eraseLineWidth;
    ZXCanvasViewColoredPathContainer *currentLine;
    ZXCanvasLineView *currentLineView;
    CGImageRef currentImageCache_;
}

@property (strong) NSMutableArray *plotLines; // typically should not modify directly.
@property (nonatomic) CGFloat plotLineWidth;
@property (nonatomic) CGFloat eraseLineWidth;
@property (strong) ZXCanvasViewColoredPathContainer *currentLine;
@property (strong) ZXCanvasLineView *currentLineView;
@property (nonatomic, retain) __attribute__((NSObject)) CGImageRef currentImageCache DEPRECATED_ATTRIBUTE;

@property (nonatomic, unsafe_unretained, readonly) CADisplayLink *displayLink;
@property (nonatomic, unsafe_unretained, readonly) NSThread *worker;
- (void)setDisplayLink:(CADisplayLink *)aDisplayLink withWorker:(NSThread *)aWorker;
- (void)clearDisplayLink;
+ (SEL)selectorForDisplayLink;

- (void)drawRect:(CGRect)aRect toContext:(CGContextRef)aContext;
- (CGImageRef)newCurrentCGImageWithRect:(CGRect)rect;
- (UIImage *)currentImage;
- (UIImage *)currentImageWithRect:(CGRect)rect;
- (void)writeCurrentImageWithRect:(CGRect)rect toPath:(NSString *)aFilePath;

+ (NSMutableArray *)plotLinesByDeletingUnnecessaryEraseFrom:(NSArray *)aLineArray;
- (NSMutableArray *)plotLinesByDeletingUnnecessaryErase;

- (void)addNewLineWithStartingPoint:(CGPoint)aPoint color:(UIColor *)aColor; // eraser mode when aColor is nil.
- (void)addNewLineWithStartingPoint:(CGPoint)aPoint color:(UIColor *)aColor lineWidth:(CGFloat)aWidth;
- (void)addPlotPoint:(CGPoint)aPoint;
- (void)addPlotPoint:(CGPoint)aPoint withUpdate:(BOOL)shouldUpdate;
- (void)finishCurrentLine;
- (void)cancelCurrentLine;
- (void)clearAllLines;
- (void)resetPlotLines;

- (ZXCanvasViewColoredPathContainer *)undoPrevioiusLine;
- (void)redoWithLine:(ZXCanvasViewColoredPathContainer *)aLine;
- (void)updateBaseImage;

@end



#pragma mark -
#pragma mark Classes for internal use.

@interface ZXCanvasLineView : UIView {
    ZXCanvasViewColoredPathContainer *pathContainer;
}

@property (nonatomic, strong) ZXCanvasViewColoredPathContainer *pathContainer;

@end



@interface ZXCanvasViewColoredPathContainer : NSObject <NSCoding> {
    CGPoint startingPoint;
    NSValue *finishingPoint;
    UIBezierPath *bezierPath;
    NSMutableArray *drawPoints;
    UIColor *color;
    CGFloat drawWidth;
}

@property (nonatomic) CGPoint startingPoint;
@property (nonatomic, strong) NSValue *finishingPoint;
@property (nonatomic, strong) UIBezierPath *bezierPath;
@property (nonatomic, strong) NSMutableArray *drawPoints; // currently unused.
@property (nonatomic, strong) UIColor *color;
@property (nonatomic) CGFloat drawWidth;
@property (nonatomic, readonly) BOOL isDraw; // true when color is not nil.
@property (nonatomic, readonly) CGRect lineBounds;

- (id)initWithStartingPoint:(CGPoint)aPoint color:(UIColor *)aColor drawWidth:(CGFloat)aWidth;
+ (id)containerWithStartingPoint:(CGPoint)aPoint color:(UIColor *)aColor drawWidth:(CGFloat)aWidth;

- (void)draw;
- (void)drawToContext:(CGContextRef)aContext;
- (void)addPoint:(CGPoint)aPoint;
- (void)finish;

- (UIBezierPath *)bezierPathOfPoint:(CGPoint)aPoint;

@end
