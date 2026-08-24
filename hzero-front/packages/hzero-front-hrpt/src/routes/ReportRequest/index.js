/**
 * List - 报表平台/报表请求
 * @date: 2019-1-28
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import moment from 'moment';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { HZERO_FILE, BKT_RPT } from 'utils/config';

import { downloadFile } from 'hzero-front/lib/services/api';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import Drawer from './Drawer';

/**
 * 报表定义
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} approveAuth - 数据源
 * @reactProps {!Object} fetchApproveLoading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hrpt.reportRequest', 'entity.tenant', 'hrpt.common'] })
@connect(({ reportRequest, loading }) => ({
  reportRequest,
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
  fetchReportLoading: loading.effects['reportRequest/fetchRequestList'],
  fetchRequestDetailLoading: loading.effects['reportRequest/fetchRequestDetail'],
}))
export default class List extends Component {
  form;

  /**
   * state初始化
   */
  state = {
    drawerVisible: false,
    tableLoading: false,
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
    this.props.dispatch({ type: 'reportRequest/init' });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, currentTenantId, tenantRoleLevel } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    const { startDate, endDate, tenantId, tenantName, ...others } = fieldValues;
    dispatch({
      type: 'reportRequest/fetchRequestList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        startDate: startDate ? moment(startDate).format(DEFAULT_DATETIME_FORMAT) : null,
        endDate: endDate ? moment(endDate).format(DEFAULT_DATETIME_FORMAT) : null,
        tenantId: tenantRoleLevel ? currentTenantId : tenantId,
        ...others,
      },
    });
  }

  // 打开弹窗
  @Bind()
  showModal(record) {
    this.setState(
      {
        drawerVisible: true,
      },
      () => {
        this.handleSearchDetail(record);
      }
    );
  }

  // 关闭弹窗
  @Bind()
  hiddenModal() {
    const { dispatch } = this.props;
    this.setState(
      {
        drawerVisible: false,
      },
      () => {
        dispatch({
          type: 'reportRequest/updateState',
          payload: {
            requestDetail: {},
          },
        });
      }
    );
  }

  @Bind()
  handleSearchDetail(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'reportRequest/fetchRequestDetail',
      payload: {
        requestId: params.requestId,
      },
    });
  }

  // 导出
  @Bind()
  handleExport(record) {
    const { currentTenantId, tenantRoleLevel } = this.props;
    const requestUrl = tenantRoleLevel
      ? `${HZERO_FILE}/v1/${currentTenantId}/files/download`
      : `${HZERO_FILE}/v1/files/download`;
    this.setState({ tableLoading: true });
    downloadFile({
      requestUrl,
      queryParams: [
        { name: 'bucketName', value: BKT_RPT },
        { name: 'bucketDirectory', value: 'hrpt02' },
        { name: 'url', value: record.fileUrl },
      ],
    }).then(() => {
      this.setState({ tableLoading: false });
    });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchReportLoading,
      fetchRequestDetailLoading,
      reportRequest: { list = [], pagination, requestStatusList = [], requestDetail = {} },
      tenantRoleLevel,
    } = this.props;
    const { drawerVisible, tableLoading } = this.state;
    const filterProps = {
      tenantRoleLevel,
      requestStatusList,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      tenantRoleLevel,
      loading: fetchReportLoading || tableLoading,
      dataSource: list,
      onDetail: this.showModal,
      onExport: this.handleExport,
      onChange: this.handleSearch,
    };
    const drawerProps = {
      tenantRoleLevel,
      fetchRequestDetailLoading,
      initData: requestDetail,
      visible: drawerVisible,
      onSearch: this.handleSearchDetail,
      onOk: this.hiddenModal,
      onCancel: this.hiddenModal,
      onExport: this.handleExport,
    };
    return (
      <>
        <Header title={intl.get('hrpt.reportRequest.view.message.title').d('报表请求')} />
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
          {drawerVisible && <Drawer {...drawerProps} />}
        </Content>
      </>
    );
  }
}
