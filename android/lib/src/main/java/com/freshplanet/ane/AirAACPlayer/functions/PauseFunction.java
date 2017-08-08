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


import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.freshplanet.ane.AirAACPlayer.AirAACPlayerExtensionContext;

import static com.freshplanet.ane.AirAACPlayer.Constants.AirAACPlayerEvent_AAC_PLAYER_ERROR;

public class PauseFunction extends BaseFunction {


	@Override
	public FREObject call(FREContext context, FREObject[] args) {
		super.call(context, args);

		AirAACPlayerExtensionContext playerContext = (AirAACPlayerExtensionContext) context;

		if (playerContext.get_player() == null) {
			return  null;
		}

		try {
			if (playerContext.get_player().getPlayWhenReady())
				playerContext.get_player().setPlayWhenReady(false);
		}
		catch (IllegalStateException e) {
			playerContext.dispatchStatusEventAsync(AirAACPlayerEvent_AAC_PLAYER_ERROR, "" + e.getMessage());
		}

		return null;

	}


}
