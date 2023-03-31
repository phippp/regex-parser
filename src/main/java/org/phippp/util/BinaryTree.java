package org.phippp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BinaryTree<T> {

    private final Node<T> root;

    public BinaryTree(T data) {
        this.root = new Node<T>(data);
    }

    public List<T> getLeaves() {
        return traverse()
                .filter(n -> n.left == null && n.right == null)
                .map(n -> n.data)
                .toList();
    }

    public Stream<Node<T>> traverse() {
        return root.traverse().stream();
    }

    private static class Node<T>{

        private T data;
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

    }

}
