package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class LoadFunction extends BaseFunction 
{
    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
    	String url = getStringFromFREObject(args[0]);
    	((ExtensionContext)context).load(url);
        return null;
    }
}
