package script.db

databaseChangeLog(logicalFilePath: 'script/db/hres_process_node.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hres_process_node") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hres_process_node_s', startValue:"1")
        }
        createTable(tableName: "hres_process_node", remarks: "规则流程节点") {
            column(name: "PROCESS_NODE_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "RULE_CODE", type: "varchar(" + 60 * weight + ")",  remarks: "规则编码，HRES_RULE.RULE_CODE")  {constraints(nullable:"false")}  
            column(name: "ID", type: "varchar(" + 60 * weight + ")",  remarks: "节点编码，前端自动生成")  {constraints(nullable:"false")}  
            column(name: "COMPONENT_TYPE", type: "varchar(" + 60 * weight + ")",  remarks: "节点组件类型")  {constraints(nullable:"false")}  
            column(name: "COMPONENT_TYPE_DESC", type: "varchar(" + 240 * weight + ")",  remarks: "节点组件类型描述")   
            column(name: "COMPONENT_NAME", type: "varchar(" + 60 * weight + ")",  remarks: "节点组件名称")   
            column(name: "COLOR", type: "varchar(" + 30 * weight + ")",  remarks: "节点颜色")   
            column(name: "NODE_INDEX", type: "bigint",  remarks: "节点索引")   
            column(name: "SHAPE", type: "varchar(" + 60 * weight + ")",  remarks: "节点形状")   
            column(name: "NODE_SIZE", type: "varchar(" + 60 * weight + ")",  remarks: "节点大小")   
            column(name: "LABEL", type: "varchar(" + 60 * weight + ")",  remarks: "节点标签")   
            column(name: "NODE_TYPE", type: "varchar(" + 60 * weight + ")",  remarks: "节点类型")   
            column(name: "X", type: "bigint",  remarks: "节点位置X")   
            column(name: "Y", type: "bigint",  remarks: "节点位置Y")   
            column(name: "ICON", type: "longtext",  remarks: "节点图标")   
            column(name: "LABEL_OFFSET_Y", type: "bigint",  remarks: "标签文字偏移量")   
            column(name: "TENANT_ID", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"RULE_CODE,ID",tableName:"hres_process_node",constraintName: "HRES_PROCESS_NODE_U1")
    }
}