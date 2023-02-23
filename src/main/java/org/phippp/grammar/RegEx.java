package org.phippp.grammar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RegEx {

    private final static Logger LOG = LogManager.getLogger(RegEx.class);

    public static final Predicate<RegEx> ALL = r -> true;
    public static final Predicate<RegEx> SHRINKABLE = r -> {
        return r.rule == Rule.CONCAT && r.right.terminal && r.left.rule != Rule.SIMPLE_GROUP && (r.left.terminal || r.left.right.terminal);
    };
    public static final Function<RegEx, RegEx> SHRINK = r -> {
        boolean below = !r.left.terminal;
        String str = (below ? r.left.right.text : r.left.text) + r.right.text;
        if(!below) return RegEx.makeTerminal(r, str);
        RegEx[] children = {r.left.left, RegEx.makeTerminal(r.right, str)};
        return r.replaceChildren(children);
    };

    private final Integer term;
    private final Rule rule;
    private RegEx left;
    private RegEx right;
    private boolean terminal;
    private final String text;

    public RegEx(Integer term, String text) {
        this.term = term;
        this.text = text;
        this.rule = Rule.CHARACTER;
        this.terminal = true;
    }

    public RegEx(Integer term, Rule rule, RegEx... children){
        if(children.length > 2) throw new RuntimeException("Invalid number of children");
        this.term = term;
        this.rule = rule;
        this.terminal = children.length == 0;
        this.left = !this.terminal ? children[0] : null;
        this.right = children.length == 2 ? children[1] : null;
        this.text = "";
    }

    public static RegEx makeTerminal(RegEx r, String str) {
        return new RegEx(r.term, str);
    }

    public RegEx replaceChildren(RegEx... children){
        this.terminal = children.length == 0;
        this.left = !this.terminal ? children[0] : null;
        this.right = children.length == 2 ? children[1] : null;
        return this;
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

    @Override
    public String toString(){
        // we return early if it's a character
        if(terminal)
            return "x_" + term + " := " + text;
        // we make a list of all terms included in the expression
        List<String> terms = getChildren().stream().map(r -> "x_" + r.term).toList();
        // we start building return string
        StringBuilder builder = new StringBuilder("x_" + term + " := ");
        String termString = String.join(getJoin(), terms);
        if(getChildren().size() == 1) termString = "(" + termString + ")";
        if(rule == Rule.PLUS) termString += "+";
        if(rule == Rule.OPTIONAL) termString += "?";
        builder.append(termString).append("\t").append(rule.toString());
        // we visit children via breadth first search
        // we don't want to revisit any references as they visited in the capture group
        List<RegEx> visitable = this.rule == Rule.REFERENCE
                ? this.getChildren().subList(0, 1)
                : this.getChildren();
        for(RegEx r : visitable)
            builder.append("\n").append(r.toString());
        return builder.toString();
    }

    /**
     * Alternative to toString that gives a basic description of the
     * node, useful for debugging.
     */

    public String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("Term : ").append(this.term)
                .append("\nRule : ").append(this.rule.toString());
        if(this.getChildren().size() > 0)
            builder.append("\nChildren : ").append(this.getChildren().stream().map(r -> String.valueOf(r.term)).collect(Collectors.joining(", ")));
        if(this.terminal)
            builder.append("\nTerminal : ").append(this.text);
        return builder.toString();
    }

    /**
     * Multiple traversal functions, with defaults to accept without
     * having to remember to pass arguments.
     * {@link RegEx#traverseAndDo} is the main function used for
     * optimisation. It will execute the passed function if it matches
     * the predicate.
     */

    public List<RegEx> traverse() {
        // visit all nodes, return all nodes
        return traverse(ALL, ALL);
    }

    public List<RegEx> traverse(Predicate<RegEx> condition) {
        // return nodes matching condition, but visit all nodes
        return traverse(condition, ALL);
    }

    public List<RegEx> traverse(Predicate<RegEx> parent, Predicate<RegEx> child) {
        // returns nodes matching parent, visit only nodes that match child
        List<RegEx> result = new ArrayList<>();
        if(!this.terminal && this.getChildren().size() > 0)
            for(RegEx c : this.getChildren())
                if(child.test(c))
                    result.addAll(c.traverse(parent, child));
        if(parent.test(this))
            result.add(this);
        return result;
    }

    public RegEx traverseAndDo(Predicate<RegEx> cond, Function<RegEx, RegEx> func) {
        RegEx[] children = new RegEx[this.getChildren().size()];
        if (this.left != null) children[0] = this.left.traverseAndDo(cond, func);
        if (this.right != null) children[1] = this.right.traverseAndDo(cond, func);
        this.replaceChildren(children);

        return cond.test(this) ? func.apply(this) : this;
    }

    /**
     * Getters, I originally was planning on not having these exposed
     * at all, however it's becoming a pain to add redundant class
     * methods.
     */

    public Integer getTerm() {
        return term;
    }

    public Rule getRule() { return rule; }

    public String getText() { return text; }

    public List<RegEx> getChildren() {
        List<RegEx> list = new ArrayList<>();
        if(this.left != null) list.add(this.left);
        if(this.right != null) list.add(this.right);
        return list;
    }

    public boolean isTerminal() { return this.terminal; }

    /**
     * Helper function that tidies up the switch case inside
     * a separate function.
     */

    private String getJoin(){
        return switch (this.rule) {
            case REFERENCE, CONCAT -> ".";
            case ALTERNATION -> "|";
            default -> "";
        };
    }

    public enum Rule {
        REFERENCE, CHARACTER, ALTERNATION, OPTIONAL, GROUPS, CONCAT, PLUS, SIMPLE_GROUP, NON_GROUP
    }
}
