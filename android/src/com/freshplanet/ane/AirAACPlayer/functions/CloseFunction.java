package com.freshplanet.ane.AirAACPlayer.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class CloseFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] arg1) 
    {
        try
        {
        	ExtensionContext extensionContext = (ExtensionContext) context;
        	
        	if (extensionContext.getPlayer() != null)
        	{
        		extensionContext.getPlayer().stop();
        		extensionContext.getPlayer().release();
        		extensionContext.setPlayer(null);
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
