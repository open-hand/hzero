package org.hzero.file.infra.util;

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 删除文件上下文
 *
 * @author berg-turing 2025/02/24
 */
public class DeleteFileContext {
    /**
     * 物理删除标识
     */
    public static final String HEADER_PHYSICAL_DELETE = "physical-delete";

    /**
     * 文件物理删除标识
     * 当标识为true时，代表文件需要被物理删除
     */
    private static final ThreadLocal<Boolean> PHYSICAL_DELETE = new InheritableThreadLocal<>();
    /**
     * 当请求头含有时physical-delete，视为远程调用
     * 远程flag
     */
    private static final ThreadLocal<Boolean> ORIGIN_FLAG = new InheritableThreadLocal<>();

    /**
     * 设置物理删除标识
     *
     * @param value 值
     */
    public static void setPhysical(@Nullable Boolean value) {
        PHYSICAL_DELETE.set(value);
    }

    /**
     * 设置物理删除标识
     *
     * @return 值
     */
    @Nullable
    public static Boolean getPhysical() {
        return PHYSICAL_DELETE.get();
    }

    /**
     * 清理线程变量
     */
    public static void removePhysical() {
        PHYSICAL_DELETE.remove();
    }

    /**
     * 设置物理删除标识
     *
     * @return 值
     */
    @Nullable
    public static Boolean getOrigin() {
        return ORIGIN_FLAG.get();
    }

    /**
     * 设置物理删除标识
     *
     * @param value 值
     */
    public static void setOrigin(@Nullable Boolean value) {
        ORIGIN_FLAG.set(value);
    }

    /**
     * 清理线程变量
     */
    public static void removeOrigin() {
        ORIGIN_FLAG.remove();
    }

    /**
     * 判断是否开启了物理删除
     *
     * @return 是否开启了物理删除
     */
    public static boolean isEnabledPhysical() {
        return BooleanUtils.isTrue(getPhysical());
    }

    /**
     * 判断是否开启了物理删除
     *
     * @return 是否开启了物理删除
     */
    public static boolean isOrigin() {
        return BooleanUtils.isTrue(getOrigin());
    }

    /**
     * 开启物理删除
     */
    public static void enablePhysical() {
        setPhysical(Boolean.TRUE);
    }

    /**
     * 禁用物理删除
     */
    public static void disablePhysical() {
        setPhysical(Boolean.FALSE);
    }

    /**
     * 启用物理删除并执行
     *
     * @param runnable 执行的逻辑
     */
    public static void processWithEnablePhysical(@NonNull Runnable runnable) {
        processWithPhysical(runnable, Boolean.TRUE);
    }

    /**
     * 禁用物理删除并执行
     *
     * @param runnable 执行的逻辑
     */
    public static void processWithDisablePhysical(@NonNull Runnable runnable) {
        processWithPhysical(runnable, Boolean.FALSE);
    }

    /**
     * 操作物理删除的执行
     *
     * @param runnable       执行的逻辑
     * @param physicalDelete 是否物理删除
     */
    public static void processWithPhysical(@NonNull Runnable runnable, @Nullable Boolean physicalDelete) {
        // 原始值
        Boolean sourceValue = getPhysical();

        try {
            // 禁用
            setPhysical(physicalDelete);
            // 执行逻辑
            runnable.run();
        } finally {
            if (Objects.nonNull(sourceValue)) {
                setPhysical(sourceValue);
            } else {
                removePhysical();
            }
        }
    }
}
