package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_datasource_route_dtl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-04-28-hpfm_datasource_route_dtl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_datasource_route_dtl_s', startValue: "1")
        }
        createTable(tableName: "hpfm_datasource_route_dtl", remarks: "数据源路由规则详细") {
            column(name: "ds_route_dtl_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "ds_route_id", type: "bigint", remarks: "数据源路由规则ID，hpfm_ds_route.ds_route_id") {
                constraints(nullable: "false")
            }
            column(name: "datasource_id", type: "bigint", remarks: "数据源ID，hpfm_datasource.database_id") {
                constraints(nullable: "false")
            }
            column(name: "master_flag", type: "tinyint", defaultValue:"1", remarks: "是否主数据源") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
        }
        addUniqueConstraint(columnNames: "ds_route_id,datasource_id", tableName: "hpfm_datasource_route_dtl", constraintName: "hpfm_datasource_route_dtl_u1")
    }

}