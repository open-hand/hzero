package org.hzero.boot.scheduler.infra.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.scheduler.api.dto.ChildJobDTO;
import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.hzero.boot.scheduler.api.dto.JobProgress;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.boot.scheduler.infra.feign.SchedulerFeignClient;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * 任务工具
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/25 10:04
 */
public class SchedulerTool {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerTool.class);

    /**
     * 上传文件后缀名
     */
    private String fileSuffix;
    /**
     * 上传的文件
     */
    private ByteArrayOutputStream byteArrayOutputStream;
    private ProgressCache progressCache;
    private final Long logId;
    private JobDataDTO jobDataDTO;

    private RedisHelper redisHelper;
    private FileClient fileClient;
    /**
     * 自定义导出文件
     */
    private String outputFile;
    /**
     * 调度服务的缓存库
     */
    private final Integer schedulerRedis;

    public SchedulerTool(Integer schedulerRedis, Long logId, JobDataDTO jobDataDTO) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        this.schedulerRedis = schedulerRedis;
        this.logId = logId;
        this.jobDataDTO = jobDataDTO;
    }

    private ProgressCache getProgressCache() {
        if (this.progressCache == null) {
            this.progressCache = ApplicationContextHelper.getContext().getBean(ProgressCache.class);
        }
        return this.progressCache;
    }

    private RedisHelper getRedisHelper() {
        if (this.redisHelper == null) {
            this.redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        return this.redisHelper;
    }

    private FileClient getFileClient() {
        if (this.fileClient == null) {
            this.fileClient = ApplicationContextHelper.getContext().getBean(FileClient.class);
        }
        return this.fileClient;
    }

    /**
     * 获取当前任务进度
     */
    public JobProgress getJobProgress() {
        RedisHelper redis = getRedisHelper();
        redis.setCurrentDatabase(schedulerRedis);
        JobProgress progress = this.getProgressCache().getCache(logId, redis);
        redis.clearCurrentDatabase();
        if (progress == null) {
            progress = new JobProgress().setProgress(0).setMessage("");
        }
        return progress;
    }

    /**
     * 更新任务进度
     */
    public void updateProgress(Integer progress) {
        RedisHelper redis = getRedisHelper();
        redis.setCurrentDatabase(schedulerRedis);
        getProgressCache().refreshCache(logId, progress, null, redis);
        redis.clearCurrentDatabase();
    }

    /**
     * 更新任务进度
     */
    public void updateProgress(Integer progress, String message) {
        RedisHelper redis = getRedisHelper();
        redis.setCurrentDatabase(schedulerRedis);
        getProgressCache().refreshCache(logId, progress, message, redis);
        redis.clearCurrentDatabase();
    }

    /**
     * 清除进度缓存
     */
    public void clearProgress(Long logId) {
        RedisHelper redis = getRedisHelper();
        redis.setCurrentDatabase(schedulerRedis);
        getProgressCache().clearRedisCache(logId, redis);
        redis.clearCurrentDatabase();
    }

    /**
     * 添加子任务
     */
    public void addChildJob(ChildJobDTO childJobDTO) {
        SchedulerFeignClient schedulerFeignClient = ApplicationContextHelper.getContext().getBean(SchedulerFeignClient.class);
        ResponseEntity<String> responseEntity = schedulerFeignClient.createChildJob(childJobDTO.getTenantId(), childJobDTO);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            logger.debug("add child job success , jobCode : {}", childJobDTO.getJobCode());
        }
    }

    /**
     * 创建自定义输出文件
     *
     * @param inputStream 文件流
     * @param filename    文件名
     * @return 文件url
     */
    public String buildOutputFile(InputStream inputStream, String filename) {
        FileClient client = getFileClient();
        String url;
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            url = client.uploadFile(getBelongTenantId(), HZeroService.Scheduler.BUCKET_NAME, BootSchedulerConstant.LOG_DIR, filename, data);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
        this.outputFile = url;
        return url;
    }

    public void debug(String str) {
        record(str, BootSchedulerConstant.DEBUG);
        logger.debug(str);
    }

    public void info(String str) {
        record(str, BootSchedulerConstant.INFO);
        logger.info(str);
    }

    public void warn(String str) {
        record(str, BootSchedulerConstant.WARN);
        logger.warn(str);
    }

    public void error(String str) {
        record(str, BootSchedulerConstant.ERROR);
        logger.error(str);
    }

    /**
     * 写日志
     *
     * @param str 日志内容
     */
    private void record(String str, String level) {
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(BaseConstants.Pattern.DATETIME_SSS));
            byteArrayOutputStream.write((String.format("%s [%5s] : %s", time, level, str) + BootSchedulerConstant.WRAP).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    public void record(String str) {
        try {
            byteArrayOutputStream.write((str + BootSchedulerConstant.WRAP).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }


    public void closeLogger() {
        try {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public SchedulerTool setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
        return this;
    }

    public JobDataDTO getJobDataDTO() {
        return jobDataDTO;
    }

    public SchedulerTool setJobDataDTO(JobDataDTO jobDataDTO) {
        this.jobDataDTO = jobDataDTO;
        return this;
    }

    public String getOutputFile() {
        return outputFile;
    }

    /**
     * 任务所属租户
     */
    public Long getBelongTenantId() {
        return jobDataDTO.getTenantId();
    }

    /**
     * 任务创建租户
     */
    public Long getCreatTenantId() {
        return jobDataDTO.getUserInfo().getTenantId();
    }

    /**
     * 创建角色
     */
    public Long getCreatRoleId() {
        return jobDataDTO.getUserInfo().getRoleId();
    }

    /**
     * 创建用户
     */
    public Long getCreatUserId() {
        return jobDataDTO.getUserInfo().getUserId();
    }

    /**
     * 获取任务执行时间
     */
    public Date getRunTime() {
        return new Date(jobDataDTO.getRunTime());
    }

    /**
     * 转换数据类型
     */
    public <T> T conversion(String data, Class<T> clazz) {
        if (StringUtils.isBlank(data) || clazz == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
            return objectMapper.readValue(data, clazz);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }

    /**
     * 转换数据类型
     */
    public <T> T conversion(String data, TypeReference<?> valueTypeRef) {
        if (StringUtils.isBlank(data) || valueTypeRef == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
            return objectMapper.readValue(data, valueTypeRef);
        } catch (IOException e) {
            return null;
        }
    }
}
