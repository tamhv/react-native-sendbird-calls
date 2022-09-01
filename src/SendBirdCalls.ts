import { ICallResponse, IEventTypes, ISendbirdCalls, IUserResponse } from './SendbirdCalls.d';
import { NativeModules, NativeEventEmitter, DeviceEventEmitter, Platform } from 'react-native'

const { RNSendBirdCalls } = NativeModules

const isIOS = Platform.OS === 'ios'
const isAndroid = Platform.OS === 'android'

export type SendbirdCallsProps = ISendbirdCalls;
export type CallResponse = ICallResponse;

class SendBirdCalls implements ISendbirdCalls {
  private _eventHandlers: Map<any, any>;
  private _eventEmitter: NativeEventEmitter;

  soundType = {
    DIALING: 'DIALING',
    RINGING:'RINGING',
    RECONNECTING:'RECONNECTING',
    RECONNECTED:'RECONNECTED',
  }

  eventType = {
    RINGING: 'SendBirdCallRinging',
    ACCEPTED: 'DirectCallDidAccept',
    CONNECTED: 'DirectCallDidConnect',
    ENDED: 'DirectCallDidEnd',
    REMOTE_AUDIO_CHANGED: "DirectCallRemoteAudioSettingsChanged",
    REMOTE_VIDEO_CHANGED: "DirectCallVideoSettingsChanged",
  };

  constructor () {
    this._eventHandlers = new Map()
    this._eventEmitter = new NativeEventEmitter(RNSendBirdCalls)
  }

  setup = async (appId: string): Promise<boolean> => {
      return await RNSendBirdCalls.configure(appId)
  }

  authenticate = async (userId: string, accessToken: string): Promise<IUserResponse> => {
      return await RNSendBirdCalls.authenticate(userId, accessToken)
  }

  setupVoIP = async (): Promise<void> => {
    if (isIOS){
      return await RNSendBirdCalls.voipRegistration()
    }
  }

  registerPushToken = async (token: string): Promise<boolean> =>{
    return await RNSendBirdCalls.registerPushToken(token)
  }

  unregisterPushToken = async (token: string): Promise<boolean> => {
    if (isIOS) {
      return false;
    }
    return await RNSendBirdCalls.unregisterPushToken(token)
  }

  unregisterAllPushTokens = async (): Promise<boolean> => {
    if (isIOS) {
      return false;
    }
    return await RNSendBirdCalls.unregisterAllPushTokens()
  }

  dial = async (callee: string, isVideoCall: boolean): Promise<ICallResponse> => {
    return await RNSendBirdCalls.dial(callee, isVideoCall)
  }

  endCall = async (callId: string): Promise<ICallResponse> => {
    return await RNSendBirdCalls.endCall(callId)
  }

  acceptCall = async (callId: string): Promise<boolean> => {
    return await RNSendBirdCalls.acceptCall(callId)
  }

  addDirectCallSound = async (soundType: string, filename: string): Promise<boolean> => {
      return await RNSendBirdCalls.addDirectCallSound(soundType,filename)
  }

  removeDirectCallSound = async (soundType: string): Promise<boolean> => {
    if(isAndroid) {
      return await RNSendBirdCalls.removeDirectCallSound(soundType)
    }
    return false;
  }

  setDirectCallDialingSoundOnWhenSilentOrVibrateMode = async (b:boolean): Promise<boolean> => {
    if(isAndroid) {
      return await RNSendBirdCalls.setDirectCallDialingSoundOnWhenSilentOrVibrateMode(b)
    }else if(isIOS){
      return await RNSendBirdCalls.setDirectCallDialingSoundOnWhenSilentMode(b)
    }
    return false;
  }

  addEventListener = (event: string, handler: (callData: ICallResponse) => void): void => {
    if (Platform.OS === "ios") {
      if (event === this.eventType.REMOTE_AUDIO_CHANGED
         || event === this.eventType.REMOTE_VIDEO_CHANGED
         || event === this.eventType.RINGING
         || event === this.eventType.ACCEPTED) {
        return;
      }
    }
    const subscription = this._eventEmitter.addListener(event, handler)
    this._eventHandlers.set(event, subscription)
  }

  removeAllEventListeners = (): void => {
    this._eventHandlers.forEach((value, key, map) => {
      value.remove()
    })
    this._eventHandlers.clear()
  }

  setCallConnectionTimeout(second: number): void {
    if (isAndroid) {
      RNSendBirdCalls.setCallConnectionTimeout(second);
    }
  }

  setRingingTimeout(second: number): void {
    if (isAndroid) {
      RNSendBirdCalls.setRingingTimeout(second);
    }
  }

  handleFirebaseMessageData = async (data: any): Promise<boolean> => {
    return await RNSendBirdCalls.handleFirebaseMessageData(data);
  }

  switchCamera = async (callId: string): Promise<boolean> => {
    return await RNSendBirdCalls.switchCamera(callId);
  }

  stopVideo = async (callId: string): Promise<boolean> => {
    return await RNSendBirdCalls.stopVideo(callId);
  }

  startVideo = async (callId: string): Promise<boolean> => {
    return await RNSendBirdCalls.startVideo(callId);
  }

  muteMicrophone = async (callId: string): Promise<boolean> => {
    return await RNSendBirdCalls.muteMicrophone(callId);
  }

  unmuteMicrophone = async (callId: string): Promise<boolean> => {
    return await RNSendBirdCalls.unmuteMicrophone(callId);
  }

}

export default new SendBirdCalls() as ISendbirdCalls
