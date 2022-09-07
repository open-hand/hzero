import intl from 'utils/intl';

import { isTenantRoleLevel } from 'utils/utils';

export const colOptions = [
  1,
  2,
  3,
  4,
  5,
  6,
  7,
  8,
  9,
  10,
  11,
  12,
  13,
  14,
  15,
  16,
  17,
  18,
  19,
  20,
  21,
  22,
  23,
  24,
];

export function getFieldCodeAlias(type) {
  switch (type) {
    case 'TABPANE':
      return intl.get('hpfm.customize.common.tabsCode').d('标签页编码');
    case 'COLLAPSE':
      return intl.get('hpfm.customize.common.collapseCode').d('折叠面板编码');
    default:
      return intl.get('hpfm.customize.common.fieldCode').d('字段编码');
  }
}
export function getFieldNameAlias(type) {
  switch (type) {
    case 'TABPANE':
      return intl.get('hpfm.customize.common.tabsName').d('标签页名称');
    case 'COLLAPSE':
      return intl.get('hpfm.customize.common.collapseName').d('折叠面板名称');
    default:
      return intl.get('hpfm.customize.common.fieldName').d('字段名称');
  }
}
export function getFieldConfigAlias(type) {
  switch (type) {
    case 'TABPANE':
      return intl.get('hpfm.customize.common.tabsConfig').d('标签页配置');
    case 'COLLAPSE':
      return intl.get('hpfm.customize.common.collapseConfig').d('折叠面板配置');
    default:
      return intl.get('hpfm.customize.common.fieldConfig').d('字段配置');
  }
}
export function getAddFieldAlias(type) {
  switch (type) {
    case 'TABPANE':
      return intl.get('hpfm.customize.common.addTabPane').d('添加标签页');
    case 'COLLAPSE':
      return intl.get('hpfm.customize.common.addCollapse').d('添加折叠面板');
    default:
      return intl.get('hpfm.customize.common.addField').d('添加字段');
  }
}
export function getEditFieldAlias(type) {
  switch (type) {
    case 'TABPANE':
      return intl.get('hpfm.customize.common.editTabPane').d('编辑标签页');
    case 'COLLAPSE':
      return intl.get('hpfm.customize.common.editCollapse').d('编辑折叠面板');
    default:
      return intl.get('hpfm.customize.common.editField').d('编辑字段');
  }
}
export function getDefaultActiveAlias(type) {
  switch (type) {
    case 'COLLAPSE':
      return intl.get('hpfm.customize.common.defaultExpand').d('默认展开');
    default:
      return intl.get('hpfm.customize.common.defaultActive').d('默认激活');
  }
}
export function getSingleTenantValueCode(code = '') {
  return `${code}${isTenantRoleLevel() ? '.ORG' : ''}`;
}
