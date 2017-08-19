package com.edmunds.etm.nitro;

import com.citrix.netscaler.nitro.exception.nitro_exception;
import com.citrix.netscaler.nitro.resource.base.base_response;

import java.util.concurrent.Callable;

public class NitroException extends Exception {
    public NitroException(String message, base_response baseResponse) {
        super(message + " - " + baseResponse.message + " (" + baseResponse.errorcode + ")");
    }

    public NitroException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public static void wrap(String message, Callable<base_response> c) throws NitroException {
        try {
            final base_response response = c.call();

            if (response.errorcode != 0) {
                throw new NitroException(message, response);
            }
        } catch (RuntimeException | NitroException e) {
            throw e;
        } catch (nitro_exception e) {
            throw wrapNitroException(e);
        } catch (Exception e) {
            throw new NitroException(e);
        }
    }

    public static <T> T wrap(Callable<T> c) throws NitroException {
        try {
            return c.call();
        } catch (RuntimeException | NitroException e) {
            throw e;
        } catch (nitro_exception e) {
            throw wrapNitroException(e);
        } catch (Exception e) {
            throw new NitroException(e);
        }
    }

    public static void wrap(VoidCall c) throws NitroException {
        try {
            c.call();
        } catch (RuntimeException | NitroException e) {
            throw e;
        } catch (nitro_exception e) {
            throw wrapNitroException(e);
        } catch (Exception e) {
            throw new NitroException(e);
        }
    }

    /**
     * Resource already exists
     */
    private static final int NSERR_EXIST = 273;

    /**
     * No such resource
     */
    private static final int NSERR_NOENT = 258;

    private static NitroException wrapNitroException(nitro_exception e) {
        switch (e.getErrorCode()) {
            case NSERR_EXIST:
                return new NitroExceptionResourceExists(e);
            case NSERR_NOENT:
                return new NitroExceptionNoSuchResource(e);
            default:
                return new NitroException(e);
        }
    }
}
