package org.hzero.boot.platform.export.feign;

import org.hzero.common.HZeroService;
import org.hzero.core.export.ExportTaskDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author XCXCXCXCX
 * @date 2019/8/5
 * @project hzero-starter-parent
 */
@FeignClient(name = HZeroService.Platform.NAME)
public interface ExportTaskService {

    @PostMapping("/v1/export-task")
    ResponseEntity<?> insert(ExportTaskDTO dto);

    @PutMapping("/v1/export-task")
    ResponseEntity<ExportTaskDTO> update(ExportTaskDTO dto);

}
