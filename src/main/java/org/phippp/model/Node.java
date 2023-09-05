package org.phippp.model;

import java.util.stream.Stream;

public interface Node<T> {
    /**
     * @return whether the node has no children
     */
    boolean isLeaf();

    /**
     * @return the value stored in the node
     */
    T getValue();

    /**
     * @param value to store at the new node
     * @return whether the node was added successfully
     */
    boolean add(T value);

    /**
     * @param order {@link Traversal} enum
     * @return a stream of all nodes under this node
     */
    Stream<Node<T>> traverse(Traversal order);
}
