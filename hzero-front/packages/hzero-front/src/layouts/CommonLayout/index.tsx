/**
 * 通用的布局架子
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/13
 * @copyright 2019 ® HAND
 */

import classNames from 'classnames';
import { connect } from 'dva';
import React from 'react';
import DocumentTitle from 'react-document-title';
import { getCurrentOrganizationId } from 'utils/utils';

import DefaultCheckUserSafe from '../components/DefaultCheckUserSafe';
// layouts/components
import DefaultLayoutAction from '../components/DefaultLayoutAction';
import DefaultListenAccessToken from '../components/DefaultListenAccessToken';
import DefaultListenFavicon from '../components/DefaultListenFavicon';
import DefaultListenWebSocket from '../components/DefaultListenWebSocket';

import { getClassName as getCommonLayoutClassName } from './utils';

import DefaultAside from './components/Aside';
import DefaultContentWrap from './components/ContentWrap';
import DefaultHeader from './components/Header';
import './index.less';
// import Content from './components/Content';
// import Logo from './components/Logo';
// import Menu from './components/Menu';
// import MenuTab from './components/MenuTab';
// import Search from './components/Search';
// import Toolbar from './components/Toolbar';

interface CommonLayoutProps<HeaderProps = any, AsideProps = any, ContentWrapProps = any> {
  dispatch: (any) => void;
  title: string;
  logo: string;
  dataHierarchyFlag: number;
  hierarchicalSelectList: any[];
  isModal: number;
  isSelect: number;
  components: {
    Header: React.FC;
    Aside: React.FC;
    ContentWrap: React.FC;
  };
  getClassName: (cls: string) => string;
  headerProps: HeaderProps;
  asideProps: AsideProps;
  contentWrapProps: ContentWrapProps;
}

const CommonLayout: React.FC<CommonLayoutProps> = ({
  getClassName = getCommonLayoutClassName,
  components = {
    Header: DefaultHeader,
    Aside: DefaultAside,
    ContentWrap: DefaultContentWrap,
  },
  headerProps,
  asideProps,
  contentWrapProps,
  // props
  logo,
  title,
  dispatch,
  dataHierarchyFlag,
  hierarchicalSelectList,
  isModal,
  isSelect,
}) => {
  const { Header, Aside, ContentWrap } = components;
  const [collapsed, setCollapsed] = React.useState(false);
  const layout = (
    <div
      className={classNames(getClassName('container'), {
        [getClassName('container-collapsed')]: collapsed,
      })}
    >
      <Header
        getClassName={getClassName}
        logo={logo}
        title={title}
        collapsed={collapsed}
        setCollapsed={setCollapsed}
        dispatch={dispatch}
        dataHierarchyFlag={dataHierarchyFlag}
        hierarchicalSelectList={hierarchicalSelectList}
        isModal={isModal}
        isSelect={isSelect}
        {...headerProps}
      />
      <div className="hzero-common-layout-body">
        <Aside collapsed={collapsed} {...asideProps} />
        <ContentWrap {...contentWrapProps} />
      </div>
      <DefaultLayoutAction />
      <DefaultCheckUserSafe />
      <DefaultListenAccessToken />
      <DefaultListenWebSocket />
      <DefaultListenFavicon />
    </div>
  );

  React.useEffect(() => {
    dispatch({
      type: 'user/queryDataHierarchies',
      payload: { organizationId: getCurrentOrganizationId() },
    });
    dispatch({
      type: 'global/changeLayoutCollapsed',
      payload: { collapsed: false },
    });
  }, []);
  React.useEffect(() => {
    dispatch({
      type: 'global/changeLayoutCollapsed',
      payload: { collapsed },
    });
  }, [collapsed]);
  return <DocumentTitle title={title || ''}>{layout}</DocumentTitle>;
};

export default connect(
  ({
    user = {
      currentUser: {
        title: String,
        logo: String,
        dataHierarchyFlag: Number,
      },
      roleList: Array,
      hierarchicalSelectList: Array,
      isModal: Number,
      isSelect: Number,
    },
    global,
  }) => {
    const { currentUser } = user || {};
    return {
      title: currentUser.title, // 当前标题
      logo: currentUser.logo, // 当前logo
      dataHierarchyFlag: currentUser.dataHierarchyFlag,
      roleList: user.roleList, // 当前角色
      hierarchicalSelectList: user.hierarchicalSelectList, // 数据层级下拉框列表
      isModal: user.isModal, // 是否显示模态框形式数据层级配置
      isSelect: user.isSelect, // 是否显示下拉框形式数据层级配置
      menu: global.menu, // 菜单
      menuLoad: global.menuLoad, // 菜单
      routerData: global.routerData, // 路由配置
      activeTabKey: global.activeTabKey, // 当前路由
      tabs: global.tabs, // 所有 tab 页
      language: global.language, // 当前语言
      // count: global.count, // 当前消息计数
    };
  },
  undefined,
  undefined,
  { pure: false }
)(CommonLayout);
