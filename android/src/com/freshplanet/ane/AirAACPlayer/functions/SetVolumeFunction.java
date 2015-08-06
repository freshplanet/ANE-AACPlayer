package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class SetVolumeFunction extends BaseFunction
{
    @Override
    public FREObject call(FREContext context, FREObject[] args)
    {
        float volume = (float)getDoubleFromFREObject(args[0]);
        ((ExtensionContext)context).setVolume(volume);
        return null;
    }
}
