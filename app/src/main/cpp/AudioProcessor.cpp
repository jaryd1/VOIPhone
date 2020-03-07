//
// Created by Jarryd.
//

#include "AudioProcessor.h"

AudioProcessor::AudioProcessor() {

}

AudioProcessor::~AudioProcessor() {
//    if(speexdsp){
//        delete  speexdsp;
//        speexdsp = NULL;
//    }
}

void AudioProcessor::init(int sample_rate,int frame_size ) {
    preprocessState = speex_preprocess_state_init(frame_size,sample_rate);
    int i = 1;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_DENOISE, &i); //降噪 建议1
    i = -15;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_NOISE_SUPPRESS, &i);////设置噪声的dB
    i = 1;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_VAD, &i);////设置噪声的dB
    i = 0;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_AGC, &i);////增益
    i = 24000;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_AGC_LEVEL, &i);
    i = 0;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_DEREVERB, &i);
    float f = 0;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_DEREVERB_DECAY, &f);
    f = 0;
    speex_preprocess_ctl(preprocessState, SPEEX_PREPROCESS_SET_DEREVERB_LEVEL, &f);

//    if(speexdsp){
//        delete speexdsp;
//    }
//    speexdsp = new Speexdsp();
//    speexdsp->init(sample_rate,frame_size);
}

void AudioProcessor::putPlayBack(short *sample) {
//    speexdsp->putPlayBack(sample);
}

void AudioProcessor::cancle(short *input, int size, short *echo) {
//    speexdsp->cancle(input,size,echo);
}

int AudioProcessor::process(short *sample, int size) {
//    return speexdsp->process(sample,size);
    return speex_preprocess_run(preprocessState,sample);
}