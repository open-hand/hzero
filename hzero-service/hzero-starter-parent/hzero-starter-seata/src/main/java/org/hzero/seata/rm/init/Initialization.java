package org.hzero.seata.rm.init;

/**
 * @author XCXCXCXCX
 * @date 2020/4/17 4:40 下午
 */
public interface Initialization {

    default void exec(){
        try {
            doExec();
        } catch (Throwable e) {
            if (!allowFailure()) {
                throw e;
            }
            //ignore ex if allow failure
        }
    }

    void doExec();

    boolean allowFailure();

}
