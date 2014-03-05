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

#import "Bluetooth.h"
#import "PluginResult.h"

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
#define SERVICE_TYPE        @"DABLT"
#endif

@implementation Bluetooth


#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
@synthesize mypeerID, toServerSession, toClientSession;
@synthesize nearbyServiceAdvertiser, nearbyServiceBrowser, waitingDisconnect;
#else
@synthesize session;
#endif
@synthesize peerList;

- (void)discover:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    if(self.mypeerID == nil) {
        self.peerList =  [NSMutableDictionary dictionary];

        self.mypeerID = [[[MCPeerID alloc] initWithDisplayName:[UIDevice currentDevice].name] autorelease];
        
        self.toServerSession = [[[MCSession alloc] initWithPeer:self.mypeerID] autorelease];
        self.toServerSession.delegate = self;
        
        self.toClientSession = [[[MCSession alloc] initWithPeer:self.mypeerID] autorelease];
        self.toClientSession.delegate = self;
        
        self.nearbyServiceAdvertiser = [[[MCNearbyServiceAdvertiser alloc] initWithPeer:self.mypeerID discoveryInfo:nil
                                                                            serviceType:SERVICE_TYPE] autorelease];
        
        self.nearbyServiceAdvertiser.delegate = self;
        
        self.nearbyServiceBrowser = [[[MCNearbyServiceBrowser alloc] initWithPeer:self.mypeerID serviceType:SERVICE_TYPE] autorelease];
        self.nearbyServiceBrowser.delegate = self;
    }
#else
    if(self.session == nil) {
        self.peerList = [[[NSMutableArray alloc] init] autorelease];

        self.session = [[[GKSession alloc] initWithSessionID:@"DynamicApp_Bluetooth" displayName:[UIDevice currentDevice].name sessionMode:GKSessionModePeer] autorelease];
        self.session.delegate = self;
        [self.session setDataReceiveHandler:self withContext:nil];
        self.session.available = YES;
    }
#endif
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    [self.nearbyServiceAdvertiser stopAdvertisingPeer];
    [self.nearbyServiceAdvertiser startAdvertisingPeer];
    [self.nearbyServiceBrowser stopBrowsingForPeers];
    [self.nearbyServiceBrowser startBrowsingForPeers];
#endif
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (void) connect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    
    NSDictionary *peerData = [arguments objectForKey:@"peerData"];
    NSString *peerId = [peerData objectForKey:@"id"];
    NSTimeInterval timeout = 0;
    if([arguments objectForKey:@"timeout"]) {
        timeout = [[arguments objectForKey:@"timeout"] doubleValue];
    }

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    Peer *peer = [self peerbyUUID:peerId];
    if(peer) {
        if([self.toServerSession.connectedPeers count] > 0) {
            [self.toServerSession disconnect];
            self.waitingDisconnect = YES;
        }
        [self.nearbyServiceBrowser invitePeer:peer.peerId toSession:self.toServerSession withContext:nil timeout:timeout];
        peer.state = MCSessionStateConnecting;
    } else {
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
    }
#else
    [self.session connectToPeer:peerId withTimeout:timeout];
#endif
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (void) disconnect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    
    NSDictionary *peerData = [arguments objectForKey:@"peerData"];
    NSString *peerId = [peerData objectForKey:@"id"];
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    Peer *peer = [self peerbyUUID:peerId];
    
    if(peer)
    {
        BOOL disconnectMyself = false;
        for(MCPeerID *connectedPeer in self.toServerSession.connectedPeers)
        {
            if([connectedPeer.displayName isEqualToString:peer.peerId.displayName]) {
                disconnectMyself = true;
                break;
            }
        }

        if(disconnectMyself) {
            [self.toServerSession disconnect];
        } else {
            NSData *disconnectMsg = [[NSString stringWithFormat:@"%@_disconnect_%@",
                                  self.toClientSession.myPeerID.displayName, peer.peerId.displayName]
                             dataUsingEncoding:NSUTF8StringEncoding];
        
            if(![self.toClientSession sendData:disconnectMsg toPeers:[NSArray arrayWithObject:peer.peerId] withMode:MCSessionSendDataReliable error:nil]) {
                PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
            }
        }
    } else {
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
    }
