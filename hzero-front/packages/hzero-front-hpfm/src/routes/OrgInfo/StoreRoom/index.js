/*
 * index - 库房
 * @date: 2018/08/07 14:45:33
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component, Fragment } from 'react';
import uuid from 'uuid/v4';
import { connect } from 'dva';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  getEditTableData,
  addItemToPagination,
  delItemToPagination,
  filterNullValueObject,
} from 'utils/utils';
import notification from 'utils/notification';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

/**
 * 库房
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} storeRoom - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
const messagePrompt = 'hpfm.storeRoom';

@connect(({ loading, storeRoom }) => ({
  loading: loading.effects['storeRoom/queryStoreRoomList'],
  saving:
    loading.effects['storeRoom/saveStoreRoom'] || loading.effects['storeRoom/queryStoreRoomList'],
  storeRoom,
}))
@formatterCollections({ code: ['hpfm.storeRoom'] })
export default class StoreRoom extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    this.handleSearch();
  }

  /**
   * 查询库房列表
   * @param {Object} page
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.filterForm)
      ? {}
      : filterNullValueObject(this.filterForm.getFieldsValue());
    dispatch({
      type: 'storeRoom/queryStoreRoomList',
      payload: {
        page,
        ...filterValues,
      },
    });
  }

  /**
   * 修改行，对查询的结果进行修改
   * @param {Object} record
   */
  @Bind()
  handleEditLine(record, flag) {
    const {
      storeRoom: { storeRoomList },
      dispatch,
    } = this.props;
    const newStoreRoomList = storeRoomList.map(item =>
      record.inventoryId === item.inventoryId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    dispatch({
      type: 'storeRoom/updateState',
      payload: { storeRoomList: newStoreRoomList },
    });
  }

  /**
   * 新建行
   */
  @Bind()
  handleCreate() {
    const {
      dispatch,
      storeRoom: { storeRoomList, pagination },
      commonSourceCode,
      commonExternalSystemCode,
    } = this.props;
    dispatch({
      type: 'storeRoom/updateState',
      payload: {
        storeRoomList: [
          {
            inventoryCode: '',
            inventoryName: '',
            invOrganizationName: '',
            ouId: '',
            _status: 'create',
            inventoryId: uuid(),
            sourceCode: commonSourceCode,
            externalSystemCode: commonExternalSystemCode,
          },
          ...storeRoomList,
        ],
        pagination: addItemToPagination(storeRoomList.length, pagination),
      },
    });
  }

  /**
   * 删除新建行
   * @param {Object} record
   * @memberof StoreRoom
   */
  @Bind()
  handleDelete(record) {
    const {
      dispatch,
      storeRoom: { storeRoomList, pagination },
    } = this.props;
    const newStoreRoomList = storeRoomList.filter(item => item.inventoryId !== record.inventoryId);
    dispatch({
      type: 'storeRoom/updateState',
      payload: {
        storeRoomList: newStoreRoomList,
        pagination: delItemToPagination(storeRoomList.length, pagination),
      },
    });
  }

  /**
   *  保存：判断新增或者修改调用不同的接口
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      storeRoom: { storeRoomList, pagination },
    } = this.props;
    const inventoryList = getEditTableData(storeRoomList, ['inventoryId']);
    if (Array.isArray(inventoryList) && inventoryList.length === 0) {
      return;
    }
    dispatch({
      type: 'storeRoom/saveStoreRoom',
      payload: { inventoryList },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 取消编辑行
   * @param {Object} record
   */
  @Bind()
  onHandleCancel(record) {
    const {
      dispatch,
      storeRoom: { storeRoomList },
    } = this.props;
    const newStoreRoomList = storeRoomList.map(item =>
      item.inventoryId === record.inventoryId ? { ...item, _status: null } : item
    );
    dispatch({
      type: 'storeRoom/updateState',
      payload: { storeRoomList: newStoreRoomList },
    });
  }

  render() {
    const {
      loading,
      saving,
      commonSourceCode,
      match,
      storeRoom: { storeRoomList, pagination },
    } = this.props;

    const isSaveList = storeRoomList.filter(
      item => item._status === 'create' || item._status === 'update'
    );
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: node => {
        this.filterForm = node.props.form;
      },
    };
    const listProps = {
      commonSourceCode,
      loading,
      pagination,
      match,
      dataSource: storeRoomList,
      onEditLine: this.handleEditLine,
      onDelete: this.handleDelete,
      onSearch: this.handleSearch,
    };

    return (
      <Fragment>
        <Header title={intl.get(`${messagePrompt}.title`).d('库房')}>
          <ButtonPermission
            type="primary"
            icon="save"
            loading={saving && !isEmpty(isSaveList)}
            onClick={this.handleSave}
            disabled={isEmpty(isSaveList)}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '库房-新建',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content noCard>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
        </Content>
      </Fragment>
    );
  }
}
