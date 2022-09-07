/**
 * FlowTopButtonBar
 * 任务流画布-工具栏[按钮]
 * @author baitao.huang@hand-china.com
 * @date 2020/1/2
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Button, Tooltip } from 'hzero-ui';
import { Command } from 'gg-editor';

import styles from './index.less';

const ToolbarButton = (props) => {
  const { command, icon, text, loading, disabledFlag } = props;
  return disabledFlag ? (
    <Tooltip title={text} placement="top" overlayClassName={styles['horc-flow-tooltip']}>
      <Button disabled funcType="flat" loading={loading} icon={icon} />
    </Tooltip>
  ) : (
    <Command name={command}>
      <Tooltip title={text} placement="top" overlayClassName={styles['horc-flow-tooltip']}>
        <Button funcType="flat" loading={loading} icon={icon} />
      </Tooltip>
    </Command>
  );
};

export default ToolbarButton;
