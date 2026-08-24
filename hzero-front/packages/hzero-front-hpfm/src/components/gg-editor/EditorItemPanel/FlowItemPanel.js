import React from 'react';
import intl from 'utils/intl';
import { Card } from 'choerodon-ui';
import { ItemPanel, Item } from 'gg-editor';
import styles from './index.less';

const FlowItemPanel = () => {
  return (
    <ItemPanel className={styles.itemPanel}>
      <Card bordered={false} style={{ height: '100%' }}>
        {/* <Item
          type="node"
          size="72*72"
          shape="custom-start"
          model={{
            color: '#FA8C16',
            label: 'Start',
            labelOffsetY: 0,
            componentType: 'START',
            componentTypeDesc: intl.get('hpfm.event.model.event.start').d('开始'),
          }}
          src={startCircle}
        /> */}
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 0,
            label: { text: intl.get('hpfm.event.model.event.condition').d('条件组件') },
            componentType: 'CONDITION_EXECUTOR',
            componentTypeDesc: intl.get('hpfm.event.model.event.condition').d('条件组件'),
          }}
        >
          <div
            style={{
              width: 88,
              height: 56,
              border: '1px solid #1890FF',
              borderRadius: '5px',
              backgroundColor: '#E6F7FF',
            }}
          >
            <span style={{ lineHeight: '54px' }}>
              {intl.get('hpfm.event.model.event.condition').d('条件组件')}
            </span>
          </div>
        </Item>
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 0,
            label: intl.get('hpfm.event.model.event.method').d('方法组件'),
            componentType: 'METHOD_EXECUTOR',
            componentTypeDesc: intl.get('hpfm.event.model.event.method').d('方法组件'),
          }}
        >
          <div
            style={{
              width: 88,
              height: 56,
              border: '1px solid #1890FF',
              borderRadius: '5px',
              backgroundColor: '#E6F7FF',
            }}
          >
            <span style={{ lineHeight: '54px' }}>
              {intl.get('hpfm.event.model.event.method').d('方法组件')}
            </span>
          </div>
        </Item>
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 0,
            label: intl.get('hpfm.event.model.event.API').d('API组件'),
            componentType: 'API_EXECUTOR',
            componentTypeDesc: intl.get('hpfm.event.model.event.API').d('API组件'),
          }}
        >
          <div
            style={{
              width: 88,
              height: 56,
              border: '1px solid #1890FF',
              borderRadius: '5px',
              backgroundColor: '#E6F7FF',
            }}
          >
            <span style={{ lineHeight: '54px' }}>
              {intl.get('hpfm.event.model.event.API').d('API组件')}
            </span>
          </div>
        </Item>
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 0,
            label: intl.get('hpfm.event.model.event.webHook').d('WebHook组件'),
            componentType: 'WEBHOOK_EXECUTOR',
            componentTypeDesc: intl.get('hpfm.event.model.event.webHook').d('WebHook组件'),
          }}
        >
          <div
            style={{
              width: 88,
              height: 56,
              border: '1px solid #1890FF',
              borderRadius: '5px',
              backgroundColor: '#E6F7FF',
            }}
          >
            <span style={{ lineHeight: '54px' }}>
              {intl.get('hpfm.event.model.event.webHook').d('WebHook组件')}
            </span>
          </div>
        </Item>
        {/* <Item
          type="node"
          size="72*72"
          shape="custom-end"
          model={{
            color: '#722ED1',
            label: 'End',
            labelOffsetY: 0,
            componentType: 'END',
            componentTypeDesc: intl.get('hpfm.event.model.event.end').d('结束'),
          }}
          src={endCircle}
        /> */}
      </Card>
    </ItemPanel>
  );
};

export default FlowItemPanel;
