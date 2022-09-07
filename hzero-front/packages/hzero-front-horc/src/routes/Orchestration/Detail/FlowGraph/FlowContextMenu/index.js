/**
 * FlowContextMenu
 * 任务流画布-右键菜单
 * @author baitao.huang@hand-china.com
 * @date 2020/1/2
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { CanvasMenu, ContextMenu, EdgeMenu, NodeMenu } from 'gg-editor';

import ORCHESTRATION_LANG from '@/langs/orchestrationLang';

import MenuItem from './MenuItem';
import styles from './index.less';

const FlowContextMenu = () => {
  return (
    <ContextMenu className={styles['flow-context-menu']}>
      <NodeMenu>
        <MenuItem command="copy" icon="copy" text={ORCHESTRATION_LANG.COPY} />
        <MenuItem command="editFlow" icon="edit" text={ORCHESTRATION_LANG.EDIT} />
        <MenuItem command="delete" icon="delete" text={ORCHESTRATION_LANG.DELETE} />
      </NodeMenu>
      <EdgeMenu>
        <MenuItem command="delete" icon="delete" text={ORCHESTRATION_LANG.DELETE} />
      </EdgeMenu>
      <CanvasMenu>
        <MenuItem command="undo" icon="swap-left" text={ORCHESTRATION_LANG.UNDO} />
        <MenuItem command="redo" icon="swap-right" text={ORCHESTRATION_LANG.REDO} />
        <MenuItem command="pasteHere" icon="book" text={ORCHESTRATION_LANG.PASTE} />
      </CanvasMenu>
    </ContextMenu>
  );
};

export default FlowContextMenu;
