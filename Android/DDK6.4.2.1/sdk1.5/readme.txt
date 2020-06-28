目录结构
	apk : 基于ddk6421的分类demo apk
	demo : demo source code
	package : jpg and nbg files
	so : armeabi-v7a环境下的相关so
	readme.txt

apk 使用说明：
	1、安装apk
	adb install app-debug.apk
	2、放置测试文件
	adb push */inceptionv1.nb  /sdcard/Android/data/com.jnitest/files
	adb push */224x224x3.jpeg  /sdcard/Android/data/com.jnitest/files/images


Demo 使用说明：
	安装APK后，直接点击start，demo运行并输出最后的分类结果。
	主要的注意点如下：
	1).nndemo.nn_model_init((mpath.getAbsolutePath()+File.separator+"inceptionv1.nb").getBytes(),TENSORFLOW);
	该部分加载的为inceptionv1.nb模型，该模型基于tensorflow架构，故type为1.依据上面path，需要预先将该nb push到对应的路径下，如/storage/emulated/0/Android/data/com.jnitest/files/路径，com.jnitest即为实际apk的name。
	对应nnapi.java中的定义为：
	public int nn_model_init(byte[] nbg_model_file, char type)
	
	2) nn_model_inputs_set
	nndemo.nn_model_inputs_set(0,0,224*224*3,udata);
	本demo的数据输入为2242243的图片做分类，故需要将要分类的图片放到相应的path中，如/storage/emulated/0/Android/data/com.jnitest/files/images/，上面所提到的路径为apk安装后默认放置数据文件的地方，是有访问权限的，也可以放在其他地方，只要在app中可以正常访问就行。
	对应nnapi.java中的定义为：
	public int nn_model_inputs_set(int input_type,int input_num,int input_size,byte[] data)
	input_type表示输入数据类型，0表示RGB24_RAW_DATA，请见nn_api.h；input_num为多输入条件下对应的input index值，从0开始。input_size表示输入数据总大小。data表示输入数据buffer。
	
	3）nn_model_outputs_get
	nndemo.nn_model_outputs_get(out,nnapi.CUSTOM_NETWORK,nnapi.AML_OUTDATA_FLOAT32);
	对应nnapi.java中的定义为：
	public int nn_model_outputs_get(float[] outbuf,int netid,int format)
	outbuf表示输出buffer。netid表示模型序号，用户自定义网络序号为CUSTOM_NETWORK。format表示输出数据类型，AML_OUTDATA_FLOAT32表示输出float数据，netid和format详细定义请参考nn_api.h。