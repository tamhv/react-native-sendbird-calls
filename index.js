import { NativeModules } from 'react-native'

const { RNSendBirdCalls } = NativeModules
import SendBirdCalls from './src/SendBirdCalls'
import SendBirdCallsVideo from './src/SendBirdCallsVideo'

export {
  RNSendBirdCalls,
  SendBirdCalls,
  SendBirdCallsVideo
}
