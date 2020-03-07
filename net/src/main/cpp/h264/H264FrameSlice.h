//
// Created by Jarryd.
//

#ifndef VOIPHONE_H264FRAMESLICE_H
#define VOIPHONE_H264FRAMESLICE_H

#include "PlayLoads.h"

typedef unsigned char byte;

class H264FrameSlice{
public:
    static void removeStartCode(byte** nal,int * len);

public:
    static PlayLoads* frameSlice(byte* nal,int len,int max_size);
    static void test(int *len);
};
#endif //VOIPHONE_H264FRAMESLICE_H
