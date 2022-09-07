/**
 * TenantManager - 租户管理员
 * @date: 2019-01-10
 * @author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import ExcelExport from 'components/ExcelExport';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { HZERO_PLATFORM } from 'utils/config';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import ClearLogsDrawer from './ClearLogsDrawer';

@connect(({ tenantManager, loading }) => ({
  tenantManager,
  loading: loading.effects['tenantManager/fetchMembers'],
  clearLogLoading: loading.effects['tenantManager/clearLogs'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hpfm.login'] })
export default class tenantManager extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      clearLogsVisible: false, // 清除日志模态框是否可见
    };
  }

  componentDidMount() {
    const { dispatch, tenantId } = this.props;
    const type = 'tenantManager/fetchMembers';
    const lovCodes = { typeList: 'HPFM.LOGIN_AUDIT_TYPE' };
    dispatch({
      type: 'tenantManager/init',
      payload: {
        lovCodes,
      },
    });
    dispatch({
      type,
      payload: {
        tenantId,
      },
    });
  }

  /**
   * 表单元素绑定
   * @param {*} [ref={}]
   * @memberof tenantManager
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 条件查询及表格翻页
   * @param {*} [fields={}]
   * @memberof tenantManager
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, tenantId } = this.props;
    const filterValues = this.handleGetFormValue();
    dispatch({
      type: 'tenantManager/fetchMembers',
      payload: {
        tenantId,
        page: isEmpty(fields) ? {} : fields,
        ...filterValues,
      },
    });
  }

  /**
   * 获取form数据
   */
  @Bind()
  handleGetFormValue() {
    const formValue = isUndefined(this.form) ? {} : this.form.getFieldsValue();
    const values = {
      ...formValue,
      loginDateAfter:
        formValue.loginDateAfter && formValue.loginDateAfter.format(DEFAULT_DATETIME_FORMAT),
      loginDateBefore:
        formValue.loginDateBefore && formValue.loginDateBefore.format(DEFAULT_DATETIME_FORMAT),
    };
    const filterValues = filterNullValueObject(values);
    return filterValues;
  }

  /**
   * 日志清理
   * @param {object} fieldsValue - 请求参数
   */
  @Bind()
  handleClearLogs(fieldsValue) {
    const { dispatch } = this.props;
    dispatch({
      type: 'tenantManager/clearLogs',
      payload: fieldsValue,
    }).then(res => {
      if (res) {
        notification.success();
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

  render() {
    const {
      loading,
      tenantId,
      match: { path },
      clearLogLoading = false,
      tenantManager: { list = [], pagination = {}, typeList = [], clearTypeList = [] },
    } = this.props;
    const { clearLogsVisible } = this.state;
    const filterProps = {
      onRef: this.handleBindRef,
      onSearch: this.handleSearch,
      typeList,
    };
    const listProps = {
      loading,
      pagination,
      dataSource: list,
      onChange: this.handleSearch,
    };
    return (
      <React.Fragment>
        <Header title={intl.get(`hpfm.login.audit.view.title.userLoginLog`).d('用户登录日志')}>
          <ExcelExport
            exportAsync
            requestUrl={`${HZERO_PLATFORM}/v1/${tenantId}/audit-logins/export`}
            otherButtonProps={{ type: 'primary' }}
            queryParams={this.handleGetFormValue()}
          />
          <ButtonPermission
            icon="delete"
            onClick={() => this.handleClearLogsDrawer(true)}
            permissionList={[
              {
                code: `${path}.button.clearLogs`,
                type: 'button',
                meaning: '登录日志-日志清理',
              },
            ]}
          >
            {intl.get('hpfm.login.view.button.clearLogs').d('日志清理')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
          <ClearLogsDrawer
            title={intl.get('hpfm.login.view.button.clearLogs').d('日志清理')}
            loading={clearLogLoading}
            modalVisible={clearLogsVisible}
            clearTypeList={clearTypeList}
            onCancel={() => this.handleClearLogsDrawer(false)}
            onOk={this.handleClearLogs}
          />
        </Content>
      </React.Fragment>
    );
  }
}
