package org.phippp.util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetHelper {

    public static <T> boolean disjoint(Set<T> s1, Set<T> s2) {
        return !intersects(s1, s2);
    }

    public static <T> boolean intersects(Set<T> s1, Set<T> s2){
        // ensures it is mutable
        Set<T> inter = new HashSet<>(s1);
        inter.retainAll(s2);
        return inter.size() > 0;
    }

    public static Set<Integer> makeSet(int n) {
        Set<Integer> set = new LinkedHashSet<>();
        for(int i = 1; i <= n; i++)
            set.add(i);
        return set;
    }
}
