package org.phippp.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.app.Args;

public class Logging {

    /**
     * Helper class that will help deal with the verbose flag,
     * also allowing me to switch context easily inline allows
     * easier analysis when do lots of development.
     */

    private static final Logger LOG = LogManager.getLogger(Logging.class);

    public static <T> void log(String str, Class<T> clazz) {
        if(clazz == null) {
            LOG.info(str);
        }else{
            Logger logger = LogManager.getLogger(clazz);
            logger.info(str);
        }
    }

    private static void log(String str, Logger logger) {
        if(logger == null)
            LOG.info(str);
        else
            logger.info(str);
    }

    public static <T> void log(String str, Class<T> clazz, Args args) {
        if(args.verbose)
            System.out.printf("%s | %s%n", clazz, str);
        log(str, clazz);
    }

    public static void log(String str, Logger logger, Args args) {
        if(args.verbose)
            System.out.printf("%s | %s%n", logger.getName(), str);
        log(str, logger);
    }

}
