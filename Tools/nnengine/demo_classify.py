import struct
import numpy as np
import sys
sys.path.append('aml_nn_engine_38')
from amlnn import *
from easydict import EasyDict as edict

config = edict()

config.input_name = '2242243.jpeg'
config.input_data = None

config.linux_path='/media/nn'             # board workspace
config.nb_path = 'image_classify_a1_C308.nb'   # nbg path
config.model_type = 'tensorflow'          # framework type

config.input_type = 'rgb'       # rgb tensor raw
config.input_w = '224'          
config.input_h ='224'           
config.input_c = '3'            

config.profile = '0'             
config.run_cyc = '1'            # loop time
config.modetype = '0'           # custom network set ‘99’
config.device_type = 'linux'  # set ‘linux’ or 'android'

amlnn = AMLNN()
amlnn.init_runtime(config.linux_path, config.nb_path, config.model_type, config.device_type)
amlnn.set_input(config.input_name,config.input_data,config.input_type,config.input_w,config.input_h,config.input_c,config.run_cyc,config.modetype,config.profile)
output=amlnn.inference()

if  config.modetype == '0':
    for i in range(14,19):
        print("pc received top",i-4," is: ",struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0])
    for i in range(4,7):
        print("profile",i-4," is: ",struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0])
    #profile[0]:runtime (ms) profile[1]:fps  profile[2]:ddr bandwitch
    print("\n")
elif config.modetype == '99':
    list_out=list(output[(4+10)*4:])
    lista = []
    out_lenth = (int)((len(list_out))/4)
    for i in range(out_lenth):
        lista.append(struct.unpack('f',struct.pack('4B',list_out[4*i],list_out[4*i+1],list_out[4*i+2],list_out[4*i+3]))[0])

    pMaxClass = [0] * 5
    pfMaxProb = [0] * 5
    for i in range(5):
        for j in range(len(lista)):
            if j == pMaxClass[0] or j == pMaxClass[1] or j == pMaxClass[2] or j == pMaxClass[3] or j == pMaxClass[4]:
                continue
            if lista[j] > pfMaxProb[i]:
                pfMaxProb[i] = lista[j]
                pMaxClass[i] = j
    print(pfMaxProb)
    print(pMaxClass)

else:
    raise Exception("Please check the modeltype!")

amlnn.destroy()