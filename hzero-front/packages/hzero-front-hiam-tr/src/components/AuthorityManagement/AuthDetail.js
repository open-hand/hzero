/* eslint-disable no-nested-ternary */
/**
 *  数据权限维度 __组件
 * @date: 2019-11-26
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Button, Form, Input, Modal, Switch, Table, Tooltip, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import lodash from 'lodash';
import { connect } from 'dva';

import { Button as ButtonPermission } from 'components/Permission';
import {
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { HZERO_IAM } from 'utils/config';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import { operatorRender, yesOrNoRender } from 'utils/renderer';

import AddDataModal from './AddDataModal';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;

/**
 * 租户级权限管理 - 值集
 * @extends {Component} - React.Component
 * @reactProps {Object} authorityPuragent - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({
  code: ['hiam.authorityManagement'],
})
@Form.create({ fieldNameProp: null })
class AuthDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectRows: [],
      switchLoading: false,
      addModalVisible: false,
    };
  }

  componentDidMount() {
    this.fetchData();
  }

  /**
   *查询数据
   *
   * @param {Object} pageData
   */
  @Bind()
  fetchData(pageData = {}) {
    const {
      form,
      dispatch,
      roleId,
      userId,
      code,
      isSecGrp = false,
      secGrpId = '',
      isAccount = false,
    } = this.props;
    const fieldsValue = form.getFieldsValue();
    const typeCode = code.replace(/([A-Z])/, '_$1');
    const payload = isSecGrp ? { secGrpId, roleId } : { roleId, userId };
    dispatch({
      type: `${
        isSecGrp
          ? isAccount // isAccount=true 证明来源子账户管理-分配安全组
            ? `trAccSecGrpAuthority`
            : `trSecGrpAuthority`
          : !lodash.isUndefined(roleId)
          ? 'trRoleDataAuthority'
          : 'trAuthority'
      }${lodash.upperFirst(code)}/fetchAuthority${lodash.upperFirst(code)}`,
      payload: {
        ...payload,
        ...fieldsValue,
        authorityTypeCode:
          code === 'valueList'
            ? 'LOV'
            : code === 'dataSource'
            ? lodash.toUpper(code)
            : code === 'purorg'
            ? 'PURCHASE_ORGANIZATION'
            : code === 'puragent'
            ? 'PURCHASE_AGENT'
            : lodash.toUpper(typeCode),
        ...pageData,
      },
    });
  }

  /**
   * 添加数据
   * @param {Aarray} addRows 选择的数据
   */
  @Bind()
  addValueList(addRows) {
    const {
      dispatch,
      roleDataAuthority: { head = {} },
      roleId,
      userId,
      code,
    } = this.props;
    const typeCode = code.replace(/([A-Z])/, '_$1');
    const obj = !lodash.isUndefined(roleId)
      ? {
          roleAuthData: head,
          roleAuthDataLineList: addRows,
        }
      : {
          userAuthority: head,
          userAuthorityLineList: addRows,
        };
    dispatch({
      type: `${
        !lodash.isUndefined(roleId) ? 'trRoleDataAuthority' : 'trAuthority'
      }${lodash.upperFirst(code)}/addAuthority${lodash.upperFirst(code)}`,
      payload: {
        authorityTypeCode:
          code === 'valueList'
            ? 'LOV'
            : code === 'dataSource'
            ? lodash.toUpper(code)
            : code === 'purorg'
            ? 'PURCHASE_ORGANIZATION'
            : code === 'puragent'
            ? 'PURCHASE_AGENT'
            : lodash.toUpper(typeCode),
        roleId,
        userId,
        ...obj,
      },
    }).then((response) => {
      if (response) {
        this.onHideAddModal();
        notification.success();
        this.refresh();
      }
    });
  }

  /**
   *删除方法
   */
  @Bind()
  remove() {
    const { dispatch, roleId, userId, code } = this.props;
    const { selectRows } = this.state;
    const onOk = () => {
      dispatch({
        type: `${
          !lodash.isUndefined(roleId) ? 'trRoleDataAuthority' : 'trAuthority'
        }${lodash.upperFirst(code)}/deleteAuthority${lodash.upperFirst(code)}`,
        payload: {
          userId,
          roleId,
          deleteRows: selectRows,
        },
      }).then((response) => {
        if (response) {
          this.refresh();
          notification.success();
        }
      });
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  }

  /**
   *刷新
   */
  @Bind()
  refresh() {
    this.fetchData();
    this.setState({
      selectRows: [],
    });
  }

  /**
   * 表格勾选
   * @param {null} _ 占位
   * @param {object} selectedRow 选中行
   */
  @Bind()
  onSelectChange(_, selectedRows) {
    this.setState({ selectRows: selectedRows });
  }

  /**
   * 展示弹出框
   */
  @Bind()
  onShowAddModal() {
    this.setState({
      addModalVisible: true,
    });
  }

  /**
   * 隐藏弹出框
   */
  @Bind()
  onHideAddModal() {
    this.setState({
      addModalVisible: false,
    });
  }

  /**
   *点击查询按钮事件
   */
  @Bind()
  queryValue() {
    this.fetchData();
    this.setState({ selectRows: [] });
  }

  /**
   *分页change事件
   */
  @Bind()
  handleTableChange(pagination = {}) {
    this.fetchData({
      page: pagination,
    });
  }

  /**
   * 表单重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   *点击加入全部后触发事件
   *
   * @param {Boolean} checked switch的value值
   */
  @Bind()
  includeAllFlag(checked) {
    const {
      code,
      dispatch,
      roleId,
      userId,
      roleDataAuthority: { head = {} },
    } = this.props;
    this.setState({
      switchLoading: true,
    });
    const typeCode = code.replace(/([A-Z])/, '_$1');
    const obj = !lodash.isUndefined(roleId)
      ? {
          roleAuthData: {
            ...head,
            includeAllFlag: checked ? 1 : 0,
          },
          roleAuthDataLineList: [],
        }
      : {
          userAuthority: {
            ...head,
            includeAllFlag: checked ? 1 : 0,
          },
          userAuthorityLineList: [],
        };
    dispatch({
      type: `${
        !lodash.isUndefined(roleId) ? 'trRoleDataAuthority' : 'trAuthority'
      }${lodash.upperFirst(code)}/addAuthority${lodash.upperFirst(code)}`,
      payload: {
        authorityTypeCode:
          code === 'valueList'
            ? 'LOV'
            : code === 'dataSource'
            ? lodash.toUpper(code)
            : code === 'purorg'
            ? 'PURCHASE_ORGANIZATION'
            : code === 'puragent'
            ? 'PURCHASE_AGENT'
            : lodash.toUpper(typeCode),
        roleId,
        userId,
        ...obj,
      },
    }).then((response) => {
      if (response) {
        this.refresh();
        notification.success();
        this.setState({
          switchLoading: false,
        });
      }
    });
  }

  /**
   *渲染查询结构
   *
   * @returns
   */
  renderForm() {
    const { getFieldDecorator } = this.props.form;
    const { name, isSecGrp } = this.props;
    const secGrpFormLayout = {
      labelCol: {
        span: 12,
      },
      wrapperCol: {
        span: 12,
      },
    };
    return (
      <Form>
        {isSecGrp ? (
          <>
            <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl
                    .get('hiam.authorityManagement.model.authorityManagement.code', { name })
                    .d(`${name}代码`)}
                  {...secGrpFormLayout}
                >
                  {getFieldDecorator('dataCode')(<Input trim inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  label={intl
                    .get('hiam.authorityManagement.model.authorityManagement.name', { name })
                    .d(`${name}名称`)}
                  {...secGrpFormLayout}
                >
                  {getFieldDecorator('dataName')(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
                <FormItem>
                  <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button type="primary" onClick={() => this.queryValue()} htmlType="submit">
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </FormItem>
              </Col>
            </Row>
          </>
        ) : (
          <Row type="flex" align="bottom" gutter={24}>
            <Col span={8}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.authorityManagement.model.authorityManagement.code', { name })
                  .d(`${name}代码`)}
              >
                {getFieldDecorator('dataCode')(<Input trim inputChinese={false} />)}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl
                  .get('hiam.authorityManagement.model.authorityManagement.name', { name })
                  .d(`${name}名称`)}
              >
                {getFieldDecorator('dataName')(<Input />)}
              </FormItem>
            </Col>
            <Col span={8}>
              <FormItem {...SEARCH_FORM_ITEM_LAYOUT}>
                <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button type="primary" onClick={() => this.queryValue()} htmlType="submit">
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </FormItem>
            </Col>
          </Row>
        )}
      </Form>
    );
  }

  /**
   * 屏蔽/取消屏蔽安全组数据权限
   * @param {object} record - 表格行数据
   */
  @Bind()
  handleShield(record) {
    const { secGrpDclLineId, shieldFlag } = record;
    const { onShield = (e) => e } = this.props;
    const shieldData = { shieldFlag, authorityId: secGrpDclLineId, authorityType: 'DCL' };
    onShield(shieldData, this.fetchData);
  }

  render() {
    const {
      roleDataAuthority = {},
      name,
      code,
      roleId,
      userId,
      organizationId,
      addLoading,
      fetchLoading,
      shieldLoading,
      isSecGrp = false,
      isShow = true,
    } = this.props;
    const { list = [], head = {}, pagination = {} } = roleDataAuthority;
    const typeCode = code.replace(/([A-Z])/, '-$1');
    const { switchLoading, addModalVisible, selectRows } = this.state;
    const columns = [
      !lodash.isUndefined(roleId) &&
        !(code === 'purorg' || code === 'puragent') &&
        !isSecGrp && {
          title: intl.get('hzero.common.model.common.tenantId').d('租户'),
          dataIndex: 'tenantName',
          width: 200,
        },
      {
        title: intl
          .get('hiam.authorityManagement.model.authorityManagement.code', { name })
          .d(`${name}代码`),
        dataIndex: 'dataCode',
        width: 300,
      },
      {
        title: intl
          .get('hiam.authorityManagement.model.authorityManagement.name', { name })
          .d(`${name}名称`),
        dataIndex: 'dataName',
      },
      isSecGrp &&
        isShow && {
          dataIndex: 'shieldFlag',
          title: intl
            .get('hiam.authorityManagement.model.authorityManagement.isShield')
            .d('是否屏蔽'),
          width: 90,
          render: yesOrNoRender,
        },
      isSecGrp &&
        isShow && {
          key: 'operator',
          title: intl.get('hzero.common.button.action').d('操作'),
          width: 100,
          fixed: 'right',
          render: (_, record) => {
            const operators = [];
            const shieldBtn = {
              key: 'shield',
              ele: (
                <ButtonPermission type="text" onClick={() => this.handleShield(record)}>
                  {record.shieldFlag
                    ? intl.get('hiam.authorityManagement.view.button.cancelShield').d('取消屏蔽')
                    : intl.get('hiam.authorityManagement.view.button.shield').d('屏蔽')}
                </ButtonPermission>
              ),
              len: record.shieldFlag ? 4 : 2,
              title: record.shieldFlag
                ? intl.get('hiam.authorityManagement.view.button.cancelShield').d('取消屏蔽')
                : intl.get('hiam.authorityManagement.view.button.shield').d('屏蔽'),
            };
            operators.push(shieldBtn);
            return operatorRender(operators);
          },
        },
    ].filter(Boolean);

    const addModalOptions = {
      defaultFlag: true,
      rowKey: 'dataId',
      title: intl
        .get('hiam.authorityManagement.model.authorityManagement.select', { name })
        .d(`选择${name}`),
      lov: {
        lovTypeCode: 'URL',
        valueField: 'dataId',
        displayField: 'dataName',
        tableFields: columns,
        queryFields: [
          {
            label: intl
              .get('hiam.authorityManagement.model.authorityManagement.code', { name })
              .d(`${name}代码`),
            field: 'dataCode',
          },
          {
            label: intl
              .get('hiam.authorityManagement.model.authorityManagement.name', { name })
              .d(`${name}名称`),
            field: 'dataName',
          },
        ],
        queryUrl: `${HZERO_IAM}/v1/${organizationId}/${
          lodash.isUndefined(roleId) ? 'users' : 'role'
        }/${lodash.isUndefined(roleId) ? userId : roleId}/data/${
          code === 'valueList'
            ? 'lov'
            : code === 'dataSource'
            ? lodash.toLower(code)
            : lodash.toLower(typeCode)
        }${code === 'dataGroup' ? '' : 's'}`,
      },
      confirmLoading: addLoading,
      modalVisible: addModalVisible,
      onHideAddModal: this.onHideAddModal,
      addData: this.addValueList,
    };

    const rowSelection = isSecGrp
      ? null
      : {
          onChange: this.onSelectChange,
          selectedRowKeys: selectRows.map((n) => n.dataId),
        };

    return (
      <div>
        <div className="table-list-search">{this.renderForm()}</div>
        {!isSecGrp && (
          <div style={{ textAlign: 'right' }}>
            {!head.includeAllFlag && (
              <>
                <ButtonPermission style={{ margin: '0 8px 16px 0' }} onClick={this.onShowAddModal}>
                  {intl.get(`hiam.authority.view.button.table.create.valueList`).d('新建权限')}
                </ButtonPermission>
                <ButtonPermission
                  style={{ margin: '0 8px 16px 0' }}
                  disabled={selectRows.length <= 0}
                  onClick={() => this.remove()}
                >
                  {intl.get(`hiam.authority.view.button.table.delete.valueList`).d('删除权限')}
                </ButtonPermission>
              </>
            )}
            <div style={{ display: 'inline-block', margin: '0 8px 16px 0' }}>
              <span style={{ marginRight: '8px' }}>
                {intl.get('hiam.authority.view.message.label').d('加入全部:')}
              </span>
              <Tooltip
                title={intl
                  .get('hiam.authority.view.message.title.tooltip.vl')
                  .d('“加入全部”即将所有值集权限自动添加至当前账户，无需再手工添加。')}
                placement="right"
              >
                <Switch
                  loading={switchLoading || fetchLoading}
                  checked={!!head.includeAllFlag}
                  onChange={this.includeAllFlag}
                />
              </Tooltip>
            </div>
          </div>
        )}
        <Table
          bordered
          rowKey="dataId"
          loading={isSecGrp ? shieldLoading || fetchLoading : fetchLoading}
          dataSource={list}
          rowSelection={rowSelection}
          pagination={pagination}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={this.handleTableChange}
        />
        <AddDataModal {...addModalOptions} />
      </div>
    );
  }
}

function mapStateToProps(state, props) {
  const { loading } = state;
  const { code, roleId, isSecGrp = false, isAccount = false } = props;
  const temp = `${
    isSecGrp
      ? isAccount
        ? `trAccSecGrpAuthority`
        : `trSecGrpAuthority`
      : !lodash.isUndefined(roleId)
      ? 'trRoleDataAuthority'
      : 'trAuthority'
  }${lodash.upperFirst(code)}`;
  return {
    roleDataAuthority: state[temp],
    addLoading:
      loading.effects[
        `${!lodash.isUndefined(roleId) ? 'trRoleDataAuthority' : 'trAuthority'}${lodash.upperFirst(
          code
        )}/addAuthority${lodash.upperFirst(code)}`
      ],
    fetchLoading:
      loading.effects[
        `${
          isSecGrp
            ? isAccount
              ? `trAccSecGrpAuthority`
              : `trSecGrpAuthority`
            : !lodash.isUndefined(roleId)
            ? 'trRoleDataAuthority'
            : 'trAuthority'
        }${lodash.upperFirst(code)}/fetchAuthority${lodash.upperFirst(code)}`
      ],
    shieldLoading:
      loading.effects['trRoleManagement/shieldSecGrpPermission'] ||
      loading.effects['trRoleManagement/cancelShieldSecGrpPermission'],
  };
}

function mapDispatchToProps(dispatch) {
  return { dispatch };
}

export default connect(mapStateToProps, mapDispatchToProps)(AuthDetail);
