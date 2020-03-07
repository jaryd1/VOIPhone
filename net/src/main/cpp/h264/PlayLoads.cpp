//
// Created by Jarryd.
//

#include "PlayLoads.h"


PlayLoads::PlayLoads() {
    playloads = nullptr;
    per_playload_size = nullptr;
    size = 0;
}

PlayLoads::PlayLoads(byte *nal,int len) {
    playloads = new byte*[1];
    playloads[0] = nal;
    per_playload_size = new int[1];
    per_playload_size[0] = len;
    size = 1;
}

PlayLoads::PlayLoads(byte **playloads, int *per_playload_size, int size) {
    this->playloads = playloads;
    this->per_playload_size = per_playload_size;
    this->size = size;
}

PlayLoads::~PlayLoads() {
    if(size >0){
        for (int i = 0; i < size; ++i) {
            delete [] playloads[i];
        }
        delete []playloads;
        delete []per_playload_size;
        size = 0;
    }
}

byte** PlayLoads::getPlayLoads() {
    return playloads;
}

int* PlayLoads::getPlayLoadSize() {
    return per_playload_size;
}

int PlayLoads::getSize() {
    return size;
}