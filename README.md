# react-native-sendbird-calls


### TODO

- [x] ios
   - [x] configure appId
   - [x] configure voIP
   - [ ] configure remote push notification
   - [x] authenticate
   - [x] voice call
   - [x] video call
   - [ ] on/off audio
   - [ ] on/off video
- [ ] android

## Getting started

`$ npm install react-native-sendbird-calls --save`

## Usage
```javascript
import {SendBirdCalls} from 'react-native-sendbird-calls';

SendBirdCalls.setup(appId)
SendBirdCalls.addEventListener(event, handler)
SendBirdCalls.authenticate(userId)
SendBirdCalls.setupVoIP()
SendBirdCalls.dial(callee, isVideoCall)
SendBirdCalls.endCall(callId)
SendBirdCalls.removeAllEventListeners())

```


