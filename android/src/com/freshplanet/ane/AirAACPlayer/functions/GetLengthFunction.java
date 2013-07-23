package com.freshplanet.ane.AirAACPlayer.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;

public class GetLengthFunction implements FREFunction
{

    @Override
    public FREObject call(FREContext context, FREObject[] args)
    {
        try
        {
            int length = Extension.context.getPlayer().getDuration();
            return FREObject.newObject(length);
        }
        catch (Exception e)
        {
        	Log.e("[AirAACPlayer]", "Error on get length");
            e.printStackTrace();
        }
        
        return null;
    }

}
