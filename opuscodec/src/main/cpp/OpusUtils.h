//
// Created by Jarryd.
//

#ifndef VOIPHONE_OPUSUTILS_H
#define VOIPHONE_OPUSUTILS_H


#include <opus.h>
typedef unsigned char byte;
class OpusUtils {
private:
    OpusEncoder* encoder;
    OpusDecoder* decoder;
public:
    OpusUtils();
    ~OpusUtils();
    void init(int sample_rate,int channels,int bitrate);
    int encode(short* input,int sample_size,byte* output,int output_size);
    int decode(byte* input,int len, short* output,int output_sample_size);
};


#endif //VOIPHONE_OPUSUTILS_H
