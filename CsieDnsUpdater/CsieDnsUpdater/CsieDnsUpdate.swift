//
//  CsieDnsUpdate.swift
//  CsieDnsUpdater
//
//  Created by Cheng-Lin Yang on 2015/5/15.
//  Copyright (c) 2015 Cheng-Lin Yang. All rights reserved.
//

import Foundation

class CsieDnsUpdate {
    func getPublicIP(completionHandler: (String?, NSError?) -> Void) -> NSURLSessionTask {
        let session = NSURLSession.sharedSession()
        let url = NSURL(string: "http://checkip.amazonaws.com")
        let task = session.dataTaskWithURL(url!) { data, response, error in
            if let err = error {
                completionHandler(nil, error)
                return
            }
            
            let dataString = NSString(data: data, encoding: NSUTF8StringEncoding) as! String
            completionHandler(dataString.substringToIndex(dataString.endIndex.predecessor()), nil)
            return
        }
        task.resume()
        return task
    }
    
    func updateIP(query: String, completionHandler: (String?, NSError?) -> Void) -> NSURLSessionTask {
        let urlPath:String = "https://csie.io/update?"+query
        let url = NSURL(string: urlPath)
        let session = NSURLSession.sharedSession()
        let task = session.dataTaskWithURL(url!) { data, response, error in
            if let err = error {
                completionHandler(nil, error)
                return
            }
            
            let dataString = NSString(data: data, encoding: NSUTF8StringEncoding) as! String
            completionHandler(dataString, nil)
            return
        }
        task.resume()
        return task
    }    
}
