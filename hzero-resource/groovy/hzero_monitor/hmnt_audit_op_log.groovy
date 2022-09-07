package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_op_log.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hmnt_audit_op_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_op_log_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_op_log", remarks: "操作审计记录") {
            column(name: "log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "操作审计数据来源服务")  {constraints(nullable:"false")}  
            column(name: "user_id", type: "bigint",  remarks: "操作用户，iam_user.user_id")   
            column(name: "audit_content", type: "varchar(" + 480 * weight + ")",  remarks: "操作审计内容")  {constraints(nullable:"false")}  
            column(name: "audit_datetime", type: "datetime",  remarks: "操作审计发生时间")  {constraints(nullable:"false")}  
            column(name: "audit_result", type: "varchar(" + 30 * weight + ")",  remarks: "操作审计结果，值集HMNT.AUDIT_RESULT[SUCCESS(成功),FAILED(失败)]")   
            column(name: "time_consuming", type: "bigint",  remarks: "操作耗时")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-16-hmnt_audit_op_log-1") {
        addColumn(tableName: "hmnt_audit_op_log") {
            column(name: "request_url", type: "varchar(480)", remarks: "请求路径")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-16-hmnt_audit_op_log-2") {
        addColumn(tableName: "hmnt_audit_op_log") {
            column(name: "request_ip", type: "varchar(30)", remarks: "请求IP")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-16-hmnt_audit_op_log-3") {
        addColumn(tableName: "hmnt_audit_op_log") {
            column(name: "request_referrer", type: "varchar(480)", remarks: "请求referer")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-16-hmnt_audit_op_log-4") {
        addColumn(tableName: "hmnt_audit_op_log") {
            column(name: "request_user_agent", type: "varchar(480)", remarks: "请求用户代理")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-16-hmnt_audit_op_log-5") {
        addColumn(tableName: "hmnt_audit_op_log") {
            column(name: "request_method", type: "varchar(30)", remarks: "请求方式")
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-01-04-hmnt_audit_op_log') {
        addColumn(tableName: 'hmnt_audit_op_log') {
            column(name: 'audit_batch_number', type: "varchar(60)", remarks: '审计批次号，hmnt_audit_data.audit_batch_number')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-10-hmnt_audit_op_log') {
        addColumn(tableName: 'hmnt_audit_op_log') {
            column(name: 'menu_id', type: "bigint", remarks: '菜单ID')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-16-hmnt_audit_op_log') {
        addColumn(tableName: 'hmnt_audit_op_log') {
            column(name: 'audit_op_config_id', type: "bigint", remarks: '操作审计ID,hmnt_audit_op_config.audit_op_config_id')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-17-hmnt_audit_op_log-01') {
        addColumn(tableName: 'hmnt_audit_op_log') {
            column(name: 'role_id', type: "bigint", remarks: '角色ID，iam_role.id')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-17-hmnt_audit_op_log-02') {
        addColumn(tableName: 'hmnt_audit_op_log') {
            column(name: 'client_name', type: "varchar(32)", remarks: '客户端，oauth_client.name')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-08-17-hmnt_audit_op_log') {
        addColumn(tableName: 'hmnt_audit_op_log') {
            column(name: 'business_key', type: "varchar(240)", remarks: '业务主键')
        }
    }
}