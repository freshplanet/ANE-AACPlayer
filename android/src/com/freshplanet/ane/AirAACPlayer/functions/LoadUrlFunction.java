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
            extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] Loading url " + url + " - existing url: " + extensionContext.getMediaUrl() + " - isLoaded: " + extensionContext.isLoaded());

            if( !extensionContext.isLoaded() ) 
            {
                if ( url.equals(extensionContext.getMediaUrl()) )
                {

                    // This is a tough one - don't want to reload unless loading error

                    extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] LOADING AGAIN!!");
                    extensionContext.getPlayer().release();
                    extensionContext.setMediaUrl(null);
                    extensionContext.setPlayer(null);
                }

            	extensionContext.setMediaUrl(url);
            	extensionContext.getPlayer().reset();
            	extensionContext.getPlayer().setDataSource(url);
            	extensionContext.getPlayer().prepareAsync();

            	OnPreparedListener listener = new OnPreparedListener() 
                {
                    @Override
                    public void onPrepared(MediaPlayer mp) 
                    {
                        extensionContext.setIsLoaded(true);
                        extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] Player prepared");
                        extensionContext.dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
                    }
                };
                extensionContext.getPlayer().setOnPreparedListener(listener);

            } else {
            	extensionContext.dispatchStatusEventAsync("LOGGING", "[Info] Player already prepared");
                extensionContext.dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
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
