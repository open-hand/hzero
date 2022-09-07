import React from 'react';
import { NodeMenu, EdgeMenu, ContextMenu } from 'gg-editor';
import MenuItem from './MenuItem';
import styles from './index.less';

const FlowContextMenu = () => {
  return (
    <ContextMenu className={styles.contextMenu}>
      <NodeMenu>
        <MenuItem command="delete" />
      </NodeMenu>
      <EdgeMenu>
        <MenuItem command="delete" />
      </EdgeMenu>
    </ContextMenu>
  );
};

export default FlowContextMenu;
