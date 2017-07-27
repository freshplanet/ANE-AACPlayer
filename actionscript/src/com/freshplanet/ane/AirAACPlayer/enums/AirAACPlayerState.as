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
package com.freshplanet.ane.AirAACPlayer.enums {
public class AirAACPlayerState {
	/***************************
	 *
	 * PUBLIC
	 *
	 ***************************/


	static public const INITIAL                      : AirAACPlayerState = new AirAACPlayerState(Private, "initial");
	static public const LOADING                      : AirAACPlayerState = new AirAACPlayerState(Private, "loading");
	static public const READY                        : AirAACPlayerState = new AirAACPlayerState(Private, "ready");
	static public const ERROR                        : AirAACPlayerState = new AirAACPlayerState(Private, "error");
	static public const DISPOSED                     : AirAACPlayerState = new AirAACPlayerState(Private, "disposed");

	public static function fromValue(value:String):AirAACPlayerState {

		switch (value)
		{
			case INITIAL.value:
				return INITIAL;
				break;
			case LOADING.value:
				return LOADING;
				break;
			case READY.value:
				return READY;
				break;
			case ERROR.value:
				return ERROR;
				break;
			case DISPOSED.value:
				return DISPOSED;
				break;
			default:
				return null;
				break;
		}
	}

	public function get value():String {
		return _value;
	}

	/***************************
	 *
	 * PRIVATE
	 *
	 ***************************/

	private var _value:String;

	public function AirAACPlayerState(access:Class, value:String) {

		if (access != Private)
			throw new Error("Private constructor call!");

		_value = value;
	}
}
}
final class Private {}