/**
 * index - 接口平台-应用配置
 * @date: 2018-7-26
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Spin, Button, Modal } from 'hzero-ui';
import { Modal as C7NModal } from 'choerodon-ui/pro';
import { connect } from 'dva';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Header, Content } from 'components/Page';

import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import getLang from '@/langs/interfacesLang';
import List from './List';
import Form from './Form';
import AuthModal from '../AuthModal';
import styles from '../index.less';

const listRowKey = 'interfaceAuthId';

@connect(({ loading, interfaces }) => ({
  queryListLoading: loading.effects['interfaces/queryAuthSelfList'],
  queryDetailLoading: loading.effects['interfaces/queryDetail'],
  interfaces,
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@formatterCollections({ code: 'hitf.interfaces' })
export default class AuthConfig extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      pagination: {},
      formDataSource: {},
      activeRowData: {},
    };
  }

  componentDidMount() {
    this.fetchDetail().then((res = {}) => {
      const { authFlag } = res;
      this.fetchList({ authFlag });
    });
  }

  /**
   * fetchList - 获取列表数据
   * @param {Object} payload - 查询参数
   */
  @Bind()
  fetchList(params) {
    const { dispatch = () => {}, match = {} } = this.props;
    const param = (match.params || {}).interfaceId;
    const interfaceId = param.indexOf('?') === -1 ? param : param.substring(0, param.indexOf('?'));
    return dispatch({
      type: 'interfaces/queryAuthSelfList',
      interfaceId,
      params,
    }).then((res = {}) => {
      const { dataSource, pagination } = res;
      this.setState({
        dataSource,
        pagination,
        listSelectedRows: [],
      });
    });
  }

  /**
   * fetchList - 获取列表数据
   * @param {Object} payload - 查询参数
   */
  @Bind()
  fetchDetail() {
    const { dispatch = () => {}, match = {} } = this.props;
    const param = (match.params || {}).interfaceId;
    const interfaceId = param.indexOf('?') === -1 ? param : param.substring(0, param.indexOf('?'));
    return dispatch({ type: 'interfaces/queryDetail', interfaceId }).then((res = {}) => {
      this.setState({
        formDataSource: res,
      });
      return res;
    });
  }

  /**
   * 查询接口详情
   * @param {Object} params
   */
  @Bind()
  createAuthSelf(data, cb = () => {}) {
    const { dispatch = () => {}, match = {} } = this.props;
    const param = (match.params || {}).interfaceId;
    const interfaceId = param.indexOf('?') === -1 ? param : param.substring(0, param.indexOf('?'));
    return dispatch({
      type: 'interfaces/createAuthSelf',
      interfaceId,
      data,
    }).then((res) => {
      if (res && !res.failed) {
        cb();
        this.fetchList();
        notification.success();
      } else {
        notification.error({ description: res.message });
      }
      return res;
    });
  }

  /**
   * 查询接口详情
   * @param {Object} params
   */
  @Bind()
  updateAuthSelf(data, cb = () => {}) {
    const { tenantRoleLevel, currentTenantId, dispatch = () => {}, match = {} } = this.props;
    const { activeRowData } = this.state;
    const param = (match.params || {}).interfaceId;
    const interfaceId = param.indexOf('?') === -1 ? param : param.substring(0, param.indexOf('?'));
    const updatedData = {
      interfaceId,
      tenantId: tenantRoleLevel ? currentTenantId : activeRowData.tenantId,
      ...data,
    };
    return dispatch({
      type: 'interfaces/updateAuthSelf',
      interfaceId,
      data: updatedData,
    }).then((res) => {
      if (res && !res.failed) {
        cb();
        this.fetchList();
        notification.success();
      } else {
        notification.error({ description: res.message });
      }
      return res;
    });
  }

  @Bind()
  deleteRows() {
    const { dispatch = () => {}, match = {} } = this.props;
    const { listSelectedRows = [] } = this.state;
    const param = (match.params || {}).interfaceId;
    const interfaceId = param.indexOf('?') === -1 ? param : param.substring(0, param.indexOf('?'));
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        dispatch({
          type: 'interfaces/deleteAuthSelf',
          interfaceId,
          data: listSelectedRows,
        }).then((res) => {
          if (res && !res.failed) {
            this.fetchList();
            notification.success();
          } else {
            notification.error({ description: res.message });
          }
        });
      },
    });
  }

  @Bind()
  onTableChange(page) {
    this.fetchList({ page });
  }

  @Bind()
  openEditor(activeRowData = {}) {
    const { match = {} } = this.props;
    const { formDataSource = {} } = this.state;
    const { authFlag } = formDataSource;
    const param = (match.params || {}).interfaceId;
    const interfaceId = param.indexOf('?') === -1 ? param : param.substring(0, param.indexOf('?'));
    const { interfaceAuthId } = activeRowData;
    const modalProps = {
      interfaceId,
      interfaceAuthId,
      authFlag,
      isNew: false,
      onOk: this.updateAuthSelf,
    };
    this.authModal = C7NModal.open({
      title: getLang('EDIT_AUTH'),
      destroyOnClose: true,
      closable: true,
      style: { width: '60%' },
      okText: getLang('SAVE'),
      className: styles['calc-height-modal'],
      children: <AuthModal {...modalProps} />,
    });
    this.setState({
      activeRowData,
    });
  }

  @Bind()
  add() {
    const { formDataSource = {} } = this.state;
    const { authFlag } = formDataSource;
    const modalProps = {
      authFlag,
      isNew: true,
      onOk: this.createAuthSelf,
    };
    this.authModal = C7NModal.open({
      title: getLang('CREATE_AUTH'),
      destroyOnClose: true,
      closable: true,
      style: { width: '60%' },
      okText: getLang('SAVE'),
      className: styles['calc-height-modal'],
      children: <AuthModal {...modalProps} />,
    });
  }

  @Bind()
  onRowSelectionChange(selectedRowKeys, listSelectedRows) {
    this.setState({
      listSelectedRows,
    });
  }

  render() {
    const {
      location: { search },
      queryListLoading,
      tenantRoleLevel,
      queryDetailLoading,
      currentTenantId,
      match = {},
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const { dataSource, pagination, formDataSource = {}, listSelectedRows = [] } = this.state;

    const formProps = {
      dataSource: formDataSource,
    };

    const listProps = {
      loading: queryListLoading,
      onChange: this.onTableChange,
      dataSource,
      pagination,
      tenantRoleLevel,
      openEditor: this.openEditor,
      listRowKey,
      selectedRowKeys: listSelectedRows.map((o) => o[listRowKey]),
      onRowSelectionChange: this.onRowSelectionChange,
      currentTenantId,
    };

    return (
      <>
        <Header
          title={intl.get('hitf.interfaces.view.message.title.authConfigHeader').d('认证配置')}
          backPath={
            match.path.indexOf('/private') === 0
              ? `/private/hitf/interfaces/list?access_token=${accessToken}`
              : '/hitf/interfaces/list'
          }
        />
        <Content>
          <Spin spinning={queryDetailLoading}>
            <Form {...formProps} />
          </Spin>
          <div style={{ float: 'right', marginBottom: 16, clear: 'both' }}>
            <Button onClick={this.add}>{intl.get('hzero.common.button.add').d('新增')}</Button>
            <Button
              disabled={isEmpty(listSelectedRows)}
              onClick={this.deleteRows}
              style={{ marginLeft: 8 }}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </Button>
          </div>
          <div style={{ clear: 'both' }} />
          <List {...listProps} />
        </Content>
      </>
    );
  }
}
