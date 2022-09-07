/**
 * trClient - 客户端
 * @date: 2018-12-24
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Button, Table, Tag, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isObject, isString } from 'lodash';
import {
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { isTenantRoleLevel, getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import Lov from 'components/Lov';
import Drawer from './Drawer';
import MemberDrawer from './MemberDrawer';

const FormItem = Form.Item;

function isJSON(str) {
  let result;
  try {
    result = JSON.parse(str);
  } catch (e) {
    return false;
  }
  return isObject(result) && !isString(result);
}
@connect(({ loading, trClient }) => ({
  trClient,
  fetchListLoading: loading.effects['trClient/fetchClientList'],
  fetchDetailLoading: loading.effects['trClient/fetchDetail'],
  createLoading: loading.effects['trClient/createClient'],
  updateLoading: loading.effects['trClient/updateClient'],
  loadingDistributeUsers: loading.effects['trClient/roleQueryAll'], // 查询可分配的所有角色
  fetchOwnedLoading: loading.effects['trClient/roleCurrent'],
  fetchVisitLoading: loading.effects['trClient/roleVisitCurrent'],
  saveRoleLoading: loading.effects['trClient/saveRoleSet'],
  tenantId: getCurrentOrganizationId(),
  isTenant: isTenantRoleLevel(),
}))
@formatterCollections({ code: ['hiam.client', 'entity.tenant', 'hiam.roleManagement'] })
@Form.create({ fieldNameProp: null })
/**
 * trClient - 业务组件 - 客户端
 * @extends {Component} - React.Component
 * @reactProps {!Object} [trClient={}] - 数据源
 * @reactProps {!Object} [loading={}] - 加载是否完成
 * @reactProps {!Object} [loading.effect={}] - 加载是否完成
 * @reactProps {boolean} [fetchListLoading=false] - 客户端列表信息加载中
 * @reactProps {boolean} [fetchDetailLoading=false] - 详情信息加载中
 * @reactProps {boolean} [fetchRandomLoading=false] - 随机客户端信息生成中
 * @reactProps {boolean} [createLoading=false] - 新增客户端处理中
 * @reactProps {boolean} [loadingDistributeUsers=false] - 查询可分配的所有角色处理中
 * @reactProps {boolean} [fetchOwnedLoading=false] - 查询所属角色处理中
 * @reactProps {boolean} [saveRoleLoading=false] - 保存分配角色处理中
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
export default class ThreeRoleClient extends React.Component {
  state = {
    visible: false, // 编辑弹窗
    detailStatus: 'create', // the detail's status
    memberVisible: false, // 分配用户弹窗
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'trClient/queryType',
    });
    this.handleSearch();
    this.fetchPublicKey();
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    const { dispatch = () => {} } = this.props;
    dispatch({
      type: 'trClient/getPublicKey',
    });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { form, dispatch, tenantId } = this.props;
    form.validateFields((err, values) => {
      const fieldValues = values || {};
      if (!err) {
        dispatch({
          type: 'trClient/fetchClientList',
          payload: {
            page: isEmpty(fields) ? {} : fields,
            tenantId,
            ...fieldValues,
          },
        });
      }
    });
  }

  /**
   * @function handleResetSearch - 重置查询表单
   */
  @Bind()
  handleResetSearch() {
    this.props.form.resetFields();
  }

  // 打开连接测试弹窗
  @Bind()
  showModal() {
    this.setState({ visible: true });
  }

  // 关闭连接测试弹窗
  @Bind()
  hiddenModal() {
    this.setState({ visible: false });
  }

  // 新建
  @Bind()
  handleAdd() {
    const { dispatch } = this.props;
    // 新建的时候清空详情的数据 防止 编辑 => 新建 后 将编辑的数据传给后台
    dispatch({
      type: 'trClient/updateState',
      payload: {
        detailData: {},
      },
    });
    this.setState({
      detailStatus: 'create',
      visible: true,
    });
  }

  // fetch detail data
  @Bind()
  fetchDetailData(record) {
    const { dispatch, tenantId } = this.props;
    return dispatch({
      type: 'trClient/fetchDetail',
      payload: {
        tenantId,
        clientId: record.id,
      },
    });
  }

  // 编辑
  @Bind()
  handleUpdate(record) {
    this.setState({
      detailRecord: record,
      detailStatus: 'update',
      visible: true,
    });
  }

  @Bind()
  handleStatus(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'trClient/changeStatus',
      payload: record,
    }).then((res) => {
      const parseResult = isJSON(res) ? JSON.parse(res) : res;
      if (parseResult.failed) {
        notification.error({ description: parseResult.message });
      } else {
        notification.success();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleAssignMember(record) {
    this.setState({
      detailRecord: record,
      detailStatus: 'update',
      memberVisible: true,
    });
  }

  @Bind()
  handleAssignMemberClose() {
    this.setState({
      memberVisible: false,
    });
  }

  // 保存
  @Bind()
  handleSave(fieldsValue) {
    const {
      dispatch,
      trClient: { pagination, detailData = {} },
      tenantId,
    } = this.props;
    const { id } = detailData;
    const { authorizedGrantTypes, scope, autoApprove } = fieldsValue;
    let grantTypes;
    let scopeT;
    let autoApproveT;
    if (authorizedGrantTypes) {
      grantTypes = authorizedGrantTypes.join(',');
    }
    if (scope) {
      scopeT = scope.join(',');
    }
    if (autoApprove) {
      autoApproveT = autoApprove.join(',');
    }
    // TODO: 保存时保存编辑客户端分配角色
    dispatch({
      type: `trClient/${id ? 'updateClient' : 'createClient'}`,
      payload: {
        tenantId,
        ...detailData,
        ...fieldsValue,
        authorizedGrantTypes: grantTypes,
        scope: scopeT,
        autoApprove: autoApproveT,
        clientId: id,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hiddenModal();
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  handleMemberSave(fieldsValue) {
    const { dispatch } = this.props;
    dispatch({
      type: 'trClient/fetchAssignRole',
      payload: fieldsValue,
    }).then((res) => {
      if (res) {
        this.handleAssignMemberClose();
        notification.success();
      }
    });
  }

  /**
   * 数据列表，删除
   * @param {obejct} record - 操作对象
   */
  // @Bind()
  // handleDeleteContent(record) {
  //   const {
  //     dispatch,
  //     tenantId,
  //     client: { pagination },
  //   } = this.props;
  //   dispatch({
  //     type: 'client/deleteClient',
  //     payload: { tenantId, ...record },
  //   }).then(res => {
  //     if (res) {
  //       notification.success();
  //       this.handleSearch(pagination);
  //     }
  //   });
  // }

  /**
   * @function renderForm - 渲染搜索表单
   */
  renderFilterForm() {
    const { getFieldDecorator } = this.props.form;
    const { isTenant } = this.props;
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          {!isTenant && (
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                label={intl.get('entity.tenant.tag').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('organizationId')(
                  <Lov code="HPFM.TENANT" textField="tenantName" />
                )}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.client.model.client.name').d('名称')}
            >
              {getFieldDecorator('name', {})(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleResetSearch}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  // 查询所有可分配的角色
  @Bind()
  fetchAllRoles(fields) {
    const { level, ...rest } = fields;
    const { dispatch } = this.props;
    const { detailRecord } = this.state;
    return dispatch({
      type: 'trClient/roleQueryAll',
      payload: {
        ...rest,
        clientId: detailRecord.id,
      },
    });
  }

  render() {
    const {
      match: { path },
      dispatch,
      tenantId,
      trClient,
      createLoading,
      updateLoading,
      fetchListLoading,
      fetchRandomLoading,
      fetchDetailLoading,
      loadingDistributeUsers,
      fetchOwnedLoading,
      saveRoleLoading,
      isTenant,
      fetchVisitLoading,
    } = this.props;
    const {
      clientList = [],
      pagination,
      typeList = [],
      paginationRole,
      detailData = {},
      levelMap = {},
      publicKey,
      visitRoleList = [],
      visitRolePagination = {},
    } = trClient;
    const {
      visible,
      detailStatus = 'create', // create
      detailRecord = {}, // current edit record
      memberVisible,
    } = this.state;
    const columns = [
      !isTenant && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hiam.client.model.client.name').d('名称'),
        dataIndex: 'name',
        width: 200,
      },
      {
        title: intl.get('hiam.client.model.client.authorizedGrantTypes').d('授权类型'),
        dataIndex: 'authorizedGrantTypes',
        // width: 200,
        render: (text) => {
          const typeListT = text.split(',') || [];
          return typeListT.map((item) => <Tag key={item}>{item}</Tag>);
        },
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operation',
        width: 200,
        // fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleUpdate(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'enabled',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.status`,
                      type: 'button',
                      meaning: '客户端-状态',
                    },
                  ]}
                  onClick={() => this.handleStatus(record)}
                >
                  {record.enabledFlag
                    ? intl.get(`hzero.common.status.disable`).d('禁用')
                    : intl.get(`hzero.common.status.enable`).d('启用')}
                </ButtonPermission>
              ),
              len: 2,
              title: record.enabled
                ? intl.get(`hzero.common.status.disable`).d('禁用')
                : intl.get(`hzero.common.status.enable`).d('启用'),
            },
            {
              key: 'assign-members',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.members`,
                      type: 'button',
                      meaning: '客户端-分配角色',
                    },
                  ]}
                  onClick={() => this.handleAssignMember(record)}
                >
                  {intl.get(`hiam.client.view.title.members`).d('分配角色')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get(`hiam.client.view.title.members`).d('分配角色'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    const title =
      detailStatus === 'create'
        ? intl.get('hzero.common.button.create').d('新建')
        : intl.get('hzero.common.button.edit').d('编辑');

    const drawerProps = {
      dispatch,
      tenantId,
      title,
      typeList,
      visible,
      detailStatus, // current edit status
      detailRecord, // current edit record
      loading: createLoading || updateLoading || fetchDetailLoading || fetchRandomLoading,
      fetchLoading: fetchDetailLoading || fetchRandomLoading,
      initData: detailData,
      onCancel: this.hiddenModal,
      onOk: this.handleSave,
      fetchDetailData: this.fetchDetailData,
      isTenantRoleLevel: isTenant,
      levelMap,
      publicKey,
    };

    const memberProps = {
      path,
      dispatch,
      tenantId,
      visible: memberVisible,
      paginationRole,
      visitRolePagination,
      detailStatus,
      detailRecord, // current edit record
      loadingDistributeUsers,
      saveRoleLoading,
      fetchOwnedLoading,
      visitRoleList,
      initData: detailData,
      fetchLoading: fetchOwnedLoading || fetchVisitLoading,
      loading: createLoading || updateLoading || fetchDetailLoading || fetchRandomLoading,
      onOk: this.handleMemberSave,
      onCancel: this.handleAssignMemberClose,
      fetchAllRoles: this.fetchAllRoles,
    };

    return (
      <>
        <Header title={intl.get('hiam.client.view.message.title.list').d('客户端')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '客户端-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.handleAdd}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">{this.renderFilterForm()}</div>
          <Table
            bordered
            rowKey="id"
            loading={fetchListLoading}
            dataSource={clientList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns, 400) }}
            pagination={pagination}
            onChange={this.handleSearch}
          />
          {visible && <Drawer {...drawerProps} />}
          {memberVisible && <MemberDrawer {...memberProps} />}
        </Content>
      </>
    );
  }
}
