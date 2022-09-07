/**
 * FlowItemPanel
 * 任务流画布-节点选择面板
 * @author baitao.huang@hand-china.com
 * @date 2020/1/2
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { ItemPanel } from 'gg-editor';
import { HttpNode, FieldMappingNode, DataCastNode, ConditionNode } from './Nodes';
import LeftPanel from './LeftPanel';
import styles from './index.less';

export default class FlowItemPanel extends React.PureComponent {
  render() {
    const { disabledFlag, orchTaskType } = this.props;
    const leftPanelProps = {
      disabledFlag,
      orchTaskType,
    };
    return (
      <div className={styles['horc-item-panel']}>
        <ItemPanel>
          <HttpNode />
          <FieldMappingNode />
          <DataCastNode />
          <ConditionNode />
          <LeftPanel {...leftPanelProps} />
        </ItemPanel>
      </div>
    );
  }
}
