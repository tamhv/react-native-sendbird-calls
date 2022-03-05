//
//  AppDelegate.swift
//  example
//
//  Created by Tam Huynh on 2/26/22.
//

import UIKit
import CallKit
import PushKit
import SendBirdCalls
import react_native_sendbird_calls


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
  var window: UIWindow?
  
//  var queue: DispatchQueue = DispatchQueue(label: "com.sendbird.calls.demo.appdelegate")
//  var voipRegistry: PKPushRegistry?
  
  func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

         let bridge = RCTBridge(delegate: self, launchOptions: launchOptions)!
         let rootView = RCTRootView(bridge: bridge, moduleName: "example", initialProperties: nil)
         rootView.backgroundColor = UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 1)
         self.window = UIWindow(frame: UIScreen.main.bounds)
         let rootViewController = UIViewController()
         rootViewController.view = rootView
         self.window?.rootViewController = rootViewController
         self.window?.makeKeyAndVisible()
    
//          let appId = "10A8BD3E-3A52-4BC4-B3A7-EA3C2375D599"
//          SendBirdCall.configure(appId: appId)
          
//        let params = AuthenticateParams(userId: "1111", accessToken: nil)
        
//        SendBirdCall.authenticate(with: params) { (user, error) in
//                    guard let user = user, error == nil else {
//                        // Handle error.
//                      return
//                    }
//        }
      
//          SendBirdCall.addDelegate(self, identifier: "AppDelegate")
         
//         self.voipRegistration()
//         self.addDirectCallSounds()
         return true
     }
  
  func applicationWillTerminate(_ application: UIApplication) {
    RNSendBirdCallsHelper.applicationWillTerminate(application: application)
    //RNSendBirdCallsBridge.sayHello("hello1")
  }
}

extension AppDelegate: RCTBridgeDelegate {
    func sourceURL(for bridge: RCTBridge!) -> URL! {
        #if DEBUG
        return RCTBundleURLProvider.sharedSettings()?.jsBundleURL(forBundleRoot: "index", fallbackResource: nil)
        #else
        return Bundle.main.url(forResource: "main", withExtension: "jsbundle")
        #endif
    }
}
