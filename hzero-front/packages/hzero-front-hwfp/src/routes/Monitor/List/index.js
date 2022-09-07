/**
 * List - 流程监控
 * @date: 2018-8-20
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import moment from 'moment';

import { Header, Content } from 'components/Page';

import { openTab } from 'utils/menuTab';
import { getCurrentOrganizationId, filterNullValueObject, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { DATETIME_MIN, DATETIME_MAX } from 'utils/constants';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import EmployeeDrawer from './EmployeeDrawer';
import ExceptionMsgDrawer from './ExceptionMsgDrawer';
import JumpNodeDrawer from './JumpNodeDrawer';
import LogDrawer from './LogDrawer';

/**
 * 流程监控组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} monitor - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ monitor, loading }) => ({
  monitor,
  fetchListLoading: loading.effects['monitor/fetchMonitorList'],
  fetchEmployeeLoading: loading.effects['monitor/fetchEmployeeList'],
  employeeLoading: loading.effects['monitor/retryProcess'],
  jumpLoading: loading.effects['monitor/jumpProcess'],
  fetchNodeLoading: loading.effects['monitor/fetchValidNode'],
  logLoading: loading.effects['monitor/fetchProcessException'],
  tenantId: getCurrentOrganizationId(),
  isSiteFlag: !isTenantRoleLevel(),
}))
@formatterCollections({
  code: [
    'hwfp.monitor',
    'entity.tenant',
    'entity.position',
    'entity.department',
    'entity.employee',
    'hwfp.common',
  ],
})
export default class List extends Component {
  form;

  state = {
    drawerVisible: false, // 选择员工弹窗
    exceptionVisible: false, // 挂起详情弹窗
    jumpNodeVisible: false, // 挂起详情弹窗
    operationRecord: {},
    validNodeList: [], // 有效节点
    jumpSelected: {},
    exceptionLogVisible: false, // 异常查看标记
    retryData: {},
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      monitor: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.props.dispatch({ type: 'monitor/queryProcessStatus' });
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
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());

    const {
      processInstanceId,
      processDefinitionNameLike,
      startedBy,
      startedBefore,
      startedAfter,
      finishedBefore,
      finishedAfter,
      ...others
    } = filterValues;
    dispatch({
      type: 'monitor/fetchMonitorList',
      payload: {
        processInstanceId,
        processDefinitionNameLike,
        startedBy,
        startedBefore: startedBefore ? moment(startedBefore).format(DATETIME_MAX) : null,
        startedAfter: startedAfter ? moment(startedAfter).format(DATETIME_MIN) : null,
        finishedBefore: finishedBefore ? moment(finishedBefore).format(DATETIME_MAX) : null,
        finishedAfter: finishedAfter ? moment(finishedAfter).format(DATETIME_MIN) : null,
        page: isEmpty(fields) ? {} : fields,
        ...others,
      },
    });
  }

  @Bind()
  handleToDetail(record) {
    openTab({
      title: record.processName,
      key: `/hwfp/monitor/detail/${record.encryptId}`,
      path: `/hwfp/monitor/detail/${record.encryptId}`,
      icon: 'edit',
      closable: true,
    });
  }

  @Bind()
  handleSuspendedReason(processInstanceId) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'monitor/fetchExceptionDetail',
      payload: {
        tenantId,
        processInstanceId,
      },
    }).then((res) => {
      if (res) {
        // notification.success();
        this.setState({ exceptionVisible: true });
      }
    });
  }

  @Bind()
  handleExceptionCancel() {
    this.setState({
      exceptionVisible: false,
    });
  }

  // 挂起、恢复、终止流程
  @Bind()
  handleOperateProcess(processInstanceId, dispatchType) {
    const {
      dispatch,
      tenantId,
      monitor: { pagination },
    } = this.props;
    dispatch({
      type: `monitor/${dispatchType}`,
      payload: {
        tenantId,
        processInstanceId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  // 指定审批人
  @Bind()
  handleRetry(record) {
    this.setState({
      drawerVisible: true,
      retryData: record,
    });
    this.handleSearchEmployee({
      tenantId: record.tenantId,
      lovCode: 'HPFM.EMPLOYEE',
      enabledFlag: 1,
    });
  }

  // 转交
  @Bind()
  handleDelegate(record) {
    if (record.currentTasks && record.currentTasks.length === 1) {
      this.setState({
        drawerVisible: true,
        operationRecord: record,
      });
    } else {
      this.props.history.push(`/hwfp/delegate`);
    }
  }

  /**
   * 提交指定审批人
   * @param data 任务数据
   */
  @Bind()
  handleAction(data = {}) {
    const {
      dispatch,
      monitor: { pagination },
    } = this.props;
    dispatch({
      type: 'monitor/retryProcess',
      payload: data,
    }).then((res) => {
      if (res) {
        notification.success();
        this.setState({
          drawerVisible: false,
        });
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  handleCancel() {
    this.setState({
      drawerVisible: false,
    });
  }

  // 跳转节点TODO
  @Bind()
  handleJumpNode(record) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'monitor/fetchValidNode',
      payload: {
        tenantId,
        processInstanceId: record.id,
      },
    }).then((res) => {
      if (res) {
        this.setState({
          jumpNodeVisible: true,
          operationRecord: record,
          validNodeList: res,
          jumpSelected: {},
        });
      }
    });
  }

  @Bind()
  handleExceptionLog(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'monitor/fetchProcessException',
      payload: record,
    });
    this.setState({ exceptionLogVisible: true });
  }

  @Bind()
  handleLogCancel() {
    const { dispatch } = this.props;
    dispatch({
      type: 'monitor/updateState',
      payload: { exceptionList: [] },
    });
    this.setState({ exceptionLogVisible: false });
  }

  @Bind()
  handleSelectNode(record) {
    this.setState({
      jumpSelected: record,
    });
  }

  @Bind()
  handleJumpSubmit() {
    const {
      tenantId,
      dispatch,
      monitor: { pagination },
    } = this.props;
    const { jumpSelected, operationRecord } = this.state;
    if (jumpSelected.nodeId) {
      const currentTaskId = operationRecord.currentTasks[0].taskId;
      dispatch({
        type: 'monitor/jumpProcess',
        payload: {
          tenantId,
          action: 'jump',
          currentTaskId,
          jumpTarget: jumpSelected.nodeId,
          jumpTargetName: jumpSelected.name,
          processInstanceId: operationRecord.id,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.setState({
            jumpNodeVisible: false,
          });
          this.handleSearch(pagination);
        }
      });
    } else {
      this.handleJumpCancel();
    }
  }

  @Bind()
  handleJumpCancel() {
    this.setState({
      jumpNodeVisible: false,
    });
  }

  @Bind()
  handleSearchEmployee(data = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'monitor/fetchEmployeeList',
      payload: data,
    });
  }

  render() {
    const {
      fetchListLoading,
      dispatch,
      employeeLoading,
      fetchNodeLoading,
      jumpLoading,
      fetchEmployeeLoading,
      logLoading,
      isSiteFlag,
      monitor: {
        exceptionDetail,
        employeeList,
        employeePagination,
        list = [],
        pagination,
        processStatus = [],
        exceptionList = [],
      },
    } = this.props;
    const {
      drawerVisible,
      retryData,
      exceptionVisible,
      jumpNodeVisible,
      validNodeList,
      exceptionLogVisible,
    } = this.state;
    const filterProps = {
      isSiteFlag,
      processStatus,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      pagination,
      isSiteFlag,
      processStatus,
      dataSource: list,
      loading: fetchListLoading,
      onDetail: this.handleToDetail, // 流程详情
      onSuspendedReason: this.handleSuspendedReason, // 挂起详情
      onStop: this.handleOperateProcess, // 终止流程
      onSuspend: this.handleOperateProcess, // 挂起节点
      onResume: this.handleOperateProcess, // 恢复流程
      onRetry: this.handleRetry, // 指定审批人
      onChange: this.handleSearch,
      onException: this.handleExceptionLog,
    };

    const employeeProps = {
      employeeList,
      pagination: employeePagination,
      drawerVisible,
      dispatch,
      retryData,
      submitLoading: employeeLoading,
      loading: fetchEmployeeLoading,
      onSearch: this.handleSearchEmployee,
      onCancel: this.handleCancel,
      onAction: this.handleAction,
    };

    const exceptionMsgProps = {
      exceptionDetail,
      title: intl.get('hwfp.monitor.model.monitor.exceptionDetail').d('挂起详情'),
      visible: exceptionVisible,
      onCancel: this.handleExceptionCancel,
    };

    const jumpNodeProps = {
      validNodeList,
      fetchNodeLoading,
      jumpLoading,
      title: intl.get('hwfp.monitor.model.monitor.jumpNode').d('跳转节点'),
      visible: jumpNodeVisible,
      onSelectNode: this.handleSelectNode,
      onOk: this.handleJumpSubmit,
      onCancel: this.handleJumpCancel,
    };

    const logDrawerProps = {
      logLoading,
      exceptionList,
      visible: exceptionLogVisible,
      onCancel: this.handleLogCancel,
    };

    return (
      <>
        <Header title={intl.get('hwfp.monitor.view.message.title').d('流程监控')}>
          {/* <Button icon="sync" onClick={() => this.handleSearch()}>
            {intl.get('hzero.common.button.reload').d('刷新')}
          </Button> */}
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />

          <EmployeeDrawer {...employeeProps} />
          <ExceptionMsgDrawer {...exceptionMsgProps} />
          <JumpNodeDrawer {...jumpNodeProps} />
          {exceptionLogVisible && <LogDrawer {...logDrawerProps} />}
        </Content>
      </>
    );
  }
}
