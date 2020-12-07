/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.lite.examples.detection.tflite;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Trace;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.amlogic.nnapi;

import static android.content.ContentValues.TAG;

/**
 * Wrapper for frozen detection models trained using the Tensorflow Object Detection API:
 * - https://github.com/tensorflow/models/tree/master/research/object_detection
 * where you can find the training code.
 *
 * To use pretrained models in the API or convert to TF Lite models, please see docs for details:
 * - https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/detection_model_zoo.md
 * - https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/running_on_mobile_tensorflowlite.md#running-our-model-on-android
 */
public class TFLiteObjectDetectionAPIModel implements Classifier {

    final static String TAG = "nndemo";
    private static final int IMAGE_H = 416;
    private static final int IMAGE_W = 416;
  private static final Logger LOGGER = new Logger();

  // Only return this many results.
  private static final int NUM_DETECTIONS = 10;
  // Float model
  private static final float IMAGE_MEAN = 127.5f;
  private static final float IMAGE_STD = 127.5f;
  // Number of threads in the java app
  private static final int NUM_THREADS = 4;
  private boolean isModelQuantized;
  // Config values.
  private int inputSize;
  // Pre-allocated buffers.
  private Vector<String> labels = new Vector<String>();
  private int[] intValues;
  // outputLocations: array of shape [Batchsize, NUM_DETECTIONS,4]
  // contains the location of detected boxes

    private int[] intValues_self = new int[IMAGE_H*IMAGE_W];

  private float[][][] outputLocations;
  // outputClasses: array of shape [Batchsize, NUM_DETECTIONS]
  // contains the classes of detected boxes
  private float[][] outputClasses;
  // outputScores: array of shape [Batchsize, NUM_DETECTIONS]
  // contains the scores of detected boxes
  private float[][] outputScores;
  // numDetections: array of shape [Batchsize]
  // contains the number of detected boxes
  private float[] numDetections;

  private ByteBuffer imgData;
  private ByteBuffer imgData_self=null;

  private Interpreter tfLite;


  private TFLiteObjectDetectionAPIModel() {}

  /** Memory-map the model file in Assets. */
  private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
      throws IOException {
    AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
    FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
    FileChannel fileChannel = inputStream.getChannel();
    long startOffset = fileDescriptor.getStartOffset();
    long declaredLength = fileDescriptor.getDeclaredLength();
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
  }

  /**
   * Initializes a native TensorFlow session for classifying images.
   *
   * @param assetManager The asset manager to be used to load assets.
   * @param labelFilename The filepath of label file for classes.
   * @param inputSize The size of image input
   * @param isQuantized Boolean representing model is quantized or not
   */
  public static Classifier create(
      final AssetManager assetManager,
      final String labelFilename,
      final int inputSize,
      final boolean isQuantized
      )
      throws IOException {
    final TFLiteObjectDetectionAPIModel d = new TFLiteObjectDetectionAPIModel();

    String actualFilename = labelFilename.split("file:///android_asset/")[1];
    InputStream labelsInput = assetManager.open(actualFilename);
    BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
    String line;
    while ((line = br.readLine()) != null) {
      LOGGER.w(line);
      d.labels.add(line);
    }
    br.close();

    d.inputSize = inputSize;



    d.isModelQuantized = isQuantized;
    // Pre-allocate buffers.
    int numBytesPerChannel;
    if (isQuantized) {
      numBytesPerChannel = 1; // Quantized
    } else {
      numBytesPerChannel = 4; // Floating point
    }
    d.imgData = ByteBuffer.allocateDirect(1 * d.inputSize * d.inputSize * 3 * numBytesPerChannel);
    d.imgData.order(ByteOrder.nativeOrder());
    d.imgData_self = ByteBuffer.allocateDirect(IMAGE_H* IMAGE_W* 3);
    d.imgData_self.order(ByteOrder.nativeOrder());
    d.intValues = new int[d.inputSize * d.inputSize];


    d.outputLocations = new float[1][NUM_DETECTIONS][4];
    d.outputClasses = new float[1][NUM_DETECTIONS];
    d.outputScores = new float[1][NUM_DETECTIONS];
    d.numDetections = new float[1];

    return d;
  }

