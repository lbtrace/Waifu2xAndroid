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

package lbtrace.waifu2xandroid_v2;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import lbtrace.imageutils.Image;
import lbtrace.imageutils.RGBImage;
import lbtrace.imageutils.YUVImage;

/*
 * Waifu2x Scale Model
 *
 * See https://github.com/nagadomi/waifu2x
 */
public class Waifu2xScale implements ScalePolicy {
    private static final String LOG_TAG = Waifu2xScale.class.getSimpleName();
    private static final String MODEL_FILE = "file:///android_asset/tf_waifu2x_frozed.pb";
    private static final int SCALE_FACTOR = 2;
    private static final int PAD_SIZE = 14;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output_13";

    private final AssetManager assetManager;
    private final TensorFlowInferenceInterface tf;


    public Waifu2xScale(@NonNull AssetManager assetManager) {
        this.assetManager = assetManager;
        tf = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
    }

    @Override
    public Bitmap scale(@NonNull Bitmap ori) {
        // Convert to YUV image
        Image yuvImage = new RGBImage(ori).convert(Image.ImageFormat.YUV);

        // Scale image
        yuvImage.resize(yuvImage.getWidth() * SCALE_FACTOR,
                yuvImage.getHeight() * SCALE_FACTOR);

        // Just use Y channel
        short[][] yImage = ((YUVImage) yuvImage).padYChannel(PAD_SIZE);
        float[] inputTensor = buildInputTensor(yImage, yuvImage.getWidth() + 2 * PAD_SIZE,
                yuvImage.getHeight() + 2 * PAD_SIZE);

        Log.i(LOG_TAG, "TensorFlow Mobile running ......");
        // feed input tensor
        tf.feed(INPUT_NAME, inputTensor, 1, yuvImage.getHeight() + 2 * PAD_SIZE,
                yuvImage.getWidth() + 2 * PAD_SIZE, 1);

        // run waifu2x model
        tf.run(new String[]{OUTPUT_NAME});

        // fetch output result
        float[] outputTensor = new float[yuvImage.getHeight() * yuvImage.getWidth()];
        tf.fetch(OUTPUT_NAME, outputTensor);

        Log.i(LOG_TAG, "TensorFlow Mobile Success ");
        ((YUVImage) yuvImage).updateYChannel(outputTensor);

        Image scaleRGBImage = yuvImage.convert(Image.ImageFormat.RGB);

        return ((RGBImage) scaleRGBImage).generateBitmap();
    }

    private float[] buildInputTensor(short[][] yImage, int width, int height) {
        float[] inputTensor = new float[width * height];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                inputTensor[j * width + i] = ((float) yImage[i][j]) / 255.0f;
            }
        }

        return inputTensor;
    }
}