#else
    NSData *disconnectMsg = [[NSString stringWithFormat:@"%@_disconnect_%@", self.session.peerID, peerId] dataUsingEncoding:NSUTF8StringEncoding];
    if(![self.session sendData:disconnectMsg toPeers:[NSArray arrayWithObject:peerId] withDataMode:GKSendDataReliable error:nil]) {
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
    }
#endif
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (void) send:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    PluginResult *result = nil;
    NSString *jsString = nil;
    
    NSDictionary *peerData = [arguments objectForKey:@"peerData"];
    NSString *peerId = [peerData objectForKey:@"id"];
    NSString *sendData = [arguments objectForKey:@"sendData"];
    NSData *data = [sendData dataUsingEncoding:NSUTF8StringEncoding];
    
    NSError *error = nil;
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    Peer *peer = [self peerbyUUID:peerId];

    if(peer)
    {
        BOOL toServer = NO;
        for(MCPeerID *peerConnected in self.toServerSession.connectedPeers)
        {
            if([peerConnected.displayName isEqualToString:peer.peerId.displayName]) {
                toServer = YES;
                break;
            }
        }

        MCSession *session = nil;
        if(toServer) {
            session = self.toServerSession;
        } else {
            session = self.toClientSession;
        }
    
        if([session sendData:data toPeers:[NSArray arrayWithObject:peer.peerId] withMode:MCSessionSendDataReliable error:&error]) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            jsString = [result toSuccessCallbackString:self.callbackId];
        } else {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            jsString = [result toErrorCallbackString:self.callbackId];
        }
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [result toErrorCallbackString:self.callbackId];
    }
#else
    if([self.session sendData:data toPeers:[NSArray arrayWithObject:peerId] withDataMode:GKSendDataReliable error:&error]) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        jsString = [result toSuccessCallbackString:self.callbackId];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [result toErrorCallbackString:self.callbackId];
    }
#endif
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
#pragma mark - MCSessionDelegate
- (void)session:(MCSession *)session peer:(MCPeerID *)_peerID didChangeState:(MCSessionState)state
{
    PluginResult *result = nil;
    NSString *jsString = nil;
    
    NSString *uuid = [self UUIDForPeer:_peerID];

    if(uuid == nil) {
        return;
    }

    Peer *peer = [self.peerList objectForKey:uuid];
	switch (state) {
		case MCSessionStateConnected:
            if(![self isConnected:_peerID]) {
                return;
            }

            jsString = [NSString stringWithFormat:@"Bluetooth.onStateChanged('%@', '%@', Bluetooth.STATE_CONNECTED);",
                                  _peerID.displayName, uuid];
            [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:)
                                           withObject:jsString
                                        waitUntilDone:NO];
            
            jsString = @"DynamicApp.processing = false;";
            [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:)
                                           withObject:jsString waitUntilDone:NO];
            if(self.callbackId)
            {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
                jsString = [result toSuccessCallbackString:self.callbackId];
                [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:)
                                               withObject:jsString waitUntilDone:NO];
                
                self.callbackId = nil;
            }

            peer.state = MCSessionStateConnected;

			break;
		case MCSessionStateNotConnected:
            jsString = [NSString stringWithFormat:@"Bluetooth.onStateChanged('%@', '%@', Bluetooth.STATE_DISCONNECTED);",
                        _peerID.displayName, uuid];
            [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];
            NSLog(@"jsString : %@", jsString);
        
            jsString = @"DynamicApp.processing = false;";
            [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];
        
            if(self.callbackId) {
                if(!self.waitingDisconnect) {
                    result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
                    jsString = [result toSuccessCallbackString:self.callbackId];
                    [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];
                    self.callbackId = nil;
                } else {
                    self.waitingDisconnect = NO;
                }
            }
            
            [self.peerList removeObjectForKey:uuid];
			break;
        case MCSessionStateConnecting:
            jsString = [NSString stringWithFormat:@"Bluetooth.onStateChanged('%@', '%@', Bluetooth.STATE_CONNECTING);",
                    _peerID.displayName, uuid];
            [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];
            
            peer.state = MCSessionStateConnecting;
            break;
		default:
			break;
	}
}

