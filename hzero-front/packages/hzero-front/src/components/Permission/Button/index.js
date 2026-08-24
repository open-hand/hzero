import React from 'react';
import PropTypes from 'prop-types';
import { Button as HzeroButton } from 'hzero-ui';
import { Button as C7nProButton } from 'choerodon-ui/pro';
import { Button as C7nButton } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { PENDING, SUCCESS } from 'components/Permission/Status';

export default class Button extends React.Component {
  // 获取传递的context
  static contextTypes = {
    permission: PropTypes.object,
  };

  state = {
    status: PENDING,
    controllerType: 'hidden',
  };

  // 在 render 之前检查权限， permissionList
  // eslint-disable-next-line
  componentDidMount() {
    const { permissionList = [] } = this.props;
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
  handlePermission(status, controllerType = 'hidden') {
    this.setState({
      status,
      controllerType,
    });
  }

  @Bind()
  renderButton(params = {}) {
    const { type = '', c7nType = 'button', ...otherProps } = params;
    switch (type) {
      case 'text':
        return <a {...otherProps}>{params.children}</a>;
      case 'c7n-pro':
        return <C7nProButton {...otherProps} type={c7nType} />;
      case 'c7n':
        return <C7nButton {...otherProps} type={c7nType} />;
      default:
        return <HzeroButton {...otherProps} type={type} />;
    }
  }

  @Bind()
  extendProps() {
    const { permissionList, className: propsClass, ...otherProps } = this.props;
    const { style = {} } = otherProps;
    const { status, controllerType } = this.state;
    const className = [propsClass, 'hzero-permission-btn'].join(' ');
    // 普通按钮不做限制
    if (permissionList === undefined) {
      return this.renderButton({ ...otherProps, className });
    }
    // 鉴权通过后的处理
    if (status === SUCCESS) {
      return this.renderButton({
        ...otherProps,
        className,
      });
      // 鉴权失败
    } else if (controllerType === 'disabled') {
      // approved=false，则controllerType=disabled则禁用，其他，则隐藏
      return this.renderButton({
        ...otherProps,
        className: [className, 'hzero-permission-btn-disabled'].join(' '),
        disabled: true,
        style: { ...style, cursor: 'not-allowed', color: 'rgba(0,0,0,0.25)' },
      });
    } else {
      return null;
    }
  }

  render() {
    return this.extendProps();
  }
}
