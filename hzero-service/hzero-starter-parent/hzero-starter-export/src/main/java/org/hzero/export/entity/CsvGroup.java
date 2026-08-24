package org.hzero.export.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 根据 excel 与 sheet 抽象的csv分组概念
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/10 11:08
 */
public class CsvGroup {

    private List<Csv> csvList = new ArrayList<>();

    public List<Csv> getCsvList() {
        return csvList;
    }

    public CsvGroup setCsvList(List<Csv> csvList) {
        this.csvList = csvList;
        return this;
    }

    public Csv createCsv(String name, String batch) {
        Csv csv = new Csv(this, name, batch);
        // 记录创建的csv
        csvList.add(csv);
        return csv;
    }

    public Csv getCsv(String name) {
        for (Csv csv : csvList) {
            if (Objects.equals(name, csv.getName())) {
                return csv;
            }
        }
        return null;
    }
}
