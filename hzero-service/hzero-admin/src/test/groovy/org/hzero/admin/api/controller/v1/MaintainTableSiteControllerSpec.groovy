package org.hzero.admin.api.controller.v1

import org.hzero.admin.domain.entity.Maintain
import org.hzero.admin.domain.entity.MaintainTable
import org.hzero.admin.domain.repository.MaintainRepository
import org.hzero.admin.domain.repository.MaintainTableRepository
import org.mockito.internal.matchers.Contains
import org.spockframework.util.IoUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Title

/**
 * @author XCXCXCXCX* @date 2020/6/24 11:19 上午
 */
@Title("运维表接口测试")
class MaintainTableSiteControllerSpec extends ControllerSpec {

    @Autowired
    private MaintainTableSiteController controller;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private MaintainRepository maintainRepository;
    @Autowired
    private MaintainTableRepository maintainTableRepository;

    private MockMvc mockMvc;

    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    def "模版下载"() {
        expect:
        def content = mockMvc.perform(MockMvcRequestBuilders.post("/v1/maintain-tables/download-template")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.header().string("Content-Disposition", new Contains("attachment; filename=")))
                .andExpect(MockMvcResultMatchers.content().contentType("application/octet-stream; charset=utf-8"))
                .andReturn().getResponse().getContentAsString();
        println(content);
    }

    def "导入数据"() {
        given:
        Maintain maintain = new Maintain();
        maintain.setMaintainVersion("import_unit_test");
        maintain.setDescription("单元测试");
        maintainRepository.insertSelective(maintain);
        Resource resource = context.getResource("classpath:template/maintain/template.properties")
        InputStream is = resource.getInputStream();

        expect:
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/maintain-tables/import-data")
                .contentType("multipart/form-data;")
                .param("maintainId", String.valueOf(maintain.getMaintainId()))
                .content(IoUtil.getText(is)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        List<MaintainTable> tableList = maintainTableRepository.selectMaintainTables(maintain.getMaintainId(), null);
        tableList.size() > 0;
        println(objectMapper.writeValueAsString(tableList));
    }

}
