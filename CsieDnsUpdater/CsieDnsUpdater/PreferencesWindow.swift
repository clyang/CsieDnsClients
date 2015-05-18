//
//  PreferencesWindow.swift
//  CsieDnsUpdater
//
//  Created by Cheng-Lin Yang on 2015/5/15.
//  Copyright (c) 2015 Cheng-Lin Yang. All rights reserved.
//

import Cocoa

class PreferencesWindow: NSWindowController, NSWindowDelegate {

    let alert = NSAlert()
    let csieioAPI = CsieDnsUpdate()
    
    @IBOutlet weak var txHostname: Editing!
    @IBOutlet weak var txToken: Editing!
    @IBOutlet weak var txFreq: NSPopUpButton!
    @IBOutlet weak var txNotify: NSPopUpButton!
    @IBOutlet weak var prefSave: NSButton!
    
    override func windowDidLoad() {
        super.windowDidLoad()
        
        let defaults = NSUserDefaults.standardUserDefaults()
        self.window?.center()
        self.window?.makeKeyAndOrderFront(nil)
        NSApp.activateIgnoringOtherApps(true)
        
        let prefFreq = defaults.stringForKey("FREQ") ?? "5"
        let hostname = defaults.stringForKey("HOSTNAME") ?? ""
        let token = defaults.stringForKey("TOKEN") ?? ""
        let notify = defaults.stringForKey("NOTIFY") ?? "是"
        var index = 0
        
        txHostname.stringValue = hostname
        txToken.stringValue = token
        txFreq.selectItemWithTitle(prefFreq)
        if (notify == "是") {
            txNotify.selectItemAtIndex(0)
        } else {
            txNotify.selectItemAtIndex(1)
        }
        txNotify.stringValue = notify
        
        prefSave.title = "儲存"
        prefSave.enabled = true


        // Implement this method to handle any initialization after your window controller's window has been loaded from its nib file.
    }
    
    override var windowNibName : String! {
        return "PreferencesWindow"
    }
    
    @IBAction func test(sender: NSButton) {
        prefSave.title = "儲存"
        prefSave.enabled = true
        
        self.close()
    }

    @IBAction func prefSave(sender: NSButton) {
        
        let defaults = NSUserDefaults.standardUserDefaults()
        
        var hostname = txHostname.stringValue
        var token = txToken.stringValue
        var freq = txFreq.titleOfSelectedItem
        var notify = txNotify.titleOfSelectedItem
        
        println(notify)
        prefSave.title = "處理中..."
        prefSave.enabled = false
        
        if(hostname.isEmpty || token.isEmpty){
            alert.messageText = "Hostname以及Token不可為空白，請重新輸入"
            alert.runModal()
            return
        }
        
        csieioAPI.getPublicIP() { retIP, error in
            if error !== nil {
                //NSLog("Unable to retrieving external IP, let server to decide")
                self.csieioAPI.updateIP("hn="+hostname+"&token="+token+"&ip=") { resCode, error in
                    dispatch_sync(dispatch_get_main_queue()) {
                        let alert = NSAlert()
                        if error !== nil {
                            alert.messageText = "與csie.io伺服器連線發生錯誤，請稍候再試。"
                            alert.runModal()
                            self.prefSave.title = "儲存"
                            self.prefSave.enabled = true
                        } else {
                            if(resCode == "OK") {
                                defaults.setValue(hostname, forKey:"HOSTNAME")
                                defaults.setValue(token, forKey:"TOKEN")
                                defaults.setValue(freq, forKey:"FREQ")
                                defaults.setValue(notify, forKey:"NOTIFY")
                                let tmp:Int? = freq?.toInt()
                                StatusMenuController.timercount = tmp! * 60
                                self.prefSave.title = "儲存"
                                self.prefSave.enabled = true
                                alert.messageText = "DDNS設定成功！"
                                alert.runModal()
                                self.close()
                            } else if(resCode == "KO") {
                                
                                    alert.messageText = "DDNS設定失敗，請檢查您所輸入的Hostname以及token！"
                                    alert.runModal()
                                    self.prefSave.title = "儲存"
                                    self.prefSave.enabled = true
                            } else {
                                alert.messageText = "與csie.io伺服器連線發生錯誤，請稍候再試。"
                                alert.runModal()
                                self.prefSave.title = "儲存"
                                self.prefSave.enabled = true
                            }
                        }
                    }
                }
            } else {
                self.csieioAPI.updateIP("hn="+hostname+"&token="+token+"&ip="+retIP!) { resCode, error in
                    dispatch_sync(dispatch_get_main_queue()) {
                        let alert = NSAlert()
                        if error !== nil {
                            alert.messageText = "與csie.io伺服器連線發生錯誤，請稍候再試。"
                            alert.runModal()
                            self.prefSave.title = "儲存"
                            self.prefSave.enabled = true
                        } else {
                            if(resCode == "OK") {
                                defaults.setValue(hostname, forKey:"HOSTNAME")
                                defaults.setValue(token, forKey:"TOKEN")
                                defaults.setValue(freq, forKey:"FREQ")
                                defaults.setValue(notify, forKey:"NOTIFY")
                                let tmp:Int? = freq?.toInt()
                                StatusMenuController.timercount = tmp! * 60
                                self.prefSave.title = "儲存"
                                self.prefSave.enabled = true
                                alert.messageText = "DDNS設定成功！"
                                alert.runModal()
                                self.close()
                            } else if(resCode == "KO") {
                                alert.messageText = "DDNS設定失敗，請檢查您所輸入的Hostname以及token！"
                                alert.runModal()
                                self.prefSave.title = "儲存"
                                self.prefSave.enabled = true
                            } else {
                                alert.messageText = "與csie.io伺服器連線發生錯誤，請稍候再試。"
                                alert.runModal()
                                self.prefSave.title = "儲存"
                                self.prefSave.enabled = true
                            }
                        }
                    }
                }
            }
        }
    }
}


class Editing: NSTextField {
    
    private let commandKey = NSEventModifierFlags.CommandKeyMask.rawValue
    private let commandShiftKey = NSEventModifierFlags.CommandKeyMask.rawValue | NSEventModifierFlags.ShiftKeyMask.rawValue
    override func performKeyEquivalent(event: NSEvent) -> Bool {
        if event.type == NSEventType.KeyDown {
            if (event.modifierFlags.rawValue & NSEventModifierFlags.DeviceIndependentModifierFlagsMask.rawValue) == commandKey {
                switch event.charactersIgnoringModifiers! {
                case "x":
                    if NSApp.sendAction(Selector("cut:"), to:nil, from:self) { return true }
                case "c":
                    if NSApp.sendAction(Selector("copy:"), to:nil, from:self) { return true }
                case "v":
                    if NSApp.sendAction(Selector("paste:"), to:nil, from:self) { return true }
                case "z":
                    if NSApp.sendAction(Selector("undo:"), to:nil, from:self) { return true }
                case "a":
                    if NSApp.sendAction(Selector("selectAll:"), to:nil, from:self) { return true }
                default:
                    break
                }
            }
            else if (event.modifierFlags.rawValue & NSEventModifierFlags.DeviceIndependentModifierFlagsMask.rawValue) == commandShiftKey {
                if event.charactersIgnoringModifiers == "Z" {
                    if NSApp.sendAction(Selector("redo:"), to:nil, from:self) { return true }
                }
            }
        }
        return super.performKeyEquivalent(event)
    }
}