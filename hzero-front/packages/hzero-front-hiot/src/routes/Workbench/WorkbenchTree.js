/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 导览工作台层级树
 * onTreeNodeSelect
 * showInMonitor: 在监测指标中显示
 */
import React from 'react';
import { Tree, Input, Spin, Icon } from 'choerodon-ui';
import { DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { workbenchTreeDS } from '@/stores/workbenchDS';
import styles from './index.less';

const dataList = [];
const generateList = (data) => {
  for (let i = 0, len = data.length; i < len; i++) {
    const node = data[i];
    const { key, name, thingName, children } = node;
    dataList.push({ key, title: name || thingName });
    if (children) {
      generateList(children);
    }
  }
};
const getParentKey = (childKey, tree) => {
  let parentKey;
  for (let i = 0, len = tree.length; i < len; i++) {
    const node = tree[i];
    const { key, children } = node;
    if (children) {
      if (children.some((item) => item.key === childKey)) {
        parentKey = key;
      } else if (getParentKey(childKey, children)) {
        parentKey = getParentKey(childKey, children);
      }
    }
  }
  return parentKey;
};

export default class WorkbenchTree extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      expandedKeys: [],
      searchValue: '',
      autoExpandParent: true,
      treeData: [],
      loading: true,
      selectedKeys: [],
      tileTreeData: [], // 打平处理的数据,用于搜索
    };
    this.workbenchTreeDS = new DataSet(workbenchTreeDS());
  }

  componentDidMount() {
    this.loadWorkbenchTree();
  }

  @Bind()
  loadWorkbenchTree() {
    this.workbenchTreeDS
      .query()
      .then((resp) => {
        if (resp) {
          const treeData = this.handleFormatData(resp);
          const tileTreeData = [];
          this.handleTileTreeData(treeData, tileTreeData);
          this.setState({
            tileTreeData,
          });
          generateList(treeData);
          const { onTreeNodeSelect, showInMonitor } = this.props;
          if (!showInMonitor) {
            const firstProjectInfo = treeData[0];
            const { thingGroupId, name, code, key, levelPath } = firstProjectInfo;
            onTreeNodeSelect({ thingGroupId, code, name, levelPath }, 'project');
            this.setState({ selectedKeys: [key] });
          }
          this.setState({
            treeData,
            loading: false,
          });
        }
      })
      .catch(() => {
        this.setState({ loading: false });
      });
  }

  @Bind()
  handleFormatData(data) {
    return data.map((item) => {
      const projectNode = { ...item };
      const { name, thingGroupId, thingList, children, parentId } = projectNode;
      projectNode.key = parentId
        ? `${thingGroupId}#project#${name}#${parentId}`
        : `${thingGroupId}#project#${name}`;
      projectNode.children = [];
      if (!isEmpty(thingList)) {
        const arr = thingList.map((child) => {
          const deviceNode = { ...child };
          const { thingId, thingName, thingModelId } = deviceNode;
          deviceNode.key = `${thingId}#device#${thingName}#${thingModelId}`;
          return deviceNode;
        });
        projectNode.children.push(...arr);
      }
      if (!isEmpty(children)) {
        const arr = this.handleFormatData(children);
        projectNode.children.push(...arr);
      }
      return projectNode;
    });
  }

  @Bind()
  handleTreeExpand(expandedKeys) {
    this.setState({
      expandedKeys,
      autoExpandParent: false,
    });
  }

  /**
   * 将树形结构打平处理
   */
  @Bind()
  handleTileTreeData(list = [], target) {
    list.forEach((item) => {
      target.push(item);
      if (!isEmpty(item.children)) {
        this.handleTileTreeData(item.children, target);
      }
    });
  }

  @Bind()
  handleSearchChange(e) {
    const {
      target: { value },
    } = e;
    const { treeData } = this.state;
    const expandedKeys = dataList
      .map((item) => {
        const { key } = item;
        if (key.indexOf(value) > -1) {
          return getParentKey(key, treeData);
        }
        return null;
      })
      .filter((item, i, self) => item && self.indexOf(item) === i);
    this.setState({
      expandedKeys,
      searchValue: value,
      autoExpandParent: true,
    });
  }

  @Bind()
  handleTreeSelect(selectedKeys) {
    // selectedInfoArr[0] thingGroupId/deviceId
    // selectedInfoArr[1] 节点类型 project/device
    // selectedInfoArr[2] 节点名称 项目名称/设备名称
    // selectedInfoArr[3] 设备节点的设备模板Id，项目节点没有
    if (!isEmpty(selectedKeys)) {
      const { onTreeNodeSelect, showInMonitor } = this.props;
      if (showInMonitor) {
        onTreeNodeSelect(selectedKeys);
      } else {
        const selectedInfoArr = selectedKeys[0] ? selectedKeys[0].split('#') : [];
        const id = String(selectedInfoArr[0]);
        const selectedType = selectedInfoArr[1];
        if (selectedType === 'project') {
          const { tileTreeData } = this.state;
          const projectNode = tileTreeData.find((node) => String(node.thingGroupId) === id) || {};
          const { thingGroupId, code, name, levelPath } = projectNode;
          onTreeNodeSelect({ thingGroupId, code, name, levelPath }, selectedType);
        } else {
          const { tileTreeData } = this.state;
          const deviceNode = tileTreeData.find((node) => String(node.thingId) === id) || {};
          onTreeNodeSelect(id, selectedType, undefined, deviceNode.guid);
        }
      }
      this.setState({ selectedKeys });
    }
  }

  @Bind()
  renderTreeNode(data) {
    const { searchValue } = this.state;
    return data.map((item) => {
      const { name, thingName, key, children } = item;
      const type = key.split('#')[1];
      const newName = name || thingName || '';
      const index = newName.indexOf(searchValue);
      const beforeStr = newName.substr(0, index);
      const afterStr = newName.substr(index + searchValue.length);
      const title =
        index > -1 ? (
          <span>
            {beforeStr}
            <span style={{ color: '#f50' }}>{searchValue}</span>
            {afterStr}
          </span>
        ) : (
          <span>{newName}</span>
        );
      if (children) {
        return (
          <Tree.TreeNode
            key={key}
            title={
              <div style={{ display: 'flex' }}>
                <Icon
                  className={styles['workbench-tree-icons']}
                  type={type === 'project' ? 'folder_open2' : 'desktop_mac'}
                />
                <span style={{ whiteSpace: 'nowrap' }}>{title}</span>
              </div>
            }
          >
            {this.renderTreeNode(children)}
          </Tree.TreeNode>
        );
      }
      return (
        <Tree.TreeNode
          key={key}
          title={
            <div style={{ display: 'flex' }}>
              <Icon
                className={styles['workbench-tree-icons']}
                type={type === 'project' ? 'folder_open2' : 'desktop_mac'}
              />
              <span style={{ whiteSpace: 'nowrap' }}>{title}</span>
            </div>
          }
        />
      );
    });
  }

  render() {
    const { expandedKeys, autoExpandParent, treeData, loading, selectedKeys } = this.state;
    return (
      <div style={{ height: 'calc(100vh - 163px)', overflowY: 'auto' }}>
        <Input.Search onChange={this.handleSearchChange} />
        <Spin spinning={loading}>
          <Tree
            onExpand={this.handleTreeExpand}
            expandedKeys={expandedKeys}
            autoExpandParent={autoExpandParent}
            onSelect={this.handleTreeSelect}
            selectedKeys={selectedKeys}
          >
            {this.renderTreeNode(treeData)}
          </Tree>
        </Spin>
      </div>
    );
  }
}
