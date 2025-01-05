package cn.lambdalib2.util;

import cn.lambdalib2.LambdaLib2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Generic debug utils.
 * @author WeAthFolD
 */
public class Debug {
    private static final Logger LOGGER = getOrCreateLogger();

    public static RuntimeException TODO() {
        throw new RuntimeException("TODO: Not implemented!");
    }

    public static void assert2(boolean expr) {
        assert2(expr, "Assertion failed");
    }
    
    public static void assert2(boolean expr, Supplier<String> lazyMessage) {
        if (!expr) {
            throw new RuntimeException("Assertion failed: " + lazyMessage.get());
        }
    }

    public static void assert2(boolean expr, String message) {
        if (!expr) {
            throw new RuntimeException("Assertion failed: " + message);
        }
    }

    public static void require(boolean expr) {
        require(expr, "Requirement failed");
    }

    public static void require(boolean expr, String message) {
        if (!expr) {
            throw new RuntimeException("Requirement failed: " + message);
        }
    }

    public static <T> T assertNotNull(T obj) {
        return assertNotNull(obj, "Object is null");
    }

    public static <T> T assertNotNull(T obj, String message) {
        return Objects.requireNonNull(obj, message);
    }

    public static <T> T assertNotNull(T obj, Supplier<String> lazyMessage) {
        if (obj == null) {
            throw new RuntimeException(lazyMessage.get());
        }
        return obj;
    }

    public static void debug(String msg) {
        LOGGER.debug(msg);
    }

    public static void log(String msg) {
        LOGGER.info(msg);
    }

    public static void error(Throwable ex) {
        LOGGER.error(ex);
    }

    public static void error(String msg, Throwable ex) {
        LOGGER.error(msg, ex);
    }

    public static void error(String msg) {
        LOGGER.error(msg);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }

    public static void warnFormat(String msg, Object ...pars) {
        warn(String.format(msg, pars));
    }

    private static Logger getOrCreateLogger() {
        Logger res = LambdaLib2.getLogger();
        if (res == null) {
            res = LogManager.getLogger("LL2_DEBUG");
        }
        return res;
    }

    private Debug() {}
}
