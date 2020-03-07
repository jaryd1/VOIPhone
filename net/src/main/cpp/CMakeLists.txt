
cmake_minimum_required(VERSION 3.4.1)

add_library(
        rtp
        SHARED

        com_jaryd_net_RTPManager.cpp
        RTPManager.cpp
        JNICallBack.cpp
        BaseRTPSession.cpp
        RTPAudioSession.cpp
        RTPVideoSession.cpp
        h264/H264FrameSlice.cpp
        h264/PlayLoads.cpp
        h264/NALUHeader.cpp
        h264/FUA_header.cpp
        h264/FUA_indicator.cpp
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/h264)

add_library(
        jrtp
        SHARED
        IMPORTED
)

set_target_properties(
        jrtp
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}./../JniLibs/${CMAKE_ANDROID_ARCH_ABI}/libjrtp.so
)

add_library(
        jthread
        SHARED
        IMPORTED
)

set_target_properties(
        jthread
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}./../JniLibs/${CMAKE_ANDROID_ARCH_ABI}/libjthread.so
)



target_link_libraries(
        rtp
        jrtp
        jthread
        log
)