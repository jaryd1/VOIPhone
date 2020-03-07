//
// Created by Jarryd.
//
#include <assert.h>
#include "Speexdsp.h"

Speexdsp::Speexdsp() {
    config = NULL;
    processor = NULL;
    echoState = NULL;
}

Speexdsp::~Speexdsp() {
    SpeexConfig::destroySpeexProcessor(processor);
    if(config){
        delete  config;
    }
    if(processor){
        delete processor;
    }
}



void Speexdsp::init(int sample_rate, int frame_size) {
    SpeexConfig::initSpeexProcessor(sample_rate,frame_size,&processor,&config);
    assert(config);
    echoState = speex_echo_state_init(frame_size,frame_size*5);
    assert(echoState);
//    assert(speex_echo_ctl(echoState,SPEEX_ECHO_SET_SAMPLING_RATE,&sample_rate) == 0);
//    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_ECHO_STATE,echoState) == 0);
}

void Speexdsp::putPlayBack(short *playback) {
    speex_echo_playback(echoState,playback);
}

void Speexdsp::cancle(short *input, int frame_size, short *echo) {

    speex_echo_cancellation(echoState,input,echo,input);
}

int Speexdsp::process(short *samples,int frame_size) {
    return speex_preprocess_run(processor,samples);

}
