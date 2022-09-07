package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_check_result.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_check_result") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_check_result_s', startValue:"1")
        }
        createTable(tableName: "hivc_check_result", remarks: "查验结果") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "image_url", type: "varchar(" + 480 * weight + ")",  remarks: "图片地址")   
            column(name: "invoice_type", type: "varchar(" + 30 * weight + ")",  remarks: "发票类型(01:专票/02:货运专票/03:机动车发票/04:普票/10:电子票/11:卷票/14:通行发票/15:二手车发票)")   
            column(name: "saler_name", type: "varchar(" + 240 * weight + ")",  remarks: "销方名称 02:承运方")   
            column(name: "total_amount", type: "decimal(20,2)",  remarks: "价税合计 15:车价合计")
            column(name: "buyer_name", type: "varchar(" + 240 * weight + ")",  remarks: "购方名称(类型02:受票方名称/类型03:个人或单位名称/类型15:买方单位/个人)")   
            column(name: "invoice_date", type: "date",  remarks: "开票日期")   
            column(name: "invoice_no", type: "varchar(" + 30 * weight + ")",  remarks: "发票编号")   
            column(name: "invoice_code", type: "varchar(" + 20 * weight + ")",  remarks: "发票代码")   
            column(name: "invoice_amount", type: "decimal(20,2)",  remarks: "不含税金额")
            column(name: "tax_amount", type: "decimal(20,2)",  remarks: "税额(类型15:空)")
            column(name: "buyer_no", type: "varchar(" + 240 * weight + ")",  remarks: "购方识别号(02:受票方识别号/03:身份证号或单位代码/15:买方单位代码/身份证号)")   
            column(name: "saler_no", type: "varchar(" + 240 * weight + ")",  remarks: "销方识别号(发票类型02:承运人识别号)")   
            column(name: "buyer_address_phone", type: "varchar(" + 480 * weight + ")",  remarks: "购方地址电话")   
            column(name: "saler_address_phone", type: "varchar(" + 480 * weight + ")",  remarks: "销方地址电话")   
            column(name: "buyer_account", type: "varchar(" + 240 * weight + ")",  remarks: "购方账户")   
            column(name: "saler_account", type: "varchar(" + 240 * weight + ")",  remarks: "销方账户")   
            column(name: "machine_no", type: "varchar(" + 240 * weight + ")",  remarks: "机器编号")   
            column(name: "cancellation_mark", type: "varchar(" + 30 * weight + ")",  remarks: "作废标志")   
            column(name: "check_code", type: "varchar(" + 30 * weight + ")",  remarks: "校验码")   
            column(name: "remark", type: "varchar(" + 1000 * weight + ")",  remarks: "备注")   
            column(name: "traffic_fee_flag", type: "varchar(" + 30 * weight + ")",  remarks: "通行费标志")   
            column(name: "zero_tax_rate_flag", type: "varchar(" + 30 * weight + ")",  remarks: "零税率标志")   
            column(name: "oil_flag", type: "varchar(" + 30 * weight + ")",  remarks: "成品油标志(0/1)")   
            column(name: "agent_flag", type: "varchar(" + 30 * weight + ")",  remarks: "代开标志(1:自开/2:代开)")   
            column(name: "special_flag", type: "varchar(" + 30 * weight + ")",  remarks: "特殊政策标志(0:正常票/1:免税/2:不征税/3:零税率)")   
            column(name: "drawer", type: "varchar(" + 30 * weight + ")",  remarks: "开票人")   
            column(name: "reviewer", type: "varchar(" + 30 * weight + ")",  remarks: "复核人")   
            column(name: "payee", type: "varchar(" + 30 * weight + ")",  remarks: "收款人")   
            column(name: "blue_invoice_code", type: "varchar(" + 240 * weight + ")",  remarks: "蓝票发票代码")   
            column(name: "blue_invoice_no", type: "varchar(" + 240 * weight + ")",  remarks: "蓝票发票号码")   
            column(name: "vehicle_type", type: "varchar(" + 240 * weight + ")",  remarks: "车辆类型(03/15)")   
            column(name: "band_model", type: "varchar(" + 240 * weight + ")",  remarks: "厂牌类型(03/15)")   
            column(name: "produce_area", type: "varchar(" + 240 * weight + ")",  remarks: "产地(03/15)")   
            column(name: "qualified_no", type: "varchar(" + 240 * weight + ")",  remarks: "合格证号(03/15)")   
            column(name: "commodity_inspection_no", type: "varchar(" + 240 * weight + ")",  remarks: "商检单号(03/15)")   
            column(name: "engine_no", type: "varchar(" + 240 * weight + ")",  remarks: "发动机号(03/15)")   
            column(name: "vehicle_no", type: "varchar(" + 240 * weight + ")",  remarks: "车架号码(03/15)")   
            column(name: "import_certificate_no", type: "varchar(" + 240 * weight + ")",  remarks: "进口证书说明(03/15)")   
            column(name: "tax_authority_code", type: "varchar(" + 240 * weight + ")",  remarks: "主管税务机关代码(03/15)")   
            column(name: "tax_authority_name", type: "varchar(" + 240 * weight + ")",  remarks: "主管税务机关名称(03/15)")   
            column(name: "tax_payment_certificate_no", type: "varchar(" + 240 * weight + ")",  remarks: "完税凭证号码(03/15)")   
            column(name: "limited_people_count", type: "int",  remarks: "限乘人数(03/15)")   
            column(name: "tonnage", type: "varchar(" + 240 * weight + ")",  remarks: "吨位(03/15)")   
            column(name: "tax_rate", type: "decimal(10,0)",  remarks: "税率(03/15)")
            column(name: "receive_name", type: "varchar(" + 240 * weight + ")",  remarks: "收货人名称(02)")   
            column(name: "receive_tax_no", type: "varchar(" + 240 * weight + ")",  remarks: "收货人识别号(02)")   
            column(name: "consignor_name", type: "varchar(" + 240 * weight + ")",  remarks: "发货人名称(02)")   
            column(name: "consignor_tax_no", type: "varchar(" + 240 * weight + ")",  remarks: "发货人识别号(02)")   
            column(name: "transport_goods_info", type: "varchar(" + 240 * weight + ")",  remarks: "运输货物信息(02)")   
            column(name: "through_address", type: "varchar(" + 240 * weight + ")",  remarks: "起运地、经由、到达地(02)")   
            column(name: "tax_disk_number", type: "varchar(" + 240 * weight + ")",  remarks: "税控盘号(02)")   
            column(name: "car_number", type: "varchar(" + 240 * weight + ")",  remarks: "车种车号(02)")   
            column(name: "vehicle_tonnage", type: "varchar(" + 240 * weight + ")",  remarks: "车船吨位(02)")   
            column(name: "license_plate", type: "varchar(" + 240 * weight + ")",  remarks: "车牌照号(15)")   
            column(name: "registration_no", type: "varchar(" + 240 * weight + ")",  remarks: "登记证号(15)")   
            column(name: "transferred_vehicle_office", type: "varchar(" + 240 * weight + ")",  remarks: "转入地车辆车管所名称(15)")   
            column(name: "business_unit", type: "varchar(" + 240 * weight + ")",  remarks: "经营、拍卖单位(15)")   
            column(name: "business_unit_address", type: "varchar(" + 240 * weight + ")",  remarks: "经营、拍卖单位地址(15)")   
            column(name: "business_unit_tax_no", type: "varchar(" + 240 * weight + ")",  remarks: "经营、拍卖单位纳税人识别号(15)")   
            column(name: "business_unit_bank_and_account", type: "varchar(" + 240 * weight + ")",  remarks: "开户银行及账号(15)")   
            column(name: "business_unit_phone", type: "varchar(" + 240 * weight + ")",  remarks: "经营、拍卖单位电话(15)")   
            column(name: "lemon_market", type: "varchar(" + 240 * weight + ")",  remarks: "二手车市场(15)")   
            column(name: "lemon_market_tax_no", type: "varchar(" + 240 * weight + ")",  remarks: "二手车市场纳税人识别号(15)")   
            column(name: "lemon_market_address", type: "varchar(" + 240 * weight + ")",  remarks: "二手车市场地址(15)")   
            column(name: "lemon_market_bank_and_account", type: "varchar(" + 240 * weight + ")",  remarks: "二手车市场开户银行及账号(15)")   
            column(name: "lemon_market_phone", type: "varchar(" + 240 * weight + ")",  remarks: "二手车市场电话(15)")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}