package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_lov") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_lov_s', startValue:"1")
        }
        createTable(tableName: "hpfm_lov", remarks: "LOV表") {
            column(name: "lov_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "lov_code", type: "varchar(" + 60 * weight + ")",  remarks: "LOV代码")  {constraints(nullable:"false")}  
            column(name: "lov_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "LOV数据类型: URL/SQL/FIXED")  {constraints(nullable:"false")}  
            column(name: "route_name", type: "varchar(" + 120 * weight + ")",  remarks: "目标路由")   
            column(name: "lov_name", type: "varchar(" + 240 * weight + ")",   defaultValue:"",   remarks: "值集名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "parent_lov_code", type: "varchar(" + 30 * weight + ")",  remarks: "父级LOV CODE")   
            column(name: "parent_tenant_id", type: "bigint",  remarks: "父级值集租户ID")   
            column(name: "custom_sql", type: "longtext",  remarks: "自定义sql")   
            column(name: "custom_url", type: "varchar(" + 600 * weight + ")",  remarks: "查询URL")
            column(name: "value_field", type: "varchar(" + 30 * weight + ")",  remarks: "值字段")   
            column(name: "value_table_alias", type: "varchar(" + 30 * weight + ")",  remarks: "值字段所在表别名")   
            column(name: "display_field", type: "varchar(" + 30 * weight + ")",  remarks: "显示字段")   
            column(name: "meaning_table_alias", type: "varchar(" + 30 * weight + ")",  remarks: "含义字段所在表别名")   
            column(name: "must_page_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否必须分页")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"0",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_lov", indexName: "hpfm_lov_n1") {
            column(name: "parent_lov_code")
        }

        addUniqueConstraint(columnNames:"lov_code,tenant_id",tableName:"hpfm_lov",constraintName: "hpfm_lov_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-09-06-hpfm_lov") {
        addColumn(tableName: 'hpfm_lov') {
            column(name: "translation_sql", type: "longtext", remarks: "翻译sql")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com",id: "2019-09-07-hpfm_lov"){
        dropColumn(tableName: "hpfm_lov",columnName:"value_table_alias" )
        dropColumn(tableName: "hpfm_lov",columnName:"meaning_table_alias" )
    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2020-06-15-hpfm_lov") {
        addColumn(tableName: 'hpfm_lov') {
            column(name: "public_flag", type: "tinyint", defaultValue: 0, remarks: "是否公开权限，0:不公开 1:公开") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2020-06-30-hpfm_lov") {
        addColumn(tableName: 'hpfm_lov') {
            column(name: "encrypt_field", type: "varchar(480)", remarks: "加密字段")
        }
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-08-06-hpfm_lov") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_lov", columnName: 'parent_lov_code', newDataType: "varchar(" + 60 * weight + ")")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2020-09-09-hpfm_lov") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_lov') {
            column(name: "decrypt_field", type: "varchar(" + 480 * weight + ")", remarks: "存储解密字段")
        }
    }
}
