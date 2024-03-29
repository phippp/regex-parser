package org.phippp.grammar;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.logic.ConjunctiveTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.phippp.logic.ConjunctiveTree.Type.FILTER;
import static org.phippp.logic.Parts.*;

public class RegEx {

    private final static Logger LOG = LogManager.getLogger(RegEx.class);

    /* Returns all nodes when used in traversal */
    public static final Predicate<RegEx> ALL = r -> true;
    /* Returns all nodes that can be concatenated in traversal */
    public static final Predicate<RegEx> SHRINKABLE = r -> {
        return (r.rule == Rule.CONCAT || r.rule == Rule.REFERENCE) && r.right.terminal && r.left.rule != Rule.SIMPLE_GROUP && (r.left.terminal || (r.left.right != null && r.left.right.terminal) );
    };
    /* Returns the new RegEx after concatenating in traversal */
    public static final Function<RegEx, RegEx> SHRINK = r -> {
        boolean below = !r.left.terminal;
        String str = (below ? r.left.right.text : r.left.text) + r.right.text;
        if(!below) return RegEx.makeTerminal(r, Rule.CHARACTER, str);
        RegEx[] children = {r.left.left, RegEx.makeTerminal(r.right, Rule.CHARACTER, str)};
        return r.replaceChildren(children);
    };
    /* Returns all nodes that can be simplified in traversal */
    public static final Predicate<RegEx> EXTENDED_PLUS_OPTIONAL = r -> {
        return (r.rule == Rule.PLUS || r.rule == Rule.OPTIONAL) && r.left.rule == Rule.CHARACTER;
    };
    /* Returns the new RegEx after simplifying in traversal */
    public static final Function<RegEx, RegEx> SIMPLIFY = r -> {
        return RegEx.makeTerminal(r, r.rule, r.left.text);
    };
    /* Returns the new RegEx after reordering nodes (not used) */
    public static final Function<RegEx, RegEx> REORDER = r -> {
        if(r.terminal) return r;
        Integer leftNodes = r.left.traverse().size() + 1;
        RegEx[] children = (r.right != null)
                ? new RegEx[]{new RegEx(r.term + 1, r.left), new RegEx(r.term + leftNodes, r.right)}
                : new RegEx[]{new RegEx(r.term + 1, r.left)};
        return r.replaceChildren(children);
    };
    /* Returns all nodes that are groups in traversal */
    public static final Predicate<RegEx> IS_GROUP = r -> { return r.rule == Rule.SIMPLE_GROUP; };
    /* Returns the new RegEx after removing any groups in traversal */
    public static final Function<RegEx, RegEx> SKIP = r -> {
        return r.left;
    };

    private final Integer term;
    private final Rule rule;
    private RegEx left, right;
    private boolean terminal;
    private final String text;

    public RegEx(Integer term, Rule rule, String text) {
        this.term = term;
        this.text = text;
        this.rule = rule;
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

    public RegEx(Integer term, RegEx r){
        this.term = term;
        this.text = r.text;
        this.rule = r.rule;
        this.left = r.left;
        this.right = r.right;
        this.terminal = r.terminal;
    }

    public static RegEx makeTerminal(RegEx r, Rule rule, String str) {
        return new RegEx(r.term, rule, str);
    }

    public RegEx replaceChildren(RegEx... children){
        this.terminal = children.length == 0;
        this.left = !this.terminal ? children[0] : null;
        this.right = children.length > 1 ? children[1] : null;
        return this;
    }

    /**
     * toString(true) should be called on the root to convert properly
     * to an FC formulae.
     */

    public String toString(boolean start){
        List<String> variables = Stream.of(this, left, right)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(RegEx::getTerm))
                .map(RegEx::getVariable).toList();
        return EXISTS + String.join(", ", variables) + ": (" + this + ")";
    }

