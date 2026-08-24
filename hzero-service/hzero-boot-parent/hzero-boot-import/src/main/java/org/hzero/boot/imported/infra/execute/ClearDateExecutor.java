package org.hzero.boot.imported.infra.execute;

import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.repository.ImportDataRepository;
import org.hzero.boot.imported.domain.repository.ImportRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 数据清理线程
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/16 15:30
 */
public class ClearDateExecutor implements Runnable {

    private final Import imported;
    private final ImportRepository importRepository;
    private final ImportDataRepository dataRepository;

    public ClearDateExecutor(Import imported,
                             ImportRepository importRepository,
                             ImportDataRepository dataRepository) {
        this.imported = imported;
        this.importRepository = importRepository;
        this.dataRepository = dataRepository;
    }

    @Override
    public void run() {
        // 删除导入记录
        importRepository.deleteByPrimaryKey(imported.getImportId());
        // 分页删除数据
        PageRequest pageRequest = new PageRequest(0, 1000);
        Page<ImportData> dataList;
        do {
            dataList = dataRepository.pageData(imported.getTemplateCode(), imported.getBatch(), null, null, pageRequest);
            if (dataList.isEmpty()) {
                break;
            }
            dataRepository.batchDeleteByPrimaryKey(dataList);
        } while (!dataList.isEmpty());
    }
}
