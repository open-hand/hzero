package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dashboard_card.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_dashboard_card") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dashboard_card_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dashboard_card", remarks: "平台卡片表") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "code", type: "varchar(" + 250 * weight + ")",  remarks: "卡片编码")  {constraints(nullable:"false")}  
            column(name: "fd_level", type: "varchar(" + 32 * weight + ")",  remarks: "卡片所属级别")  {constraints(nullable:"false")}  
            column(name: "catalog_type", type: "varchar(" + 32 * weight + ")",  remarks: "目录类型")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 32 * weight + ")",  remarks: "卡片名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 250 * weight + ")",  remarks: "卡片描述")   
            column(name: "w", type: "tinyint",  remarks: "卡片宽")  {constraints(nullable:"false")}
            column(name: "h", type: "tinyint",  remarks: "卡片高")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}
            column(name: "logo", type: "varchar(" + 300 * weight + ")",  remarks: "卡片图标")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"code",tableName:"hpfm_dashboard_card",constraintName: "hpfm_dashboard_card_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-04-11-hpfm_dashboard_card") {
        addColumn(tableName: "hpfm_dashboard_card"){
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")  {constraints(nullable:"false")}
        }
        dropUniqueConstraint(tableName: "hpfm_dashboard_card", constraintName: "hpfm_dashboard_card_u1")
        addUniqueConstraint(columnNames: "code,tenant_id", tableName: "hpfm_dashboard_card", constraintName: "hpfm_dashboard_card_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-11-04-hpfm_dashboard_card") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: "hpfm_dashboard_card"){
            column(name: "card_params", type: "varchar(" + 240 * weight + ")", defaultValue: "none", remarks: "卡片参数字段，用于传递卡片所需参数")  {constraints(nullable:"false")}
        }
        dropUniqueConstraint(tableName: "hpfm_dashboard_card", constraintName: "hpfm_dashboard_card_u1")
        addUniqueConstraint(columnNames: "code,tenant_id,card_params", tableName: "hpfm_dashboard_card", constraintName: "hpfm_dashboard_card_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-04-29-hpfm_dashboard_card") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_dashboard_card", columnName: 'h', newDataType: "int")
    }

}
