//
//  DynamicAppViewController.m
//  DynamicApp
//
//  Created by ZYYX on 12/01/16.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppViewController.h"
#import "DynamicAppSplashViewController.h"
#import "define.h"
#import "DynamicAppPlugin.h"
#import "Shortcut.h"
#import "UrlCommand.h"
#import "ZXUtility.h"

#import "Camera.h"
#import "Sound.h"
#import "Movie.h"
#import "File.h"
#import "ImageDecrypt.h"
#import "Encryptor.h"
#import "Notification.h"
#import "LoadingScreen.h"
#import "Cache.h"
#import "QRReader.h"
#import "Database.h"
#import "AddressBook.h"
#import "Bluetooth.h"
#import "Bluetooth4LE.h"
#import "AppVersion.h"
#import "Contacts.h"
#import "PDFViewer.h"
#import "Ad.h"

@interface DynamicAppViewController()
@property (nonatomic, retain) NSMutableDictionary *pluginObjects;
@property (nonatomic) BOOL isDataDownloaded;
@property (nonatomic) BOOL isInitialized;

- (void)initialize;
- (void)splashScreenFinish;
- (NSArray*)checkSupportedInterfaceOrientations:(NSArray*)orientations;
@end

@implementation DynamicAppViewController

@synthesize webView;
@synthesize pluginObjects;

@synthesize isDataDownloaded;
@synthesize isInitialized;
@synthesize supportedOrientations;

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
    [super loadView];
}

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.isDataDownloaded = NO;
    self.isInitialized = NO;
    self.view.backgroundColor = [UIColor blackColor];
    self.supportedOrientations = [self checkSupportedInterfaceOrientations:
                                  [[[NSBundle mainBundle] infoDictionary] objectForKey:@"UISupportedInterfaceOrientations"]];

    CGRect screenBounds = [[UIScreen mainScreen] bounds];
    
    self.webView = [[[UIWebView alloc] initWithFrame:screenBounds] autorelease];
    self.webView.delegate = self;

    self.webView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    
    UIScrollView *scrollView = [self.webView.subviews objectAtIndex:0];
    scrollView.bounces = NO;

    [self.view addSubview:self.webView];    
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
#if SKIP_DOWNLOAD
    if(!self.isInitialized) {
        //[self performSelector:@selector(initialize) withObject:nil afterDelay:0.5];
        [self initialize];
    } 
#else
    if(self.isDataDownloaded) {
        if(!self.isInitialized) {
            [self performSelector:@selector(initialize) withObject:nil afterDelay:0.5];
        }
    } else {
        NSString *ServerAddress = [[NSUserDefaults standardUserDefaults] stringForKey:SERVER_ADDRESS_KEY];
        if(!ServerAddress || NSOrderedSame == [ServerAddress compare:@""]) {
            if(!self.isInitialized) {
                [self performSelector:@selector(initialize) withObject:nil afterDelay:0.5];
            } 
            return;
        }
        
        NSString *UserID = [[NSUserDefaults standardUserDefaults] stringForKey:USER_ID_KEY];
        if(!UserID || NSOrderedSame == [UserID compare:@""]) {
            if(!self.isInitialized) {
                [self performSelector:@selector(initialize) withObject:nil afterDelay:0.5];
            } 
            return;
        }
        
        DynamicAppSplashViewController *controller = [[DynamicAppSplashViewController alloc] init];
        
        controller.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
        controller.modalPresentationStyle = UIModalPresentationFullScreen;
        controller.delayToHiding = 3.0;
        
        controller.portraitImage = [UIImage imageNamed:@"Default-Portrait.png"];
        [self presentViewController:controller animated:NO completion:nil];
        [controller release];
    }   
#endif
}

