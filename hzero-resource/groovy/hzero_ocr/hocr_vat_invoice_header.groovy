package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_vat_invoice_header.groovy') {
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_vat_invoice_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hocr_vat_invoice_header_s', startValue:"1")
        }
        createTable(tableName: "hocr_vat_invoice_header", remarks: "增值税发票识别信息头表") {
            column(name: "vat_invoice_header_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "type", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "发票种类")  {constraints(nullable:"false")}  
            column(name: "number", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "发票号码")  {constraints(nullable:"false")}  
            column(name: "code", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "发票代码")  {constraints(nullable:"false")}  
            column(name: "issue_date", type: "date",  remarks: "开票日期")   
            column(name: "check_code", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "校验码")  {constraints(nullable:"false")}  
            column(name: "encryption_block", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "密码区")  {constraints(nullable:"false")}  
            column(name: "attribution", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "发票归属地")  {constraints(nullable:"false")}  
            column(name: "buyer_name", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "购方名称")  {constraints(nullable:"false")}  
            column(name: "buyer_id", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "购方纳税人识别号")  {constraints(nullable:"false")}  
            column(name: "buyer_address", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "购方地址及电话")  {constraints(nullable:"false")}  
            column(name: "buyer_bank", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "购方开户行及账号")  {constraints(nullable:"false")}  
            column(name: "seller_name", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "销售方名称")  {constraints(nullable:"false")}  
            column(name: "seller_id", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "销售方纳税人识别号")  {constraints(nullable:"false")}  
            column(name: "seller_address", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "销售方地址及电话")  {constraints(nullable:"false")}  
            column(name: "seller_bank", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "销售方开户行及账号")  {constraints(nullable:"false")}  
            column(name: "subtotal_amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "合计金额")  {constraints(nullable:"false")}  
            column(name: "subtotal_tax", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "合计税额")  {constraints(nullable:"false")}  
            column(name: "total", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "价税合计(小写)")  {constraints(nullable:"false")}  
            column(name: "total_in_words", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "价税合计(大写)")  {constraints(nullable:"false")}  
            column(name: "receiver", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "收款人")  {constraints(nullable:"false")}  
            column(name: "reviewer", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "复核人")  {constraints(nullable:"false")}  
            column(name: "issuer", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "开票人")  {constraints(nullable:"false")}  
            column(name: "remarks", type: "longtext",  remarks: "备注")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hocr_vat_invoice_header", indexName: "hocr_vat_invoice_header_n1") {
            column(name: "type")
        }

    }
	
    changeSet(author: "liang.li05@hand-china.com", id: "2019-10-14-hocr_vat_invoice_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }

        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "type", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "number", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "code", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "check_code", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "encryption_block", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "attribution", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "buyer_name", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "buyer_id", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "buyer_address", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "buyer_bank", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "seller_name", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "seller_id", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "seller_address", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "seller_bank", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "subtotal_amount", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "subtotal_tax", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "total", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "total_in_words", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "receiver", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "reviewer", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "issuer", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_header", columnName: "remarks", columnDataType: "longtext")

        renameColumn(tableName: "hocr_vat_invoice_header", oldColumnName: "type", newColumnName: "invoice_type", columnDataType: "varchar(" + 255 * weight + ")")
        renameColumn(tableName: "hocr_vat_invoice_header", oldColumnName: "number", newColumnName: "invoice_number", columnDataType: "varchar(" + 255 * weight + ")")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-02-hocr_vat_invoice_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hocr_vat_invoice_header') {
            column(name: "invoice_type_num", type: "varchar(" + 30 * weight + ")", remarks: "发票种类编码")
        }
    }

    changeSet(author: "liang.li05@hand-china.com", id: "2020-07-09-hocr_vat_invoice_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }

        addColumn(tableName: "hocr_vat_invoice_header") {
            column(name: "machine_code", type: "varchar(" + 255 * weight + ")", remarks: "机器编号") {constraints(nullable:"true")}
        }
        addColumn(tableName: "hocr_vat_invoice_header") {
            column(name: "machine_num", type: "varchar(" + 255 * weight + ")", remarks: "机打号码") {constraints(nullable:"true")}
        }
    }
}