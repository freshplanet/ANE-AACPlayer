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

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.freshplanet.ane.AirAACPlayer.functions.*;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

import java.util.HashMap;
import java.util.Map;


public class ExtensionContext extends FREContext implements ExoPlayer.Listener
{
	public static final String TAG = "AirAACPlayer.Context";
	public static final int BUFFER_SEGMENT_SIZE = 1000;
	public static final int BUFFER_SEGMENT_COUNT = 500;
    private ExoPlayer _player;
	private MediaCodecAudioTrackRenderer _renderer;
	private ExtractorSampleSource _sampleSource;
	private DataSource _dataSource;
    private int _download;
	private String _url = "<no url assigned>";
    
    public ExtensionContext()
    {
    	super();
    	Log.d(TAG, "creating context");
		_player = ExoPlayer.Factory.newInstance(1);
		_player.addListener(this);
    }

	private void createPlayer()
	{


	}
    
    @Override
    public void dispose()
    {
		Log.d("AirAACPlayer", "disposing context");
    	_player.stop();
    	_player.release();
    }

    @Override
    public Map<String, FREFunction> getFunctions()
    {
        Map<String, FREFunction> functions = new HashMap<String, FREFunction>();

        functions.put("AirAACPlayer_load", new LoadFunction());
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
		Uri uri = Uri.parse(_url);
		Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
		_dataSource= new DefaultUriDataSource(getActivity().getApplicationContext(), null);
		_sampleSource = new ExtractorSampleSource(uri, _dataSource, allocator, BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
		_renderer = new MediaCodecAudioTrackRenderer(_sampleSource, MediaCodecSelector.DEFAULT);
		_player.prepare(_renderer);

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
			Log.e(TAG, "Error playing " + _url, e);
			onError(_player, 0, 0);
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
    		Log.e(TAG, "Error pausing " + _url, e);
			onError(_player, 0, 0);
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
			Log.e(TAG, "Error stopping " + _url, e);
			onError(_player, 0, 0);
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

    public void onPrepared(ExoPlayer mp)
    {
    	dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
    }
    
    public boolean onError(ExoPlayer mp, int what, int extra)
    {
    	dispatchStatusEventAsync("AAC_PLAYER_ERROR", "" + what);
    	return true;
    }
    public void onBufferingUpdate (MediaPlayer mp, int percent)
    {
        _download = _player.getBufferedPercentage();
        dispatchStatusEventAsync("AAC_PLAYER_DOWNLOAD", "" + _download);
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
}
