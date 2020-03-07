//
// Created by Jarryd.
//

#ifndef VOIPHONE_SPEEXCONFIG_H
#define VOIPHONE_SPEEXCONFIG_H

#include <speex_preprocess.h>

class SpeexConfig{
private:
    static int sample_rate,frame_size;
    bool denoise_state,agc_state,dereverb_state,vad_state;
    int agc_level,dereverb_level,dereverb_decay,noise_suppress;
public:
    SpeexConfig();
    inline bool* getDenoiseState(){
        return &denoise_state;
    }

    inline void setDenoiseState(bool denoiseState) {
        denoise_state = denoiseState;
    }

    inline bool* getAgcState() {
        return &agc_state;
    }

    inline void setAgcState(bool agcState) {
        agc_state = agcState;
    }

    inline bool* getvadState() {
        return &vad_state;
    }

    inline void setVadState(bool vadstate) {
        vad_state = vadstate;
    }

    inline bool* getDereverbState() {
        return &dereverb_state;
    }

    inline void setDereverbState(bool dereverbState) {
        dereverb_state = dereverbState;
    }

    inline int* getAgcLevel() {
        return &agc_level;
    }

    inline void setAgcLevel(int agcLever) {
        agc_level = agcLever;
    }

    inline int* getDereverbLevel(){
        return &dereverb_level;
    }

    inline void setDereverbLevel(int dereverbLever) {
        dereverb_level = dereverbLever;
    }

    inline int* getDereverbDecay(){
        return &dereverb_decay;
    }

    inline void setDereverbDecay(int dereverbDecay) {
        dereverb_decay = dereverbDecay;
    }

    inline int* getNoiseSuppress(){
        return &noise_suppress;
    }

    inline void setNoiseSuppress(int noiseSuppress) {
        noise_suppress = noiseSuppress;
    }
    void updateConfig(SpeexPreprocessState* processor);
    static int verifyConfig(int frame_size,SpeexPreprocessState** processor,SpeexConfig** config);
    static void initSpeexProcessor(int sample_rate,int frame_size,SpeexPreprocessState** processor,SpeexConfig** config);
    static void destroySpeexProcessor(SpeexPreprocessState* processor);
};

#endif //VOIPHONE_SPEEXCONFIG_H
