import React from 'react'
import { requireNativeComponent } from 'react-native'
const RNVSendBirdCallsVideo = requireNativeComponent('RNVSendBirdCallsVideo')
const isAndroid = Platform.OS === 'android'

export interface SendBirdCallsVideoProps {
  callId: string;
  local?: boolean
}

class SendBirdCallsVideo extends React.Component<SendBirdCallsVideoProps> {
  render () {
    if(isAndroid){
      return <RNVSendBirdCallsVideo {...this.props} call={{callId:this.props.callId, local:this.props.local}} />
    }
    return <RNVSendBirdCallsVideo {...this.props} />
  }
}

export default SendBirdCallsVideo
