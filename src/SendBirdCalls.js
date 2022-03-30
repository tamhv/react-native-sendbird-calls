import { NativeModules, NativeEventEmitter, DeviceEventEmitter, Platform } from 'react-native'

const { RNSendBirdCalls } = NativeModules
const isIOS = Platform.OS === 'ios'

class SendBirdCalls {
  constructor () {
    this._eventHandlers = new Map()
    this._eventEmitter = new NativeEventEmitter(RNSendBirdCalls)
  }

  setup = async (appId) => {
      return await RNSendBirdCalls.configure(appId)
  }

  authenticate = async (userId) => {
      return await RNSendBirdCalls.authenticate(userId, '')
  }

  setupVoIP = async () => {
    if (isIOS){
      return await RNSendBirdCalls.voipRegistration()
    }
  }

  registerPushToken = async (token) =>{
    return await RNSendBirdCalls.registerPushToken(token)
  }

  dial = async (callee, isVideoCall) => {
    return await RNSendBirdCalls.dial(callee, isVideoCall)
  }

  endCall = async (callId) => {
    return await RNSendBirdCalls.endCall(callId)
  }

  acceptCall = async (callId) => {
    return await RNSendBirdCalls.acceptCall(callId)
  }

  addEventListener = (event, handler) => {
    const subscription = this._eventEmitter.addListener(event, handler)
    this._eventHandlers.set(event, subscription)
  }

  removeAllEventListeners = () => {
    this._eventHandlers.forEach((value, key, map) => {
      value.remove()
    })
    this._eventHandlers.clear()
  }

}

export default new SendBirdCalls()
