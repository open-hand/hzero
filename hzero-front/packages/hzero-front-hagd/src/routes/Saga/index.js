/**
 * saga - 事务定义
 * @date: 2018-12-24
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import FilterForm from './FilterForm';
import Drawer from './Drawer';

@connect(({ loading, saga }) => ({
  saga,
  fetchLoading: loading.effects['saga/fetchSagaList'],
  detailLoading: loading.effects['saga/querySagaDetail'],
}))
@formatterCollections({ code: ['hagd.saga'] })
export default class Saga extends React.Component {
  state = {
    modalVisible: false,
  };

  form;

  componentDidMount() {
    this.fetchSagaList();
  }

  fetchSagaList(params = {}) {
    const {
      dispatch,
      saga: { pagination = {} },
    } = this.props;
    const fieldsValue = this.form.getFieldsValue();
    dispatch({
      type: 'saga/fetchSagaList',
      payload: { page: pagination, ...fieldsValue, ...params },
    });
  }

  @Bind()
  handleBindRef(ref) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 查询表单
   */
  @Bind()
  handleSearch() {
    this.fetchSagaList();
  }

  /**
   * 重置查询表单
   */
  @Bind()
  handleResetSearch() {
    this.form.resetFields();
  }

  @Bind()
  handleShowDetail(record = {}) {
    const { dispatch } = this.props;
    this.setState({ modalVisible: true });
    dispatch({
      type: 'saga/querySagaDetail',
      payload: { id: record.id },
    });
  }

  @Bind()
  handleModalVisible() {
    this.setState({ modalVisible: false });
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchSagaList({ page: pagination });
  }

  render() {
    const {
      fetchLoading = false,
      refreshLoading = false,
      detailLoading = false,
      saga: { sagaList = [], sagaDetail = {}, pagination = {} },
    } = this.props;
    const { modalVisible } = this.state;
    const columns = [
      {
        title: intl.get('hagd.saga.model.saga.code').d('编码'),
        dataIndex: 'code',
        width: 200,
      },
      {
        title: intl.get('hagd.saga.model.saga.service').d('所属微服务'),
        dataIndex: 'service',
        width: 200,
      },
      {
        title: intl.get('hagd.saga.model.saga.description').d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        width: 120,
        render: (_, record) => {
          const operators = [
            {
              key: 'detail',
              ele: (
                <span className="action-link">
                  <a onClick={() => this.handleShowDetail(record)}>
                    {intl.get('hzero.common.button.detail').d('详情')}
                  </a>
                </span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.detail').d('详情'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <>
        <Header title={intl.get('hagd.saga.view.title.header').d('事务定义')} />
        <Content>
          <div className="table-list-search">
            <FilterForm
              onSearch={this.handleSearch}
              onReset={this.handleResetSearch}
              onRef={this.handleBindRef}
            />
          </div>
          <Table
            bordered
            rowKey="id"
            loading={fetchLoading || refreshLoading}
            dataSource={sagaList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            onChange={this.handlePagination}
            pagination={pagination}
          />
          <Drawer
            title={intl
              .get('hagd.saga.view.title.modal', { code: sagaDetail.code })
              .d(`${sagaDetail.code}-事务详情`)}
            initLoading={detailLoading}
            loading={detailLoading}
            initData={sagaDetail}
            modalVisible={modalVisible}
            onCancel={this.handleModalVisible}
          />
        </Content>
      </>
    );
  }
}
