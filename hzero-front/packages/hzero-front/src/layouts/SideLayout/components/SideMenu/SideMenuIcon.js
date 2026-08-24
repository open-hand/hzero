import React from 'react';

import { Icon } from 'choerodon-ui/pro';

import Icons from 'components/Icons';

const SideMenuIcon = ({ menu, classPrefix = 'main-menu-item' }) => {
  const className = React.useMemo(() => `${classPrefix}-icon ${classPrefix}-icon-img`, [
    classPrefix,
  ]);
  const { name, icon, path } = menu;
  return name.includes('choerodon') && path.includes('hlcd') ? (
    <Icon type={icon} style={{ fontSize: '12px' }} className={className} />
  ) : (
    <Icons type={icon} className={className} />
  );
};

export default SideMenuIcon;
