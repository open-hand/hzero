import React, { cloneElement, isValidElement } from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';

import { PENDING, SUCCESS } from 'components/Permission/Status';

export default class Field extends React.Component {
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

  /**
   * 检查权限后的回调函数
   * @param {number} status - 权限状态
   * @param {string} controllerType - 权限的控制类型
   */
  @Bind()
  handlePermission(status, controllerType = 'disabled') {
    this.setState({
      status,
      controllerType,
    });
  }

  /**
   * 递归遍历 react 节点
   * @param {array} children - react node
   * @param {function} fn - 回调函数
   */
  @Bind()
  mapReactChildren(children, fn) {
    return React.Children.map(children, child => {
      if (!isValidElement(child)) {
        return child;
      }
      if (child.props.children) {
        // eslint-disable-next-line
        child = React.cloneElement(child, {
          children: this.mapReactChildren(child.props.children, fn),
        });
      }
      return fn(child);
    });
  }

  @Bind()
  extendProps() {
    const { permissionList, children } = this.props;
    const { status, controllerType } = this.state;
    if (!permissionList || !Array.isArray(permissionList)) {
      return children;
    }
    if (status === SUCCESS) {
      if (controllerType === 'disabled') {
        return this.mapReactChildren(children, child => cloneElement(child, { disabled: true }));
      } else {
        // approved=true，则controllerType=disabled则禁用，其他，则不控制
        return children;
      }
    } else {
      return null;
    }
  }

  render() {
    return this.extendProps();
  }
}
