/**
 * Client - 客户端
 * @date: 2018-12-24
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, Button, Table, Tag, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
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

const FormItem = Form.Item;
@connect(({ loading, client }) => ({
  client,
  fetchListLoading: loading.effects['client/fetchClientList'],
  fetchDetailLoading: loading.effects['client/fetchDetail'],
  createLoading: loading.effects['client/createClient'],
  updateLoading: loading.effects['client/updateClient'],
  loadingDistributeUsers: loading.effects['client/roleQueryAll'], // 查询可分配的所有角色
  fetchOwnedLoading: loading.effects['client/roleCurrent'],
  saveRoleLoading: loading.effects['client/saveRoleSet'],
  tenantId: getCurrentOrganizationId(),
  isTenant: isTenantRoleLevel(),
}))
@formatterCollections({ code: ['hiam.client', 'entity.tenant', 'hiam.roleManagement'] })
@Form.create({ fieldNameProp: null })
/**
 * Client - 业务组件 - 客户端
 * @extends {Component} - React.Component
 * @reactProps {!Object} [client={}] - 数据源
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
export default class Client extends React.Component {
  state = {
    visible: false, // 测试弹窗
    detailStatus: 'create', // the detail's status
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'client/queryType',
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
      type: 'client/getPublicKey',
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
          type: 'client/fetchClientList',
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
      type: 'client/updateState',
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
      type: 'client/fetchDetail',
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

  // 保存
  @Bind()
  handleSave(fieldsValue) {
    const {
      dispatch,
      client: { pagination, detailData = {} },
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
      type: `client/${id ? 'updateClient' : 'createClient'}`,
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
    return dispatch({
      type: 'client/roleQueryAll',
      payload: {
        ...rest,
      },
    });
  }

  render() {
    const {
      match: { path },
      dispatch,
      tenantId,
      client,
      createLoading,
      updateLoading,
      fetchListLoading,
      fetchRandomLoading,
      fetchDetailLoading,
      loadingDistributeUsers,
      fetchOwnedLoading,
      saveRoleLoading,
      isTenant,
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
    } = client;
    const {
      visible,
      detailStatus = 'create', // create
      detailRecord = {}, // current edit record
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
        dataIndex: 'authGrantTypeMeanings',
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
        width: 120,
        fixed: 'right',
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
      path,
      dispatch,
      tenantId,
      title,
      typeList,
      visible,
      loadingDistributeUsers,
      fetchOwnedLoading,
      saveRoleLoading,
      paginationRole,
      detailRecord, // current edit record
      detailStatus, // current edit status
      loading: createLoading || updateLoading || fetchDetailLoading || fetchRandomLoading,
      fetchLoading: fetchDetailLoading || fetchRandomLoading,
      initData: detailData,
      onCancel: this.hiddenModal,
      onOk: this.handleSave,
      fetchAllRoles: this.fetchAllRoles,
      fetchDetailData: this.fetchDetailData,
      isTenantRoleLevel: isTenant,
      levelMap,
      publicKey,
      visitRoleList,
      visitRolePagination,
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
        </Content>
      </>
    );
  }
}
