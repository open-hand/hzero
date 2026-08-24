package org.hzero.boot.platform.lov.handler;

import org.hzero.boot.platform.lov.annotation.LovValue;

/**
 * 值集值处理类
 *
 * @author gaokuo.dai@hand-china.com 2018年7月3日下午7:16:25
 */
public interface LovValueHandle {

    /**
     * <p>将传入对象的指定字段进行处理</p>
     * <p>扫描指定字段中的对象,将其中用{@link LovValue}注解的字段按其配置的规则得到值集值meaning并回写到对象中</p>
     * <p>如果targetFields为空或包含空字符串("")时,将会扫描传入对象本身</p>
     * @param targetFields 指定字段
     * @param result 传入对象
     * @return 处理后的字段
     */
    Object process(String[] targetFields, Object result);
    
}
