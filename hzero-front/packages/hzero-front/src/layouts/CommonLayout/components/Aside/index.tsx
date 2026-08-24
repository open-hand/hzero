/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import React from 'react';

import HeaderSearch from './components/HeaderSearch';

import Menu from './components/Menu';

import { getClassName as getCommonLayoutClassName } from '../../utils';

interface AsideProps {
  components: {};
  getClassName: (cls: string) => string;
  collapsed: boolean;
}

const Aside: React.FC<AsideProps> = ({ getClassName = getCommonLayoutClassName, collapsed }) => {
  return (
    <div className={getClassName('aside')}>
      <HeaderSearch collapsed={collapsed} />
      <Menu />
    </div>
  );
};

export default Aside;
