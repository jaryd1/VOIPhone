//
// Created by Jarryd.
//

#ifndef VOIPHONE_SPEEXDSP_H
#define VOIPHONE_SPEEXDSP_H

#include <speex_echo.h>
#include "SpeexConfig.h"


class Speexdsp{
private:
    SpeexConfig* config;
    SpeexPreprocessState* processor;
    SpeexEchoState* echoState;
public:
    Speexdsp();
    ~Speexdsp();
    void init(int sample_rate,int frame_size);
    void putPlayBack(short* playback);
    int process(short* samples,int frame_size);
    void cancle(short* input,int frame_size,short*echo);
};

#endif //VOIPHONE_SPEEXDSP_H
