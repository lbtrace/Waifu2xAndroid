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

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public final class ImageUnitTest extends TestCase {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int THRESHOLD = 10;

    public void testResize() {
        Image rgbImage = createImage(Image.ImageFormat.RGB);

        rgbImage.resize(800, 600);
        assertEquals(800, rgbImage.width);
        assertEquals(600, rgbImage.height);
    }

    public void testConvert() {
        Image expRGBImage = createImage(Image.ImageFormat.RGB);
        Image yuvImage = expRGBImage.convert(Image.ImageFormat.YUV);
        Image actRGVImage = yuvImage.convert(Image.ImageFormat.RGB);

        assertImageEquals(expRGBImage, actRGVImage);
        System.out.println("testConvert");
    }

    public void testYUVImagePad() {
        // Todo
    }

    private Image createImage(Image.ImageFormat format) {
        short[][][] imageData = new short[WIDTH][HEIGHT][3];
        Random random = new Random();

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                for (int k = 0; k < 3; k++) {
                    imageData[i][j][k] = (short) random.nextInt(256);
                }
            }
        }

        switch (format) {
            case RGB:
                return new RGBImage(imageData, WIDTH, HEIGHT);
            case YUV:
                return new YUVImage(imageData, WIDTH, HEIGHT);
            default:
                throw new IllegalArgumentException();
        }
    }

    private void assertImageEquals(Image experted, Image actual) {
        assertEquals(experted.width, actual.width);
        assertEquals(experted.height, actual.height);

        for (int i = 0; i < experted.width; i++) {
            for (int j = 0; j < experted.height; j++) {
                for (int k = 0; k < 3; k++) {
                    int diff = actual.imageData[i][j][k] - experted.imageData[i][j][k];

                    if (diff > THRESHOLD || diff < (THRESHOLD * (-1)))
                        throw new RuntimeException();
                }
            }
        }
    }
}