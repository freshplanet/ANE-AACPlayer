package com.freshplanet.ane.AirAACPlayer.functions;

import android.util.Log;

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
            Extension.context.getPlayer().pause();
        }
        catch (Exception e)
        {
        	Log.e("[AirAACPlayer]", "Error on pause");
            e.printStackTrace();
        }
        
        return null;
    }

}
