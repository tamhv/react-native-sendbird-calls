import React from 'react'
import PropTypes from 'prop-types'
import { requireNativeComponent } from 'react-native'
const RNVSendBirdCallsVideo = requireNativeComponent('RNVSendBirdCallsVideo')
class SendBirdCallsVideo extends React.Component {
  render () {
    return <RNVSendBirdCallsVideo {...this.props} />
  }
}

RNVSendBirdCallsVideo.propTypes = {
  callId: PropTypes.string.isRequired,
  local: PropTypes.bool
};

export default SendBirdCallsVideo
