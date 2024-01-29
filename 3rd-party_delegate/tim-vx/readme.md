一、结构
└── tim-vx
    ├── include
    ├── lib32
    └── lib64

include文件夹中为Tim-VX编译需要的头文件
lib32文件夹中为32位系统Tim-VX编译需要的so,包括OpenVX.so和timvx.so
lib64文件夹中为64位系统Tim-VX编译需要的so,包括OpenVX.so和timvx.so

二、使用说明
编译步骤：
1. 板端根路径执行 git clone https://github.com/fengyuentau/opencv.git
2. cd opencv
3. git checkout vim4
4. cd ..
5. 将timvx相关库文件及头文件push到板端，假设tim-vx在同级目录, cd tim-vx/lib && ln -sf libtim-vx.so.64 libtim-vx.so （默认libtim-vx.so是libtim-vx.so.32，因此重新链接到64位），库中需要将对应系统来讲lib32/64文件夹改成lib
6. cd ../../opencv
7. cmake -B build -DCMAKE_BUILD_TYPE=RELEASE -DWITH_OPENCL=OFF -DWITH_TIMVX=ON -DTIMVX_INSTALL_DIR=(指到tim-vx的绝对路径比如/home/user/tim-vx) .
8. cmake --build build --target opencv_test_dnn -j6
 
调试步骤：
1. 根路径执行 git clone https://github.com/opencv/opencv_extra.git
2. export OPENCV_TEST_DATA_PATH=/opencv_extra/testdata
3. 运行测试程序：./opencv/build/bin/opencv_test_dnn --gtest_filter="Test_Int8_layers.Reduce/1"      

三、算子支持情况
已支持算子：
vxConvolutionReluPoolingLayer2
vxConvolutionReluPoolingLayer
vxConvolutionReluLayer
vxConvolutionLayer
vxFullyConnectedLayer
vxActivationLayer
vxDeconvolutionLayer
vxPReluLayer
vxConcat2Layer
vxLeakyReluLayer
vxPoolingLayer
vxSoftmaxLayer
vxL2NormalizeLayer
vxTensorReshapeNode
vxSoftmaxLayer2
vxTensorPermuteNode
vxTensorReduceSumNode
vxTensorPadNode
vxTensorReverse
vxL2NormalizeLayer2
vxTensorMeanNode
vxTensorSqueezeNode
vxTensorStrideSliceNode
vxFullyConnectedReluLayer
vxPoolingLayer2
vxTensorTransposeNode

未支持算子：
ROIPooling
BatchNormalization
Reorg
TensorRounding
SVDF
LSHProjection
HashTableLookup
TensorScale
RNN
YUV2RGBScale
GRU
RPN
Lstm