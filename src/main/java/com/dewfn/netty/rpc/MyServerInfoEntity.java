package com.dewfn.netty.rpc;

import lombok.Data;

@Data
public class MyServerInfoEntity {
    private String host;
    private int prot;
    private String serviceName;
}
