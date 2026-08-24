package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_conc_param.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_conc_param") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_conc_param_s', startValue:"1")
        }
        createTable(tableName: "hsdr_conc_param", remarks: "并发程序参数") {
            column(name: "conc_param_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "concurrent_id", type: "bigint",  remarks: "并发程序ID，hsdr_concurrent.concurrent_id")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",  remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "param_code", type: "varchar(" + 30 * weight + ")",  remarks: "参数名称")  {constraints(nullable:"false")}  
            column(name: "param_name", type: "varchar(" + 120 * weight + ")",  remarks: "参数描述")  {constraints(nullable:"false")}  
            column(name: "param_format_code", type: "varchar(" + 30 * weight + ")",  remarks: "参数格式，HSDR.PARAM_FORMAT")  {constraints(nullable:"false")}  
            column(name: "param_edit_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "编辑类型，HSDR.PARAM_EDIT_TYPE")  {constraints(nullable:"false")}  
            column(name: "notnull_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否必须")  {constraints(nullable:"false")}  
            column(name: "business_model", type: "varchar(" + 1200 * weight + ")",  remarks: "业务模型")   
            column(name: "value_filed_from", type: "varchar(" + 30 * weight + ")",  remarks: "字段值从")   
            column(name: "value_filed_to", type: "varchar(" + 30 * weight + ")",  remarks: "字段值至")   
            column(name: "show_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否展示")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"concurrent_id,param_code,tenant_id",tableName:"hsdr_conc_param",constraintName: "hsdr_conc_param_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-04-30-hsdr_conc_param") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hsdr_conc_param') {
            column(name: "default_value", type: "varchar(" + 240 * weight + ")", remarks: "默认值")
        }
    }
}