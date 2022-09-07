/**
 * 接口能力汇总-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-3-23
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

const getLang = (key) => {
  const PREFIX = 'hitf.interfaces';
  const LANGS = {
    PREFIX,
    CREATE: intl.get('hzero.common.create').d('新建'),
    INCREASE: intl.get('hzero.common.button.add').d('新增'),
    SAVE: intl.get('hzero.common.button.save').d('保存'),
    SURE: intl.get('hzero.common.button.ok').d('确定'),
    EDIT: intl.get('hzero.common.edit').d('编辑'),
    VIEW: intl.get('hzero.common.button.view').d('查看'),
    CANCEL: intl.get('hzero.common.button.cancel').d('取消'),
    DELETE: intl.get('hzero.common.button.delete').d('删除'),

    BATCH_ADD: intl.get(`${PREFIX}.view.button.add`).d('批量添加认证'),
    TEST: intl.get(`${PREFIX}.view.button.test`).d('测试'),

    AUTH_LEVEL: intl.get(`${PREFIX}.model.interfaces.authLevel`).d('认证层级'),
    AUTH_LEVEL_VALUE: intl.get(`${PREFIX}.model.interfaces.authLevelValue`).d('认证层级值'),
    AUTH_TYPE: intl.get(`${PREFIX}.model.interfaces.authType`).d('认证模式'),
    REMARK: intl.get(`${PREFIX}.model.interfaces.remark`).d('备注'),
    AUTH_INFO: intl.get(`${PREFIX}.model.interfaces.authInfo`).d('认证信息'),

    EDIT_AUTH: intl.get(`${PREFIX}.view.message.title.auth.edit`).d('编辑认证配置'),
    CREATE_AUTH: intl.get(`${PREFIX}.view.message.title.auth.create`).d('创建认证配置'),

    SAVE_VALIDATE: intl.get(`${PREFIX}.model.dataMapping.saveValidate`).d('请先完善必输内容'),
    SAVE_EMPTY: intl.get(`${PREFIX}.model.dataMapping.saveEmpty`).d('无修改内容,无需保存'),
  };
  return LANGS[key];
};

export default getLang;
