package org.hzero.file.app.service

import io.choerodon.mybatis.pagehelper.domain.PageRequest
import org.hzero.file.FileApplication
import org.hzero.file.domain.entity.CapacityConfig
import org.hzero.file.domain.repository.CapacityConfigRepository
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
 * capacityConfig service 单元测试
 *
 * @author shuangfei.zhu@hand-china.com 2018/10/16 14:08
 */
@SpringBootTest(classes = FileApplication.class)
@ContextConfiguration(loader = SpringBootContextLoader.class)
@Title("Unit test for hzero file capacityConfig service")
@Unroll
@Transactional
@Rollback
@Timeout(10)
class CapacityConfigServiceTest extends Specification {

    @Autowired
    CapacityConfigService capacityConfigService
    @Autowired
    CapacityConfigRepository capacityConfigRepository

    def setup() {
    }

    def "test pageUploadConfig"() {
        given: "准备数据"
        PageRequest pageRequest = new PageRequest()
        pageRequest.setPage(0)
        pageRequest.setSize(10)
        Long tenantId = 0

        when: "查询"
        CapacityConfig capacityConfig = capacityConfigService.pageUploadConfig(tenantId, pageRequest)
        then: "查询到的主键不为空"
        capacityConfig.getCapacityConfigId() != null
    }

    def "test createCapacityConfig"() {
        given: "初始化数据"
        CapacityConfig capacityConfig = new CapacityConfig()
        capacityConfig.setStorageSize(10)
        capacityConfig.setStorageUnit(HfleConstant.StorageUnit.MB)
        capacityConfig.setTenantId(5)
        capacityConfig.setTotalCapacity(200)
        capacityConfig.setTotalCapacityUnit(HfleConstant.StorageUnit.MB)

        when: "插入数据库"
        CapacityConfig create = capacityConfigService.createCapacityConfig(capacityConfig)
        then: "返回新建的对象"
        create.getCapacityConfigId() != null
    }

    def "test updateCapacityConfig"() {
        given: "获取数据"
        Long id = 3
        CapacityConfig capacityConfig = capacityConfigRepository.selectByPrimaryKey(id)
        capacityConfig.setTotalCapacity(150)

        when: "更新数据库"
        CapacityConfig update = capacityConfigService.updateCapacityConfig(capacityConfig)
        then: "更新成功"
        update.getTotalCapacity() == 150
    }
}
