package com.algo;

import java.util.Arrays;

public class MinPQ<Key extends Comparable<Key>> {
    private Key[] pq;

    public Key[] toArray(Key[] a) {
        a = Arrays.copyOf(a, pq.length-1);
        System.arraycopy(pq, 1, a, 0, a.length);
        return a;
    }
    private int n = 0;

    @SuppressWarnings("unchecked")
    public MinPQ(int maxN) {
        pq = (Key[]) new Comparable[maxN+1];
    }

    public boolean isEmpty() {
        return n == 0;
    }
    public int size() {
        return n;
    }
    public void insert(Key v) {
        pq[++n] = v;
        swim(n);
    }
    public Key delMin() {
        Key min = pq[1];        // Retrieve max key from top.
        exch(1, n--);       // Exchange with last item.
        pq[n+1] = null;      // Avoid loitering.
        sink(1);         // Restore heap property.
        return min;
    }

    private boolean greater(int i, int j) {
        return pq[i].compareTo(pq[j]) > 0;
    }
    private void exch(int i, int j) {
        Key t = pq[i]; pq[i] = pq[j]; pq[j] = t;
    }
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k/2, k);
            k = k/2;
        }
    }
    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }

    public static void main(String[] args) {
        int[] arr = {3, 2, 100, 5};
        int size = arr.length;
        MinPQ<Integer> minPQ = new MinPQ<>(size);

        for (int i : arr) {
            minPQ.insert(i);
        }
        
        //Integer[] a = minPQ.toArray(new Integer[0]);
        //System.out.println(Arrays.toString(a));
    }
}