  public List<Recognition> recognizeImage(final Bitmap bitmap,nnapi nndemo) {
    // Log this method so that it can be analyzed with systrace.
    //Trace.beginSection("recognizeImage");

    //Trace.beginSection("preprocessBitmap");
    // Preprocess the image data from 0-255 int to normalized float based
    // on the provided parameters.

    int ret;



    byte[] udata = new byte[416*416*3];
    float[] out = new float[1024];
    float[] out_2 = new float[10];



    //Log.i(TAG, "start test model: " + test_model);
    //try
    //{

        //bt_start.setText(R.string.bt_text_prepare);

        Log.i(TAG, "Start Inference");





      //bitmap = BitmapFactory.decodeFile(image_list[0]);
      //show_img.setImageBitmap(bitmap);
      Log.d(TAG, "bitmap." + bitmap.getWidth() + "****" + bitmap.getHeight());

      convertBitmapToByteBuffer(bitmap);
      //imgData_self.get(udata,0,udata.length);
      imgData_self.rewind();
      imgData_self.get(udata);

      ret = nndemo.nn_model_inputs_set(nnapi.RGB24_RAW_DATA,0,416*416*3,udata);
      if ( ret != 0 )
      {
        Log.e(TAG,"nn model set input fail");

      }
      Log.d(TAG,"Get outputs");
      out_2=nndemo.nn_model_outputs_get(out, nnapi.OBJECT_DETECT , nnapi.AML_OUTDATA_FLOAT32);
      Log.d(TAG,"result:"+ret);


      //this.readLineNumber(MainActivity.this.getAssets().open("imagenet_slim_labels.txt"),ret);

      //Log.i(TAG,"classify result : "+sline);
//
//
      /*
      ret = nndemo.nn_model_destroy();

      if ( ret != 0 )
      {
        Log.e(TAG,"nn model destroy fail");

      }
        */
  //}
  //catch (IOException e) {
  //  Log.e(TAG, "Failed to initialize an image classifiertf.", e);
  //}



    /*
    bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

    imgData.rewind();
    for (int i = 0; i < inputSize; ++i) {
      for (int j = 0; j < inputSize; ++j) {
        int pixelValue = intValues[i * inputSize + j];
        if (isModelQuantized) {
          // Quantized model
          imgData.put((byte) ((pixelValue >> 16) & 0xFF));
          imgData.put((byte) ((pixelValue >> 8) & 0xFF));
          imgData.put((byte) (pixelValue & 0xFF));
        } else { // Float model
          imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
          imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
          imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        }
      }
    }*/
    //Trace.endSection(); // preprocessBitmap

    // Copy the input data into TensorFlow.
    //Trace.beginSection("feed");
	/*
    outputLocations = new float[1][NUM_DETECTIONS][4];
    outputClasses = new float[1][NUM_DETECTIONS];
    outputScores = new float[1][NUM_DETECTIONS];
    numDetections = new float[1];
	*/
    numDetections[0] = out_2[0];
    // You need to use the number of detections from the output and not the NUM_DETECTONS variable declared on top
    // because on some models, they don't always output the same total number of detections
    // For example, your model's NUM_DETECTIONS = 20, but sometimes it only outputs 16 predictions
    // If you don't use the output's numDetections, you'll get nonsensical data
    int numDetectionsOutput = Math.min(NUM_DETECTIONS, (int) numDetections[0]); // cast from float to integer, use min for safety

    final ArrayList<Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
    for (int i = 0; i < numDetectionsOutput; ++i) {
      /*final RectF detection =
              new RectF(
                      outputLocations[0][i][1] * inputSize,
                      outputLocations[0][i][0] * inputSize,
                      outputLocations[0][i][3] * inputSize,
                      outputLocations[0][i][2] * inputSize);*/
        final RectF detection =
                new RectF(
                        (1-out_2[6*i+4])* inputSize,
                        out_2[6*i+3] * inputSize,
                        (1-out_2[6*i+6]) * inputSize,
                        out_2[6*i+5] * inputSize);
      // SSD Mobilenet V1 Model assumes class 0 is background class
      // in label file and class labels start from 1 to number_of_classes+1,
      // while outputClasses correspond to class index from 0 to number_of_classes
      int labelOffset = 1;
      recognitions.add(
              new Recognition(
                      "" + i,
                      labels.get((int) out_2[6*i+1] + labelOffset),
                      out_2[6*i+2],
                      detection));
    }
    //Trace.endSection(); // "recognizeImage"
    return recognitions;
  }

    public void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData_self == null) {
            Log.d(TAG, "imgData_self == null");
            return;
        }
        imgData_self.rewind();
        Bitmap im_resize = Bitmap.createScaledBitmap(bitmap, IMAGE_W, IMAGE_H, false);
        im_resize.getPixels(intValues_self, 0, im_resize.getWidth(), 0, 0, im_resize.getWidth(), im_resize.getHeight());
        // Convert the image to floating point.
        Log.d(TAG, "imgData_self "+im_resize.getWidth()+"**"+im_resize.getHeight());
        int pixel = 0;

        //long startTime = SystemClock.uptimeMillis();
        for (int i = 0; i < IMAGE_W; ++i) {
            for (int j = 0; j < IMAGE_H; ++j) {
                final int val = intValues_self[pixel++];
                addPixelValue(val);
            }
        }
    }

    protected void addPixelValue(int pixelValue)
    {
        int  temp;
        temp = ((pixelValue >> 16) & 0xFF);
        imgData_self.put((byte)temp);
        temp = ((pixelValue >> 8) & 0xFF);
        imgData_self.put((byte)temp);
        temp = (pixelValue & 0xFF);
        imgData_self.put((byte)temp);
    }

  @Override
  public void enableStatLogging(final boolean logStats) {}

  @Override
  public String getStatString() {
    return "";
  }

  @Override
  public void close() {}




}
