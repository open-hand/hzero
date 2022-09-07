/**
 * Concurrent - 并发管理器/请求定义
 * @date: 2018-9-10
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import moment from 'moment';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel, filterNullValueObject } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import FilterForm from './FilterForm';
import ListTable from './ListTable';
import PermissionAssign from './PermissionAssign';

/**
 * 请求定义
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} Concurrent - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({
  code: ['hsdr.concurrent', 'entity.tenant', 'hsdr.concPermission', 'hrpt.concPermission'],
})
@connect(({ concurrent, loading }) => ({
  concurrent,
  fetchList: loading.effects['concurrent/fetchConcurrentList'],
  fetchAssignedLoading: loading.effects['concurrent/fetchAssignedPermission'],
  createPermissionLoading: loading.effects['concurrent/createPermission'],
  updatePermissionLoading: loading.effects['concurrent/updatePermission'],
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
export default class List extends Component {
  form;

  /**
   * state初始化
   */
  state = {
    permissionVisible: false,
    currentConcurrentData: {},
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      concurrent: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
  }

  /**
   * 传递表单对象
   * @param {object} ref - FilterForm对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, currentTenantId } = this.props;
    const filterValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'concurrent/fetchConcurrentList',
      payload: {
        currentTenantId,
        page: isEmpty(fields) ? {} : fields,
        ...filterValues,
      },
    });
  }

  /**
   * 新增，跳转到明细页面
   */
  @Bind()
  handleAddConcurrent() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hsdr/concurrent/detail/create`,
      })
    );
  }

  /**
   * 数据列表，头编辑
   *@param {obejct} record - 操作对象
   */
  @Bind()
  handleEditContent(record) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hsdr/concurrent/detail/${record.concurrentId}`,
      })
    );
  }

  /**
   * @function handleUpdateEmail - 编辑
   * @param {object} record - 行数据
   */
  @Bind()
  showPermissionModal(record) {
    this.setState({
      permissionVisible: true,
      currentConcurrentData: record,
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
        currentConcurrentData: {},
      },
      () => {
        this.props.dispatch({
          type: 'concurrent/updateState',
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
    const { currentConcurrentData = {} } = this.state;
    dispatch({
      type: 'concurrent/fetchAssignedPermission',
      payload: {
        concurrentId: currentConcurrentData.concurrentId,
        ...params,
      },
    });
  }

  // 创建权限
  @Bind()
  handlePermissionSave(values = {}) {
    const { dispatch, currentTenantId, tenantRoleLevel } = this.props;
    const { currentConcurrentData } = this.state;
    const { startDate, endDate, tenantId, roleName, tenantName, ...others } = values;
    return dispatch({
      type: 'concurrent/createPermission', // 新增逻辑
      payload: {
        startDate: startDate ? moment(startDate).format(DEFAULT_DATE_FORMAT) : null,
        endDate: endDate ? moment(endDate).format(DEFAULT_DATE_FORMAT) : null,
        concurrentId: currentConcurrentData.concurrentId,
        tenantId: tenantRoleLevel ? currentTenantId : tenantId,
        ...others,
      },
    });
  }

  // 编辑权限
  @Bind()
  handlePermissionEdit(values = {}) {
    const { dispatch, currentTenantId, tenantRoleLevel } = this.props;
    const { currentConcurrentData } = this.state;
    const { startDate, endDate, tenantId, roleName, tenantName, ...others } = values;
    return dispatch({
      type: 'concurrent/updatePermission',
      payload: {
        startDate: startDate ? moment(startDate).format(DEFAULT_DATE_FORMAT) : null,
        endDate: endDate ? moment(endDate).format(DEFAULT_DATE_FORMAT) : null,
        concurrentId: currentConcurrentData.concurrentId,
        tenantId: tenantRoleLevel ? currentTenantId : tenantId,
        ...others,
      },
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
      fetchList,
      match: { path },
      concurrent: { list = [], pagination, permissionsList = [], permissionsPagination },
      fetchAssignedLoading,
      createPermissionLoading,
      updatePermissionLoading,
    } = this.props;
    const { permissionVisible = false, currentConcurrentData = {} } = this.state;
    const filterProps = {
      tenantId: currentTenantId,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      path,
      loading: fetchList,
      dataSource: list,
      editContent: this.handleEditContent,
      onChange: this.handleSearch,
      onAssign: this.showPermissionModal,
    };
    const permissionsProps = {
      path,
      currentConcurrentData,
      permissionsList,
      permissionsPagination,
      tenantRoleLevel,
      currentTenantId,
      visible: permissionVisible,
      fetchListLoading: fetchAssignedLoading,
      createLoading: createPermissionLoading,
      updateLoading: updatePermissionLoading,
      onSearch: this.fetchAssignedPermission,
      onOk: this.hiddenPermissionModal,
      onCancel: this.hiddenPermissionModal,
      onSave: this.handlePermissionSave,
      onEdit: this.handlePermissionEdit,
    };
    return (
      <>
        <Header title={intl.get('hsdr.concurrent.view.message.title').d('请求定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '请求定义-新建',
              },
            ]}
            onClick={this.handleAddConcurrent}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
          {permissionVisible && <PermissionAssign {...permissionsProps} />}
        </Content>
      </>
    );
  }
}
