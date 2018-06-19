package com.freshplanet.ane.AirAACPlayer;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.freshplanet.ane.AirAACPlayer.functions.PlaySimpleSoundFunction;

import java.util.HashMap;
import java.util.Map;

public class AirAACPlayerSimpleSoundContext extends FREContext {

    private SoundPool soundPool;
    private HashMap<String, Integer> soundCache;
    private HashMap<String, Integer> durationCache;

    public SoundPool getSoundPool() {
        if(soundPool == null) {
            // SoundPool constructor is deprecated since API 21
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

                soundPool = new SoundPool.Builder()
                        .setAudioAttributes(attributes)
                        .setMaxStreams(10)
                        .build();
            }
            else
            {
                soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);

            }
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        }

        return soundPool;
    }

    public HashMap<String, Integer> getSoundCache() {
        if(soundCache == null) {
            soundCache = new HashMap<String, Integer>();
        }
        return soundCache;
    }

    public HashMap<String, Integer> getDurationCache() {
        if(durationCache == null) {
            durationCache = new HashMap<String, Integer>();
        }
        return durationCache;
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
