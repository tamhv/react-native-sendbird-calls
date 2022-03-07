import { NativeModules, NativeEventEmitter, DeviceEventEmitter, Platform } from 'react-native'

const { RNSendBirdCalls } = NativeModules
const isIOS = Platform.OS === 'ios'

class SendBirdCalls {
  constructor () {
    this._eventHandlers = new Map()
    this._eventEmitter = new NativeEventEmitter(RNSendBirdCalls)
  }

  setup = async (appId) => {
    try {
      return await RNSendBirdCalls.configure(appId)
    } catch (e) {
      console.log('Error setup', e)
    }
  }

  authenticate = async (userId) => {
    try {
      return await RNSendBirdCalls.authenticate(userId, '')
    } catch (e) {
      console.log('Error authenticate', e)
    }
  }

  setupVoIP = async () => {
    RNSendBirdCalls.voipRegistration()
  }

  dial = async (callee, isVideoCall) => {
    return await RNSendBirdCalls.dial(callee, isVideoCall)
  }

  endCall = async (callId) => {
    return await RNSendBirdCalls.endCall(callId)
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
