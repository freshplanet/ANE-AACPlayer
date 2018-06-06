package com.freshplanet.ane.AirAACPlayer;

import android.content.Context;
import android.media.SoundPool;
import android.util.SparseArray;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.freshplanet.ane.AirAACPlayer.functions.PlaySimpleSoundFunction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class AirAACPlayerSimpleSoundContext extends FREContext {

    public SoundPool soundPool;
    public HashMap<String, Integer> soundCache;


    public AirAACPlayerSimpleSoundContext() {

    }

    @Override
    public Map<String, FREFunction> getFunctions() {
        Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
        functions.put("AirAACPlayer_playSimpleSound", new PlaySimpleSoundFunction());
        return functions;
    }

    @Override
    public void dispose() {

    }
}
