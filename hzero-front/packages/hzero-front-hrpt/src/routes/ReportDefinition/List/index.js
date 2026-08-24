/**
 * reportDefinition - 报表平台/报表定义
 * @date: 2018-11-22
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import moment from 'moment';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { openTab, closeTab } from 'utils/menuTab';

import ListTable from './ListTable';
import PermissionAssign from './PermissionAssign';
import SearchForm from './SearchForm';
import RouteDrawer from './RouteDrawer';

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
@connect(({ reportDefinition, user, loading }) => ({
  user,
  reportDefinition,
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
  fetchReportDefListLoading: loading.effects['reportDefinition/fetchReportDefList'],
  fetchAssignedLoading: loading.effects['reportDefinition/fetchAssignedPermission'],
  fetchPermissionDetailLoading: loading.effects['reportDefinition/fetchPermissionDetail'],
  createPermissionLoading: loading.effects['reportDefinition/createPermission'],
  updatePermissionLoading: loading.effects['reportDefinition/updatePermission'],
  deletePermissionLoading: loading.effects['reportDefinition/deletePermission'],
}))
@formatterCollections({
  code: ['hrpt.reportDefinition', 'entity.tenant', 'hiam.subAccount', 'hrpt.common'],
})
export default class List extends Component {
  form;

  /**
   * state初始化
   */
  state = {
    permissionVisible: false,
    currentReportData: {},
    routeVisible: false, // 菜单路由模态框是否可见
    routeContent: '', // 路由内容
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      reportDefinition: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.props.dispatch({ type: 'reportDefinition/fetchReportTypeCode' });
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
      type: 'reportDefinition/fetchReportDefList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 新增，跳转到明细页面
   */
  @Bind()
  handleAddDefinition() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hrpt/report-definition/create`,
      })
    );
  }

  /**
   * 数据列表，行删除
   * @param {obejct} record - 操作对象
   */
  @Bind()
  handleDeleteContent(record) {
    const {
      dispatch,
      reportDefinition: { pagination },
    } = this.props;
    dispatch({
      type: 'reportDefinition/deleteReportDefinition',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  handleCopy({ reportId }) {
    const { dispatch } = this.props;
    dispatch({
      type: 'reportDefinition/copyReportDefinition',
      payload: {
        reportId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  /**
   * 数据列表，行编辑
   *@param {obejct} record - 操作对象
   */
  @Bind()
  handleEditContent(record) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hrpt/report-definition/detail/${record.reportId}`,
      })
    );
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
   * @function handleUpdateEmail - 编辑
   * @param {object} record - 行数据
   */
  @Bind()
  showPermissionModal(record) {
    this.setState({
      permissionVisible: true,
      currentReportData: record,
    });
  }

  /**
   * 关闭权限分配模态框
   */
  @Bind()
  hiddenPermissionModal() {
    this.setState(
      {
        permissionVisible: false,
        currentReportData: {},
      },
      () => {
        this.props.dispatch({
          type: 'reportDefinition/updateState',
          payload: {
            permissionsList: [],
          },
        });
      }
    );
  }

  // 查询已分配的权限
  @Bind()
  fetchAssignedPermission(params = {}) {
    const { dispatch } = this.props;
    const { currentReportData = {} } = this.state;
    dispatch({
      type: 'reportDefinition/fetchAssignedPermission',
      payload: {
        reportId: currentReportData.reportId,
        ...params,
      },
    });
  }

  // 创建权限
  @Bind()
  handlePermissionSave(values = {}) {
    const { dispatch, currentTenantId, tenantRoleLevel } = this.props;
    const { currentReportData } = this.state;
    const { startDate, endDate, tenantId, roleName, tenantName, ...others } = values;
    return dispatch({
      type: 'reportDefinition/createPermission', // 新增逻辑
      payload: {
        startDate: startDate ? moment(startDate).format(DEFAULT_DATE_FORMAT) : null,
        endDate: endDate ? moment(endDate).format(DEFAULT_DATE_FORMAT) : null,
        reportId: currentReportData.reportId,
        tenantId: tenantRoleLevel ? currentTenantId : tenantId,
        ...others,
      },
    });
  }

  // 编辑权限
  @Bind()
  handlePermissionEdit(values = {}) {
    const { dispatch, currentTenantId, tenantRoleLevel } = this.props;
    const { currentReportData } = this.state;
    const { startDate, endDate, tenantId, roleName, tenantName, ...others } = values;
    return dispatch({
      type: 'reportDefinition/updatePermission',
      payload: {
        startDate: startDate ? moment(startDate).format(DEFAULT_DATE_FORMAT) : null,
        endDate: endDate ? moment(endDate).format(DEFAULT_DATE_FORMAT) : null,
        reportId: currentReportData.reportId,
        tenantId: tenantRoleLevel ? currentTenantId : tenantId,
        ...others,
      },
    });
  }

  // 删除权限
  @Bind()
  handlePermissionDelete(values = {}) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'reportDefinition/deletePermission',
      payload: {
        ...values,
      },
    });
  }

  // 生成菜单路由
  @Bind()
  handleMenuRoute(record = {}) {
    this.setState({
      routeVisible: true,
      routeContent: `/hrpt/report-detail-path/${record.reportCode}?auto=true`,
    });
  }

  // 隐藏菜单路由模态框
  @Bind()
  hiddenRouteModal() {
    this.setState({
      routeContent: '',
      routeVisible: false,
    });
  }

  /**
   * u-report编辑器
   */
  @Bind()
  async handleUReportEditor(record) {
    await closeTab('/hrpt/report-definition/u-report');
    openTab({
      key: `/hrpt/report-definition/u-report`,
      search: { id: record.reportUuid },
      title: 'hzero.common.title.reportEditor',
      closable: true,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      currentTenantId,
      tenantRoleLevel,
      fetchReportDefListLoading,
      fetchAssignedLoading,
      fetchPermissionDetailLoading,
      createPermissionLoading,
      updatePermissionLoading,
      deletePermissionLoading,
      user: {
        currentUser: { tenantId },
      },
      reportDefinition: {
        list = [],
        pagination,
        reportTypeCode = [],
        permissionsList = [],
        permissionsPagination,
      },
      match: { path },
    } = this.props;
    const {
      permissionVisible = false,
      currentReportData = {},
      routeVisible = false,
      routeContent,
    } = this.state;
    const filterProps = {
      reportTypeCode,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      path,
      pagination,
      tenantRoleLevel,
      tenantId,
      loading: fetchReportDefListLoading,
      dataSource: list,
      onCopy: this.handleCopy,
      onEdit: this.handleEditContent,
      onAssign: this.showPermissionModal,
      onDelete: this.handleDeleteContent,
      onChange: this.handleSearch,
      onMenuRoute: this.handleMenuRoute,
      onUReportEditor: this.handleUReportEditor,
    };
    const permissionsProps = {
      currentReportData,
      permissionsList,
      permissionsPagination,
      tenantRoleLevel,
      currentTenantId,
      visible: permissionVisible,
      fetchListLoading: fetchAssignedLoading,
      fetchDetailLoading: fetchPermissionDetailLoading,
      createLoading: createPermissionLoading,
      updateLoading: updatePermissionLoading,
      deleteLoading: deletePermissionLoading,
      onSearch: this.fetchAssignedPermission,
      onOk: this.hiddenPermissionModal,
      onCancel: this.hiddenPermissionModal,
      onSave: this.handlePermissionSave,
      onEdit: this.handlePermissionEdit,
      onDelete: this.handlePermissionDelete,
    };
    const routeProps = {
      visible: routeVisible,
      content: routeContent,
      onCancel: this.hiddenRouteModal,
    };
    return (
      <>
        <Header title={intl.get('hrpt.reportDefinition.view.message.title').d('报表定义')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '报表定义-新建',
              },
            ]}
            type="primary"
            onClick={this.handleAddDefinition}
            icon="plus"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
          {permissionVisible && <PermissionAssign {...permissionsProps} />}
          {routeVisible && <RouteDrawer {...routeProps} />}
        </Content>
      </>
    );
  }
}
