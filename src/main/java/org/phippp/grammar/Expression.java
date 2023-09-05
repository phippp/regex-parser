package org.phippp.grammar;

public class Expression {

    public Rule rule;
    public String data;

    public Expression(Rule r){
        this(r, null);
    }

    public Expression(Rule r, String data) {
        this.rule = r;
        this.data = data;
    }

}
;