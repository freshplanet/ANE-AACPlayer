package com.freshplanet.ane.AirMediaPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirMediaPlayer.Extension;

public class LoadUrlFunction implements FREFunction 
{

    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {   
        try
        {
            String url = args[0].getAsString();
            Extension.context.mediaPlayer.setDataSource(url);
            Extension.context.mediaPlayer.prepare();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }

}
