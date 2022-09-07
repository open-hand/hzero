package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_server_cluster.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-02-hpfm_server_cluster") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_server_cluster_s', startValue:"1")
        }
        createTable(tableName: "hpfm_server_cluster", remarks: "服务器集群设置表") {
            column(name: "CLUSTER_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "CLUSTER_CODE", type: "varchar(" + 30 * weight + ")",  remarks: "代码")  {constraints(nullable:"false")}  
            column(name: "CLUSTER_NAME", type: "varchar(" + 80 * weight + ")",  remarks: "名称")  {constraints(nullable:"false")}  
            column(name: "CLUSTER_DESCRIPTION", type: "varchar(" + 240 * weight + ")",  remarks: "说明")
            column(name: "ENABLED_FLAG", type: "tinyint",   defaultValue:"1",   remarks: "是否启用 1:启用 0：不启用")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint", defaultValue:"0",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames: "CLUSTER_CODE,tenant_id", tableName: "hpfm_server_cluster", constraintName: "hpfm_server_cluster_u1")
    }
}