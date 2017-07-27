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
package {


import com.freshplanet.ane.AirAACPlayer.AirAACPlayer;
import com.freshplanet.ane.AirAACPlayer.events.AirAACPlayerErrorEvent;
import com.freshplanet.ane.AirAACPlayer.events.AirAACPlayerEvent;

import flash.display.Sprite;
import flash.display.StageAlign;
import flash.events.Event;

import com.freshplanet.ui.ScrollableContainer;
import com.freshplanet.ui.TestBlock;

[SWF(backgroundColor="#057fbc", frameRate='60')]
public class Main extends Sprite {

    public static var stageWidth:Number = 0;
    public static var indent:Number = 0;

    private var _scrollableContainer:ScrollableContainer = null;

    private var _player:AirAACPlayer;

    public function Main() {
        this.addEventListener(Event.ADDED_TO_STAGE, _onAddedToStage);
    }

    private function _onAddedToStage(event:Event):void {
        this.removeEventListener(Event.ADDED_TO_STAGE, _onAddedToStage);
        this.stage.align = StageAlign.TOP_LEFT;

        stageWidth = this.stage.stageWidth;
        indent = stage.stageWidth * 0.025;

        _scrollableContainer = new ScrollableContainer(false, true);
        this.addChild(_scrollableContainer);

        if (!AirAACPlayer.isSupported) {
            trace("AirAACPlayer ANE is NOT supported on this platform!");
            return;
        }

        _player = new AirAACPlayer("YOUR-SOUND-URL");
        _player.addEventListener(AirAACPlayerEvent.AAC_PLAYER_DOWNLOAD, onDownloadProgress);
        _player.addEventListener(AirAACPlayerEvent.AAC_PLAYER_PLAYBACK_FINISHED, onPlaybackFinished);
        _player.addEventListener(AirAACPlayerEvent.AAC_PLAYER_PREPARED, onPlayerPrepared);
        _player.addEventListener(AirAACPlayerErrorEvent.AAC_PLAYER_ERROR, onPlayerError);

        var blocks:Array = [];

	    blocks.push(new TestBlock("load", function():void {
		    _player.load();
	    }));
        blocks.push(new TestBlock("play", function():void {
            _player.play();
        }));
        blocks.push(new TestBlock("pause", function():void {
            _player.pause();
        }));
        blocks.push(new TestBlock("stop", function():void {
            _player.stop();
        }));
        blocks.push(new TestBlock("dispose", function():void {
            _player.dispose();
        }));
        blocks.push(new TestBlock("getState", function():void {
            trace("Player state: ", _player.state.value);
        }));



        /**
         * add ui to screen
         */

        var nextY:Number = indent;

        for each (var block:TestBlock in blocks) {

            _scrollableContainer.addChild(block);
            block.y = nextY;
            nextY +=  block.height + indent;
        }
    }

    private function onPlayerError(event:AirAACPlayerErrorEvent):void {
        trace("Player error occurred ", event.error);
    }

    private function onPlayerPrepared(event:AirAACPlayerEvent):void {
        trace("Player prepared");
    }

    private function onPlaybackFinished(event:AirAACPlayerEvent):void {
        trace("Player playback finished");
    }

    private function onDownloadProgress(event:AirAACPlayerEvent):void {
        trace("Player download progress ", event.downloadProgress);
    }




}
}
