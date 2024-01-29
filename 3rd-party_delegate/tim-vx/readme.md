一、结构
└── tim-vx
    ├── include
    ├── lib32
    └── lib64

include文件夹中为Tim-VX编译需要的头文件
lib32文件夹中为32位系统Tim-VX编译需要的so,包括OpenVX.so和timvx.so
lib64文件夹中为64位系统Tim-VX编译需要的so,包括OpenVX.so和timvx.so

二、算子支持情况
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