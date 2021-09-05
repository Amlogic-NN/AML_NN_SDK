import re
from matplotlib import pyplot as plt
import matplotlib
import sys, getopt
import os
import math

def draw_result(ddr_r,ddr_w,cycle_r,colors):
    plt.switch_backend('agg')
    path_bandwith = "DDR_BandWidth"
    path_cycle = "Runtime"

    max_cycle=max(cycle_r)
    max_bandwitch_r=max(ddr_r)
    max_bandwitch_w=max(ddr_w)
    ddr_scale=math.ceil((len(ddr_w)/50))
    cycle_scale=math.ceil((len(cycle_r)/50))

    rects1 = plt.bar(range(len(ddr_w)), height=ddr_w, width=0.45, alpha=0.8, color='lightgreen', label="bw_write")
    rects2 = plt.bar([i + 0.5 for i in range(len(ddr_w))], height=ddr_r, width=0.45, color='deepskyblue', label="bw_read")
    plt.ylabel("ddr_bandwith(MByte)")

    plt.xticks([index + 0.01 for index in range(0,len(ddr_w),ddr_scale)], [i for i in range(0,len(ddr_w),ddr_scale)])
    
    plt.xticks(size=8,fontsize=3.5)
    plt.xlabel("layer id")
    plt.title("Layer DDR_BandWidth")
    plt.legend()
    for rect in rects1:
        height = rect.get_height()
        if (len(ddr_w) > 30 and height > (max_bandwitch_w*ddr_scale/8)) or (len(ddr_w) <= 30 and height > 0.1):
            plt.text(rect.get_x() + rect.get_width() / 2 , height, str(round(height,ddr_scale)), ha="center", va="bottom",fontsize=4)
    for rect in rects2:
        height = rect.get_height()
        if (len(ddr_w) > 30 and height >(max_bandwitch_r*ddr_scale/8)) or (len(ddr_w) <= 30 and height > 0.1):
            plt.text(rect.get_x() + rect.get_width() / 2 , height, str(round(height,ddr_scale)), ha="center", va="bottom",fontsize=4)
    plt.savefig(path_bandwith,dpi = 600)  

    fig2=plt.figure(2)
    rects1 = plt.bar(range(len(cycle_r)), height=cycle_r, width=0.45, alpha=0.8, color=colors)
    plt.bar(0,0,color="lightgreen",label="NN")
    plt.bar(0,0,color="deepskyblue",label="TP")
    plt.bar(0,0,color="mediumslateblue",label="PPU")
    plt.ylabel("run time(us)")
    plt.xticks([index + 0.01 for index in range(0,len(cycle_r),cycle_scale)], [i for i in range(0,len(cycle_r),cycle_scale)])
    plt.xticks(size=15,fontsize=3.5)
    plt.xlabel("layer id")
    plt.title("Layer Run Time")
    plt.legend()
    for rect in rects1:
        height = rect.get_height()
        if (len(cycle_r) > 30 and height >(max_cycle*cycle_scale/8)) or (len(cycle_r) <= 30 and height > 0.1):
            plt.text(rect.get_x() + rect.get_width() / 2 , height+0.3, str(round(height,0)), ha="center", va="bottom",fontsize=4)
    plt.savefig(path_cycle,dpi = 600)

