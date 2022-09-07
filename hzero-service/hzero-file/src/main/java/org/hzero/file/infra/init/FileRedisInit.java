package org.hzero.file.infra.init;

import java.io.InputStream;
import java.util.Objects;

import org.hzero.core.message.MessageAccessor;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.infra.config.FileConfig;
import org.hzero.file.infra.constant.HfleConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.aspose.words.License;

/**
 * 服务启动时初始化缓存
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/23 14:19
 */
@Component
public class FileRedisInit implements InitializingBean {

    @Autowired
    private CapacityUsedService capacityUsedService;
    @Autowired
    private AsposeFonts asposeFonts;
    @Autowired
    private FileConfig fileConfig;

    private static final Logger logger = LoggerFactory.getLogger(FileRedisInit.class);

    @Override
    public void afterPropertiesSet() {
        capacityUsedService.initUsedRedis();

        if (Objects.equals(fileConfig.getPreviewType(), HfleConstant.PreviewType.AS)) {
            // 加载license
            ClassPathResource classPathResource = new ClassPathResource("license/license.xml");
            try (InputStream inputStream = classPathResource.getInputStream()) {
                License license = new License();
                license.setLicense(inputStream);
            } catch (Exception e) {
                logger.error("Failed to load aspose config !!!!");
            }
            // 补充字体
            asposeFonts.extraAsposeFonts();
        }

        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_hfle");
    }
}
