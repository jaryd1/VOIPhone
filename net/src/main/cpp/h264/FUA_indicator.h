//
// Created by Jarryd.
//

#ifndef VOIPHONE_FUA_INDICATOR_H
#define VOIPHONE_FUA_INDICATOR_H

class FUA_Indicator{
private:
    unsigned char forbidden;
    unsigned char reference;
    unsigned char Type;
public:
    FUA_Indicator();
    FUA_Indicator(unsigned char f, unsigned char nri, unsigned char type);
    unsigned char getForbidden(){return forbidden;}
    unsigned char getReference(){return reference;}
    unsigned char getType(){return Type;}
    unsigned char toByte();
    unsigned char getForbidAndRefer();
    static FUA_Indicator* parse(unsigned char indicator);
};

#endif //VOIPHONE_FUA_INDICATOR_H
