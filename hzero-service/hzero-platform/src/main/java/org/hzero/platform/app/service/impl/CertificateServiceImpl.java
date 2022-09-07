package org.hzero.platform.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.util.Objects;
import org.hzero.boot.platform.certificate.helper.CertificateCacheHelper;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.api.dto.CertificateQueryDTO;
import org.hzero.platform.app.service.CertificateService;
import org.hzero.platform.domain.entity.Certificate;
import org.hzero.platform.domain.repository.CertificateRepository;
import org.hzero.platform.infra.util.CertificateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/**
 *  CA证书配置应用服务
 *
 * @author xingxing.wu@hand-china.com 2019-09-29 10:38:09
 */
@Service
public class CertificateServiceImpl implements CertificateService {
	@Autowired
	private CertificateRepository certificateRepository;

	@Override
	public Page<Certificate> pageAndSortCertificate(CertificateQueryDTO certificate, PageRequest pageRequest) {
		return certificateRepository.pageAndSortCertificate(certificate, pageRequest);
	}

	@Override
	public Certificate createCertificate(Long tenantId, MultipartFile customMenuFile) {
		java.security.cert.Certificate certificateEntity = null;
		byte[] certificateEncoded;
		//转化文件流为Certificate对象
		try {
			InputStream inputStream = customMenuFile.getInputStream();
			certificateEntity = CertificateUtil.buildCertificate(inputStream);
		} catch (IOException e) {
			throw new CommonException("hpfm.error.certificate.file.error");
		}
		if (certificateEntity == null) {
			throw new CommonException("hpfm.error.certificate.not_null");
		}
		//获取二进制流
		try {
			certificateEncoded = customMenuFile.getBytes();
		} catch (IOException e) {
			throw new CommonException("hpfm.error.certificate.file.error");
		}

		Certificate certificate = CertificateUtil.parseCertificate(certificateEntity);
		//写入数据库
		certificate.setData(certificateEncoded);
		certificate.setTenantId(tenantId);
		//唯一性校验
		certificate.checkUnique(certificateRepository);
		certificateRepository.insertSelective(certificate);
		//写入缓存
		CertificateCacheHelper
				.addCertificateToRedis(certificate.getCertificateId(), certificateEncoded);
		return certificate;
	}

	@Override
	public Certificate updateCertificate(Long certificateId, MultipartFile customMenuFile) {
		Certificate certificate = certificateRepository.selectByPrimaryKey(certificateId);
		Assert.notNull(certificate, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
		byte[] certificateEncoded;
		java.security.cert.Certificate certificateEntity = null;
		//转化文件流为Certificate对象
		try {
			InputStream inputStream = customMenuFile.getInputStream();
			certificateEntity = CertificateUtil.buildCertificate(inputStream);
		} catch (IOException e) {
			throw new CommonException("hpfm.error.certificate.file.error");
		}
		//获取二进制流
		try {
			certificateEncoded = customMenuFile.getBytes();
		} catch (IOException e) {
			throw new CommonException("hpfm.error.certificate.file.error");
		}
		Certificate newCertificate = CertificateUtil.parseCertificate(certificateEntity);
		if (!Objects.equals(newCertificate.getDomainName(), certificate.getDomainName())) {
			throw new CommonException("hpfm.error.certificate.name.not_match");

		}
		certificate.setData(certificateEncoded);
		certificateRepository.updateOptional(certificate, Certificate.FIELD_DATA);
		//写入缓存
		CertificateCacheHelper
				.addCertificateToRedis(certificate.getCertificateId(), certificateEncoded);
		return null;
	}

	@Override
	public void deleteCertificate(Long certificateId) {
		certificateRepository.deleteByPrimaryKey(certificateId);
		CertificateCacheHelper
				.deleteCertificateToRedis(certificateId);
	}


}
