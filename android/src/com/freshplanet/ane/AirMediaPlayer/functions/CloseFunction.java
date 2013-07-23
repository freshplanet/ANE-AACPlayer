package com.freshplanet.ane.AirMediaPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirMediaPlayer.Extension;

public class CloseFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] arg1) 
    {
        try
        {
            Extension.context.mediaPlayer.stop();
            Extension.context.mediaPlayer.release();
            Extension.context.mediaPlayer = null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

}
