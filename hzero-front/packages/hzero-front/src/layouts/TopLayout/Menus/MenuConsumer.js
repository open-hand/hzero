/**
 * MenuConsumer
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-24
 * @copyright 2019-05-24 © HAND
 */

import React, { cloneElement, Component } from 'react';
import { Bind } from 'lodash-decorators';

import { MenuContext } from './utils';

export default class MenuConsumer extends Component {
  @Bind()
  renderChildren(menuContext) {
    const { children } = this.props;
    const { props = {} } = children;
    return cloneElement(children, {
      ...menuContext,
      ...props,
    });
  }

  render() {
    return <MenuContext.Consumer>{this.renderChildren}</MenuContext.Consumer>;
  }
}
