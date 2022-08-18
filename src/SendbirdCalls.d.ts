export interface SendBirdError {
    message: string;
    code: string;
}

export interface ISendbirdCalls {
    soundType: ISoundTypes;
    eventType: IEventTypes;
    /**
     * @param appId
     * @throws {SendBirdError}
     */
     setup(appId: string): Promise<boolean>;

     /**
      *
      * @param userId
      * @param accessToken
      * @throws {SendBirdError}
      */
     authenticate(userId: string, accessToken: string): Promise<IUserResponse>;
 
     /**
      *
      * @param token
      * @throws {SendBirdError}
      */
     registerPushToken(token: string): Promise<boolean>;
 
     /**
      * Note: Android only
      * @param token
      * @throws {SendBirdError}
      */
     unregisterPushToken(token: string): Promise<boolean>;
 
     /**
      * Note: Android only
      * @throws {SendBirdError}
      */
     unregisterAllPushTokens(): Promise<boolean>;
 
     /**
      *
      * @param callee
      * @param isVideoCall
      * @throws {SendBirdError}
      */
     dial(callee: string, isVideoCall: boolean): Promise<ICallResponse>;
 
     /**
      *
      * @param callId
      * @throws {SendBirdError}
      */
     endCall(callId: string): Promise<ICallResponse>;
 
     /**
      *
      * @param callId
      * @throws {SendBirdError}
      */
     acceptCall(callId: string): Promise<boolean>;
 
     /**
      *
      * @param eventName
      */
     addEventListener(eventName: string, handler: (callData: ICallResponse) => ICallResponse): void;
 
     removeAllEventListeners(): void;

     /**
      * Note: Android only
      * @param second 
      */
     setCallConnectionTimeout(second:number): void;

     /**
      * Note: Android only
      * @param second 
      */
     setRingingTimeout(second:number): void;
 
     /**
      *
      * @param soundType
      * @param fileName
      * @throws {SendBirdError}
      */
     addDirectCallSound(soundType: string, fileName: string): Promise<boolean>;
 
     /**
      * Note: Android only
      * @param soundType
      * @throws {SendBirdError}
      */
     removeDirectCallSound(soundType: string): Promise<boolean>;

     /**
      * Note: iOS only
      */
     setupVoIP(): Promise<void>;
}

export interface ICallResponse {
    callId: string;
    callee: string;
    caller: string;
    duration?: number;
}

export interface ISoundTypes {
    DIALING: string;
    RINGING: string;
    RECONNECTING: string;
    RECONNECTED: string;
}

export interface IEventTypes {
    RINGING: string;
    ACCEPTED: string;
    CONNECTED: string;
    ENDED: string;
}

export interface IUserResponse {
    userId: string;
    nickname: string;
}