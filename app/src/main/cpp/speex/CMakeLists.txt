
cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DFLOATING_POINT -DUSE_KISS_FFT -DEXPORT=\"\" -DHAVE_STDINT_H -UHAVE_CONFIG_H")

aux_source_directory(${CMAKE_CURRENT_SOURCE_DIR}/libspeexdsp SRC)
add_library(
        speex
        SHARED
        ${SRC}
)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

target_link_libraries(
        speex
)