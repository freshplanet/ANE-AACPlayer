Air Native Extension for AAC Streaming (Android)
================================================

This is an [Air native extensions](http://www.adobe.com/devnet/air/native-extensions-for-air.html) 
to interact with the native [MediaPlayer](http://developer.android.com/reference/android/media/MediaPlayer.html) 
on Android. It has been developed by [FreshPlanet](http://freshplanet.com) and is used in the game [SongPop](http://songpop.fm).

The goal of this extension is to have better support for M4a (AAC) streaming on Android.

Installation
------------

The ANE binary (AirAACPlayer.ane) is located in the `bin` folder. 
You should add it to your application project's Build Path and make sure to package it with your app 
(more information [here](http://help.adobe.com/en_US/air/build/WS597e5dadb9cc1e0253f7d2fc1311b491071-8000.html)).

Usage
-----

In order to play a stream, you have to call the loadUrl() function, add an event listener to the instance to
know when the player is ready, and then call the play() function.

    
```actionscript
// Load an url and listen to the AirAACPlayer.AAC_PLAYER_PREPARED event
AirAACPlayer.getInstance().addEventListener(AirAACPlayer.AAC_PLAYER_PREPARED, onPlayerPrepared);
AirAACPlayer.getInstance().loadUrl("http://www.example.com/url-to-your-file.mp4");

// Once the player is ready, you can start playing
var onPlayerPrepared:Function = function(event:Event):void {
    AirAACPlayer.getInstance().play();
};

// Pause the playback
AirAACPlayer.getInstance().pause();

// Start from a specific position (in milliseconds)
AirAACPlayer.getInstance().play(4200);

// You have access to the current position and total length (in milliseconds)
AirAACPlayer.getInstance().progress;
AirAACPlayer.getInstance().length;

// Stop it
AirAACPlayer.getInstance().stop();

// Don't forget to close when you don't need it anymore
AirAACPlayer.getInstance().close();
```


Build script
------------

Should you need to edit the extension source code and/or recompile it, you will find an ant build script 
(build.xml) in the *build* folder:

    cd /path/to/the/ane/build
    mv example.build.config build.config
    #edit the build.config file to provide your machine-specific paths
    ant


Authors
-------

This ANE has been written by [Corentin Smith](http://csmith.fr). 
It belongs to [FreshPlanet Inc.](http://freshplanet.com) and is distributed under the 
[Apache Licence, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
