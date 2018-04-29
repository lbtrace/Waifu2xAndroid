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
 * Image class
 */
public abstract class Image {
    private final ImageFormat imageFormat;
    // RGB: byte[][][0] : R, byte[][][1] : G, byte[][][2] : B,
    // YUV: byte[][][0] : Y, byte[][][1] : U, byte[][][2] : V,
    protected short[][][] imageData;
    protected int width;
    protected int height;

    public enum ImageFormat {
        RGB,
        YUV
    }

    protected Image(@NonNull ImageFormat imageFormat, short[][][] imageData,
                  int width, int height) {
        if (imageFormat != ImageFormat.RGB && imageFormat != ImageFormat.YUV ||
                imageData == null || width <= 0 || height <= 0)
            throw new IllegalArgumentException();

        this.imageFormat = imageFormat;
        this.imageData = imageData;
        this.width = width;
        this.height = height;
    }

    protected Image(@NonNull Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        short[][][] data = new short[width][height][3];

        if (bitmap.getConfig() != Bitmap.Config.ARGB_8888)
            throw new UnsupportedOperationException();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int value = bitmap.getPixel(i, j);
                int b = value & 0xFF;
                int g = (value >>> 8) & 0xFF;
                int r = (value >>> 16) & 0xFF;

                data[i][j][0] = (short) r;
                data[i][j][1] = (short) g;
                data[i][j][2] = (short) b;
            }
        }

        this.imageFormat = ImageFormat.RGB;
        this.imageData = data;
        this.width = width;
        this.height = height;
    }

    /**
     * Convert image format.
     *
     * @param dst format of dstination image
     * @return destination image
     */
    public abstract Image convert(@NonNull ImageFormat dst);

    /**
     * Nearest algorithm implement image resized
     *
     * @param dst_width width of resized image
     * @param dst_height height of resized image
     */
    public void resize(int dst_width, int dst_height) {
        if (dst_height <= 0 || dst_width <= 0)
            throw new IllegalArgumentException();

        float wRatio = ((float) width) / dst_width;
        float hRatio = ((float) height) / dst_height;
        short[][][] dst = new short[dst_width][dst_height][3];

        for (int i = 0; i < dst_width; i++) {
            for (int j = 0; j < dst_height; j++) {
                int src_i = (int) ((float) i * wRatio);
                int src_j = (int) ((float) j * hRatio);

                dst[i][j][0] = imageData[src_i][src_j][0];
                dst[i][j][1] = imageData[src_i][src_j][1];
                dst[i][j][2] = imageData[src_i][src_j][2];
            }
        }

        imageData = dst;
        width = dst_width;
        height = dst_height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected int range(int value) {
        if (value > 255)
            value = 255;
        if (value < 0)
            value = 0;

        return value;
    }
}
