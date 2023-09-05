package org.phippp.logic;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.app.Args;
import org.phippp.util.BinaryTree;
import org.phippp.util.Logging;
import org.phippp.util.SetUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Acyclic {

    private static final Logger LOG = LogManager.getLogger(Acyclic.class);

    public static BinaryTree<Spanner> tree(Spanner original, Graph<Integer> graph, Args args) {

        BinaryTree<Spanner> tree = new BinaryTree<>(original);
        int i , k;
        outerLoop:
        while(canContinue(tree, args)){
            Spanner leaf = tree.getLeaves().stream().filter(l -> l.size() > 1).findFirst().get();
            i = leaf.getDisplacement(); k = leaf.getDisplacement() + leaf.size() - 1;
            Logging.log(String.format("Start : %d, End : %d", i, k), LOG, args);
            for(int j = i; j <= k; j++){
                // considers disjoint variables
                Spanner sp1 = original.subList(i, j + 1);
                Spanner sp2 = original.subList(j + 1, k + 1);
                if(sp1.equals(sp2) || SetUtils.disjoint(sp1.var(), sp2.var())){
                    Logging.log(String.format("Rule 1: Attempting to add (%d, %d) and (%d, %d)", i, j + 1, j + 1, k + 1), LOG, args);
                    tree.addToParent(leaf, sp1).addToParent(leaf, sp2);
                    break;
                }
                // end disjoint part
                // consider the left part
                for(int x = i; x < j; x++) {
                    if(checkLeft(leaf, graph, i, j, x, k)){
                        Logging.log(String.format("Rule 2: Attempting to add (%d, %d) and (%d, %d)", i, j + 1, j + 1, k + 1), LOG, args);
                        tree.addToParent(leaf, sp1).addToParent(leaf, sp2);
                        Logging.log(String.format("Rule 2a: Attempting to add (%d, %d) and (%d, %d)", i, (x + 1), (x + 1), j + 1), LOG, args);
                        tree.addToParent(sp1, original.subList(i, x + 1)).addToParent(sp1, original.subList(x + 1, j + 1));
                        continue outerLoop;
                    }
                }
                // end left part
                // consider the right part
                for(int x = j + 1; x < k; x++) {
                    if(checkRight(leaf, graph, i, j, x, k)){
                        Logging.log(String.format("Rule 3: Attempting to add (%d, %d) and (%d, %d)", i, j + 1, j + 1, k + 1), LOG, args);
                        tree.addToParent(leaf, sp1).addToParent(leaf, sp2);
                        Logging.log(String.format("Rule 3a: Attempting to add (%d, %d) and (%d, %d)", (j + 1), (x + 1), (x + 1), k + 1), LOG, args);
                        tree.addToParent(sp2, original.subList(j + 1, x + 1)).addToParent(sp2, original.subList(x + 1, k + 1));
                        continue outerLoop;
                    }
                }
            }
        }
        return tree;
    }

    public static boolean checkRight(Spanner leaf, Graph<Integer> graph, int i, int j, int x, int k){
        // a[j+1,k] is subList(j+1, k+1), a[j+1, x] is subList(j+1, x+1), a[x+1, k+1] is subList(x+1, k+1)
        Wrapper<Integer> w = new Wrapper<>(j + 1, k + 1, j + 1, x + 1, x + 1, k + 1);
        Spanner ij = leaf.subList(i, j + 1), jx = leaf.subList(j + 1, x + 1),
                xk = leaf.subList(x + 1, k + 1);
        return graph.getE().contains(w) && (ij.equals(jx) || ij.equals(xk));
    }

    public static boolean checkLeft(Spanner leaf, Graph<Integer> graph, int i, int j, int x, int k){
        // a[i,j] is subList(i, j+1), a[i,x] is subList(i, x+1), a[x+1,j] is subList(x+1, j+1)
        Wrapper<Integer> w = new Wrapper<>(i, j + 1, i, x + 1, x + 1, j + 1);
        Spanner ix = leaf.subList(i, x + 1), jk = leaf.subList(j + 1, k + 1),
                xj = leaf.subList(x + 1, j + 1);
        return graph.getE().contains(w) && (ix.equals(jk) || xj.equals(jk));
    }

    public static boolean canContinue(BinaryTree<Spanner> tree, Args args) {
        List<Spanner> list = tree.getLeaves();
        long count = list.stream().filter(s -> s.size() > 1).count();
        Logging.log(String.format("Current Leaf Node Count : %d", count), LOG, args);
        return list.stream().map(Spanner::size).anyMatch(s -> s > 1);
    }

    public static Pair<Boolean, Graph<Integer>> bracket(Spanner original) {
        int n = original.size();
        Set<Pair<Integer, Integer>> v = new HashSet<>();
        Set<Wrapper<Integer>> Etilde = new HashSet<>();
        Set<Wrapper<Integer>> E = new HashSet<>();
        // generate starting values;
        // a[i, i] is sublist(i, i+1), a[i+1, i+1] is sublist(i+1, i+2), a[i, i+1] is subList(i, i+2)
        for(int i : SetUtils.makeSet(n - 1)){
            v.addAll(List.of(Pair.of(i, i + 1), Pair.of(i + 1, i + 2), Pair.of(i, i + 2)));
            Etilde.add(new Wrapper<>(Pair.of(i, i + 2), Pair.of(i, i + 1), Pair.of(i + 1, i + 2)));
        }
        //
        while(!Etilde.equals(E)){
            E.clear();
            E.addAll(Etilde);
            // nested for loop equivalent to for i,k in [n] where i < k
            for(int i : SetUtils.makeSet(n)){
                for(int k = i + 1; k <= n; k++){
                    for(int j = i; j < k; j++){
                        // a[i, k] is subList(i, k+1), a[i, j] is subList(i, j+1), a[j+1, k] is subList(j+1, k+1)
                        Wrapper<Integer> w = new Wrapper<>(i, k + 1, i, j + 1, j + 1, k + 1);
                        if(!Etilde.contains(w)){
                            // a[i, j] is subList(i, j+1), a[j+1, k] is subList(j+1, k+1)
                            if(v.containsAll(List.of(Pair.of(i, j + 1), Pair.of(j + 1, k + 1))) && isAcyclic(i, j, k, original, Etilde)){
                                Etilde.add(w);
                                v.add(Pair.of(i, k + 1));
                            }
                        }
                    }
                }
            }
        }
        return Pair.of(v.contains(Pair.of(1, n + 1)), new Graph<>(v, Etilde));
    }

    private static boolean isAcyclic(int i, int j, int k, Spanner spanner, Set<Wrapper<Integer>> list){
        // a[i,j] is subList(i j+1) a[j+1, k] is subList(j+1, k+1), a[i,x] is subList(i, x+1), a[x+1,j] is subList(x+1, j+1)
        Spanner sp1 = spanner.subList(i, j + 1), sp2 = spanner.subList(j + 1, k + 1);
        if(sp1.equals(sp2))
            return true;
        if(SetUtils.disjoint(sp1.var(), sp2.var()))
            return true;
        for(int x = i; x < j; x++){
            Wrapper<Integer> w1 = new Wrapper<>(i, j+1, i, x+1, x+1, j+1);
            Wrapper<Integer> w2 = new Wrapper<>(j+1, k+1, j+1, x+1, x+1, k+1);
            if(list.contains(w1) && sp2.equals(spanner.subList(i, x+1)))
                return true;
            if(list.contains(w1) && sp2.equals(spanner.subList(x+1, j+1)))
                return true;
            if(list.contains(w2) && sp1.equals(spanner.subList(j+1, x+1)))
                return true;
            if(list.contains(w2) && sp1.equals(spanner.subList(x+1, k+1)))
                return true;
        }
        return false;
    }

    public static class Wrapper<T> extends Triple<Pair<T, T>, Pair<T, T>, Pair<T, T>> {

        /**
         * I don't like the idea of implementing a tuple based class but nested tuples
         * are very untidy as you have to configure the type for each instance,
         * instead this will do it all for us and we should only need one type.
         */

        private final Pair<T, T> left;
        private final Pair<T, T> middle;
        private final Pair<T, T> right;

        public Wrapper(T... values) {
            super();
            if(values.length != 6) throw new RuntimeException("Invalid number of parameters");
            left = Pair.of(values[0], values[1]);
            middle = Pair.of(values[2], values[3]);
            right = Pair.of(values[4], values[5]);
        }

        public Wrapper(Pair<T, T> l, Pair<T, T> m, Pair<T, T> r){
            super();
            left = l;
            middle = m;
            right = r;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Wrapper<T> w = (Wrapper<T>) o;
            return getLeft().equals(w.getLeft()) && getMiddle().equals(w.getMiddle())
                    && getRight().equals(w.getRight());
        }

        @Override
        public Pair<T, T> getLeft() {
            return this.left;
        }

        @Override
        public Pair<T, T> getMiddle() {
            return this.middle;
        }

        @Override
        public Pair<T, T> getRight() {
            return this.right;
        }
    }

    public static class Graph<T> {

        private final Set<Pair<T, T>> v;
        private final Set<Wrapper<T>> e;

        protected Graph(Set<Pair<T, T>> v, Set<Wrapper<T>> e) {
            this.v = v;
            this.e = e;
        }

        public Set<Pair<T, T>> getV() {
            return v;
        }

        public Set<Wrapper<T>> getE() {
            return e;
        }
    }
}
