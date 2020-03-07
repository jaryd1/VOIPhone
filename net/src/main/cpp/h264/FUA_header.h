//
// Created by Jarryd.
//

#ifndef VOIPHONE_FUA_HEADER_H
#define VOIPHONE_FUA_HEADER_H

class FUA_header{
private:
    unsigned char Start;
    unsigned char End;
    unsigned char Reserved ;
    unsigned char Type;
public:
    FUA_header();
    FUA_header(unsigned char start, unsigned char end,
               unsigned char type);
    bool isStart(){return Start == 1;}
    bool isEnd(){return End == 1;}
    void setStart(unsigned char start){Start = start;}
    void setEnd(unsigned char end){ End = end;}
    unsigned char getType(){return Type;}
    unsigned char toByte();

    static FUA_header* parse(unsigned char header);
};

#endif //VOIPHONE_FUA_HEADER_H
