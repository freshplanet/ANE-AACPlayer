package com.freshplanet.ane.AirAACPlayer.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;

public class LoadUrlFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {   
        try
        {
            String url = args[0].getAsString();
            Extension.context.getPlayer().setDataSource(url);
            Extension.context.getPlayer().prepare();
        }
        catch (Exception e)
        {
        	Log.e("[AirAACPlayer]", "Error on load");
            e.printStackTrace();
        }
        
        return null;
    }

}
