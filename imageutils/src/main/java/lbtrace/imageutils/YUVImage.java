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

import android.support.annotation.NonNull;

/*
 * YUV format image
 */
public class YUVImage extends Image {
    public YUVImage(short[][][] imageData, int width, int height) {
        super(ImageFormat.YUV, imageData, width, height);
    }

    @Override
    public Image convert(@NonNull ImageFormat dst) {
        switch (dst) {
            case RGB:
                return convertToRGB();
            case YUV:
                return this;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Pad Y channel of image with edge value
     *
     * @param pad padding size
     * @return padded Y channel data
     */
    public short[][] padYChannel(int pad) {
        if (pad < 0)
            throw new IllegalArgumentException();

        int newWidth = width + 2 * pad;
        int newHeight = height + 2 * pad;
        short[][] dst = new short[newWidth][newHeight];

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                if (i < pad && j < pad)
                    dst[i][j] = imageData[0][0][0];
                if (i >= pad && i < pad + width && j < pad)
                    dst[i][j] = imageData[i - pad][0][0];
                if (i >= pad + width && j < pad)
                    dst[i][j] = imageData[width - 1][0][0];
                if (i < pad && j >= pad && j < pad + height)
                    dst[i][j] = imageData[0][j - pad][0];
                if (i >= pad && i < pad + width && j >= pad && j < pad + height)
                    dst[i][j] = imageData[i - pad][j - pad][0];
                if (i >= pad + width && j >= pad && j < pad + height)
                    dst[i][j] = imageData[width - 1][j - pad][0];
                if (i < pad && j >= pad + height)
                    dst[i][j] = imageData[0][height - 1][0];
                if (i >= pad && i < pad + width && j >= pad + height)
                    dst[i][j] = imageData[i - pad][height - 1][0];
                if (i >= pad + width && j >= pad + height)
                    dst[i][j] = imageData[width - 1][height - 1][0];
            }
        }

        return dst;
    }

    /**
     * Update Y channel value of YUVImage
     *
     * @param newYImage new Y channel value array
     */
    public void updateYChannel(float[] newYImage) {
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                short value = (short) (newYImage[j * width + i] * 255.0f);

                imageData[i][j][0] = (value > 255) ? 255 : ((value < 0) ? 0 : value);
            }
        }
    }

    private Image convertToRGB() {
        short[][][] dst = new short[width][height][3];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int y = imageData[i][j][0];
                int u = imageData[i][j][1];
                int v = imageData[i][j][2];
                int r = range((1000 * y + 1402 * (v - 128)) / 1000);
                int g = range((1000 * y - 344 * (u - 128) - 714 * (v - 128)) / 1000);
                int b = range((1000 * y + 1772 * (u - 128)) / 1000);

                dst[i][j][0] = (short) r;
                dst[i][j][1] = (short) g;
                dst[i][j][2] = (short) b;
            }
        }

        return new RGBImage(dst, width, height);
    }
}
