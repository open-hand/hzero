import React, { Component } from 'react';
import { Spin, Button } from 'hzero-ui';

import styles from '../style/index.less';

export default class List extends Component {
  render() {
    const {
      processNodes = [],
      defaultSelectedNode = '',
      handleSelectNodes = () => {},
      fetchProcessNodeLoading,
    } = this.props;
    return (
      <Spin spinning={fetchProcessNodeLoading} wrapperClassName={styles['process-node-list-spin']}>
        {processNodes.map(item => (
          <Button
            type="dashed"
            key={item.nodeId}
            className={item.nodeId === defaultSelectedNode && styles['process-node-list-selected']}
            onClick={() => handleSelectNodes(item.nodeId)}
          >
            {item.name}
          </Button>
        ))}
      </Spin>
    );
  }
}