- (void)initialize {
	NSString* plistName = @"DynamicAppSettings";
	NSDictionary* plist = [[self class] getBundlePlist:plistName];
	if (plist != nil) {
        NSDictionary *dict = [[NSDictionary alloc] initWithDictionary:plist];

        NSString* key = @"Plugins";
        NSDictionary* plugins = [dict objectForKey:key];
        if (plugins != nil && [plugins count]) {
            self.pluginObjects = [NSMutableDictionary dictionaryWithCapacity:[plugins count]];
 
            NSArray *pluginsArray = [NSArray arrayWithObjects:CAMERA_FUNC,
                                SOUND_FUNC,
                                MOVIE_FUNC,
                                FILE_FUNC,
                                FILE_READER_FUNC,
                                FILE_WRITER_FUNC,
                                ENCRYPT_FUNC,
                                IMAGE_ENCRYPT_FUNC,
                                NOTIFICATION_FUNC,
                                LOADING_SCREEN_FUNC,
                                RESOURCE_CACHE_FUNC,
                                QR_READER_FUNC,
                                DATABASE_FUNC,
                                ADDRESS_BOOK_FUNC,
                                BLUETOOTH_FUNC,
                                BLUETOOTH_4LE_FUNC,
                                APPVERSION_FUNC,
                                CONTACTS_FUNC,
                                PDFVIEWER_FUNC,
                                AD_FUNC,    
                                nil];
            
            for(NSString *plugin in pluginsArray) {
                id obj = [plugins objectForKey:plugin];
#if IS_FREE_VERSION
                if((obj && [obj boolValue]) || [plugin isEqualToString:AD_FUNC]) {
#else
                if(obj && [obj boolValue]) {
#endif
                    id pluginObject = [[NSClassFromString(plugin) alloc] initWithWebView:self.webView withViewController:self];
                    [self.pluginObjects setObject:pluginObject forKey:plugin];
                    [pluginObject release];
                }
            }
        }

        [dict release];
	}

    NSURL *appURL = [NSURL URLWithString:START_PAGE];
    NSString* loadErr = nil;
    
    if(![appURL scheme])
    {
        //        NSString* startFilePath = [DynamicAppPlugin pathForResource:START_PAGE];
        NSString* startFilePath = [[Shortcut wwwPath] stringByAppendingPathComponent:START_PAGE];
        if (startFilePath == nil)
        {
            loadErr = [NSString stringWithFormat:@"ERROR: Start Page at '%@/%@' was not found.", [Shortcut wwwPath], START_PAGE];
            appURL = nil;
        }
        else {
            appURL = [NSURL fileURLWithPath:startFilePath];
        }
    }
    
    if (!loadErr) {
        NSURLRequest *appReq = [NSURLRequest requestWithURL:appURL cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:60.0];
        [self.webView loadRequest:appReq];
    } else {
        NSString* html = [NSString stringWithFormat:@"<html><body> %@ </body></html>", loadErr];
        [self.webView loadHTMLString:html baseURL:nil];
        //        self.loadFromString = YES;
    }
    
    self.isInitialized = YES;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
    self.webView = nil;
    self.pluginObjects = nil;
}


- (void)dealloc {
    self.webView.delegate = nil;
    self.webView = nil;
    self.pluginObjects = nil;
    self.supportedOrientations = nil;

    [super dealloc];
}

-(id) getCommandInstance:(UrlCommand*)urlCommand
{
    // first, we try to find the pluginName in the pluginsMap 
    // (acts as a whitelist as well) if it does not exist, we return nil
    
    id obj = [self.pluginObjects objectForKey:urlCommand.className];
    return obj;
}

- (BOOL) execute:(UrlCommand*)urlCommand
{
    if (urlCommand.className == nil || urlCommand.methodName == nil) {
        return NO;
    }
    
    // Fetch an instance of this class
    id obj = [self getCommandInstance:urlCommand];
    
    if(obj) {
        NSString* fullMethodName = [[NSString alloc] initWithFormat:@"%@:withOptions:", urlCommand.methodName];
        if([obj respondsToSelector:NSSelectorFromString(fullMethodName)]) {
            [obj performSelector:NSSelectorFromString(fullMethodName) withObject:urlCommand.arguments withObject:urlCommand.options];
            [fullMethodName release];
        } else {
            [fullMethodName release];
            return NO;
        }
    } else {
        return NO;
    }
    
    return YES;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

- (BOOL)webView:(UIWebView *)webViewRef shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    
    NSURL *url = [request URL];
    
    if ( [[url scheme] isEqualToString:@"dynamicapp"] ) {
        if(![self execute:[UrlCommand commandFromURL:url]]) {
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        }
        
        return NO;
    } else if ( ![[url scheme] isEqualToString:@"file"] ) {
        [[UIApplication sharedApplication] openURL:url];
        
        return NO;
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    
    return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView {
    
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
    
}


- (void)splashScreenFinish {
    self.isDataDownloaded = YES;
}

- (NSArray*) checkSupportedInterfaceOrientations:(NSArray*)orientations
{
    NSMutableArray* result = [[[NSMutableArray alloc] init] autorelease];
	
    if (orientations != nil) 
    {
        NSEnumerator* enumerator = [orientations objectEnumerator];
        NSString* orientationString;
        
        while (orientationString = [enumerator nextObject]) 
        {
            if ([orientationString isEqualToString:@"UIInterfaceOrientationPortrait"]) {
                [result addObject:[NSNumber numberWithInt:UIInterfaceOrientationPortrait]];
            } else if ([orientationString isEqualToString:@"UIInterfaceOrientationPortraitUpsideDown"]) {
                [result addObject:[NSNumber numberWithInt:UIInterfaceOrientationPortraitUpsideDown]];
            } else if ([orientationString isEqualToString:@"UIInterfaceOrientationLandscapeLeft"]) {
                [result addObject:[NSNumber numberWithInt:UIInterfaceOrientationLandscapeLeft]];
            } else if ([orientationString isEqualToString:@"UIInterfaceOrientationLandscapeRight"]) {
                [result addObject:[NSNumber numberWithInt:UIInterfaceOrientationLandscapeRight]];
            }
        }
    }
    
    // default
    if ([result count] == 0) {
        [result addObject:[NSNumber numberWithInt:UIInterfaceOrientationPortrait]];
    }
    
    return result;
}

+ (NSDictionary*)getBundlePlist:(NSString *)plistName
{
    NSString *errorDesc = nil;
    NSPropertyListFormat format;
    NSString *plistPath = [[NSBundle mainBundle] pathForResource:plistName ofType:@"plist"];
    NSData *plistXML = [[NSFileManager defaultManager] contentsAtPath:plistPath];
    NSDictionary *temp = (NSDictionary *)[NSPropertyListSerialization
                                          propertyListFromData:plistXML
                                          mutabilityOption:NSPropertyListMutableContainersAndLeaves
                                          format:&format errorDescription:&errorDesc];
    return temp;
}

@end
