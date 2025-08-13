package org.hzero.platform.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.api.dto.CertificateQueryDTO;
import org.hzero.platform.domain.entity.Certificate;
import org.springframework.web.multipart.MultipartFile;

/**
 * CA证书配置应用服务
 *
 * @author xingxing.wu@hand-china.com 2019-09-29 10:38:09
 */
public interface CertificateService {
	/**
	 * @param certificate 查询参数
	 * @param pageRequest 分页参数
	 * @return
	 */
	Page<Certificate> pageAndSortCertificate(CertificateQueryDTO certificate, PageRequest pageRequest);

	/**
	 * 创建CA证书
	 *
	 * @param tenantId       租户ID
	 * @param customMenuFile customMenuFile
	 * @return
	 */
	Certificate createCertificate(Long tenantId, MultipartFile customMenuFile);

	/**
	 * 创建CA证书
	 *
	 * @param certificateId  CA证书Id
	 * @param customMenuFile customMenuFile
	 * @return CA证书
	 */
	Certificate updateCertificate(Long certificateId, MultipartFile customMenuFile);

	/**
	 * 删除CA证书
	 *
	 * @param certificateId CA证书ID
	 */
	void deleteCertificate(Long certificateId);
}
