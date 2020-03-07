//
// Created by Jarryd.
//

#include "FUA_indicator.h"

FUA_Indicator::FUA_Indicator() {
    forbidden = 0;
    reference = 0;
    Type = 0;
}

FUA_Indicator::FUA_Indicator(unsigned char f, unsigned char nri, unsigned char type) {
    forbidden = f;
    reference = nri;
    Type = type;
}

FUA_Indicator* FUA_Indicator::parse(unsigned char indicator) {
    unsigned char forbid = (unsigned char)((indicator & 0x80) >> 7);
    unsigned char refer = (unsigned char)((indicator & 0x60) >> 5);
    unsigned char type = (unsigned char)(indicator & 0x1f);
    return new FUA_Indicator(forbid,refer,type);
}

unsigned char FUA_Indicator::getForbidAndRefer() {
    return (unsigned char)((forbidden << 7 & 0x80) | (reference <<5 & 0x60) & 0xe0);
}

unsigned char FUA_Indicator::toByte() {
    return (unsigned char)((forbidden << 7 & 0x80) | (reference <<5 & 0x60) | (Type & 0x1f));
}
