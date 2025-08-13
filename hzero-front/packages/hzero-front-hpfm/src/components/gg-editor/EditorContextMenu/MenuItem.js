import React from 'react';
import { Command } from 'gg-editor';
import upperFirst from 'lodash/upperFirst';
import { Icon } from 'choerodon-ui';
import styles from './index.less';

const MenuItem = (props) => {
  const { command, icon, text } = props;

  return (
    <Command name={command}>
      <div className={styles.item}>
        <Icon type={icon || command} />
        <span>{text || upperFirst(command)}</span>
      </div>
    </Command>
  );
};

export default MenuItem;
