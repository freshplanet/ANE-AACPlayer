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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.freshplanet.ane.AirAACPlayer.functions.GetDurationFunction;
import com.freshplanet.ane.AirAACPlayer.functions.GetProgressFunction;
import com.freshplanet.ane.AirAACPlayer.functions.LoadFunction;
import com.freshplanet.ane.AirAACPlayer.functions.PauseFunction;
import com.freshplanet.ane.AirAACPlayer.functions.PlayFunction;
import com.freshplanet.ane.AirAACPlayer.functions.StopFunction;

public class ExtensionContext extends FREContext implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener
{
    private MediaPlayer _player;
    
    public ExtensionContext()
    {
    	super();
    	
    	_player = new MediaPlayer();
    	_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    	_player.setOnPreparedListener(this);
    	_player.setOnErrorListener(this);
    }
    
    @Override
    public void dispose()
    {
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
        
        return functions;
    }
    
    public void load(String url)
    {
    	try
    	{
    		_player.setDataSource(url);
        	_player.prepareAsync();
		}
    	catch (IOException e)
    	{
    		e.printStackTrace();
		}
    }
    
    public void play(int position)
    {
    	try
    	{
    		if (position > 0)
    		{
    			_player.seekTo(position);
    		}
			_player.start();
		}
    	catch (IllegalStateException e)
    	{
			e.printStackTrace();
		}
    }
    
    public void pause()
    {
    	try
    	{
    		if (_player.isPlaying())
    		{
    			_player.pause();
    		}
		}
    	catch (IllegalStateException e)
    	{
    		e.printStackTrace();
		}
    }
    
    public void stop()
    {
    	try
    	{
    		if (_player.isPlaying())
    		{
    			_player.pause();
    		}
			_player.seekTo(0);
		}
    	catch (IllegalStateException e)
    	{
			e.printStackTrace();
		}
    }
    
    public int getDuration()
    {
    	return _player.getDuration();
    }
    
    public int getProgress()
    {
    	return _player.getCurrentPosition();
    }
    
    public void onPrepared(MediaPlayer mp)
    {
    	dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
    }
    
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
    	dispatchStatusEventAsync("AAC_PLAYER_ERROR", "" + what);
    	return true;
    }
}
