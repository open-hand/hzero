import React from 'react';
import { Card } from 'choerodon-ui';
import { EdgePanel, GroupPanel, MultiPanel, CanvasPanel, DetailPanel } from 'gg-editor';
import DetailForm from './DetailForm';
import styles from './index.less';

/**
 * MultiPanel: 多选属性面板
 * CanvasPanel： 画布属性面板
 */

const FlowDetailPanel = () => (
  <DetailPanel className={styles.detailPanel}>
    <EdgePanel>
      <DetailForm type="edge" />
    </EdgePanel>
    <GroupPanel>
      <DetailForm type="group" />
    </GroupPanel>
    <MultiPanel>
      <Card type="inner" size="small" title="Multi Select" bordered={false} />
    </MultiPanel>
    <CanvasPanel>
      <Card type="inner" size="small" title="Canvas" bordered={false} />
    </CanvasPanel>
  </DetailPanel>
);

export default FlowDetailPanel;
