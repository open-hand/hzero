/* eslint-disable no-nested-ternary */
/**
 * 租户切换
 */
import React, { PureComponent } from 'react';
import { Icon, Modal, Spin, Table } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import { getCurrentOrganizationId, getCurrentTenant } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { cleanMenuTabs } from 'utils/menuTab';

import RecordCheckbox from './RecordCheckbox';

import style from './index.less';

/**
 * 租户切换 Tenant
 *
 * @author wangjiacheng <jiacheng.wang@hand-china.com>
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!boolean} [modalVisible=false] - 是否显示选择租户的模态框
 * @reactProps {!string} [selectTenant=''] - 用户选择的租户名称
 * @reactProps {!array} [historyTenantList=[]] - 缓存用户选择的租户数据
 * @reactProps {!array} [tenantList=[]] - 租户列表数据
 * @reactProps {!number} [organizationId] - 当前登录用户的租户ID
 * @reactProps {!string} [tenantName] - 当前登录用户的租户名称
 * @returns React.element
 */
class DefaultTenantSelect extends PureComponent {
  /**
   * constructor - constructor方法
   * 组件构造函数
   */
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      tenantId: getCurrentOrganizationId(),
      selectTenant: (getCurrentTenant() || {}).tenantName || '',
    };
  }

  /**
   * @function showModal - 显示和隐藏租户切换模态框
   * @param {boolean} flag - 显示或隐藏标识
   */
  @Bind()
  showModal(flag) {
    const { dispatch } = this.props;
    if (flag) {
      dispatch({
        type: 'user/fetchTenantList',
      });
    }
    this.setState({ modalVisible: flag });
  }

  /**
   * @function handleSelectTenant - 选择租户
   * @param {object} record - 选择的租户行数据
   * @param {string} record.tenantId - 租户ID
   * @param {string} record.tenantName - 租户名称
   * @param {string} record.tenantNum - 租户编码
   */
  @Bind()
  handleSelectTenant(record) {
    const { dispatch } = this.props;
    this.setState({
      // modalVisible: false,
      // 设置 loading
      updateTenantLoading: true,
      selectTenant: record.tenantName,
    });
    // TODO 历史记录只需要缓存历史 而不需要缓存当前租户
    // 设置当前租户ID缓存
    // const saveTenant = setSession('currentTenant', record);
    // 重新设置租户历史数据缓存
    // warn 清空 tabs 信息
    cleanMenuTabs();
    // 切换租户成功后跳转首页，刷新页面
    dispatch(routerRedux.push({ pathname: '/' }));
    // 缓存当前用户的租户
    dispatch({
      type: 'user/updateCurrentTenant',
      payload: { tenantId: record.tenantId },
    }).then((res) => {
      if (res) {
        dispatch(routerRedux.push({ pathname: '/workplace' }));
        // 成功 刷新页面
        window.location.reload();
      } else {
        // 失败 不关闭模态框
        this.setState({
          updateTenantLoading: false,
        });
      }
    });
  }

  /**
   * 修改默认租户
   */
  @Bind()
  handleChangeDefaultTenant(record) {
    // 如果已经设置了 默认租户 则 不能切换
    const payload = {};
    if (isUndefined(record.defaultTenantId)) {
      payload.tenantId = record.tenantId;
    }
    const { dispatch } = this.props;
    dispatch({
      type: 'user/updateDefaultTenant',
      payload,
    }).then((res) => {
      if (res) {
        notification.success();
        dispatch({
          type: 'user/fetchTenantList',
        });
      }
    });
  }

  render() {
    const { tenantList = [], tenantName, allLoading = false } = this.props;
    const { tenantId, updateTenantLoading = false } = this.state;
    const columns = [
      {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
        render: (text, record) => (
          <div className={style.tenant}>
            {record.tenantId === tenantId && <div className={style['tenant-select-wrapper']} />}
            <a onClick={() => this.handleSelectTenant(record)}>
              {text}
              {record.tenantId === tenantId && `(${intl.get('hzero.common.current').d('当前')})`}
            </a>
          </div>
        ),
      },
      {
        title: intl.get('entity.tenant.code').d('租户编码'),
        width: 180,
        dataIndex: 'tenantNum',
      },
      {
        title: intl.get('hzero.common.view.tenantSelect.defaultTenant').d('默认租户'),
        width: 90,
        dataIndex: 'defaultTenantId',
        render: (defaultTenantId, record) => (
          <RecordCheckbox
            record={record}
            onClick={this.handleChangeDefaultTenant}
            checked={!isUndefined(defaultTenantId)}
          />
        ),
      },
    ];
    return (
      <>
        <>
          {this.state.selectTenant ? (
            <>
              <span
                style={{
                  width: '100%',
                  display: 'inline-block',
                  marginRight: '12px',
                  backgroundColor: 'rgba(255, 255, 255,.1)',
                  color: '#666',
                }}
                size="small"
                onClick={() => this.showModal(true)}
              >
                {this.state.selectTenant}
              </span>
            </>
          ) : tenantName ? (
            <span
              style={{
                width: '100%',
                display: 'inline-block',
                marginRight: '12px',
                backgroundColor: 'rgba(255, 255, 255,.1)',
              }}
              size="small"
              onClick={() => this.showModal(true)}
            >
              <Icon type="home" />
              {tenantName}
            </span>
          ) : (
            ''
          )}
        </>
        <Modal
          title={intl.get('hzero.common.view.tenantSelect.title').d('选择租户')}
          width="620px"
          bodyStyle={{ paddingTop: 0, height: '460px', overflow: 'scroll' }}
          visible={this.state.modalVisible}
          onCancel={() => this.showModal(false)}
          footer={null}
        >
          <Spin spinning={updateTenantLoading}>
            <Table
              bordered
              style={{ marginTop: 24 }}
              rowKey="tenantId"
              pagination={false}
              loading={allLoading}
              dataSource={tenantList}
              columns={columns}
            />
          </Spin>
        </Modal>
      </>
    );
  }
}

export default formatterCollections({ code: ['hpfm.tenantSelect', 'entity.tenant'] })(
  connect(({ user: { tenantList = [], currentUser: { tenantName } } = {}, loading }) => ({
    tenantList,
    tenantName,
    allLoading:
      loading.effects['user/updateDefaultTenant'] || loading.effects['user/fetchTenantList'],
  }))(DefaultTenantSelect)
);
