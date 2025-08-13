package org.hzero.boot.platform.data.permission.builder;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import org.apache.commons.lang3.BooleanUtils;
import org.hzero.boot.platform.data.autoconfigure.PermissionDataProperties;
import org.hzero.boot.platform.data.permission.interceptor.FilterSqlInterceptor;
import org.hzero.boot.platform.data.permission.repository.PermissionSqlRepository;
import org.hzero.boot.platform.data.permission.util.SqlUtils;
import org.hzero.boot.platform.data.permission.vo.PermissionRangeVO;
import org.hzero.mybatis.parser.SqlInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 数据权限sql默认构建器
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/04 17:52
 */
public class PermissionSqlBuilder implements SqlInterceptor {
    public static final Logger logger = LoggerFactory.getLogger(PermissionSqlBuilder.class);
    private final PermissionSqlRepository permissionSqlRepository;
    private final PermissionDataProperties permissionDataProperties;
    private final Set<String> filterSqlInterceptorNames;
    private Set<FilterSqlInterceptor> sqlInterceptorSet;

    private volatile Set<FilterSqlInterceptor> filterSqlInterceptors;
    private ThreadLocal<Boolean> onlyHandlePrefix = new ThreadLocal<>();
    private ThreadLocal<AtomicInteger> tableAliasNumber = new ThreadLocal<>();

    public PermissionSqlBuilder(PermissionSqlRepository permissionSqlRepository,
                                PermissionDataProperties permissionDataProperties,
                                Set<String> filterSqlInterceptorNames) {
        this.permissionSqlRepository = permissionSqlRepository;
        this.permissionDataProperties = permissionDataProperties;
        this.filterSqlInterceptorNames = filterSqlInterceptorNames;
    }

    private void init(Set<FilterSqlInterceptor> sqlInterceptorSet) {
        this.sqlInterceptorSet = sqlInterceptorSet;
    }

    @Override
    public void before() {
        permissionSqlRepository.resetCache();
        onlyHandlePrefix.remove();
        tableAliasNumber.set(new AtomicInteger(0));
    }

    @Override
    public void after() {
        permissionSqlRepository.resetCache();
        onlyHandlePrefix.remove();
    }

    @Override
    public boolean select() {
        return true;
    }

    @Override
    public FromItem handleTable(Table table, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        init(getInitFilterSqlInterceptors());
        return handleTable2FromItem(table, serviceName, sqlId, args, userDetails == null ? DetailsHelper.getAnonymousDetails() : userDetails);
    }
    private FromItem handleTable2FromItem(Table table, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        FromItem fromItem = table;
        PermissionRangeVO permissionRange = getPermissionRange(table, serviceName, sqlId, args, userDetails);
        if (permissionRange != null) {
            if (StringUtils.hasText(permissionRange.getDbPrefix())) {
                String dbPrefix = permissionRange.getDbPrefix();
                if (permissionDataProperties != null && StringUtils.hasText(permissionDataProperties.getDbOwner())) {
                    dbPrefix = dbPrefix + "." + permissionDataProperties.getDbOwner();
                }
                fromItem = SqlUtils.generateTablePrefix(table, dbPrefix);
            }
            if (!CollectionUtils.isEmpty(permissionRange.getSqlList())) {
                if (!BooleanUtils.isTrue(onlyHandlePrefix.get())) {
                    try {
                        onlyHandlePrefix.set(true);
                        fromItem = handleSubSelect(SqlUtils.generateSubSelect(table, permissionRange.getSqlList(), args, userDetails, tableAliasNumber.get().getAndIncrement()),
                                serviceName, sqlId, args, userDetails);
                    } catch (JSQLParserException e) {
                        logger.error("Error generateSubSelect.", e);
                    } finally {
                        onlyHandlePrefix.remove();
                    }
                }
            }
        }
        return fromItem;
    }

    private PermissionRangeVO getPermissionRange(Table table, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        PermissionRangeVO permissionRange = permissionSqlRepository.getPermissionRange(serviceName, table, sqlId, userDetails.getTenantId());
        if (permissionRange == null || CollectionUtils.isEmpty(sqlInterceptorSet)) {
            return permissionRange;
        }
        for (FilterSqlInterceptor sqlInterceptor : sqlInterceptorSet) {
            permissionRange = sqlInterceptor.process(userDetails, table, permissionRange);
        }
        return permissionRange;
    }

    /**
     * lazy load FilterSqlInterceptor beans<br/>
     * 因为直接在configuration的时候加载bean会导致mybatis产生循环依赖<br/>
     * 所以configuration的时候只获取bean名,在具体使用的时候再懒加载<br/>
     *
     * @return InitFilterSqlInterceptors
     */
    private Set<FilterSqlInterceptor> getInitFilterSqlInterceptors() {
        if (CollectionUtils.isEmpty(this.filterSqlInterceptorNames)) {
            return Collections.emptySet();
        }
        if (this.filterSqlInterceptors == null) {
            synchronized (this) {
                if (this.filterSqlInterceptors == null) {
                    // double check
                    Set<FilterSqlInterceptor> temp = new HashSet<>(this.filterSqlInterceptorNames.size());
                    for (String filterSqlInterceptorName : this.filterSqlInterceptorNames) {
                        temp.add((FilterSqlInterceptor) ApplicationContextHelper.getContext()
                                .getBean(filterSqlInterceptorName));
                    }
                    this.filterSqlInterceptors = temp;
                }
            }
        }
        return this.filterSqlInterceptors;
    }
}
