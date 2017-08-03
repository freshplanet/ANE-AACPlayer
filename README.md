Air Native Extension for AAC playback (iOS + Android)
=====================================================

This is an [Air native extensions](http://www.adobe.com/devnet/air/native-extensions-for-air.html) 
to interact with the native audio players:

* [AVAudioPlayer](https://developer.apple.com/library/ios/documentation/AVFoundation/Reference/AVAudioPlayerClassReference/Chapters/Reference.html) on iOS
* [MediaPlayer](http://developer.android.com/reference/android/media/MediaPlayer.html) on Android

It has been developed by [FreshPlanet](http://freshplanet.com) and is used in the game [SongPop](http://songpop.fm).


Installation
------------

The ANE binary (AirAACPlayer.ane) is located in the `bin` folder. 
You should add it to your application project's Build Path and make sure to package it with your app 
(more information [here](http://help.adobe.com/en_US/air/build/WS597e5dadb9cc1e0253f7d2fc1311b491071-8000.html)).

Usage
-----

A sample application is provided in the `sample` folder to help you get familiar with this ANE.



Notes:
* included binary has been compiled for 64-bit iOS support

Build script
------------

Should you need to edit the extension source code and/or recompile it, you will find an ant build script 
(build.xml) in the *build* folder:

```bash
cd /path/to/the/ane/build
mv example.build.config build.config
#edit the build.config file to provide your machine-specific paths
ant
```


Authors
-------

This ANE has been written by [Corentin Smith](http://csmith.fr), [Kevin Lockard](https://github.com/kevinfreshplanet),
[Jay Canty](https://github.com/jaycanty), [Alexis Taugeron](http://alexistaugeron.com) and [Mateo Kozomara](mateo.kozomara@gmail.com). 
It belongs to [FreshPlanet Inc.](http://freshplanet.com) and is distributed under the 
[Apache Licence, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
