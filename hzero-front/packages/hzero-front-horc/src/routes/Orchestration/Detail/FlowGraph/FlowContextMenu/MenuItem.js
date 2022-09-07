/**
 * MenuItem
 * 任务流画布-右键菜单[Item]
 * @author baitao.huang@hand-china.com
 * @date 2020/1/2
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Command } from 'gg-editor';
import { Icon } from 'hzero-ui';

import styles from './index.less';

const MenuItem = (props) => {
  const { command, icon, text } = props;

  return (
    <Command name={command}>
      <div className={styles.item}>
        <Icon type={icon} />
        <span>{text}</span>
      </div>
    </Command>
  );
};

export default MenuItem;
