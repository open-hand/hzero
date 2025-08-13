package org.hzero.apollo.config.client.namespace;

import java.util.HashSet;
import java.util.Set;

/**
 * 命名空间的隔离管理类
 * @author XCXCXCXCX
 */
public class NamespaceManager {

    private static final Set<String> NAMESPACES = new HashSet<>();

    public static Set<String> get() {
        return NAMESPACES;
    }

    public static void addNamespace(String namespace){
        NAMESPACES.add(namespace);
    }

}
