package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov_value.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_lov_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_lov_value_s', startValue:"1")
        }
        createTable(tableName: "hpfm_lov_value", remarks: "LOV独立值集表") {
            column(name: "lov_value_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "lov_id", type: "bigint",  remarks: "lov表Id")  {constraints(nullable:"false")}  
            column(name: "lov_code", type: "varchar(" + 60 * weight + ")",   defaultValue:"",   remarks: "值集代码")  {constraints(nullable:"false")}  
            column(name: "value", type: "varchar(" + 30 * weight + ")",  remarks: "值集值")  {constraints(nullable:"false")}
            column(name: "meaning", type: "varchar(" + 120 * weight + ")",  remarks: "含义")
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "tag", type: "varchar(" + 240 * weight + ")",  remarks: "标记")   
            column(name: "order_seq", type: "int",   defaultValue:"0",   remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "parent_value", type: "varchar(" + 30 * weight + ")",  remarks: "父级LOV值")   
            column(name: "start_date_active", type: "date",  remarks: "有效期起")   
            column(name: "end_date_active", type: "date",  remarks: "有效期止")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "生效标识：1:生效，0:失效")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"0",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_lov_value", indexName: "hpfm_lov_value_n1") {
            column(name: "lov_id")
            column(name: "order_seq")
        }
   createIndex(tableName: "hpfm_lov_value", indexName: "hpfm_lov_value_n2") {
            column(name: "lov_code")
            column(name: "tenant_id")
        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-11-hpfm_lov_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_lov_value", columnName: 'value', newDataType: "varchar(" + 150 * weight + ")")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-07-20-hpfm_lov_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_lov_value", columnName: 'meaning', newDataType: "varchar(" + 480 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-11-26-hpfm_lov_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_lov_value", columnName: 'parent_value', newDataType: "varchar(" + 150 * weight + ")")
    }
}
