
cmake_minimum_required(VERSION 3.4.1)

add_library(
    opusutils
    SHARED
    com_jaryd_opuscodec_OpusUtils.cpp
    OpusUtils.cpp
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/opus/include)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/opus)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries(
    opusutils
    opus
    ${log-lib}
)