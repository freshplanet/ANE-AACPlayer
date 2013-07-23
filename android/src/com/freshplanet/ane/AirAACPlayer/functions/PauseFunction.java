package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;

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
