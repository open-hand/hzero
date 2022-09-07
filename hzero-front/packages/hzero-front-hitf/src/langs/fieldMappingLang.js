/**
 * 字段映射-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-7-8
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

const getLang = (key) => {
  const PREFIX = 'hitf.fieldMapping';

  const LANGS = {
    PREFIX,
    CREATE: intl.get('hzero.common.button.create').d('新建'),
    SAVE: intl.get('hzero.common.button.save').d('保存'),
    SURE: intl.get('hzero.common.button.sure').d('确定'),
    ENABLE: intl.get('hzero.common.button.enable').d('启用'),
    DISABLE: intl.get('hzero.common.button.disable').d('禁用'),
    EDIT: intl.get('hzero.common.button.edit').d('编辑'),
    VIEW: intl.get('hzero.common.button.view').d('查看'),
    DELETE: intl.get('hzero.common.button.delete').d('删除'),
    OPERATOR: intl.get('hzero.common.table.column.option').d('操作'),
    EXECUTE: intl.get(`${PREFIX}.view.button.exec`).d('执行'),
    VIEW_HISTORY: intl.get(`${PREFIX}.view.button.viewHistory`).d('查看历史版本'),
    REVERT: intl.get(`${PREFIX}.view.button.revert`).d('版本回退至'),
    RELEASE: intl.get(`${PREFIX}.view.button.release`).d('发布'),
    OFFLINE: intl.get(`${PREFIX}.view.button.offline`).d('下线'),
    SAME_NAME_REL: intl.get(`${PREFIX}.view.button.sameNameRel`).d('同名关联'),
    SAME_LINE_REL: intl.get(`${PREFIX}.view.button.sameLineRel`).d('同行关联'),
    CANCEL_REL: intl.get(`${PREFIX}.view.button.cancelRel`).d('取消关联'),

    HEADER: intl.get(`${PREFIX}.view.title.header`).d('字段映射'),
    DETAIL: intl.get(`${PREFIX}.view.title.detail`).d('字段映射明细'),
    BASIC_INFO: intl.get(`${PREFIX}.view.title.basicInfo`).d('基本信息'),
    DETAIL_INFO: intl.get(`${PREFIX}.view.title.detailInfo`).d('字段映射维护'),
    VERSION_HISTORY: intl.get(`${PREFIX}.view.title.versionHistory`).d('历史版本'),
    STATUS: intl.get(`${PREFIX}.view.title.status`).d('状态'),

    SEQ_NUMBER: intl.get(`${PREFIX}.model.fieldMapping.seqNumber`).d('序号'),
    TRANSFORM_CODE: intl.get(`${PREFIX}.model.fieldMapping.transformCode`).d('字段映射代码'),
    TRANSFORM_NAME: intl.get(`${PREFIX}.model.fieldMapping.transformName`).d('字段映射名称'),
    TRANSFORM_TYPE: intl.get(`${PREFIX}.model.fieldMapping.transformType`).d('字段映射类型'),
    VERSION: intl.get(`${PREFIX}.model.fieldMapping.version`).d('版本'),
    FROM_VERSION: intl.get(`${PREFIX}.model.fieldMapping.fromVersion`).d('来源版本'),
    TRANSFORM_SCRIPT: intl.get(`${PREFIX}.model.fieldMapping.transformScript`).d('映射转化脚本'),
    SOURCE_STRUCTURE: intl.get(`${PREFIX}.model.fieldMapping.sourceStructure`).d('映射来源结构'),
    TARGET_STRUCTURE: intl.get(`${PREFIX}.model.fieldMapping.targetStructure`).d('映射目标结构'),

    SOURCE_TITLE: intl.get(`${PREFIX}.model.fieldMapping.sourceTitle`).d('来源结构'),
    SOURCE_TITLE_TIP: intl
      .get(`${PREFIX}.model.fieldMapping.sourceTitle.tip`)
      .d(
        '来源结构，设定来源数据结构，通过JSON结构体表达数据结构。通过与目标结构连线构造出DW脚本，可实现JSON与JSON、XML与XML、JSON与XML之间的互相转换。'
      ),
    TARGET_TITLE: intl.get(`${PREFIX}.model.fieldMapping.targetTitle`).d('目标结构'),
    TARGET_TITLE_TIP: intl
      .get(`${PREFIX}.model.fieldMapping.targetTitle.tip`)
      .d(
        '来源结构，设定目标数据结构，通过JSON结构体表达数据结构。通过与来源结构连线构造出DW脚本，可实现JSON与JSON、XML与XML、JSON与XML之间的互相转换。'
      ),
    FIELD_DATA: intl.get(`${PREFIX}.view.modal.fieldData`).d('字段数据'),

    EXEC_CONFIRM: intl.get(`${PREFIX}.view.modal.execConfirm`).d('确定执行映射转化吗'),

    SAVE_VALIDATE: intl.get(`${PREFIX}.model.fieldMapping.saveValidate`).d('请先完善必输内容'),
    SAVE_EMPTY: intl.get(`${PREFIX}.model.fieldMapping.saveEmpty`).d('无修改内容,无需保存'),

    MODAL_EDIT_INFO: intl
      .get(`${PREFIX}.view.message.editInfo`)
      .d('关联接口已上线，不允许编辑当前配置'),
  };
  return LANGS[key];
};

export default getLang;
