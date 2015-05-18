//
//  AppDelegate.swift
//  CsieDnsUpdater
//
//  Created by Cheng-Lin Yang on 2015/5/13.
//  Copyright (c) 2015 Cheng-Lin Yang. All rights reserved.
//

import Cocoa

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {

    @IBOutlet weak var statusMenu: NSMenu!
    
    func applicationDidFinishLaunching(aNotification: NSNotification) {
        
    }
        
    @IBAction func abortButton(sender: AnyObject) {
        //NSApplication.sharedApplication().terminate(self)
    }

    func applicationWillTerminate(aNotification: NSNotification) {
        // Insert code here to tear down your application
    }

}

