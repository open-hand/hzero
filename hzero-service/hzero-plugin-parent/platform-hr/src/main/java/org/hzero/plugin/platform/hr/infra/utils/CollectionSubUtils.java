package org.hzero.plugin.platform.hr.infra.utils;

import java.util.*;

/**
 *
 * @author bojiangzhou 2019/08/05
 */
public class CollectionSubUtils {

    private CollectionSubUtils() {
        throw new IllegalStateException("cann`t instantiation class");
    }

    public static <T> List<List<T>> subList(List<T> originalList, int volume) {
        List<List<T>> list = new ArrayList<>();
        if (volume < 1) {
            return list;
        }
        int size = originalList.size();
        int count = (size % volume == 0) ? size / volume : size / volume + 1;
        int start = 0;
        int end = volume;
        if (size != 0) {
            for (int i = 0; i < count; i++) {
                end = Math.min(end, size);
                List<T> subList = originalList.subList(start, end);
                start = start + volume;
                end = end + volume;
                list.add(subList);
            }
        }
        return list;
    }

    public static <T> List<Set<T>> subSet(Set<T> originalSet, int volume) {
        List<Set<T>> list = new ArrayList<>();
        if (volume < 1) {
            return list;
        }
        int size = originalSet.size();
        int count = (size % volume == 0) ? size / volume : size / volume + 1;
        if (size != 0) {
            Iterator<T> iterator = originalSet.iterator();
            for (int i = 0; i < count; i++) {
                int counter = 0;
                Set<T> set = new HashSet<>();
                list.add(set);
                while (counter < volume) {
                    if (iterator.hasNext()) {
                        set.add(iterator.next());
                    }
                    counter++;
                }
            }
        }
        return list;
    }
}
