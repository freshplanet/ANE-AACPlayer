/*
 * Copyright 2017 FreshPlanet
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freshplanet.ane.AirAACPlayer
{
	import com.freshplanet.ane.AirAACPlayer.enums.AirAACPlayerState;
	import com.freshplanet.ane.AirAACPlayer.events.AirAACPlayerErrorEvent;
	import com.freshplanet.ane.AirAACPlayer.events.AirAACPlayerEvent;

    import flash.events.EventDispatcher;
    import flash.events.StatusEvent;
    import flash.external.ExtensionContext;
	import flash.filesystem.File;
	import flash.system.Capabilities;

    public class AirAACPlayer extends EventDispatcher
    {
	    // --------------------------------------------------------------------------------------//
	    //																						 //
	    // 									   PUBLIC API										 //
	    // 																						 //
	    // --------------------------------------------------------------------------------------//
		private static const EXTENSION_CONTEXT_SIMPLE_SOUND:String = "simpleSound";
		private static var _simpleSoundContext:ExtensionContext;
		public static function playSimpleSound(path:String, cacheSound:Boolean = false, volume:Number = 1.0):void {

			if(!isAndroid)
				return;

			if(!_simpleSoundContext) {
		 		_simpleSoundContext = ExtensionContext.createExtensionContext(EXTENSION_ID,  EXTENSION_CONTEXT_SIMPLE_SOUND);
				_simpleSoundContext.addEventListener(StatusEvent.STATUS, onSimpleSoundStatusEvent)

			}

			var file:File = File.applicationDirectory.resolvePath(path);
			if(!file.exists) {
				logSimpleSound("File with path " + path + " does not exist in File.applicationDirectory");
				return;
			}

			_simpleSoundContext.call("AirAACPlayer_playSimpleSound", getNativePath(file), volume, cacheSound);
		}

		private static function onSimpleSoundStatusEvent(event:StatusEvent):void {
			if(event.code == "log") {
				logSimpleSound(event.level);
			}
		}

		private static function logSimpleSound(message:String):void {
			trace("[AirAACPlayer - simpleSound] " , message);
		}

	    /**
	     * If <code>true</code>, logs will be displayed at the Actionscript level.
	     */
	    public function get logEnabled() : Boolean {
		    return _logEnabled;
	    }

	    public function set logEnabled( value : Boolean ) : void {
		    _logEnabled = value;
	    }

	    /** AirAACPlayer is supported on iOS and Android devices. */
	    public static function get isSupported():Boolean {
		    return isAndroid || isIOS;
	    }

	    /**
	     * Create AirAACPlayer instance
	     * @param url file or remote sound URL
	     */
	    public function AirAACPlayer(url:String) {
		    if (!isSupported) return;

		    _context = ExtensionContext.createExtensionContext(EXTENSION_ID, null);
		    if (!_context) {
			    log("ERROR - Extension context is null. Please check if extension.xml is setup correctly.");
			    return;
		    }
		    _context.addEventListener(StatusEvent.STATUS, onStatus);
		    _url = url;
	    }



	    /**
	     * Load sound
	     */
	    public function load():void {
		    if (!isSupported || state != AirAACPlayerState.INITIAL) return;
		    _state = AirAACPlayerState.LOADING;
		    _context.call("AirAACPlayer_load", _url);
	    }

        /**
         * Android only - Prepare sound
         */
        public function prepare():void {
            if (!isAndroid || state != AirAACPlayerState.LOADED) return;
            _state = AirAACPlayerState.LOADING;
            _context.call("AirAACPlayer_prepare");
        }

	    /**
	     * Dispose AirAACPlayer
	     */
	    public function dispose():void {
		    if (!isSupported || state == AirAACPlayerState.DISPOSED) return;

		    _state = AirAACPlayerState.DISPOSED;
            _context.call("AirAACPlayer_dispose");
		    _context.dispose();
		    _context = null;
	    }

	    /**
	     * Current player url
	     */
	    public function get url():String {
		    return _url;
	    }

	    /**
	     * Get AirAACPlayer state
	     */
	    public function get state():AirAACPlayerState {
		    return _state;
	    }

	    /**
	     * Duration in milliseconds
	     */
	    public function get duration():int {
		    if (!isSupported || state != AirAACPlayerState.READY) return -1;
		    return _context.call("AirAACPlayer_getDuration") as int;
	    }

	    /**
	     * Progress in milliseconds
	     */
	    public function get progress():int {
		    if (!isSupported || state != AirAACPlayerState.READY) return -1;
		    return _context.call("AirAACPlayer_getProgress") as int;
	    }

	    /**
	     * Set the media volume.
	     * @param volume:Number float between 0.0 and 1.0
	     */
	    public function set volume(volume:Number):void {
		    if (!isSupported || state != AirAACPlayerState.READY) return;
		    _context.call("AirAACPlayer_setVolume", volume);
	    }

	    /**
	     * Start playing the stream.
	     * If the playback has been paused before, it will continue from this point.
	     * @param startTime:int the start time in milliseconds
	     */
	    public function play(startTime:int = 0, mode:int = 0):void {
		    if (!isSupported || state != AirAACPlayerState.READY) return;
		    startTime = Math.max(0, Math.min(duration, startTime));
			if (isIOS) {
				_context.call("AirAACPlayer_play", startTime, mode ? mode : _playbackCategory);
			} else {
				_context.call("AirAACPlayer_play", startTime);
			}
	    }

	    /** Pause the playback */
	    public function pause():void {
		    if (!isSupported || state != AirAACPlayerState.READY) return;
		    _context.call("AirAACPlayer_pause");
	    }

	    /** Stop the playback and move the play head to the beginning of the file */
	    public function stop():void {
		    if (!isSupported || state != AirAACPlayerState.READY) return;
		    _context.call("AirAACPlayer_stop");
	    }

		public static const PLAYBACK_MODE_AMBIENT:int = 1
		public static const PLAYBACK_MODE_SOLO_AMBIENT:int = 2;
		public static const PLAYBACK_MODE_MEDIA:int = 3;
		private static var _playbackCategory:int = 0;
	    public static function setPlaybackCategory(mode:int = 0):void
	    {
	    	_playbackCategory = mode
	    }

	    // --------------------------------------------------------------------------------------//
	    //																						 //
	    // 									 	PRIVATE API										 //
	    // 																						 //
	    // --------------------------------------------------------------------------------------//

		private static const EXTENSION_ID:String = "com.freshplanet.ane.AirAACPlayer";
	    private var _logEnabled:Boolean = true;
		private var _context:ExtensionContext;
		private var _state:AirAACPlayerState = AirAACPlayerState.INITIAL;
		private var _url:String;

		private function log(message:String):void {
			if (_logEnabled) trace("[AirAACPlayer] " + message);
		}

        private function onStatus(event:StatusEvent):void {
			if (state == AirAACPlayerState.DISPOSED) return;
			
            if (event.code == "log") {
                log(event.level);
            }
            else if (event.code == AirAACPlayerEvent.AAC_PLAYER_DOWNLOAD) {
	            dispatchEvent(new AirAACPlayerEvent(event.code, int(event.level)));
            }
            else if (event.code == AirAACPlayerEvent.AAC_PLAYER_LOADED) {
				_state = AirAACPlayerState.LOADED;
                dispatchEvent(new AirAACPlayerEvent(event.code));
            }
            else if (event.code == AirAACPlayerEvent.AAC_PLAYER_PREPARED) {
				_state = AirAACPlayerState.READY;
	            dispatchEvent(new AirAACPlayerEvent(event.code));
            }
            else if (event.code == AirAACPlayerErrorEvent.AAC_PLAYER_ERROR) {
				_state = AirAACPlayerState.ERROR;
	            dispatchEvent(new AirAACPlayerErrorEvent(event.code, event.level));
            }
            else if (event.code == AirAACPlayerEvent.AAC_PLAYER_PLAYBACK_FINISHED) {
	            dispatchEvent(new AirAACPlayerEvent(event.code));
            }
        }

	    private static function get isIOS():Boolean {
		    return Capabilities.manufacturer.indexOf("iOS") > -1 && Capabilities.os.indexOf("x86_64") < 0 && Capabilities.os.indexOf("i386") < 0;;
	    }

	    private static function get isAndroid():Boolean {
		    return Capabilities.manufacturer.indexOf("Android") > -1;
	    }

		private static function getNativePath(file:File):String
		{

			if(file.nativePath == "")
			{
				var tmpArray:Array = file.url.split('/');
				var filename:String = tmpArray.pop();

				var newFilename:String = filename.replace('/', '_');
				var newFile:File = File.applicationStorageDirectory.resolvePath(newFilename);

				file.copyTo(newFile, true);
				return newFile.nativePath;
			}

			return file.nativePath;
		}
    }
}
