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

import java.util.HashMap;
import java.util.Map;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.ViewGroup;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.freshplanet.ane.AirAACPlayer.functions.*;

public class ExtensionContext extends FREContext
{
    public static final String LOADING = "LOADING";
    public static final String PREPARED = "PREPARED";
    public static final String ERROR = "ERROR";

    private MediaPlayer _mediaPlayer;
    private String _mediaUrl;

    private String _state;
    
    @Override
    public void dispose() {}

    @Override
    public Map<String, FREFunction> getFunctions()
    {
        Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
        
        functions.put("loadUrl", new LoadUrlFunction());
        functions.put("play", new PlayFunction());
        functions.put("pause", new PauseFunction());
        functions.put("stop", new StopFunction());
        functions.put("close", new CloseFunction());
        functions.put("getLength", new GetLengthFunction());
        functions.put("getProgress", new GetProgressFunction());
        
        return functions;
    }

    public String getState()
    {
        return _state;
    }

    public void setState(String state)
    {
        _state = state;
    }
    
    public ViewGroup getRootContainer()
    {
        return (ViewGroup)((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0);
    }
    
    public MediaPlayer getPlayer()
    {
        if (_mediaPlayer == null)
        {
            _mediaPlayer = new MediaPlayer();
            _mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        
        return _mediaPlayer;
    }
    
    public void setPlayer(MediaPlayer player)
    {
        this._mediaPlayer = player;
    }
}
