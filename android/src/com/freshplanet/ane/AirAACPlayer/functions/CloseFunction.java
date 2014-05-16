package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class CloseFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] arg1) 
    {
        try
        {
            ExtensionContext extensionContext = (ExtensionContext) context;

            extensionContext.getPlayer().stop();
            extensionContext.getPlayer().release();
            extensionContext.setMediaUrl(null);
            extensionContext.setPlayer(null);
        }
        catch (Exception e)
        {
            Extension.context.dispatchStatusEventAsync("LOGGING", "[Error] Error on close");
            e.printStackTrace();
        }
        
        return null;
    }

}
