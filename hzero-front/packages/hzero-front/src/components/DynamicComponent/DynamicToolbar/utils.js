/**
 * 仅对 Toolbar 按钮的onClick 做特殊处理
 * @author WY yang.wang06@hand-china.com
 */
import { Button } from 'hzero-ui';
import { forEach, set } from 'lodash';

import { dealObjectProps } from '../utils';

const types = {
  Button,
};

export function dealToolbarProps(toolbarProps, context) {
  return dealObjectProps(toolbarProps, context);
}

export function dealButtonProps({ field, context }) {
  const otherProps = {};
  forEach(field.props, (propV, propK) => {
    set(otherProps, propK, propV);
  });
  const { btn } = otherProps;
  delete otherProps.btn;
  const dealProps = dealObjectProps(otherProps, context);
  return { componentProps: dealProps, btn };
}

/**
 * getBtnType - 获取字段的组件类型
 * @param {Object} field - 字段
 */
export function getBtnType({ field }) {
  return types[field.componentType] || types.Button;
}
