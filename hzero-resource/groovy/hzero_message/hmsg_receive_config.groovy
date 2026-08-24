package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_receive_config.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_receive_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_receive_config_s', startValue:"1")
        }
        createTable(tableName: "hmsg_receive_config", remarks: "接收配置") {
            column(name: "receive_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "receive_code", type: "varchar(" + 30 * weight + ")",  remarks: "接收配置编码")  {constraints(nullable:"false")}  
            column(name: "receive_name", type: "varchar(" + 120 * weight + ")",  remarks: "接收配置名称(TL)")  {constraints(nullable:"false")}  
            column(name: "default_receive_type", type: "varchar(" + 480 * weight + ")",  remarks: "默认接收的方式")  {constraints(nullable:"false")}  
            column(name: "parent_receive_id", type: "bigint",  remarks: "上级配置目录")   
            column(name: "level_number", type: "tinyint",  remarks: "层级深度")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"receive_code",tableName:"hmsg_receive_config",constraintName: "hmsg_receive_config_u1")
    }

    changeSet(author: 'minghui.qiu@hand-china.com', id: '2019-06-06-hmsg_receive_config-add') {
        addColumn(tableName: 'hmsg_receive_config') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID", afterColumn: 'level_number') {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "minghui.qiu@hand-china.com", id: "2019-06-21-hmsg_receive_config-unique-del-add") {
        dropUniqueConstraint(tableName: "hmsg_receive_config", constraintName: "hmsg_receive_config_u1")
        addUniqueConstraint(columnNames: "receive_code,tenant_id", tableName: "hmsg_receive_config", constraintName: "hmsg_receive_config_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-11-22-hmsg_receive_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: "hmsg_receive_config") {
            column(name: "parent_receive_code", type: "varchar(" + 30 * weight + ")",  remarks: "上级配置目录编码")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-04-27-hmsg_receive_config") {
        addColumn(tableName: 'hmsg_receive_config') {
            column(name: "editable_flag", type: "tinyint", defaultValue:"1", remarks: "更新标识")  {constraints(nullable:"false")}
        }
    }
}