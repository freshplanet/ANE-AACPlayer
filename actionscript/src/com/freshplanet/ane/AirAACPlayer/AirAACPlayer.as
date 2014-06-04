//////////////////////////////////////////////////////////////////////////////////////
//
//  Copyright 2012 Freshplanet (http://freshplanet.com | opensource@freshplanet.com)
//  
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//  
//    http://www.apache.org/licenses/LICENSE-2.0
//  
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//  
//////////////////////////////////////////////////////////////////////////////////////

package com.freshplanet.ane.AirAACPlayer
{
    import flash.events.Event;
    import flash.events.EventDispatcher;
    import flash.events.StatusEvent;
    import flash.external.ExtensionContext;
    import flash.system.Capabilities;

    public class AirAACPlayer extends EventDispatcher
    {
        // --------------------------------------------------------------------------------------//
        //                                                                                       //
        //                                     PUBLIC API                                        //
        //                                                                                       //
        // --------------------------------------------------------------------------------------//
        
        public static const AAC_PLAYER_PREPARED:String = "AAC_PLAYER_PREPARED";
        public static const AAC_PLAYER_ERROR:String = "AAC_PLAYER_ERROR";

        /** AirAACPlayer is supported on Android devices. */
        public static function get isSupported():Boolean
        {
            var isAndroid:Boolean = (Capabilities.manufacturer.indexOf("Android") != -1)
            return isAndroid;
        }
        
        public function AirAACPlayer()
        {
            _context = ExtensionContext.createExtensionContext(EXTENSION_ID, null);
            if (!_context)
            {
                log("ERROR - Extension context is null. Please check if extension.xml is setup correctly.");
                return;
            }
            _context.addEventListener(StatusEvent.STATUS, onStatus);
        }
        
        public var logEnabled:Boolean = true;

        /**
        * Load a music stream url
        * @param url:String
        */
        public function loadUrl(url:String):void
        {
            _context.call("loadUrl", url);
        }
        
        /**
        * Start playing the stream.
        * If the playback has been paused before, it will continue from this point.
        * @param startTime:int the start time in milliseconds
        */
        public function play(startTime:int=0):void
        {
            _context.call("play", startTime);
        }
        
        /**
        * Pause the playback
        */
        public function pause():void
        {
            _context.call("pause");
        }

        /**
        * Stop the playback
        */
        public function stop():void
        {
            _context.call("stop");
        }
        
        /**
        * Close the stream and release.
        * Note that loadUrl() has to be called again before replaying if close has been called.
        */
        public function close():void
        {
            _context.call("close");
        }
        
        /** 
        * Length in milliseconds 
        */
        public function get length():int
        {
            return _context.call("getLength") as int;
        }
        
        /** 
        * Progress in milliseconds
        */
        public function get progress():int
        {
            return _context.call("getProgress") as int;
        }
        

        
        // --------------------------------------------------------------------------------------//
        //                                                                                       //
        //                                      PRIVATE API                                      //
        //                                                                                       //
        // --------------------------------------------------------------------------------------//
        
        private static const EXTENSION_ID:String = "com.freshplanet.AirAACPlayer"; 
        private var _context:ExtensionContext;
        
        private function onStatus( event:StatusEvent ):void
        {
            if (event.code == "LOGGING") // Simple log message
            {
                log(event.level);
            }
            else if (event.code == AAC_PLAYER_PREPARED)
            {
                dispatchEvent(new Event(event.code));
            }
            else if (event.code == AAC_PLAYER_ERROR)
            {
                dispatchEvent(new Event(event.code));
            }
        }
        
        private function log( message:String ):void
        {
            if (logEnabled) trace("[AirAACPlayer] " + message);
        }
    }
}