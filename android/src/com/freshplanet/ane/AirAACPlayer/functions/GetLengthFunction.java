package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class GetLengthFunction implements FREFunction
{
    private ExtensionContext extensionContext;

    @Override
    public FREObject call(FREContext context, FREObject[] args)
    {
        extensionContext = (ExtensionContext) context;

        try
        {
            int length = extensionContext.getPlayer().getDuration();
            return FREObject.newObject(length);
        }
        catch (Exception e)
        {
            extensionContext.dispatchStatusEventAsync("LOGGING", "[Error] Error on get length");
            e.printStackTrace();
        }
        
        return null;
    }
}
