package org.phippp.logic;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.app.Args;
import org.phippp.grammar.RegEx;
import org.phippp.util.Logging;

import java.util.*;

public class BruteForce {

    private static final Logger LOG = LogManager.getLogger(BruteForce.class);

    private static final Set<Pair<String, String>> WXX = new HashSet<>();
    private static final Set<Pair<String, String>> WXY = new HashSet<>();
    private static final Set<Pair<String, String>> XYY = new HashSet<>();
    private static final Set<Pair<String, String>> XYZ = new HashSet<>();

    private static final Map<Integer, Set<String>> NODES = new HashMap<>();

    public static boolean testString(String input, ConjunctiveTree tree, Args args) {
        long start = System.currentTimeMillis();
        // build the tables of all possible strings that can match
        buildTables(input, args);
        // get all nodes that we can restrict on
        List<ConjunctiveTree.Node> list = tree.toList().stream()
                .filter(n -> !n.isJoin())
                .toList();
        list = new ArrayList<>(list);
        Collections.reverse(list);

        Logging.log(String.format("%d nodes", list.size()), LOG, args);

        for(ConjunctiveTree.Node node : list){

            Set<String> matches = new HashSet<>();
            List<RegEx> restrictions = node.getRestrictions();

            for(Pair<String, String> pair : getSet(node.getType())){
                // both are restrictions
                if(restrictions.size() == 2){
                    RegEx left = restrictions.get(0), right = restrictions.get(1);
                    if(left.match(pair.getLeft()) && right.match(pair.getRight())){
                        matches.add(pair.getLeft() + pair.getRight());
                    }
                }
                // left or right is restrictions
                else if(restrictions.size() == 1) {
                    boolean order = node.getRegExIndexes().contains(0); // if contains 0 is on left
                    RegEx rest = restrictions.get(0);
                    if(order){
                        if(rest.match(pair.getLeft())
                                && NODES.get(node.getData().getRight().getData().hash()).contains(pair.getRight())){
                            matches.add(pair.getLeft() + pair.getRight());
                        }
                    } else {
                        if(rest.match(pair.getRight())
                                && NODES.get(node.getData().getLeft().getData().hash()).contains(pair.getLeft())){
                            matches.add(pair.getLeft() + pair.getRight());
                        }
                    }
                }
                // none are restrictions
                else {
                    if(NODES.get(node.getData().getRight().getData().hash()).contains(pair.getRight()) &&
                            NODES.get(node.getData().getLeft().getData().hash()).contains(pair.getLeft())){
                        matches.add(pair.getLeft() + pair.getRight());
                    }
                }
            }

            NODES.put(node.getData().getData().hash(), matches);
        }

        Logging.log(String.format("Took %dms", (System.currentTimeMillis() - start)), LOG, args);
        return !NODES.get(list.get(list.size() - 1).getData().getData().hash()).isEmpty();
    }

    private static Set<Pair<String, String>> getSet(ConjunctiveTree.Type type) {
        return switch (type) {
            case WXX -> WXX;
            case WXY -> WXY;
            case XYY -> XYY;
            case XYZ -> XYZ;
            default -> throw new RuntimeException("Invalid Type");
        };
    }

    public static void buildTables(String input, Args args) {
        // all have to be added to not square as 'aa?' would be evaluated as WXY as 'a' and 'a?'
        // are not the same, but they match on ('a', 'a')
        for(int i = 0; i < input.length(); i++){
            for(int j = i; j <= input.length(); j++){
                for(int k = j; k <= input.length(); k++) {
                    String left = input.substring(i, j), right = input.substring(j, k);
                    boolean square = left.equals(right);
                    if(i == 0 && k == input.length()) {
                        WXY.add(Pair.of(left, right));
                        if(square) WXX.add(Pair.of(left, right));
                    } else {
                        XYZ.add(Pair.of(left, right));
                        if(square) XYY.add(Pair.of(left, right));
                    }
                    // Logging.log(String.format("(%s, %s)", left, right), LOG, args);
                }
            }
        }
    }
}