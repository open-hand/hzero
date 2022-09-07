/**
 * 数据映射-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-7-8
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'horc.dataMapping';

export default {
  PREFIX,
  CREATE: intl.get('hzero.common.button.create').d('新建'),
  INCREASE: intl.get('hzero.common.button.increase').d('新增'),
  SAVE: intl.get('hzero.common.button.save').d('保存'),
  SURE: intl.get('hzero.common.button.sure').d('确定'),
  EDIT: intl.get('hzero.common.button.edit').d('编辑'),
  VIEW: intl.get('hzero.common.button.view').d('查看'),
  CANCEL: intl.get('hzero.common.button.cancel').d('取消'),
  DELETE: intl.get('hzero.common.button.delete').d('删除'),
  OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
  EXECUTE: intl.get(`${PREFIX}.view.button.exec`).d('执行'),
  MAPPING_MAINTAIN: intl.get(`${PREFIX}.view.button.mappingMaintain`).d('值映射维护'),
  ADD_CONDITION: intl.get(`${PREFIX}.view.button.addCondition`).d('添加条件'),
  VIEW_HISTORY: intl.get(`${PREFIX}.view.button.viewHistory`).d('查看历史版本'),
  REVERT: intl.get(`${PREFIX}.view.button.revert`).d('版本回退至'),
  RELEASE: intl.get(`${PREFIX}.view.button.release`).d('发布'),

  HEADER: intl.get(`${PREFIX}.view.title.header`).d('数据映射'),
  DETAIL: intl.get(`${PREFIX}.view.title.detail`).d('数据映射明细'),
  CREATE_LINE: intl.get(`${PREFIX}.view.title.createLine`).d('创建转换维护信息'),
  EDIT_LINE: intl.get(`${PREFIX}.view.title.editLine`).d('更新转换维护信息'),
  BASIC_INFO: intl.get(`${PREFIX}.view.title.basicInfo`).d('基本信息'),
  DETAIL_INFO: intl.get(`${PREFIX}.view.title.detailInfo`).d('转换维护'),
  FORMULA_MAINTAIN: intl.get(`${PREFIX}.view.title.formulaMaintain`).d('公式维护'),
  CAST_VAL_MAINTAIN: intl.get(`${PREFIX}.view.title.castValMaintain`).d('值转换维护'),
  CONDITION_MAINTAIN: intl.get(`${PREFIX}.view.title.conditionMaintain`).d('条件维护'),

  SEQ_NUMBER: intl.get(`${PREFIX}.model.dataMapping.seqNumber`).d('序号'),
  CAST_CODE: intl.get(`${PREFIX}.model.dataMapping.castCode`).d('数据映射代码'),
  CAST_NAME: intl.get(`${PREFIX}.model.dataMapping.castName`).d('数据映射名称'),
  DATA_TYPE: intl.get(`${PREFIX}.model.dataMapping.dataType`).d('数据映射类型'),
  VERSION_HISTORY: intl.get(`${PREFIX}.view.title.versionHistory`).d('历史版本'),
  STATUS: intl.get(`${PREFIX}.view.title.status`).d('状态'),
  VERSION: intl.get(`${PREFIX}.model.dataMapping.version`).d('版本'),
  FROM_VERSION: intl.get(`${PREFIX}.model.dataMapping.fromVersion`).d('来源版本'),

  CAST_TYPE: intl.get(`${PREFIX}.model.dataMapping.castType`).d('数据映射类型'),
  CAST_ROOT: intl.get(`${PREFIX}.model.dataMapping.castRoot`).d('字段路径'),
  CAST_FIELD: intl.get(`${PREFIX}.model.dataMapping.castField`).d('字段名称'),
  CAST_FORMULA: intl.get(`${PREFIX}.model.dataMapping.castFormula`).d('公式转换'),
  CAST_VAL: intl.get(`${PREFIX}.model.dataMapping.castVal`).d('值转换'),
  CAST_SQL: intl.get(`${PREFIX}.model.dataMapping.castSql`).d('SQL转换'),
  SQL: intl.get(`${PREFIX}.model.dataMapping.Sql`).d('SQL'),
  FORMULA: intl.get(`${PREFIX}.model.dataMapping.formula`).d('公式'),

  CAST_LOV_CODE: intl.get(`${PREFIX}.model.dataMapping.castLovCode`).d('值集编码'),
  CAST_LOV_FIELD: intl.get(`${PREFIX}.model.dataMapping.castLovField`).d('值集转化字段'),
  CAST_LOV_LANG: intl.get(`${PREFIX}.model.dataMapping.castLovLang`).d('值集转化语言'),

  EXPR_SOURCE_TYPE: intl.get(`${PREFIX}.model.dataMapping.exprSourceType`).d('来源类型'),
  EXPR_SOURCE_VALUE: intl.get(`${PREFIX}.model.dataMapping.exprSourceValue`).d('来源值'),

  TARGET_VALUE: intl.get(`${PREFIX}.model.dataMapping.targetValue`).d('目标值'),
  CONDITION: intl.get(`${PREFIX}.model.dataMapping.condition`).d('条件'),
  CONJUNCTION: intl.get(`${PREFIX}.model.dataMapping.conjunction`).d('多条件连接符'),

  FIELD_TYPE: intl.get(`${PREFIX}.model.dataMapping.fieldType`).d('目标字段类型'),

  CONDITION_FIELD: intl.get(`${PREFIX}.model.dataMapping.conditionField`).d('条件字段'),
  VALUE: intl.get(`${PREFIX}.model.dataMapping.sourceValue`).d('值'),

  CAST_FORMULA_TIP_HEADER: intl
    .get(`${PREFIX}.view.message.castFormulaTip`)
    .d('不同的颜色代表不同的含义：'),
  CAST_FORMULA_TIP_CONST: intl.get(`${PREFIX}.view.message.castFormulaConst`).d('常量：'),
  CAST_FORMULA_TIP_FORMULA: intl.get(`${PREFIX}.view.message.castFormulaFormula`).d('公式：'),
  CAST_FORMULA_TIP_RESPONSE: intl.get(`${PREFIX}.view.message.castFormulaResponse`).d('报文字段：'),

  SAVE_VALIDATE: intl.get(`${PREFIX}.model.dataMapping.saveValidate`).d('请先完善必输内容'),
  SAVE_EMPTY: intl.get(`${PREFIX}.model.dataMapping.saveEmpty`).d('无修改内容,无需保存'),

  PATTERN_MISMACTH: intl
    .get('hzero.common.validation.codeUpper')
    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),

  MODAL_EDIT_INFO: intl
    .get(`${PREFIX}.view.message.editInfo`)
    .d('关联接口已上线，不允许编辑当前配置'),
};
