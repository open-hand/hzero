/**
 * 通用-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-7-8
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

export const PREFIX = 'horc.common';

export default {
  PREFIX,
  DW_SCRIPT_TIP: intl
    .get(`${PREFIX}.view.tip.dw`)
    .d('请参考DataWeave Language语法编写映射脚本，官方文档参见'),

  AND: intl.get(`${PREFIX}.view.logicOperation.and`).d('与'),
  OR: intl.get(`${PREFIX}.view.logicOperation.or`).d('或'),
  EQUAL: intl.get(`${PREFIX}.view.logicOperation.equal`).d('等于'),
  NOT_EQUAL: intl.get(`${PREFIX}.view.logicOperation.notEqual`).d('不等于'),
  LESS: intl.get(`${PREFIX}.view.logicOperation.less`).d('小于'),
  LESS_OR_EQUAL: intl.get(`${PREFIX}.view.logicOperation.lessOrEqual`).d('小于等于'),
  GREATER: intl.get(`${PREFIX}.view.logicOperation.greater`).d('大于'),
  GREATER_OR_EQUAL: intl.get(`${PREFIX}.view.logicOperation.greaterOrEqual`).d('大于等于'),
  IS_EMPTY: intl.get(`${PREFIX}.view.logicOperation.isEmpty`).d('为空'),
  IS_NOT_EMPTY: intl.get(`${PREFIX}.view.logicOperation.isNotEmpty`).d('非空'),
  VALUE: intl.get(`${PREFIX}.view.logicOperation.value`).d('值'),
  CONDITION_FIELD: intl.get(`${PREFIX}.view.logicOperation.conditionField`).d('条件字段'),
  CONDITION: intl.get(`${PREFIX}.view.logicOperation.condition`).d('条件'),
  ADD_CONDITION_GROUP: intl
    .get(`${PREFIX}.view.logicOperation.addConditionGroup`)
    .d('添加条件分组'),
  ADD_CONDITION: intl.get(`${PREFIX}.view.logicOperation.addCondition`).d('添加条件'),
  NOT: intl.get(`${PREFIX}.view.logicOperation.not`).d('非'),
  SURE: intl.get(`${PREFIX}.view.logicOperation.sure`).d('确定'),
  DELETE_CONDITION_CONFIRM: intl
    .get(`${PREFIX}.view.logicOperation.deleteConditionConfirm`)
    .d('确定删除该条件'),
  DELETE_GROUP_CONFIRM: intl
    .get(`${PREFIX}.view.logicOperation.deleteGroupConfirm`)
    .d('确定删除该条件分组'),
};
