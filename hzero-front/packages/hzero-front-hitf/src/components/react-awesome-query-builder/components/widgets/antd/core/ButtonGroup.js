import React from 'react';
import { Button } from 'hzero-ui';
const ButtonGroup = Button.Group;

export default ({ children, config: { settings } }) => {
  const { renderSize } = settings;
  return <ButtonGroup size={renderSize}>{children}</ButtonGroup>;
};
