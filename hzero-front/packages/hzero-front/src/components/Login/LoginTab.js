import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Tabs } from 'hzero-ui';

const { TabPane } = Tabs;

const generateId = (() => {
  let i = 0;
  return (prefix = '') => {
    i += 1;
    return `${prefix}${i}`;
  };
})();

export default class LoginTab extends Component {
  static __ANT_PRO_LOGIN_TAB = true;

  static contextTypes = {
    tabUtil: PropTypes.object,
  };

  constructor(props) {
    super(props);
    this.uniqueId = generateId('login-tab-');
  }
  //  eslint-disable-next-line
  UNSAFE_componentWillMount() {
    if (this.context.tabUtil) {
      this.context.tabUtil.addTab(this.uniqueId);
    }
  }

  render() {
    return <TabPane {...this.props} />;
  }
}
