package org.hzero.platform.infra.mapper;

import java.util.List;
import org.hzero.platform.api.dto.CertificateQueryDTO;
import org.hzero.platform.domain.entity.Certificate;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * CA证书配置Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-09-29 10:38:09
 */
public interface CertificateMapper extends BaseMapper<Certificate> {
	/**
	 * @param certificate 查询参数
	 * @return
	 */
	List<Certificate> selectSortCertificate(CertificateQueryDTO certificate);
}
