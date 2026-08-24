package org.hzero.boot.platform.certificate.helper;

import io.choerodon.core.convertor.ApplicationContextHelper;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.springframework.util.Assert;

/**
 * 证书缓存帮助类
 *
 * @author xingxingwu.hand-china.com 2019/09/29 11:25
 */
public class CertificateCacheHelper {
    /**
     * 接口平台证书缓存key前缀
     */
    public static final String PLATFORM_CERTIFICATE_PREFIX = HZeroService.Platform.CODE + ":certificate";

    /**
     * 获取缓存中证书
     *
     * @param certificateId 证书ID
     * @return 证书二进制
     */
    public static byte[] getCertificateFromRedis(Long certificateId) {
        Assert.notNull(certificateId, BaseConstants.ErrorCode.DATA_INVALID);
        RedisHelper redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        try {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            String cacheKey = PLATFORM_CERTIFICATE_PREFIX;
            String hshKey = certificateId + "";
            byte[] bytes = redisHelper.hshGetSerial(cacheKey, hshKey);
            return bytes;
        } finally {
            redisHelper.clearCurrentDatabase();
        }

    }

    /**
     * 写入证书到缓存
     *
     * @param certificateId 证书ID
     * @param certificateBytes 证书二进制
     * 
     */
    public static void addCertificateToRedis(Long certificateId, byte[] certificateBytes) {
        Assert.notNull(certificateId, BaseConstants.ErrorCode.DATA_INVALID);
        RedisHelper redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        try {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            String cacheKey = PLATFORM_CERTIFICATE_PREFIX;
            String hshKey = certificateId + "";
            redisHelper.hshPutSerial(cacheKey, hshKey, certificateBytes);
        } finally {
            redisHelper.clearCurrentDatabase();
        }

    }

    /**
     * 删除缓存中证书
     *
     * @param certificateId 证书ID
     * 
     */
    public static void deleteCertificateToRedis(Long certificateId) {
        Assert.notNull(certificateId, BaseConstants.ErrorCode.DATA_INVALID);
        RedisHelper redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        try {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            String cacheKey = PLATFORM_CERTIFICATE_PREFIX;
            String hshKey = certificateId + "";
            redisHelper.hshDelete(cacheKey, hshKey);
        } finally {
            redisHelper.clearCurrentDatabase();
        }

    }

}
