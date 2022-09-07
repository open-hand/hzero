/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/20
 * @copyright HAND ® 2019
 */
import React from 'react';
import { isUndefined } from 'lodash';
import { Icon } from 'choerodon-ui/pro';

import Icons from 'components/Icons';
import { MenuItem } from './types';

interface MenuIconProps {
  menu: MenuItem;
  classPrefix: string;
}

const MenuIcon: React.FC<MenuIconProps> = ({ menu, classPrefix }) => {
  const className = React.useMemo(() => {
    return `${classPrefix}-icon ${classPrefix}-icon-img`;
  }, [classPrefix]);
  const { name, icon, path } = menu;
  const realPath: string = typeof path === 'string' ? path : '';
  const realIcon: string = typeof icon === 'string' ? icon : '';
  if (!isUndefined(icon)) {
    return name.includes('choerodon') && realPath.includes('hlcd') ? (
      <Icon type={realIcon} style={{ fontSize: '12px' }} className={className} />
    ) : (
      <Icons style={{ width: '12px' }} type={icon} className={className} />
    );
  } else {
    return <Icons type="development-management" className={className} />;
  }
};

export default MenuIcon;
