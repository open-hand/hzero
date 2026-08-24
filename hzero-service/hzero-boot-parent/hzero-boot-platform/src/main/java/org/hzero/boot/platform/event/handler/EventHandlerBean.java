package org.hzero.boot.platform.event.handler;

/**
 * 事件处理类声明，实现该接口的类将用于事件调度器。事件处理类的 beanName 需要配置到对应的事件规则里。<br/>
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 13:58
 */
public interface EventHandlerBean {

    /**
     * 默认返回 null，表示使用 bean 的默认名字<br>
     * 返回一个有效的值，则使用指定的名字<br/>
     *
     * @return beanName
     */
    default String getBeanName() {
        return null;
    }

}
