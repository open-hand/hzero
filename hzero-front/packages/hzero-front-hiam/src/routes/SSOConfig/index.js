/**
 * SSOConfig 二级域名单点登录配置
 * @date: 2019-6-27
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table, Popconfirm } from 'hzero-ui';
import { Link } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

import FilterForm from './FilterForm';
import Drawer from './Drawer';
import DistributeDrawer from './DistributeModal';

@connect(({ ssoConfig, loading }) => ({
  ssoConfig,
  isSiteFlag: !isTenantRoleLevel(),
  fetchConfigListLoading: loading.effects['ssoConfig/fetchConfigList'],
  getConfigDetailLoading: loading.effects['ssoConfig/getConfigDetail'],
  createConfigDetailLoading: loading.effects['ssoConfig/createConfig'],
  editConfigListLoading: loading.effects['ssoConfig/editConfig'],
  queryDistributeLoading: loading.effects['ssoConfig/queryDistributeList'],
  deleteDistributeLoading: loading.effects['ssoConfig/deleteDistribute'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hiam.ssoConfig'],
})
export default class SSOConfig extends React.Component {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
    this.state = {
      modalVisible: false,
      editflag: false,
      distributeVisible: false,
      currentRecord: {},
    };
  }

  componentDidMount() {
    this.handleSearch();
    const { dispatch } = this.props;
    const lovCodes = { typeList: 'HIAM.SSO_TYPE_CODE' };
    dispatch({
      type: 'ssoConfig/init',
      payload: {
        lovCodes,
      },
    });
  }

  /**
   * @function handleSearch - 获取单点登陆配置列表数据
   * @param {object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const fieldsValue = this.filterFormRef.current.getFieldsValue();
    dispatch({
      type: 'ssoConfig/fetchConfigList',
      payload: { ...fieldsValue, ...params },
    });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination = {}) {
    this.handleSearch({
      page: pagination,
    });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}, editflag) {
    this.handleModalVisible(true);
    const { dispatch } = this.props;
    this.setState({ editflag });
    if (editflag) {
      dispatch({
        type: 'ssoConfig/getConfigDetail',
        payload: record,
      });
    }
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ssoConfig/updateState',
      payload: {
        ssoConfigDetail: {},
      },
    });
    this.setState({ editflag: false });
    this.handleModalVisible(false);
  }

  /**
   * 模态窗确认
   */
  @Bind()
  handleConfirm(fieldValues, record) {
    const { dispatch } = this.props;
    const { editflag } = this.state;
    if (editflag) {
      dispatch({
        type: 'ssoConfig/editConfig',
        payload: fieldValues,
      }).then((res) => {
        if (res) {
          this.setState({ editflag: false });
          notification.success();
          this.hideModal();
          this.handleSearch();
        } else {
          this.setState({ editflag: true });
          dispatch({
            type: 'ssoConfig/getConfigDetail',
            payload: record,
          });
        }
      });
    } else {
      dispatch({
        type: 'ssoConfig/createConfig',
        payload: fieldValues,
      }).then((res) => {
        this.setState({ editflag: false });
        if (res) {
          notification.success();
          this.hideModal();
          this.handleSearch();
        }
      });
    }
  }

  /**
   * 删除配置
   */
  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'ssoConfig/deleteConfig',
      payload: record,
    }).then((res) => {
      this.setState({ editflag: false });
      if (res) {
        notification.success();
        this.handleSearch();
      }
    });
  }

  // 保存分配租户/公司
  @Bind()
  handleSaveDistribute(record) {
    const { currentRecord = {} } = this.state;
    if (!isEmpty(record)) {
      const { dispatch } = this.props;
      dispatch({
        type: 'ssoConfig/saveDistribute',
        payload: { domainId: currentRecord.domainId, body: record },
      }).then((res) => {
        // this.setState({ editflag: false });
        if (res) {
          notification.success();
          this.handleQueryDistribute(currentRecord);
        }
      });
    }
  }

  // 设置分配模态框
  @Bind()
  handleDistributeModalVisible(flag) {
    this.setState({
      distributeVisible: !!flag,
    });
  }

  // 展示分配模态框
  @Bind()
  showDistributeModal(record) {
    this.handleDistributeModalVisible(true);
    this.setState({
      currentRecord: record,
    });
    this.handleQueryDistribute(record);
  }

  // 查询分配列表
  @Bind()
  handleQueryDistribute(record, params = { page: 0, size: 10 }) {
    const { dispatch } = this.props;
    dispatch({
      type: 'ssoConfig/queryDistributeList',
      payload: {
        domainId: record.domainId,
        params,
      },
    });
  }

  // 删除分配租户/公司
  @Bind()
  handleDeleteDistribute(list) {
    const { currentRecord } = this.state;
    if (!isEmpty(list)) {
      const { dispatch } = this.props;
      dispatch({
        type: 'ssoConfig/deleteDistribute',
        payload: { domainId: currentRecord.domainId, body: list },
      }).then((res) => {
        // this.setState({ editflag: false });
        if (res) {
          notification.success();
          this.handleQueryDistribute(currentRecord);
        }
      });
    }
  }

  render() {
    const {
      fetchConfigListLoading = false,
      getConfigDetailLoading = false,
      createConfigDetailLoad = false,
      editConfigListLoading = false,
      queryDistributeLoading = false,
      deleteDistributeLoading = false,
      match: { path },
      location: { search },
      isSiteFlag,
      ssoConfig: {
        pagination = {},
        ssoConfigList = [],
        typeList = [],
        ssoConfigDetail = {},
        distributeList = [],
        distributePagination = {},
      },
      dispatch,
    } = this.props;
    const { modalVisible, editflag, distributeVisible, currentRecord = {} } = this.state;

    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const columns = [
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.domainUrl').d('单点登录域名'),
        dataIndex: 'domainUrl',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.ssoTypeCode').d('单点登录类型'),
        dataIndex: 'ssoTypeMeaning',
        width: 150,
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.ssoServerUrl').d('单点登录服务器地址'),
        width: 200,
        dataIndex: 'ssoServerUrl',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.ssoLoginUrl').d('单点登录地址'),
        width: 200,
        dataIndex: 'ssoLoginUrl',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.ssoLogoutUrl').d('单点登出地址'),
        dataIndex: 'ssoLogoutUrl',
        width: 200,
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.clientHostUrl').d('客户端地址'),
        width: 200,
        dataIndex: 'clientHostUrl',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.ssoClientId').d('oAuth认证ClientId'),
        width: 200,
        dataIndex: 'ssoClientId',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.ssoClientPwd').d('oAuth认证Client密码'),
        width: 200,
        dataIndex: 'ssoClientPwd',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.ssoUserInfo').d('oAuth认证用户信息'),
        width: 200,
        dataIndex: 'ssoUserInfo',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.samlMetaUrl').d('SAML元数据地址'),
        width: 200,
        dataIndex: 'samlMetaUrl',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '域名配置-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showModal(record, true);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (isSiteFlag) {
            operators.push({
              key: 'distributeTenant',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.distributeTenant`,
                      type: 'button',
                      meaning: '域名配置-分配租户',
                    },
                  ]}
                  onClick={() => {
                    this.showDistributeModal(record);
                  }}
                >
                  {intl.get('hiam.ssoConfig.view.message.title.distributeTenant').d('分配租户')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hiam.ssoConfig.view.message.title.distributeTenant').d('分配租户'),
            });
          } else {
            operators.push({
              key: 'distributeCompany',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.distributeCompany`,
                      type: 'button',
                      meaning: '域名配置-分配公司',
                    },
                  ]}
                  onClick={() => {
                    this.showDistributeModal(record);
                  }}
                >
                  {intl.get('hiam.ssoConfig.view.message.title.distributeCompany').d('分配公司')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hiam.ssoConfig.view.message.title.distributeCompany').d('分配公司'),
            });
          }
          operators.push(
            ...[
              {
                key: 'detail',
                ele: (
                  <Link
                    to={
                      path.indexOf('/private') === 0
                        ? `/private/hiam/domain-config/template/${record.domainId}/${record.tenantId}?access_token=${accessToken}`
                        : `/hiam/domain-config/template/${record.domainId}/${record.tenantId}`
                    }
                  >
                    {intl.get('hiam.ssoConfig.view.message.title.detail').d('分配模板')}
                  </Link>
                ),
                len: 4,
                title: intl.get('hiam.ssoConfig.view.message.title.detail').d('分配模板'),
              },
              {
                key: 'delete',
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={() => this.handleDelete(record)}
                  >
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${path}.button.delete`,
                          type: 'button',
                          meaning: '域名配置-删除',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              },
            ]
          );
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    return (
      <>
        <Header title={intl.get('hiam.ssoConfig.view.message.title.list').d('域名配置')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '域名配置-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={() => this.showModal({}, false)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm
            onSearch={this.handleSearch}
            isSiteFlag={isSiteFlag}
            ref={this.filterFormRef}
          />
          <Table
            bordered
            rowKey="domainId"
            loading={fetchConfigListLoading}
            dataSource={ssoConfigList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          {modalVisible && (
            <Drawer
              title={
                editflag
                  ? intl.get('hiam.ssoConfig.view.message.modal.edit').d('编辑单点登录页面')
                  : intl.get('hiam.ssoConfig.view.message.modal.create').d('新建单点登录页面')
              }
              editflag={editflag}
              modalVisible={modalVisible}
              onOk={this.handleConfirm}
              onCancel={this.hideModal}
              initData={ssoConfigDetail}
              typeList={typeList}
              initLoading={getConfigDetailLoading}
              loading={createConfigDetailLoad || editConfigListLoading}
              isSiteFlag={isSiteFlag}
              tenantId={getCurrentOrganizationId()}
            />
          )}
          {distributeVisible && (
            <DistributeDrawer
              title={
                isSiteFlag
                  ? intl.get('hiam.ssoConfig.view.message.title.distributeTenant').d('分配租户')
                  : intl.get('hiam.ssoConfig.view.message.title.distributeCompany').d('分配公司')
              }
              path={path}
              dispatch={dispatch}
              modalVisible={distributeVisible}
              onOk={this.handleSaveDistribute}
              onCancel={() => this.handleDistributeModalVisible(false)}
              initData={distributeList}
              loading={queryDistributeLoading}
              deleteDistributeLoading={deleteDistributeLoading}
              isSiteFlag={isSiteFlag}
              tenantId={getCurrentOrganizationId()}
              pagination={distributePagination}
              currentRecord={currentRecord}
              handleQuery={this.handleQueryDistribute}
              handleDeleteDistribute={this.handleDeleteDistribute}
            />
          )}
        </Content>
      </>
    );
  }
}
