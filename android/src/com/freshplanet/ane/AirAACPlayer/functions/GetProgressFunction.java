package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class GetProgressFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
        try
        {
            int position = ((ExtensionContext) context).getPlayer().getCurrentPosition();
            return FREObject.newObject(position);
        }
        catch (Exception e)
        {
        	Extension.context.dispatchStatusEventAsync("LOGGING", "[Error] Error on get progress");
            e.printStackTrace();
        }
        
        return null;
    }

}
