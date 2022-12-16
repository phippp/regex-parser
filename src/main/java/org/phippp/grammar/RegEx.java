package org.phippp.grammar;

import java.util.List;

public class RegEx {

    private final Integer term;
    private final Rule rule;
    private final List<RegEx> children;
    private final boolean terminal;
    private final String text;

    public RegEx(Integer term, Rule rule, List<RegEx> children, String character){
        this.term = term;
        this.rule = rule;
        this.terminal = children == null || children.isEmpty();
        this.children = children;
        this.text = character;
    }

    @Override
    public String toString(){
        if(terminal){
            return "x_" + term + " := " + text;
        }
        List<Integer> terms = this.children.stream().map(a -> a.term).toList();
        StringBuilder builder = new StringBuilder("x_" + term + " := ");
        for(Integer i : terms)
            builder.append("x_").append(i);
        for(RegEx r : children)
            builder.append("\n").append(r.toString());
        return builder.toString();
    }

    public enum Rule {
        REFERENCE, CHARACTER, ALTERNATION, GROUPS, CONCAT, PLUS, SIMPLE_GROUP, NON_GROUP
    }
}
