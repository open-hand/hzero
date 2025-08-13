/**
 * ServiceManage - 服务管理
 * @date: 2019-9-16
 * @author: wangtao <tao13.wang@hand-china.com>
 * @version: 1.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';
import cacheComponent from 'components/CacheComponent';
import { HZERO_ADM } from 'utils/config';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { tableScrollWidth, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { downloadFile } from 'hzero-front/lib/services/api';

import VersionsDrawer from './VersionsDrawer';
import FilterForm from './FilterForm';
import UpdateDrawer from './Drawer';

@connect(({ loading, hadmServiceManage }) => ({
  hadmServiceManage,
  fetchLoading: loading.effects['hadmServiceManage/fetchServiceManageList'],
  fetchDetailLoading: loading.effects['hadmServiceManage/fetchServiceManageDetail'],
  createLoading: loading.effects['hadmServiceManage/createService'],
  updateLoading: loading.effects['hadmServiceManage/updateService'],
  fetchVersionsLoading: loading.effects['hadmServiceManage/fetchServiceVersionsList'],
  createRouteLoading: loading.effects['hadmServiceManage/createServiceRoute'],
  updateRouteLoading: loading.effects['hadmServiceManage/updateServiceRoute'],
  deleteRouteLoading: loading.effects['hadmServiceManage/deleteServiceRoute'],
  fetchServiceRouteDetailLoading: loading.effects['hadmServiceManage/fetchServiceRouteDetail'],
}))
@formatterCollections({
  code: ['hadm.serviceManage', 'hadm.common', 'hadm.nodeRule', 'hadm.serviceRoute'],
})
@cacheComponent({ cacheKey: '/hadm/service' })
export default class ServiceManage extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      versionsModalVisible: false, // 版本模态框状态
      fieldsValue: {},
      actionType: '', // 当前点击按钮的类型
      currentServiceId: '', // 当前选中的服务id
      currentServiceCode: '', // 当前选中的服务编码
    };
  }

  componentDidMount() {
    this.fetchServiceManageList();
  }

  fetchServiceManageList(params = {}) {
    const {
      dispatch,
      hadmServiceManage: { pagination = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'hadmServiceManage/fetchServiceManageList',
      payload: { ...fieldsValue, page: pagination, ...params },
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
    this.fetchServiceManageList({ ...fieldsValue, page: {} });
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
   * 控制modal显示与隐藏
   * @param {boolean} flag 是否显示modal
   * @param {string} type 当前操作类型
   */
  handleModalVisible(flag, type) {
    this.setState({
      modalVisible: flag,
      actionType: type,
    });
  }

  /**
   * 显示编辑模态框
   * @param {object} record - 编辑的行数据
   */
  @Bind()
  showModal(record = {}, type) {
    const { dispatch } = this.props;
    if (type === 'manage') {
      dispatch({
        type: 'hadmServiceManage/updateState',
        payload: {
          serviceDetail: [],
        },
      });
      if (record.serviceId) {
        dispatch({
          type: 'hadmServiceManage/fetchServiceManageDetail',
          payload: { serviceId: record.serviceId },
        });
      } else {
        dispatch({
          type: 'hadmServiceManage/updateState',
          payload: { serviceDetail: [] },
        });
      }
    } else if (type === 'route') {
      if (record.serviceCode) {
        dispatch({
          type: 'hadmServiceManage/fetchServiceRouteDetail',
          payload: {
            serviceCode: record.serviceCode,
          },
        });
        this.setState({
          currentServiceCode: record.serviceCode,
        });
      } else {
        dispatch({
          type: 'hadmServiceManage/updateState',
          payload: { serviceRouteDetail: {} },
        });
      }
    }
    this.handleModalVisible(true, type);
  }

  @Bind()
  handleDownloadFile() {
    // 下载文件
    const { fieldsValue } = this.state;
    const { serviceCode, serviceName } = fieldsValue;
    const filterCode = serviceCode !== undefined ? serviceCode : '';
    const filterName = serviceName !== undefined ? serviceName : '';
    const tenantId = getCurrentOrganizationId();
    const api = isTenantRoleLevel()
      ? `${HZERO_ADM}/v1/${tenantId}/services/download/yml`
      : `${HZERO_ADM}/v1/services/download/yml`;
    downloadFile({
      requestUrl: api,
      queryParams: [
        { name: 'serviceCode', value: filterCode },
        { name: 'serviceName', value: filterName },
      ],
    });
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    const { dispatch } = this.props;
    this.handleModalVisible(false, '');
    dispatch({
      type: 'hadmServiceManage/updateState',
      payload: {
        serviceDetail: [],
      },
    });
  }

  @Bind()
  handleDeleteService(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'hadmServiceManage/deleteService',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchServiceManageList();
      }
    });
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchServiceManageList({ page: pagination });
  }

  @Bind()
  getRowKey(record) {
    return `${record.serviceCode}-${record.versionNumber}`;
  }

  /**
   * 保存服务汇总
   */
  @Bind()
  handleSaveServiceCollect(fieldsValue) {
    const {
      dispatch,
      hadmServiceManage: { serviceDetail = {} },
    } = this.props;
    const { serviceCode, serviceName, sourceKey, serviceLogo, _tls } = fieldsValue;
    const params =
      serviceDetail.serviceId !== undefined
        ? {
            ...serviceDetail,
            serviceCode,
            serviceName,
            sourceKey,
            serviceLogo,
            _tls,
          }
        : {
            serviceCode,
            serviceName,
            sourceKey,
            serviceLogo,
            enabledFlag: 1,
            _tls,
          };
    dispatch({
      type: `hadmServiceManage/${
        serviceDetail.serviceId !== undefined ? 'updateService' : 'createService'
      }`,
      payload: params,
    }).then((res) => {
      if (res) {
        this.hideModal();
        notification.success();
      }
      this.fetchServiceManageList();
    });
  }

  /**
   * 更新路由信息
   * @param {object} fieldsValue
   * @memberof ServiceManage
   */
  @Bind()
  handleUpdateRoute(fieldsValue, status) {
    const {
      dispatch,
      // hadmServiceManage: { serviceDetail = [] },
    } = this.props;
    dispatch({
      type: `hadmServiceManage/${
        status === 'create' ? 'createServiceRoute' : 'updateServiceRoute'
      }`,
      payload: {
        // ...serviceDetail,
        ...fieldsValue,
      },
    }).then((res) => {
      if (res) {
        notification.success();
      }
      dispatch({
        type: 'hadmServiceManage/fetchServiceRouteDetail',
        payload: {
          serviceCode: fieldsValue.serviceCode,
        },
      });
    });
  }

  /**
   * 模态框确定时触发
   * @param {object} fieldsValue
   */
  @Bind()
  onSave(fieldsValue, status) {
    const { actionType } = this.state;
    const { dispatch } = this.props;
    if (actionType === 'route') {
      this.handleUpdateRoute(fieldsValue, status);
    } else {
      this.handleSaveServiceCollect(fieldsValue);
    }
    dispatch({
      type: 'hadmServiceManage/updateState',
      payload: {
        serviceDetail: [],
      },
    });
  }

  @Bind()
  handleRefresh(serviceCode) {
    const { dispatch } = this.props;
    dispatch({
      type: 'hadmServiceManage/refreshRoute',
      payload: {
        serviceName: serviceCode,
      },
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * 打开服务版本模态框
   * @param {object} record
   */
  @Bind()
  showVersionsModal(record) {
    this.setVersionModalVisible(true);
    this.fetchServiceVersionsList(record);
  }

  /**
   * 设置版本模态框是否可见
   * @param {boolean} visible
   */
  @Bind()
  setVersionModalVisible(visible) {
    this.setState({
      versionsModalVisible: visible,
    });
  }

  /**
   * 获取服务版本数据
   * @param {object} record
   * @memberof ServiceManage
   */
  @Bind()
  fetchServiceVersionsList(record, params) {
    const { serviceId } = record;
    const { currentServiceId } = this.state;
    if (serviceId) {
      this.setState({
        currentServiceId: serviceId,
      });
    }
    const id = serviceId || currentServiceId;
    const {
      dispatch,
      hadmServiceManage: { versionPagination = {} },
    } = this.props;
    dispatch({
      type: 'hadmServiceManage/fetchServiceVersionsList',
      payload: { serviceId: id, page: versionPagination, ...params },
    });
  }

  @Bind()
  paginationChange(pagination) {
    this.fetchServiceVersionsList({}, { page: pagination });
  }

  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'hadmServiceManage/deleteServiceRoute',
      payload: record,
    }).then(() => {
      notification.success();
      dispatch({
        type: 'hadmServiceManage/fetchServiceRouteDetail',
        payload: {
          serviceCode: record.serviceCode,
        },
      });
    });
  }

  render() {
    const {
      fetchLoading = false,
      updateLoading = false,
      createLoading = false,
      fetchDetailLoading = false,
      fetchVersionsLoading = false,
      createRouteLoading = false,
      updateRouteLoading = false,
      fetchServiceRouteDetailLoading = false,
      deleteRouteLoading = false,
      hadmServiceManage: {
        serviceList = [],
        pagination = {},
        serviceDetail = [],
        serviceValueSetList = [],
        serviceVersionList = [],
        versionPagination = {},
      },
    } = this.props;
    const { actionType, modalVisible, versionsModalVisible, currentServiceCode } = this.state;
    const columns = [
      {
        title: intl.get('hadm.common.model.common.serviceCode').d('服务编码'),
        width: 180,
        dataIndex: 'serviceCode',
      },
      {
        title: intl.get('hadm.common.model.serviceCollect.serviceLogo').d('服务图片'),
        dataIndex: 'serviceLogo',
        width: 160,
        render: (text, record) => {
          if (text) {
            return (
              <img
                alt={record.appName}
                src={text}
                width="24"
                height="24"
                style={{ borderRadius: '50%' }}
              />
            );
          } else {
            return '';
          }
        },
      },
      {
        title: intl.get('hadm.common.model.common.serviceName').d('服务名称'),
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        fixed: 'right',
        render: (val, record) => (
          <span className="action-link">
            <a onClick={() => this.showModal(record, 'manage')}>
              {intl.get('hzero.common.button.edit').d('编辑')}
            </a>
            <a onClick={() => this.showModal(record, 'route')}>
              {intl.get('hadm.serviceManage.view.serviceManage.route').d('路由')}
            </a>
            <a onClick={() => this.showVersionsModal(record)}>
              {intl.get('hadm.serviceManage.view.serviceManage.version').d('版本')}
            </a>
            <Popconfirm
              title={intl.get('hadm.nodeRule.view.message.confirm.remove').d('是否删除此条记录？')}
              onConfirm={() => this.handleDeleteService(record)}
            >
              <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
            </Popconfirm>
          </span>
        ),
      },
    ];
    const versionsProps = {
      visible: versionsModalVisible,
      title: intl.get('hadm.common.model.common.versionNumber').d('服务版本'),
      onChange: this.paginationChange,
      onCancel: this.setVersionModalVisible,
      onOk: this.setVersionModalVisible,
      initLoading: fetchVersionsLoading,
      serviceVersionList,
      pagination: versionPagination,
    };
    return (
      <>
        <Header title={intl.get('hadm.serviceManage.view.message.title').d('服务管理')}>
          <Button icon="plus" type="primary" onClick={() => this.showModal({}, 'manage')}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
          <Button icon="download" onClick={this.handleDownloadFile}>
            {intl.get('hzero.common.button.downloadServiceInf').d('下载服务信息')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm search={this.handleSearch} reset={this.handleResetSearch} />
          </div>
          <Table
            bordered
            rowKey={this.getRowKey}
            loading={fetchLoading}
            dataSource={serviceList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <UpdateDrawer
            title={
              // eslint-disable-next-line no-nested-ternary
              actionType === 'route'
                ? isEmpty(serviceDetail)
                  ? intl
                      .get('hadm.serviceManage.view.message.title.editor.creatRoute')
                      .d('新建服务路由')
                  : intl.get('hadm.serviceManage.view.message.title.editor.route').d('编辑服务路由')
                : serviceDetail.serviceId !== undefined
                ? intl.get('hadm.serviceManage.view.message.title.editor.edit').d('编辑服务')
                : intl.get('hadm.serviceManage.view.message.title.editor.create').d('新建服务')
            }
            initLoading={
              fetchDetailLoading ||
              deleteRouteLoading ||
              createRouteLoading ||
              updateRouteLoading ||
              fetchServiceRouteDetailLoading
            }
            loading={
              // eslint-disable-next-line no-nested-ternary
              actionType === 'route'
                ? isEmpty(serviceDetail)
                  ? createRouteLoading
                  : updateRouteLoading
                : serviceDetail.serviceId !== undefined
                ? updateLoading
                : createLoading
            }
            modalVisible={modalVisible}
            serviceValueSetList={serviceValueSetList}
            initData={serviceDetail}
            onCancel={this.hideModal}
            onOk={this.onSave}
            actionType={actionType}
            currentServiceCode={currentServiceCode}
            onRefresh={this.handleRefresh}
            onDelete={this.handleDelete}
          />
          <VersionsDrawer {...versionsProps} />
        </Content>
      </>
    );
  }
}
