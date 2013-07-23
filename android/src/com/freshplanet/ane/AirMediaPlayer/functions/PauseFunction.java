package com.freshplanet.ane.AirMediaPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirMediaPlayer.Extension;

public class PauseFunction implements FREFunction
{

    @Override
    public FREObject call(FREContext arg0, FREObject[] args) 
    {
        try
        {
            Extension.context.mediaPlayer.pause();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

}
