package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_application_inst_map.groovy') {
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-09-05-hitf_application_inst_map") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_application_inst_map_s', startValue:"1")
        }
        createTable(tableName: "hitf_application_inst_map", remarks: "应用产品实例参数映射") {
            column(name: "application_inst_map_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "application_inst_id", type: "bigint",  remarks: "组合ID")  {constraints(nullable:"false")}  
            column(name: "application_id", type: "bigint",  remarks: "应用产品ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "action_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"REQ",   remarks: "HTTP操作类型，代码HITF.HTTP_ACTION_TYPE")  {constraints(nullable:"false")}  
            column(name: "source_param_id", type: "bigint",  remarks: "来源参数ID")  {constraints(nullable:"false")}  
            column(name: "target_param_id", type: "bigint",  remarks: "目标参数ID")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"application_inst_id,action_type,source_param_id",tableName:"hitf_application_inst_map",constraintName: "hitf_application_inst_map_u1")
    }
}