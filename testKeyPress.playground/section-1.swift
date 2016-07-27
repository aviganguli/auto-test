// Playground - noun: a place where people can play

import Cocoa

//#import <Cocoa/Cocoa.h>

func myCGEventCallback(proxy: CGEventTapProxy, type: CGEventType, event: CGEventRef , refcon: UnsafeMutablePointer<Void> ) -> CGEventRef {
    let keycode : CGKeyCode = CGEventGetIntegerValueField(event, kCGKeyboardEventKeycode);
    
    if (keycode > 0) {
        NSLog("%d", keycode);
    }
    
    return event;
}

func main() -> Int {
    CFRunLoopSourceRef runLoopSource;
    
    let eventTap : CFMachPortRef = CGEventTapCreate(kCGHIDEventTap as uint32, kCGHeadInsertEventTap as uint32, kCGEventTapOptionListenOnly as uint, kCGEventKeyUp as uint32 , myCGEventCallback, UnsafePointer.null());
    
    CGEventTapCreate(kCGHIDEventTap, kCGHeadInsertEventTap, kCGEventTapOptionListenOnly, kCGEventMas, <#callback: CGEventTapCallBack#>, <#userInfo: UnsafeMutablePointer<Void>#>)
    
    CGEventTapCreate(<#tap: CGEventTapLocation#>, <#place: CGEventTapPlacement#>, <#options: CGEventTapOptions#>, <#eventsOfInterest: CGEventMask#>, <#callback: CGEventTapCallBack#>, <#userInfo: UnsafeMutablePointer<Void>#>)
    
    
    if (! eventTap) {
        NSLog("Couldn't create event tap!");
        exit(1);
    }
    
    runLoopSource = CFMachPortCreateRunLoopSource(kCFAllocatorDefault, eventTap, 0);
    
    CFRunLoopAddSource(CFRunLoopGetCurrent(), runLoopSource, kCFRunLoopCommonModes);
    
    CGEventTapEnable(eventTap, true);
    
    CFRunLoopRun();
    
    exit(0);
}
