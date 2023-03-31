package org.phippp.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.grammar.RegEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Conjunctive {

    private static final Logger LOG = LogManager.getLogger(Conjunctive.class);

    public static Node fromRegEx(RegEx r){
        List<Node> nodes = new ArrayList<>(r.traverse().stream().distinct().map(RegEx::toNode).toList());
        Collections.reverse(nodes);
        Node n = Node.newJoin(nodes.get(0), null);
        // we keep this the original object, so we can return the root
        final Node start = n;
        for(int i = 1; i < nodes.size(); i++){
            n = n.addNode(nodes.get(i));
        }
        return start;
    }

    public static class Node {

        private final Type type;
        private final RegEx from;

        private Node left;
        private Node right;

        // shouldn't be used except for in Join class

        protected Node(Node... nodes) {
            if(nodes.length > 2 || nodes.length == 0) throw new RuntimeException("Invalid argument length");
            this.left = nodes[0];
            this.right = (nodes.length > 1) ? nodes[1] : null;
            this.from = null;
            this.type = Type.CONJ;
        }

        public Node(Type t, RegEx r) {
            this.type = t;
            this.from = r;
            this.left = null;
            this.right = null;
        }

        protected Node replaceChildren(Node left, Node right) {
            this.left = left;
            this.right = right;
            return this;
        }

        public static Node newJoin(Node n, Node m){
            return new Node(n, m);
        }

        public List<Node> toList(){
            List<Node> list = new ArrayList<>(List.of(this));
            if(type != Type.CONJ)
                return list;
            list.addAll(this.left.toList());
            if(right != null) list.addAll(this.right.toList());
            return list;
        }

        public RegEx getFrom(){
            return this.from;
        }

        public List<Node> getParts() {
            return this.toList().stream().filter(n -> !n.isJoin()).toList();
        }

        public Node addNode(Node n){
            if(type != Type.CONJ)
                return Node.newJoin(this, n);
            if(right == null){
                return this.replaceChildren(this.left, n);
            }
            Node m = Node.newJoin(this.right, n);
            this.replaceChildren(this.left, m);
            return this.right;
        }

        public boolean isJoin() {
            return this.type == Type.CONJ;
        }

        public List<Node> getChildren(){
            List<Node> list = new ArrayList<>();
            if(type != Type.CONJ) return list;
            list.addAll(List.of(left, right));
            list.removeIf(Objects::isNull);
            return list;
        }

        public String toString(){
            if(type == Type.CONJ)
                return Parts.JOIN;
            if(from == null)
                throw new RuntimeException("No object to call function on");
            return from.toConjunctive(type);
        }

    }

    public enum Type {
        WXX, WXY, XYY, XYZ, FILTER, CONJ
    }

}
