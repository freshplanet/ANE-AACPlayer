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
            
            extensionContext.getPlayer().setDataSource(url);
            extensionContext.getPlayer().prepareAsync();
            
            OnPreparedListener listener = new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					Extension.context.dispatchStatusEventAsync("AAC_PLAYER_PREPARED", "OK");
				}
            };
        }
        catch (Exception e)
        {
        	Log.e("[AirAACPlayer]", "Error on load");
            e.printStackTrace();
        }
        
        return null;
    }

}
