//
//  Bluetooth.h
//  DynamicApp
//
//  Created by ZYYX Inc on 8/22/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"
#import "CoreBluetooth/CBCentralManager.h"

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
#import <MultipeerConnectivity/MultipeerConnectivity.h>
#import "UUIDHelper.h"
#else
#import "GameKit/GameKit.h"
#endif

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
@interface Peer : NSObject {
    MCPeerID *peerId;
    MCSessionState state;
}
@property (nonatomic, retain) MCPeerID *peerId;
@property (nonatomic) MCSessionState state;
@end
#endif

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
@interface Bluetooth : DynamicAppPlugin <MCSessionDelegate, MCNearbyServiceAdvertiserDelegate, MCNearbyServiceBrowserDelegate> {
    MCSession *toClientSession;
    MCSession *toServerSession;
    MCNearbyServiceAdvertiser *nearbyServiceAdvertiser;
    MCNearbyServiceBrowser *nearbyServiceBrowser;
    MCPeerID                *mypeerID;
    NSMutableDictionary     *peerList;
    BOOL                    waitingDisconnect;
}
#else
@interface Bluetooth : DynamicAppPlugin <GKSessionDelegate> {
    GKSession *session;
    NSMutableArray *peerList;
}
#endif

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
@property (nonatomic, retain) MCSession *toClientSession;
@property (nonatomic, retain) MCSession *toServerSession;
@property (nonatomic, retain) MCNearbyServiceAdvertiser *nearbyServiceAdvertiser;
@property (nonatomic, retain) MCNearbyServiceBrowser *nearbyServiceBrowser;
@property (nonatomic, retain) MCPeerID *mypeerID;
@property (nonatomic, retain) NSMutableDictionary *peerList;
@property (nonatomic) BOOL waitingDisconnect;
#else
@property (nonatomic, retain) GKSession *session;NSMutableDictionary
@property (nonatomic, retain) NSMutableArray *peerList;
#endif

- (void) discover:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) connect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) disconnect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) send:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;

@end
