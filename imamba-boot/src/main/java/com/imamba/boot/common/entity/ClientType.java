package com.imamba.boot.common.entity;

public enum ClientType {
    UNKNOWN(0),
    MOBILE(1),
    BROWSER(2),
    WECHAT(3),
    TBOX(4),
    AVN(5),
    HMI(6);

    protected int code;

    private ClientType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static ClientType parse(int typeCode) {
        ClientType _clientType = UNKNOWN;
        switch(typeCode) {
            case 0:
                _clientType = UNKNOWN;
                break;
            case 1:
                _clientType = MOBILE;
                break;
            case 2:
                _clientType = BROWSER;
                break;
            case 3:
                _clientType = WECHAT;
                break;
            case 4:
                _clientType = TBOX;
                break;
            case 5:
                _clientType = AVN;
                break;
            case 6:
                _clientType = HMI;
        }

        return _clientType;
    }
}
