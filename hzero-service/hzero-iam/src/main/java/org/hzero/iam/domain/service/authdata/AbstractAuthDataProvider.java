package org.hzero.iam.domain.service.authdata;

import org.hzero.iam.domain.repository.AuthDataRepository;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 权限管理提供器的抽象实现
 *
 * @author bo.he02@hand-china.com 2020/05/26 11:31
 */
public abstract class AbstractAuthDataProvider implements AuthDataProvider {
    /**
     * 权限数据仓库对象
     */
    private final AuthDataRepository authDataRepository;

    protected AbstractAuthDataProvider(AuthDataRepository authDataRepository) {
        this.authDataRepository = authDataRepository;
    }

    @Override
    public int getOrder() {
        // 默认使用最低优先级，如果需要自定义处理流程，可以设置更高的优先级，使用自定义逻辑处理
        return Integer.MAX_VALUE;
    }

    /**
     * 根据权限数据类型和权限数据IDs构建权限数据对象
     *
     * @param authDataVo 权限数据对象
     * @return 构建成功的权限数据
     */
    protected List<AuthDataVo> singleAuthDataVo(AuthDataVo authDataVo) {
        if (Objects.isNull(authDataVo)) {
            return Collections.emptyList();
        }

        // 生成权限数据对象
        return Collections.singletonList(authDataVo.setAuthorityTypeCode(this.getAuthorityTypeCode()));
    }

    /**
     * 获取权限数据仓库对象
     *
     * @return 权限数据仓库对象
     */
    protected AuthDataRepository getAuthDataRepository() {
        return this.authDataRepository;
    }
}
