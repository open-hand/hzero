package org.hzero.platform.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.EventDTO;
import org.hzero.platform.api.dto.EventRuleDTO;
import org.hzero.platform.app.assembler.EventAssembler;
import org.hzero.platform.app.service.EventService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Event;
import org.hzero.platform.domain.entity.EventRule;
import org.hzero.platform.domain.repository.EventRepository;
import org.hzero.platform.domain.repository.EventRuleRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 事件管理 API
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 16:29
 */
@Api(tags = {PlatformSwaggerApiConfig.EVENT})
@RestController("eventController.v1")
@RequestMapping("/v1/{organizationId}/events")
public class EventController extends BaseController {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventRuleRepository eventRuleRepository;
    @Autowired
    private EventService eventService;

    @ApiOperation(value = "查询事件列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "eventCode", value = "事件编码", paramType = "query"),
            @ApiImplicitParam(name = "eventDescription", value = "事件描述", paramType = "query")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<EventDTO>> list(@PathVariable("organizationId") Long organizationId, String eventCode,
                                               String eventDescription, PageRequest pageRequest) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setEventCode(eventCode);
        eventDTO.setEventDescription(eventDescription);
        eventDTO.setTenantId(organizationId);
        eventDTO.setSiteQueryFlag(BaseConstants.Flag.NO);
        return Results.success(eventRepository.page(eventDTO, pageRequest.getPage(), pageRequest.getSize()));
    }

    @ApiOperation(value = "事件及规则列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "eventId", value = "事件ID", paramType = "path", dataType = "long")})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> listEventRule(@PathVariable("eventId") @Encrypt Long eventId) {
        EventDTO event = eventRepository.get(eventId);
        return Results.success(event);
    }

    @ApiOperation(value = "创建事件")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户Id", paramType = "path", required = true)})
    @PostMapping
    public ResponseEntity<EventDTO> create(@PathVariable Long organizationId, @RequestBody Event event) {
        event.setTenantId(organizationId);
        event = eventService.create(event);
        validObject(event);
        return Results.success(EventAssembler.eventEntityToDto(event));
    }

    @ApiOperation(value = "修改事件")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<EventDTO> update(@RequestBody @Encrypt Event event) {
        SecurityTokenHelper.validToken(event, false);
        validObject(event);
        event.validateTenant(eventRepository);
        event = eventService.update(event);
        return Results.success(EventAssembler.eventEntityToDto(event));
    }

    @ApiOperation(value = "批量删除事件")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> batchRemove(@RequestBody @Encrypt List<Event> events) {
        SecurityTokenHelper.validToken(events, false);
        events.forEach(event -> event.validateTenant(eventRepository));
        eventService.batchRemove(events);
        return Results.success();
    }

    @ApiOperation(value = "查询事件规则")
    @ApiImplicitParams({@ApiImplicitParam(name = "eventId", value = "事件ID", paramType = "query"),
            @ApiImplicitParam(name = "eventRuleId", value = "事件规则ID", paramType = "query"),})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{eventId}/rules/{eventRuleId}")
    public ResponseEntity<EventRuleDTO> getEventRule(@PathVariable @Encrypt Long eventId, @PathVariable @Encrypt Long eventRuleId) {
        EventRuleDTO eventRuleDTO = new EventRuleDTO();
        eventRuleDTO.setEventId(eventId);
        eventRuleDTO.setEventRuleId(eventRuleId);
        eventRuleDTO = eventRuleRepository.get(eventRuleDTO);
        return Results.success(eventRuleDTO);
    }

    @ApiOperation(value = "创建事件规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{eventId}/rules")
    public ResponseEntity<EventRuleDTO> createEventRule(@PathVariable @Encrypt Long eventId, @RequestBody EventRule eventRule) {
        validObject(eventRule);
        eventRule = eventService.createEventRule(eventId, eventRule);
        return Results.success(EventAssembler.eventRuleEntityToDto(eventRule));
    }

    @ApiOperation(value = "修改事件规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{eventId}/rules")
    public ResponseEntity<EventRuleDTO> updateEventRule(@PathVariable @Encrypt Long eventId, @RequestBody @Encrypt EventRule eventRule) {
        SecurityTokenHelper.validToken(eventRule);
        validObject(eventRule);
        eventRule = eventService.updateEventRule(eventId, eventRule);
        return Results.success(EventAssembler.eventRuleEntityToDto(eventRule));
    }

    @ApiOperation(value = "批量删除事件规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{eventId}/rules")
    public ResponseEntity<Void> batchRemoveEventRule(@PathVariable @Encrypt Long eventId, @RequestBody @Encrypt List<EventRule> eventRules) {
        SecurityTokenHelper.validToken(eventRules);
        Event event = new Event();
        event.setEventId(eventId);
        eventService.batchRemoveEventRule(eventId, eventRules);
        return Results.success();
    }

    @ApiOperation(value = "批量操作事件规则(新建/更新/删除)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{eventId}/rules/batch")
    public ResponseEntity<List<EventRuleDTO>> batch(@PathVariable @Encrypt Long eventId,
                                                    @RequestBody @Encrypt List<EventRule> eventRules) {
        SecurityTokenHelper.validTokenIgnoreInsert(eventRules);
        this.validList(eventRules);
        return Results.success(this.eventService.batch(eventId, eventRules));
    }

    @ApiOperation(value = "导出事件规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/export")
    @ExcelExport(EventDTO.class)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<EventDTO>> export(@PathVariable("organizationId") Long tenantId, @Encrypt EventDTO event, ExportParam exportParam, HttpServletResponse response, PageRequest pageRequest) {
        event.setTenantId(tenantId);
        List<EventDTO> list = eventRepository.export(event, exportParam, pageRequest);
        return Results.success(list);
    }

}
