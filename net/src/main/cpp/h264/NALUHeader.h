//
// Created by Jarryd.
//

#ifndef VOIPHONE_NALUHEADER_H
#define VOIPHONE_NALUHEADER_H

class NALUHeader{
private:
    unsigned char forbidden;
    unsigned char reference;
    unsigned char type;
public:
    NALUHeader();
    NALUHeader(unsigned char fb, unsigned char ref, unsigned char typ);
    unsigned char getForbidden(){return forbidden;}
    unsigned char getReference(){return reference;}
    unsigned char getType(){return type;}
    unsigned char toByte();
    static NALUHeader* paserHeader(unsigned char header);
};
#endif //VOIPHONE_NALUHEADER_H
