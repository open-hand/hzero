/**
 * UserReceiveConfig - 用户消息接收配置
 * @date: 2018-11-22
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 *
 * @notice components/* and index.less is copy from hiam, if you update these, please update there too
 * @notice 有数据转化
 */

import React, { Component } from 'react';
import { Button, Checkbox, Table } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { findIndex, forEach, indexOf, isArray, isEmpty, remove } from 'lodash';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { tableScrollWidth } from 'utils/utils';

import Main from './components/Main';

import styles from './index.less';

const btnStyle = { marginLeft: 8 };

/**
 * 判断节点是不是 中间状态
 * 1. 当前节点肯定是未选中的
 * 2. checkedList 认为是有序的
 * @param {number} curId - 当前的节点 receiveCode
 * @param {object[]} checkedList - 选中信息
 * @param {string} receiveType - 当前信息类型
 */
function isReceiveTypeIsIndeterminate(receiveType, curId, checkedList = []) {
  // 如果checkedList 不是有序的; 让 checkedList 排好序
  const parentIds = [curId];
  for (let index = 0; index < checkedList.length; index++) {
    if (parentIds.includes(checkedList[index].parentId)) {
      parentIds.push(checkedList[index].receiveCode);
      const { receiveTypeList = [] } = checkedList[index];
      if (receiveTypeList.includes(receiveType)) {
        return true;
      }
    }
  }
  return false;
}

@connect(({ userReceiveConfig, loading }) => ({
  userReceiveConfig,
  searchLoading: loading.effects['userReceiveConfig/fetchReceiveConfig'],
  saveLoading: loading.effects['userReceiveConfig/saveConfig'],
}))
@formatterCollections({ code: ['hiam.userReceiveConfig'] })
export default class UserReceiveConfig extends Component {
  form;

