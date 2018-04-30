/*
 * Copyright (C) 2018 lbtrace(coder.wlb@gmail.com)
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

package lbtrace.imageutils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/*
 * RGB format image
 */
public class RGBImage extends Image {
    public RGBImage(short[][][] imageData, int width, int height) {
        super(ImageFormat.RGB, imageData, width, height);
    }

    public RGBImage(@NonNull Bitmap bitmap) {
        super(bitmap);
    }

    @Override
    public Image convert(@NonNull ImageFormat dst) {
        switch (dst) {
            case RGB:
                return this;
            case YUV:
                return convertToYUV();
                default:
                    throw new IllegalArgumentException();

        }
    }

    /**
     * Create a ARGB_8888 format Bitmap
     *
     * @return Bitmap
     */
    public Bitmap generateBitmap() {
        int[] pixels = new int[width * height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int b = imageData[i][j][2];
                int g = imageData[i][j][1];
                int r = imageData[i][j][0];
                int value = (r << 16) + (g << 8) + b + (255 << 24);

                pixels[j * width + i] = value;
            }
        }

        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    private Image convertToYUV() {
        short[][][] dst = new short[width][height][3];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int r = imageData[i][j][0];
                int g = imageData[i][j][1];
                int b = imageData[i][j][2];
                int y = range((299 * r + 587 * g + 114 * b) / 1000);
                int u = range((499 * b - 169 * r - 331 * g + 128 * 1000) / 1000);
                int v = range((499 * r - 418 * g - 81 * b + 128 * 1000) / 1000);

                dst[i][j][0] = (short) y;
                dst[i][j][1] = (short) u;
                dst[i][j][2] = (short) v;
            }
        }

        return new YUVImage(dst, width, height);
    }
}
