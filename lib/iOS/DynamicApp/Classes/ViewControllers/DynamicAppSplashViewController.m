/*
 * Copyright (C) 2014 ZYYX, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "DynamicAppSplashViewController.h"
#import "ZXUtility.h"
//#import "ZipArchive.h"
#import "Shortcut.h"

const CGFloat DEFAULT_LABEL_WIDTH = 280.0;
const CGFloat DEFAULT_LABEL_HEIGHT = 50.0;

@interface DynamicAppSplashViewController(private)
- (void)adjustFrameWithInterfaceOrientation:(UIInterfaceOrientation)anOrientation;
- (BOOL)updateLocalHTML:(NSString *)filePath;
@end


@implementation DynamicAppSplashViewController

@synthesize downloadController;
@synthesize splashView;
@synthesize backgroundColor;
@synthesize defaultImage;
@synthesize portraitImage;
@synthesize landscapeImage;
@synthesize activityIndicatorView;
@synthesize loadingLabel;
@synthesize delayToHiding;

- (UIImage *)portraitImage {
    if (portraitImage) {
        return portraitImage;
    } else {
        return self.defaultImage;
    }
}

- (UIImage *)landscapeImage {
    if (landscapeImage) {
        return landscapeImage;
    } else {
        return self.defaultImage;
    }
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
    
    CGRect screenBounds = [ [ UIScreen mainScreen ] bounds ];
    self.view = [[[UIView alloc] initWithFrame:screenBounds] autorelease];
    
    self.view.autoresizingMask = (  UIViewAutoresizingFlexibleWidth
                                  | UIViewAutoresizingFlexibleHeight
                                  | UIViewAutoresizingFlexibleLeftMargin
                                  | UIViewAutoresizingFlexibleRightMargin
                                  | UIViewAutoresizingFlexibleTopMargin
                                  | UIViewAutoresizingFlexibleBottomMargin);
    self.splashView = [[[UIImageView alloc] initWithFrame:self.view.frame] autorelease];
    self.splashView.autoresizingMask = UIViewAutoresizingNone;
    [self.view addSubview:self.splashView];
    if (!self.backgroundColor) {
        self.backgroundColor = [UIColor whiteColor];
    }
    
    
    CGRect labelFrame = CGRectMake(0, 0, DEFAULT_LABEL_WIDTH, DEFAULT_LABEL_HEIGHT);
    
    self.loadingLabel = [[[UILabel alloc] initWithFrame:labelFrame] autorelease];
	self.loadingLabel.text = NSLocalizedString(@"Loading...", nil);
	self.loadingLabel.textColor = [UIColor grayColor];
	self.loadingLabel.backgroundColor = [UIColor clearColor];
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
	self.loadingLabel.textAlignment = NSTextAlignmentCenter;
#else
	self.loadingLabel.textAlignment = UITextAlignmentCenter;
#endif
    self.loadingLabel.font = [UIFont boldSystemFontOfSize:[UIFont labelFontSize]];
	
    self.loadingLabel.autoresizingMask = (UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin | 
                                          UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin);
	
	[self.view addSubview:self.loadingLabel];
    
    self.activityIndicatorView = [[[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray] autorelease];
    self.activityIndicatorView.frame = CGRectMake(0, 0, 200, 200);
    [self.view addSubview:self.activityIndicatorView];
	
    self.loadingLabel.center = self.view.center;
    
    CGRect rect = self.loadingLabel.frame;
    self.loadingLabel.frame = CGRectMake(rect.origin.x, rect.origin.y + 50, rect.size.width, rect.size.height);
    self.activityIndicatorView.center = self.view.center;
    rect = self.activityIndicatorView.frame;
    rect.origin.y += 40 + self.loadingLabel.frame.size.height;
    self.activityIndicatorView.frame = rect;
    
}


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self adjustFrameWithInterfaceOrientation:self.interfaceOrientation];
    
    
    self.downloadController = [[[DownloadConttroller alloc] init] autorelease];
    self.downloadController.delegate = self;
    
    [self.activityIndicatorView startAnimating];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    [self.downloadController start];
}
- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    
    self.splashView = nil;
    self.activityIndicatorView = nil;
    self.loadingLabel = nil;
    self.downloadController = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Overriden to allow any orientation.
    SEL sel = @selector(presentingViewController);
    UIViewController *parent = ([self respondsToSelector:sel] ? [self performSelector:sel] : self.parentViewController);
    if (parent) {
        return [parent shouldAutorotateToInterfaceOrientation:interfaceOrientation];
    } else {
        if ([ZXUtility deviceIs_iPad]) {
            return YES;
        } else {
            return (interfaceOrientation == UIInterfaceOrientationPortrait
                    || interfaceOrientation == UIInterfaceOrientationLandscapeRight);
        }
    }
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    [self adjustFrameWithInterfaceOrientation:toInterfaceOrientation];
}

- (void)dealloc {
    self.downloadController.delegate = nil;
    self.downloadController = nil;
    self.splashView = nil;
    self.backgroundColor = nil;
    self.defaultImage = nil;
    self.portraitImage = nil;
    self.landscapeImage = nil;
    self.activityIndicatorView = nil;
    self.loadingLabel = nil;
    
    [super dealloc];
}

- (void)adjustFrameWithInterfaceOrientation:(UIInterfaceOrientation)anOrientation {
    self.view.backgroundColor = self.backgroundColor;
    
    BOOL isPortrait = UIInterfaceOrientationIsPortrait(anOrientation);
    
    UIScreen *screen = [UIScreen mainScreen];
    CGRect baseFrame = screen.bounds;
    
    if (!isPortrait) {
        baseFrame.size = CGSizeMake(baseFrame.size.height, baseFrame.size.width);
    }
    if (![UIApplication sharedApplication].statusBarHidden) {
        baseFrame.size.height -= 20;
    }
    self.view.frame = baseFrame;
    
    UIImage *anImage = (isPortrait ? self.portraitImage : self.landscapeImage);
    if (!anImage) {
        anImage = [UIImage imageNamed:@"Default.png"];
    }
    self.splashView.image = anImage;
    CGRect aFrame = self.splashView.frame;
    aFrame.origin = CGPointZero;
    aFrame.size = anImage.size;
    self.splashView.frame = [ZXUtility rectAsCenterForRect:aFrame inRect:baseFrame];
}

- (void)dismissSelfAnimated {
    if([self respondsToSelector:@selector(presentingViewController)]) {
        [self.presentingViewController performSelector:@selector(splashScreenFinish)];
    } else {
        [self.parentViewController performSelector:@selector(splashScreenFinish)];
    }
    
    [self.activityIndicatorView stopAnimating];
    [self dismissViewControllerAnimated:YES completion:nil];
}

# pragma mark -
# pragma mark DownloadControllerDelegate
- (void)downloadFailedWithError:(NSError *)anError {
    [self dismissSelfAnimated];
}

- (void)downloadDidFinish {
    NSString *filePath = self.downloadController.filePath;
    [self updateLocalHTML:filePath];
    
    [self performSelector:@selector(dismissSelfAnimated) withObject:nil afterDelay:self.delayToHiding];
}

- (void)downloadDidFinishWithNoNeed {
    [self performSelector:@selector(dismissSelfAnimated) withObject:nil afterDelay:self.delayToHiding];
}

- (BOOL)updateLocalHTML:(NSString *)filePath {
//    ZipArchive* zip = [[ZipArchive alloc] init];
//
//    if(![zip UnzipOpenFile:filePath]) {
//        [zip release];
//        return NO;
//    }
//
//    if(![zip UnzipFileTo:[Shortcut applicationDocumentsDirectory] overWrite:true]) {
//        [zip UnzipCloseFile];
//        [zip release];
//        return NO;
//    }
//    [zip UnzipCloseFile];
//
//    [zip release];
    
    return YES;
}
@end
