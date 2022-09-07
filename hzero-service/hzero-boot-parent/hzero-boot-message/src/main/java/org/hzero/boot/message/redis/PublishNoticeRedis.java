package org.hzero.boot.message.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.constant.HmsgBootConstant;
import org.hzero.boot.message.entity.NoticeCacheVO;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 获取发布公告缓存
 *
 * @author xiaoyu.zhao@hand-china.com 2019/08/16 15:54
 */
public class PublishNoticeRedis {

    private PublishNoticeRedis() {
    }

    private static RedisHelper redisHelper;

    private static RedisHelper getRedisHelper() {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        redisHelper.setCurrentDatabase(HZeroService.Message.REDIS_DB);
        return redisHelper;
    }

    private static void clear() {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        redisHelper.clearCurrentDatabase();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishNoticeRedis.class);

    public static List<NoticeCacheVO> selectLatestPublishedNotice(Long tenantId, String noticeCategoryCode, String lang, int count) {
        List<NoticeCacheVO> list = new ArrayList<>(count);
        String orderKey = getOrderCacheKey(noticeCategoryCode, tenantId, lang);

        Set<String> noticeIds = getRedisHelper().zSetReverseRangeByScore(orderKey, 0.0, NoticeCacheVO.getNowTimeScore(), 0L, count + 10L);
        int has = 0;
        for (String noticeId : noticeIds) {
            String noticeStr = getRedisHelper()
                    .strGet(HmsgBootConstant.CacheKey.PUBLISHED_NOTICE + BaseConstants.Symbol.COLON + noticeId);
            if (StringUtils.isBlank(noticeStr)) {
                getRedisHelper().zSetRemove(orderKey, noticeId);
                clear();
            } else {
                try {
                    NoticeCacheVO noticeCacheVO = getRedisHelper().fromJson(noticeStr, NoticeCacheVO.class);
                    list.add(noticeCacheVO);
                    if (++has == count) {
                        break;
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                } finally {
                    clear();
                }
            }
        }
        return list;
    }

    /**
     * 获取缓存顺序的KEY
     *
     * @param noticeCategoryCode 公告类别
     * @param tenantId           租户ID
     * @param lang               语言
     * @return KEY
     */
    private static String getOrderCacheKey(String noticeCategoryCode, Long tenantId, String lang) {
        return StringUtils.join(HmsgBootConstant.CacheKey.PUBLISHED_NOTICE_ORDER, ":", tenantId, ":", noticeCategoryCode, ":", lang);
    }
}