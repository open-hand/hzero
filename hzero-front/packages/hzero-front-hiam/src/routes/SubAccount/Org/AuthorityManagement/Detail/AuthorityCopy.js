/**
 * AuthorityCopy - 租户级权限维护tab页 - 权限复制
 * @date: 2018-7-31
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Input, message, Modal, Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import uuidv4 from 'uuid/v4';

import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';

import { queryLov } from 'services/api';

import { operatorRender } from 'utils/renderer';
import AddDataLov from './AddDataLov';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;

/**
 * 租户级权限管理 - 权限复制
 * @extends {Component} - React.Component
 * @reactProps {Object} authorityManagement - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
@connect(({ authorityManagement, loading }) => ({
  authorityManagement,
  loading: loading.models.authorityManagement,
}))
export default class AuthorityCopy extends React.Component {
  /**
   *Creates an instance of AuthorityCopy.
   * @param {Object} props 属性
   */
  constructor(props) {
    super(props);
    this.PageSize = 10;
    this.dataSourceRef = React.createRef();
    this.state = {
      newCreateRows: [],
      addModalVisible: false,
    };
  }

  /**
   *增加一行
   */
  @Bind()
  addData(record) {
    const arr = record.map(item => {
      const authorityLineId = `authorityLineId${uuidv4()}`;
      const data = {
        authorityLineId,
        isCreate: true,
        isEditing: true,
      };
      return { ...data, ...item };
    });

    this.setState({
      newCreateRows: [...this.state.newCreateRows, ...arr],
      addModalVisible: false,
    });
  }

  /**
   *行取消事件
   *
   * @param {Object} record 行数据
   */
  @Bind()
  cancel(record) {
    const { dispatch } = this.props;
    const { newCreateRows } = this.state;
    if (record.isCreate) {
      const listData = newCreateRows.filter(
        item => item.authorityLineId !== record.authorityLineId
      );
      this.setState({
        newCreateRows: listData,
      });
      dispatch({
        type: 'authorityManagement/removeNewAdd',
        payload: {},
      });
    } else {
      this.edit(record, false);
    }
  }

  /**
   *编辑事件
   *
   * @param {Object} record 行数据
   * @param {Boolean} flag 是否编辑状态标记
   */
  @Bind()
  edit(record = {}, flag) {
    const {
      dispatch,
      authorityManagement: { data = {} },
    } = this.props;
    const index = data.list.findIndex(item => item.authorityLineId === record.authorityLineId);
    dispatch({
      type: 'authorityManagement/editRow',
      payload: {
        data: [
          ...data.slice(0, index),
          {
            ...record,
            isEditing: flag,
          },
          ...data.slice(index + 1),
        ],
      },
    });
  }

  /**
   *保存数据
   */
  @Bind()
  dataSave() {
    const { form, dispatch, userId, refresh, authorityCopy } = this.props;
    const { newCreateRows } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const arr = [];
        const isNewRowKeys = newCreateRows.filter(v => v.isEditing);
        isNewRowKeys.forEach(item => {
          arr.push(values[`${item.authorityLineId}#dataId`]);
        });
        dispatch({
          type: 'authorityManagement/copyAuthority',
          payload: {
            userId,
            copyUserIdList: arr,
          },
        }).then(response => {
          if (response) {
            notification.success();
            if (authorityCopy) {
              this.setState({ newCreateRows: [] });
              authorityCopy();
            }
            refresh();
          }
        });
      }
    });
  }

  /**
   *lov选中后渲染同行其他数据
   *
   * @param {Object} lovRecord
   * @param {Object} tableRecord
   */
  @Bind()
  setDataCode(lovRecord = {}, tableRecord = {}) {
    this.props.form.setFieldsValue({
      [`${tableRecord.authorityLineId}#dataCode`]: lovRecord.realName,
    });
  }

  /**
   * 展示弹出框
   */
  @Bind()
  onShowAddModal() {
    queryLov({ viewCode: 'HIAM.USER_AUTHORITY_USER' }).then(res => {
      const lovInfo = { ...res };
      if (!isEmpty(lovInfo)) {
        const { viewCode: hasCode } = lovInfo;
        if (hasCode) {
          this.setState({ lov: lovInfo });
        } else {
          message.error(
            intl.get('hzero.common.components.lov.notification.undefined').d('值集视图未定义!')
          );
        }
        this.setState({ addModalVisible: true });
      }
    });
  }

  /**
   * 隐藏弹出框
   */
  @Bind()
  onHideAddModal() {
    this.dataSourceRef.current.state.addRows = [];
    this.setState({ addModalVisible: false });
  }

  /**
   *渲染事件
   *
   * @returns
   */
  render() {
    const { loading, organizationId, userId, path, copyModalVisible, authorityCopy } = this.props;
    const { newCreateRows = [], addModalVisible, lov } = this.state;
    const { getFieldDecorator } = this.props.form;
    const columns = [
      {
        title: intl.get('hiam.authority.model.authorityManagement.dataName').d('账号'),
        dataIndex: 'dataId',
        width: 200,
        render: (text, tableRecord) =>
          tableRecord.isEditing ? (
            <FormItem>
              {getFieldDecorator(`${tableRecord.authorityLineId}#dataId`, {
                initialValue: tableRecord.dataId,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.authority.model.authorityManagement.dataName').d('账号'),
                    }),
                  },
                ],
              })(
                <Lov
                  code="HIAM.USER_AUTHORITY_USER"
                  queryParams={{ organizationId, userId }}
                  textValue={tableRecord.dataName}
                  onChange={(_, record) => this.setDataCode(record, tableRecord)}
                />
              )}
            </FormItem>
          ) : (
            <div>{text}</div>
          ),
      },
      {
        title: intl.get('hiam.authority.model.authorityManagement.dataCode').d('名称'),
        dataIndex: 'dataCode',
        render: (text, record) =>
          record.isEditing ? (
            <FormItem>
              {getFieldDecorator(`${record.authorityLineId}#dataCode`, {
                initialValue: record.dataCode,
              })(<Input disabled />)}
            </FormItem>
          ) : (
            <div>{text}</div>
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 85,
        render: (_, record) => {
          const operators = [
            {
              key: 'cancel',
              ele: (
                <Popconfirm
                  title={intl.get('hiam.authority.view.message.confirmDelete').d('确认取消？')}
                  onConfirm={() => this.cancel(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.cancelAuthority`,
                        type: 'button',
                        meaning: '权限维护-取消权限复制',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.cancel').d('取消')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];

    const addModalOptions = {
      rowKey: 'userId',
      lov,
      queryParams: { userId, organizationId },
      modalVisible: addModalVisible,
      onHideAddModal: this.onHideAddModal,
      addData: this.addData,
      ref: this.dataSourceRef,
    };
    return (
      <Modal
        title={intl.get('hiam.authority.view.button.copy').d('权限复制')}
        visible={copyModalVisible}
        onCancel={() => {
          this.setState({ newCreateRows: [] });
          authorityCopy(false);
        }}
        width={600}
        footer={null}
      >
        <div>
          <div>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.createAuthority`,
                  type: 'button',
                  meaning: '权限维护-新建权限复制',
                },
              ]}
              icon="plus"
              style={{ margin: '0 10px 10px 0' }}
              onClick={() => this.onShowAddModal()}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.saveAuthority`,
                  type: 'button',
                  meaning: '权限维护-保存权限复制',
                },
              ]}
              icon="save"
              type="primary"
              onClick={() => this.dataSave()}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          </div>
          <div>
            {intl
              .get('hiam.authority.view.message.title.authorityCopy')
              .d('权限复制操作会将当前用户的权限一键添加至其他用户，请谨慎操作！')}
          </div>
          <Table
            bordered
            rowKey="authorityLineId"
            pagination={false}
            loading={loading}
            dataSource={newCreateRows}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
          />
        </div>
        <AddDataLov {...addModalOptions} />
      </Modal>
    );
  }
}
