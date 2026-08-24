/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */

import React from 'react';

import { getClassName as getCommonLayoutClassName } from '../../../../utils';

interface SideMaskProps {
  getClassName?: (cls: string) => string;
  onTrigger: () => void;
}

const SideMask: React.FC<SideMaskProps> = ({
  getClassName = getCommonLayoutClassName,
  onTrigger,
}) => {
  const handleSideMaskClick = React.useCallback(() => {
    onTrigger();
  }, [onTrigger]);
  return (
    <>
      <div className={getClassName('menu-mask-top')} onMouseEnter={handleSideMaskClick} />
      <div className={getClassName('menu-mask-right')} onMouseEnter={handleSideMaskClick} />
    </>
  );
};

export default SideMask;
