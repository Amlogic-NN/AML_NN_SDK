#include <dlfcn.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <android/log.h>
#include <cstring>
#include "com_amlogic_nnapi.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "amlogic_nnapi", __VA_ARGS__)
/*
typedef void* (*pInit_t)(const char * nbg_model_file, amlnn_model_type type);
typedef nn_query* (*pQuery_t)(void* context, amlnn_query_cmd cmd,unsigned int number);
typedef int (*pSetInput_t)(void* context,nn_input *pInput);
typedef int (*pRun_t)(void* context, int extend);
typedef nn_output* (*pSetOut_t)(void* context);
typedef int (*pDestroy_t)(void* context);
static pInit_t pInitfn;
static pQuery_t pQueryfn;
static pSetInput_t pSetInputfn;
static pRun_t  pRunfn;
static pSetOut_t pSetOutfn;
static pDestroy_t pDestroyfn;
static void *handle = NULL;*/
/*
 * Class:     com_amlogic_nnapi
 * Method:    model_init
 * Signature: ([BCC)J
 */
//extern "C" int getResnetResult(void);
JNIEXPORT jlong JNICALL Java_com_amlogic_nnapi_model_1init
  (JNIEnv *env, jobject obj, jbyteArray in,jchar type)
{
	//jboolean inputCopy = JNI_FALSE;
  	//jbyte* inData = env->GetByteArrayElements(in, &inputCopy);
	//char *pfile = (char*)inData;
	char *pfile = "/storage/emulated/0/Android/data/com.jnitest/files/inceptionv1.nb";
	void* context;
	aml_config config;
	memset(&config,0,sizeof(aml_config));
	config.modelType = (amlnn_model_type)type;
	config.path = (const char *)pfile;
	LOGD("model name is %s\n",pfile);
	context = aml_module_create(&config);
	return reinterpret_cast<jlong>(context);
}

/*
 * Class:     com_amlogic_nnapi
 * Method:    model_inputs_set
 * Signature: (JIII[B)I
 */
JNIEXPORT jint JNICALL Java_com_amlogic_nnapi_model_1inputs_1set
  (JNIEnv *env, jobject obj, jlong context, jint type, jint num, jint size, jbyteArray data)
{
    int ret = 0;
	jboolean inputCopy = JNI_FALSE;
  	jbyte* inData = env->GetByteArrayElements(data, &inputCopy);
    nn_input input;
    input.input_index = num;   //this value is index of input,begin from 0
    input.size = size;
    //LOGD("will set input\n");
    input.input = (unsigned char *)inData;
    input.input_type = (amlnn_input_type)type;
    ret = aml_module_input_set((void*)context,&input);
	//test_model((long)context,(char*)inData);
	env->ReleaseByteArrayElements(data, inData, JNI_ABORT);
	return (jint)ret;
}



/*
 * Class:     com_amlogic_nnapi
 * Method:    model_get_outputinfo
 * Signature: (J[B)I
 * the output info can get from amlnn_outputs_get,so it maybe not needed
 */
JNIEXPORT jint JNICALL Java_com_amlogic_nnapi_model_1get_1outputinfo
  (JNIEnv *env, jobject obj, jlong context, jbyteArray out)
{
    int ret = 0;
	//LOGD("model_get_outinfo enter");
	return (jint)ret;
}
/*
 * this is util function for display top-5 value
 *
 * author zhouxingwei for jni test 2019
 */
unsigned int  print_top5(float *buf,unsigned int num)
{
    int i = 0,j = 0;
    unsigned int MaxClass[5]={0};
    float fMaxProb[5]={0.0};

    float *pfMaxProb = fMaxProb;
    unsigned int  *pMaxClass = MaxClass;
    for (j = 0; j < 5; j++)
    {
        for (i=0; i<num; i++)
        {
            if ((i == *(pMaxClass+0)) || (i == *(pMaxClass+1)) || (i == *(pMaxClass+2)) ||
                (i == *(pMaxClass+3)) || (i == *(pMaxClass+4)))
            {
                continue;
            }

            if (buf[i] > *(pfMaxProb+j))
            {
                *(pfMaxProb+j) = buf[i];
                *(pMaxClass+j) = i;
            }
        }
    }
    for(i=0; i<5; i++)
    {
        LOGD("%3d: %8.6f\n", MaxClass[i], fMaxProb[i]);
    }
	return MaxClass[0];
}
/*
 * Class:     com_amlogic_nnapi
 * Method:    model_outputs_get
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL Java_com_amlogic_nnapi_model_1outputs_1get
  (JNIEnv *env, jobject obj, jlong context, jfloatArray out,jint netid,jint format)
{
    int ret = 0;
    nn_output *pout = NULL;
	jboolean outputCopy = JNI_FALSE;
	aml_output_config_t outconfig;
	
	outconfig.mdType = (aml_module_t)netid;
	outconfig.format = (aml_output_format_t)format;
	////////////////////this is for custom netid////////////////////
    pout = (nn_output *)aml_module_output_get((void*)context,outconfig);
	
	jfloat* jout = env->GetFloatArrayElements(out, &outputCopy);
    if(pout == NULL)
    {
        LOGD("aml_module_output_get error\n");
        return 1;
    }
    if(pout->out[0].param->data_format == NN_BUFFER_FORMAT_FP32)
    {
        ret = print_top5((float*)pout->out[0].buf,pout->out[0].size/sizeof(float)); //just for test top5
		memcpy(jout,pout->out[0].buf,pout->out[0].size);
    }
	env->ReleaseFloatArrayElements(out, jout, 0);
	return (jint)ret;
}

/*
 * Class:     com_amlogic_nnapi
 * Method:    model_destroy
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_amlogic_nnapi_model_1destroy
  (JNIEnv *env, jobject obj, jlong context)
{
    int ret = 0;
    LOGD("model_destroy enter");
    ret = aml_module_destroy((void*)context);
	return (jint)ret;
}