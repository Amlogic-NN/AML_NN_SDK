使用简介：
1. 平台运行
    1.1 板端运行前设置环境变量
        export VIV_VX_PROFILE=1
        export VIV_VX_DEBUG_LEVEL=1
        export CNN_PERF=1
        export VIV_VX_SAVE_PATH=test.txt   #设置保存打印信息路径
    1.2 运行export.data case
        例如：
            ./inceptionv1 inceptionv1.export.data input.tensor
        每层信息打印会保存在之前设置的路径test.txt中
    
    
2. PC可视化
    2.1 将板端运行时保存的test.txt和可视化脚本放置在python3环境下
    2.2 执行脚本
        python3 show_layer_info.py -i test.txt -c 0xa1
        其中-i 为输入的文本文件路径，-c为对应的平台序号
            A311D  --- 0x88
            S905D3 --- 0x99
            C305X  --- 0xbe
            C308X  --- 0xa1
            AB301  --- 0xb9
            AR301  --- 0xe8
    2.3 保存图片和对应结果文件
        执行脚本后保存DDR_BandWidth.png，Runtime.png分别描述带宽、运行时间的图像，而且会生成results.txt保存相关信息
注：需使用Linux/runtime_sharelib路径下的动态库