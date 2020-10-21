package com.jnitest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.amlogic.nnapi; 

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static String TAG = "nndemo";
	private static final int IMAGE_H = 64;
	private static final int IMAGE_W = 64;
	
    private Button bt_start;
    private TextView tv_result;
    private ImageView show_img;
    String sline = "";
    private static final String imagePath = "images";
    private static String imageRootPath = "";
    private String[] image_list;  

    private Bitmap bitmap;
	private int[] intValues = new int[IMAGE_H*IMAGE_W];
    private boolean flag_running = false;

	private ByteBuffer imgData = null;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_result = (TextView)findViewById(R.id.tv_result);
        bt_start = (Button)findViewById(R.id.bt_start);
        show_img = (ImageView) findViewById(R.id.show_image);

		imgData = ByteBuffer.allocateDirect(IMAGE_H* IMAGE_W* 3);
        imgData.order(ByteOrder.nativeOrder());

        try {
            final File modelsRoot = getBaseContext().getExternalFilesDir("models");
            if (modelsRoot == null) {
                Log.i(TAG, "modelsRoot is null");
            }
            if (!modelsRoot.isDirectory() && !modelsRoot.mkdir()) {
                Log.i(TAG, "Unable to create model root directory: " +
                        modelsRoot.getAbsolutePath());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        image_list = getSdcardImages(imagePath);
        bt_start.setOnClickListener(this);

    }

    private String[] getSdcardImages(String imagePath)
    {
        ArrayList<String> imageList2 = new ArrayList<String>();
        String imageList[] = {};
        try {
            Log.i(TAG, "sdcardImgpath = " + getApplicationContext().getExternalFilesDir(imagePath));
            File sdImgPath = getApplicationContext().getExternalFilesDir(imagePath);
            if(sdImgPath.isDirectory()){
                imageRootPath = sdImgPath.getPath()+File.separator;
                Log.i(TAG, "imageRootPath = " + imageRootPath);
                for(File img: sdImgPath.listFiles()){
                    imageList2.add(img.getAbsolutePath());
                }
                imageList = imageList2.toArray(new String[0]);
                Arrays.sort(imageList);
                Log.i(TAG, "Numbers of images= " + imageList.length);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return imageList;
    }

	protected void addPixelValue(int pixelValue) 
	{
		int  temp;
        temp = ((pixelValue >> 16) & 0xFF);
        imgData.put((byte)temp);
        temp = ((pixelValue >> 8) & 0xFF);
        imgData.put((byte)temp);
		temp = (pixelValue & 0xFF);
        imgData.put((byte)temp);
    }
	public void convertBitmapToByteBuffer(Bitmap bitmap) {
		if (imgData == null) {
		  Log.d(TAG, "imgData == null");
		  return;
		}
		imgData.rewind();
		Bitmap im_resize = Bitmap.createScaledBitmap(bitmap, IMAGE_W, IMAGE_H, false);
		im_resize.getPixels(intValues, 0, im_resize.getWidth(), 0, 0, im_resize.getWidth(), im_resize.getHeight());
		// Convert the image to floating point.
		Log.d(TAG, "imgData "+im_resize.getWidth()+"**"+im_resize.getHeight());
		int pixel = 0;

		//long startTime = SystemClock.uptimeMillis();
		for (int i = 0; i < IMAGE_W; ++i) {
		  for (int j = 0; j < IMAGE_H; ++j) {
			final int val = intValues[pixel++];
			addPixelValue(val);
		  }
		}
  }
  
    void readLineNumber(InputStream sourceFile, int lineNumber)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(sourceFile));
            int lines = 0;
            while (sline != null)
            {
                lines++;
                sline = reader.readLine();
                if((lines - lineNumber) == 0)
                {
                    break;
                }
            }
            reader.close();
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to read line string", e);
        }
    }
    @Override
    public void onClick(View view) {
		int ret;

		byte[] path = new byte[128];
		String pdata = "/storage/emulated/0/Android/data/com.jnitest/files/age.nb";
		nnapi nndemo = new nnapi();
		byte[] udata = new byte[IMAGE_H*IMAGE_W*3];
        float[] out = new float[1024];

		path = pdata.getBytes();
        switch(view.getId()){
            case R.id.bt_start:
                //Log.i(TAG, "start test model: " + test_model);
                try
				{
					if(flag_running == false){
                    bt_start.setText(R.string.bt_text_prepare);
                    flag_running = true;
					Log.i(TAG, "Start Inference");
					ret = nndemo.nn_model_init(path,nnapi.KERAS);
					if(ret != 0)
                    {
                        Log.e(TAG,"nn model init fail");
                        break;
                    }
					bitmap = BitmapFactory.decodeFile(image_list[0]);
					show_img.setImageBitmap(bitmap);
                    Log.d(TAG, "bitmap." + bitmap.getWidth() + "****" + bitmap.getHeight());
                    convertBitmapToByteBuffer(bitmap);
					//imgData.get(udata,0,udata.length);
                    imgData.rewind();
                    imgData.get(udata);

					ret = nndemo.nn_model_inputs_set(nnapi.RGB24_RAW_DATA,0,64*64*3,udata);
                    if(ret != 0)
                    {
                        Log.e(TAG,"nn model set input fail");
                        break;
                    }
                    Log.d(TAG,"Get outputs");
					ret = nndemo.nn_model_outputs_get(out,nnapi.FACE_AGE,nnapi.AML_OUTDATA_FLOAT32);
					Log.d(TAG,"result:"+ret);
					bt_start.setText("process over");
					bt_start.setBackgroundColor(0xff00ff00);

					this.readLineNumber(MainActivity.this.getAssets().open("imagenet_slim_labels.txt"),ret);
					//tv_result.setText("The object is:"+sline);
					//tv_result.setTextColor(0xffff0000);
                    tv_result.setText("The age is:"+ ret );
                    tv_result.setTextColor(0xffff0000);
					Log.i(TAG,"age result : "+ ret);
					Log.i(TAG,"==============================");
					ret = nndemo.nn_model_destroy();
                    if(ret != 0)
                    {
                        Log.e(TAG,"nn model destroy fail");
                        break;
                    }
					}else{
						bt_start.setText(R.string.bt_text_start);
						flag_running = false;
					}
				}
				catch (IOException e) {
                    Log.e(TAG, "Failed to initialize an image classifiertf.", e);
                }
                break;
            default:
                break;
        }
    }
	
}
