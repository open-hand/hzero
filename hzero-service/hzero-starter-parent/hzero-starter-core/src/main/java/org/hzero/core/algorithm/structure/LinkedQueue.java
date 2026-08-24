package org.hzero.core.algorithm.structure;

import java.util.*;

import org.springframework.util.CollectionUtils;

/**
 * <p>
 * 有序的队列
 * 每一次插入后所有的元素都是按照规则排序
 * </p>
 *
 * @author qingsheng.chen 2018/9/17 星期一 15:39
 */
public class LinkedQueue<T> implements Collection<T> {
    private LinkedList<T> list;
    private Comparator<T> comparator;

    public LinkedQueue(Comparator<T> comparator) {
        list = new LinkedList<>();
        this.comparator = comparator;
    }

    public LinkedQueue(List<T> list, Comparator<T> comparator) {
        list.sort(comparator);
        this.list = new LinkedList<>(list);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object element) {
        return list.contains(element);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <E> E[] toArray(E[] elements) {
        return list.toArray(elements);
    }

    @Override
    public boolean add(T element) {
        list.add(findIndex(element), element);
        return true;
    }

    public T getFirst() {
        return list.getFirst();
    }

    public T getLast() {
        return list.getLast();
    }

    @Override
    public int size() {
        return list.size();
    }

    public LinkedQueue<T> append(T element) {
        list.add(findIndex(element), element);
        return this;
    }


    @Override
    public boolean remove(Object element) {
        return list.remove(element);
    }

    @Override
    public boolean containsAll(Collection<?> elements) {
        return list.containsAll(elements);
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) {
        if (CollectionUtils.isEmpty(elements)) {
            return false;
        }
        elements.forEach(this::append);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> elements) {
        return list.removeAll(elements);
    }

    @Override
    public boolean retainAll(Collection<?> elements) {
        return list.retainAll(elements);
    }

    @Override
    public void clear() {
        list.clear();
    }

    private int findIndex(T element) {
        if (isEmpty()) {
            return 0;
        }
        if (list.size() == 1) {
            return comparator.compare(list.get(0), element) > 0 ? 0 : 1;
        }
        return dichotomyFindIndex(element, 0, list.size() - 1);
    }

    private int dichotomyFindIndex(T element, int start, int end) {
        if (comparator.compare(list.get(start), element) > 0) {
            return start;
        }
        if (comparator.compare(list.get(end), element) < 0) {
            return end + 1;
        }
        if (end - start <= 1) {
            return end;
        }
        int middle = (start + end) / 2;
        if (comparator.compare(list.get(middle), element) < 0) {
            return dichotomyFindIndex(element, middle, end);
        } else {
            return dichotomyFindIndex(element, start, middle);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof LinkedQueue)) {return false;}

        LinkedQueue<?> that = (LinkedQueue<?>) o;

        if (list != null ? !list.equals(that.list) : that.list != null) {return false;}
        return comparator != null ? comparator.equals(that.comparator) : that.comparator == null;
    }

    @Override
    public int hashCode() {
        int result = list != null ? list.hashCode() : 0;
        result = 31 * result + (comparator != null ? comparator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
