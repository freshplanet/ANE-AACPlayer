package com.freshplanet.ane.AirAACPlayer.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;

public class CloseFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] arg1) 
    {
        try
        {
        	if (Extension.context.getPlayer() != null)
        	{
	            Extension.context.getPlayer().stop();
	            Extension.context.getPlayer().release();
	            Extension.context.setPlayer(null);
        	}
        }
        catch (Exception e)
        {
        	Log.e("[AirAACPlayer]", "Error on close");
            e.printStackTrace();
        }
        
        return null;
    }

}
