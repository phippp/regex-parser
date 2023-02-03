package org.phippp.util;

import org.phippp.grammar.RegEx;

import java.util.List;
import java.util.stream.Collectors;

public class Optimizer {

    public static final byte CONCAT = 1;

    public static void optimize(RegEx r, byte options){
        if((options & CONCAT) > 0){
            // check for any concats that only cover terminals and replace as a single variable
            concat(r);
        }
    }

    public static void concat(RegEx r) {
        // get all nodes that are CONCAT and have TERMINAL child, without this it's not safe to
        // concat a string.
        List<RegEx> list = r.find(RegEx.concat);
        // calculate which nodes are the highest up the tree, instead of merging lots of individual nodes
        // we will attempt to merge all in one go.
        for(int i = list.size() - 1; i >= 0; i--){
            RegEx re = list.get(i);
            if(!((i - 1) >= 0 && r.hasChild(list.get(i - 1)))) continue;
            System.out.println("---------" + (i) + "---------");
            System.out.println(re.find(RegEx.finral).stream().map(RegEx.mapper).collect(Collectors.joining()));
        }
    }

}
