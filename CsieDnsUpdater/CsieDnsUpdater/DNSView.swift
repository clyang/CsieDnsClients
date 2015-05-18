//
//  DNSView.swift
//  CsieDnsUpdater
//
//  Created by Cheng-Lin Yang on 2015/5/16.
//  Copyright (c) 2015 Cheng-Lin Yang. All rights reserved.
//

import Cocoa

class DNSView: NSView {

    @IBOutlet weak var imageView: NSImageView!
    @IBOutlet weak var statusLabel: NSTextField!
    
    func dnsStatusUpdate(message: String){
    }
    
    override func awakeFromNib() {
        imageView.image = NSImage(named: "cculogo")
    }
    
    func changeLabel(msg: String){
        self.statusLabel.stringValue = msg
    }
}
