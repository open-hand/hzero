/**
 * index - 接口平台-应用配置
 * @date: 2018-7-26
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';

import { Header, Content } from 'components/Page';

import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import Search from './Search';
import List from './List';
import Editor from './Editor';

import styles from './index.less';

@connect(({ loading, application }) => ({
  loading,
  application,
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@formatterCollections({ code: 'hitf.application' })
export default class Role extends PureComponent {
  constructor(props) {
    super(props);
    this.fetchList = this.fetchList.bind(this);
  }

  componentDidMount() {
    this.fetchList();
    this.fetchAuthorizationTypeCode();
  }

  state = {
    editorVisible: false,
    editingRow: {},
    processingRow: null,
  };

  /**
   * fetchList - 获取列表数据
   * @param {Object} payload - 查询参数
   */
  fetchList(params) {
    const { dispatch } = this.props;
    dispatch({ type: 'application/queryList', params });
  }

  fetchDetail(applicationId) {
    const { dispatch } = this.props;
    return dispatch({ type: 'application/queryDetail', applicationId });
  }

  /**
   * 删除行
   * @param {Array} assignIds
   */
  handleDeleteLines(assignIds) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'application/deleteLines',
      assignIds,
    });
  }

  /**
   * fetchAuthorizationTypeCode - 查询授权模式<HITF.GRANT_TYPE>code
   * @return {Array}
   */
  fetchAuthorizationTypeCode() {
    const { dispatch } = this.props;
    return dispatch({ type: 'application/queryCode', payload: { lovCode: 'HITF.GRANT_TYPE' } });
  }

  create(data, cb = (e) => e) {
    const { dispatch, application = {} } = this.props;
    const { list = {} } = application;
    dispatch({ type: 'application/create', data }).then((res) => {
      if (res) {
        this.fetchList({ page: list.pagination });
        notification.success();
        cb();
      }
    });
  }

  save(data, cb = (e) => e) {
    const { dispatch, application = {} } = this.props;
    const { list = {} } = application;
    dispatch({ type: 'application/save', data }).then((res) => {
      if (res) {
        this.fetchList({ page: list.pagination });
        notification.success();
        cb();
      }
    });
  }

  deleteRow(data = [], cb = (e) => e) {
    const { dispatch = (e) => e, application = {} } = this.props;
    const { list = {} } = application;
    this.setState({
      processingRow: (data[0] || {}).applicationId,
    });
    dispatch({ type: 'application/deleteApplication', data }).then((res) => {
      if (res && res.failed) {
        notification.error({ description: res.message });
      } else {
        this.fetchList({ page: list.pagination });
        notification.success();
        cb();
      }
      this.setState({
        processingRow: null,
      });
    });
  }

  onTableChange(pagination) {
    const { getFieldsValue = (e) => e } = this.search;
    this.fetchList({ page: pagination, ...getFieldsValue() });
  }

  openEditor(editingRow = {}) {
    this.setState({
      editorVisible: true,
      editingRow,
    });
  }

  closeEditor(cb = (e) => e) {
    this.setState(
      {
        editorVisible: false,
        editingRow: {},
      },
      () => {
        cb();
      }
    );
  }

  render() {
    const { application = {}, loading = {}, currentTenantId, tenantRoleLevel } = this.props;
    const { list = {} } = application;
    const { editorVisible, editingRow, processingRow } = this.state;
    const { effects } = loading;
    const searchProps = {
      fetchList: this.fetchList,
      currentTenantId,
      tenantRoleLevel,
      pagination: list.pagination,
    };
    const listProps = {
      loading: effects['application/queryList'],
      onChange: this.onTableChange.bind(this),
      openEditor: this.openEditor.bind(this),
      dataSource: list.dataSource,
      pagination: list.pagination,
      deleteRow: this.deleteRow.bind(this),
      processingRow,
      tenantRoleLevel,
    };

    const editorProps = {
      currentTenantId,
      tenantRoleLevel,
      visible: editorVisible,
      applicationId: editingRow.applicationId,
      onCancel: this.closeEditor.bind(this),
      processing: {
        queryDetail: effects['application/queryDetail'],
        save: effects['application/save'],
        create: effects['application/create'],
      },
      save: this.save.bind(this),
      create: this.create.bind(this),
      fetchDetail: this.fetchDetail.bind(this),
      deleteLines: this.handleDeleteLines.bind(this),
    };

    return (
      <div className={styles['hitf-application']}>
        <Header title={intl.get('hitf.application.view.message.title.header').d('应用管理')}>
          <Button
            type="primary"
            icon="plus"
            onClick={this.openEditor.bind(this, { applicationId: 'create' })}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <Search
            ref={(node) => {
              this.search = node;
            }}
            {...searchProps}
          />
          <List {...listProps} />
        </Content>
        <Editor {...editorProps} />
      </div>
    );
  }
}