def calculate(log_results,layer_num,chip_freq,chip_nn,chip_tp,chip_ppu,ddr_num):  
    bw_cycle=log_results[layer_num-ddr_num:]
    maccs=log_results[:layer_num-ddr_num]
    i=0
    k=1
    layer_type,ddr_bandwitch,ddr_bandwitch_w,ddr_bandwitch_r,cycles_t,cycles_r,cycles_i,cycles,flops,color,flops_results,utilize=[],[],[],[],[],[],[],[],[],[],[],[]
    total_flops=0

    flops_num=layer_num-ddr_num

    while i < flops_num:
        if (i+1)<flops_num and maccs[i][1] == maccs[i+1][1]:
            while (i+k+1)<flops_num and maccs[i][1] == maccs[i+k][1]:
                k=k+1
            for j in range(1,6,2):
                if maccs[i][5] == "NN" or maccs[i+k][5] == "NN" and j == 5:
                    layer_type.append("NN")
                else:
                    layer_type.append(maccs[i][j])
            for j in range(7,16,2):
                flops.append((int)(maccs[i][j]))
            for j in range(17,46,2):
                flops.append((int)(maccs[i+k][j]))
            i=i+k-1
            k=1
        else:
            for j in range(1,6,2):
                layer_type.append(maccs[i][j])
            for j in range(7,46,2):
                flops.append((int)(maccs[i][j]))
        i=i+1

    i=0
    layer_id=[]  
    
    
    while i < ddr_num:
        if bw_cycle[i][1] in layer_id:
            if bw_cycle[i][7] == 'unkown':
                ddr_bandwitch_r[layer_id.index(bw_cycle[i][1])]+=round((float)(bw_cycle[i][11]),1)
                ddr_bandwitch_w[layer_id.index(bw_cycle[i][1])]+=round((float)(bw_cycle[i][13]),1)                
                cycles_t[layer_id.index(bw_cycle[i][1])]+=(int)(bw_cycle[i][17])
                cycles_i[layer_id.index(bw_cycle[i][1])]+=(int)(bw_cycle[i][19])
                cycles_r[layer_id.index(bw_cycle[i][1])]+=(int)(((int)(bw_cycle[i][21])/chip_freq))
            else:
                ddr_bandwitch_r[layer_id.index(bw_cycle[i][1])]+=round((float)(bw_cycle[i][11]),1)
                ddr_bandwitch_w[layer_id.index(bw_cycle[i][1])]+=round((float)(bw_cycle[i][13]),1)
                cycles_t[layer_id.index(bw_cycle[i][1])]+=(int)(bw_cycle[i][15])
                cycles_i[layer_id.index(bw_cycle[i][1])]+=(int)(bw_cycle[i][17])
                cycles_r[layer_id.index(bw_cycle[i][1])]+=(int)(((int)(bw_cycle[i][19])/chip_freq))
        else:
            if bw_cycle[i][7] == 'unkown':
                ddr_bandwitch_r.append(round((float)(bw_cycle[i][11]),1))
                ddr_bandwitch_w.append(round((float)(bw_cycle[i][13]),1))
                cycles_t.append((int)(bw_cycle[i][17]))
                cycles_i.append((int)(bw_cycle[i][19]))
                cycles_r.append((int)((int)(bw_cycle[i][21])/chip_freq))
                layer_id.append(bw_cycle[i][1])

            else:
                ddr_bandwitch_r.append(round((float)(bw_cycle[i][9]),1))
                ddr_bandwitch_w.append(round((float)(bw_cycle[i][11]),1))
                cycles_t.append((int)(bw_cycle[i][15]))
                cycles_i.append((int)(bw_cycle[i][17]))
                cycles_r.append((int)((int)(bw_cycle[i][19])/chip_freq))
                layer_id.append(bw_cycle[i][1])                     
        i=i+1
        
    i=0
    for i in range((int)(len(layer_type)/3)):
        if layer_type[3*i+2] == "NN":
            color.append("lightgreen")
        elif layer_type[3*i+2] == "TP":
            color.append("deepskyblue")
        elif layer_type[3*i+2] == "SH":
            color.append("mediumslateblue")

    draw_result(ddr_bandwitch_r,ddr_bandwitch_w,cycles_r,color)
    save_path="results.txt"
    if os.path.isfile(save_path):
        os.remove(save_path)
    with open(save_path, "a") as f:
        for i in range(len(ddr_bandwitch_r)):
            f.write('id:{:} layer_id:{:} layer_name:{:} layer_target:{:} input_size:({:} {:} {:} {:}) output_size:({:} {:} {:} {:}) kernel_size:({:} {:} {:} {:}) pad_size:({:} {:}) pool_size:({:} {:} {:} {:}) ddr_bandwitch_r:{:} ddr_bandwitch_w:{:} cycles_t:{:} cycles_i:{:} run_time:{:}us  \n'.format(i, layer_type[3*i],layer_type[3*i+1],layer_type[3*i+2],flops[20*i],flops[20*i+1],flops[20*i+2],flops[20*i+3],flops[20*i+5],flops[20*i+6],flops[20*i+7],flops[20*i+8],flops[20*i+10],flops[20*i+11],flops[20*i+12],flops[20*i+13],flops[20*i+14],flops[20*i+15],flops[20*i+16],flops[20*i+17],flops[20*i+18],flops[20*i+19],ddr_bandwitch_r[i],ddr_bandwitch_w[i],cycles_t[i],cycles_i[i],cycles_r[i]))
        f.close()

    



def main(argv):
    inputfile = ''
    chip_id = ''
    chip_freq,chip_nn,chip_tp,chip_ppu=0,0,0,0
    num=0
    ddr_num=0
    log_results = []

    try:
        opts, args = getopt.getopt(argv,"hi:c:",["ifile=","cplatform="])
    except getopt.GetoptError:
        print('test.py -i <inputfile> -c <platform>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print('test.py -i <inputfile> -c <platform>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
        elif opt in ("-c", "--cplatform"):
            chip_id = arg
    print('input files:', inputfile)
    print('platform:', chip_id)
    patch_file=open(inputfile,'r')
   
    if chip_id == '0x88':
        chip_freq=800
        chip_nn=5*1000*1000*1000*1000
        chip_tp=800*1000*1000*4
        chip_ppu=6.4*1000*1000*1000 
    elif chip_id == '0x99':
        chip_freq=800
        chip_nn=1.25*1000*1000*1000*1000
        chip_tp=800*1000*1000*8
        chip_ppu=6.4*1000*1000*1000    
    elif chip_id == '0xa1':
        chip_freq=667
        chip_nn=4*1000*1000*1000*1000
        chip_tp=800*1000*1000*6
        chip_ppu=6.4*1000*1000*1000*667/800   
    elif chip_id == '0xb9':
        chip_freq=800
        chip_nn=1.25*1000*1000*1000*1000
        chip_tp=800*1000*1000*1
        chip_ppu=6.4*1000*1000*1000     
    elif chip_id == '0xbe':
        chip_freq=667
        chip_nn=2*1000*1000*1000*1000
        chip_tp=800*1000*1000*4
        chip_ppu=6.4*1000*1000*1000*667/800     
    elif chip_id == '0xe8':
        chip_freq=850
        chip_nn=1.25*1000*1000*1000*1000
        chip_tp=800*1000*1000*1
        chip_ppu=6.4*1000*1000*1000*850/800
    else:
        print("please set the right platform id \n")
    for line in open(inputfile):
        line=patch_file.readline()
        tmp=line.strip().split(" ") 
        log_results.append(tmp) 
        num=num+1
        if re.search('operation',line) is not None:
            ddr_num=ddr_num+1

    calculate(log_results,num,chip_freq,chip_nn,chip_tp,chip_ppu,ddr_num)

if __name__ == "__main__":
   main(sys.argv[1:])