package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;

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
                Extension.context.mediaPlayer.seekTo(position);
            }

            Extension.context.mediaPlayer.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

}
