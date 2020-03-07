//
// Created by Jarryd.
//

#include "NALUHeader.h"

NALUHeader::NALUHeader() {
    forbidden = 0;
    reference = 0;
    type = 0;
}

NALUHeader::NALUHeader(unsigned char fb, unsigned char ref, unsigned char typ) {
    forbidden = fb;
    reference = ref;
    type = typ;
}

unsigned char NALUHeader::toByte() {
    return (unsigned char)((forbidden << 7 & 0x80) | (reference <<5 & 0x60) | (type & 0x1f));
}

NALUHeader* NALUHeader::paserHeader(unsigned char header) {
    unsigned char forbidden = (unsigned char)((header & 80) >>7);
    unsigned char reference = (unsigned char)((header & 0x60) >> 5);
    unsigned char type = (unsigned char)(header & 0x1f);
    return new NALUHeader(forbidden,reference,type);
}