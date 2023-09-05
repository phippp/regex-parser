package org.phippp.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.phippp.util.BinaryTree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Representation of a tree, the tree structure handles all things the
 * same regardless of whether it is binary or not. Due to this the
 * handling of unique types of trees should be handled via the nodes
 * themselves.
 * {@link SimpleNode} and {@link BinaryNode} represent a traditional
 * and a binary tree respectively as seen by the implementation of
 * their children.
 *
 * @param <T> represents the type of the data to be stored in a node
 */
public class Tree<T>{
    private final static Logger LOG = LogManager.getLogger(Tree.class);

    protected Node<T> root;

    public Tree(){
        this.root = null;
    }

    public Tree(T data) {
        this.root = new SimpleNode<>(data);
    }

    public Stream<Node<T>> traverse(Traversal order){
        if(root == null) return Stream.<Node<T>>builder().build();
        return this.root.traverse(order);
    }

    public List<Node<T>> getLeaves(){
        return this.traverse(Traversal.PRE_ORDER)
                .filter(Node::isLeaf)
                .toList();
    }

    public Node<T> find(T value){
        return this.traverse(Traversal.PRE_ORDER)
                .filter(n -> n.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }

    public void print() {
        this.traverse(Traversal.PRE_ORDER)
                .peek(n -> LOG.info(String.format("%s", n.getValue())));
    }

    public static class SimpleNode<T> implements Node<T>{
        protected final T value;
        protected List<SimpleNode<T>> children;

        protected SimpleNode(T value){
            this.value = value;
            this.children = new ArrayList<>();
        }

        @Override
        public boolean isLeaf() {
            return this.children.isEmpty();
        }

        @Override
        public T getValue() {
            return this.value;
        }

        @Override
        public boolean add(T value) {
            return this.children.add(new SimpleNode<>(value));
        }

        @Override
        public Stream<Node<T>> traverse(Traversal order) {
            // defaults to using PRE_ORDER because otherwise it's a nightmare
            Stream<Node<T>> s = Stream.of(this);
            for(Node<T> child: this.children){
                s = Stream.concat(s, child.traverse(order));
            }
            return s;
        }
    }

    public static class BinaryNode<T> implements Node<T>{
        protected final T value;
        protected Node<T> left, right;

        protected BinaryNode(T value){
            this.value = value;
            this.right = this.left = null;
        }

        @Override
        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        @Override
        public T getValue() {
            return this.value;
        }

        @Override
        public boolean add(T value) {
            if(this.left == null){
                this.left = new BinaryNode<>(value);
                return true;
            }
            if(this.right == null){
                this.right = new BinaryNode<>(value);
                return true;
            }
            return false;
        }

        @Override
        public Stream<Node<T>> traverse(Traversal order) {
            return switch (order){
                case IN_ORDER -> {
                    Stream<Node<T>> s = Stream.empty();
                    if(this.left != null) s = Stream.concat(s, this.left.traverse(order));
                    s = Stream.concat(s, Stream.of(new BinaryNode<>(this.value)));
                    if(this.right != null) s = Stream.concat(s, this.right.traverse(order));
                    yield s;
                }
                case PRE_ORDER -> {
                    Stream<Node<T>> s = Stream.of(new BinaryNode<>(this.value));
                    if(this.left != null) s = Stream.concat(s, this.left.traverse(order));
                    if(this.right != null) s = Stream.concat(s, this.right.traverse(order));
                    yield s;
                }
                case POST_ORDER -> {
                    Stream<Node<T>> s = Stream.empty();
                    if(this.left != null) s = Stream.concat(s, this.left.traverse(order));
                    if(this.right != null) s = Stream.concat(s, this.right.traverse(order));
                    yield Stream.concat(s, Stream.of(new BinaryNode<>(this.value)));
                }
            };
        }
    }
}
