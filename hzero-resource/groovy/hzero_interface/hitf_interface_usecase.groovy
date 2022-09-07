package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_usecase.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_interface_usecase") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_interface_usecase_s', startValue:"1")
        }
        createTable(tableName: "hitf_interface_usecase", remarks: "接口测试用例") {
            column(name: "interface_usecase_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "interface_id", type: "bigint",  remarks: "接口ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "usecase_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"GENERAL",   remarks: "用例类型，代码HITF.USECASE_TYPE")  {constraints(nullable:"false")}  
            column(name: "usecase_name", type: "varchar(" + 128 * weight + ")",  remarks: "用例名称")   
            column(name: "req_mime_type", type: "varchar(" + 80 * weight + ")",  remarks: "请求体MIME类型，代码HITF.MIME_TYPE")   
            column(name: "req_raw_flag", type: "tinyint",  remarks: "请求体是否Raw类型数据")   
            column(name: "req_root_type", type: "varchar(" + 30 * weight + ")",  remarks: "请求体根节点类型，代码HITF.ROOT_TYPE")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"usecase_type,usecase_name,interface_id,tenant_id",tableName:"hitf_interface_usecase",constraintName: "hitf_interface_usecase_u1")
    }
}