package org.hzero.platform.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.platform.api.dto.commontemplate.*;
import org.hzero.platform.app.service.CommonTemplateService;
import org.hzero.platform.domain.entity.CommonTemplate;
import org.hzero.platform.domain.repository.CommonTemplateRepository;
import org.hzero.platform.domain.vo.CommonTemplateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

/**
 * 通用模板应用服务默认实现
 *
 * @author bo.he02@hand-china.com 2020-08-04 09:49:14
 */
@Service
public class CommonTemplateServiceImpl implements CommonTemplateService {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonTemplateServiceImpl.class);
    /**
     * Log Tag
     */
    private static final String LOG_TAG = "Platform Common Template";
    /**
     * 通用模板仓库对象
     */
    private final CommonTemplateRepository commonTemplateRepository;

    @Autowired
    public CommonTemplateServiceImpl(CommonTemplateRepository commonTemplateRepository) {
        this.commonTemplateRepository = commonTemplateRepository;
    }

    @Override
    public Page<CommonTemplateDTO> list(CommonTemplateQueryDTO queryDTO, PageRequest pageRequest) {
        // 分页查询
        return PageHelper.doPage(pageRequest, () -> {
            // 执行查询逻辑
            return this.commonTemplateRepository.list(queryDTO);
        });
    }

    @Override
    public CommonTemplateDTO detail(Long organizationId, Long templateId) {
        // 查询数据
        return this.commonTemplateRepository.detail(organizationId, templateId);
    }

    @Override
    public CommonTemplateDTO creation(CommonTemplateCreationDTO creationDTO) {
        if (Objects.isNull(creationDTO)) {
            throw new CommonException("待创建的通用模板参数对象为空");
        }

        // 初始化创建对象
        CommonTemplate commonTemplate = creationDTO.initCreation();
        // 持久化数据
        this.commonTemplateRepository.insertSelective(commonTemplate);
        // 缓存数据
        this.commonTemplateRepository.cache(commonTemplate);

        // 返回结果
        return this.detail(commonTemplate.getTenantId(), commonTemplate.getTemplateId());
    }

    @Override
    public CommonTemplateDTO update(Long organizationId, Long templateId, CommonTemplateUpdateDTO updateDTO) {
        if (Objects.isNull(updateDTO)) {
            throw new CommonException("待更新的通用模板参数对象为空");
        }

        // 查询通用模板数据
        CommonTemplate condition = new CommonTemplate();
        condition.setTenantId(organizationId);
        condition.setTemplateId(templateId);
        CommonTemplate commonTemplate = this.commonTemplateRepository.selectOne(condition);
        if (Objects.isNull(commonTemplate)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }

        // 初始化更新数据对象
        commonTemplate = updateDTO.initUpdate(commonTemplate);
        // 更新数据
        this.commonTemplateRepository.updateByPrimaryKeySelective(commonTemplate);
        // 缓存数据
        this.commonTemplateRepository.cache(commonTemplate);

        // 返回结果
        return this.detail(organizationId, templateId);
    }

    @Override
    public RenderResult render(Long tenantId, String templateCode, String lang, Map<String, Object> args) {
        try {
            // 模板渲染
            String renderContent = this.renderContent(tenantId, templateCode, lang, args);

            // 渲染成功
            return RenderResult.success(renderContent);
        } catch (CommonException e) {
            // 校验失败
            return RenderResult.failure(MessageAccessor.getMessage(e.getCode(), e.getParameters()).getDesc());
        } catch (Exception e) {
            // 渲染失败
            return RenderResult.failure("未知异常: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * 渲染模板内容
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         模板语言
     * @param args         模板参数
     * @return 模板渲染结果
     */
    private String renderContent(Long tenantId, String templateCode, String lang, Map<String, Object> args) {
        // 1. 参数校验
        this.validate(tenantId, templateCode, lang);
        // 2. 渲染模板内容
        LOGGER.debug("开始渲染模板，租户：{}，模板编码：{}，语言：{}", tenantId, templateCode, lang);
        // 3. 获取模板内容
        CommonTemplateVO commonTemplateVO = this.commonTemplateRepository.find(tenantId, templateCode, lang);
        if (Objects.isNull(commonTemplateVO)) {
            throw new RuntimeException(String.format("未找到指定的通用模板数据，租户：%s，模板编码：%s，语言：%s",
                    tenantId, templateCode, lang));
        }
        if (BaseConstants.Flag.NO.equals(commonTemplateVO.getEnabledFlag())) {
            throw new RuntimeException(String.format("当前通用模板已禁用，租户：%s，模板编码：%s，语言：%s",
                    tenantId, templateCode, lang));
        }

        try (StringWriter writer = new StringWriter()) {
            LOGGER.debug("模板配置：{}", commonTemplateVO);
            LOGGER.debug("模板参数: {}", args);

            // 准备渲染参数
            VelocityContext context = new VelocityContext();
            if (MapUtils.isNotEmpty(args)) {
                args.forEach(context::put);
            }
            context.put("Calendar", Calendar.getInstance());
            context.put("StringUtils", StringUtils.class);

            // 开始渲染
            Velocity.init();
            Velocity.evaluate(context, writer, LOG_TAG, commonTemplateVO.getTemplateContent());

            // 返回渲染结果
            return writer.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 参数校验
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         语言
     */
    private void validate(Long tenantId, String templateCode, String lang) {
        if (Objects.isNull(tenantId)) {
            // 通用模板渲染时，通用模板租户ID不能为空
            throw new CommonException("hpfm.boot.common-template.tenant-id.required");
        }

        if (StringUtils.isBlank(templateCode)) {
            // 通用模板渲染时，通用模板编码不能为空
            throw new CommonException("hpfm.boot.common-template.template-code.required");
        }

        if (StringUtils.isBlank(lang)) {
            // 通用模板渲染时，通用模板语言不能为空
            throw new CommonException("hpfm.boot.common-template.lang.required");
        }
    }
}
