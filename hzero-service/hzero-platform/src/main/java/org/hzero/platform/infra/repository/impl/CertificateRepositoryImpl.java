package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.CertificateQueryDTO;
import org.hzero.platform.domain.entity.Certificate;
import org.hzero.platform.domain.repository.CertificateRepository;
import org.hzero.platform.infra.mapper.CertificateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CA证书配置 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2019-09-29 10:38:09
 */
@Component
public class CertificateRepositoryImpl extends BaseRepositoryImpl<Certificate> implements CertificateRepository {
	@Autowired
	CertificateMapper certificateMapper;

	@Override
	public Page<Certificate> pageAndSortCertificate(CertificateQueryDTO certificate, PageRequest pageRequest) {
		return PageHelper.doPageAndSort(pageRequest, () -> certificateMapper.selectSortCertificate(certificate));
	}
  
}
