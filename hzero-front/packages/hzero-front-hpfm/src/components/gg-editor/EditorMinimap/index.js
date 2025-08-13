import React from 'react';
import { Card } from 'choerodon-ui';
import { Minimap } from 'gg-editor';

import styles from './index.less';

const EditorMinimap = () => (
  <Card
    style={{ height: '100%', width: 'auto' }}
    type="inner"
    size="small"
    title="Minimap"
    bordered={false}
    className={styles['editor-mini-map-card']}
  >
    <Minimap height={200} />
  </Card>
);

export default EditorMinimap;
