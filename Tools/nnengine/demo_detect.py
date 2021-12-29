import struct
import numpy as np
import sys
sys.path.append('aml_nn_engine_38')
from amlnn import *
import cv2
from easydict import EasyDict as edict

img_src = cv2.imread('4164163.bmp')
img_rgb = cv2.cvtColor(img_src, cv2.COLOR_BGR2RGB)
config = edict()

config.input_name = None
config.input_data = img_rgb
config.model_type = 'darknet'

config.linux_path='/media/nn' #pos0
config.nb_path = 'object_detect_a1_C308.nb'

config.input_type = 'rgb'
config.input_w = '416'               
config.input_h ='416'                
config.input_c = '3'               

config.profile = 'none' 
config.run_cyc = '1'  
config.modetype = '1' 
config.device_type = 'linux'

amlnn = AMLNN()
amlnn.init_runtime(config.linux_path, config.nb_path, config.model_type, config.device_type)
amlnn.set_input(config.input_name,config.input_data,config.input_type,config.input_w,config.input_h,config.input_c,config.run_cyc,config.modetype,config.profile)

if config.modetype == '1':
    output=amlnn.inference()
    i = 14 
    det_num = struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0]
    print("object_num:",int(det_num))
    obj_list = []
    
    if int(det_num) != '0':
        for detnum in range(int(det_num)):
            i = detnum*6 + 1 + 14
            classid = int(struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0])
            print("Class id is:",classid )

            i = detnum*6 + 2 + 14
            prob = struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0]
            print("Prob is:",prob )
            
            i = detnum*6 + 3 + 14
            x0 = int(struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0])
           
            
            i = detnum*6 + 4 + 14
            x1 = int(struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0])
            
            
            i = detnum*6 + 5 + 14
            y0 = int(struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0])
        
 
            i = detnum*6 + 6 + 14
            y1 = int(struct.unpack('f',struct.pack('4B',output[4*i],output[4*i+1],output[4*i+2],output[4*i+3]))[0])
            
            print("x0:%s, x1:%s，y0:%s，y10:%s"%(str(x0),str(x1),str(y0),str(y1)))
            box = str(classid)+" "+str(prob)+" "+str(x0)+" "+str(y0)+" "+str(x1)+" "+str(y1)+"\n"
            obj_list.append(box)
            print(box)
            
    print("\n")
elif config.modetype == '99':
    output=amlnn.inference(71825)
    list_out=list(output[(4+10)*4:(71825+10+4)*4])
    lista = []
    out_lenth = (int)((len(list_out) )/4)
    for i in range(out_lenth):
        lista.append(struct.unpack('f',struct.pack('4B',list_out[4*i],list_out[4*i+1],list_out[4*i+2],list_out[4*i+3]))[0])

else:
    raise Exception("Please check the modeltype!")

amlnn.destroy()