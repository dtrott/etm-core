package com.edmunds.etm.nitro;

public enum NsEffectiveState {
    UP,
    DOWN,
    UNKNOWN,
    BUSY,
    OUT_OF_SERVICE,
    GOING_OUT_OF_SERVICE,
    DOWN_WHEN_GOING_OUT_OF_SERVICE,
    NS_EMPTY_STR,
    DISABLED;

    public static NsEffectiveState remoteValueOf(String value) {
        return valueOf(value.toUpperCase().replace(' ', '_'));
    }
}
