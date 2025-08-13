package script.db

databaseChangeLog(logicalFilePath: 'script/db/himp_template_header.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-himp_template_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'himp_template_header_s', startValue:"1")
        }
        createTable(tableName: "himp_template_header", remarks: "通用模板头") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "template_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板代码")  {constraints(nullable:"false")}  
            column(name: "template_name", type: "varchar(" + 240 * weight + ")",  remarks: "模板名")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "template_type", type: "varchar(" + 10 * weight + ")",  remarks: "模板类型")  {constraints(nullable:"false")}  
            column(name: "prefix_patch", type: "varchar(" + 30 * weight + ")",  remarks: "客户端路径前缀")   
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"template_code,tenant_id",tableName:"himp_template_header",constraintName: "himp_template_header_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-11-29-himp_template_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'himp_template_header') {
            column(name: "template_url", type: "varchar(" + 480 * weight + ")",  remarks: "自定义模板地址")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2020-02-28-himp_template_header") {
        addColumn(tableName: 'himp_template_header') {
            column(name: "fragment_flag", type: "tinyint",   defaultValue:"0",   remarks: "分片上传标识")  {constraints(nullable:"false")}
        }
    }
}