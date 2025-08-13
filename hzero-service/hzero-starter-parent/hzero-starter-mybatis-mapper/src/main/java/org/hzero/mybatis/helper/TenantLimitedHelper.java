package org.hzero.mybatis.helper;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * <p>
 * 租户限制请求
 * </p>
 *
 * @author qingsheng.chen 2019/2/27 星期三 15:26
 */
public class TenantLimitedHelper {
    private static final ThreadLocal<TenantLimited> TENANT_LIMITED_REQUEST = new ThreadLocal<>();

    private TenantLimitedHelper() {
    }

    public static Long tenantId() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            return null;
        }
        return customUserDetails.getTenantId();
    }

    public static List<Long> tenantIds() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            return Collections.emptyList();
        }
        return customUserDetails.getTenantIds();
    }

    public static boolean isOpen() {
        return TENANT_LIMITED_REQUEST.get() != null && BooleanUtils.isTrue(TENANT_LIMITED_REQUEST.get().isEnable);
    }

    public static boolean isEqual() {
        return TENANT_LIMITED_REQUEST.get() != null && BooleanUtils.isTrue(TENANT_LIMITED_REQUEST.get().isEqual);
    }

    public static void open() {
        if (TENANT_LIMITED_REQUEST.get() != null) {
            TENANT_LIMITED_REQUEST.get().isEnable = true;
        } else {
            TENANT_LIMITED_REQUEST.set(new TenantLimited(true));
        }
    }

    public static void open(boolean equals) {
        if (TENANT_LIMITED_REQUEST.get() != null) {
            TENANT_LIMITED_REQUEST.get().isEnable = true;
        } else {
            TENANT_LIMITED_REQUEST.set(new TenantLimited(true, equals));
        }
    }

    public static void close() {
        if (TENANT_LIMITED_REQUEST.get() != null) {
            TENANT_LIMITED_REQUEST.get().isEnable = false;
        } else {
            TENANT_LIMITED_REQUEST.set(new TenantLimited(false));
        }
    }

    public static void clear() {
        TENANT_LIMITED_REQUEST.remove();
    }

    public static class TenantLimited {
        private boolean isEnable;
        private boolean isEqual = false;

        public TenantLimited(boolean isEnable) {
            this.isEnable = isEnable;
        }

        public TenantLimited(boolean isEnable, boolean isEqual) {
            this.isEnable = isEnable;
            this.isEqual = isEqual;
        }

        public boolean isEnable() {
            return isEnable;
        }

        public TenantLimited setEnable(boolean enable) {
            isEnable = enable;
            return this;
        }

        public boolean isEqual() {
            return isEqual;
        }

        public TenantLimited setEqual(boolean equal) {
            isEqual = equal;
            return this;
        }
    }
}
