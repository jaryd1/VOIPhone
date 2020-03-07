//
// Created by Jarryd.
//

#include <assert.h>
#include "OpusUtils.h"

OpusUtils::OpusUtils() {
    encoder = nullptr;
    decoder = nullptr;
}

OpusUtils::~OpusUtils() {
    if(encoder){
        opus_encoder_destroy(encoder);
        encoder = nullptr;
    }

    if(decoder){
        opus_decoder_destroy(decoder);
        decoder = nullptr;
    }
}

void OpusUtils::init(int sample_rate, int channels, int bitrate) {
    int error;
    encoder =opus_encoder_create(sample_rate,channels,OPUS_APPLICATION_VOIP,&error);
    assert(error == OPUS_OK);
    opus_encoder_ctl(encoder,OPUS_SET_BITRATE(bitrate));
    opus_encoder_ctl(encoder,OPUS_SET_COMPLEXITY(3));
    opus_encoder_ctl(encoder,OPUS_SET_SIGNAL(OPUS_SIGNAL_VOICE));

    decoder = opus_decoder_create(sample_rate,channels,&error);
    assert(error == OPUS_OK);


}

int OpusUtils::encode(short *input, int sample_size, byte *output,int output_size) {
    assert(encoder);
    return opus_encode(encoder,input,sample_size,output,output_size);
}

int OpusUtils::decode(byte *input, int len, short *output,int output_sample_size) {
    assert(decoder);
    return opus_decode(decoder,input,len,output,output_sample_size,0);
}