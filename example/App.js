/**
 * Sample React Native App
 *
 * adapted from App.js generated by the following command:
 *
 * react-native init example
 *
 * https://github.com/facebook/react-native
 */

import React, { Component } from 'react'
import { Platform,Button, StyleSheet, Text, View } from 'react-native'

import RNSendBirdCalls from 'react-native-sendbird-calls'

export default class App extends Component<{}> {

  async componentDidMount () {

    try{
      let r = await RNSendBirdCalls.configure('10A8BD3E-3A52-4BC4-B3A7-EA3C2375D599')
      console.log('configured app id', r)

      // RNSendBirdCalls.addDelegate('AppDelegate')

      r = await RNSendBirdCalls.authenticate('1111', '')
      console.log('user authenticated', r)
    }catch (e) {
      console.log('ERROR',e)
    }

    RNSendBirdCalls.voipRegistration();

  }

  call = async () =>{
    RNSendBirdCalls.dial();
  }

  render () {
    return (
      <View style={styles.container}>
        <Text>saaas</Text>

        <Button title={'Call 123'} onPress={this.call}/>
      </View>
    )
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
})
