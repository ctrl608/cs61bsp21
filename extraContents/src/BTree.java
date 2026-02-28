//import java.util.ArrayList;
//
//public class BTree<K, V> {
//    //2-3
//    private class Node {
//        K key;
//        V value;
//        private ArrayList<Node> childs=new ArrayList<>();
//
//        Node(K k, V v) {
//            key = k;
//            value = v;
//        }
//        public  int size(){
//            int size=1;
//            for(Node child:childs){
//                size+=child.size();
//            }
//            return size;
//        }
//        public void  insert(){
//
//        }
//        public void split(){
//
//        }
//    }
//    Node HEAD;
//
//    BTree(){
//    }
//    public boolean isEmpty(){
//        return HEAD==null;
//    }
//    public int size(){
//        return isEmpty() ? 0: HEAD.size();
//    }
//
//}


import java.util.*;

public class BTree<K extends Comparable<K>, V> implements Map61B<K, V> {
    private static class Leaf<K extends Comparable<K>, V> {
        private K key;
        private V value;
        private Leaf<K, V> prev;
        private Leaf<K, V> next;

        Leaf(K key, V value) {
            this.key = key;
            this.value = value;
            this.prev = null;
            this.next = null;
        }

        public int compareTo(Leaf<K, V> another) {
            return this.key.compareTo(another.key);
        }
        public K key(){
            return key;
        }
    }

    private static class BNode<K extends Comparable<K>, V> {


        private BNode<K, V> parent;
        private ArrayList<Leaf<K, V>> leaves;
        private ArrayList<BNode<K, V>> childs;

        private BNode(K key, V value) {
            parent = null;
            childs = new ArrayList<>();
        }

//
//        public boolean isLeft() {
//            return parent != null && parent.left == this;
//        }

        public int size() {
            int size = leaves.size();
            for (BNode child : childs) {
                size += child.size();
            }
            return size;
        }
        private static Set  keySet(BNode curr) {
            if (curr == null) {
                return new HashSet<>();
            }
            Set keys = new HashSet<>();
            for (Object leaf: curr.leaves){
                keys.add(((Leaf)leaf).key());
            }
            for(Object child:curr.childs){
                keys.addAll(keySet((BNode) child));
            }
            return keys;
        }


    }

    private BNode<K, V> head;

    public BTree() {
        head = null;

    }

    @Override
    public void clear() {
        head = null;
    }

    @Override
    public boolean containsKey(K key) {
        BNode<K, V> node = find(key);
        return node != null && Objects.equals(node.key, key);
    }

    @Override
    public V get(K key) {
        BNode<K, V> branch = find(key);
        return branch != null && Objects.equals(branch.key, key) ? branch.value : null;
    }

    @Override
    public int size() {
        if (head == null) {
            return 0;
        }
        return head.size();
    }

    @Override
    public void put(K key, V value) {
        BNode<K, V> newNode = new BNode<>((K) key, (V) value);
        if (head == null) {
            head = newNode;
            return;
        }
//        put(newNode, head);
        BNode<K, V> branch = find(key);
        if (newNode.compareTo(branch) < 0) {
            branch.left = newNode;
            newNode.parent = branch;
        } else if (newNode.compareTo(branch) == 0) {
            branch.value = value;
        } else {
            branch.right = newNode;
            newNode.parent = branch;
        }
    }

    /// 找到key应该在的位置,存在则返回对应node ,否则为父结点
    private BNode<K, V> find(K key) {
        return find(key, head);
    }

    private BNode<K, V> find(K key, BNode<K, V> curr) {
        //head==null
        if (curr == null) {
            return null;
        }
        if (key.compareTo(curr.key) == 0) {
            return curr;
        } else if (key.compareTo(curr.key) < 0) {
            return curr.left == null ? curr : find(key, curr.left);
        } else {
            return curr.right == null ? curr : find(key, curr.right);
        }
    }


//    public void printInOrder() {
//        printInOrder(head);
//    }
//
//    private void printInOrder(BNode<K, V> curr) {
//        if (curr == null) {
//            return;
//        }
//        printInOrder(curr.left);
//        System.out.println(curr.value);
//        printInOrder(curr.right);
//    }

    @Override
    public Set<K> keySet() {
        return keySet(head);
    }
    public Set<K> keySet(BNode<K,V> curr) {
        return BNode.keySet(curr);
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        BNode<K, V> node = find(key);
        remove(node);
        return node.value;
    }

    @Override
    public V remove(K key, V value) {
        if (containsKey(key) && Objects.equals(value, get(key))) {
            return remove(key);
        }
        return null;
    }

    /**
     * 用子结点覆盖自己
     *
     */
    private void replace(BNode<K, V> node, BNode<K, V> another) {
        if (node == head) {
            head = another;
            if (another != null) {
                another.parent = null;
            }
            return;
        }
        if (node.isLeft()) {
            node.parent.left = another;
            if (another != null) {
                another.parent = node.parent;
            }
        } else {
            node.parent.right = another;
            if (another != null) {
                another.parent = node.parent;
            }
        }
    }

    private void remove(BNode<K, V> node) {
        if (node.left == null && node.right == null) {
            replace(node, null);
            return;
        }
        if (node.left == null) {
            replace(node, node.right);
            return;
        }
        if (node.right == null) {
            replace(node, node.left);
            return;
        }
        /// ================================
        BNode<K, V> pre = node.left;
        while (pre.right != null) {
            pre = pre.right;
        }
        if (pre == node.left) {
            pre.right = node.right;
            pre.right.parent = pre;
            replace(node, pre);
            return;
        }
        pre.parent.right = pre.left;
        if (pre.left != null) {
            pre.left.parent = pre.parent;
        }
        pre.left = node.left;
        pre.right = node.right;
        pre.left.parent = pre;
        pre.right.parent = pre;
        replace(node, pre);
    }


    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }


}

