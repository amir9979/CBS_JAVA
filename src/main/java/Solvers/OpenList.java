package Solvers;

import java.util.*;

/**
 * An implementation of {@link I_OpenList} that supports priority queue operations and runtimes using an instance
 * of {@link PriorityQueue}, while also using a {@link HashMap} to support a O(1) {@link #get(E)} operation.
 *
 * @param <E>
 */
public class OpenList<E> implements I_OpenList<E> {
    private Queue<E> queue;
    private Map<E, E> map;

    /*  = constructors like in PriorityQueue =  */

    public OpenList() {
        this.queue = new PriorityQueue<>();
        this.map = new HashMap<>();
    }

    public OpenList(int initialCapacity) {
        this(initialCapacity, null);
    }

    public OpenList(Comparator<? super E> comparator) {
        this.queue = new PriorityQueue<>(comparator);
        this.map = new HashMap<>();
    }

    public OpenList(int initialCapacity,
                         Comparator<? super E> comparator) {
        // Note: This restriction of at least one is not actually needed,
        // but continues for 1.5 compatibility
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.queue = new PriorityQueue<>(initialCapacity, comparator);
        this.map = new HashMap<>(initialCapacity);
    }

    public OpenList(Collection<? extends E> c) {
        this.queue = new PriorityQueue<>(c);
        this.map = new HashMap<>();
        for(E elem : c){
            this.map.put(elem, elem);
        }
    }

    /*  = interface implementation =  */

    @Override
    public E get(E e) {
        return this.map.get(e);
    }

    @Override
    public E replace(E e1, E e2) {
        boolean removed = this.remove(e1);
        this.add(e2);
        return removed ? e1 : null;
    }

    @Override
    public E keepOne(E e1, E e2, Comparator<E> criteria) {
        E keepElem = criteria.compare(e1, e2) <= 0 ? e1 : e2;
        E discardElem = keepElem == e1 ? e2 : e1;
        return this.replace(discardElem, keepElem);
    }

    @Override
    public int size() {
        return this.queue.size();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        // todo - probably wrong, as calling Iterator.remove() would not remove from both collections. replace with internal.
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }


    @Override
    public boolean add(E e) {
        map.put(e, e);
        return queue.add(e);
    }

    @Override
    public boolean remove(Object o) {
        //did the element even exist
        boolean existed = map.remove(o) != null;
        //if it didn't then there is no need for a somewhat expensive remove operation on #queue
        if(existed) {queue.remove(o);}
        return existed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for(E e : c){
            map.put(e, e);
        }
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for(Object o : c){
            result |= this.remove(o);
        }
        return result;
    }

    /**
     * Supported, but avoid using this.
     * @throws ClassCastException is c is not a collection of E or an extending class.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        if(c == null){return false;}
        Map<E, E> newMap = new HashMap<>();
        for(Object o : c){
            E e = ((E)o);
            if(this.map.containsKey(e)) {newMap.put(e, e);}
        }
        this.map = newMap;
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        this.queue.clear();
        this.map.clear();
    }

    @Override
    public boolean offer(E e) {
        return this.add(e);
    }


    /**
     * This is a dequeue method.
     * @return the "smallest" element as determined by natural ordering.
     * @throws NoSuchElementException {@inheritDoc}
     */
    @Override
    public E remove() {
        E e = queue.remove();
        map.remove(e);
        return e;
    }

    /**
     * This is a dequeue method.
     * @return the "smallest" element as determined by natural ordering.
     */
    @Override
    public E poll() {
        E e = queue.poll();
        map.remove(e);
        return e;
    }

    @Override
    public E element() {
        return queue.element();
    }

    @Override
    public E peek() {
        return queue.peek();
    }

}
