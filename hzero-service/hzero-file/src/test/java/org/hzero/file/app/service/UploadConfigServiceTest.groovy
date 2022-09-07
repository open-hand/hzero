package org.hzero.file.app.service

import org.hzero.file.FileApplication
import org.hzero.file.domain.entity.File
import org.hzero.file.domain.entity.UploadConfig
import org.hzero.file.domain.repository.UploadConfigRepository
import org.hzero.file.infra.constant.HfleConstant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Timeout
import spock.lang.Title
import spock.lang.Unroll

/**
 * uploadConfig service 单元测试
 *
 * @author shuangfei.zhu@hand-china.com 2018/10/16 14:13
 */
@SpringBootTest(classes = FileApplication.class)
@ContextConfiguration(loader = SpringBootContextLoader.class)
@Title("Unit test for hzero file uploadConfig service")
@Unroll
@Transactional
@Rollback
@Timeout(10)
class UploadConfigServiceTest extends Specification {

    @Autowired
    UploadConfigService uploadConfigService
    @Autowired
    UploadConfigRepository uploadConfigRepository

    def setup() {
    }

    def "test createUploadConfig"() {
        given: "建立新数据"
        UploadConfig uploadConfig = new UploadConfig()
        uploadConfig.setTenantId(1)
        uploadConfig.setContentType(HfleConstant.ContentType.IMAGE)
        uploadConfig.setStorageSize(10)
        uploadConfig.setStorageUnit(HfleConstant.StorageUnit.MB)

        when: "插入数据成功"
        UploadConfig create = uploadConfigService.createUploadConfig(uploadConfig)
        then: "返回新建的对象"
        create.getUploadConfigId() != null
    }

    def "test updateUploadConfig"() {
        given: "获取数据Id为12"
        Long id = 12
        UploadConfig uploadConfig = uploadConfigRepository.selectByPrimaryKey(id)
        Long objectVersionNumber = uploadConfig.getObjectVersionNumber()
        uploadConfig.setStorageSize(8)

        when: "更改限制大小"
        UploadConfig update = uploadConfigService.updateUploadConfig(uploadConfig)
        then: "更新后的对象"
        update.getObjectVersionNumber() - objectVersionNumber == 1
    }

    def "test deleteUploadConfig"() {
        given:
        Long id = 12

        when: "删除Id为12的记录"
        uploadConfigService.deleteUploadConfig(id)
        then: "删除成功"
        uploadConfigRepository.selectByPrimaryKey(id) == null
    }

    def "test validateFileSize"() {
        given: "准备数据"
        File file = new File()
        file.setFileType("image/jpeg")
        file.setFileName("123.jpg")
        file.setFileSize(21065)
        file.setBucketName("spfm-comp")
        file.setTenantId(2)

        when: "校验文件大小"
        uploadConfigService.validateFileSize(file)

        then: "正常"
    }
}
