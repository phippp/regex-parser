package org.phippp.logic;

import org.phippp.grammar.RegEx;
import org.phippp.util.SetHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Spanner {

    private final List<RegEx> parts;
    private final int displacement;

    private Spanner(List<RegEx> list, int s) {
        this.parts = list;
        this.displacement = s;
    }

    public int getDisplacement() {
        return this.displacement;
    }

    public static Spanner fromRegEx(RegEx re){
        List<RegEx> list = re.traverse().stream()
                .filter(r -> !(r.getRule() == RegEx.Rule.CONCAT || r.getRule() == RegEx.Rule.REFERENCE))
                .toList();
        return new Spanner(list, 1);
    }

    public int size() {
        return this.parts.size();
    }

    public Spanner subList(int a, int b) {
        // this should allow a subset to chain correctly
        // i.e .subList(1,5).sublist(2,4) will contain 2 -> 4 from the original
        List<RegEx> list = this.parts.subList(a - this.displacement, b - this.displacement);
        return new Spanner(list, this.displacement + a - 1);
    }

    public Set<String> var() {
        Set<String> list = new HashSet<>();
        for(RegEx r: parts)
            list.add("x_" + r.getTerm());
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Spanner other = (Spanner) o;
        if(size() != other.size()) return false;
        return parts.equals(other.parts);
    }

    @Override
    public String toString(){
        Set<Integer> terms = SetHelper.makeSet(size());
        StringBuilder str = new StringBuilder(Parts.ALPHA)
                .append(Parts.DOT_EQ)
                .append(String.join(".", terms.stream().map(i -> Parts.ALPHA + "_" + (i + displacement - 1)).toList()))
                .append("\nWHERE\n");
        for(int i : terms)
            str.append(Parts.ALPHA).append("_").append((i + displacement - 1)).append(" IS (").append(parts.get(i - 1).toNode().toString()).append("), ");
        return str.toString();
    }

}
