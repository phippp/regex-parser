package org.phippp.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.logic.Spanner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BinaryTree<T> {

    private final static Logger LOG = LogManager.getLogger(BinaryTree.class);

    private final Node<T> root;

    public BinaryTree(T data) {
        this.root = new Node<>(data);
    }

    public List<T> getLeaves() {
        return traverse()
                .filter(n -> n.left == null && n.right == null)
                .map(n -> n.data)
                .toList();
    }

    public BinaryTree<T> addToParent(T p, T child) {
        Node<T> parent = find(p);
        boolean success = parent.add(child);
        if(!success){
            throw new RuntimeException("Failed adding to parent");
        }
        return this;
    }

    private Node<T> find(T data) {
        return this.traverse()
                .filter(n -> {
                    // need to make sure it finds the exact instance not one with the same elements
                    boolean match = !(data instanceof Spanner) || ((Spanner) data).getDisplacement() == ((Spanner) n.data).getDisplacement();
                    return n.data.equals(data) && match;
                })
                .findFirst()
                .orElseThrow();
    }

    public Stream<Node<T>> traverse() {
        return root.traverse().stream();
    }

    public void print() {
        List<Node<T>> list = this.traverse()
                .peek(n -> LOG.info(String.format("%s", n.data)))
                .toList();
    }

    public Node<T> getRoot() {
        return root;
    }

    public static class Node<T>{

        private final T data;
        private Node<T> left;
        private Node<T> right;

        public Node(T data) {
            this.data = data;
            this.left = this.right = null;
        }

        public List<Node<T>> traverse() {
            List<Node<T>> list = new ArrayList<>(List.of(this));
            if(this.left != null) list.addAll(left.traverse());
            if(this.right != null) list.addAll(right.traverse());
            return list;
        }

        public boolean add(T data) {
            if(left == null) {
                left = new Node<>(data);
                return true;
            }
            if(right == null) {
                right = new Node<>(data);
                return true;
            }
            return false;
        }

        public T getData() {
            return data;
        }

        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        public Node<T> getLeft() {
            return left;
        }

        public Node<T> getRight() {
            return right;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

}
