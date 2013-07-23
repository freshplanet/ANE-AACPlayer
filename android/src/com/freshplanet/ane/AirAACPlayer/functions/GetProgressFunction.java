package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.Extension;

public class GetProgressFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
        try
        {
            int position = Extension.context.mediaPlayer.getCurrentPosition();
            return FREObject.newObject(position);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

}
