package org.phippp.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class RegEx {

    public static final Predicate<RegEx> finral = r -> r.terminal;
    public static final Predicate<RegEx> concat = r -> r.rule == Rule.CONCAT && r.children.stream().anyMatch(finral);
    public static final Function<RegEx, String> mapper = r -> r.text;

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
        if(rule == Rule.OPTIONAL) termString += "?";
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

    /**
     * Method to create a list of all the regex instances within the
     * tree. This allows us to extract information more easily from a list
     * using streams.
     */

    public List<RegEx> traverse() {
        List<RegEx> result = new ArrayList<>();
        if(!this.terminal && this.children != null && !this.children.isEmpty())
            for(RegEx child : this.children)
                result.addAll(child.traverse());
        result.add(this);
        return result;
    }

    /**
     * Method to create a list of all the regex instances within the
     * tree that also match a certain rule.
     */

    public List<RegEx> find(Predicate<RegEx> predicate) {
        return this.traverse()
                .stream()
                .filter(predicate)
                .toList();
    }

    /**
     * Method to check if a RegEx is inside another already.
     */

    public boolean hasChild(RegEx r){
        return this.children.contains(r);
    }

    /**
     * Overwritten equals and hashcode functions so .equals() can be
     * used accurately and as desired. Both were generated using
     * intelliJ IDEA default settings.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegEx regEx = (RegEx) o;

        if (terminal != regEx.terminal) return false;
        if (!term.equals(regEx.term)) return false;
        return rule == regEx.rule;
    }

    @Override
    public int hashCode() {
        int result = term.hashCode();
        result = 31 * result + rule.hashCode();
        result = 31 * result + (terminal ? 1 : 0);
        return result;
    }

    public enum Rule {
        REFERENCE, CHARACTER, ALTERNATION, OPTIONAL, GROUPS, CONCAT, PLUS, SIMPLE_GROUP, NON_GROUP
    }
}
