package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_company.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_company") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_company_s', startValue:"1")
        }
        createTable(tableName: "hpfm_company", remarks: "公司信息") {
            column(name: "company_id", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "company_num", type: "varchar(" + 30 * weight + ")",  remarks: "公司编码，自动生成")  {constraints(nullable:"false")}  
            column(name: "group_id", type: "bigint",  remarks: "集团id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户id，从集团带过来")  {constraints(nullable:"false")}  
            column(name: "unit_id", type: "bigint",  remarks: "组织id")   
            column(name: "domestic_foreign_relation", type: "tinyint",  remarks: "境内境外，IN境内，OUT境外")  {constraints(nullable:"false")}  
            column(name: "unified_social_code", type: "varchar(" + 30 * weight + ")",  remarks: "统一社会信用码")   
            column(name: "organizing_institution_code", type: "varchar(" + 30 * weight + ")",  remarks: "组织机构代码，和统一社会信用码至少存在一个")   
            column(name: "company_name", type: "varchar(" + 360 * weight + ")",  remarks: "公司名称")  {constraints(nullable:"false")}  
            column(name: "short_name", type: "varchar(" + 50 * weight + ")",  remarks: "公司简称")   
            column(name: "company_type", type: "varchar(" + 30 * weight + ")",  remarks: "公司类型")   
            column(name: "registered_country_id", type: "bigint",  remarks: "国家")  {constraints(nullable:"false")}  
            column(name: "registered_region_id", type: "bigint",  remarks: "地区id，树形值集")   
            column(name: "address_detail", type: "varchar(" + 150 * weight + ")",  remarks: "详细地址")  {constraints(nullable:"false")}  
            column(name: "duns_code", type: "varchar(" + 30 * weight + ")",  remarks: "邓白氏编码")   
            column(name: "taxpayer_type", type: "varchar(" + 30 * weight + ")",  remarks: "纳税人类型，值集HPFM.TAXPAYER_TYPE")
            column(name: "legal_rep_name", type: "varchar(" + 30 * weight + ")",  remarks: "法人姓名")   
            column(name: "build_date", type: "date",  remarks: "成立日期")  {constraints(nullable:"false")}  
            column(name: "registered_capital", type: "decimal(20,0)",  remarks: "")   
            column(name: "licence_end_date", type: "date",  remarks: "营业期限")   
            column(name: "business_scope", type: "longtext",  remarks: "经营范围")   
            column(name: "long_term_flag", type: "tinyint",   defaultValue:"0",   remarks: "长期标志，1：长期，0：非长期")  {constraints(nullable:"false")}  
            column(name: "licence_url", type: "varchar(" + 150 * weight + ")",  remarks: "营业执照附件路径")   
            column(name: "source_key", type: "varchar(" + 60 * weight + ")",  remarks: "源数据key")   
            column(name: "source_code", type: "varchar(" + 30 * weight + ")",  remarks: "来源,值集：HPFM.DATA_SOURCE")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"source_code,source_key",tableName:"hpfm_company",constraintName: "hpfm_company_u1")
        addUniqueConstraint(columnNames:"company_num,tenant_id",tableName:"hpfm_company",constraintName: "hpfm_company_u2")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-10-hpfm_company") {
        dropUniqueConstraint(tableName: "hpfm_company", constraintName: "hpfm_company_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-07-09-hpfm_company") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_company", columnName: 'legal_rep_name', newDataType: "varchar(" + 120 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-07-10-hpfm_company") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_company", columnName: 'licence_url', newDataType: "varchar(" + 480 * weight + ")")
    }
}