/**
 * PlatformManager - 平台管理员
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
import { getCurrentOrganizationId, filterNullValueObject } from 'utils/utils';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import ClearLogsDrawer from './ClearLogsDrawer';

@connect(({ platformManager, loading }) => ({
  platformManager,
  clearLogLoading: loading.effects['platformManager/clearLogs'],
  loading: loading.effects['platformManager/fetchMembers'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hpfm.login'] })
export default class PlatformManager extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      clearLogsVisible: false, // 清除日志模态框是否可见
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    const lovCodes = { typeList: 'HPFM.LOGIN_AUDIT_TYPE' };
    dispatch({
      type: 'platformManager/init',
      payload: {
        lovCodes,
      },
    });
    const type = 'platformManager/fetchMembers';
    dispatch({ type });
  }

  /**
   * form元素绑定
   * @param {*} [ref={}]
   * @memberof PlatformManager
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 条件查询及表格翻页
   * @param {*} [fields={}]
   * @memberof PlatformManager
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const filterValues = this.handleGetFormValue();
    dispatch({
      type: 'platformManager/fetchMembers',
      payload: {
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
      type: 'platformManager/clearLogs',
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
      match: { path },
      clearLogLoading = false,
      platformManager: { list = [], pagination = {}, typeList = [], clearTypeList = [] },
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
        <Header title={intl.get('hpfm.login.view.message.title').d('用户登录日志')}>
          <ExcelExport
            exportAsync
            requestUrl="/hpfm/v1/audit-logins/export"
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
