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

package com.freshplanet.ane.AirAACPlayer;

import android.media.MediaCodec;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.functions.*;
import com.google.android.exoplayer.*;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.extractor.mp4.FragmentedMp4Extractor;
import com.google.android.exoplayer.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer.extractor.ts.AdtsExtractor;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.ByteArrayDataSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ExtensionContext extends FREContext implements ExoPlayer.Listener,
		MediaCodecAudioTrackRenderer.EventListener
{
	public static final String TAG = "AirAACPlayer.Context";
	public static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
	public static final int BUFFER_SEGMENT_COUNT = 256;
    private ExoPlayer _player;
	private MediaCodecAudioTrackRenderer _renderer;
	private ExtractorSampleSource _sampleSource;
	private DataSource _dataSource;
    private int _download = 0;
	private FileLoader _loader;
	private byte[] _loadedData;
	private String _url = "<no url assigned>";
    
    public ExtensionContext()
    {
    	super();
    	Log.d(TAG, "creating context");
    }
    
    @Override
    public void dispose()
    {
		Log.d("AirAACPlayer", "disposing context");
		if(_player != null) {
			_player.stop();
			_player.release();
			_player = null;
		}
		if(_loader != null) {
			_loader.cancel(true);
			_loader = null;
		}
    }

    @Override
    public Map<String, FREFunction> getFunctions()
    {
        Map<String, FREFunction> functions = new HashMap<String, FREFunction>();

        functions.put("AirAACPlayer_load", new LoadFunction());
		functions.put("AirAACPlayer_prepare", new PrepareFunction());
        functions.put("AirAACPlayer_play", new PlayFunction());
        functions.put("AirAACPlayer_pause", new PauseFunction());
        functions.put("AirAACPlayer_stop", new StopFunction());
        functions.put("AirAACPlayer_getDuration", new GetDurationFunction());
		functions.put("AirAACPlayer_getProgress", new GetProgressFunction());
        functions.put("AirAACPlayer_getDownload", new GetDownloadFunction());
		functions.put("AirAACPlayer_setVolume", new SetVolumeFunction());

        return functions;
    }

	public void load(String url)
    {
		_url = url;
		_loader = new FileLoader();
		_loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, _url);
	}

	private class FileLoader extends AsyncTask<String, Integer, byte[]> {

		private HttpURLConnection connection;
		private Exception error;

		@Override
		protected byte[] doInBackground(String... params) {
			ByteArrayOutputStream outputStream;

			try {
				connection = (HttpURLConnection) new URL(params[0]).openConnection();
				connection.setReadTimeout(10000);
				connection.connect();
				int bytesTotal = connection.getContentLength();
				if(bytesTotal == 0) {
					error = new Exception("Downloaded file had 0 bytes");
					return null;
				}

				outputStream = new ByteArrayOutputStream(bytesTotal);
				int bytesLoaded = 0;
				InputStream stream = connection.getInputStream();

				byte[] chunk = new byte[4096];
				int bytesRead;

				while ((bytesRead = stream.read(chunk)) > 0) {
					if(isCancelled()) {
						connection.disconnect();
						return null;
					}
					bytesLoaded += bytesRead;
					outputStream.write(chunk, 0, bytesRead);
					publishProgress(bytesLoaded, bytesTotal);
				}
			} catch (MalformedURLException e) {
				error = e;
				return null;
			} catch (IOException e) {
				error = e;
				return null;
			}

			return outputStream.toByteArray();
		}

		@Override
		protected void onPostExecute(byte[] bytes) {
			if(bytes == null && error != null) {
				onError(error);
			} else {
				onLoaded(bytes);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			onProgress(values[0], values[1]);
		}
	}

	private void onLoaded(byte[] bytes)
	{
		_loadedData = bytes;
		dispatchStatusEventAsync("AAC_PLAYER_LOADED", "OK");
	}

	private class PrepareFunction implements FREFunction
	{
		@Override
		public FREObject call(FREContext freContext, FREObject[] freObjects) {
			if(_player == null && _loadedData != null) {
				_player = ExoPlayer.Factory.newInstance(1, 10000, 15000);
				_player.addListener(ExtensionContext.this);
				Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
				_dataSource = new ByteArrayDataSource(_loadedData);
				_sampleSource = new ExtractorSampleSource(Uri.parse(_url), _dataSource, allocator,
						BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
				_renderer = new MediaCodecAudioTrackRenderer(_sampleSource, MediaCodecSelector.DEFAULT);
				_player.prepare(_renderer);
			}
			return null;
		}
	}

	private void onProgress(int bytesLoaded, int bytesTotal)
	{
		if(bytesTotal > 0) {
			_download = Math.max(0, Math.min(100, (int) ((float)bytesLoaded / (float) bytesTotal * 100)) );
		} else {
			_download = 0;
		}
		dispatchStatusEventAsync("AAC_PLAYER_DOWNLOAD", "" + _download);
	}
    
    public void play(int position)
    {
    	try
    	{
    		if (position > 0)
    		{
    			_player.seekTo(position);
    		}
			_player.setPlayWhenReady(true);
		}
    	catch (IllegalStateException e)
    	{
			onError(e);
		}
    }
    
    public void pause()
    {
    	try
    	{
    		if (_player.getPlayWhenReady())
    		{
    			_player.setPlayWhenReady(false);
    		}
		}
    	catch (IllegalStateException e)
    	{
			onError(e);
		}
    }
    
    public void stop()
    {
    	try
    	{
    		if (_player.getPlayWhenReady())
    		{
    			_player.setPlayWhenReady(false);
    		}
			_player.seekTo(0);
		}
    	catch (IllegalStateException e)
    	{
			onError(e);
		}
    }
    
    public int getDuration()
    {
    	return (int)_player.getDuration();
    }
    
    public int getProgress()
    {
    	return (int)_player.getCurrentPosition();
    }

    public int getDownload()
    {
        return _download;
    }

	public void setVolume(float volume) {
		volume = volume < 0 ? 0 : volume;
		volume = volume > 1 ? 1 : volume;
		_player.sendMessage(_renderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volume);
	}
    
    public void onError(Exception e)
    {
    	dispatchStatusEventAsync("AAC_PLAYER_ERROR", "" + e.getMessage());
		Log.e(TAG, _url, e);
    }

	@Override
	public void onPlayerStateChanged(boolean b, int i) {
		if(_player.getPlaybackState() == ExoPlayer.STATE_READY) {
			dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
		}
	}

	@Override
	public void onPlayWhenReadyCommitted() {

	}

	@Override
	public void onPlayerError(ExoPlaybackException e) {
		dispatchStatusEventAsync("AAC_PLAYER_ERROR", "" + e.getMessage());
	}

	@Override
	public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
		onError(e);
	}

	@Override
	public void onAudioTrackWriteError(AudioTrack.WriteException e) {
		onError(e);
	}

	@Override
	public void onAudioTrackUnderrun(int i, long l, long l1) {

	}

	@Override
	public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
		onError(e);
	}

	@Override
	public void onCryptoError(MediaCodec.CryptoException e) {
		onError(e);
	}

	@Override
	public void onDecoderInitialized(String s, long l, long l1) {

	}
}
