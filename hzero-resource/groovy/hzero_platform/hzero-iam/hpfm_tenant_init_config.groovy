package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_tenant_init_config.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-07-09-hpfm_tenant_init_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_tenant_init_config_s', startValue:"1")
        }
        createTable(tableName: "hpfm_tenant_init_config", remarks: "租户初始化处理器配置") {
            column(name: "tenant_init_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}  
            column(name: "processor_code", type: "varchar(" + 60 * weight + ")",  remarks: "处理器代码")  {constraints(nullable:"false")}  
            column(name: "processor_name", type: "varchar(" + 128 * weight + ")",  remarks: "处理器名称，表述处理器所做内容")  {constraints(nullable:"false")}  
            column(name: "processor_type", type: "varchar(" + 30 * weight + ")",  remarks: "处理器类型，代码HPFM.TENANT_INIT_PROC_TYPE，PRE_PROCESSOR-前置处理器，POST_PROCESSOR-后置处理器")  {constraints(nullable:"false")}  
            column(name: "init_type", type: "varchar(" + 30 * weight + ")",  remarks: "初始化类型，代码HPFM.TENANT_INIT_TYPE，CREATE-创建，UPDATE-更新")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",   defaultValue:"10",   remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"service_name,processor_code",tableName:"hpfm_tenant_init_config",constraintName: "hpfm_tenant_init_config_u1")
    }
}