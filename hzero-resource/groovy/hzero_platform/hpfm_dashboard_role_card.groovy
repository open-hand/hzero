package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dashboard_role_card.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_dashboard_role_card") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dashboard_role_card_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dashboard_role_card", remarks: "角色卡片表") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "role_id", type: "int",  remarks: "角色id")  {constraints(nullable:"false")}
            column(name: "card_id", type: "int",  remarks: "卡片id")  {constraints(nullable:"false")}
            column(name: "x", type: "tinyint",  remarks: "x轴")  {constraints(nullable:"false")}
            column(name: "y", type: "tinyint",  remarks: "y轴")  {constraints(nullable:"false")}
            column(name: "default_display_flag", type: "tinyint",  remarks: "是否默认显示")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"role_id,card_id",tableName:"hpfm_dashboard_role_card",constraintName: "hpfm_dashboard_role_card_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-03-16-hpfm_dashboard_role_card") {
        dropUniqueConstraint(tableName: "hpfm_dashboard_role_card",constraintName: "hpfm_dashboard_role_card_u1")
        modifyDataType(tableName: "hpfm_dashboard_role_card", columnName: 'role_id', newDataType: "bigint")
        modifyDataType(tableName: "hpfm_dashboard_role_card", columnName: 'card_id', newDataType: "bigint")
        addUniqueConstraint(columnNames:"role_id,card_id",tableName:"hpfm_dashboard_role_card",constraintName: "hpfm_dashboard_role_card_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-04-29-hpfm_dashboard_role_card") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_dashboard_role_card", columnName: 'y', newDataType: "int")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_dashboard_role_card") {
        addColumn(tableName: 'hpfm_dashboard_role_card') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}
