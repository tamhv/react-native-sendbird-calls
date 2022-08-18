import React from 'react'
import PropTypes from 'prop-types'
import { requireNativeComponent } from 'react-native'
const RNVSendBirdCallsVideo = requireNativeComponent('RNVSendBirdCallsVideo')
const isAndroid = Platform.OS === 'android'

class SendBirdCallsVideo extends React.Component {
  render () {
    if(isAndroid){
      return <RNVSendBirdCallsVideo {...this.props} call={{callId:this.props.callId, local:this.props.local}} />
    }
    return <RNVSendBirdCallsVideo {...this.props} />
  }
}

RNVSendBirdCallsVideo.propTypes = {
  callId: PropTypes.string.isRequired,
  local: PropTypes.bool
};

export default SendBirdCallsVideo
