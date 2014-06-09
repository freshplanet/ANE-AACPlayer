package com.freshplanet.ane.AirAACPlayer.functions;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class LoadUrlFunction implements FREFunction 
{
    private ExtensionContext extensionContext;

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {   
        extensionContext = (ExtensionContext) context;

        try
        {
            String url = args[0].getAsString();
            extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] Loading url " + url + " - STATE: " + extensionContext.getState());

            if ( extensionContext.getState() == ExtensionContext.LOADING )
            {
                return null;
            }
            else if( extensionContext.getState() == ExtensionContext.PREPARED ) 
            {
                extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] Player already prepared");
                extensionContext.dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
            }
            else
            {
                if ( extensionContext.getState() == ExtensionContext.ERROR )
                {
                    // This should never be called
                    extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] LOADING AGAIN!!");
                    extensionContext.getPlayer().release();
                    extensionContext.setPlayer(null);
                }

            	extensionContext.getPlayer().reset();
            	extensionContext.getPlayer().setDataSource(url);
            	extensionContext.getPlayer().prepareAsync();
                extensionContext.setState(ExtensionContext.LOADING);

            	OnPreparedListener listener = new OnPreparedListener() 
                {
                    @Override
                    public void onPrepared(MediaPlayer mp) 
                    {
                        extensionContext.setState(ExtensionContext.PREPARED);
                        extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] Player prepared");
                        extensionContext.dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
                    }
                };
                extensionContext.getPlayer().setOnPreparedListener(listener);

                MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener()   
                {  
                    @Override  
                    public boolean onError(MediaPlayer mp, int what, int extra)   
                    {  
                        extensionContext.setState(ExtensionContext.ERROR);
                        extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] Player error: " + what + " : " + extra);
                        extensionContext.dispatchStatusEventAsync("AAC_PLAYER_ERROR", "ERROR");
                        return true;  
                    }  
                }; 
                extensionContext.getPlayer().setOnErrorListener(onErrorListener);
            } 

        }
        catch (Exception e)
        {
            extensionContext.dispatchStatusEventAsync("LOGGING", "[Error] Error on load");
            Log.e(Extension.TAG, "Error loading url: " + e.getMessage());
        }
        
        return null;
    }
}
