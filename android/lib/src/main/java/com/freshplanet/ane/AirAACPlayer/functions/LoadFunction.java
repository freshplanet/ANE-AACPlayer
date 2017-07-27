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


import android.net.Uri;
import android.os.AsyncTask;
import com.google.android.exoplayer.upstream.DataSource;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.AirAACPlayerExtension;
import com.freshplanet.ane.AirAACPlayer.AirAACPlayerExtensionContext;
import com.freshplanet.ane.AirAACPlayer.FileLoader;
import com.freshplanet.ane.AirAACPlayer.FileLoaderListener;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.ByteArrayDataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;

import static com.freshplanet.ane.AirAACPlayer.Constants.AirAACPlayerEvent_AAC_PLAYER_DOWNLOAD;
import static com.freshplanet.ane.AirAACPlayer.Constants.AirAACPlayerEvent_AAC_PLAYER_ERROR;

public class LoadFunction extends BaseFunction implements FileLoaderListener {

	private String _url;

	@Override
	public FREObject call(FREContext context, FREObject[] args) {
		super.call(context, args);

		this._url = getStringFromFREObject(args[0]);
		FileLoader loader = new FileLoader(this);
		loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this._url);

		return null;

	}

	/*
	 *
     * FileLoaderListener
     *
     */

	@Override
	public void onError(Exception e) {
		AirAACPlayerExtension.context.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "" + e.getMessage());

	}

	@Override
	public void onLoaded(byte[] bytes) {
		// prepare
		if (AirAACPlayerExtensionContext.player == null && bytes != null) {

			AirAACPlayerExtensionContext.player = ExoPlayer.Factory.newInstance(1, 500, 2500);
			AirAACPlayerExtensionContext.player.addListener(AirAACPlayerExtension.context);

			Allocator allocator = new DefaultAllocator(AirAACPlayerExtensionContext.BUFFER_SEGMENT_SIZE);

			DataSource dataSource = new ByteArrayDataSource(bytes);
			ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(this._url), dataSource, allocator,
					AirAACPlayerExtensionContext.BUFFER_SEGMENT_COUNT * AirAACPlayerExtensionContext.BUFFER_SEGMENT_SIZE);
			MediaCodecAudioTrackRenderer renderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);
			AirAACPlayerExtensionContext.renderer = renderer;
			AirAACPlayerExtensionContext.player.prepare(renderer);
		}
		else if (bytes == null) {
			AirAACPlayerExtension.context.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "ExoPlayer error");
		}


	}

	@Override
	public void onProgress(Integer bytesLoaded, Integer bytesTotal) {
		int downloadProgress = 0;
		if (bytesTotal > 0)
			downloadProgress = Math.max(0, Math.min(100, (int) ((float) bytesLoaded / (float) bytesTotal * 100)));

		AirAACPlayerExtension.context.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_DOWNLOAD, String.valueOf(downloadProgress));

	}
}
