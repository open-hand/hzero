package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.CertificateQueryDTO;
import org.hzero.platform.domain.entity.Certificate;

/**
 * CA证书配置资源库
 *
 * @author xingxing.wu@hand-china.com 2019-09-29 10:38:09
 */
public interface CertificateRepository extends BaseRepository<Certificate> {
	/**
	 * @param certificate 查询参数
	 * @param pageRequest 分页参数
	 * @return
	 */
	Page<Certificate> pageAndSortCertificate(CertificateQueryDTO certificate, PageRequest pageRequest);
    
}
