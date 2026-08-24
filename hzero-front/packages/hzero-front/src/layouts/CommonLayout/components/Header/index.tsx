/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import React from 'react';

import { getClassName as getCommonLayoutClassName } from '../../utils';

import DefaultLogo from './components/Logo';
import DefaultToolbar from './components/Toolbar';

interface HeaderProps {
  components: {
    Logo: React.FC;
    Toolbar: React.FC;
  };
  collapsed: boolean;
  setCollapsed: (collapsed: boolean) => void;
  getClassName: (cls: string) => string;
  logoProps: any;
  toolbarProps: any;
  // props
  logo: string;
  title: string;
  dispatch: () => void;
  dataHierarchyFlag: number;
  hierarchicalSelectList: any[];
  isModal: number;
  isSelect: number;
}

const Header: React.FC<HeaderProps> = ({
  getClassName = getCommonLayoutClassName,
  components = {
    Logo: DefaultLogo,
    Toolbar: DefaultToolbar,
  },
  collapsed,
  setCollapsed,
  logoProps,
  toolbarProps,
  // props
  logo,
  title,
  dispatch,
  dataHierarchyFlag,
  hierarchicalSelectList,
  isModal,
  isSelect,
}) => {
  const { Logo, Toolbar } = components;
  return (
    <div className={getClassName('header')}>
      <div className="hzero-common-layout-header-container">
        <Logo logo={logo} title={title} {...logoProps} />
        <Toolbar
          setCollapsed={setCollapsed}
          collapsed={collapsed}
          {...toolbarProps}
          dispatch={dispatch}
          dataHierarchyFlag={dataHierarchyFlag}
          hierarchicalSelectList={hierarchicalSelectList}
          isModal={isModal}
          isSelect={isSelect}
        />
      </div>
    </div>
  );
};

export default Header;
