package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_template_server.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_template_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_template_server_s', startValue:"1")
        }
        createTable(tableName: "hmsg_template_server", remarks: "消息模板账户") {
            column(name: "temp_server_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "message_code", type: "varchar(" + 30 * weight + ")",  remarks: "消息代码")  {constraints(nullable:"false")}  
            column(name: "message_name", type: "varchar(" + 120 * weight + ")",  remarks: "消息名称")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"tenant_id,message_code",tableName:"hmsg_template_server",constraintName: "hmsg_template_server_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-04-23-hmsg_template_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hmsg_template_server') {
            column(name: "category_code", type: "varchar(" + 30 * weight + ")", remarks: "分类代码,值集:HMSG.TEMP_SERVER.CATEGORY")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-04-24-hmsg_template_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hmsg_template_server') {
            column(name: "subcategory_code", type: "varchar(" + 30 * weight + ")", remarks: "子类别代码,值集:HMSG.TEMP_SERVER.SUBCATEGORY")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-04-27-hmsg_template_server") {
        addColumn(tableName: 'hmsg_template_server') {
            column(name: "receive_config_flag", type: "tinyint", defaultValue:"0", remarks: "自定义配置接收标识")  {constraints(nullable:"false")}
        }
    }


    changeSet(author: "hzero@hand-china.com", id: "2020-05-22-hmsg_template_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hmsg_template_server') {
            column(name: "description", type: "varchar(" + 480 * weight + ")", remarks: "描述")
        }
    }
}