import React from 'react';

import { defaultGetSideCascaderMenuMaskClassName } from './utils';

const SideMask = ({
  getClassName = defaultGetSideCascaderMenuMaskClassName,
  onTrigger,
  leftTopStyle,
  rightStyle,
}) => (
  <>
    <div className={getClassName('left-top')} onMouseEnter={onTrigger} style={leftTopStyle} />
    <div className={getClassName('right')} onMouseEnter={onTrigger} style={rightStyle} />
  </>
);

export default SideMask;
