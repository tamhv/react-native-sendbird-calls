// main index.js

import { NativeModules } from 'react-native';
import CSendBirdCalls from './CSendBirdCalls'
const { RNSendBirdCalls } = NativeModules;
import SendBirdCalls from './SendBirdCalls'

export {
  RNSendBirdCalls,
  CSendBirdCalls,
  SendBirdCalls
}
