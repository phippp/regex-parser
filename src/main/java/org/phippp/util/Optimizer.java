package org.phippp.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.grammar.RegEx;

public class Optimizer {

    public static final Logger LOG = LogManager.getLogger(Optimizer.class);

    // concat finite terminals to reduce number fo free variables
    public static final byte CONCAT = 1;
    // will simplify unnecessary nodes i.e. x_4 IN x_3+ AND x_3=c becomes x_4 IN c+
    public static final byte SIMPLIFY = 2;
    // will invert terms to start with 0 at top and also make sure there are no gaps
    public static final byte ORDER = 4;


    public static RegEx optimize(RegEx r, byte options){
        if((options & CONCAT) > 0)
            r = concat(r);
        if((options & SIMPLIFY) > 0)
            r = simplify(r);
        if((options & ORDER) > 0)
            r = order(r);
        return r;
    }

    protected static RegEx concat(RegEx r)  {
        LOG.info("Starting concatenation");
        RegEx concatenated = r.traverseAndDo(RegEx.SHRINKABLE, RegEx.SHRINK);
        return RegEx.SHRINKABLE.test(concatenated) ? RegEx.SHRINK.apply(concatenated) : concatenated;
    }

    protected static RegEx simplify(RegEx r) {
        LOG.info("Starting simplifying nodes");
        return r.traverseAndDo(RegEx.EXTENDED_PLUS, RegEx.SIMPLIFY);
    }

    protected static RegEx order(RegEx r) {
        LOG.info("Starting re-ordering of variables");
        // replace first node with 0 then iterate
        r = new RegEx(0, r);
        return r.traverseBreadthAndDo(RegEx.ALL, RegEx.REORDER);
    }

}
