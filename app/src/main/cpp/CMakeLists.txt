
cmake_minimum_required(VERSION 3.4.1)

add_library(
    voip
    SHARED
    SpeexConfig.cpp
    Speexdsp.cpp
    AudioProcessor.cpp
        com_jaryd_voiphone_AudioProcessor.cpp
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/speex/include/speex)

add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/speex)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries(
    voip
        speex
    ${log-lib}
)