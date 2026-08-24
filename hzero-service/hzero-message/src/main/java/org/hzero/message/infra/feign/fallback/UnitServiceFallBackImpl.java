package org.hzero.message.infra.feign.fallback;

import java.util.List;

import org.hzero.message.api.dto.UnitUserDTO;
import org.hzero.message.infra.feign.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UnitServiceFallBackImpl implements UnitService {

    private static final Logger logger = LoggerFactory.getLogger(UnitServiceFallBackImpl.class);

    @Override
    public ResponseEntity<String> listUnitUsers(List<UnitUserDTO> units) {
        logger.error("Error to list Unit Users, params[units = {}]", units);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> listOpenUnitUsers(List<UnitUserDTO> units, String messageType) {
        logger.error("Error to list Unit Users, params[units = {}], messageType {} ", units, messageType);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
