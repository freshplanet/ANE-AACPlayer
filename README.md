Air Native Extension for Media Playback (Android)
=================================================

This is an [Air native extensions](http://www.adobe.com/devnet/air/native-extensions-for-air.html) to interact with the Amazon MP3 application on Android. It has been developed by [FreshPlanet](http://freshplanet.com) and is used in the game [SongPop](http://songpop.fm).

The goal of this extension is to have better support for M4a (AAC) streaming on Android.

Installation
------------

The ANE binary (AirAmazonMP3.ane) is located in the *bin* folder. You should add it to your application project's Build Path and make sure to package it with your app (more information [here](http://help.adobe.com/en_US/air/build/WS597e5dadb9cc1e0253f7d2fc1311b491071-8000.html)).

Usage
-----



    
```actionscript
Example code
```


Build script
------------

Should you need to edit the extension source code and/or recompile it, you will find an ant build script (build.xml) in the *build* folder:

    cd /path/to/the/ane/build
    mv example.build.config build.config
    #edit the build.config file to provide your machine-specific paths
    ant


Authors
-------

This ANE has been written by [Corentin Smith](http://csmith.fr). 
It belongs to [FreshPlanet Inc.](http://freshplanet.com) and is distributed under the [Apache Licence, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).