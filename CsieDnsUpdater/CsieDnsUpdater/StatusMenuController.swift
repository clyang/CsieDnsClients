//
//  StatusMenuController.swift
//  CsieDnsUpdater
//
//  Created by Cheng-Lin Yang on 2015/5/15.
//  Copyright (c) 2015 Cheng-Lin Yang. All rights reserved.
//

import Cocoa
import CFNetwork

class StatusMenuController: NSObject,NSUserNotificationCenterDelegate {
    @IBOutlet weak var statusMenu: NSMenu!
    @IBOutlet weak var dnsStatusView: DNSView!
    
    
    let statusItem = NSStatusBar.systemStatusBar().statusItemWithLength(-1)
    static var timercount:Int = 0
    let csieioAPI = CsieDnsUpdate()
    let defaults = NSUserDefaults.standardUserDefaults()
    
    var preferencesWindow: PreferencesWindow!
    var dnsStatusMenuItem: NSMenuItem!

    override func awakeFromNib() {
        let icon = NSImage(named: "csielogo")
        let prefFreq = defaults.stringForKey("FREQ") ?? "5"
        let hostname = defaults.stringForKey("HOSTNAME") ?? ""
        let token = defaults.stringForKey("TOKEN") ?? ""
        
        if (hostname.isEmpty && token.isEmpty){
            // application launch for the 1st time
            dnsStatusView.changeLabel("這是您第一次使用本服務，\n請到設定頁面完成相關設定")
        } else {
            // set warning message in case the app is launched without network
            // it will be replace after update
            dnsStatusView.changeLabel("嘗試與csie.io更新資訊！")
        }
        
        icon?.setTemplate(true) // best for dark mode
        statusItem.image = icon
        statusItem.menu = statusMenu
        
        dnsStatusMenuItem = statusMenu.itemWithTitle("DNSSTATUS")
        dnsStatusMenuItem.view = dnsStatusView
        preferencesWindow = PreferencesWindow()
        
        StatusMenuController.timercount = prefFreq.toInt()! * 60
        var timer = NSTimer.scheduledTimerWithTimeInterval(1, target: self, selector: Selector("update"), userInfo: nil, repeats: true)
        NSRunLoop.mainRunLoop().addTimer(timer, forMode: NSRunLoopCommonModes)

    }
    
    @IBAction func updateClicked(sender: NSMenuItem) {
        let prefFreq = defaults.stringForKey("FREQ") ?? "5"
        StatusMenuController.timercount = prefFreq.toInt()! * 60
    }
    
    @IBAction func quitClicked(sender: NSMenuItem) {
        NSApplication.sharedApplication().terminate(self)
    }
    
    @IBAction func preferencesClicked(sender: NSMenuItem) {
        preferencesWindow.showWindow(nil)
    }
    
    func update(){
        let prefFreq = defaults.stringForKey("FREQ") ?? "5"
        let hostname = defaults.stringForKey("HOSTNAME") ?? ""
        let token = defaults.stringForKey("TOKEN") ?? ""
        let date = NSDate()
        let dateFormatter = NSDateFormatter()
        var curIP = ""
        
        StatusMenuController.timercount += 1
        
        if (StatusMenuController.timercount > prefFreq.toInt()! * 60){
            let timestamp = Double(Int(date.timeIntervalSince1970) + prefFreq.toInt()! * 60)
            dateFormatter.dateFormat = "MMM-dd HH:mm:ss"
            var updateDate:NSDate = NSDate(timeIntervalSince1970: timestamp)
            var nextCheck = dateFormatter.stringFromDate(updateDate)

            if (hostname.isEmpty == false && token.isEmpty == false) {
                csieioAPI.getPublicIP() { retIP, error in
                    if error !== nil {
                        //NSLog("Something goes wrong while retrieving external IP")
                    } else {
                        curIP = retIP!
                        var oldIP = self.IPlookup(hostname + ".csie.io")
                        if (curIP != oldIP && oldIP != "") {
                            //NSLog("IP changes, update it")
                            self.csieioAPI.updateIP("hn="+hostname+"&token="+token+"&ip="+retIP!) { resCode, error in
                                if error !== nil {
                                    self.showAlert("更新發生錯誤", msg: "csie.io伺服器更新時發生錯誤, 請稍候再試")
                                } else {
                                    if(resCode == "OK") {
                                        self.dnsStatusView.changeLabel("舊IP: "+oldIP+"\n新IP: "+curIP+"\n下次檢查: "+nextCheck+"\n檢查頻率: "+prefFreq+" 分鐘")
                                        self.showAlert("偵測到IP改變", msg: "您新的外部IP: "+curIP)
                                    } else if(resCode == "KO") {
                                        self.showAlert("更新發生錯誤", msg: "請檢查並且更新您的設定")
                                    } else {
                                        self.showAlert("更新發生錯誤", msg: "csie.io伺服器更新時發生錯誤, 請稍候再試")
                                    }
                                }
                            }
                        } else {
                            if (oldIP == "") {
                                self.dnsStatusView.changeLabel("無法取得hostname資訊\n下次檢查: "+nextCheck+"\n檢查頻率: "+prefFreq+" 分鐘")
                            } else {
                                self.dnsStatusView.changeLabel("IP未改變: "+curIP+"\n下次檢查: "+nextCheck+"\n檢查頻率: "+prefFreq+" 分鐘")
                            }
                        }
                    }
                }
            } else {
                //NSLog("Hostname or Token is empty!")
            }
            StatusMenuController.timercount = 0
        }
    }

    func showAlert(subtitle: String, msg: String) {
        if (defaults.stringForKey("NOTIFY") == "是") {
            NSUserNotificationCenter.defaultUserNotificationCenter().delegate = self
            let notification = NSUserNotification()
            notification.title = "csie.io DDNS服務通知"
            notification.subtitle = subtitle
            notification.informativeText = msg
            notification.soundName = NSUserNotificationDefaultSoundName
            NSUserNotificationCenter.defaultUserNotificationCenter().deliverNotification(notification)
        }
    }
    
    func userNotificationCenter(center: NSUserNotificationCenter!, shouldPresentNotification notification: NSUserNotification!) -> Bool {
        return true
    }

    func IPlookup(hosts: String) -> String {
        let host = CFHostCreateWithName(nil, hosts).takeRetainedValue();
        var result = CFHostStartInfoResolution(host, .Addresses, nil);
        if (result == 0 ){
            return ""
        }
        var success: Boolean = 0;
        let addresses = CFHostGetAddressing(host, &success).takeUnretainedValue() as NSArray;
        if (addresses.count > 0){
            let theAddress = addresses[0] as! NSData;
            var hostname = [CChar](count: Int(NI_MAXHOST), repeatedValue: 0)
            if getnameinfo(UnsafePointer(theAddress.bytes), socklen_t(theAddress.length),
                &hostname, socklen_t(hostname.count), nil, 0, NI_NUMERICHOST) == 0 {
                    if let numAddress = String.fromCString(hostname) {
                        return numAddress
                    } else {
                        return ""
                    }
            } else {
                return ""
            }
        } else {
            return ""
        }
    }
    
}