// Received data from remote peer
- (void)session:(MCSession *)session didReceiveData:(NSData *)data fromPeer:(MCPeerID *)_peerID
{
    NSString *receiveData = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    Peer* peer = [self peerbyUUID:[self UUIDForPeer:_peerID]];
    if(!peer)
    {
        return;
    }

    if([receiveData isEqualToString:[NSString stringWithFormat:@"%@_disconnect_%@", _peerID.displayName,
                                     self.toServerSession.myPeerID.displayName]]) {
        [self.toServerSession disconnect];
    } else {
        NSString *uuid = [self UUIDForPeer:_peerID];
        NSString *jsString = [NSString stringWithFormat:@"Bluetooth.onRecvCallback({id:'%@', deviceName:'%@', state:Bluetooth.STATE_CONNECTED}, '%@')",
                              uuid,
                              _peerID.displayName,
                              [[receiveData componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:@"\\n"]];
        [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];
    }
    
    
    [receiveData release];
    
}

    
// Received a byte stream from remote peer
- (void)session:(MCSession *)session didReceiveStream:(NSInputStream *)stream withName:(NSString *)streamName fromPeer:(MCPeerID *)peerID
{
        
}
    
// Start receiving a resource from remote peer
- (void)session:(MCSession *)session didStartReceivingResourceWithName:(NSString *)resourceName fromPeer:(MCPeerID *)peerID
                            withProgress:(NSProgress *)progress
{
        
}
    
// Finished receiving a resource from remote peer and saved the content in a temporary location - the app is responsible for moving the file to a permanent location within its sandbox
- (void)session:(MCSession *)session didFinishReceivingResourceWithName:(NSString *)resourceName fromPeer:(MCPeerID *)peerID
                            atURL:(NSURL *)localURL withError:(NSError *)error
{
        
}

// Made first contact with peer and have identity information about the remote peer (certificate may be nil)
- (void)session:(MCSession *)session didReceiveCertificate:(NSArray *)certificate fromPeer:(MCPeerID *)peerID certificateHandler:(void(^)(BOOL accept))certificateHandler
{
    if (certificateHandler != nil) {
        certificateHandler(YES);
    }
}

#pragma mark - MCNearbyServiceAdvertiserDelegate
- (void)advertiser:(MCNearbyServiceAdvertiser *)advertiser didReceiveInvitationFromPeer:(MCPeerID *)_peerID withContext:(NSData *)context invitationHandler:(void(^)(BOOL accept, MCSession *session))invitationHandler
{
    invitationHandler(YES, self.toClientSession);
}
    
- (void)advertiser:(MCNearbyServiceAdvertiser *)advertiser didNotStartAdvertisingPeer:(NSError *)error
{
    
}

#pragma mark - MCNearbyServiceBrowserDelegate
- (void)browser:(MCNearbyServiceBrowser *)browser foundPeer:(MCPeerID *)_peerID withDiscoveryInfo:(NSDictionary *)info
{
    NSString *uuid = [self UUIDForPeer:_peerID];
    
    if(uuid != nil) {
        return;
    }
    
    uuid = [UUIDHelper generateUUID];
    
    Peer *peer = [[Peer alloc] init];
    peer.peerId = _peerID;

    [self.peerList setObject:peer forKey:uuid];
    
    NSString *jsString;
    if([self isConnected:_peerID]) {
        jsString = [NSString stringWithFormat:@"Bluetooth.onStateChanged('%@', '%@', Bluetooth.STATE_CONNECTED);",
                    _peerID.displayName, uuid];
    } else {
        jsString = [NSString stringWithFormat:@"Bluetooth.onStateChanged('%@', '%@', Bluetooth.STATE_AVAILABLE);",
                    _peerID.displayName, uuid];
    }
    [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];
 
    [peer release];
}

// A nearby peer has stopped advertising
- (void)browser:(MCNearbyServiceBrowser *)browser lostPeer:(MCPeerID *)_peerID
{
    NSString *uuid = [self UUIDForPeer:_peerID];

    if(uuid == nil) {
        return;
    }

    NSString *jsString = [NSString stringWithFormat:@"Bluetooth.onStateChanged('%@', '%@', Bluetooth.STATE_DISCONNECTED);",
                          _peerID.displayName, uuid];
    [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];

    [self.peerList removeObjectForKey:uuid];
    
    jsString = @"DynamicApp.processing = false;";
    [self.webView performSelectorOnMainThread:@selector(stringByEvaluatingJavaScriptFromString:) withObject:jsString waitUntilDone:NO];

}

// Browsing did not start due to an error
- (void)browser:(MCNearbyServiceBrowser *)browser didNotStartBrowsingForPeers:(NSError *)error
{
    
}



#else
- (void)session:(GKSession *)_session didReceiveConnectionRequestFromPeer:(NSString *)peerID {
    [_session acceptConnectionFromPeer:peerID error:nil];
}

- (void)session:(GKSession *)_session peer:(NSString *)peerId didChangeState:(GKPeerConnectionState)state {
    PluginResult *result = nil;
    NSString *jsString = [NSString stringWithFormat:@"Bluetooth.onStateChanged('%@', '%@', %d);", [_session displayNameForPeer:peerId], peerId, state];
    [self.webView stringByEvaluatingJavaScriptFromString: jsString];
    
	switch (state) { 
		case GKPeerStateAvailable:
			if (![self.peerList containsObject:peerId]) {
				[self.peerList addObject:peerId];
			}
            
			break;
		case GKPeerStateUnavailable:
            [self.peerList removeObject:peerId];
            
			break;
		case GKPeerStateConnected:
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
            
			break;				
		case GKPeerStateDisconnected:
            [self.peerList removeObject:peerId];
            
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
            if(self.callbackId) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
                [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
            }
            
			break;
        case GKPeerStateConnecting:
            break;
		default:
			break;
	}
}

- (void)session:(GKSession *)session connectionWithPeerFailed:(NSString *)peerID withError:(NSError *)error {
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
}

- (void)session:(GKSession *)_session didFailWithError:(NSError *)error {
    [_session disconnectFromAllPeers];
    self.session = nil;
    self.peerList = nil;
}

- (void)receiveData:(NSData *)data fromPeer:(NSString *)peerId inSession:(GKSession *)_session context:(void *)context {
    
    NSString *receiveData = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    
    if([receiveData isEqualToString:[NSString stringWithFormat:@"%@_disconnect_%@", peerId, _session.peerID]]) {
        [_session disconnectFromAllPeers];
    } else {
        NSString *jsString = [NSString stringWithFormat:@"Bluetooth.onRecvCallback({id:%@, deviceName:'%@', state:%d}, '%@')", peerId, [_session displayNameForPeer:peerId], GKPeerStateConnected, [[receiveData componentsSeparatedByCharactersInSet:[NSCharacterSet newlineCharacterSet]] componentsJoinedByString:@"\\n"]];
        [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    }

    [receiveData release];
}
#endif


- (void)dealloc {
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    [self.nearbyServiceAdvertiser stopAdvertisingPeer];
    self.nearbyServiceAdvertiser.delegate = nil;
    self.nearbyServiceAdvertiser = nil;
    
    [self.nearbyServiceBrowser stopBrowsingForPeers];
    self.nearbyServiceBrowser.delegate = nil;
    self.nearbyServiceBrowser = nil;

    self.toServerSession.delegate = nil;
    self.toServerSession = nil;
    
    self.toClientSession.delegate = nil;
    self.toClientSession = nil;
    self.mypeerID = nil;
#else
    self.session.delegate = nil;
    self.session = nil;
#endif
    self.peerList = nil;
    [super dealloc];
}

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
- (NSString*)UUIDForPeer:(MCPeerID *)peerId
{
    NSArray *uuids = [self.peerList allKeys];
    NSString *uuid = nil;
    
    for(NSInteger i=0; i < [uuids count]; i++) {
        Peer *peer = [self.peerList valueForKey:uuids[i]];
        if([peer.peerId.displayName isEqualToString:peerId.displayName]) {
            uuid = uuids[i];
            break;
        }
    }
    
    return uuid;
}
   
- (Peer*)peerbyUUID:(NSString *)uuid
{
    return [self.peerList valueForKey:uuid];
}

- (BOOL)isConnected:(MCPeerID *)_peerId
{
    for(MCPeerID *peer in self.toServerSession.connectedPeers)
    {
        if([peer.displayName isEqualToString:_peerId.displayName]){
            return YES;
        }
    }
    
    for(MCPeerID *peer in self.toClientSession.connectedPeers)
    {
        if([peer.displayName isEqualToString:_peerId.displayName]){
            return YES;
        }
    }
    
    return NO;
}
#endif
@end

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
@implementation Peer
@synthesize peerId, state;

-(id)init {
    self = [super init];
    if(nil != self) {
        self.state = MCSessionStateNotConnected;
    }
        
    return self;
}

- (void) dealloc {
    self.peerId = nil;
    [super dealloc];
}
@end
#endif
