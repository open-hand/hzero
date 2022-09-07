/**
 * serviceRoute - 服务路由
 * @date: 2019-1-23
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Table, Popconfirm, Button, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { yesOrNoRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

import FilterForm from './FilterForm';
import Drawer from './Drawer';

@formatterCollections({ code: ['hadm.serviceRoute', 'hadm.common', 'hadm.nodeRule'] })
@connect(({ loading, hadmServiceRoute }) => ({
  hadmServiceRoute,
  fetchLoading: loading.effects['hadmServiceRoute/fetchServiceRouteList'],
  fetchDetailLoading: loading.effects['hadmServiceRoute/fetchServiceRouteDetail'],
  updateLoading: loading.effects['hadmServiceRoute/updateServiceRoute'],
  createLoading: loading.effects['hadmServiceRoute/createServiceRoute'],
  refreshLoading: loading.effects['hadmServiceRoute/refreshServiceRoute'],
}))
export default class ServiceRoute extends React.PureComponent {
  state = {
    modalVisible: false,
    fieldsValue: {},
  };

  componentDidMount() {
    this.fetchServiceRouteList();
  }

  fetchServiceRouteList(params = {}) {
    const { dispatch } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'hadmServiceRoute/fetchServiceRouteList',
      payload: { ...fieldsValue, ...params },
    });
  }

  @Bind()
  handleRefresh() {
    const that = this;
    const { dispatch } = this.props;
    Modal.confirm({
      title: `${intl.get('hadm.common.view.message.confirm.refresh').d('确定要刷新')}?`,
      content: intl
        .get('hadm.serviceRoute.view.message.refresh.description')
        .d('将动态刷新服务路由'),
      onOk() {
        dispatch({
          type: 'hadmServiceRoute/refreshServiceRoute',
        }).then((res) => {
          if (res) {
            notification.success();
            that.fetchServiceRouteList();
          }
        });
      },
    });
  }

  /**
   * 查询表单
   * @param {object} form - 查询表单
   */
  @Bind()
  handleSearch(form) {
    const fieldsValue = form.getFieldsValue();
    this.setState({ fieldsValue });
    this.fetchServiceRouteList({ ...fieldsValue, page: {} });
  }

  /**
   * 重置查询表单
   * @param {object} form - 查询表单
   */
  @Bind()
  handleResetSearch(form) {
    this.setState({ fieldsValue: {} });
    form.resetFields();
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchServiceRouteList({ page: pagination });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: flag });
  }

  /**
   * 显示编辑模态框
   * @param {object} record - 编辑的行数据
   */
  @Bind()
  showModal(record = {}) {
    const { dispatch } = this.props;
    if (record.serviceRouteId) {
      dispatch({
        type: 'hadmServiceRoute/fetchServiceRouteDetail',
        payload: {
          serviceRouteId: record.serviceRouteId,
        },
      });
    } else {
      dispatch({
        type: 'hadmServiceRoute/updateState',
        payload: { serviceRouteDetail: {} },
      });
    }
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  @Bind()
  handleUpdateRoute(fieldsValue) {
    const {
      dispatch,
      hadmServiceRoute: { serviceRouteDetail = {} },
    } = this.props;
    dispatch({
      type: `hadmServiceRoute/${
        serviceRouteDetail.serviceRouteId !== undefined
          ? 'updateServiceRoute'
          : 'createServiceRoute'
      }`,
      payload: {
        ...serviceRouteDetail,
        ...fieldsValue,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchServiceRouteList();
      }
    });
  }

  /**
   * 删除服务路由
   * @param {object} record - 删除的行数据
   */
  @Bind()
  handleDeleteServiceRoute(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'hadmServiceRoute/deleteServiceRoute',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchServiceRouteList();
      }
    });
  }

  render() {
    const {
      fetchLoading = false,
      updateLoading = false,
      createLoading = false,
      fetchDetailLoading = false,
      refreshLoading = false,
      hadmServiceRoute: { serviceRouteList = [], serviceRouteDetail = {}, pagination = {} },
    } = this.props;
    const { modalVisible } = this.state;
    const drawerProps = {
      title:
        serviceRouteDetail.serviceRouteId !== undefined
          ? intl.get('hadm.serviceRoute.view.message.editor.edit').d('编辑服务路由')
          : intl.get('hadm.serviceRoute.view.message.editor.create').d('新建服务路由'),
      modalVisible,
      initData: serviceRouteDetail,
      initLoading: fetchDetailLoading,
      loading: serviceRouteDetail.serviceRouteId !== undefined ? updateLoading : createLoading,
      onCancel: this.hideModal,
      onOk: this.handleUpdateRoute,
    };
    const columns = [
      {
        title: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
        dataIndex: 'serviceCode',
        width: 200,
      },
      {
        title: intl.get('hadm.common.model.common.serviceName').d('服务名称'),
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hadm.serviceRoute.model.serviceRoute.name').d('路由标识'),
        dataIndex: 'name',
        width: 120,
      },
      {
        title: intl.get('hadm.serviceRoute.model.serviceRoute.path').d('服务路径'),
        dataIndex: 'path',
        width: 120,
      },
      {
        title: intl.get('hadm.serviceRoute.model.serviceRoute.url').d('物理路径'),
        dataIndex: 'url',
        width: 120,
      },
      {
        title: intl.get('hadm.serviceRoute.model.serviceRoute.stripPrefix').d('去掉前缀'),
        dataIndex: 'stripPrefix',
        width: 120,
        render: yesOrNoRender,
      },
      {
        title: intl
          .get('hadm.serviceRoute.model.serviceRoute.customSensitiveHeaders')
          .d('自定义敏感头'),
        dataIndex: 'customSensitiveHeaders',
        width: 160,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hadm.serviceRoute.model.serviceRoute.helperService').d('敏感头部列表'),
        dataIndex: 'sensitiveHeaders',
        width: 160,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        width: 120,
        fixed: 'right',
        render: (text, record) => (
          <span className="action-link">
            <a onClick={() => this.showModal(record)}>
              {intl.get('hzero.common.button.edit').d('编辑')}
            </a>
            <Popconfirm
              title={intl.get('hadm.nodeRule.view.message.confirm.remove').d('是否删除此条记录？')}
              onConfirm={() => this.handleDeleteServiceRoute(record)}
            >
              <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
            </Popconfirm>
          </span>
        ),
      },
    ];
    return (
      <>
        <Header title={intl.get('hadm.common.title.serviceRoute').d('服务路由')}>
          <Button type="primary" icon="plus" style={{ marginLeft: 10 }} onClick={this.showModal}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
          <Button icon="sync" onClick={this.handleRefresh} loading={refreshLoading}>
            {intl.get('hadm.serviceRoute.view.button.refresh').d('刷新路由')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm search={this.handleSearch} reset={this.handleResetSearch} />
          </div>
          <Table
            bordered
            rowKey="serviceRouteId"
            loading={fetchLoading || refreshLoading}
            dataSource={serviceRouteList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <Drawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
