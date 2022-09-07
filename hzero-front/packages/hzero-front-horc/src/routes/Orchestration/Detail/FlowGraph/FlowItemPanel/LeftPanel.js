/**
 * 任务面板
 * @author baitao.huang@hand-china.com
 * @date 2020/4/9
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Menu, Icon } from 'choerodon-ui';
import { Item } from 'gg-editor';

const LeftPanel = (props) => {
  const { disabledFlag, orchTaskType = [] } = props;
  const renderMenuItem = () => {
    const treeNodes = orchTaskType.map((item) => {
      return (
        <Menu.Item key={item.value}>
          {disabledFlag ? renderItem(item) : renderNodeItem(item, props)}
        </Menu.Item>
      );
    });
    return treeNodes;
  };
  return <Menu mode="vertical">{renderMenuItem()}</Menu>;
};

export default LeftPanel;

/**
 * 渲染g6节点
 * @param item
 * @param props
 * @returns {*}
 */
const renderNodeItem = (item, props) => {
  const { value, meaning } = item;
  const {
    disabledFlag,
    name = '',
    params = {},
    description,
    conditionResult = {},
    dependency,
    maxRetryTimes,
    retryInterval,
    timeout,
    taskInstancePriority,
    workerGroup,
    preTasks = [],
  } = props;
  // 节点数据
  const nodeModel = {
    name,
    meaning,
    label: name,
    type: value,
    params,
    description,
    conditionResult,
    dependency,
    maxRetryTimes,
    retryInterval,
    timeout,
    taskInstancePriority,
    workerGroup,
    preTasks,
  };
  return (
    <Item
      type="node"
      shape={`${value}-NODE`}
      size={`${name.length * 8 + 100}*48`}
      model={nodeModel}
      disabled={disabledFlag}
    >
      {renderItem(item)}
    </Item>
  );
};

const renderItem = (item) => {
  return (
    <>
      <Icon type={item.tag} />
      {item.meaning}
    </>
  );
};
