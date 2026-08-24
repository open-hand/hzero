import React from 'react';
import { connect } from 'dva';

const SideLineMenu = () => <>I&#39;m SideLineMenu</>;

export default connect(({ global = {} }) => ({
  menus: global.menu,
  activeTabKey: global.activeTabKey,
}))(SideLineMenu);
