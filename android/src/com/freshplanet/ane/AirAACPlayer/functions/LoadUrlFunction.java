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

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {   
        try
        {
            ExtensionContext extensionContext = (ExtensionContext) context;
            String url = args[0].getAsString();
            
            Extension.context.dispatchStatusEventAsync("LOGGING", "[Info] Loading url " + url);
            
            if(!url.equals(extensionContext.getMediaUrl())) {
            	extensionContext.setMediaUrl(url);
            	extensionContext.getPlayer().reset();
            	extensionContext.getPlayer().setDataSource(url);
            	extensionContext.getPlayer().prepareAsync();
            	OnPreparedListener listener = new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Extension.context.dispatchStatusEventAsync("LOGGING", "[Info] Player prepared");
                        Extension.context.dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
                    }
                };
                extensionContext.getPlayer().setOnPreparedListener(listener);
            } else {
            	Extension.context.dispatchStatusEventAsync("LOGGING", "[Info] Player prepared");
                Extension.context.dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
            }

        }
        catch (Exception e)
        {
            Extension.context.dispatchStatusEventAsync("LOGGING", "[Error] Error on load");
            Log.e(Extension.TAG, "Error loading url: " + e.getMessage());
        }
        
        return null;
    }

}
