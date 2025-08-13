package org.hzero.core.redis;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.hzero.core.redis.convert.DateDeserializer;
import org.hzero.core.redis.convert.DateSerializer;

/**
 * Redis操作工具类 集成封装一些常用方法
 * 
 * @author xianzhi.chen@hand-china.com 2018年6月8日上午11:20:39
 */
@Component
public class RedisHelper implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RedisHelper.class);
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ValueOperations<String, String> valueOpr;
    @Autowired
    private HashOperations<String, String, String> hashOpr;
    @Autowired
    private ListOperations<String, String> listOpr;
    @Autowired
    private SetOperations<String, String> setOpr;
    @Autowired
    private ZSetOperations<String, String> zSetOpr;

    static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Date.class, new DateSerializer());
        javaTimeModule.addDeserializer(Date.class, new DateDeserializer());
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(redisTemplate, "redisTemplate must not be null.");
        Assert.notNull(valueOpr, "redisTemplate must not be null.");
        Assert.notNull(hashOpr, "redisTemplate must not be null.");
        Assert.notNull(listOpr, "redisTemplate must not be null.");
        Assert.notNull(setOpr, "redisTemplate must not be null.");
        Assert.notNull(zSetOpr, "redisTemplate must not be null.");
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /** 默认过期时长，单位：秒 */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24L;

    /** 不设置过期时长 */
    public static final long NOT_EXPIRE = -1;

    /**
     * 获取RedisTemplate对象
     * 
     * @return RedisTemplate
     */
    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 设置当前线程操作 redis database，同一个线程内操作多次redis，不同database，
     * 需要调用 {@link RedisHelper#clearCurrentDatabase()} 清除当前线程redis database，从而使用默认的db.
     * 
     * @param database redis database
     */
    public void setCurrentDatabase(int database) {
        logger.warn("Use default RedisHelper, you'd better use a DynamicRedisHelper instead.");
    }

    /**
     * 清除当前线程 redis database.
     */
    public void clearCurrentDatabase() {
        logger.warn("Use default RedisHelper, you'd better use a DynamicRedisHelper instead.");
    }

    /**
     * 删除key
     * 
     * @param key
     */
    public void delKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除key
     *
     * @param key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * returns -2 if the key does not exist.
     * returns -1 if the key exists but has no associated expire.
     *
     * @param key key
     * @return TTL in seconds, or a negative value in order to signal an error
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * returns -2 if the key does not exist.
     * returns -1 if the key exists but has no associated expire.
     *
     * @param key
     * @param timeUnit
     * @return TTL in seconds, or a negative value in order to signal an error
     */
    public Long getExpire(String key, final TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 设置过期时间,默认一天
     * 
     * @param key key
     * @return
     */
    public Boolean setExpire(String key) {
        return this.setExpire(key, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间,默认时间单位:秒
     * 
     * @param key key
     * @param expire 存活时长
     * @return
     */
    public Boolean setExpire(String key, long expire) {
        return this.setExpire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间
     * 
     * @param key key
     * @param expire 存活时长
     * @param timeUnit 时间单位
     * @return
     */
    public Boolean setExpire(String key, long expire, TimeUnit timeUnit) {
        return this.redisTemplate.expire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
    }

    /**
     * 批量删除Key
     * 
     * @param keys
     */
    public void delKeys(Collection<String> keys) {
        Set<String> hs = new HashSet<>();
        for (String key : keys) {
            hs.add(key);
        }
        redisTemplate.delete(hs);
    }

    /**
     * 删除一个完整的key
     * 
     * @param fullKey 完整的key
     */
    @SuppressWarnings("unused")
    private void deleteFullKey(String fullKey) {
        this.redisTemplate.delete(fullKey);
    }

    /**
     * 批量删除完整的key
     * 
     * @param fullKeys 完整的key集合
     */
    private void deleteFullKeys(Collection<String> fullKeys) {
        this.redisTemplate.delete(fullKeys);
    }

    /**
     * String 设置值
     * 
     * @param key
     * @param value
     * @param expire
     */
    public void strSet(String key, String value, long expire, TimeUnit timeUnit) {
        valueOpr.set(key, value);
        if (expire != NOT_EXPIRE) {
            this.setExpire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
        }
    }

    /**
     * String 设置值
     * 
     * @param key
     * @param value
     */
    public void strSet(String key, String value) {
        valueOpr.set(key, value);
    }

    /**
     * String 获取值
     * 
     * @param key
     * @return
     */
    public String strGet(String key) {
        return valueOpr.get(key);
    }

    /**
     * String 获取值
     * 
     * @param key
     * @param expire
     * @return
     */
    public String strGet(String key, long expire, TimeUnit timeUnit) {
        String value = valueOpr.get(key);
        if (expire != NOT_EXPIRE) {
            this.setExpire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
        }
        return value;
    }

    /**
     * String 获取值
     * 
     * @param key
     * @param clazz
     * @return
     */
    public <T> T strGet(String key, Class<T> clazz) {
        String value = valueOpr.get(key);
        return value == null ? null : fromJson(value, clazz);
    }

    /**
     * String 设置值
     * 
     * @param key
     * @param clazz
     * @param expire
     * @return
     */
    public <T> T strGet(String key, Class<T> clazz, long expire, TimeUnit timeUnit) {
        String value = valueOpr.get(key);
        if (expire != NOT_EXPIRE) {
            this.setExpire(key, expire, timeUnit == null ? TimeUnit.SECONDS : timeUnit);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    /**
     * String 获取值
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String strGet(String key, Long start, Long end) {
        return valueOpr.get(key, start, end);
    }

    /**
     * 如果值不存在则设置（原子操作）
     * @param key
     * @param value
     * @return
     */
    public Boolean strSetIfAbsent(String key, String value) {
        return this.valueOpr.setIfAbsent(key, value);
    }

    /**
     * String 获取自增字段，递减字段可使用delta为负数的方式
     * 
     * @param key
     * @param delta
     * @return
     */
    public Long strIncrement(String key, Long delta) {
        return valueOpr.increment(key, delta);
    }

    /**
     * List 推入数据至列表左端
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lstLeftPush(String key, String value) {
        return listOpr.leftPush(key, value);
    }

    /**
     * List 推入数据至列表左端
     * 
     * @param key
     * @param values Collection集合
     * @return
     */
    public Long lstLeftPushAll(String key, Collection<String> values) {
        return listOpr.leftPushAll(key, values);
    }

    /**
     * List 推入数据至列表右端
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lstRightPush(String key, String value) {
        return listOpr.rightPush(key, value);
    }

    /**
     * List 推入数据至列表右端
     * 
     * @param key
     * @param values Collection集合
     * @return
     */
    public Long lstRightPushAll(String key, Collection<String> values) {
        return listOpr.rightPushAll(key, values);
    }

    /**
     * List 返回列表键key中，从索引start至索引end范围的所有列表项。两个索引都可以是正数或负数
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> lstRange(String key, long start, long end) {
        return listOpr.range(key, start, end);
    }

    /**
     * List 返回列表键key中所有的元素
     * 
     * @param key
     * @return
     */
    public List<String> lstAll(String key) {
        return this.lstRange(key, 0, this.lstLen(key));
    }

    /**
     * List 移除并返回列表最左端的项
     * 
     * @param key
     * @return
     */
    public String lstLeftPop(String key) {
        return listOpr.leftPop(key);
    }

    /**
     * List 移除并返回列表最右端的项
     * 
     * @param key
     * @return
     */
    public String lstRightPop(String key) {
        return listOpr.rightPop(key);
    }

    /**
     * List 移除并返回列表最左端的项
     *
     * @param key
     * @param timeout 等待超时时间
     */
    public String lstLeftPop(String key, long timeout, TimeUnit timeUnit) {
        return listOpr.leftPop(key, timeout, timeUnit);
    }

    /**
     * List 移除并返回列表最右端的项
     *
     * @param key
     * @param timeout 等待超时时间
     */
    public String lstRightPop(String key, long timeout, TimeUnit timeUnit) {
        return listOpr.rightPop(key, timeout, timeUnit);
    }

    /**
     * List 返回指定key的长度
     * 
     * @param key
     * @return
     */
    public Long lstLen(String key) {
        return listOpr.size(key);
    }

    /**
     * List 设置指定索引上的列表项。将列表键 key索引index上的列表项设置为value。 如果index参数超过了列表的索引范围，那么命令返回了一个错误
     * 
     * @param key
     * @param index
     * @param value
     */
    public void lstSet(String key, long index, String value) {
        listOpr.set(key, index, value);
    }

    /**
     * List 根据参数 count的值，移除列表中与参数value相等的元素。 count的值可以是以下几种：count &gt; 0 :从表头开始向表尾搜索，移除与 value相等的元素，数量为
     * count
     * 
     * @param key
     * @param index
     * @param value
     * @return
     */
    public Long lstRemove(String key, long index, String value) {
        return listOpr.remove(key, index, value);
    }

    /**
     * List 返回列表键key中，指定索引index上的列表项。index索引可以是正数或者负数
     * 
     * @param key
     * @param index
     * @return
     */
    public Object lstIndex(String key, long index) {
        return listOpr.index(key, index);
    }

    /**
     * List 对一个列表进行修剪(trim)，让列表只保留指定索引范围内的列表项，而将不在范围内的其它列表项全部删除。 两个索引都可以是正数或者负数
     * 
     * @param key
     * @param start
     * @param end
     */
    public void lstTrim(String key, long start, long end) {
        listOpr.trim(key, start, end);
    }

    /**
     * Set 将数组添加到给定的集合里面，已经存在于集合的元素会自动的被忽略， 命令返回新添加到集合的元素数量。
     * 
     * @param key
     * @param values
     * @return
     */
    public Long setAdd(String key, String[] values) {
        return setOpr.add(key, values);
    }

    /**
     * Set 将一个或多个元素添加到给定的集合里面，已经存在于集合的元素会自动的被忽略， 命令返回新添加到集合的元素数量。
     * 
     * @param key
     * @param values
     * @return
     */
    public Long setIrt(String key, String... values) {
        return setOpr.add(key, values);
    }

    /**
     * Set 将返回集合中所有的元素。
     * 
     * @param key
     * @return
     */
    public Set<String> setMembers(String key) {
        return setOpr.members(key);
    }

    /**
     * Set 检查给定的元素是否存在于集合
     * 
     * @param key
     * @param o
     * @return
     */
    public Boolean setIsmember(String key, String o) {
        return setOpr.isMember(key, o);
    }

    /**
     * Set 返回集合包含的元素数量（也即是集合的基数）
     * 
     * @param key
     * @return
     */
    public Long setSize(String key) {
        return setOpr.size(key);
    }

    /**
     * Set 计算所有给定集合的交集，并返回结果
     * 
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> setIntersect(String key, String otherKey) {
        return setOpr.intersect(key, otherKey);
    }

    /**
     * Set 计算所有的并集并返回结果
     * 
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> setUnion(String key, String otherKey) {
        return setOpr.union(key, otherKey);
    }

    /**
     * Set 计算所有的并集并返回结果
     * 
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> setUnion(String key, Collection<String> otherKeys) {
        return setOpr.union(key, otherKeys);
    }

    /**
     * Set 返回一个集合的全部成员，该集合是所有给定集合之间的差集
     * 
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> setDifference(String key, String otherKey) {
        return setOpr.difference(key, otherKey);
    }

    /**
     * Set 返回一个集合的全部成员，该集合是所有给定集合之间的差集
     * 
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> setDifference(String key, Collection<String> otherKeys) {
        return setOpr.difference(key, otherKeys);
    }

    /**
     * set 删除数据
     * 
     * @param key
     * @param value
     * @return
     */
    public Long setDel(String key, String value) {
        return setOpr.remove(key, value);
    }

    /**
     * Set 批量删除数据
     * 
     * @param key
     * @param value
     * @return
     */
    public Long setRemove(String key, Object[] value) {
        return setOpr.remove(key, value);
    }

    /**
     * ZSet Zadd 命令用于将一个或多个成员元素及其分数值加入到有序集当中。 如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上。
     * 分数值可以是整数值或双精度浮点数。 如果有序集合 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当 key 存在但不是有序集类型时，返回一个错误。
     * 
     * @param key
     * @param value
     * @param score
     */
    public Boolean zSetAdd(String key, String value, double score) {
        return zSetOpr.add(key, value, score);
    }

    /**
     * ZSet 返回有序集合中，指定元素的分值
     * 
     * @param key
     * @param value
     * @return
     */
    public Double zSetScore(String key, String value) {
        return zSetOpr.score(key, value);
    }

    /**
     * ZSet 为有序集合指定元素的分值加上增量increment，命令返回执行操作之后，元素的分值 可以通过将 increment设置为负数来减少分值
     * 
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public Double zSetIncrementScore(String key, String value, double delta) {
        return zSetOpr.incrementScore(key, value, delta);
    }

    /**
     * ZSet 返回指定元素在有序集合中的排名，其中排名按照元素的分值从小到大计算。排名以 0 开始
     * 
     * @param key
     * @param value
     * @return
     */
    public Long zSetRank(String key, String value) {
        return zSetOpr.rank(key, value);
    }

    /**
     * ZSet 返回成员在有序集合中的逆序排名，其中排名按照元素的分值从大到小计算
     * 
     * @param key
     * @param value
     * @return
     */
    public Long zSetReverseRank(String key, String value) {
        return zSetOpr.reverseRank(key, value);
    }

    /**
     * ZSet 返回有序集合的基数
     * 
     * @param key
     * @return
     */
    public Long zSetSize(String key) {
        return zSetOpr.size(key);
    }

    /**
     * ZSet 删除数据
     * 
     * @param key
     * @param value
     * @return
     */
    public Long zSetRemove(String key, String value) {
        return zSetOpr.remove(key, value);
    }

    /**
     * ZSet 根据score区间删除数据
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zSetRemoveByScore(String key, double min, double max) {
        return zSetOpr.removeRangeByScore(key, min, max);
    }


    /**
     * ZSet 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从大到小)的次序排列。 具有相同分数值的成员按字典序的逆序(reverse lexicographical order
     * )排列。
     * 
     * @param key Redis Key
     * @param start
     * @param end
     * @return Set
     */
    public Set<String> zSetRange(String key, Long start, Long end) {
        return zSetOpr.range(key, start, end);
    }

    /**
     * ZSet
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zSetReverseRange(String key, Long start, Long end) {
        return zSetOpr.reverseRange(key, start, end);
    }

    /**
     * ZSet 返回有序集合在按照分值升序排列元素的情况下，分值在 min 和 max范围之内的所有元素
     * 
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> zSetRangeByScore(String key, Double min, Double max) {
        return zSetOpr.rangeByScore(key, min, max);
    }

    /**
     * ZSet 返回有序集合在按照分值降序排列元素的情况下，分值在 min 和 max范围之内的所有元素
     * 
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> zSetReverseRangeByScore(String key, Double min, Double max) {
        return zSetOpr.reverseRangeByScore(key, min, max);
    }

    /**
     * ZSet 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从小到大)的次序排列。 具有相同分数值的成员按字典序的顺序(reverse lexicographical order
     * )排列。
     * 
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return Set
     */
    public Set<String> zSetRangeByScore(String key, Double min, Double max, Long offset, Long count) {
        return zSetOpr.rangeByScore(key, min, max, offset, count);
    }

    /**
     * 返回有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从大到小)的次序排列。 具有相同分数值的成员按字典序的逆序(reverse lexicographical order )排列。
     * 
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Set<String> zSetReverseRangeByScore(String key, Double min, Double max, Long offset, Long count) {
        return zSetOpr.reverseRangeByScore(key, min, max, offset, count);
    }

    /**
     * ZSet 返回有序集合在升序排列元素的情况下，分值在 min和 max范围内的元素数量
     * 
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zSetCount(String key, Double min, Double max) {
        return zSetOpr.count(key, min, max);
    }

    /**
     * Hash 将哈希表 key 中的域 field的值设为 value。如果 key不存在，一个新的哈希表被创建并进行HSET操作。 如果域 field已经存在于哈希表中，旧值将被覆盖
     * 
     * @param key
     * @param hashKey
     * @param value
     */
    public void hshPut(String key, String hashKey, String value) {
        hashOpr.put(key, hashKey, value);
    }

    /**
     * Hash 批量插入值，Map的key代表Field
     * 
     * @param key
     * @param map
     */
    public void hshPutAll(String key, Map<String, String> map) {
        hashOpr.putAll(key, map);
    }

    /**
     * 获取hash对象中的对象序列字符
     * 
     * @param key
     * @param hashKey
     * @return
     */
    public byte[] hshGetSerial(String key, String hashKey) {
        RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
        return redisTemplate.execute((RedisCallback<byte[]>) connection -> {
            try {
                return connection.hGet(redisSerializer.serialize(key), redisSerializer.serialize(hashKey));
            } catch (Exception e) {
                logger.error("获取HASH对象序列失败", e);
            }
            return null;
        });
    }

    /**
     * 插入hash对象序列值
     * 
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hshPutSerial(String key, String hashKey, byte[] value) {
        RedisSerializer<String> redisSerializer = redisTemplate.getStringSerializer();
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            try {
                return connection.hSet(redisSerializer.serialize(key), redisSerializer.serialize(hashKey),
                                value);
            } catch (Exception e) {
                logger.error("插入HASH对象序列失败", e);
            }
            return Boolean.FALSE;
        });
    }

    /**
     * Hash 返回哈希表 key 中给定域 field的值，返回值：给定域的值。当给定域不存在或是给定 key不存在时，返回 nil。
     * 
     * @param key
     * @param hashKey
     * @return
     */
    public String hshGet(String key, String hashKey) {
        return hashOpr.get(key, hashKey);
    }

    /**
     * Hash 返回散列键 key 中，一个或多个域的值，相当于同时执行多个 HGET
     * 
     * @param key
     * @param hashKeys
     * @return
     */
    public List<String> hshMultiGet(String key, Collection<String> hashKeys) {
        return hashOpr.multiGet(key, hashKeys);
    }

    /**
     * Hash 获取散列Key中所有的键值对
     * 
     * @param key
     * @return
     */
    public Map<String, String> hshGetAll(String key) {
        return hashOpr.entries(key);
    }

    /**
     * Hash 查看哈希表 key 中，给定域 field是否存在
     * 
     * @param key
     * @param hashKey
     * @return
     */
    public Boolean hshHasKey(String key, String hashKey) {
        return hashOpr.hasKey(key, hashKey);
    }

    /**
     * Hash 返回哈希表 key 中的所有域
     * 
     * @param key
     * @return
     */
    public Set<String> hshKeys(String key) {
        return hashOpr.keys(key);
    }

    /**
     * Hash 返回散列键 key 中，所有域的值
     * 
     * @param key
     * @return
     */
    public List<String> hshVals(String key) {
        return hashOpr.values(key);
    }

    /**
     * Hash 返回散列键 key中指定Field的域的值
     * 
     * @param key
     * @param hashKeys
     * @return
     */
    public List<String> hshVals(String key, Collection<String> hashKeys) {
        return hashOpr.multiGet(key, hashKeys);
    }

    /**
     * Hash 散列键 key的数量
     * 
     * @param key
     * @return
     */
    public Long hshSize(String key) {
        return hashOpr.size(key);
    }

    /**
     * Hash 删除散列键 key 中的一个或多个指定域，以及那些域的值。不存在的域将被忽略。命令返回被成功删除的域值对数量
     * 
     * @param key
     * @param hashKeys
     */
    public void hshDelete(String key, Object... hashKeys) {
        hashOpr.delete(key, hashKeys);
    }

    /**
     * Hash 删除散列键 key的数组
     * 
     * @param key
     * @param hashKeys
     */
    public void hshRemove(String key, Object[] hashKeys) {
        hashOpr.delete(key, hashKeys);
    }

    /**
     * Object转成JSON数据
     */
    public <T> String toJson(T object) {
        if (object == null) {
            return StringUtils.EMPTY;
        }
        if (object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double
                        || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return StringUtils.EMPTY;
        }
    }

    /**
     * JSON数据，转成Object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json) || clazz == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error(e.getMessage(), e);
            }
            return null;
        }
    }

    /**
     * JSON数据，转成Object
     */
    public <T> T fromJson(String json, TypeReference valueTypeRef) {
        if (StringUtils.isBlank(json) || valueTypeRef == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, valueTypeRef);
        } catch (Exception e) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error(e.getMessage(), e);
            }
            return null;
        }
    }

    /**
     * JSON数据，转成 List&lt;Object&gt;
     */
    public <T> List<T> fromJsonList(String json, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            this.logger.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * 将对象直接以json数据不设置过期时间的方式保存
     *
     * @param key 键
     * @param object
     * @author yunxiang.zhou01@hand-china.com 2018-06-11 13:46
     */
    public <T> void objectSet(String key, T object) {
        this.strSet(key, this.toJson(object));
    }

    /**
     * 根据一个前缀来删除所有匹配的key
     *
     * @deprecated 集群环境不可用
     * @param keyPrefix 前缀
     * @return 删除的数量
     */
    @Deprecated
    public int deleteKeysWithPrefix(String keyPrefix) {
        Set<String> keys = this.keys(keyPrefix + '*');
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        this.deleteFullKeys(keys);
        return keys.size();
    }

    /**
     * 根据表达式获取匹配的所有key
     *
     * @deprecated 集群环境不可用
     * @param pattern 表达式
     * @return 匹配的所有key
     */
    @Deprecated
    public Set<String> keys(String pattern) {
        return this.redisTemplate.keys(pattern);
    }

}
