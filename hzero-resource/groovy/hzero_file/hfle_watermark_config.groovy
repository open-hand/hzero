package script.db

databaseChangeLog(logicalFilePath: 'script/db/hfle_watermark_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-17-hfle_watermark_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hfle_watermark_config_s', startValue:"1")
        }
        createTable(tableName: "hfle_watermark_config", remarks: "水印配置") {
            column(name: "watermark_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "watermark_code", type: "varchar(" + 30 * weight + ")",  remarks: "水印编码")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "水印描述")   
            column(name: "watermark_type", type: "varchar(" + 30 * weight + ")",  remarks: "水印类型，值集：HFLE.WATERMARK_TYPE")  {constraints(nullable:"false")}  
            column(name: "fill_opacity", type: "decimal(2,1)",  remarks: "透明度")  {constraints(nullable:"false")}  
            column(name: "color", type: "varchar(" + 30 * weight + ")",  remarks: "色彩，值集：HFLE.WATERMARK.COLOR")   
            column(name: "font_size", type: "int",  remarks: "字体大小")   
            column(name: "x_axis", type: "int",  remarks: "横坐标")  {constraints(nullable:"false")}  
            column(name: "y_axis", type: "int",  remarks: "纵坐标")  {constraints(nullable:"false")}  
            column(name: "align", type: "tinyint",  remarks: "对齐方式，0左对齐1居中2右对齐")   
            column(name: "weight", type: "int",  remarks: "图片宽度")   
            column(name: "height", type: "int",  remarks: "图片高度")   
            column(name: "rotation", type: "int",  remarks: "倾斜角度")  {constraints(nullable:"false")}  
            column(name: "detail", type: "varchar(" + 480 * weight + ")",  remarks: "水印内容")  {constraints(nullable:"false")}  
            column(name: "font_url", type: "varchar(" + 480 * weight + ")",  remarks: "字体文件")
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"watermark_code,tenant_id",tableName:"hfle_watermark_config",constraintName: "hfle_watermark_config_u1")
    }
}