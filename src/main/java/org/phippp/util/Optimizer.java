package org.phippp.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.grammar.RegEx;

public class Optimizer {

    public static final Logger LOG = LogManager.getLogger(Optimizer.class);
    public static final byte CONCAT = 1;

    public static RegEx optimize(RegEx r, byte options){
        if((options & CONCAT) > 0){
            r = concat(r);
        }
        return r;
    }

    public static RegEx concat(RegEx r) {
        LOG.info("Starting concatenation");
        RegEx concatenated = r.traverseAndDo(RegEx.SHRINKABLE, RegEx.SHRINK);
        return RegEx.SHRINKABLE.test(concatenated) ? RegEx.SHRINK.apply(concatenated) : concatenated;
    }

}
