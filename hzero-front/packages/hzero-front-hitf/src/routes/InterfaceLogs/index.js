/*
 * index - 接口监控页面
 * @date: 2018/09/17 15:40:00
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { isUndefined, isEmpty } from 'lodash';
import { Modal } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import List from './List';
import Search from './Search';
import ClearLogsDrawer from './ClearLogsDrawer';

@connect(({ loading, interfaceLogs }) => ({
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
  clearLogLoading: loading.effects['platformManager/clearLogs'],
  fetchLogsListLoading: loading.effects['interfaceLogs/fetchLogsList'],
  interfaceLogs,
}))
@formatterCollections({ code: ['hitf.interfaceLogs'] })
export default class InterfaceLogs extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      loadingMap: {},
      clearLogsVisible: false, // 清除日志模态框是否可见
    };
  }

  componentDidMount() {
    const {
      location: { state: { _back } = {} },
      interfaceLogs: { pagination = {} },
      dispatch,
    } = this.props;

    dispatch({
      type: 'interfaceLogs/init',
    });

    if (_back === -1) {
      this.handleSearch(pagination);
    } else {
      this.handleSearch();
    }
  }

  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.formDom)
      ? {}
      : filterNullValueObject(this.formDom.getFieldsValue());
    const { requestTimeStart, requestTimeEnd } = filterValues;
    dispatch({
      type: 'interfaceLogs/fetchLogsList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...filterValues,
        requestTimeStart: requestTimeStart
          ? requestTimeStart.format(DEFAULT_DATETIME_FORMAT)
          : undefined,
        requestTimeEnd: requestTimeEnd ? requestTimeEnd.format(DEFAULT_DATETIME_FORMAT) : undefined,
      },
    });
  }

  @Bind()
  handleStandardTableChange(pagination = {}) {
    this.handleSearch(pagination);
  }

  /**
   * 日志清理
   * @param {object} fieldsValue - 请求参数
   */
  @Bind()
  handleClearLogs(fieldsValue) {
    const { dispatch } = this.props;
    dispatch({
      type: 'interfaceLogs/clearLogs',
      payload: fieldsValue,
    }).then((res) => {
      if (res) {
        notification.success({
          message: intl
            .get('hitf.interfaceLogs.model.traceLog.clearLogSuccess')
            .d('操作成功，日志清理将执行后台操作，请重新执行查询或刷新列表操作查看最新进度'),
        });
        this.handleClearLogsDrawer(false);
        this.handleSearch();
      }
    });
  }

  /**
   *是否打开清除日志模态框
   *
   * @param {boolean} flag
   */
  handleClearLogsDrawer(flag) {
    this.setState({
      clearLogsVisible: flag,
    });
  }

  /**
   * 重试
   */
  @Bind()
  async handleRetry(params) {
    // {intl.get(`hitf.interfaceLogs.view.message.retryConfirm`).d('确认重试吗')}
    const confirm = await Modal.confirm({
      children: (
        <p>
          {`${intl.get('hitf.interfaceLogs.view.message.lastest').d('当前接口最新版本为')}${
            params.formatInterfaceVersion
          },${intl.get('hitf.interfaceLogs.view.message.retryConfirm').d('是否进行重试')}?`}
        </p>
      ),
    });
    if (confirm === 'ok') {
      const {
        interfaceLogId,
        invokeType,
        clearType,
        interfaceCode,
        interfaceName,
        serverName,
        requestTimeStart,
        requestTimeEnd,
        tenantId,
        clientId,
        applicationCode,
        invokeKey,
        serverCode,
        responseStatus,
        interfaceRequestTimeStart,
        interfaceRequestTimeEnd,
      } = params;
      this.setState({
        loadingMap: {
          [interfaceLogId]: true,
        },
      });
      this.props
        .dispatch({
          type: 'interfaceLogs/retry',
          payload: {
            interfaceLogId,
            invokeType,
            clearType,
            interfaceCode,
            interfaceName,
            serverName,
            requestTimeStart,
            requestTimeEnd,
            tenantId,
            clientId,
            applicationCode,
            invokeKey,
            serverCode,
            responseStatus,
            interfaceRequestTimeStart,
            interfaceRequestTimeEnd,
            organizationId: tenantId,
          },
        })
        .then((res) => {
          this.setState({
            loadingMap: {
              [interfaceLogId]: false,
            },
          });
          if (!isUndefined(res)) {
            notification.success();
            this.handleSearch();
          }
        });
    }
  }

  render() {
    const {
      match: { path },
      clearLogLoading = false,
      interfaceLogs: { dataList = [], pagination = {}, clearTypeList = [] },
      history,
      fetchLogsListLoading,
      location,
      tenantRoleLevel,
      currentTenantId,
    } = this.props;
    const { clearLogsVisible, loadingMap } = this.state;
    const listProps = {
      history,
      location,
      pagination,
      loadingMap,
      dataSource: dataList,
      loading: fetchLogsListLoading,
      searchPaging: this.handleStandardTableChange,
      onRetry: this.handleRetry,
    };
    const searchProps = {
      onRef: (node) => {
        this.formDom = node.props.form;
      },
      onFilterChange: this.handleSearch,
    };
    return (
      <>
        <Header title={intl.get('hitf.interfaceLogs.view.message.title').d('接口监控')}>
          <ButtonPermission
            icon="delete"
            type="c7n-pro"
            color="primary"
            onClick={() => this.handleClearLogsDrawer(true)}
            permissionList={[
              {
                code: `${path}.button.clearLogs`,
                type: 'button',
                meaning: '接口监控-日志清理',
              },
            ]}
          >
            {intl.get('hitf.interfaceLogs.view.button.clearLogs').d('日志清理')}
          </ButtonPermission>
        </Header>
        <Content>
          <Search {...searchProps} />
          <List {...listProps} />
          <ClearLogsDrawer
            title={intl.get('hitf.interfaceLogs.view.button.clearLogs').d('日志清理')}
            loading={clearLogLoading}
            modalVisible={clearLogsVisible}
            clearTypeList={clearTypeList}
            onCancel={() => this.handleClearLogsDrawer(false)}
            onOk={this.handleClearLogs}
            tenantRoleLevel={tenantRoleLevel}
            currentTenantId={currentTenantId}
          />
        </Content>
      </>
    );
  }
}
