package org.hzero.boot.imported.app.service.impl;

import java.io.InputStream;
import java.util.Map;

import org.hzero.boot.imported.app.service.ImportDataService;
import org.hzero.fragment.service.FileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/18 16:11
 */
@Service
public class FragmentHandler implements FileHandler {

    @Autowired
    private ImportDataService importDataService;

    @Override
    public String process(Long tenantId, String filename, String filePath, InputStream inputStream, Map<String, String> params) {
        String templateCode = params.get("templateCode");
        String param = null;
        if (params.containsKey("param")) {
            param = params.get("param");
        }
        return importDataService.uploadData(tenantId, templateCode, param, inputStream, filename);
    }
}
