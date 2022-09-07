package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_op_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hmnt_audit_op_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_op_config_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_op_config", remarks: "操作审计配置") {
            column(name: "audit_op_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "permission_id", type: "bigint",  remarks: "接口权限ID,iam_permmission.id")  {constraints(nullable:"false")}  
            column(name: "audit_args_flag", type: "tinyint",  remarks: "是否审计请求参数信息")  {constraints(nullable:"false")}  
            column(name: "audit_result_flag", type: "tinyint",  remarks: "是否审计请求响应信息")  {constraints(nullable:"false")}
            column(name: "audit_content", type: "varchar(" + 480 * weight + ")",  remarks: "自定义审计内容，支持SpEL表达式")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"permission_id,tenant_id",tableName:"hmnt_audit_op_config",constraintName: "hmnt_audit_op_config_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-01-04-hmnt_audit_op_config') {
        addColumn(tableName: 'hmnt_audit_op_config') {
            column(name: 'audit_data_flag', type: "tinyint", remarks: '是否审计操作数据') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_config-01') {
        addColumn(tableName: 'hmnt_audit_op_config') {
            column(name: 'audit_type', type: "varchar(60)", defaultValue: "API", remarks: '审计类型') {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_config-02') {
        dropNotNullConstraint(tableName: "hmnt_audit_op_config", columnName: "permission_id", columnDataType: "bigint")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_config-03') {
        addColumn(tableName: 'hmnt_audit_op_config') {
            column(name: 'user_id', type: "bigint", remarks: '用户ID，iam_user.id')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_config-04') {
        addColumn(tableName: 'hmnt_audit_op_config') {
            column(name: 'role_id', type: "bigint", remarks: '角色ID，iam_role.id')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_config-05') {
        addColumn(tableName: 'hmnt_audit_op_config') {
            column(name: 'client_name', type: "varchar(32)", remarks: '客户端，oauth_client.name')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_config-06') {
        dropUniqueConstraint(tableName: 'hmnt_audit_op_config', constraintName: "hmnt_audit_op_config_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_config-07') {
        addUniqueConstraint(columnNames: "permission_id,user_id,role_id,client_name,tenant_id", tableName: "hmnt_audit_op_config", constraintName: "hmnt_audit_op_config_u2")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-08-13-hmnt_audit_op_config-01') {
        addColumn(tableName: 'hmnt_audit_op_config') {
            column(name: 'business_key', type: "varchar(480)", remarks: '业务主键，支持SpEL表达式')
        }
    }
}