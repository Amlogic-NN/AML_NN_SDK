cmake_minimum_required(VERSION 3.5.1)
SET(CMAKE_SYSTEM_NAME Linux)

SET(GCC_PATH /toolchain_path/gcc/linux-x86)

PROJECT(AML_NN_SDK)
include_directories( ../include)


SET(COMPILER ${GCC_PATH}/aarch64/gcc-linaro-6.3.1-2017.02-x86_64_aarch64-linux-gnu/bin/)
SET(CMAKE_C_COMPILER ${COMPILER}/aarch64-linux-gnu-gcc)
SET(CMAKE_CXX_COMPILER ${COMPILER}/aarch64-linux-gnu-g++)
SET(LINK_DIR ../lib64 ./)

link_directories(${LINK_DIR})
aux_source_directory(. SRC_LIST)
link_libraries(nnsdk  libjpeg.a )


ADD_EXECUTABLE(sdkdemo64 ${SRC_LIST})
target_link_libraries(sdkdemo64 -lpthread -lm -ldl)


