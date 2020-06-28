/*=========================================================================
*   amlogic android native nn jni interface
*   
*   Neural Network appliction network interface file
*
*   Date: 2019.9
=========================================================================*/
package com.amlogic;
import android.util.Log;

public class nnapi {
	
	//support the input type
	public static final char RGB24_RAW_DATA = 0;
	public static final char TENSOR_RAW_DATA = 1;
	public static final char QTENSOR_RAW_DATA = 2;
	public static final char BINARY_RAW_DATA = 3;
	
	//support the network framework type
	public static final char CAFFE    = 0;
	public static final char TENSORFLOW = 1;
    public static final char TENSORFLOWLITE = 2;
    public static final char DARKNET = 3;
    public static final char ONNX = 4;
	public static final char KERAS = 5;
	public static final char PYTORCH = 6;
	
	//support application network
	public static final char IMAGE_CLASSIFY      = 0;
    public static final char OBJECT_DETECT       = 1;    /// object_detection
    public static final char FACE_DETECTION      = 2;    ///
    public static final char FACE_LANDMARK_5     = 3;    /// landmark 5 points
    public static final char FACE_LANDMARK_68    = 4;    ///< Face Landmark (68 KeyPoints)
    public static final char FACE_RECOGNIZE      = 5;    ///< Face Recognition
    public static final char FACE_COMPARISION    = 6;    ///< Face Recognition
    public static final char FACE_AGE            = 7;
    public static final char FACE_GENDER         = 8;
    public static final char FACE_EMOTION        = 9;
    public static final char BODY_POSE           = 10;    ///< Body Pose
    public static final char FINGER_POSE         = 11;    ///< Finger Landmark(21 KeyPoint)
    public static final char HEAD_DETECTION      = 12;    ///< Head Detection
    public static final char CARPLATE_DETECTION  = 13;    ///< Car Plate Detection
    public static final char CARPLATE_RECOG      = 14;   ///< Car Plate Recognition
    public static final char TEXT_DETECTION      = 15;
    public static final char IMAGE_SR            = 16;
    public static final char IMAGE_SEGMENTATION  = 17;
    public static final char CUSTOM_NETWORK      = 99;
	
	//support output config format 
	public static final char AML_OUTDATA_FLOAT32  = 0;  //the output data is float, this is the real value
    public static final char AML_OUTDATA_RAW      = 1;  //rawdata output, should convert according to outinfo as zeropoint\mean 
	static {
         System.loadLibrary("aml_nnapi");
    }
	private long context = 0;
	
	public int nn_model_init(byte[] nbg_model_file, char type)
	{
		//Log.i("AML-NNAPI","model path:%s",context().getExternalFilesDir(dir).getAbsolutePath() );
		int ret = 0;
		this.context = model_init(nbg_model_file,type);
		if(this.context == 0)
		{
			ret = 1;
		}
		return ret;
	}
	
	public int nn_model_inputs_set(int input_type,int input_num,int input_size,byte[] data)
	{
		int ret;
		ret = model_inputs_set(this.context,input_type,input_num,input_size,data);
		return ret;
	}
	

	public int nn_model_get_outputinfo(byte[] info)
	{
		int ret;
		ret = model_get_outputinfo(this.context,info);
		return ret;		
	}
	
	public int nn_model_outputs_get(float[] outbuf,int netid,int format)
	{
		int ret;
	    ret = model_outputs_get(this.context,outbuf,CUSTOM_NETWORK,0);
		return ret;
	}
	
	public int nn_model_destroy()
	{
		int ret;
	    ret = model_destroy(this.context);
		return ret;
	}
	
	
	private native long model_init(byte[] nbg_model_file, char type);
	private native int model_inputs_set(long context,int input_type,int input_num,int input_size,byte[] data);
	private native int model_get_outputinfo(long context,byte[] info);
	private native int model_outputs_get(long context,float[] outbuf,int netid,int format);
	private native int model_destroy(long context);
}