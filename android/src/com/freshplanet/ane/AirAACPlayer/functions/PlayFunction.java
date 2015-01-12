package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class PlayFunction extends BaseFunction 
{
	@Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
    	int position = getIntFromFREObject(args[0]);
    	((ExtensionContext)context).play(position);
        return null;
    }
}
