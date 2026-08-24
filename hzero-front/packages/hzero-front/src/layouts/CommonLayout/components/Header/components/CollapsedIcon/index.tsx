/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/23
 * @copyright HAND ® 2019
 */

import React from 'react';
import { Icon } from 'hzero-ui';

import { getClassName as getCommonLayoutClassName } from '../../../../utils';

interface CollapsedIconProps {
  collapsed: boolean;
  setCollapsed: (collapsed: boolean) => void;
  getClassName?: (cls: string) => string;
}

const CollapsedIcon: React.FC<CollapsedIconProps> = ({
  collapsed,
  setCollapsed,
  getClassName = getCommonLayoutClassName,
}) => {
  const handleToggleCollapse = React.useCallback(() => {
    setCollapsed(!collapsed);
  }, [collapsed, setCollapsed]);
  return (
    <Icon
      className={getClassName('header-collapsed-icon')}
      type={collapsed ? 'menu-unfold' : 'menu-fold'}
      onClick={handleToggleCollapse}
    />
  );
};

export default CollapsedIcon;
