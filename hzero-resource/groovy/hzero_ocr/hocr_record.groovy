package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_record.groovy') {
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_record") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hocr_record_s', startValue:"1")
        }
        createTable(tableName: "hocr_record", remarks: "OCR识别记录表") {
            column(name: "record_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "record_detail_id", type: "bigint",   defaultValue:"0",   remarks: "记录回写ID")  {constraints(nullable:"false")}
            column(name: "recognize_type", type: "varchar(" + 30 * weight + ")",  remarks: "识别类型")  {constraints(nullable:"false")}  
            column(name: "resource_url1", type: "varchar(" + 480 * weight + ")",  remarks: "上传图片路径1")  {constraints(nullable:"false")}  
            column(name: "resource_url2", type: "varchar(" + 480 * weight + ")",  remarks: "上传图片路径2")   
            column(name: "recognize_date", type: "datetime",  remarks: "识别时间")  {constraints(nullable:"false")}  
            column(name: "recognized_by", type: "bigint",  remarks: "识别人")  {constraints(nullable:"false")}
            column(name: "recognize_status", type: "varchar(" + 30 * weight + ")",  remarks: "识别状态 1成功 0失败")  {constraints(nullable:"false")}  
            column(name: "request_param", type: "longtext",  remarks: "识别请求参数")   
            column(name: "response_content", type: "longtext",  remarks: "识别接口返回内容")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hocr_record", indexName: "hocr_record_n1") {
            column(name: "recognize_type")
        }

    }
	
    changeSet(author: "liang.li05@hand-china.com", id: "2020-04-23-hocr_record") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }

        addColumn(tableName: 'hocr_record') {
            column(name: "parent_id", type: "bigint", remarks: "所属多图识别记录的ID"){
                constraints(nullable: "true")
            }
        }
        addColumn(tableName: 'hocr_record') {
            column(name: "hidden_flag", type: "tinyint", defaultValue: "0", remarks: "数据是否需要被隐藏，隐藏1 展示0"){
                constraints(nullable: "true")
            }
        }
        dropNotNullConstraint(tableName: "hocr_record", columnName: "record_detail_id", columnDataType: "bigint")
    }

    changeSet(author: "liang.li05@hand-china.com", id: "2020-06-03-hocr_record") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        dropNotNullConstraint(tableName: "hocr_record", columnName: "resource_url1", columnDataType: "varchar(" + 480 * weight + ")")
    }
}