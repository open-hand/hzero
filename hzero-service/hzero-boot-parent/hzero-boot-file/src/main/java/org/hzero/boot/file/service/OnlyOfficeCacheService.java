package org.hzero.boot.file.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.hzero.boot.autoconfigure.file.OnlyOfficeConfigProperties;
import org.hzero.boot.file.dto.OnlyOfficeFileDTO;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;

/**
 * description
 *
 * @author fanghan.liu 2020/06/05 10:16
 */
public class OnlyOfficeCacheService {

    private final RedisHelper redisHelper;
    private final OnlyOfficeConfigProperties onlyOfficeConfig;
    private static final String CACHE_FILE_PREFIX = HZeroService.File.CODE + ":only-office:file-info:";
    private static final String CACHE_KEY_PREFIX = HZeroService.File.CODE + ":only-office:key:";

    public OnlyOfficeCacheService(RedisHelper redisHelper, OnlyOfficeConfigProperties onlyOfficeConfig) {
        this.redisHelper = redisHelper;
        this.onlyOfficeConfig = onlyOfficeConfig;
    }

    public void cacheFile(String fileKey, OnlyOfficeFileDTO fileDTO) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.File.REDIS_DB);
            redisHelper.strSet(CACHE_FILE_PREFIX + fileKey, redisHelper.toJson(fileDTO), onlyOfficeConfig.getFileKeyExpire(), TimeUnit.DAYS);
            redisHelper.strSet(CACHE_KEY_PREFIX + fileDTO.getKey(), fileKey, onlyOfficeConfig.getFileKeyExpire(), TimeUnit.DAYS);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    public OnlyOfficeFileDTO getFile(String fileKey) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.File.REDIS_DB);
            return redisHelper.fromJson(redisHelper.strGet(CACHE_FILE_PREFIX + fileKey), OnlyOfficeFileDTO.class);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    public String getFileKey(String key) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.File.REDIS_DB);
            return redisHelper.strGet(CACHE_KEY_PREFIX + key);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    public void refreshKey(OnlyOfficeFileDTO fileVO) {
        String fileKey = fileVO.getFileKey();
        try {
            redisHelper.setCurrentDatabase(HZeroService.File.REDIS_DB);
            redisHelper.delKey(CACHE_FILE_PREFIX + fileKey);
            redisHelper.delKey(CACHE_KEY_PREFIX + fileVO.getKey());
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        String key = System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 7);
        fileVO.setKey(key);
        cacheFile(fileKey, fileVO);
    }
}
