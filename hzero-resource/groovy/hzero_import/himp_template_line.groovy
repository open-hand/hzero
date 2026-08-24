package script.db

databaseChangeLog(logicalFilePath: 'script/db/himp_template_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-himp_template_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'himp_template_line_s', startValue:"1")
        }
        createTable(tableName: "himp_template_line", remarks: "通用模板行") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "target_id", type: "bigint",  remarks: "模板目标ID,himp_template_target.id")  {constraints(nullable:"false")}  
            column(name: "column_index", type: "int",  remarks: "列序号")  {constraints(nullable:"false")}  
            column(name: "column_name", type: "varchar(" + 60 * weight + ")",  remarks: "列名称")  {constraints(nullable:"false")}  
            column(name: "column_code", type: "varchar(" + 60 * weight + ")",  remarks: "列代码")  {constraints(nullable:"false")}  
            column(name: "column_type", type: "varchar(" + 10 * weight + ")",  remarks: "列类型")  {constraints(nullable:"false")}  
            column(name: "length", type: "bigint",  remarks: "列长度")   
            column(name: "format_mask", type: "varchar(" + 20 * weight + ")",  remarks: "列值格式掩码")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否有效")  {constraints(nullable:"false")}  
            column(name: "nullable_flag", type: "tinyint",   defaultValue:"0",   remarks: "必输验证标识")  {constraints(nullable:"false")}  
            column(name: "validate_flag", type: "tinyint",   defaultValue:"0",   remarks: "验证标识")  {constraints(nullable:"false")}  
            column(name: "change_data_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否开启数据转换")  {constraints(nullable:"false")}  
            column(name: "change_data_url", type: "varchar(" + 240 * weight + ")",  remarks: "数据转换服务URL信息")   
            column(name: "change_data_code", type: "varchar(" + 240 * weight + ")",  remarks: "数据转换块码")   
            column(name: "max_value", type: "varchar(" + 30 * weight + ")",  remarks: "数据中最大值")   
            column(name: "min_value", type: "varchar(" + 30 * weight + ")",  remarks: "数据中最小值")   
            column(name: "validate_set", type: "varchar(" + 30 * weight + ")",  remarks: "验证值集")   
            column(name: "regular_expression", type: "varchar(" + 30 * weight + ")",  remarks: "正则表达式验证")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"target_id,column_index",tableName:"himp_template_line",constraintName: "himp_template_lines_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-04-23-himp_template_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropColumn(tableName: 'himp_template_line', columnName: 'format_mask')
        addColumn(tableName: 'himp_template_line') {
            column(name: "format_mask", type: "varchar(" + 30 * weight + ")",  remarks: "列值格式掩码")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-05-24-himp_template_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropColumn(tableName: 'himp_template_line', columnName: 'change_data_url')
        dropColumn(tableName: 'himp_template_line', columnName: 'change_data_code')
        addColumn(tableName: 'himp_template_line') {
            column(name: "sample_data", type: "longtext",  remarks: "示例数据")
        }
        addColumn(tableName: 'himp_template_line') {
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-07-03-himp_template_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "himp_template_line", columnName: 'validate_set', newDataType: "varchar(" + 60 * weight + ")")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-11-07-himp_template_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "himp_template_line", columnName: 'regular_expression', newDataType: "varchar(" + 480 * weight + ")")
    }
}