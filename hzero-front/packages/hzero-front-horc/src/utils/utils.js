import _ from 'lodash';
import {
  SUCCESS_STATUS,
  ERROR_STATUS,
  PROCESSING_STATUS,
  WARNING_STATUS,
} from '@/constants/constants';

export const ORCH_TYPE_ENUM = {
  definition: 'definition',
  instance: 'instance',
  task: 'task',
};

/**
 * 转换字段为驼峰格式
 * @param {object} ORCHESTRATION 对象this
 * @param {string} suffix 后缀名
 */
export const joinField = (ORCHESTRATION, suffix) => {
  let { orchType } = ORCHESTRATION.state;
  // 任务实例和编排实例共用同一个明细接口，所以orchType传入与编排实例一个的标识instance即可
  orchType = orchType === ORCH_TYPE_ENUM.task ? ORCH_TYPE_ENUM.instance : orchType;
  return _.camelCase(`${orchType} ${suffix}`);
};

/**
 * 转换字段为以"_"分隔开的纯大写字符串
 * @param {object} ORCHESTRATION 对象this
 * @param {string} suffix 后缀名
 */
export const joinUpperField = (ORCHESTRATION, suffix) => {
  const { orchType } = ORCHESTRATION.state;
  return _.toUpper(`${orchType}_${suffix}`);
};

/**
 * 请求地址转换
 * @param {object} ORCHESTRATION 对象this
 * @param {string} suffix 后缀名
 */
export const transformUrl = (ORCHESTRATION) => {
  const { orchType } = ORCHESTRATION.state;
  switch (orchType) {
    case ORCH_TYPE_ENUM.definition:
      return 'orch-definitions';
    // 任务实例和编排实例共用同一个明细接口，所以orchType传入与编排实例一个的标识instance即可
    case ORCH_TYPE_ENUM.instance:
    case ORCH_TYPE_ENUM.task:
      return 'orch-instances';
    default:
      return 'orch-definitions';
  }
};

/**
 * 根据传入的状态返回Badge渲染需要的status
 * Badge取值success、processing、error、warning
 */
export const transformStatus = (status) => {
  if (SUCCESS_STATUS.includes(status)) {
    return 'success';
  }
  if (PROCESSING_STATUS.includes(status)) {
    return 'processing';
  }
  if (WARNING_STATUS.includes(status)) {
    return 'warning';
  }
  if (ERROR_STATUS.includes(status)) {
    return 'error';
  }
  return 'default';
};
