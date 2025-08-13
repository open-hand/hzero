package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dashboard_layout.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_dashboard_layout") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dashboard_layout_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dashboard_layout", remarks: "工作台配置") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "code", type: "varchar(" + 250 * weight + ")",  remarks: "卡片编码")  {constraints(nullable:"false")}  
            column(name: "w", type: "tinyint",  remarks: "卡片宽度")  {constraints(nullable:"false")}
            column(name: "h", type: "tinyint",  remarks: "卡片高度")  {constraints(nullable:"false")}
            column(name: "x", type: "tinyint",  remarks: "卡片位置X")  {constraints(nullable:"false")}
            column(name: "y", type: "tinyint",  remarks: "卡片位置Y")  {constraints(nullable:"false")}
            column(name: "user_id", type: "bigint",  remarks: "用户ID")  {constraints(nullable:"false")}
            column(name: "role_id", type: "bigint",  remarks: "角色ID")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"code,user_id,role_id,tenant_id",tableName:"hpfm_dashboard_layout",constraintName: "hpfm_dashboard_layout_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-11-05-hpfm_dashboard_layout") {
        addColumn(tableName: "hpfm_dashboard_layout"){
            if(helper.isOracle()){
                column(name: "card_id", type: "bigint", defaultValue: "-1", remarks: "卡片id,hpfm_dashboard_card.id")  {constraints(nullable:"false")}
            } else {
                column(name: "card_id", type: "bigint", remarks: "卡片id,hpfm_dashboard_card.id")  {constraints(nullable:"false")}
            }
        }
        dropUniqueConstraint(tableName: "hpfm_dashboard_layout", constraintName: "hpfm_dashboard_layout_u1")
        addUniqueConstraint(columnNames: "code,card_id,user_id,role_id,tenant_id", tableName: "hpfm_dashboard_layout", constraintName: "hpfm_dashboard_layout_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-04-29-hpfm_dashboard_layout") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_dashboard_layout", columnName: 'y', newDataType: "int")
        modifyDataType(tableName: "hpfm_dashboard_layout", columnName: 'h', newDataType: "int")
    }
}
