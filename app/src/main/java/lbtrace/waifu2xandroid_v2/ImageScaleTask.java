/*
 * Copyright (c) 2018 lbtrace (coder.wlb@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lbtrace.waifu2xandroid_v2;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

public class ImageScaleTask extends AsyncTask<Bitmap, Void, Bitmap> {
    private final AssetManager mAssetManager;
    private final ExecuteCallback mCallback;

    public interface ExecuteCallback {
        void onPreExecute();

        void onPostExecute(Bitmap bitmap);
    }

    public ImageScaleTask(@NonNull AssetManager assetManager, @NonNull ExecuteCallback callback) {
        super();
        this.mAssetManager = assetManager;
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mCallback.onPostExecute(bitmap);
    }

    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {
        return new Waifu2xScale(mAssetManager).scale(bitmaps[0]);
    }
}
