// main index.js

import { NativeModules } from 'react-native';
const { RNSendBirdCalls } = NativeModules;
import SendBirdCalls from './SendBirdCalls'

export {
  RNSendBirdCalls,
  SendBirdCalls
}
