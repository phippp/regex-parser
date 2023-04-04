package org.phippp.logic;

import org.phippp.grammar.RegEx;
import org.phippp.util.BinaryTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.phippp.logic.Parts.ALPHA;
import static org.phippp.logic.Parts.DOT_EQ;

public class ConjunctiveTree {

    private final List<RegEx> allRestrictions;
    private final Node root;

    public static ConjunctiveTree fromSpanner(Spanner s) {
        return null;
    }

    public static ConjunctiveTree fromTree(BinaryTree<Spanner> t) {
        List<RegEx> restrictions = t.traverse()
                .filter(BinaryTree.Node::isLeaf)
                .map(n -> n.getData().source().get(0))
                .toList();

        int c = 0;
        // create the first root node
        BinaryTree.Node<Spanner> root = t.getRoot();
        boolean square = root.getData().isSquare();
        Type type = square ? Type.WXX : Type.WXY;
        List<RegEx> rest = Stream.of(root.getLeft(), root.getRight())
                .filter(BinaryTree.Node::isLeaf)
                .map(n -> n.getData().source().get(0))
                .toList();
        Node n = new Node(c++, type, root, rest);
        Node node = Node.newJoin(n, null);
        Node start = node;
        // build stack to do dfs traversal
        Stack<BinaryTree.Node<Spanner>> stack = new Stack<>();
        stack.push(root.getLeft());
        stack.push(root.getRight());

        //
        while(!stack.empty()){
            BinaryTree.Node<Spanner> current = stack.pop();
            if(current.isLeaf()) continue;

            type = current.getData().isSquare() ? Type.XYY : Type.XYZ;
            rest = Stream.of(current.getLeft(), current.getRight())
                    .filter(BinaryTree.Node::isLeaf)
                    .map(r -> r.getData().source().get(0))
                    .toList();
            n = new Node(c++, type, current, rest);
            node = node.addNode(n);

            stack.push(current.getLeft());
            stack.push(current.getRight());
        }

        return new ConjunctiveTree(restrictions, start);
    }

    protected ConjunctiveTree(List<RegEx> r, Node root){
        this.allRestrictions = r;
        this.root = root;
    }

    public List<Node> toList() {
        return this.root.toList();
    }

    public static class Node{

        private final Type type;
        private final BinaryTree.Node<Spanner> data;
        private final List<RegEx> restrictions;

        private Node left;
        private Node right;

        //need to check if a square
        public Node(int term, Type type, BinaryTree.Node<Spanner> node, List<RegEx> restrictions){
            this.type = type;
            this.data = node;
            this.restrictions = restrictions;
        }

        protected Node(Node... nodes) {
            if(nodes.length > 2 || nodes.length == 0) throw new RuntimeException("Invalid argument length");
            this.left = nodes[0];
            this.right = (nodes.length > 1) ? nodes[1] : null;
            this.data = null;
            this.type = Type.CONJ;
            this.restrictions = new ArrayList<>();
        }

        public static Node newJoin(Node n, Node m){
            return new Node(n, m);
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

        protected Node replaceChildren(Node left, Node right) {
            this.left = left;
            this.right = right;
            return this;
        }

        public boolean isJoin() {
            return this.type == Type.CONJ;
        }

        public List<Node> getChildren(){
            if(type != Type.CONJ) return new ArrayList<>();
            return Stream.of(left, right)
                    .filter(Objects::nonNull)
                    .toList();
        }

        @Override
        public String toString(){
            // we should only call toString on ⋈ or concatenation nodes
            if(type == Type.CONJ)
                return Parts.JOIN;
            if(data == null)
                throw new RuntimeException("No object to call function on");
            int parent = this.data.getData().hash(), left = this.data.getLeft().getData().hash(),
                    right = this.data.getRight().getData().hash();
            StringBuilder str = new StringBuilder(
                    String.format("%s_%d %s %s_%d.%s_%d", ALPHA, parent, DOT_EQ, ALPHA, left, ALPHA, right));
            if(restrictions.size() > 0) str.append("\n WITH ");
            for(RegEx r : restrictions)
                str.append("\n").append(ALPHA).append("_").append(getHash(r)).append(" AS ").append(r.toNodeString()).append(",");
            return str.toString();
        }

        private int getHash(RegEx r) {
            if(data == null || data.getLeft() == null || data.getRight() == null) return -1;
            Optional<Spanner> spanner = Stream.of(data.getLeft(), data.getRight())
                    .map(BinaryTree.Node::getData)
                    .filter(n -> n.source().get(0).equals(r))
                    .findFirst();
            return spanner.map(Spanner::hash).orElse(-1);
        }

        public BinaryTree.Node<Spanner> getData() {
            return data;
        }

        public List<Integer> getRegExIndexes() {
            List<BinaryTree.Node<Spanner>> list = List.of(getData().getLeft(), getData().getRight());
            List<Integer> ints = new ArrayList<>();
            for(int i = 0; i < list.size(); i++){
                if(list.get(i).isLeaf()) ints.add(i);
            }
            return ints;
        }

        public Type getType() {
            return type;
        }

        public List<RegEx> getRestrictions() {
            return restrictions;
        }

        public List<Node> toList(){
            List<Node> list = new ArrayList<>(List.of(this));
            if(type != Type.CONJ)
                return list;
            list.addAll(this.left.toList());
            if(right != null) list.addAll(this.right.toList());
            return list;
        }

    }

    public enum Type {
        WXX, WXY, XYY, XYZ, CONJ, FILTER
    }

}
