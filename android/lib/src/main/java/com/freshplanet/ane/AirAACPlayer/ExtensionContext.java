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
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.functions.BaseFunction;
import com.google.android.exoplayer.*;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.ByteArrayDataSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ExtensionContext extends FREContext implements ExoPlayer.Listener, MediaCodecAudioTrackRenderer.EventListener {
	
	private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
	private static final int BUFFER_SEGMENT_COUNT = 64;
	
	private ExoPlayer _player;
	private MediaCodecAudioTrackRenderer _renderer;
	private ExtractorSampleSource _sampleSource;
	private DataSource _dataSource;
	private int _download = 0;
	private FileLoader _loader;
	private byte[] _loadedData;
	private String _url = "<no url assigned>";
	
	/*
	 *
	 * ane init/helpers
	 *
	 */
	
	ExtensionContext() {
		
		super();
		Log.d(Extension.TAG, "creating context");
	}
	
	@Override
	public void dispose() {
		
		Log.d(Extension.TAG, "disposing context");
		
		if (_player != null) {
			
			_player.stop();
			_player.release();
			_player = null;
		}
		
		if (_loader != null) {
			
			_loader.cancel(true);
			_loader = null;
		}
	}
	
	@Override
	public Map<String, FREFunction> getFunctions() {
		
		Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
		
		functions.put("AirAACPlayer_load", _load);
		functions.put("AirAACPlayer_prepare", _prepare);
		functions.put("AirAACPlayer_play", _play);
		functions.put("AirAACPlayer_pause", _pause);
		functions.put("AirAACPlayer_stop", _stop);
		functions.put("AirAACPlayer_getDuration", _getDuration);
		functions.put("AirAACPlayer_getProgress", _getProgress);
		functions.put("AirAACPlayer_getDownload", _getDownload);
		functions.put("AirAACPlayer_setVolume", _setVolume);
		
		return functions;
	}
	
	private void _log(String message) {
		
		_dispatchEvent("LOG", message);
	}
	
	private void _dispatchEvent(String type, String data) {
		
		try {
			dispatchStatusEventAsync(type, "" + data);
		}
		catch (Exception exception) {
			Log.e(Extension.TAG, "dispatchStatusEventAsync", exception);
		}
	}
	
	/*
	 *
	 * as3 interface
	 *
	 */
	
	private final FREFunction _load = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			_url = getStringFromFREObject(args[0]);
			_loader = new FileLoader();
			_loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _url);
			
			return null;
		}
	};
	
	private final FREFunction _prepare = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			if (_player == null && _loadedData != null) {
				
				_player = ExoPlayer.Factory.newInstance(1, 500, 2500);
				_player.addListener(ExtensionContext.this);
				
				Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
				
				_dataSource = new ByteArrayDataSource(_loadedData);
				_sampleSource = new ExtractorSampleSource(Uri.parse(_url), _dataSource, allocator,
														  BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
				_renderer = new MediaCodecAudioTrackRenderer(_sampleSource, MediaCodecSelector.DEFAULT);
				_player.prepare(_renderer);
			}
			else if (_loadedData == null) {
				
				Log.d(Extension.TAG, "ExoPlayer error");
				_dispatchEvent("AAC_PLAYER_ERROR", "ExoPlayer error");
			}
			
			return null;
		}
	};
	
	private final FREFunction _play = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			int position = getIntFromFREObject(args[0]);
			
			try {
				
				if (position > 0)
					_player.seekTo(position);
				
				_player.setPlayWhenReady(true);
			}
			catch (IllegalStateException e) {
				_onError(e);
			}
			
			return null;
		}
	};
	
	private final FREFunction _pause = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			try {
				
				if (_player.getPlayWhenReady())
					_player.setPlayWhenReady(false);
			}
			catch (IllegalStateException e) {
				_onError(e);
			}
			
			return null;
		}
	};
	
	private final FREFunction _stop = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			try {
				
				if (_player.getPlayWhenReady())
					_player.setPlayWhenReady(false);
				
				_player.seekTo(0);
			}
			catch (IllegalStateException e) {
				_onError(e);
			}
			
			return null;
		}
	};
	
	private final FREFunction _getDuration = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			return getFREObjectFromInt((int) _player.getDuration());
		}
	};
	
	private final FREFunction _getProgress = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			return getFREObjectFromInt((int) _player.getCurrentPosition());
		}
	};
	
	private final FREFunction _getDownload = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			return getFREObjectFromInt(_download);
		}
	};
	
	private final FREFunction _setVolume = new BaseFunction() {
		@Override
		public FREObject call(FREContext context, FREObject[] args) {
			
			float volume = (float) getDoubleFromFREObject(args[0]);
			
			volume = volume < 0 ? 0 : volume;
			volume = volume > 1 ? 1 : volume;
			_player.sendMessage(_renderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volume);
			
			return null;
		}
	};
	
	/*
	 *
	 * callbacks for FileLoader
	 *
	 */
	
	private void _onLoaded(byte[] bytes) {
		
		_loadedData = bytes;
		_loader = null;
		_dispatchEvent("AAC_PLAYER_LOADED", "OK");
	}
	
	private void _onProgress(Integer bytesLoaded, Integer bytesTotal) {
		
		if (bytesTotal > 0)
			_download = Math.max(0, Math.min(100, (int) ((float) bytesLoaded / (float) bytesTotal * 100)));
		else
			_download = 0;
		
		_dispatchEvent("AAC_PLAYER_DOWNLOAD", "");
	}
	
	private void _onError(Throwable e) {
		
		_dispatchEvent("AAC_PLAYER_ERROR", "" + e.getMessage());
		Log.e(Extension.TAG, _url, e);
	}
	
    /*
	 *
     * ExoPlayer.Listener
     *
     */
	
	
	@Override
	public void onPlayerStateChanged(boolean var1, int var2) {
		
		if (_player.getPlaybackState() == ExoPlayer.STATE_READY)
			_dispatchEvent("AAC_PLAYER_PREPARED", "OK");
	}
	
	@Override
	public void onPlayWhenReadyCommitted() {
	
	}
	
	@Override
	public void onPlayerError(ExoPlaybackException var1) {
		
		_onError(var1);
	}
	
	/*
	 *
	 * MediaCodecAudioTrackRenderer.EventListener
	 *
	 */
	
	@Override
	public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
		
		Log.w(Extension.TAG, "onAudioTrackInitializationError");
		_onError(e);
	}
	
	@Override
	public void onAudioTrackWriteError(AudioTrack.WriteException e) {
		
		Log.w(Extension.TAG, "onAudioTrackWriteError");
		_onError(e);
	}
	
	@Override
	public void onAudioTrackUnderrun(int i, long l, long l1) {
		
		Log.w(Extension.TAG, "Buffer underrun");
	}
	
	/*
	 *
	 * MediaCodecTrackRenderer.EventListener
	 *
	 */
	
	@Override
	public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
		
		Log.w(Extension.TAG, "onDecoderInitializationError");
		_onError(e);
	}
	
	@Override
	public void onCryptoError(MediaCodec.CryptoException e) {
		
		Log.w(Extension.TAG, "onCryptoError");
		_onError(e);
	}
	
	@Override
	public void onDecoderInitialized(String s, long l, long l1) {
	
	}
	
	/**
	 *
	 */
	private class FileLoader extends AsyncTask<String, Integer, byte[]> {
		
		private HttpURLConnection connection;
		private Exception error;
		
		@Override
		protected byte[] doInBackground(String... params) {
			
			ByteArrayOutputStream outputStream;
			
			try {
				
				connection = (HttpURLConnection) new URL(params[0]).openConnection();
				connection.setReadTimeout(20000);
				connection.connect();
				
				int bytesTotal = connection.getContentLength();
				
				if (bytesTotal < 1) {
				
					error = new Exception("Downloaded file had 0 bytes");
					return null;
				}
				
				outputStream = new ByteArrayOutputStream(bytesTotal);
				int bytesLoaded = 0;
				InputStream stream = connection.getInputStream();
				
				byte[] chunk = new byte[4096];
				int bytesRead;
				
				while ((bytesRead = stream.read(chunk)) > 0) {
					
					if (isCancelled()) {
						
						connection.disconnect();
						return null;
					}
					
					bytesLoaded += bytesRead;
					outputStream.write(chunk, 0, bytesRead);
					publishProgress(bytesLoaded, bytesTotal);
				}
			}
			catch (MalformedURLException e) {
				error = e;
				return null;
			}
			catch (IOException e) {
				error = e;
				return null;
			}
			catch (IllegalArgumentException e) {
				//this can happen instantiating the ByteArrayOutputStream??
				error = e;
				return null;
			}
			
			return outputStream.toByteArray();
		}
		
		@Override
		protected void onPostExecute(byte[] bytes) {
			
			if (bytes == null && error != null)
				_onError(error);
			else
				_onLoaded(bytes);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			
			_onProgress(values[0], values[1]);
		}
	}
}
