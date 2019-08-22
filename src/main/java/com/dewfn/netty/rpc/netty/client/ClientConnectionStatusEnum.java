package com.dewfn.netty.rpc.netty.client;

import lombok.Getter;

public enum ClientConnectionStatusEnum {

    Init(0),Open(1),Close(2);

    @Getter
    private int value;

    ClientConnectionStatusEnum(int value) {
        this.value = value;
    }
}
