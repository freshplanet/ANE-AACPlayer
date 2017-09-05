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

package com.freshplanet.ane.AirAACPlayer;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileLoader extends AsyncTask<String, Integer, byte[]> {

    private HttpURLConnection connection;
    private Exception error;
    private FileLoaderListener listener;

    public FileLoader(FileLoaderListener listener) {
        this.listener = listener;
    }

    @Override
    protected byte[] doInBackground(String... params) {

        ByteArrayOutputStream outputStream;

        try {

            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            connection.setReadTimeout(20000);
            connection.connect();

            int bytesTotal = connection.getContentLength();

            if (bytesTotal < 1) {

                error = new Exception("Downloaded file had 0 bytes");
                return null;
            }

            outputStream = new ByteArrayOutputStream(bytesTotal);
            int bytesLoaded = 0;
            InputStream stream = connection.getInputStream();

            byte[] chunk = new byte[4096];
            int bytesRead;

            while ((bytesRead = stream.read(chunk)) > 0) {

                if (isCancelled()) {

                    connection.disconnect();
                    return null;
                }

                bytesLoaded += bytesRead;
                outputStream.write(chunk, 0, bytesRead);
                publishProgress(bytesLoaded, bytesTotal);
            }
        }
        catch (MalformedURLException e) {
            error = e;
            return null;
        }
        catch (IOException e) {
            error = e;
            return null;
        }
        catch (IllegalArgumentException e) {
            //this can happen instantiating the ByteArrayOutputStream??
            error = e;
            return null;
        }

        return outputStream.toByteArray();
    }

    @Override
    protected void onPostExecute(byte[] bytes) {

        if (bytes == null && error != null)
            listener.onError(error);
        else
            listener.onLoaded(bytes);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        listener.onProgress(values[0], values[1]);
    }
}
