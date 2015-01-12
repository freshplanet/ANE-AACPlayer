package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class GetProgressFunction extends BaseFunction 
{
    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
    	int progress = ((ExtensionContext)context).getProgress();
    	return getFREObjectFromInt(progress);
    }
}
