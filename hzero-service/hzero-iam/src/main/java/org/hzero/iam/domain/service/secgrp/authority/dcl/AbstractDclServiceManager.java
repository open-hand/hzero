package org.hzero.iam.domain.service.secgrp.authority.dcl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.repository.DocTypeDimensionRepository;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 安全组数据权限服务管理器抽象实现
 *
 * @author bergturing 2020/04/09 11:44
 */
public abstract class AbstractDclServiceManager implements DclServiceManager {
    /**
     * 单据类型维度资源库对象
     */
    @Autowired
    private DocTypeDimensionRepository docTypeDimensionRepository;
    /**
     * 数据权限服务对象
     */
    @Autowired
    private List<DclService> dclServices;

    /**
     * 找到支持处理数据的数据权限服务对象，并进行数据的处理
     *
     * @param authorityTypeCode  权限类型码
     * @param dclServiceFunction 数据权限处理逻辑
     * @param <T>                处理结果泛型
     * @return 处理结果
     */
    protected <T> T findSupportAndProcessR(@Nonnull String authorityTypeCode, @Nonnull Function<DclService, T> dclServiceFunction) {
        AssertUtils.notNull(dclServiceFunction, BaseConstants.ErrorCode.DATA_INVALID);

        // 获取支持处理当前类型的数据权限服务实现
        DclService dclService = this.findSupport(authorityTypeCode);
        try {
            // 处理数据并返回结果
            return dclServiceFunction.apply(dclService);
        } finally {
            // !important【调用清理资源方法】
            dclService.clear();
        }
    }

    /**
     * 找到支持处理数据的数据权限服务对象，并进行数据的处理
     *
     * @param authorityTypeCode  权限类型码
     * @param dclServiceConsumer 数据权限处理逻辑
     */
    protected void findSupportAndProcess(@Nonnull String authorityTypeCode, @Nonnull Consumer<DclService> dclServiceConsumer) {
        AssertUtils.notNull(dclServiceConsumer, BaseConstants.ErrorCode.DATA_INVALID);

        // 获取支持处理当前类型的数据权限服务实现
        DclService dclService = this.findSupport(authorityTypeCode);
        try {
            // 处理数据
            dclServiceConsumer.accept(dclService);
        } finally {
            // !important【调用清理资源方法】
            dclService.clear();
        }
    }

    /**
     * 获取支持处理当前类型的数据权限服务实现
     *
     * @param authorityTypeCode 权限类型码
     * @return 支持处理当前类型的数据权限服务实现
     */
    protected DclService findSupport(@Nonnull String authorityTypeCode) {
        AssertUtils.notNull(authorityTypeCode, BaseConstants.ErrorCode.DATA_INVALID);

        // 数据权限处理服务对象不为空才进行处理，可以减少对数据库数据的查询
        if (CollectionUtils.isNotEmpty(this.dclServices)) {
            // 根据权限类型码查询维度信息
            DocTypeDimension docTypeDimension = this.docTypeDimensionRepository
                    .findDocTypeDimensionByDimensionCode(authorityTypeCode);

            // 遍历数据权限服务实现
            for (DclService dclService : this.dclServices) {
                // 判断是否支持处理当前类型
                if (dclService.support(authorityTypeCode, docTypeDimension)) {
                    // 返回找到的支持处理当前类型的数据权限服务实现
                    return dclService;
                }
            }
        }

        // 没有支持的服务
        throw new CommonException("UnSupported Authority Type Code: {0}", authorityTypeCode);
    }
}
