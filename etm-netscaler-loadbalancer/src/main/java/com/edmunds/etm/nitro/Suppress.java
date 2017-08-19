package com.edmunds.etm.nitro;

import java.util.concurrent.Callable;

class Suppress {

    static void suppress(VoidCall c) {
        try {
            c.call();
        } catch (Exception e) {
            // Drop exception
        }
    }

    static <T> T suppress(Callable<T> c) {
        try {
            return c.call();
        } catch (Exception e) {
            // Drop exception
            return null;
        }
    }
}
