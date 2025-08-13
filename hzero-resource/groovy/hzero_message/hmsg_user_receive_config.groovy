package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_user_receive_config.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_user_receive_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_user_receive_config_s', startValue:"1")
        }
        createTable(tableName: "hmsg_user_receive_config", remarks: "用户接收配置") {
            column(name: "user_receive_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "bigint",  remarks: "用户id,iam_user.id")  {constraints(nullable:"false")}  
            column(name: "receive_code", type: "varchar(" + 30 * weight + ")",  remarks: "hmsg_user_receive_config .receiver_code")  {constraints(nullable:"false")}  
            column(name: "receive_type", type: "varchar(" + 240 * weight + ")",  remarks: "接收方式")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"user_id,receive_code",tableName:"hmsg_user_receive_config",constraintName: "hmsg_user_receive_config_u1")
    }

    changeSet(author: 'minghui.qiu@hand-china.com', id: '2019-06-05-hmsg_user_receive_config-add') {
        addColumn(tableName: 'hmsg_user_receive_config') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID", afterColumn: 'receive_type') {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "minghui.qiu@hand-china.com", id: "2019-06-21-hmsg_user_receive_config-unique-del-add") {
        dropUniqueConstraint(tableName: "hmsg_user_receive_config", constraintName: "hmsg_user_receive_config_u1")
        addUniqueConstraint(columnNames: "user_id,receive_code,tenant_id", tableName: "hmsg_user_receive_config", constraintName: "hmsg_user_receive_config_u1")
    }
}