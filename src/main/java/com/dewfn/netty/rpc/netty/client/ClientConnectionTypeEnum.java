package com.dewfn.netty.rpc.netty.client;

import lombok.Getter;

public enum ClientConnectionTypeEnum {

    KeepAlive(2),Short(1);

    @Getter
    private int value;

    ClientConnectionTypeEnum(int value) {
        this.value = value;
    }
}
