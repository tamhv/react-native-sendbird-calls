/**
 * Sample React Native App
 *
 * adapted from App.js generated by the following command:
 *
 * react-native init example
 *
 * https://github.com/facebook/react-native
 */

import React, {Component} from 'react';
import {
  Platform,
  Button,
  StyleSheet,
  Text,
  View,
  TextInput,
  Alert,
} from 'react-native';

import {SendBirdCalls, SendBirdCallsVideo} from 'react-native-sendbird-calls';
import AsyncStorage from '@react-native-async-storage/async-storage';
const APP_ID = '420C9A17-445C-4151-89C8-CFF29D8E403F';
export default class App extends Component<{}> {
  state = {
    loading: true,
  };

  setupSendBirdApp = async () => {
    try {
      await SendBirdCalls.setup(APP_ID);
      console.log('SendBirdCalls.setup');
      await SendBirdCalls.addDirectCallSound(
        SendBirdCalls.soundType.DIALING,
        'dialing',
      );
      await SendBirdCalls.addDirectCallSound(
        SendBirdCalls.soundType.RINGING,
        'ringing',
      );
    } catch (e) {
      console.log(e.code, e.message);
    }
  };

  getUserIdIfAny = async () => {
    const caller = await AsyncStorage.getItem('@caller');
    if (caller) {
      this.setState({caller: caller});

      await this.signIn(caller);
    }
    this.setState({loading: false});
  };

  async componentDidMount() {
    await this.setupSendBirdApp();

    SendBirdCalls.addEventListener(
      'DirectCallDidConnect',
      this.onDirectCallDidConnect,
    );
    SendBirdCalls.addEventListener('DirectCallDidEnd', this.onDirectCallDidEnd);

    await this.getUserIdIfAny();

  }

  componentWillUnmount() {
    SendBirdCalls.removeAllEventListeners();
  }

  onDirectCallDidConnect = data => {
    console.log('onDirectCallDidConnect', data);
    const {callId, isVideoCall} = data;
    this.setState({connected: true, calling: true, callId, isVideoCall});
  };

  onDirectCallDidEnd = data => {
    console.log('onDirectCallDidEnd', data);
    this.setState({calling: false, callId: null, connected: false});
  };

  call = async (callee, isVideoCall) => {
    if (!callee) {
      return false;
    }
    try {
      this.setState({loading: true});
      const data = await SendBirdCalls.dial(callee, isVideoCall);
      const {callId} = data;
      console.log('dial', data);
      this.setState({calling: true, isVideoCall, callId});
      this.setState({loading: false});
    } catch (e) {
      this.setState({loading: false});

      Alert.alert('Error', e.message);
      console.log(e.code, e.message);
    }
  };

  endCall = async () => {
    const {callId} = this.state;
    //6
    const data = await SendBirdCalls.endCall(callId);
    console.log('endcall', data);
    this.setState({calling: false, callId: null, connected: false});
  };

  signIn = async userId => {
    try {
      this.setState({loading: true});
      const result = await SendBirdCalls.authenticate(userId);
      console.log(result);
      if (result.userId) {
        await AsyncStorage.setItem('@caller', result.userId);
      }
      await SendBirdCalls.setupVoIP();
      this.setState({authenticated: true, caller: result});
      this.setState({loading: false});
      console.log('SendBirdCalls.authenticate', result);
    } catch (e) {
      this.setState({loading: false});

      Alert.alert('Error', e.message);
      console.log(`Error ${e.code}: ${e.message}`);
    }
  };