    @Override
    public String toString(){
        // we return early if it's a character
        Pair<Boolean, String> special = getSpecial();
        if(terminal)
            return special.getLeft() ? getVariable() + DOT_EQ + text
                    : getVariable() + IN + text + special.getRight();
        // we make a list of all terms included in the expression
        List<String> terms = getChildren().stream()
                .map(RegEx::getVariable).toList();
        // we start building return string
        StringBuilder builder = new StringBuilder(getVariable() + (special.getLeft() ? DOT_EQ : IN));

        String termString = (special.getLeft())
                ? String.join(special.getRight(), terms)
                : terms.get(0) + special.getRight();
        builder.append(termString);

        List<RegEx> visitable = this.rule == Rule.REFERENCE
                ? this.getChildren().subList(0, 1)
                : this.getChildren();
        for(RegEx r : visitable) {
            builder.append(AND);
            List<String> f = r.getChildren().stream().map(RegEx::getVariable).toList();
            if(f.size() > 0)
                builder.append(EXISTS).append(String.join(", ", f)).append(": ");
            builder.append("(").append(r).append(")");
        }
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

    public String toNodeString() {
        if(getType() == FILTER) {
            String symbol = (rule == Rule.CHARACTER) ? DOT_EQ : IN;
            return String.format("%s%s%s%s", getVariable(), symbol, text, getSpecial().getRight());
        }
        String t = term == 0 ? "w" : getVariable();
        return t + DOT_EQ + String.join("", getChildren().stream().map(RegEx::getVariable).toList());
    }

    /**
     * Given a string, test whether it matches a given node.
     */

    public boolean match(String str){
        if(getType() != FILTER && rule != Rule.ALTERNATION) return false;
        if(rule == Rule.CHARACTER) return text.equals(str);
        if(rule == Rule.PLUS) {
            if(terminal) {
                // check for (str.length / text.length) = n iterations of text
                if (str.length() % text.length() != 0 || str.length() == 0) return false;
                for (int i = 0; i < str.length(); i += text.length())
                    if (!str.substring(i, i + text.length()).equals(text)) return false;
                return true;
            } else {
                // check for n iterations of text assuming that text can include operators
                // like ? which shrinks the size i.e. abc? matches ab so (abc?)+ should match
                // abababab etc.
                boolean match = false;
                for(int j = 1; j < str.length(); j++){
                    String text = str.substring(0, j);
                    if(this.left.match(text)) {
                        int i;
                        if (str.length() % text.length() != 0) continue;
                        for (i = 0; i < str.length(); i += text.length())
                            if(!str.startsWith(text, i)) break;
                        if (i >= str.length()) {
                            match = true;
                            break;
                        }
                    }
                }
                return match;
            }
        }
        if(rule == Rule.OPTIONAL) {
            return "".equals(str) || (terminal ? text.equals(str) : left.match(str));
        }
        if(rule == Rule.ALTERNATION) {
            return this.left.match(str) || this.right.match(str);
        }
        return false;
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

    public RegEx traverseBreadthAndDo(Predicate<RegEx> cond, Function<RegEx, RegEx> func) {
        RegEx clone = cond.test(this) ? func.apply(this) : this;
        RegEx[] children = new RegEx[clone.getChildren().size()];
        if (clone.left != null) children[0] = clone.left.traverseBreadthAndDo(cond, func);
        if (clone.right != null) children[1] = clone.right.traverseBreadthAndDo(cond, func);
        clone.replaceChildren(children);
        return clone;
    }

   /* Getter Functions */

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

    private Pair<Boolean, String> getSpecial(){
        return switch (this.rule) {
            case REFERENCE, CONCAT -> Pair.of(true, ".");
            case ALTERNATION -> Pair.of(true, "|");
            case OPTIONAL -> Pair.of(false, "?");
            case PLUS -> Pair.of(false, "+");
            default -> Pair.of(true, "");
        };
    }

    private String getVariable(){
        return "x_" + this.term;
    }

    public ConjunctiveTree.Type getType() {
        if(this.terminal || this.right == null) return FILTER;
        if(term == 0){
            return (Objects.equals(left.term, right.term))
                    ? ConjunctiveTree.Type.WXX : ConjunctiveTree.Type.WXY;
        }
        return (Objects.equals(left.term, right.term))
                ? ConjunctiveTree.Type.XYY : ConjunctiveTree.Type.XYZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegEx regEx = (RegEx) o;

        if (!term.equals(regEx.term)) return false;
        return rule == regEx.rule;
    }

    @Override
    public int hashCode() {
        int result = term.hashCode();
        result = 31 * result + rule.hashCode();
        return result;
    }

    public enum Rule {
        REFERENCE, CHARACTER, ALTERNATION, OPTIONAL, GROUPS, CONCAT, PLUS, SIMPLE_GROUP, NON_GROUP
    }
}
