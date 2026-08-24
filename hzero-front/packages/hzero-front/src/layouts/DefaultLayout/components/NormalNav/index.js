/**
 * NormalNav
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019/8/27
 * @copyright 2019 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { getClassName } from '../../utils';

import NormalHeaderSearch from '../NormalHeaderSearch';
import DefaultMenu from '../../../components/DefaultMenu';

function getDefaultNavClassName(...paths) {
  return getClassName('nav', ...paths);
}

export default class NormalNav extends Component {
  static propTypes = {
    collapsed: PropTypes.bool.isRequired,
    getClassName: PropTypes.func,
  };

  static defaultProps = {
    getClassName: getDefaultNavClassName,
  };

  render() {
    const {
      collapsed,
      getClassName: getNavClassName,
      components = {},
      onSearchMouseEnter,
    } = this.props;

    const Menu = components.Menu || DefaultMenu;
    const HeaderSearch = components.HeaderSearch || NormalHeaderSearch;

    return (
      <div className={getNavClassName('container')}>
        <div className={getNavClassName('normal', 'search')} onMouseEnter={onSearchMouseEnter}>
          <HeaderSearch collapsed={collapsed} />
        </div>
        <div className={getNavClassName('menu')}>
          <Menu collapsed={collapsed} offsetTop={80} />
        </div>
      </div>
    );
  }
}
