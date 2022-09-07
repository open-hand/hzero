package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_process_edge.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_process_edge") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_process_edge_s', startValue:"1")
        }
        createTable(tableName: "hres_process_edge", remarks: "规则流程连线") {
            column(name: "PROCESS_EDGE_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码，HRES_RULE.RULE_CODE")  {constraints(nullable:"false")}  
            column(name: "ID", type: "varchar(" + 60 * weight + ")",  remarks: "节点编码，前端自动生成")  {constraints(nullable:"false")}  
            column(name: "NODE_INDEX", type: "bigint",  remarks: "节点索引")   
            column(name: "SOURCE", type: "varchar(" + 60 * weight + ")",  remarks: "来源节点")  {constraints(nullable:"false")}  
            column(name: "SOURCE_ANCHOR", type: "int",  remarks: "来源节点锚点")   
            column(name: "TARGET", type: "varchar(" + 60 * weight + ")",  remarks: "目标节点")  {constraints(nullable:"false")}  
            column(name: "TARGET_ANCHOR", type: "int",  remarks: "目标节点锚点")   
            column(name: "SHAPE", type: "varchar(" + 60 * weight + ")",  remarks: "节点形状")   
            column(name: "LABEL", type: "varchar(" + 60 * weight + ")",  remarks: "节点标签")   
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"RULE_CODE,ID",tableName:"hres_process_edge",constraintName: "HRES_PROCESS_EDGE_U1")
    }
}