  /**
   * state初始化
   * @param {object} props 组件Props
   */
  constructor(props) {
    super(props);
    this.state = {
      expandedRowKeys: [], // 当前展开的树
      checkedList: [],
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render执行后获取页面数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    this.handleSearch();
    dispatch({
      type: 'userReceiveConfig/fetchMessageType',
    });
  }

  @Bind()
  handleSearch() {
    const { dispatch } = this.props;
    dispatch({
      type: 'userReceiveConfig/fetchReceiveConfig',
    }).then((res) => {
      if (res) {
        this.builderFlags(res);
      }
    });
  }

  @Bind()
  builderFlags(dataSource = []) {
    const checkedList = [];
    const flatKeys = (item, parentId) => {
      const flagParam = {
        receiveId: item.receiveId,
        receiveTypeList: (item && item.receiveTypeList) || [],
        defaultReceiveTypeList: item.defaultReceiveTypeList,
        receiveCode: item.receiveCode,
        userReceiveId: item.userReceiveId ? item.userReceiveId : null,
        objectVersionNumber: item.objectVersionNumber,
        _token: item._token,
      };
      flagParam.parentId = item.receiveCode === parentId ? '' : parentId;
      checkedList.push(flagParam);
      if (isArray(item.children) && !isEmpty(item.children)) {
        forEach(item.children, (v) => flatKeys(v, item.receiveCode));
      }
    };

    forEach(dataSource, (item) => flatKeys(item, item.receiveCode));

    this.setState({ checkedList });
  }

  // 保存
  @Bind(500)
  handleSave() {
    const { dispatch, userReceiveConfig } = this.props;
    const { checkedList } = this.state;
    const { configList = [] } = userReceiveConfig;
    const { defaultReceiveTypeList = [] } = configList[0] || {};
    const paramList = [];
    forEach(checkedList, (checkedItem) => {
      const { receiveTypeList = [] } = checkedItem;
      const newReceiveTypeList = defaultReceiveTypeList.filter(
        (tItem) => !receiveTypeList.includes(tItem)
      );
      const item = {
        userReceiveId: checkedItem.userReceiveId,
        receiveCode: checkedItem.receiveCode,
        receiveType: newReceiveTypeList.join(','),
        objectVersionNumber: checkedItem.objectVersionNumber,
        _token: checkedItem._token,
      };
      if (checkedItem.userReceiveId) {
        item.userReceiveId = checkedItem.userReceiveId;
      }
      paramList.push(item);
    });
    dispatch({
      type: 'userReceiveConfig/saveConfig',
      payload: paramList,
    }).then((res) => {
      if (res) {
        notification.success();
        // this.handleSearch();
        this.builderFlags(res);
      }
    });
  }

  // 取消
  @Bind()
  handleCancel() {
    this.handleSearch();
  }

  /**
   * 树形结构点击展开收起时的回调
   */
  @Bind()
  onExpand(expanded, record) {
    const { receiveCode } = record;
    const { expandedRowKeys } = this.state;

    if (expanded) {
      this.setState({
        expandedRowKeys: [...expandedRowKeys, receiveCode],
      });
    } else {
      const newExpandRowKeys = expandedRowKeys.filter((item) => item !== receiveCode);
      this.setState({
        expandedRowKeys: newExpandRowKeys,
      });
    }
  }

  @Bind()
  onCheckboxChange(params) {
    const { receiveCode, type, flag } = params;
    const { checkedList } = this.state;
    const index = findIndex(checkedList, (v) => v.receiveCode === receiveCode);
    const checkItem = checkedList[index];

    const addOrRemove = (item) => {
      // flag为true，代表当前已经被勾选，需要去除勾选
      if (flag) {
        remove(item && item.receiveTypeList, (v) => v === type);
      } else if (
        indexOf(item && item.receiveTypeList, type) < 0 &&
        indexOf(item.defaultReceiveTypeList, type) > -1
      ) {
        (item.receiveTypeList || []).push(type);
      }
    };
    addOrRemove(checkItem);

    /**
     * 根据父节点，选择所有的子节点
     *
     * @param {*} parentId
     */
    const iterator = (parentId) => {
      const subList = [];
      forEach(checkedList, (v) => {
        if (v.parentId === parentId) {
          addOrRemove(v);
          subList.push(v);
        }
      });
      if (subList && subList.length > 0) {
        forEach(subList, (v) => iterator(v.receiveCode));
      }
    };
    iterator(checkItem.receiveCode);

    /**
     * 反向勾选，即根据子节点反向勾选父节点
     *
     * @param {*} parentId 父节点的receiveCode
     */
    const reverseCheck = (parentId) => {
      if (!parentId) {
        return;
      }
      const sameParents = checkedList.filter((v) => v.parentId === parentId) || [];
      const temp = sameParents.filter((v) => {
        if (indexOf(v.defaultReceiveTypeList, type) < 0) {
          return true;
        }
        const idx = indexOf(v && v.receiveTypeList, type);
        return flag ? idx < 0 : idx > -1;
      });
      if (sameParents.length === temp.length || (sameParents.length !== temp.length && flag)) {
        const parentIndex = findIndex(checkedList, (v) => v.receiveCode === parentId);
        const parent = checkedList[parentIndex];
        addOrRemove(parent);

        reverseCheck(parent.parentId);
      }
    };

    reverseCheck(checkItem.parentId);

    this.setState({ checkedList });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { userReceiveConfig, searchLoading, saveLoading } = this.props;
    const { configList = [], messageTypeList = [] } = userReceiveConfig;
    const { expandedRowKeys = [], checkedList } = this.state;
    let newList = messageTypeList;
    if (configList[0]) {
      const { defaultReceiveTypeList = [] } = configList[0];
      newList = messageTypeList.filter((item) => defaultReceiveTypeList.includes(item.value));
    }
    const columns = [
      {
        title: intl.get('hiam.userReceiveConfig.model.userReceiveConfig.type').d('消息类型'),
        dataIndex: 'receiveName',
      },
    ];

    forEach(newList, (item) => {
      columns.push({
        title: intl
          .get('hiam.userReceiveConfig.model.userReceiveConfig', {
            typeName: item.meaning,
          })
          .d(`${item.meaning}`),
        dataIndex: item.value,
        width: 150,
        render: (val, record) => {
          let checkboxElement = '';
          const { receiveCode } = record;
          if (isArray(checkedList) && !isEmpty(checkedList)) {
            const index = findIndex(checkedList, (v) => v.receiveCode === record.receiveCode);
            const flagParam = checkedList[index] || {};
            const { receiveTypeList = [] } = flagParam;
            const flag = indexOf(receiveTypeList, item.value) > -1;
            if (indexOf(record.defaultReceiveTypeList, item.value) > -1) {
              checkboxElement = (
                <Checkbox
                  indeterminate={
                    !flag && isReceiveTypeIsIndeterminate(item.value, receiveCode, checkedList)
                  }
                  checked={flag}
                  onChange={() => this.onCheckboxChange({ receiveCode, flag, type: item.value })}
                >
                  {intl.get('hzero.common.status.enable').d('启用')}
                </Checkbox>
              );
            }
          }
          return checkboxElement;
        },
      });
    });

    const editTableProps = {
      expandedRowKeys,
      columns,
      scroll: { x: tableScrollWidth(columns) },
      rowKey: 'receiveCode',
      pagination: false,
      bordered: true,
      dataSource: configList,
      loading: searchLoading,
      onExpand: this.onExpand,
    };

    return (
      <div className={styles.receive}>
        <Main
          title={intl.get('hiam.userReceiveConfig.view.title.subMain.config').d('用户消息接收设置')}
        >
          <Table {...editTableProps} />
          <div className={styles['operation-btn']}>
            <Button type="primary" onClick={this.handleSave} loading={saveLoading}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
            <Button style={btnStyle} onClick={this.handleCancel}>
              {intl.get('hzero.common.button.cancel').d('取消')}
            </Button>
            <Button style={btnStyle} onClick={this.handleSearch}>
              {intl.get('hzero.common.button.refresh').d('刷新')}
            </Button>
          </div>
        </Main>
      </div>
    );
  }
}
