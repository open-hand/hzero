package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_log_dtl.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-28-hitf_interface_log_dtl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_interface_log_dtl_s', startValue:"1")
        }
        createTable(tableName: "hitf_interface_log_dtl", remarks: "") {
            column(name: "interface_log_dtl_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "interface_log_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "invoke_key", type: "varchar(" + 255 * weight + ")",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "interface_req_header_param", type: "varchar(1800)",  remarks: "")
            column(name: "interface_req_body_param", type: "longtext",  remarks: "")   
            column(name: "interface_resp_content", type: "longtext",  remarks: "")   
            column(name: "req_header_param", type: "varchar(1800)",  remarks: "")
            column(name: "req_body_param", type: "longtext",  remarks: "")   
            column(name: "resp_content", type: "longtext",  remarks: "")   
            column(name: "stacktrace", type: "longtext",  remarks: "")   
            column(name: "remark", type: "longtext",  remarks: "")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
    
    changeSet(author: "qigang.qin@hand-china.com", id: "2020-10-10_hitf_interface_log_dtl") {
        if(helper.isOracle()){
            sql {
                "ALTER TABLE hitf_interface_log_dtl RENAME COLUMN interface_req_header_param TO inter_req_header_param_bak;" +
                        "ALTER TABLE hitf_interface_log_dtl ADD interface_req_header_param clob;" +
                        "COMMENT ON COLUMN hitf_interface_log_dtl.interface_req_header_param IS '接口请求头参数';" +
                        "UPDATE hitf_interface_log_dtl SET interface_req_header_param = inter_req_header_param_bak;" +
                        "ALTER TABLE hitf_interface_log_dtl DROP COLUMN inter_req_header_param_bak;"
            }
        } else {
            modifyDataType(tableName: "hitf_interface_log_dtl", columnName: "interface_req_header_param", newDataType: "longtext")
        }
    }

    changeSet(author: "qigang.qin@hand-china.com", id: "2020-10-16_hitf_interface_log_dtl") {
        if(helper.isOracle()){
            sql {
                "ALTER TABLE hitf_interface_log_dtl RENAME COLUMN req_header_param TO req_header_param_bak;" +
                        "ALTER TABLE hitf_interface_log_dtl ADD req_header_param clob;" +
                        "COMMENT ON COLUMN hitf_interface_log_dtl.req_header_param IS '请求头参数';" +
                        "UPDATE hitf_interface_log_dtl SET req_header_param = req_header_param_bak;" +
                        "ALTER TABLE hitf_interface_log_dtl DROP COLUMN req_header_param_bak;"
            }
        } else {
            modifyDataType(tableName: "hitf_interface_log_dtl", columnName: "req_header_param", newDataType: "longtext")
        }

    }

    // 补充非主键索引
    changeSet(author: "xiaolong.li@hand-china.com", id: "2021-01-07_hitf_interface_log_dtl--add-nonpri-idx") {
        createIndex(tableName: "hitf_interface_log_dtl", indexName: "hitf_interface_log_dtl_n1") {
            column(name: "interface_log_id")
            column(name: "invoke_key")
        }
    }

}