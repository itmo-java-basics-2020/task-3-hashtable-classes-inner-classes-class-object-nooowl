package ru.itmo.java;

public class HashTable {
    private static final int DEFAULT_CAPACITY = 10;
    private static final double DEFAULT_LOAD_FACTOR = 0.5;

    private Entry[] elements;
    private boolean[] deleted;
    private int size;
    private int capacity;
    private double loadFactor;
    private int threshold;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int initialCapacity, double loadFactor) {
        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        recreate();
    }

    public Object put(Object key, Object value) {
        int elementIndex = getIndexByKey(key);
        if (elementIndex == -1) {
            return null;
        }
        Object previousValue;
        if (elements[elementIndex] != null) {
            previousValue = elements[elementIndex].getValue();
        } else {
            size++;
            deleted[elementIndex] = false;
            previousValue = null;
        }
        elements[elementIndex] = new Entry(key, value);
        if (size >= threshold) {
            ensureCapacity();
        }
        return previousValue;
    }

    public Object get(Object key) {
        int elementIndex = getIndexByKey(key);
        return elementIndex == -1 || elements[elementIndex] == null
                ? null
                : elements[elementIndex].getValue();
    }

    public Object remove(Object key) {
        int elementIndex = getIndexByKey(key);
        if (elementIndex != -1 && elements[elementIndex] != null) {
            Object element = elements[elementIndex].getValue();
            elements[elementIndex] = null;
            deleted[elementIndex] = true;
            size--;
            return element;
        }
        return null;
    }

    public int size() {
        return size;
    }

    private int hashFunction(Object key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    private void ensureCapacity() {
        Entry[] temp = elements.clone();
        capacity *= 2;
        recreate();
        for (Entry entry : temp) {
            if (entry != null) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void recreate() {
        elements = new Entry[capacity];
        deleted = new boolean[capacity];
        threshold = (int) (loadFactor * capacity);
        size = 0;
    }

    private int getIndexByKey(Object key) {
        if (key == null) {
            return -1;
        }
        int i = hashFunction(key);
        int indexOfDeletedElement = -1;
        while (elements[i] == null || !elements[i].getKey().equals(key)) {
            if (elements[i] == null) {
                if (deleted[i] && indexOfDeletedElement == -1) {
                    indexOfDeletedElement = i;
                }
                if (!deleted[i]) {
                    return indexOfDeletedElement == -1 ? i : indexOfDeletedElement;
                }
            }
            i = (i + 1) % capacity;
        }
        return i;
    }

    private final static class Entry {
        private final Object key;
        private final Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }
}

