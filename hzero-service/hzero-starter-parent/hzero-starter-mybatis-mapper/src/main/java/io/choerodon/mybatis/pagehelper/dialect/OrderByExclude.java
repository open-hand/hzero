package io.choerodon.mybatis.pagehelper.dialect;

import io.choerodon.core.oauth.CustomUserDetails;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.Map;

/**
 * @author 废柴 2020/8/25 16:43
 */
public class OrderByExclude extends AbstractOrderByExclude {
    @Override
    public PlainSelect handlePlainSelect(PlainSelect plainSelect, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        plainSelect.setOrderByElements(null);
        return super.handlePlainSelect(plainSelect, serviceName, sqlId, args, userDetails);
    }
}
