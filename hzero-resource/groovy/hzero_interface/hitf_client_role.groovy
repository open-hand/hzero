package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_client_role.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_client_role") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_client_role_s', startValue:"1")
        }
        createTable(tableName: "hitf_client_role", remarks: "客户端授权角色表") {
            column(name: "client_role_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "client_auth_id", type: "bigint",  remarks: "客户端授权ID")  {constraints(nullable:"false")}
            column(name: "role_id", type: "bigint",  remarks: "角色ID")

        }

        addUniqueConstraint(columnNames:"client_auth_id,role_id",tableName:"hitf_client_role",constraintName: "hitf_client_role_u1")
    }

    // 修复租户越权问题
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-06-11-hitf_client_role") {
        // 添加租户ID
        addColumn(tableName: 'hitf_client_role') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
        }

        // 删除重建唯一性索引，添加租户ID
        dropUniqueConstraint(uniqueColumns:"client_auth_id,role_id",tableName:"hitf_client_role",constraintName: "hitf_client_role_u1")
        addUniqueConstraint(columnNames:"client_auth_id,role_id,tenant_id",tableName:"hitf_client_role",constraintName: "hitf_client_role_u1")
    }


}
