import React, { cloneElement } from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Form } from 'hzero-ui';

import { PENDING, SUCCESS } from 'components/Permission/Status';

const FormItems = Form.Item;

export default class FormItem extends React.Component {
  // 获取传递的context
  static contextTypes = {
    permission: PropTypes.object,
  };

  state = {
    status: PENDING,
    controllerType: 'disabled',
  };

  // 在 render 之前检查权限
  // eslint-disable-next-line
  componentDidMount() {
    const { permissionList } = this.props;
    if (permissionList !== undefined && Array.isArray(permissionList)) {
      this.check(this.props, this.context);
    }
  }

  /**
   * 调用 context 的 check
   * @param {object} props - 检查所需参数
   * @param {object} context - 上下文
   */
  @Bind()
  check(props, context) {
    const { permissionList = [] } = props;
    if (context.permission) {
      context.permission.check({ permissionList }, this.handlePermission);
    }
  }

  @Bind()
  handlePermission(status, controllerType = 'disabled') {
    this.setState({
      status,
      controllerType,
    });
  }

  @Bind
  extendProps() {
    const { permissionList, children, ...otherProps } = this.props;
    const { status, controllerType } = this.state;
    if (permissionList === undefined || !Array.isArray(permissionList)) {
      return <FormItems {...otherProps}>{children}</FormItems>;
    }
    if (status === SUCCESS) {
      if (controllerType === 'disabled') {
        // 处理 field 嵌套
        if (Array.isArray(children) && React.Children.count(children) === 1) {
          return (
            <FormItems {...otherProps}>{cloneElement(children[0], { disabled: true })}</FormItems>
          );
        } else {
          return (
            <FormItems {...otherProps}>{cloneElement(children, { disabled: true })}</FormItems>
          );
        }
      } else {
        // approved=true，则controllerType=disabled则禁用，其他，则不控制
        return <FormItems {...otherProps}>{children}</FormItems>;
      }
    } else {
      return null;
    }
  }

  render() {
    return this.extendProps();
  }
}
