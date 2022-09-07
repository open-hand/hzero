package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_egk_dc_device_attr.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_egk_dc_device_attr") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_egk_dc_device_attr_s', startValue: "1")
        }
        createTable(tableName: "hiot_egk_dc_device_attr", remarks: "设备通讯属性维护表") {
            column(name: "DEVICE_ATT_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "PACKAGE_NAME", type: "varchar(" + 50 * weight + ")", defaultValue: "0", remarks: "ota升级包名称") { constraints(nullable: "false") }
            column(name: "ATTRIBUTE_CODE", type: "varchar(" + 30 * weight + ")", defaultValue: "1", remarks: "字段编码，匹配多语言") { constraints(nullable: "false") }
            column(name: "MULTILINGUAL_CODE", type: "varchar(" + 255 * weight + ")", remarks: "多语言编码") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明")
            column(name: "INPUT_FLAG", type: "int", defaultValue: "1", remarks: "输入类型，0-非必输，1-必输，2-不可编辑") { constraints(nullable: "false") }
            column(name: "INPUT_BOX_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "text", remarks: "输入框类型，text-文件框，dropdown-下拉框") { constraints(nullable: "false") }
            column(name: "INPUT_BOX_LENGTH", type: "int", defaultValue: "210", remarks: "输入框长度，像素") { constraints(nullable: "false") }
            column(name: "ORDER_PRIORITY", type: "bigint", defaultValue: "1", remarks: "排序优先级，1～50，1最高，50最低") { constraints(nullable: "false") }
            column(name: "DEFAULT_VALUE", type: "varchar(" + 255 * weight + ")", defaultValue: "0", remarks: "默认值") { constraints(nullable: "false") }
            column(name: "CHECK_RULE", type: "varchar(" + 255 * weight + ")", defaultValue: "0", remarks: "正则表达式，校验规则") { constraints(nullable: "false") }
            column(name: "VALUE_CODE", type: "varchar(" + 255 * weight + ")", defaultValue: "0", remarks: "值集编码") { constraints(nullable: "false") }
            column(name: "ENABLE_FLAG", type: "tinyint", defaultValue: "1", remarks: "有效性") { constraints(nullable: "false") }
            column(name: "TIP_MULTILINGUAL_CODE", type: "varchar(" + 255 * weight + ")", remarks: "必输规则提示信息，多语言编码")
            column(name: "TIP_DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "必输规则默认提示信息")
            column(name: "ATTR_REAL_CODE", type: "varchar(" + 255 * weight + ")", remarks: "") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }
}