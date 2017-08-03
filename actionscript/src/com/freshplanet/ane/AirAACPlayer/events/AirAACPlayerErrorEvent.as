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

package com.freshplanet.ane.AirAACPlayer.events {
import flash.events.Event;

public class AirAACPlayerErrorEvent extends Event {

	public static const AAC_PLAYER_ERROR:String = "AirAACPlayerEvent_AAC_PLAYER_ERROR";
	private var _error:String;

	public function AirAACPlayerErrorEvent(type:String, error:String, bubbles:Boolean = false, cancelable:Boolean = false) {
		super(type, bubbles, cancelable);
		_error = error;
	}

	public function get error():String {
		return _error;
	}
}
}
