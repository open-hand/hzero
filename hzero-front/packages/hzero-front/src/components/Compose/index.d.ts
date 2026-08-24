/**
 * index.d
 * @date 2018/9/28
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

type Flag = 0 | 1;
type Align = 'left' | 'center' | 'right';
type TemplateType = 'form' | 'table' | 'toolbar';

// 模板
class Template {
  // 主键
  templateId: number;

  // 模板Code
  componentCode: string;

  // 模板类型
  templateType: TemplateType;

  // 模板描述
  templateDescription: string;

  // 是否启用
  enabledFlag: Flag;

  // 备注
  remark: string;

  // 版本号
  objectVersionNumber: number;
}

// UI纬度模板
class Dimension {
  // 主键
  dimTemplateId: number;

  // 模板id 外键
  templateId: number;

  // 维度描述
  dimTemplateDescription: string;

  // 租户id
  tenantId: number;

  // 维度类型 值集
  dimensionType: string;

  // 维度id 租户/公司/角色/用户 id
  dimensionId: number;

  // 来源纬度模板id
  source_dim_template_id: number;

  // 是否启用
  enabledFlag: Flag;

  // 备注
  remark: string;

  // 版本号
  objectVersionNumber: number;
}

// UI配置 维度模板属性
class DimensionTemplateAttribute {
  // 主键
  dimTplAttributeId: number;

  // UI纬度模板 id
  dimTemplateId: number;

  // UI模板属性类型 值集
  attributeType: string;

  // 属性名
  attributeName: string;

  // 属性值
  attributeValue: string;

  // 属性描述
  attributeDescription: string;

  // 版本号
  objectVersionNumber: number;
}

// 字段
class Field {
  // 字段唯一标志
  fieldId: number;

  // 维度id
  dimTemplateId: number;

  // 维度的显示值 ❌
  dimTemplateMeaning: string;

  // 字段组件
  componentType: string;

  // 字段组件含义 ❌
  componentTypeMeaning: string;

  // 字段编码
  fieldName: string;

  // 字段名称/描述
  description: string;

  // 是否必输 表单/Grid 字段属性
  requiredFlag: Flag;

  // 是否启用
  enabledFlag: Flag;

  // 字段左空位 表单/Grid 字段属性, Grid 表单编辑时的属性
  leftOffset: number;

  // 字段右空位 表单/Grid 字段属性, Grid 表单编辑时的属性
  rightOffset: number;

  // 字段跨越列数 表单/Grid 字段属性, Grid 表单编辑时的属性
  colspan: number;

  // 字段 text-align 属性, 适用于 表单/Grid/Toolbar
  align: Align = 'left';

  // 字段宽度 适用于 Grid/Toolbar
  width: number;

  // // 是否固定字段 Grid
  // frozen: Flag = 0;
  // 字段顺序
  orderSeq: number;

  // 格式
  format: string;

  // 字段说明
  remark: string;

  // 版本号
  objectVersionNumber: number;
}

// 字段属性
class FieldAttribute {
  // 主键
  fieldAttributeId: number;

  // 字段id
  fieldId: number;

  // UI维度模板 id
  dimTemplateId: number;

  // 字段属性类型 值集
  attributeType: string;

  // 字段属性 key
  attributeName: string;

  // 字段属性 值
  attributeValue: string;

  // 字段属性 描述
  attributeDescription: string;

  // 版本号
  objectVersionNumber: number;
}
