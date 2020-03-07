//
// Created by Jarryd.
//

#ifndef VOIPHONE_AUDIOPROCESSOR_H
#define VOIPHONE_AUDIOPROCESSOR_H


//#include "Speexdsp.h"

#include <speex_preprocess.h>

class AudioProcessor {
private:
//    Speexdsp* speexdsp = NULL;
    SpeexPreprocessState* preprocessState = NULL;

public:
    AudioProcessor();
    ~AudioProcessor();
    void init(int sample_rate,int frame_size);
    void putPlayBack(short*sample);
    int process(short* sample, int size);
    void cancle(short* input,int size,short* echo);
};


#endif //VOIPHONE_AUDIOPROCESSOR_H
