package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class PauseFunction implements FREFunction
{

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
        try
        {
            ((ExtensionContext) context).getPlayer().pause();
        }
        catch (Exception e)
        {
            Extension.context.dispatchStatusEventAsync("LOGGING", "[Error] Error on pause");
            e.printStackTrace();
        }
        
        return null;
    }

}
