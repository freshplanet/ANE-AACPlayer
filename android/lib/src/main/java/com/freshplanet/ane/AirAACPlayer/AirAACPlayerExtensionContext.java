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

package com.freshplanet.ane.AirAACPlayer;

import android.media.MediaCodec;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.freshplanet.ane.AirAACPlayer.functions.GetDurationFunction;
import com.freshplanet.ane.AirAACPlayer.functions.GetProgressFunction;
import com.freshplanet.ane.AirAACPlayer.functions.LoadFunction;
import com.freshplanet.ane.AirAACPlayer.functions.PauseFunction;
import com.freshplanet.ane.AirAACPlayer.functions.PlayFunction;
import com.freshplanet.ane.AirAACPlayer.functions.SetVolumeFunction;
import com.freshplanet.ane.AirAACPlayer.functions.StopFunction;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;


import java.util.HashMap;
import java.util.Map;

import static com.freshplanet.ane.AirAACPlayer.Constants.AirAACPlayerEvent_AAC_PLAYER_ERROR;
import static com.freshplanet.ane.AirAACPlayer.Constants.AirAACPlayerEvent_AAC_PLAYER_PLAYBACK_FINISHED;
import static com.freshplanet.ane.AirAACPlayer.Constants.AirAACPlayerEvent_AAC_PLAYER_PREPARED;

public class AirAACPlayerExtensionContext extends FREContext implements ExoPlayer.Listener, MediaCodecAudioTrackRenderer.EventListener {

	public static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
	public static final int BUFFER_SEGMENT_COUNT = 64;

	private ExoPlayer _player;
	private MediaCodecAudioTrackRenderer _renderer;

	private boolean _dispatchedPrepared = false;
	private FileLoader _fileLoader;
	private boolean _disposed = false;

	public void set_player(ExoPlayer _player) {
		this._player = _player;
	}
	public void set_renderer(MediaCodecAudioTrackRenderer _renderer) {
		this._renderer = _renderer;
	}
	public void set_fileLoader(FileLoader _fileLoader) {
		this._fileLoader = _fileLoader;
	}
	public ExoPlayer get_player() {
		return _player;
	}

	public MediaCodecAudioTrackRenderer get_renderer() {
		return _renderer;
	}
	public boolean is_disposed() {
		return _disposed;
	}

	@Override
	public void dispose() {
		_disposed = true;
		if(_fileLoader != null) {
			_fileLoader.cancel(true);
			_fileLoader = null;
		}
		if (_player != null) {
			_player.stop();
			_player.release();
			_player = null;
		}

		_renderer = null;
		_dispatchedPrepared = false;
	}


	@Override
	public Map<String, FREFunction> getFunctions() {
		Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
		
		functions.put("AirAACPlayer_load", new LoadFunction());
		functions.put("AirAACPlayer_play", new PlayFunction());
		functions.put("AirAACPlayer_stop", new StopFunction());
		functions.put("AirAACPlayer_pause", new PauseFunction());
		functions.put("AirAACPlayer_getDuration", new GetDurationFunction());
		functions.put("AirAACPlayer_getProgress", new GetProgressFunction());
		functions.put("AirAACPlayer_setVolume", new SetVolumeFunction());

		return functions;	
	}

	/*
	 *
     * ExoPlayer.Listener
     *
     */


	@Override
	public void onPlayerStateChanged(boolean var1, int var2) {
		if (_player.getPlaybackState() == ExoPlayer.STATE_READY && !_dispatchedPrepared) {
			_dispatchedPrepared = true;
			dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_PREPARED, "OK");
		}
		else if(_player.getPlaybackState() == ExoPlayer.STATE_ENDED) {
			dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_PLAYBACK_FINISHED, "OK");


		}

	}

	@Override
	public void onPlayWhenReadyCommitted() {

	}

	@Override
	public void onPlayerError(ExoPlaybackException var1) {
		dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "" + var1.getMessage());
	}

	/*
	 *
	 * MediaCodecAudioTrackRenderer.EventListener
	 *
	 */

	@Override
	public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {

		this.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "" + e.getMessage());
	}

	@Override
	public void onAudioTrackWriteError(AudioTrack.WriteException e) {

		this.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "" + e.getMessage());
	}

	@Override
	public void onAudioTrackUnderrun(int i, long l, long l1) {
	}

	/*
	 *
	 * MediaCodecTrackRenderer.EventListener
	 *
	 */

	@Override
	public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {

		this.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "" + e.getMessage());

	}

	@Override
	public void onCryptoError(MediaCodec.CryptoException e) {

		this.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "" + e.getMessage());
	}

	@Override
	public void onDecoderInitialized(String s, long l, long l1) {

	}

}