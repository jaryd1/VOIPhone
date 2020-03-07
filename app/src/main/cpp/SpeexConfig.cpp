//
// Created by Jarryd.
//
#include <assert.h>
#include "SpeexConfig.h"

int SpeexConfig::sample_rate = 0;
int SpeexConfig::frame_size = 0;

SpeexConfig::SpeexConfig() {
    denoise_state = false;
    agc_state = false;
    vad_state = false;
    dereverb_state = false;
    noise_suppress = -15;
    agc_level = 8000;
    dereverb_level = 0;
    dereverb_decay = 0;
}


void SpeexConfig::updateConfig(SpeexPreprocessState *processor) {
    assert(processor);

    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_DENOISE,&denoise_state) ==0);
    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_AGC,&agc_state) ==0);
    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_AGC_LEVEL,&agc_level) ==0);
    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_DEREVERB,&dereverb_state) ==0);
    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_DEREVERB_LEVEL,&dereverb_level) ==0);
    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_DEREVERB_DECAY,&dereverb_decay)==0);
    assert(speex_preprocess_ctl(processor,SPEEX_PREPROCESS_SET_NOISE_SUPPRESS,&noise_suppress)==0);
}

void SpeexConfig::destroySpeexProcessor(SpeexPreprocessState *processor) {
    if(processor){
        speex_preprocess_state_destroy(processor);
    }
}

void SpeexConfig::initSpeexProcessor(int sample_rate, int frame_size,
                                     SpeexPreprocessState **processor, SpeexConfig **config) {
    if(*processor){
        speex_preprocess_state_destroy(*processor);
    }

    *processor = speex_preprocess_state_init(frame_size,sample_rate);

    assert(processor);

    if(!*config){
        *config = new SpeexConfig();
    }

    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_DENOISE,(*config)->getDenoiseState()) ==0);
    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_AGC,(*config)->getAgcState()) ==0);
    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_AGC_LEVEL,(*config)->getAgcLevel()) ==0);
    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_DEREVERB,(*config)->getDereverbState()) ==0);
    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_DEREVERB_LEVEL,(*config)->getDereverbLevel()) ==0);
    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_DEREVERB_DECAY,(*config)->getDereverbDecay())==0);
    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_NOISE_SUPPRESS,(*config)->getNoiseSuppress())==0);
    assert(speex_preprocess_ctl(*processor,SPEEX_PREPROCESS_SET_VAD,(*config)->getvadState())==0);
    SpeexConfig::sample_rate = sample_rate;
    SpeexConfig::frame_size = frame_size;
}

int SpeexConfig::verifyConfig(int frame_size, SpeexPreprocessState **processor,
                              SpeexConfig **config) {
    if(SpeexConfig::frame_size != frame_size){
        initSpeexProcessor(SpeexConfig::sample_rate,frame_size,processor,config);
        return 1;
    }
    return 0;
}
