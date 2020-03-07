//
// Created by Jarryd.
//

#include "FUA_header.h"


FUA_header::FUA_header() {
    Start = 0;
    End = 0;
    Reserved  = 0;
    Type = 0;
}

FUA_header::FUA_header(unsigned char start, unsigned char end,
                       unsigned char type) {
    Start =start;
    End = end;
    Reserved  = 0;
    Type = type;
}



FUA_header* FUA_header::parse(unsigned char header) {
    unsigned char start = (unsigned char)((header & 0x80) >> 7);
    unsigned char end = (unsigned char)((header & 0x20) >> 5);
    unsigned char type = (unsigned char)(header & 0x1f);
    return new FUA_header(start,end,type);
}

unsigned char FUA_header::toByte() {
    return (unsigned char)((Start << 7 & 0x80) | (Reserved << 6 & 0x40)
            | (End << 5 & 0x20) | (Type & 0x1f));
}