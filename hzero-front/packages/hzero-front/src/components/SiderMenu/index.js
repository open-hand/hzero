import 'rc-drawer-menu/assets/index.css';
import React from 'react';
import { connect } from 'dva';
import { withRouter } from 'dva/router';
import DrawerMenu from 'rc-drawer-menu';
import SiderMenu from './SiderMenu';

class SiderMenuWrapper extends React.Component {
  handleMenuCollapse = collapsed => {
    this.props.dispatch({
      type: 'global/changeLayoutCollapsed',
      payload: collapsed,
    });
  };

  render() {
    const { collapsed = false, isMobile = false } = this.props;
    return isMobile ? (
      <DrawerMenu
        parent={null}
        level={null}
        iconChild={null}
        open={!collapsed}
        onMaskClick={this.handleMenuCollapse}
        width="256px"
      >
        <SiderMenu {...this.props} collapsed={isMobile ? false : collapsed} />
      </DrawerMenu>
    ) : (
      <SiderMenu {...this.props} collapsed={collapsed} onCollapse={this.handleMenuCollapse} />
    );
  }
}

export default withRouter(
  connect(({ global = {} }) => ({
    collapsed: global.collapsed,
    language: global.language, // todo 当语言更新时, SiderMenu 也需要更新
  }))(SiderMenuWrapper)
);
