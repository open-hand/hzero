package org.hzero.boot.platform.lov.constraints;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Lov限制校验
 * </p>
 *
 * @author qingsheng.chen 2018/7/5 星期四 20:39
 */
public class LovValueConstraint implements ConstraintValidator<LovValue, Object> {
    private static final Logger logger = LoggerFactory.getLogger(LovValueConstraint.class);
    private LovAdapter lovAdapter;
    private String lovCode;
    private boolean mustIn;

    public LovValueConstraint() {
        lovAdapter = ApplicationContextHelper.getContext().getBean(LovAdapter.class);
    }

    @Autowired
    public LovValueConstraint(LovAdapter lovAdapter) {
        this.lovAdapter = lovAdapter;
    }

    @Override
    public void initialize(LovValue constraintAnnotation) {
        lovCode = constraintAnnotation.value();
        mustIn = constraintAnnotation.mustIn();
        if (!StringUtils.hasText(lovCode)) {
            lovCode = constraintAnnotation.lovCode();
        }
    }

    private Set<String> getValues() {
        if (!StringUtils.hasText(lovCode)) {
            return null;
        }
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        return lovAdapter.queryLovValue(lovCode, userDetails != null ? userDetails.getTenantId() : BaseConstants.DEFAULT_TENANT_ID)
                .stream()
                .map(LovValueDTO::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!mustIn) {
            return true;
        }
        Set<String> values = getValues();
        if (CollectionUtils.isEmpty(values)) {
            logger.error("Lov code empty!");
            return true;
        }
        return value == null || values.contains(String.valueOf(value));
    }
}
