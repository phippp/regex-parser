package org.phippp.logic;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.util.BinaryTree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Acyclic {

    private static final Logger LOG = LogManager.getLogger(Acyclic.class);

    public static Conjunctive.Node tree(Spanner original, Graph<Integer> graph) {

        BinaryTree<Spanner> tree = new BinaryTree<>(original);
        int i = original.getDisplacement(), k = original.size() - 1;
        while(canContinue(tree)){
            for(int j = i; j <= k; j++){
                Spanner sp1 = original.subList(i, j);
                Spanner sp2 = original.subList(j + 1, k);
                if(sp1.equals(sp2) || !intersects(sp1.var(), sp2.var())){

                }
            }
        }

        LOG.info("NODES");
        for(Pair<Integer, Integer> p : graph.getV())
            LOG.info(p.toString());
        LOG.info("EDGES");
        for(Wrapper<Integer> w : graph.getE())
            LOG.info(w.left.toString() + w.middle + w.right);



        return null;
    }

    public static boolean canContinue(BinaryTree<Spanner> tree) {
        List<Spanner> list = tree.getLeaves();
        return list.stream().map(Spanner::size).anyMatch(s -> s > 1);
    }

    public static Pair<Boolean, Graph<Integer>> bracket(Spanner original) {
        int n = original.size();
        Set<Pair<Integer, Integer>> v = new HashSet<>();
        Set<Wrapper<Integer>> e = new HashSet<>();
        Set<Wrapper<Integer>> E = new HashSet<>();
        // generate starting values;
        for(int i : Parts.makeSet(n - 1)){
            v.addAll(List.of(Pair.of(i, i), Pair.of(i + 1, i + 1), Pair.of(i, i + 1)));
        }
        for(int i : Parts.makeSet(n - 1)){
            e.add(new Wrapper<>(Pair.of(i, i + 1), Pair.of(i, i), Pair.of(i + 1, i + 1)));
        }
        while(!(e.containsAll(E) && E.containsAll(e))){
            // janky way to swap all elements
            E.clear();
            E.addAll(e);
            //
            for(int i : Parts.makeSet(n)){
                for(int k : Parts.makeSet(n)){
                    if(i > k) continue;
                    for(int j = i; j < k; j++){
                        Wrapper<Integer> w = new Wrapper<>(Pair.of(i, k), Pair.of(i, j), Pair.of(j + 1 ,k));
                        if(!e.contains(w)){
                            if(v.containsAll(List.of(Pair.of(i, j), Pair.of(j + 1, k))) && isAcyclic(i, j, k, original, e)){
                                e.add(w);
                                v.add(Pair.of(i, k));
                            }
                        }
                    }
                }
            }
        }
        return Pair.of(v.contains(Pair.of(1, n)), new Graph<>(v, e));
    }

    private static boolean isAcyclic(int i, int j, int k, Spanner spanner, Set<Wrapper<Integer>> list){
        Spanner sp1 = spanner.subList(i, j);
        Spanner sp2 = spanner.subList(j + 1, k);
        if(sp1.equals(sp2))
            return true;
        if(!intersects(sp1.var(), sp2.var()))
            return true;
        for(int x = i; x < spanner.size(); x++){
            Wrapper<Integer> w = new Wrapper<>(Pair.of(i, j), Pair.of(i, x), Pair.of(x + 1, j));
            if(sp2.equals(spanner.subList(i, x)) && list.contains(w))
                return true;
        }
        for(int x = spanner.getDisplacement(); x < j; x++){
            Wrapper<Integer> w = new Wrapper<>(Pair.of(i, j), Pair.of(i, x), Pair.of(x + 1, j));
            if(sp2.equals(spanner.subList(x + 1, j)) && list.contains(w))
                return true;
        }
        for(int x = j + 1; x < spanner.size(); x++){
            Wrapper<Integer> w = new Wrapper<>(Pair.of(j + 1, k),Pair.of(j + 1, x),Pair.of(x + 1, k));
            if(sp1.equals(spanner.subList(j + 1, x)) && list.contains(w))
                return true;
        }
        for(int x = spanner.getDisplacement(); x < k; x++){
            Wrapper<Integer> w = new Wrapper<>(Pair.of(j + 1, k),Pair.of(j + 1, x),Pair.of(x + 1, k));
            if(sp1.equals(spanner.subList(x + 1, k)) && list.contains(w))
                return true;
        }
        return false;
    }

    protected static <T> boolean intersects(Set<T> s1, Set<T> s2){
        // ensures it is mutable
        Set<T> inter = new HashSet<T>(s1);
        inter.retainAll(s2);
        return inter.size() > 0;
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
