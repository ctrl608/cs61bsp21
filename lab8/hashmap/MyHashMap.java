package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */

    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }

    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    //elements/buckets<=load
    private double load;
    private Set<K> keySet;
    private int size;

    // You should probably define some more!

    /**
     * Constructors
     */
    public MyHashMap() {
        this(16);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        size = 0;
        load = maxLoad;
        keySet = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
         Collection[] table=new Collection[tableSize];
         for(int i=0;i<tableSize;++i){
             table[i]=createBucket();
         }
        return table;
    }


    private void resize() {
        //不做检查,依赖外部判断
        int resizeFactor = 2;
        Collection<Node>[] copy = buckets;
        buckets = createTable(length() * resizeFactor);
        //转移node,keySet不变
        for (K key : keySet) {
            V value = get(key);
            Node node = createNode(key, value);
            buckets[hashIndex(key)].add(node);
        }
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!


    @Override
    public void clear() {
        size = 0;
        buckets = createTable(length());
        keySet = new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        return keySet.contains(key);
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }
        V value = null;
        for (Iterator<Node> it = buckets[hashIndex(key)].iterator(); it.hasNext(); ) {
            Node node = it.next();
            if (Objects.equals(node.key, key)) {
                value = node.value;
            }
        }
        return value;
    }

    @Override
    public int size() {
        return size;
    }

    public int length() {
        return buckets.length;
    }

    @Override
    public void put(K key, V value) {
        if (!containsKey(key)) {
            size += 1;
            buckets[hashIndex(key)].add(new Node(key,value));
            keySet.add(key);
            return;
        }
        remove(key);
        keySet.add(key);
        buckets[hashIndex(key)].add(new Node(key,value));

    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        if (!keySet.contains(key)) {
            return null;
        }
        Collection<Node> bucket = buckets[hashIndex(key)];

        for (Iterator<Node> it = bucket.iterator(); it.hasNext(); ) {
            Node node = it.next();
            if (node.key == key) {
                V value = node.value;
                bucket.remove(node);
                keySet.remove(key);
                return value;
            }
        }
        throw new RuntimeException("debug:key found in keySet but not in buckets");
    }

    @Override
    public V remove(K key, V value) {
        if (!keySet.contains(key)) {
            return null;
        }
        Collection<Node> bucket = buckets[hashIndex(key)];

        for (Iterator<Node> it = bucket.iterator(); it.hasNext(); ) {
            Node node = it.next();
            if (node.key == key && node.value == value) {
                bucket.remove(node);
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }

    private int hashIndex(Object key) {
        return Math.floorMod(key.hashCode(), length());
    }


//    private class HashMapIterator implements Iterator<K> {
//        private int tableIndex;
//        private Iterator<Node> currBucketIterator;
//
//        HashMapIterator() {
//            tableIndex = 0;
//            currBucketIterator = buckets.length == 0 ? null : buckets[0].iterator();
//        }
//
//        @Override
//        public boolean hasNext() {
//            while (currBucketIterator == null || !currBucketIterator.hasNext()) {
//                tableIndex += 1;
//                if (tableIndex >= buckets.length) {
//                    return false;
//                }
//                currBucketIterator=buckets[tableIndex].iterator();
//            }
//            return true;
//        }
//
//        @Override
//        public K next() {
//            return currBucketIterator.next().key;
//        }
//    }


}
