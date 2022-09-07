/**
 * index - 接口平台-应用配置
 * @date: 2018-7-26
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Modal } from 'choerodon-ui/pro';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import queryString from 'query-string';
import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import Search from './Search';
import List from './List';
import AuthModal from './AuthModal';
import getLang from '@/langs/interfacesLang';
import styles from './index.less';

const listRowKey = 'interfaceAuthId';

@connect(({ loading, interfaces }) => ({
  queryListLoading: loading.effects['interfaces/queryList'],
  queryDetailLoading: loading.effects['interfaces/queryDetail'],
  saveLoading: loading.effects['interfaces/save'],
  interfaces,
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@formatterCollections({ code: ['hitf.interfaces', 'hitf.application'] })
export default class Interfaces extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      pagination: {},
      selectedRowKeys: [], // 选中行key集合
      selectedRows: [], // 选中行集合
      // activeRowData: {},
    };
  }

  componentDidMount() {
    this.fetchList();
    this.fetchServiceTypeCode();
    this.fetchYesOrNoCode();
  }

  /**
   * fetchList - 获取列表数据
   * @param {Object} payload - 查询参数
   */
  @Bind()
  fetchList(params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'interfaces/queryList', params }).then((res = {}) => {
      const { dataSource = [], pagination = {} } = res;
      this.setState({
        dataSource: dataSource.map((n) => ({ ...n, key: n[listRowKey] })),
        pagination,
      });
    });
  }

  /**
   * fetchStatisticsLevelCode - 查询授权模式<HITF.GRANT_TYPE>code
   * @return {Array}
   */
  @Bind()
  fetchServiceTypeCode() {
    const { dispatch = () => {} } = this.props;
    return dispatch({ type: 'interfaces/queryCode', payload: { lovCode: 'HITF.SERVICE_TYPE' } });
  }

  /**
   * fetchYesOrNoCode - 查询授权模式<HPFM.FLAG>code
   * @return {Array}
   */
  @Bind()
  fetchYesOrNoCode() {
    const { dispatch = () => {} } = this.props;
    return dispatch({ type: 'interfaces/queryCode', payload: { lovCode: 'HPFM.FLAG' } });
  }

  @Bind()
  onTableChange(pagination) {
    const { getFieldsValue = (e) => e } = this.search;
    this.fetchList({ page: pagination, ...getFieldsValue() });
  }

  /**
   * 新开文档预览窗口
   * @param {object} record - 表格行数据
   */
  @Bind()
  openDocument(record = {}) {
    const { interfaceId } = record;
    window.open(`${process.env.BASE_PATH || '/'}pub/hitf/document-view/${interfaceId}`);
  }

  @Bind()
  openAuthConfig(record = {}) {
    const { interfaceId } = record;
    const {
      dispatch,
      location: { search, pathname },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    dispatch(
      routerRedux.push({
        pathname:
          pathname.indexOf('/private') === 0
            ? `/private/hitf/interfaces/auth-config/${interfaceId}`
            : `/hitf/interfaces/auth-config/${interfaceId}`,
        search: pathname.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
      })
    );
  }

  /**
   * 获取选中行
   * @param {array} selectedRowKeys 选中行的key值集合
   * @param {object} selectedRows 选中行集合
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  /**
   * 开启批量新建弹窗
   */
  @Bind()
  handleOpenModal() {
    const modalProps = {
      isNew: true,
      onOk: this.handleBatchAddAuth,
    };

    this.authModal = Modal.open({
      title: getLang('BATCH_ADD'),
      destroyOnClose: true,
      closable: true,
      style: { width: '60%' },
      okText: getLang('SAVE'),
      className: styles['calc-height-modal'],
      children: <AuthModal {...modalProps} />,
    });
  }

  /**
   * 批量添加授权
   * @param {object} values - 表单值
   */
  @Bind()
  async handleBatchAddAuth(values, cb = () => {}) {
    const { dispatch } = this.props;
    const { selectedRows, pagination } = this.state;
    const nextSelectedRows = selectedRows.map((item) => {
      const { interfaceId, tenantId } = item;
      const nextItem = {
        interfaceId,
        tenantId,
        ...values,
      };
      return nextItem;
    });
    return dispatch({
      type: 'interfaces/batchAddAuth',
      payload: nextSelectedRows,
    }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.setState(
          {
            selectedRowKeys: [],
            selectedRows: [],
          },
          () => {
            this.onTableChange(pagination);
          }
        );
      }
      return res;
    });
  }

  render() {
    const {
      interfaces = {},
      queryListLoading,
      currentTenantId,
      tenantRoleLevel,
      match: { path },
    } = this.props;
    const { code = {} } = interfaces;
    const {
      dataSource,
      pagination,
      selectedRowKeys,
      // activeRowData,
    } = this.state;
    const searchProps = {
      fetchList: this.fetchList,
      currentTenantId,
      tenantRoleLevel,
      pagination,
      serverTypeCode: code['HITF.SERVICE_TYPE'],
      yesOrNoCode: code['HPFM.FLAG'],
    };
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const listProps = {
      path,
      loading: queryListLoading,
      onChange: this.onTableChange,
      dataSource,
      pagination,
      tenantRoleLevel,
      serverTypeCode: code['HITF.SERVICE_TYPE'],
      openAuthConfig: this.openAuthConfig,
      openDocument: this.openDocument,
      rowSelection,
    };

    return (
      <>
        <Header title={intl.get('hitf.interfaces.view.message.title.header').d('接口能力汇总')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.batchAuth`,
                type: 'button',
                meaning: '批量添加认证',
              },
            ]}
            type="primary"
            icon="plus"
            disabled={isEmpty(selectedRowKeys)}
            onClick={this.handleOpenModal}
          >
            {intl.get('hitf.interfaces.view.button.add').d('批量添加认证')}
          </ButtonPermission>
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
      </>
    );
  }
}
