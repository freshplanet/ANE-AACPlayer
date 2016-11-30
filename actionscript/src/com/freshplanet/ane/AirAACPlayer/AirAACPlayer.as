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
    import flash.events.ErrorEvent;
    import flash.events.Event;
    import flash.events.EventDispatcher;
    import flash.events.StatusEvent;
    import flash.external.ExtensionContext;
    import flash.system.Capabilities;
    import flash.utils.ByteArray;

    public class AirAACPlayer extends EventDispatcher
    {
		////////////////////////////////////////////////////////////////////////////////
		// Constants
		
		public static const STATE_INIT:String = "init";
		public static const STATE_LOADING:String = "loading";
		public static const STATE_LOADED:String = "loaded";
		public static const STATE_READY:String = "ready";
		public static const STATE_ERROR:String = "error";
		public static const STATE_DISPOSED:String = "disposed";

		//The LOADED state is optional, it can be skipped (iOS)
		public static const AAC_PLAYER_LOADED:String = "AAC_PLAYER_LOADED";
        public static const AAC_PLAYER_PREPARED:String = "AAC_PLAYER_PREPARED";
        public static const AAC_PLAYER_ERROR:String = "AAC_PLAYER_ERROR";
        public static const AAC_PLAYER_DOWNLOAD:String = "AAC_PLAYER_DOWNLOAD";

		
		private static const EXTENSION_ID:String = "com.freshplanet.AirAACPlayer";
		
		
		////////////////////////////////////////////////////////////////////////////////
		// States
		
		public var logEnabled:Boolean = true;
		
		private var _context:ExtensionContext;
		private var _state:String = STATE_INIT;
		private var _url:String;
		
		
		////////////////////////////////////////////////////////////////////////////////
		// Lifecycle
		
		public function AirAACPlayer(url:String)
		{
			if (!isSupported) return;
			
			_context = ExtensionContext.createExtensionContext(EXTENSION_ID, null);
			if (!_context)
			{
				log("ERROR - Extension context is null. Please check if extension.xml is setup correctly.");
				return;
			}
			_context.addEventListener(StatusEvent.STATUS, onStatus);
			_url = url;
		}
		
		public function dispose():void
		{
			if (!isSupported || state == STATE_DISPOSED) return;
			
			_state = STATE_DISPOSED;
			_context.dispose();
			_context = null;
		}
		
		
		////////////////////////////////////////////////////////////////////////////////
		// Getters

		private static function isIOS():Boolean
		{
			return (Capabilities.manufacturer.indexOf("iOS") != -1);
		}

		private static function isAndroid():Boolean
		{
			return (Capabilities.manufacturer.indexOf("Android") != -1);
		}
		
		/** AirAACPlayer is supported on iOS and Android devices. */
		public static function get isSupported():Boolean
		{
			return isIOS() || isAndroid();
		}
		
		public function get state():String
		{
			return _state;
		}
		
		public function get url():String
		{
			return _url;
		}
		
		/** Duration in milliseconds */
		public function get duration():int
		{
			if (!isSupported || state != STATE_READY) return -1;
			return _context.call("AirAACPlayer_getDuration") as int;
		}
		
		/** Progress in milliseconds */
		public function get progress():int
		{
			if (!isSupported || state != STATE_READY) return -1;
			return _context.call("AirAACPlayer_getProgress") as int;
		}

		/** Download Progress percentage between 0 and 100 */
		public function get download():int
		{
			if (!isSupported || _state == STATE_DISPOSED) return 0;
			return _context.call("AirAACPlayer_getDownload") as int;
		}

		////////////////////////////////////////////////////////////////////////////////
		// Setters

		/**
		 * Set the media volume.
		 *
		 * @param volume:Number float between 0.0 and 1.0
		 */
		public function set volume(volume:Number):void
		{
			if (!isSupported || state != STATE_READY) return;
			_context.call("AirAACPlayer_setVolume", volume);
		}
		
		
		////////////////////////////////////////////////////////////////////////////////
		// Imperatives
		
		public function load():void
		{
			if (!isSupported || state != STATE_INIT) return;
			_state = STATE_LOADING;
			_context.call("AirAACPlayer_load", _url);
		}

		//This is only called on Android
		public function prepare():void
		{
			if (!isSupported ||  state != STATE_LOADED) return;
			_state = STATE_LOADING;
			_context.call("AirAACPlayer_prepare");
		}
		
		/**
		 * Start playing the stream.
		 * If the playback has been paused before, it will continue from this point.
		 * 
		 * @param startTime:int the start time in milliseconds
		 */
		public function play(startTime:int = 0, myByteArray:ByteArray=null):void
		{
			if (!isSupported || (state != STATE_READY && myByteArray == null)) return;

			startTime = Math.max(0, Math.min(duration, startTime));

			if(isIOS())
			{
				_context.call("AirAACPlayer_play", startTime, myByteArray);
			}
			else if(isAndroid())
			{
				_context.call("AirAACPlayer_play", startTime);
			}


		}
		
		/** Pause the playback */
		public function pause():void
		{
			if (!isSupported || state != STATE_READY) return;
			_context.call("AirAACPlayer_pause");
		}
		
		/** Stop the playback and move the play head to the beginning of the file */
		public function stop():void
		{
			if (!isSupported || state != STATE_READY) return;
			_context.call("AirAACPlayer_stop");
		}
		
		private function log(message:String):void
		{
			if (logEnabled) trace("[AirAACPlayer] " + message);
		}
		
		
		////////////////////////////////////////////////////////////////////////////////
		// Event listeners

        private function onStatus(event:StatusEvent):void
        {
			if (state == STATE_DISPOSED) return;
			
            if (event.code == "LOGGING") // Simple log message
            {
                log(event.level);
            }
			else if (event.code == AAC_PLAYER_LOADED) // only on Android
			{
				_state = STATE_LOADED;
				dispatchEvent(new Event(event.code));
			}
            else if (event.code == AAC_PLAYER_PREPARED)
            { 
				_state = STATE_READY;
                dispatchEvent(new Event(event.code));
            }
            else if (event.code == AAC_PLAYER_ERROR)
            {
				_state = STATE_ERROR;
                dispatchEvent(new ErrorEvent(event.code, false, false, event.level));
            }
            else if (event.code == AAC_PLAYER_DOWNLOAD)
            {
                dispatchEvent(new Event(event.code));
            }
        }
    }
}
