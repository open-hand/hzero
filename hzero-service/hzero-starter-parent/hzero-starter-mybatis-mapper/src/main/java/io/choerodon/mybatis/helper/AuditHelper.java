package io.choerodon.mybatis.helper;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.domain.Audit;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * Created by xausky on 3/20/17.
 */
public class AuditHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditHelper.class);
    private static ThreadLocal<Audit> audits = new ThreadLocal<>();

    private AuditHelper() {
    }

    /**
     * 静态初始化
     *
     * @return Audit
     */
    public static Audit audit() {
        Audit audit = audits.get();
        if (audit == null) {
            audit = new Audit();
            audits.set(audit);
        }
        audit.setNow(new Date());
        CustomUserDetails details = DetailsHelper.getUserDetails();
        if (details != null) {
            audit.setUser(details.getUserId());
        } else {
            audit.setUser(BaseConstants.ANONYMOUS_USER_ID);
            LOGGER.warn("principal not instanceof CustomUserDetails audit user is 0L");
        }
        return audit;
    }

    public static void setAudit(Audit audit) {
        DetailsHelper.setCustomUserDetails(audit.getUser(), LanguageHelper.language());
    }

    public static void setAudit(long userId) {
        DetailsHelper.setCustomUserDetails(userId, LanguageHelper.language());
    }

    public static void setAudit(long userId, String lang) {
        DetailsHelper.setCustomUserDetails(userId, lang);
    }
}
