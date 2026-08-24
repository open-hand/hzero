/**
 * List - 报表平台/报表查询
 * @date: 2018-11-28
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import { isEmpty, isUndefined } from 'lodash';
import { Button } from 'hzero-ui';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { HZERO_FILE, BKT_RPT } from 'utils/config';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { downloadFile } from 'hzero-front/lib/services/api';
import { filterNullValueObject, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

import ListTable from './ListTable';
import SearchForm from './SearchForm';
import RequestDrawer from './RequestDrawer';

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
@formatterCollections({ code: ['hrpt.reportQuery', 'entity.tenant', 'hrpt.common'] })
@connect(({ reportQuery, loading }) => ({
  reportQuery,
  tenantRoleLevel: isTenantRoleLevel(),
  currentTenantId: getCurrentOrganizationId(),
  fetchReportLoading: loading.effects['reportQuery/fetchReportList'],
  fetchRequestListLoading: loading.effects['reportQuery/fetchRequestList'],
  fetchRequestDetailLoading: loading.effects['reportQuery/fetchRequestDetail'],
}))
export default class List extends Component {
  form;

  requestForm;

  /**
   * state初始化
   */
  state = {
    tableLoading: false,
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      reportQuery: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.props.dispatch({ type: 'reportQuery/fetchReportType' });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'reportQuery/fetchReportList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
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
   * 查询个人请求数据
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleRequestSearch(fields = {}) {
    const { dispatch, currentTenantId, tenantRoleLevel } = this.props;
    const fieldValues = isUndefined(this.requestForm)
      ? {}
      : filterNullValueObject(this.requestForm.getFieldsValue());
    const { startDate, endDate, tenantId, tenantName, ...others } = fieldValues;
    dispatch({
      type: 'reportQuery/fetchRequestList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        startDate: startDate ? moment(startDate).format(DEFAULT_DATETIME_FORMAT) : null,
        endDate: endDate ? moment(endDate).format(DEFAULT_DATETIME_FORMAT) : null,
        tenantId: tenantRoleLevel ? currentTenantId : tenantId,
        ...others,
      },
    });
  }

  @Bind()
  onIndividualReport() {
    this.handleRequestSearch();
    this.setState({
      requestDrawerVisible: true,
    });
  }

  @Bind()
  closeRequestModal() {
    this.setState({
      requestDrawerVisible: false,
    });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRequestRef(ref = {}) {
    this.requestForm = (ref.props || {}).form;
  }

  @Bind()
  handleSearchDetail(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'reportQuery/fetchRequestDetail',
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
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchReportLoading,
      reportQuery: {
        list = [],
        pagination,
        reportTypeList = [],
        request = {},
        requestStatusList = [],
      },
      tenantRoleLevel,
      fetchRequestListLoading,
      fetchRequestDetailLoading,
    } = this.props;
    const { requestDrawerVisible, tableLoading } = this.state;
    const filterProps = {
      reportTypeList,
      tenantRoleLevel,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      tenantRoleLevel,
      loading: fetchReportLoading,
      dataSource: list,
      onChange: this.handleSearch,
      individualReport: this.onIndividualReport,
    };
    const requestDrawerProps = {
      data: request,
      tenantRoleLevel,
      requestStatusList,
      from: this.requestForm,
      tableLoading,
      fetchRequestListLoading,
      fetchRequestDetailLoading,
      visible: requestDrawerVisible,
      onExport: this.handleExport,
      onOk: this.closeRequestModal,
      onCancel: this.closeRequestModal,
      onSearch: this.handleRequestSearch,
      onBindRef: this.handleBindRequestRef,
      onSearchDetail: this.handleSearchDetail,
    };
    return (
      <>
        <Header title={intl.get('hrpt.reportQuery.view.message.title').d('报表查询')}>
          <Button icon="user" onClick={this.onIndividualReport}>
            {intl.get('hrpt.reportQuery.button.view.individualReport').d('个人报表请求')}
          </Button>
        </Header>
        <Content>
          <SearchForm {...filterProps} />
          <ListTable {...listProps} />
          {requestDrawerVisible && <RequestDrawer {...requestDrawerProps} />}
        </Content>
      </>
    );
  }
}
