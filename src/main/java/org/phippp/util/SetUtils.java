package org.phippp.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetUtils {

    /**
     * @param s1 - {@link Set} of any type
     * @param s2 - {@link Set} of the same type as s1
     * @return whether the two sets are disjoint (no shared values).
     */
    public static <T> boolean disjoint(Set<T> s1, Set<T> s2) {
        return !intersects(s1, s2);
    }

    /**
     * As we are doing operations on the first set we create a new
     * set to ensure it is mutable.
     * @param s1 - {@link Set} of any type
     * @param s2 - {@link Set} of the same type as s1
     * @return whether the two sets intersect (shared values)
     */
    public static <T> boolean intersects(Set<T> s1, Set<T> s2){
        Set<T> inter = new HashSet<>(s1);
        inter.retainAll(s2);
        return !inter.isEmpty();
    }

    /**
     * @param n - size of the set to make
     * @return a {@link Set} containing values 1..n
     */
    public static Set<Integer> makeSet(int n) {
        Set<Integer> set = new LinkedHashSet<>();
        for(int i = 1; i <= n; i++)
            set.add(i);
        return set;
    }
}
