#include <dlfcn.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <android/log.h>
#include <cstring>
#include "org_tensorflow_lite_examples_detection_amlogic_nnapi.h"


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
JNIEXPORT jlong JNICALL Java_org_tensorflow_lite_examples_detection_amlogic_nnapi_model_1init
  (JNIEnv *env, jobject obj, jbyteArray in,jchar type)
{
	//jboolean inputCopy = JNI_FALSE;
	//char *pfile = (char*)inData;
    void* context;
    aml_config config;
    jbyte *bytes = env->GetByteArrayElements(in,0);
    int array_len = env->GetArrayLength(in);
    char *pfile = new char[array_len+1];
    memset(pfile,0,array_len+1);
    memcpy(pfile,bytes,array_len);
    pfile[array_len] = 0;
    memset(&config,0,sizeof(aml_config));
    config.modelType = (amlnn_model_type)type;
    config.path = (const char *)pfile;
    LOGD("model path is %s\n",pfile);
    context = aml_module_create(&config);
    delete pfile;
    env->ReleaseByteArrayElements(in,bytes,0);
    return reinterpret_cast<jlong>(context);
}

/*
 * Class:     com_amlogic_nnapi
 * Method:    model_inputs_set
 * Signature: (JIII[B)I
 */
JNIEXPORT jint JNICALL Java_org_tensorflow_lite_examples_detection_amlogic_nnapi_model_1inputs_1set
  (JNIEnv *env, jobject obj, jlong context, jint type, jint num, jint size, jbyteArray data)
{
    int ret = 0;
	jboolean inputCopy = JNI_FALSE;
	jbyte* inData = env->GetByteArrayElements(data, &inputCopy);
    nn_input input;
    input.input_index = num;   //this value is index of input,begin from 0
    input.size = size;
    LOGD("will set input\n");
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
JNIEXPORT jint JNICALL Java_org_tensorflow_lite_examples_detection_amlogic_nnapi_model_1get_1outputinfo
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
JNIEXPORT jfloatArray JNICALL Java_org_tensorflow_lite_examples_detection_amlogic_nnapi_model_1outputs_1get
  (JNIEnv *env, jobject obj, jlong context, jfloatArray out,jint netid,jint format)
{
    LOGD("model_get_output enter");
    aml_module_t modelType;
    int ret = 0;
    float classid;
    float prob;
    nn_output *pout = NULL;
    obj_detect_out_t* obj_detect_out=NULL;
	jboolean outputCopy = JNI_FALSE;
	float *mid = new float[61];
	aml_output_config_t outconfig;

    modelType=OBJECT_DETECT;
    outconfig.mdType = (aml_module_t)netid;
	outconfig.format = (aml_output_format_t)format;
    //outconfig.format = AML_OUTDATA_FLOAT32;
	////////////////////this is for custom netid////////////////////
    //pout = (nn_output *)aml_module_output_get((void*)context,outconfig);

    //obj_detect_out= (obj_detect_out_t*)aml_module_output_get((void*)context,outconfig);
    LOGD("model_get_output enter");
    pout = (nn_output*)aml_module_output_get((void*)context,outconfig);
    LOGD("model_get_output enter_2");
    obj_detect_out = (obj_detect_out_t*)post_process_all_module(modelType,pout);
    LOGD("model_get_output enter_3");
    mid[0]= obj_detect_out->detNum;


    for (int i =0;i < obj_detect_out->detNum;i++) {
        mid[6 * i + 1] = obj_detect_out->pBox[i].objectClass;
        mid[6 * i + 2] = obj_detect_out->pBox[i].score;
        //printf("class:%s,prob:%.0f%%\n",coco_names[classid],prob);
        double left = 0, right = 0, top = 0, bot = 0;
        //left  = (float)(obj_detect_out->pBox[i].x-obj_detect_out->pBox[i].w/2.);
        mid[6 * i + 4] = (float) (obj_detect_out->pBox[i].x - obj_detect_out->pBox[i].w / 2.);
        //right = (float)(obj_detect_out->pBox[i].x+obj_detect_out->pBox[i].w/2.);
        mid[6 * i + 6] = (float) (obj_detect_out->pBox[i].x + obj_detect_out->pBox[i].w / 2.);
        //top   = (float)(obj_detect_out->pBox[i].y-obj_detect_out->pBox[i].h/2.);
        mid[6 * i + 3] = (float) (obj_detect_out->pBox[i].y - obj_detect_out->pBox[i].h / 2.);
        //bot   = (float)(obj_detect_out->pBox[i].y+obj_detect_out->pBox[i].h/2.);
        mid[6 * i + 5] = (float) (obj_detect_out->pBox[i].y + obj_detect_out->pBox[i].h / 2.);
    }
    //jfloat* jout = env->GetFloatArrayElements(out, &outputCopy);
    /*if(pout == NULL)
    {
        LOGD("aml_module_output_get error\n");
        return 1;
    }
    */

    jfloatArray out_1=env->NewFloatArray(61);

    LOGD("object_num:%d\n", obj_detect_out->detNum);
    LOGD("object_x:%f\n", obj_detect_out->pBox->x);
    LOGD("object_y:%f\n", obj_detect_out->pBox->y);
    LOGD("object_x:%f\n", obj_detect_out->pBox->w);
    LOGD("object_y:%f\n", obj_detect_out->pBox->h);
    LOGD("object_class:%f\n", obj_detect_out->pBox->objectClass);
    LOGD("object_score:%f\n", obj_detect_out->pBox->score);
    env -> SetFloatArrayRegion(out_1,0,61,mid);


    return out_1;


}

/*
 * Class:     com_amlogic_nnapi
 * Method:    model_destroy
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_tensorflow_lite_examples_detection_amlogic_nnapi_model_1destroy
  (JNIEnv *env, jobject obj, jlong context)
{
    int ret = 0;
    LOGD("model_destroy enter");
    ret = aml_module_destroy((void*)context);
	return (jint)ret;
}
