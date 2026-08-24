/**
 * NormalWrapDropdown
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/8/27
 * @copyright 2019 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Dropdown, Menu, Divider } from 'hzero-ui';
import { isArray, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

const defaultTrigger = ['click'];

// MenuItem: {key: string, ele: Element}
// MenuItem: {key: string, title: Element, children: MenuItem[]}

export default class NormalWrapDropdown extends Component {
  static propTypes = {
    ...Dropdown.propTypes,
    // items: PropTypes.array,
    host: PropTypes.element,
    menuClassName: PropTypes.string,
    onItemClick: PropTypes.func.isRequired,
  };

  static defaultProps = {
    ...Dropdown.defaultProps,
    trigger: defaultTrigger,
    items: [],
    menuClassName: '',
  };

  state = {
    visible: false,
  };

  static getDerivedStateFromProps(nextProps) {
    // 如果 visible 不为undefined 则表明 是受父组件控制 visible 的
    return !isUndefined(nextProps.visible)
      ? {
          visible: nextProps.visible,
        }
      : null;
  }

  @Bind()
  handleVisibleChange(visible) {
    const { onVisibleChange } = this.props;
    if (onVisibleChange) {
      // 如果有 onVisibleChange 方法, 则代表是受控的
      onVisibleChange(visible);
    } else {
      this.setState({
        visible,
      });
    }
  }

  @Bind()
  handleMenuClick(menu) {
    const { onItemClick } = this.props;
    onItemClick(menu);
  }

  /**
   *
   * @param menuItems
   * @return {React.Element[]}
   */
  renderOverlay(menuItems = []) {
    const renderMenu = menuItems.map(menuItem => {
      if (isArray(menuItem)) {
        return [
          ...this.renderOverlay(menuItem),
          <Divider style={{ width: '128px', margin: '4px auto' }} />,
        ];
      } else if (menuItem.children && menuItem.type !== 'subItem') {
        return (
          <Menu.SubMenu title={menuItem.title} key={menuItem.key}>
            {this.renderOverlay(menuItem.children)}
          </Menu.SubMenu>
        );
        // 在原有菜单的基础上加入子菜单的交互形式
      } else if (menuItem.ele && menuItem.type === 'subItem') {
        return menuItem.ele;
      } else {
        return <Menu.Item key={menuItem.key}>{menuItem.ele}</Menu.Item>;
      }
    });
    return renderMenu;
  }

  render() {
    const {
      host,
      items,
      disabled,
      getPopupContainer,
      placement,
      trigger,
      menuClassName,
    } = this.props;
    const { visible } = this.state;
    return (
      <Dropdown
        trigger={trigger}
        disabled={disabled}
        getPopupContainer={getPopupContainer}
        placement={placement}
        visible={visible}
        onVisibleChange={this.handleVisibleChange}
        overlay={
          <Menu selectedKeys={[]} onClick={this.handleMenuClick} className={menuClassName}>
            {this.renderOverlay(items)}
          </Menu>
        }
      >
        {host}
      </Dropdown>
    );
  }
}
