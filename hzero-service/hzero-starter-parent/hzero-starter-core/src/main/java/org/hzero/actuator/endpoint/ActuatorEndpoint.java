package org.hzero.actuator.endpoint;

import org.hzero.actuator.strategy.ActuatorStrategy;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.choerodon.swagger.annotation.Permission;


@Controller
@ApiIgnore
public class ActuatorEndpoint {

    private final List<ActuatorStrategy> actuatorStrategies;

    public ActuatorEndpoint(List<ActuatorStrategy> actuators) {
        this.actuatorStrategies = Optional.ofNullable(actuators).orElse(Collections.emptyList());
    }

    @ResponseBody
    @GetMapping("/v2/actuator/{key}")
    @Permission(permissionWithin = true)
    public ResponseEntity<Object> query(HttpServletRequest request, @PathVariable String key) {
        for (ActuatorStrategy actuatorStrategy : actuatorStrategies) {
            if (actuatorStrategy.getType().equalsIgnoreCase(key)) {
                return Results.success(actuatorStrategy.queryActuatorData(request));
            }
        }
        return Results.success();
    }
}
