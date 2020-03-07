//
// Created by Jarryd.
//

#ifndef VOIPHONE_PLAYLOADS_H
#define VOIPHONE_PLAYLOADS_H

typedef unsigned char byte;

class PlayLoads{
private:
    byte** playloads;
    int* per_playload_size;
    int size;
public:
    PlayLoads();
    PlayLoads(byte* nal,int len);
    PlayLoads(byte** playloads,int* per_playload_size,int size);
    ~PlayLoads();
    int getSize();
    int* getPlayLoadSize();
    byte** getPlayLoads();

};
#endif //VOIPHONE_PLAYLOADS_H
