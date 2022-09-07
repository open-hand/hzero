import React, { Component, Fragment } from 'react';
import { Tree, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import styles from './style/index.less';

const { TreeNode, DirectoryTree } = Tree;

@connect(({ loading = {} }) => ({
  fetchMenuLoading: loading.effects['individuationUnit/fetchMenu'],
}))
export default class MenuTree extends Component {
  state = {
    menus: [], // 菜单数据
  };

  componentDidMount() {
    this.fetchMenu();
  }

  @Bind()
  fetchMenu() {
    this.props
      .dispatch({
        type: 'individuationUnit/fetchMenu',
      })
      .then(res => {
        if (res) {
          this.setState({ menus: res || [] });
        }
      });
  }

  @Bind()
  selectMenu(selectedKeys = [], selectedNode = {}) {
    // 解决双击时tree组件会取消选中的问题
    if (selectedKeys[0]) {
      const { handleSelectMenu = () => {} } = this.props;
      const menuCode = selectedKeys[0];
      const {
        node: {
          props: { title },
        },
      } = selectedNode;
      const menuName = title;
      handleSelectMenu(menuCode, menuName);
    }
  }

  @Bind()
  renderMenuTree(menus = []) {
    return menus.map(menu => {
      const { menuCode, menuName, subMenus } = menu;
      if (subMenus) {
        return (
          <TreeNode title={menuName} key={menuCode} selectable={false}>
            {this.renderMenuTree(subMenus)}
          </TreeNode>
        );
      }
      return <TreeNode title={menuName} key={menuCode} />;
    });
  }

  render() {
    const { menus = [] } = this.state;
    const { fetchMenuLoading } = this.props;
    return (
      <Fragment>
        <div className={styles['unit-menu-tree-title']}>
          {intl.get('hpfm.individuationUnit.view.message.title.menuTree').d('个性化目录')}
        </div>
        <Spin spinning={fetchMenuLoading} style={{ overflow: 'auto' }}>
          <DirectoryTree onSelect={this.selectMenu} className={styles['unit-menu-tree']}>
            <TreeNode
              title={intl.get('hpfm.individuationUnit.view.message.title.root').d('根目录')}
              key="root"
              icon={null}
            >
              {this.renderMenuTree(menus)}
            </TreeNode>
          </DirectoryTree>
        </Spin>
      </Fragment>
    );
  }
}
