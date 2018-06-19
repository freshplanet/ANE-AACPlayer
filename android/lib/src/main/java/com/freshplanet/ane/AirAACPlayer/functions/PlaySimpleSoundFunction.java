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

import android.media.MediaMetadataRetriever;
import android.media.SoundPool;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.AirAACPlayerExtension;

public class PlaySimpleSoundFunction extends BaseFunction {

	@Override
	public FREObject call(final FREContext context, FREObject[] args) {
		super.call(context, args);

		String path = getStringFromFREObject(args[0]);
		final float volume = (float) getDoubleFromFREObject(args[1]);
		boolean cacheSound = getBooleanFromFREObject(args[2]);

		AirAACPlayerExtension.simpleSoundContext.getSoundPool().setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int i, int i1) {
				AirAACPlayerExtension.simpleSoundContext.getSoundPool().play(i, volume, volume, 1, 0, 1);
			}
		});


		try {
			if(cacheSound) {
				Integer soundId = AirAACPlayerExtension.simpleSoundContext.getSoundCache().get(path);
					if( soundId == null) {
						soundId = AirAACPlayerExtension.simpleSoundContext.getSoundPool().load(path, 1);
						AirAACPlayerExtension.simpleSoundContext.getSoundCache().put(path, soundId);
					}
					else {
						AirAACPlayerExtension.simpleSoundContext.getSoundPool().play(soundId, volume, volume, 1, 0, 1);
					}
			}
			else {
				long soundDuration = getSoundDuration(path);
				final Integer soundId = AirAACPlayerExtension.simpleSoundContext.getSoundPool().load(path, 1);
				new java.util.Timer().schedule(
						new java.util.TimerTask() {
							@Override
							public void run() {
								// your code here
								AirAACPlayerExtension.simpleSoundContext.getSoundPool().unload(soundId);
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

		Integer cachedDuration = AirAACPlayerExtension.simpleSoundContext.getDurationCache().get(path);
		if(cachedDuration != null) {
			return cachedDuration;
		}

		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path);
		String durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		mmr.release();
		int duration;
		try {
			duration = Integer.parseInt(durationString);
		} catch (NumberFormatException e) {
			duration = 20000;// 20 seconds safety for unloading, sound pool should not be used for longer sounds
		}
		AirAACPlayerExtension.simpleSoundContext.getDurationCache().put(path, duration);
		return duration;
	}

}
