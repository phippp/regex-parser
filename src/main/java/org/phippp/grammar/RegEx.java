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
        // we return early if it's a character
        if(terminal)
            return "x_" + term + " := " + text;
        // we make a list of all terms included in the expression
        List<String> terms = children.stream().map(r -> "x_" + r.term).toList();
        // we start building return string
        StringBuilder builder = new StringBuilder("x_" + term + " := ");
        String termString = String.join(getJoin(), terms);
        if(children.size() == 1) termString = "(" + termString + ")";
        if(rule == Rule.PLUS) termString += "+";
        builder.append(termString).append("\t").append(rule.toString());
        // we visit children via breadth first search
        // we don't want to revisit any references as they visited in the capture group
        List<RegEx> visitable = this.rule == Rule.REFERENCE
                ? this.children.subList(0, 1)
                : this.children;
        for(RegEx r : visitable)
            builder.append("\n").append(r.toString());
        return builder.toString();
    }

    private String getJoin(){
        return switch (this.rule) {
            case REFERENCE, CONCAT -> ".";
            case ALTERNATION -> "|";
            default -> "";
        };
    }

    public enum Rule {
        REFERENCE, CHARACTER, ALTERNATION, GROUPS, CONCAT, PLUS, SIMPLE_GROUP, NON_GROUP
    }
}
