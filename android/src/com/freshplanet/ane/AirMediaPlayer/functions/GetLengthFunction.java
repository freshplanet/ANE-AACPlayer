package com.freshplanet.ane.AirMediaPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirMediaPlayer.Extension;

public class GetLengthFunction implements FREFunction
{

    @Override
    public FREObject call(FREContext context, FREObject[] args)
    {
        try
        {
            int length = Extension.context.mediaPlayer.getDuration();
            return FREObject.newObject(length);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

}