  logout = async () => {
    await AsyncStorage.removeItem('@caller');
    this.setState({caller: null, authenticated: false});
  };
  render() {
    const {
      calling,
      connected,
      callId,
      isVideoCall,
      authenticated,
      caller,
      loading,
    } = this.state;

    if (loading) {
      return (
        <View style={styles.container}>
          <Text style={styles.welcome}>Loading...</Text>
        </View>
      );
    }
    if (!authenticated) {
      return <NotAuthenticated onSubmitUserId={this.signIn} />;
    }

    return (
      <View style={styles.container}>
        {calling ? (
          <View style={styles.container}>
            {connected ? (
              <View>
                {callId && isVideoCall ? (
                  <SendBirdCallsVideo
                    callId={callId}
                    local={false}
                    style={{
                      flex: 1,
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}>
                    <SendBirdCallsVideo
                      callId={callId}
                      local={true}
                      style={{flex: 1, width: 100, height: 100}}
                    />
                    <View
                      style={{
                        flex: 2,
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}>
                      <Text>Connected (00:01...)</Text>
                      <Button title={'End call'} onPress={this.endCall} />
                    </View>
                  </SendBirdCallsVideo>
                ) : (
                  <View>
                    <Text>Connected (00:01...)</Text>
                    <Button title={'End call'} onPress={this.endCall} />
                  </View>
                )}
              </View>
            ) : (
              <View style={styles.container}>
                {callId && isVideoCall ? (
                  <View style={styles.container}>
                    <SendBirdCallsVideo
                      callId={callId}
                      local={true}
                      style={{flex: 1}}>
                      <View
                        style={{
                          flex: 1,
                          alignItems: 'center',
                          justifyContent: 'center',
                        }}>
                        <Text>Calling...</Text>
                        <Button title={'End call'} onPress={this.endCall} />
                      </View>
                    </SendBirdCallsVideo>
                  </View>
                ) : (
                  <View style={styles.container}>
                    <Text>Calling...</Text>
                    <Button title={'End call'} onPress={this.endCall} />
                  </View>
                )}
              </View>
            )}
          </View>
        ) : (
          <CallUI caller={caller} onCall={this.call} onLogout={this.logout} />
        )}
      </View>
    );
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
  calling: {},
  input: {
    height: 40,
    backgroundColor: '#e2e2e2',
    marginVertical: 10,
    borderRadius: 5,
    padding: 10,
  },
  fixToText: {
    margin: 10,
    flexDirection: 'row',
    width: 200,
    justifyContent: 'space-between',
  },
});

class NotAuthenticated extends React.Component {
  state = {};
  onChangeText = value => {
    this.setState({userId: value});
  };
  signIn = () => {
    const {userId} = this.state;
    if (userId) {
      const {onSubmitUserId} = this.props;
      onSubmitUserId && onSubmitUserId(userId);
    }
  };
  render() {
    const {userId} = this.state;
    return (
      <View style={styles.container}>
        <View>
          <Text style={styles.welcome}>Authenticate</Text>
          <TextInput
            name={'userId'}
            style={styles.input}
            onChangeText={this.onChangeText}
            value={userId}
            placeholder="...SendBird User ID"
            keyboardType="numeric"
          />
          <Button title="Sign in" color="blue" onPress={this.signIn} />
        </View>
      </View>
    );
  }
}

class CallUI extends React.Component {
  state = {};
  onChangeText = text => {
    this.setState({userId: text});
  };
  render() {
    const {caller, onCall, onLogout} = this.props;
    const {userId} = this.state;

    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Signed in user: #{caller.userId} {caller.nickname}
          <Button title={`Logout #${caller.userId}`} onPress={onLogout} />
        </Text>
        <TextInput
          name={'userId'}
          style={styles.input}
          onChangeText={this.onChangeText}
          value={userId}
          placeholder="...enter User ID to call"
          keyboardType="numeric"
        />
        <View style={{flexDirection: 'row', width: '100%'}}>
          <View style={styles.fixToText}>
            <Button
              color={'green'}
              title={'Video call'}
              onPress={() => onCall(userId, true)}
            />
            <Button
              color={'blue'}
              title={'Voice call'}
              onPress={() => onCall(userId, false)}
            />
          </View>
        </View>
      </View>
    );
  }
}
