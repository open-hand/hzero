import React from 'react';
import { Tooltip, Icon } from 'choerodon-ui';
import { Command } from 'gg-editor';
import upperFirst from 'lodash/upperFirst';
import styles from './index.less';

const ToolbarButton = (props) => {
  const { command, icon, text } = props;

  return (
    <Command name={command}>
      <Tooltip
        title={text || upperFirst(command)}
        placement="bottom"
        overlayClassName={styles.tooltip}
      >
        <Icon type={icon || command} />
      </Tooltip>
    </Command>
  );
};

export default ToolbarButton;
