package com.ibm.research.drl.deepguidelines.pathways.extractor.synthea;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

/*
 * Implementation of an interval search tree. It is essentially the same code as 
 * 
 * https://algs4.cs.princeton.edu/93intersection/IntervalST.java.html
 * 
 * with some modifications:
 * - Interval min and max are long values to properly represent dates as number of milliseconds from 1970-01-01T00:00:00Z
 * - each node can store a Set<Value>
 * - randomizedInsert and rootInsert are modified to test for Interval equality: if the interval is equal to that of 
 *   the current node, then we add the Value to the Set<Value> of the current node.
 * - searcAll return returns directly the Values instead of the Intervals
 */
public class IntervalTree<Value> {

    private final class Node {
        private final String id; // used only for plotting the tree with vis.js
        private final Interval interval;
        private final Set<Value> values = new ObjectOpenHashSet<>();
        private Node left;
        private Node right;
        private int n;
        private long max;

        public Node(Interval interval, Value value) {
            this.id = UUID.randomUUID().toString();
            this.interval = interval;
            this.values.add(value);
            this.n = 1;
            this.max = interval.getMax();
        }
    }

    private Node root;

    public int size() {
        return size(root);
    }

    public int height() {
        return height(root);
    }

    public int numberOfValues() {
        return numberOfValues(root);
    }

    public void put(Interval interval, Value value) {
        root = randomizedInsert(root, interval, value);
    }

    public List<Value> searchAll(Interval interval) {
        List<Value> result = new ObjectArrayList<>();
        searchAll(root, interval, result);
        return result;
    }

    /*
     * This method returns the javascript content for the file resources/interval_tree_vis_plot/nodes_edges.js
     * 
     * You can then see a visualization of this tree using http://visjs.org/:
     * - each node has three dates: interval min, interval max, and subtree max
     * - hovering on a node you see the Set<Value> associated to the node. 
     */
    public String buildVisualizationDataAsJavascript() {
        List<String> nodes = new ObjectArrayList<>();
        List<String> edges = new ObjectArrayList<>();
        // level order traversal
        Queue<Node> q = new LinkedList<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                Node n = q.poll();
                nodes.add("{id: '" + n.id + "', "
                        + "label: '"
                        + Instant.ofEpochMilli(n.interval.getMin()) + "\\n"
                        + Instant.ofEpochMilli(n.interval.getMax()) + "\\n"
                        + Instant.ofEpochMilli(n.max) + "', "
                        + "title: '" + String.join("<br/>", n.values.stream().map(v -> v.toString()).collect(Collectors.toSet())) + "'"
                        + "}");
                if (n.left != null) {
                    edges.add("{from: '" + n.id + "', to: '" + n.left.id + "', label: 'L'}");
                    q.offer(n.left);
                }
                if (n.right != null) {
                    edges.add("{from: '" + n.id + "', to: '" + n.right.id + "', label: 'R'}");
                    q.offer(n.right);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("var nodes = new vis.DataSet([").append(String.join(",", nodes)).append("]);");
        sb.append("var edges = new vis.DataSet([").append(String.join(",", edges)).append("]);");
        return sb.toString();
    }

    // @formatter:off
    // look in subtree rooted at x
    public boolean searchAll(Node x, Interval interval, List<Value> list) {
         boolean found1 = false;
         boolean found2 = false;
         boolean found3 = false;
         if (x == null)
            return false;
        if (interval.intersects(x.interval)) {
            list.addAll(x.values);
            found1 = true;
        }
        if (x.left != null && x.left.max >= interval.getMin())
            found2 = searchAll(x.left, interval, list);
        if (found2 || x.left == null || x.left.max < interval.getMin())
            found3 = searchAll(x.right, interval, list);
        return found1 || found2 || found3;
    }
    
    // make new node the root with uniform probability
    private Node randomizedInsert(Node x, Interval interval, Value value) {
        if (x == null) return new Node(interval, value);
        if (Math.random() * size(x) < 1.0) return rootInsert(x, interval, value);
        int cmp = interval.compareTo(x.interval);
        if      (cmp == 0) x.values.add(value); 
        else if (cmp  < 0) x.left  = randomizedInsert(x.left,  interval, value);
        else               x.right = randomizedInsert(x.right, interval, value);
        fix(x);
        return x;
    }

    private Node rootInsert(Node x, Interval interval, Value value) {
        if (x == null) return new Node(interval, value);
        int cmp = interval.compareTo(x.interval);
        if      (cmp == 0) { x.values.add(value); }
        else if (cmp  < 0) { x.left  = rootInsert(x.left,  interval, value); x = rotR(x); }
        else               { x.right = rootInsert(x.right, interval, value); x = rotL(x); }
        return x;
    }

    // fix auxiliary information (subtree count and max fields)
    private void fix(Node x) {
        if (x == null) return;
        x.n   = 1 + size(x.left) + size(x.right);
        x.max = max3(x.interval.getMax(), max(x.left), max(x.right));
    }

    private long max(Node x) {
        if (x == null) return Long.MIN_VALUE;
        return x.max;
    }

    // precondition: a is not null
    private long max3(long a, long b, long c) {
        return Math.max(a, Math.max(b, c));
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }
    
    private int size(Node x) {
        if (x == null) return 0;
        else           return x.n;
    }
    
    private int height(Node x) {
        if (x == null) return 0;
        else return 1 + Math.max(height(x.left), height(x.right));
    }
    
    private int numberOfValues(Node x) {
        if (x == null) return 0;
        else           return x.values.size() + numberOfValues(x.left) + numberOfValues(x.right);
    }
    // @formatter:on

}
