package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsrh_index_sync_conf.groovy') {
    changeSet(author: "qi.liu02@hand-china.com", id: "2020-02-26-hsrh_index_sync_conf") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsrh_index_sync_conf_s', startValue:"1")
        }
        createTable(tableName: "hsrh_index_sync_conf", remarks: "索引同步配置表") {
            column(name: "sync_conf_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键。全局ID")  {constraints(primaryKey: true)}
            column(name: "index_id", type: "bigint",  remarks: "关联版本控制表hsrh_index.index_id")  {constraints(nullable:"false")}
            column(name: "sync_conf_code", type: "varchar(" + 20 * weight + ")",  remarks: "同步配置表code")  {constraints(nullable:"false")}  
            column(name: "source_from_code", type: "varchar(" + 480 * weight + ")",  remarks: "数据来源code")  {constraints(nullable:"false")}  
            column(name: "source_from_detail_code", type: "varchar(" + 480 * weight + ")",  remarks: "数据来源详细code")   
            column(name: "source_from_type", type: "varchar(" + 20 * weight + ")",  remarks: "同步来源类型，值集：HSRH.SOURCE_FROM_TYPE")  {constraints(nullable:"false")}  
            column(name: "command_content", type: "longtext",  remarks: "命令内容")   
            column(name: "remark", type: "varchar(" + 255 * weight + ")",  remarks: "备注")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户号")  {constraints(nullable:"false")}
            column(name: "is_record", type: "tinyint",   defaultValue:"0",   remarks: "是否记录到文件,1 启用，0 禁用")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsrh_index_sync_conf", indexName: "hsrh_index_sync_conf_n1") {
            column(name: "index_id")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"index_id,sync_conf_code,tenant_id",tableName:"hsrh_index_sync_conf",constraintName: "hsrh_index_sync_conf_u1")
    }
}