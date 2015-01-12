package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class GetDurationFunction extends BaseFunction
{
    @Override
    public FREObject call(FREContext context, FREObject[] args)
    {
        int length = ((ExtensionContext)context).getDuration();
        return getFREObjectFromInt(length);
    }
}
