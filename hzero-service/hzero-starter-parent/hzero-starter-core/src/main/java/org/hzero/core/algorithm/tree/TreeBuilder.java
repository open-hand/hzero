package org.hzero.core.algorithm.tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <p>
 * 递归构建树工具
 * </p>
 *
 * @author qingsheng.chen 2018/7/26 星期四 13:43
 */
public class TreeBuilder {
    
    private TreeBuilder() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static <P, T extends Child<T>> List<T> buildTree(List<T> objList, Node<P, T> nodeOperation) {
        Map<P, T> objMap = objList.stream().collect(Collectors.toMap(nodeOperation::getKey, Function.identity()));
        Map<P, List<T>> objGroupMap = new HashMap<>(16);
        objMap.forEach((key, value) -> {
            P parent;
            if (objMap.containsKey(nodeOperation.getParentKey(value))) {
                parent = nodeOperation.getParentKey(value);
            } else {
                parent = null;
            }
            if (objGroupMap.containsKey(parent)) {
                objGroupMap.get(parent).add(value);
            } else {
                List<T> list = new ArrayList<>();
                list.add(value);
                objGroupMap.put(parent, list);
            }
        });
        return recursiveBuildTree(objGroupMap, null, new ArrayList<>(), nodeOperation);
    }

    /**
     * 构建树(使用该方法时，org.hzero.boot.supporter.algorithm.tree.Node#getParentKey(org.hzero.boot.supporter.algorithm.tree.Child)的返回值必须不为null)
     *
     * @param objList       需要构建树的对象List，对象需要继承Child
     * @param rootKey       更节点的KEY
     * @param nodeOperation 节点操作
     * @param <P>           KEY的泛型
     * @param <T>           对象泛型
     * @return 树
     */
    public static <P, T extends Child<T>> List<T> buildTree(List<T> objList, P rootKey, Node<P, T> nodeOperation) {
        return buildTree(objList, rootKey, new ArrayList<>(), nodeOperation);
    }

    /**
     * 构建树(使用该方法时，org.hzero.boot.supporter.algorithm.tree.Node#getParentKey(org.hzero.boot.supporter.algorithm.tree.Child)的返回值必须不为null)
     *
     * @param objList       需要构建树的对象List，对象需要继承Child
     * @param rootKey       更节点的KEY
     * @param list          返回的结果
     * @param nodeOperation 节点操作
     * @param <P>           KEY的泛型
     * @param <T>           对象泛型
     * @return 树
     */
    public static <P, T extends Child<T>> List<T> buildTree(List<T> objList, P rootKey, List<T> list, Node<P, T> nodeOperation) {
        return recursiveBuildTree(objList.stream().collect(groupingByWithNullKeys(nodeOperation::getParentKey)),
                rootKey, list, nodeOperation);
    }

    /**
     * 构建树(使用该方法时，org.hzero.boot.supporter.algorithm.tree.Node#getParentKey(org.hzero.boot.supporter.algorithm.tree.Child)的返回值必须不为null)
     *
     * @param objList   需要构建树的对象List，对象需要继承Child
     * @param rootKey   根节点的KEY
     * @param key       获取当前节点的KEY
     * @param parentKey 获取父亲节点的KEY
     * @param <P>       KEY的泛型
     * @param <T>       对象泛型
     * @return 树
     */
    public static <P, T extends Child<T>> List<T> buildTree(List<T> objList, P rootKey, Key<P, T> key, ParentKey<P, T> parentKey) {
        return buildTree(objList, rootKey, new ArrayList<>(), key, parentKey);
    }

    /**
     * 构建树(使用该方法时，org.hzero.boot.supporter.algorithm.tree.Node#getParentKey(org.hzero.boot.supporter.algorithm.tree.Child)的返回值必须不为null)
     *
     * @param objList   需要构建树的对象List，对象需要继承Child
     * @param rootKey   根节点的KEY
     * @param list      将树存放到List中
     * @param key       获取当前节点的KEY
     * @param parentKey 获取父亲节点的KEY
     * @param <P>       KEY的泛型
     * @param <T>       对象泛型
     * @return 树
     */
    public static <P, T extends Child<T>> List<T> buildTree(List<T> objList, P rootKey, List<T> list, Key<P, T> key, ParentKey<P, T> parentKey) {
        return recursiveBuildTree(objList.stream().collect(groupingByWithNullKeys(parentKey::getParentKey)), rootKey, list, key, parentKey);
    }

    /**
     * 构建树
     *
     * @param map           对象Map，按照父节点的KEY分组
     * @param parentId      更节点的KEY
     * @param list          返回的结果
     * @param nodeOperation 节点操作
     * @param <P>           KEY的泛型
     * @param <T>           对象泛型
     * @return 树
     */
    public static <P, T extends Child<T>> List<T> recursiveBuildTree(Map<P, List<T>> map, P parentId, List<T> list, Node<P, T> nodeOperation) {
        if (map.containsKey(parentId)) {
            List<T> parentList = map.get(parentId);
            for (T node : parentList) {
                if (map.containsKey(nodeOperation.getKey(node))) {
                    node.addChildren(recursiveBuildTree(map, nodeOperation.getKey(node), new ArrayList<>(), nodeOperation));
                }
                list.add(node);
            }
        }
        return list;
    }

    /**
     * 构建树
     *
     * @param map       对象Map，按照父节点的KEY分组
     * @param parentId  更节点的KEY
     * @param list      返回的结果
     * @param key       获取当前节点的KEY
     * @param parentKey 获取父亲节点的KEY
     * @param <P>       KEY的泛型
     * @param <T>       对象泛型
     * @return 树
     */
    public static <P, T extends Child<T>> List<T> recursiveBuildTree(Map<P, List<T>> map, P parentId, List<T> list, Key<P, T> key, ParentKey<P, T> parentKey) {
        if (map.containsKey(parentId)) {
            List<T> parentList = map.get(parentId);
            for (T node : parentList) {
                if (map.containsKey(key.getKey(node))) {
                    node.addChildren(recursiveBuildTree(map, key.getKey(node), new ArrayList<>(), key, parentKey));
                }
                list.add(node);
            }
        }
        return list;
    }

    /**
     * 参考 https://stackoverflow.com/questions/22625065/collectors-groupingby-doesnt-accept-null-keys
     */
    public static <T, A> Collector<T, ?, Map<A, List<T>>> groupingByWithNullKeys(Function<? super T, ? extends A> classifier) {
        return Collectors.toMap(
                classifier,
                Collections::singletonList,
                (List<T> oldList, List<T> newEl) -> {
                    List<T> newList = new ArrayList<>(oldList.size() + 1);
                    newList.addAll(oldList);
                    newList.addAll(newEl);
                    return newList;
                });
    }
}
