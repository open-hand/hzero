import React, { useState, useEffect, useMemo } from 'react';
import { DataSet, Icon, Tooltip, Spin } from 'choerodon-ui/pro';
import { Input, Tree } from 'choerodon-ui';
import classnames from 'classnames';
import { debounce } from 'lodash';

import notification from 'utils/notification';

import formatterCollections from 'utils/intl/formatterCollections';

import { treeDS } from '../../stores/apiTestDS';

const { TreeNode } = Tree;

const apiTree = (props) => {
  const treeDs = useMemo(() => new DataSet(treeDS), []);

  const [value, setValue] = useState('');
  const [treeData, setTreeData] = useState([]);
  const [expandedKeys, setExpandedKeys] = useState([]);
  const [selectedKeys] = useState([]);
  const [autoExpandParent, setAutoExpandParent] = useState(false);

  const { onClose, getDetail, setCurrentNode, setDetailFlag } = props;

  useEffect(() => {
    treeDs
      .query()
      .then((res) => {
        setTreeData(res);
        // if (res.length) {
        //   setExpandedKeys(['0', '0-0', '0-0-0']);
        //   const node = [res[0].children[0].children[0].children[0]];
        //   getDetail(node);
        //   setCurrentNode(node);
        // }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }, []);

  const renderTreeNodes = (data = []) => {
    let icon = (
      <Icon style={{ color: 'rgba(0,0,0,0.65)', fontSize: '.14rem' }} type="folder_open" />
    );

    return data.map((item) => {
      const index = item.title.indexOf(value);
      const beforeStr = item.title.substr(0, index);
      const afterStr = item.title.substr(index + value.length);
      const titleLength = item.title.length;
      const splitNum = 24;
      let apiWrapper;
      if (titleLength < splitNum) {
        apiWrapper = 'c7n-iam-apitest-api-wrapper-1';
      } else if (titleLength >= splitNum && titleLength < splitNum * 2) {
        apiWrapper = 'c7n-iam-apitest-api-wrapper-2';
      } else if (titleLength >= splitNum * 2 && titleLength < splitNum * 3) {
        apiWrapper = 'c7n-iam-apitest-api-wrapper-3';
      } else if (titleLength >= splitNum * 3 && titleLength < splitNum * 4) {
        apiWrapper = 'c7n-iam-apitest-api-wrapper-4';
      } else {
        apiWrapper = 'c7n-iam-apitest-api-wrapper-5';
      }

      const title =
        index > -1 ? (
          <span>
            {beforeStr}
            <span style={{ color: '#f50' }}>{value}</span>
            {afterStr}
          </span>
        ) : (
          <span>{item.title}</span>
        );
      if (item.method) {
        icon = (
          <div
            className={classnames(
              `c7n-iam-apitest-tree-${item.key}`,
              'c7n-iam-apitest-tree-methodTag',
              `c7n-iam-apitest-tree-methodTag-${item.method}`
            )}
          >
            <div>{item.method}</div>
          </div>
        );
      }

      if (item.children) {
        const icon2 = (
          <Icon
            style={{ color: 'rgba(0,0,0,0.65)', fontSize: '.14rem' }}
            type={expandedKeys.includes(item.key) ? 'folder_open2' : 'folder_open'}
            className={`c7n-iam-apitest-tree-${item.key}`}
          />
        );
        return (
          <TreeNode
            title={
              <Tooltip
                title={title}
                getPopupContainer={() =>
                  document.getElementsByClassName(`c7n-iam-apitest-tree-${item.key}`)[0]
                }
              >
                <div className="c7n-tree-title-ellipsis">{title}</div>
              </Tooltip>
            }
            key={item.key}
            dataRef={item}
            icon={icon2}
          >
            {renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return (
        <TreeNode
          {...item}
          title={
            <Tooltip
              title={item.description || title}
              getPopupContainer={() =>
                document.getElementsByClassName(`c7n-iam-apitest-tree-${item.key}`)[0].parentNode
                  .parentNode
              }
            >
              <div>{title}</div>
            </Tooltip>
          }
          dataRef={item}
          icon={icon}
          className={classnames({ [apiWrapper]: item.method })}
        />
      );
    });
  };

  // 展开或关闭树节点
  const onExpand = (newExpandedKeys) => {
    setExpandedKeys(newExpandedKeys);
    setAutoExpandParent(false);
  };

  const onSelect = (selectedKey, info) => {
    const arr = expandedKeys.includes(selectedKey[0])
      ? expandedKeys.filter((item) => item !== selectedKey[0])
      : expandedKeys.concat(selectedKey);
    setExpandedKeys(arr);

    if (
      info.selectedNodes[0] &&
      info.selectedNodes[0].children &&
      !info.selectedNodes[0].children.length
    ) {
      setCurrentNode(null);
      setDetailFlag('empty');
      return;
    }
    if (info.selectedNodes[0] && !info.selectedNodes[0].children) {
      setCurrentNode(info.selectedNodes);
      getDetail(info.selectedNodes);
    }
  };

  const getParentKey = (key, tree) => {
    let parentKey;
    for (let i = 0; i < tree.length; i += 1) {
      const node = tree[i];
      if (node.children) {
        if (node.children.some((item) => item.key === key)) {
          parentKey = node.key;
        } else if (getParentKey(key, node.children)) {
          parentKey = getParentKey(key, node.children);
        }
      }
    }
    return parentKey;
  };

  const getItem = (arr = [], target) => {
    arr.forEach((item) => {
      const temp = item;
      temp.parentKey = temp.key
        .split('-')
        .slice(0, temp.key.split('-').length - 1)
        .join('-');
      target.push(temp);
      if (Array.isArray(item.children)) {
        getItem(item.children, target);
      }
    });
  };

  const filterApi = debounce((e) => {
    const data = [];
    getItem(treeData, data);
    const expandedKey = data
      .map((item) => {
        if (item.title.indexOf(e) > -1) {
          return getParentKey(item.key, data);
        }
        return null;
      })
      .filter((item, i, self) => item && self.indexOf(item) === i);
    setValue(e);
    setAutoExpandParent(true);
    setExpandedKeys(e.length ? expandedKey : []);
  }, 1000);

  return (
    <div className="c7n-iam-apitest-tree-content">
      <Spin dataSet={treeDs} wrapperClassName="c7n-iam-apitest-tree-content-spin">
        <div className="c7n-iam-apitest-tree-top">
          <Input
            prefix={<Icon type="search" style={{ color: 'black' }} />}
            onChange={(e) => filterApi.call(null, e.target.value)}
          />
          <div role="none" className="c7n-iam-apitest-tree-top-button" onClick={onClose}>
            <Icon type="navigate_before" />
          </div>
        </div>
        <div className="c7n-iam-apitest-tree-main">
          <Tree
            expandedKeys={expandedKeys}
            selectedKeys={selectedKeys}
            showIcon
            onSelect={onSelect}
            onExpand={onExpand}
            autoExpandParent={autoExpandParent}
          >
            {renderTreeNodes(treeData)}
          </Tree>
        </div>
      </Spin>
    </div>
  );
};

export default formatterCollections({ code: ['hadm.apiTest'] })(apiTree);
