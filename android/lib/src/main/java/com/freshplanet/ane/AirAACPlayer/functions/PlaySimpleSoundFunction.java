/*
 * Copyright 2017 FreshPlanet
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freshplanet.ane.AirAACPlayer.functions;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.AirAACPlayerExtension;

import java.util.HashMap;

public class PlaySimpleSoundFunction extends BaseFunction {

	@Override
	public FREObject call(final FREContext context, FREObject[] args) {
		super.call(context, args);

		String path = getStringFromFREObject(args[0]);
		final float volume = (float) getDoubleFromFREObject(args[1]);
		boolean cacheSound = getBooleanFromFREObject(args[2]);

		if(AirAACPlayerExtension.simpleSoundContext.soundPool == null) {
			// SoundPool constructor is deprecated since API 21
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				AudioAttributes attributes = new AudioAttributes.Builder()
						.setUsage(AudioAttributes.USAGE_GAME)
						.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
						.build();

				AirAACPlayerExtension.simpleSoundContext.soundPool = new SoundPool.Builder()
						.setAudioAttributes(attributes)
						.setMaxStreams(10)
						.build();
			}
			else
			{
				AirAACPlayerExtension.simpleSoundContext.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);

			}
			context.getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
			AirAACPlayerExtension.simpleSoundContext.soundCache = new HashMap<String, Integer>();

		}

		AirAACPlayerExtension.simpleSoundContext.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int i, int i1) {
				AirAACPlayerExtension.simpleSoundContext.soundPool.play(i, volume, volume, 1, 0, 1);
			}
		});


		try {
			if(cacheSound) {

				Integer soundId = AirAACPlayerExtension.simpleSoundContext.soundCache.get(path);
					if( soundId == null) {
						soundId = AirAACPlayerExtension.simpleSoundContext.soundPool.load(path, 1);
						AirAACPlayerExtension.simpleSoundContext.soundCache.put(path, soundId);
					}
					else {
						AirAACPlayerExtension.simpleSoundContext.soundPool.play(soundId, volume, volume, 1, 0, 1);
					}
			}
			else {
				long soundDuration = getSoundDuration(path);
				final Integer soundId = AirAACPlayerExtension.simpleSoundContext.soundPool.load(path, 1);
				new java.util.Timer().schedule(
						new java.util.TimerTask() {
							@Override
							public void run() {
								// your code here
								AirAACPlayerExtension.simpleSoundContext.soundPool.unload(soundId);
							}
						},
						soundDuration
				);
			}
		}
		catch (Exception e) {
			AirAACPlayerExtension.simpleSoundContext.dispatchStatusEventAsync("log", e.getLocalizedMessage());
		}

		return null;

	}

	private long getSoundDuration(String path){
		MediaPlayer player = MediaPlayer.create(AirAACPlayerExtension.simpleSoundContext.getActivity().getApplicationContext(), Uri.parse(path));
		int duration = player.getDuration();
		player.release();
		return duration;
	}

}
