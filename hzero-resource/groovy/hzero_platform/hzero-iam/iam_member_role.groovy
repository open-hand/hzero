package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_member_role.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_member_role") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_member_role_s', startValue:"1")
        }
        createTable(tableName: "iam_member_role", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "role_id", type: "bigint",  remarks: "角色id")  {constraints(nullable:"false")}  
            column(name: "member_id", type: "bigint",  remarks: "成员id,可以是userId,clientId等，与member_type对应")  {constraints(nullable:"false")}  
            column(name: "member_type", type: "varchar(" + 32 * weight + ")",   defaultValue:"user",   remarks: "成员类型，默认为user，值集：HIAM.MEMBER_TYPE")   
            column(name: "source_id", type: "bigint",  remarks: "创建该记录的源id，可以是projectId,也可以是organizarionId等")  {constraints(nullable:"false")}  
            column(name: "source_type", type: "varchar(" + 32 * weight + ")",  remarks: "创建该记录的源类型，sit/organization/project/user等")  {constraints(nullable:"false")}  
            column(name: "h_assign_level", type: "varchar(" + 20 * weight + ")",  remarks: "分配层级")   
            column(name: "h_assign_level_value", type: "bigint",  remarks: "分配层级值")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"role_id,member_id,member_type,source_id,source_type",tableName:"iam_member_role",constraintName: "iam_member_role_u1")
    }

    // 重建索引
    changeSet(author: "bojiangzhou", id: "2019-03-27-iam_member_role") {
        dropUniqueConstraint(tableName:"iam_member_role",constraintName: "iam_member_role_u1")
        addUniqueConstraint(columnNames:"role_id,member_id,member_type",tableName:"iam_member_role",constraintName: "iam_member_role_u1")
    }
	
    changeSet(author: "bojiangzhou", id: "2019-07-24-iam_member_role") {
		createIndex(tableName: "iam_member_role", indexName: "iam_member_role_n1") {
            column(name: "member_id")
            column(name: "member_type")
        }
    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-05-08-iam_member_role") {
        addColumn(tableName: 'iam_member_role') {
            column(name: "start_date_active", type: "date",  remarks: "有效期起")
        }
        addColumn(tableName: 'iam_member_role') {
            column(name: "end_date_active", type: "date",  remarks: "有效期止")
        }
    }

}