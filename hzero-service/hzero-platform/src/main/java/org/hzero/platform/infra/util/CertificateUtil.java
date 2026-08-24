package org.hzero.platform.infra.util;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import sun.security.x509.X509CertImpl;

import io.choerodon.core.exception.CommonException;

/**
 * CA证书文件操作工具类
 *
 * @author xingxingwu.hand-china.com 2019/09/29 10:51
 */
public class CertificateUtil {
	/**
	 * 通过CA证书文件流构建Certificate对象
	 *
	 * @param inputStream CA证书文件流
	 * @return
	 */
	public static Certificate buildCertificate(InputStream inputStream) {
		Certificate certificate = null;
		try {
			//尝试解析为CA证书对象，如果失败说明文件不符合
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			certificate = certificateFactory.generateCertificate(inputStream);
		} catch (CertificateException e) {
			throw new CommonException("error.certificate.file.error");
		}
		return certificate;
	}

	/**
	 * @param certificate CA证书对象
	 */
	public static org.hzero.platform.domain.entity.Certificate parseCertificate(Certificate certificate) {
		org.hzero.platform.domain.entity.Certificate certificateDTO = new org.hzero.platform.domain.entity.Certificate();
		X509CertImpl x509Cert = (X509CertImpl) certificate;
		String SubjectDN = x509Cert.getSubjectDN().getName();
		//解析域名
		String[] split = SubjectDN.split(",");
		for (String s : split) {
			if (s.contains("CN")) {
				String[] CNStr = s.split("=");
				if (CNStr.length > 0) {
					certificateDTO.setDomainName(CNStr[1]);
					break;
				}
			}
		}
		certificateDTO.setStartDate(x509Cert.getNotBefore());
		certificateDTO.setEndDate(x509Cert.getNotAfter());
		certificateDTO.setIssuerDomainName(x509Cert.getIssuerDN().getName());
		return certificateDTO;
	}
}
