package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_report.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-hrpt_report") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_report_s', startValue:"1")
        }
        createTable(tableName: "hrpt_report", remarks: "报表信息") {
            column(name: "report_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "report_uuid", type: "varchar(" + 50 * weight + ")",   defaultValue:"",   remarks: "报表UUID")  {constraints(nullable:"false")}
            column(name: "report_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "报表类型，值集:HRPT.REPORT_TYPE")  {constraints(nullable:"false")}
            column(name: "report_code", type: "varchar(" + 30 * weight + ")",  remarks: "报表代码")  {constraints(nullable:"false")}
            column(name: "report_name", type: "varchar(" + 120 * weight + ")",  remarks: "报表名称")  {constraints(nullable:"false")}
            column(name: "dataset_id", type: "bigint",  remarks: "数据集，hrpt_dataset.dataset_id")  {constraints(nullable:"false")}
            column(name: "meta_columns", type: "longtext",  remarks: "报表列集合元数据(JSON格式)")
            column(name: "options", type: "longtext",  remarks: "报表配置选项(JSON格式)")
            column(name: "template_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板类型，值集:HRPT.TEMPLATE_TYPE")
            column(name: "page_flag", type: "tinyint",   defaultValue:"0",   remarks: "分页标识，0:不分页 1:分页")  {constraints(nullable:"false")}
            column(name: "limit_rows", type: "int",   defaultValue:"20",   remarks: "每页条数")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")
            column(name: "order_seq", type: "int",  remarks: "排序号")
            column(name: "remark", type: "longtext",  remarks: "备注说明")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
   createIndex(tableName: "hrpt_report", indexName: "hrpt_report_n1") {
            column(name: "report_uuid")
        }

        addUniqueConstraint(columnNames:"report_code,tenant_id",tableName:"hrpt_report",constraintName: "hrpt_report_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-12-11-hrpt_report") {
        addColumn(tableName: 'hrpt_report') {
            column(name: "async_flag", type: "tinyint", defaultValue: "0",  remarks: "异步标识")  {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-09-04-hrpt_report") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hrpt_report') {
            column(name: "export_type", type: "varchar(" + 120 * weight + ")",  remarks: "导出类型")
        }
    }
}