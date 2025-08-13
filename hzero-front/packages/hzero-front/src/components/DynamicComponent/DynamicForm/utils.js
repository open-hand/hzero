/**
 * utils
 * @date 2018/10/22
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
/* eslint-disable no-param-reassign */

import { isFunction, find, isNil } from 'lodash';

import {
  postProcessComponentProps, // 公共的组件属性 后处理
  defaultPostPropProcess,
} from '../utils';
import { fieldNameProp } from '../config';

// 后处理组件属性 Post 只能改变 props 下级及 field 非 props 下级 的值。
export function postDynamicFormProcessComponentProps({
  field = {},
  dealProps,
  dataSource,
  form,
  fields,
}) {
  const postPropProcess = postPropProcesses[field.componentType];
  if (postPropProcess) {
    return postPropProcess({ field, dealProps, dataSource, form, fields });
  } else {
    return defaultPostPropProcess(dealProps);
  }
}

// 表单
export const postPropProcesses = {
  ValueList: postValueListPropProcess,
  Lov: postLovPropProcess,
};

/**
 * 处理级联
 * @param {Object} field - 需要处理级联的字段
 * @param {Object} props - 经过处理过的属性
 * @param {Object} form - 表单
 * @param {Object[]} fields - 所有的字段
 */
function processForCascade({ field, form, fields, props }) {
  const { cascadeFlag, cascadeField, cascadeFrom, queryParams } = props;
  delete props.cascadeField;
  delete props.cascadeFrom;
  delete props.cascadeFlag;
  if (cascadeFlag && cascadeFrom && cascadeField) {
    // 如果是 级联 且 级联字段为 null 或者 undefined 则设置为 disabled
    props.disabled = isNil(form.getFieldValue(cascadeFrom));
    if (queryParams) {
      props.queryParams = () => {
        let q;
        if (isFunction(queryParams)) {
          q = queryParams();
        } else {
          q = { ...queryParams };
        }
        const cascadeValue = form.getFieldValue(cascadeFrom);
        q[cascadeField] = cascadeValue;
        return q;
      };
    } else {
      props.queryParams = () => {
        const cascadeValue = form.getFieldValue(cascadeFrom);
        return {
          [cascadeField]: cascadeValue,
        };
      };
    }
    const cascadeF = find(fields, f => f[fieldNameProp] === cascadeFrom);
    const cascadeTo = field[fieldNameProp];
    if (cascadeF && cascadeF.props) {
      const cascadeFProps = cascadeF.props;
      if (isFunction(cascadeFProps.onChange)) {
        const c = cascadeFProps.onChange;
        cascadeFProps.onChange = () => {
          c();
          form.setFieldsValue({ [cascadeTo]: undefined });
        };
      } else {
        cascadeFProps.onChange = () => {
          form.setFieldsValue({ [cascadeTo]: undefined });
        };
      }
    }
  }
}

function postValueListPropProcess({ field, dealProps, form, dataSource, fields }) {
  const props = postProcessComponentProps({
    field,
    componentType: 'ValueList',
    dealProps,
    dataSource,
  });
  // 处理 ValueList 级连
  processForCascade({ field, form, fields, props });
  return props;
}

function postLovPropProcess({ field, dealProps, form, dataSource, fields }) {
  const props = postProcessComponentProps({
    field,
    componentType: 'Lov',
    dealProps,
    dataSource,
  });
  // 处理 Lov 级连
  processForCascade({ field, form, fields, props });
  return props;
}
