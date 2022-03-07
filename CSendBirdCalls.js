import {Component} from 'react';
import { NativeModules } from 'react-native';
const { RNSendBirdCalls } = NativeModules;

class CSendBirdCalls extends Component {

  constructor(props) {
    super(props);
    console.log('AA')
  }
  componentDidMount() {}
  componentWillUnmount() {}

  dial = async ()=>{
    RNSendBirdCalls.dial()
  }

  render(){
    return null
  }
}

export default CSendBirdCalls
