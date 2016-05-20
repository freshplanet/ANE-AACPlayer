package com.freshplanet.ane.AirAACPlayer.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.ExtensionContext;

public class GetDownloadFunction extends BaseFunction 
{
    @Override
    public FREObject call(FREContext context, FREObject[] args) 
    {
    	int download = ((ExtensionContext)context).getDownload();
    	return getFREObjectFromInt(download);
    }
}
