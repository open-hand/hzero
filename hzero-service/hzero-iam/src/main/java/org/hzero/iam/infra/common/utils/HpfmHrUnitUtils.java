package org.hzero.iam.infra.common.utils;

import org.hzero.iam.domain.entity.HrUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/9/5
 */
public class HpfmHrUnitUtils {
    private static Logger logger = LoggerFactory.getLogger(HpfmHrUnitUtils.class);

    public static List<HrUnit> formatHrUnitTree(List<HrUnit> hrUnitList) {
        if (CollectionUtils.isEmpty(hrUnitList)) {
            return Collections.emptyList();
        }

        /**
         * 组装成树状结构
         */
        hrUnitList.forEach(item -> {
            formatHrUnitTreeInternal(item, hrUnitList);
        });

        /**
         * 获取结果集顶点
         */
        return hrUnitList.stream().filter(item -> item.getParentHrUnit() == null ||
                item.getParentUnitId() == null ||
                item.getParentUnitId() == 0).collect(Collectors.toList());
    }

    /**
     * 通过名字或者code 构建树
     *
     * @param hrUnitList 组织列表
     * @param code       代码
     * @param name       名称
     * @return 返回满足条件的树形结构
     */
    public static List<HrUnit> formatHrUnitTreeByCodeAndName(List<HrUnit> hrUnitList, String code, String name) {
        HrUnit where = new HrUnit();
        if (!StringUtils.isEmpty(code)) {
            where.setUnitCode(code);
        }
        if (!StringUtils.isEmpty(name)) {
            where.setUnitName(name);
        }
        List<HrUnit> topList = formatHrUnitTree(hrUnitList);
        return topList.stream().map(item -> treeSearch(item, where, true)).filter(item -> item != null).collect(Collectors.toList());
    }

    /**
     * 从根节点开始搜索树,<br/>
     * 当遇到满足节点的节点，记录该节点，并延着该路径反向搜索，<br/>
     * 获取到所需节点后，重构新树返回<br/>。
     *
     * @param treeRoot 树根
     * @param where    查询条件
     * @param isLike   是否开启模糊查询
     * @return 返回一颗剔除掉不必要节点的树
     * @author jianbo.li@hand-china.com
     */
    public static HrUnit treeSearch(HrUnit treeRoot, Object where, boolean isLike) {
        LinkedList<HrUnit> linkedList = new LinkedList<>();
        linkedList.push(treeRoot);
        List<HrUnit> suitedNodes = new ArrayList<>();
        HrUnit node;
        while ((node=linkedList.poll()) != null) {
            if (whereIsSatisfied(node, where, isLike)) {
                suitedNodes.addAll(findAllNodeInThePath(node,true));
            }
            if (!CollectionUtils.isEmpty(node.getChildren())) {
                node.getChildren().forEach(item -> linkedList.push(item));
            }
            node.setChildren(null);
        }
        suitedNodes = suitedNodes.stream().filter(distinctByKey(HrUnit::getUnitId)).collect(Collectors.toList());
        formatHrUnitTreeInternal(treeRoot, suitedNodes);
        if (treeRoot.getChildren() == null && !whereIsSatisfied(treeRoot, where, isLike)) {
            return null;
        }
        return treeRoot;
    }


    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 找到从一个节点到顶点的所需节点
     *
     * @param hrUnit 节点
     * @return 返回节点list
     */
    private static List<HrUnit> findAllNodeInThePath(HrUnit hrUnit, boolean containsChildren) {
        //保存结果集
        List<HrUnit> hrUnitList = new ArrayList<>();

        //若开启子查询，则遍历孩子
        if (containsChildren) {
            LinkedList<HrUnit> hrUnitLinkedList = new LinkedList<>();
            hrUnitLinkedList.push(hrUnit);
            HrUnit top;
            while ((top = hrUnitLinkedList.poll()) != null) {
                hrUnitList.add(top);
                if (!CollectionUtils.isEmpty(top.getChildren())) {
                    top.getChildren().forEach(item -> hrUnitLinkedList.push(item));
                }
            }
        }

        //父亲节点遍历
        while (hrUnit != null) {
            hrUnitList.add(hrUnit);
            hrUnit = hrUnit.getParentHrUnit();
        }

        return hrUnitList;
    }

    /**
     * 判断where与SRC是否匹配，
     * 通过所有的GET方法获取的值是否相等进行判断
     *
     * @param src    待判断bean
     * @param where  bean的限制条件
     * @param isLike 是否模糊匹配
     * @return 返回是否匹配
     */
    private static boolean whereIsSatisfied(Object src, Object where, boolean isLike) {
        Method[] whereMethods = where.getClass().getMethods();
        try {
            for (Method whereMethod : whereMethods) {
                if (whereMethod.getName().startsWith("get")) {
                    Object whereMethodValue = whereMethod.invoke(where);
                    Method srcMethod = src.getClass().getMethod(whereMethod.getName());
                    if (srcMethod != null && whereMethodValue != null) {
                        Object srcMethodValue = srcMethod.invoke(src);
                        if (isLike) {
                            if (srcMethodValue == null || !srcMethodValue.toString().contains(String.valueOf(whereMethodValue))) {
                                return false;
                            }
                        } else {
                            if (!whereMethodValue.equals(srcMethodValue)) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("======================================\n反射异常:{}\n=====================================",e);
            return false;
        }
        return true;
    }


    /**
     * 递归形成树状结构<br/>
     *
     * @param parentHrUnit
     * @param hrUnitList
     */
    private static void formatHrUnitTreeInternal(HrUnit parentHrUnit, List<HrUnit> hrUnitList) {
        /**
         * 查找子节点集合, 注意, 一定要通过parentHrUnit.getUnitId().equals(item.getParentUnitId()), 因为很有可能item.getParentUnitId == null(对于顶层节点)
         */
        List<HrUnit> childHrUnitList = hrUnitList.stream().filter(item -> parentHrUnit.getUnitId().equals(item.getParentUnitId())).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(childHrUnitList)) {
            // 递归截止条件
            return;
        } else {
            // 设置父子关系, 并进行下一次递归; 注意, HrUnit.parentHrUnit必须设置为@JsonIgnore, 否则, JSON解析时会出现递归死循环的解析结构, 发生OOM
            parentHrUnit.setChildren(childHrUnitList);
            childHrUnitList.forEach(item -> {
                item.setParentHrUnit(parentHrUnit);
                formatHrUnitTreeInternal(item, hrUnitList);
            });
        }
    }


}
