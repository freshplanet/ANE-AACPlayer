package com.freshplanet.ane.AirAACPlayer.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class PlayFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
        try
        {
            if (args.length != 0)
            {
                // start from given time (in milliseconds)
                int position = args[0].getAsInt();
                
                ((ExtensionContext) context).getPlayer().seekTo(position);
            }

            Extension.context.getPlayer().start();
        }
        catch (Exception e)
        {
        	Log.e("[AirAACPlayer]", "Error on play");
            e.printStackTrace();
        }
        
        return null;
    }

}
