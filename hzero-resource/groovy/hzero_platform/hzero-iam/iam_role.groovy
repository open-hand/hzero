package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_role") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_role_s', startValue:"1")
        }
        createTable(tableName: "iam_role", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "name", type: "varchar(" + 64 * weight + ")",  remarks: "角色名")  {constraints(nullable:"false")}  
            column(name: "code", type: "varchar(" + 128 * weight + ")",  remarks: "角色编码")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 255 * weight + ")",  remarks: "角色描述full description")   
            column(name: "fd_level", type: "varchar(" + 32 * weight + ")",  remarks: "角色级别")  {constraints(nullable:"false")}  
            column(name: "h_tenant_id", type: "bigint",   defaultValue:"0",   remarks: "所属租户ID")   
            column(name: "h_inherit_role_id", type: "bigint",   defaultValue:"0",   remarks: "继承角色ID")   
            column(name: "h_parent_role_id", type: "bigint",   defaultValue:"0",   remarks: "父级角色ID")   
            column(name: "h_parent_role_assign_level", type: "varchar(" + 20 * weight + ")",  remarks: "父级角色分配层级")   
            column(name: "h_parent_role_assign_level_val", type: "bigint",  remarks: "父级角色分配值")   
            column(name: "is_enabled", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "is_modified", type: "tinyint",   defaultValue:"1",   remarks: "是否可以修改。1表示可以，0不可以")  {constraints(nullable:"false")}  
            column(name: "is_enable_forbidden", type: "tinyint",   defaultValue:"1",   remarks: "是否可以被禁用")  {constraints(nullable:"false")}  
            column(name: "is_built_in", type: "tinyint",   defaultValue:"0",   remarks: "是否内置。1表示是，0表示不是")  {constraints(nullable:"false")}  
            column(name: "is_assignable", type: "tinyint",   defaultValue:"1",   remarks: "是否禁止在更高的层次上分配，禁止project role在organization上分配。1表示可以，0表示不可以")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "h_level_path", type: "varchar(" + 768 * weight + ")",  remarks: "")   
            column(name: "h_inherit_level_path", type: "varchar(" + 768 * weight + ")",  remarks: "角色继承生成层次关系")   

        }
		createIndex(tableName: "iam_role", indexName: "iam_role_n1") {
            column(name: "h_parent_role_id")
            column(name: "h_parent_role_assign_level")
            column(name: "h_parent_role_assign_level_val")
        }
		createIndex(tableName: "iam_role", indexName: "iam_role_n2") {
            column(name: "h_level_path")
        }

        addUniqueConstraint(columnNames:"code",tableName:"iam_role",constraintName: "iam_role_u1")
    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2019-08-12-iam_role') {
        addColumn(tableName: 'iam_role') {
            column(name: 'created_by_tenant_id',  type: 'bigint', remarks: '创建者租户ID')
        }
		createIndex(tableName: "iam_role", indexName: "iam_role_n3") {
            column(name: "h_inherit_level_path")
        }
        // 重建索引
        dropUniqueConstraint(tableName:"iam_role",constraintName: "iam_role_u1")
        addUniqueConstraint(columnNames:"h_tenant_id, code, h_parent_role_id, h_parent_role_assign_level, h_parent_role_assign_level_val, created_by_tenant_id",tableName:"iam_role",constraintName: "iam_role_u1")
    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2020-04-30-iam_role') {
		def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'iam_role') {
            column(name: 'tpl_role_name', type: "varchar(" + 64 * weight + ")", remarks: '模板角色的子角色名称') {constraints(nullable:"true")}
        }
    }
}