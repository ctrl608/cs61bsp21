package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private static class BSTNode<K extends Comparable<K>, V> {
        private K key;
        private V value;
        private BSTNode<K, V> parent;
        private BSTNode<K, V> left, right;

        private BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.parent = null;
            this.left = null;
            this.right = null;
        }


        public int compareTo(BSTNode<K, V> another) {
            return this.key.compareTo(another.key);
        }

        public boolean isLeft() {
            return parent != null && parent.left == this;
        }

        public int size() {
            int size = 1;
            if (left != null) {
                size += left.size();
            }
            if (right != null) {
                size += right.size();
            }
            return size;
        }

    }

    BSTNode<K, V> head;

    public BSTMap() {
        head = null;

    }

    @Override
    public void clear() {
        head = null;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode<K, V> node = find(key);
        return node != null && Objects.equals(node.key, key);
    }

    @Override
    public V get(K key) {
        BSTNode<K, V> branch = find(key);
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
        BSTNode<K, V> newNode = new BSTNode<>((K) key, (V) value);
        if (head == null) {
            head = newNode;
            return;
        }
//        put(newNode, head);
        BSTNode<K, V> branch = find(key);
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
    private BSTNode<K, V> find(K key) {
        return find(key, head);
    }

    private BSTNode<K, V> find(K key, BSTNode<K, V> curr) {
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


    public void printInOrder() {
        printInOrder(head);
    }

    private void printInOrder(BSTNode<K, V> curr) {
        if (curr == null) {
            return;
        }
        printInOrder(curr.left);
        System.out.println(curr.value);
        printInOrder(curr.right);
    }

    @Override
    public Set<K> keySet() {
        return keySet(head);
    }

    private Set<K> keySet(BSTNode<K, V> curr) {
        if (curr == null) {
            return new HashSet<>();
        }
        Set<K> leftSet = keySet(curr.left);
        Set<K> rightSet = keySet(curr.right);
        leftSet.add(curr.key);
        leftSet.addAll(rightSet);
        return leftSet;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        BSTNode<K, V> node = find(key);
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
    private void replace(BSTNode<K, V> node, BSTNode<K, V> another) {
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

    private void remove(BSTNode<K, V> node) {
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
        BSTNode<K, V> pre = node.left;
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
