/**
 * DimensionConfig - 数据维度配置 - 维度数据定义
 * @date: 2019-7-17
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Card, Col, Form, Modal, Row, Table } from 'hzero-ui';
import { connect } from 'dva';
import moment from 'moment';
import classnames from 'classnames';
import queryString from 'query-string';

import Switch from 'components/Switch';
import { Content, Header } from 'components/Page';
import TLEditor from 'components/TLEditor';
import { Button as ButtonPermission } from 'components/Permission';

import { dateRender, enableRender, operatorRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  DEFAULT_DATETIME_FORMAT,
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import notification from 'utils/notification';
import { createPagination, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import DetailForm from './DetailForm';

@connect(({ dimensionConfig, loading }) => ({
  dimensionConfig,
  loading: loading.effects['dimensionConfig/queryLovValues'],
  saving: loading.effects['dimensionConfig/saveLovValues'],
  saveHeaderLoading: loading.effects['dimensionConfig/saveLovHeaders'],
  tenantId: getCurrentOrganizationId(),
  isTenant: isTenantRoleLevel(),
}))
@formatterCollections({
  code: ['hpfm.valueList', 'hpfm.common', 'hpfm.dimensionConfig', 'hpfm.dimension'],
})
@Form.create({ fieldNameProp: null })
export default class EditForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      valueListHeader: {},
      valueListLine: {},
      editValue: {},
      selectedRows: [],
    };
  }

  componentDidMount() {
    const { dispatch, match, tenantId } = this.props;
    const { lovId } = match.params;
    dispatch({
      type: 'dimensionConfig/queryLovHeader',
      payload: {
        lovId: lovId.indexOf('?') === -1 ? lovId : lovId.substring(0, lovId.indexOf('?')),
        tenantId,
      },
    }).then((res) => {
      if (res) {
        this.setState({
          valueListHeader: res,
          editValue: {
            parentTenantId: res.parentTenantId,
            parentLovCode: res.parentLovCode,
          },
        });
        if (res.lovTypeCode === 'IDP') {
          this.loadValueListLine(res.lovId);
        }
      }
    });
  }

  /**
   * 添加或保存值集
   * @param {Object} fieldsValue - 值集详情编辑表单值
   */
  @Bind()
  handleAdd(fieldsValue) {
    const { dispatch } = this.props;
    const { editValue, valueListHeader } = this.state;
    const { lovId, lovCode, tenantId } = valueListHeader;
    let { startDateActive, endDateActive } = fieldsValue;
    startDateActive = startDateActive
      ? moment(startDateActive).format(DEFAULT_DATETIME_FORMAT)
      : '';
    endDateActive = startDateActive ? moment(endDateActive).format(DEFAULT_DATETIME_FORMAT) : '';

    dispatch({
      type: 'dimensionConfig/saveLovValues',
      payload: {
        ...editValue,
        ...fieldsValue,
        startDateActive,
        endDateActive,
        lovId:
          lovId.toString().indexOf('?') === -1
            ? lovId
            : lovId.substring(0, lovId.toString().indexOf('?')),
        lovCode,
        tenantId,
      },
    }).then((response) => {
      if (response) {
        this.hideModal();
        this.loadValueListLine(
          lovId.toString().indexOf('?') === -1
            ? lovId
            : lovId.substring(0, lovId.toString().indexOf('?'))
        );
        notification.success();
      }
    });
  }

  /**
   * 显示当前值集侧边栏编辑
   * @param {Object} record - 当前行数据
   */
  @Bind()
  showEditModal(record) {
    const { parentLovCode } = this.state.valueListHeader;
    this.setState(
      {
        editValue: {
          ...record,
          parentLovCode,
        },
      },
      this.showModal()
    );
  }

  /**
   * 显示侧边栏
   */
  @Bind()
  showModal() {
    this.handleModalVisible(true);
  }

  /**
   * 隐藏侧边栏
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.handleModalVisible(false);
    }
  }

  /**
   * 侧边栏显示控制函数
   * @param {Boolean} flag - 侧边栏显示控制参数
   */
  handleModalVisible(flag) {
    const { parentLovCode } = this.state.valueListHeader;

    if (flag === false) {
      this.setState({
        editValue: {
          parentLovCode,
        },
      });
    }
    this.setState({
      modalVisible: !!flag,
    });
  }

  /**
   * 根据值集头查询值集行
   * @param {Number} headerId - 值集头 id
   */
  loadValueListLine(headerId) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'dimensionConfig/queryLovValues',
      payload: {
        tenantId,
        lovId: headerId,
        page: 0,
        size: 10,
      },
    }).then((res) => {
      if (res) {
        this.setState({
          valueListLine: res,
        });
      }
    });
  }

  /**
   * 值集行分页查询
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handleTableChange(pagination) {
    const { dispatch, match, tenantId } = this.props;
    const { lovId } = match.params;

    const params = {
      tenantId,
      lovId: lovId.indexOf('?') === -1 ? lovId : lovId.substring(0, lovId.indexOf('?')),
      page: pagination.current - 1,
      size: pagination.pageSize,
    };

    dispatch({
      type: 'dimensionConfig/queryLovValues',
      payload: params,
    }).then((res) => {
      if (res) {
        this.setState({
          valueListLine: res,
        });
      }
    });
  }

  /**
   * 保存维度定义头
   */
  @Bind()
  handleSaveLovHeader() {
    const { form, dispatch, tenantId } = this.props;
    const { valueListHeader } = this.state;

    form.validateFields((err, fieldsValue) => {
      if (err) return;

      // 更新或插入值集头参数
      const params = {
        tenantId,
        ...valueListHeader,
        ...fieldsValue,
      };

      dispatch({
        type: 'dimensionConfig/saveLovHeaders',
        payload: params,
      }).then((res) => {
        if (res) {
          notification.success();
          this.setState({
            valueListHeader: res,
            editValue: {
              parentLovCode: res.parentLovCode,
            },
          });
          if (res.lovTypeCode === 'IDP') {
            this.loadValueListLine(res.lovId);
          }
        }
      });
    });
  }

  /**
   * 值集行勾选回调
   * @param {*} _
   * @param {*} selectedRows - 勾选的值集行
   */
  @Bind()
  handleSelectRows(_, selectedRows) {
    this.setState({
      selectedRows,
    });
  }

  /**
   * 取消编辑返回值集列表
   */
  @Bind()
  handleCancel() {
    const {
      history,
      location: { search, pathname },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    history.push(
      pathname.indexOf('/private') === 0
        ? `/private/hpfm/data-dimension-config/list?access_token=${accessToken}`
        : `/hpfm/data-dimension-config/list`
    );
  }

  /**
   * 删除值集
   */
  @Bind()
  handleDeleteValues() {
    const { dispatch, match, tenantId } = this.props;
    const { selectedRows } = this.state;
    // 删除后刷新传参
    const { lovId } = match.params;

    const onOk = () => {
      dispatch({
        type: 'dimensionConfig/deleteLovValues',
        payload: { tenantId, deleteRows: selectedRows },
      }).then(() => {
        this.loadValueListLine(
          lovId.indexOf('?') === -1 ? lovId : lovId.substring(0, lovId.indexOf('?'))
        );
        this.setState({
          selectedRows: [],
        });
        notification.success();
      });
    };

    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  }

  render() {
    const {
      match,
      saving,
      loading,
      location: { search, pathname },
      form: { getFieldDecorator },
      tenantId: currentTenantId,
      saveHeaderLoading = false,
      isTenant,
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const {
      modalVisible,
      valueListHeader = {},
      selectedRows,
      valueListLine,
      editValue,
    } = this.state;
    const { _token, tenantId } = valueListHeader;
    // op改造，通过层级控制平台级功能的使用，租户可编辑通用的功能
    // const tenantDisable = !valueListHeader.tenantId && !!isTenantRoleLevel();
    const tenantDisable = false;
    // 当前租户是否和数据中的租户对应
    const isNotCurrentTenant = tenantId !== undefined ? tenantId !== currentTenantId : false;
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.lovValueId),
      onChange: this.handleSelectRows,
    };
    const columns = [
      {
        title: intl.get('hpfm.dimensionConfig.model.line.value').d('维度值'),
        width: 100,
        dataIndex: 'value',
      },
      {
        title: intl.get('hpfm.dimensionConfig.model.line.meaning').d('维度值含义'),
        width: 100,
        dataIndex: 'meaning',
      },
      {
        title: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
        width: 100,
        dataIndex: 'orderSeq',
      },
      {
        title: intl.get('hpfm.dimensionConfig.model.line.parentValue').d('父级维度值'),
        width: 100,
        dataIndex: 'parentMeaning',
      },
      {
        title: intl.get('hpfm.valueList.model.line.tag').d('标记'),
        dataIndex: 'tag',
      },
      {
        title: intl.get('hpfm.valueList.model.line.description').d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hpfm.valueList.model.line.startDateActive').d('有效期起'),
        width: 100,
        dataIndex: 'startDateActive',
        render: dateRender,
      },
      {
        title: intl.get('hpfm.valueList.model.line.endDateActive').d('有效期止'),
        width: 100,
        dataIndex: 'endDateActive',
        render: dateRender,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        render: (_, record) => {
          const operators = [];
          if (tenantDisable) {
            operators.push({
              key: 'edit',
              ele: (
                <span style={{ color: '#aaa' }}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </span>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          } else {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '数据维度定义-编辑',
                    },
                  ]}
                  onClick={() => this.showEditModal(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ];

    const basePath = match.path.substring(0, match.path.indexOf('/detail'));

    return (
      <>
        <Header
          title={intl.get('hpfm.dimensionConfig.view.title.valueDetail').d('维度数据定义')}
          backPath={
            pathname.indexOf('/private') === 0
              ? `${basePath}/list?access_token=${accessToken}`
              : `${basePath}/list`
          }
        >
          {isTenant && isNotCurrentTenant ? null : (
            <>
              <ButtonPermission
                type="primary"
                icon="save"
                permissionList={[
                  {
                    code: `${match.path}.button.save`,
                    type: 'button',
                    meaning: '数据维度定义-保存',
                  },
                ]}
                onClick={this.handleSaveLovHeader}
                loading={saveHeaderLoading}
              >
                {intl.get('hzero.common.button.save').d('保存')}
              </ButtonPermission>
              <ButtonPermission
                icon="rollback"
                permissionList={[
                  {
                    code: `${match.path}.button.cancel`,
                    type: 'button',
                    meaning: '数据维度定义-取消',
                  },
                ]}
                onClick={this.handleCancel}
              >
                {intl.get('hzero.common.button.cancel').d('取消')}
              </ButtonPermission>
            </>
          )}
        </Header>
        <Content>
          <Card
            key="value-list-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hpfm.dimensionConfig.view.title.dimension').d('维度定义')}</h3>}
            loading={loading}
          >
            <Form>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={classnames({
                  'read-row': !isTenant && tenantId === currentTenantId,
                  'inclusion-row': !isTenant || tenantId === currentTenantId,
                })}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hpfm.dimensionConfig.model.header.dimensionCode')
                      .d('维度代码')}
                  >
                    {valueListHeader.lovCode}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hpfm.dimensionConfig.model.header.dimensionName')
                      .d('维度名称')}
                  >
                    {isTenant && tenantId !== currentTenantId
                      ? valueListHeader.lovName
                      : getFieldDecorator('lovName', {
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hpfm.dimensionConfig.model.header.dimensionName')
                                  .d('维度名称'),
                              }),
                            },
                          ],
                          initialValue: valueListHeader.lovName,
                        })(
                          <TLEditor
                            label={intl
                              .get('hpfm.dimensionConfig.model.header.dimensionName')
                              .d('维度名称')}
                            field="lovName"
                            token={_token}
                          />
                        )}
                  </Form.Item>
                </Col>
                {!isTenant && (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.valueList.model.header.tenantName').d('所属租户')}
                    >
                      {valueListHeader.tenantName}
                    </Form.Item>
                  </Col>
                )}
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={isTenant && isNotCurrentTenant ? 'read-row' : 'inclusion-row'}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.dimension.model.header.parentDimension').d('父级维度')}
                  >
                    {valueListHeader.parentLovName}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.status').d('状态')}
                  >
                    {isTenant && isNotCurrentTenant
                      ? enableRender(valueListHeader.enabledFlag)
                      : getFieldDecorator('enabledFlag', {
                          initialValue: valueListHeader.enabledFlag,
                        })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={isTenant && isNotCurrentTenant ? 'read-half-row' : 'half-row'}
              >
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_2}
                    label={intl.get('hpfm.valueList.model.header.description').d('描述')}
                  >
                    {isTenant && isNotCurrentTenant
                      ? valueListHeader.description
                      : getFieldDecorator('description', {
                          initialValue: valueListHeader.description,
                        })(
                          <TLEditor
                            label={intl.get('hpfm.valueList.model.header.description').d('描述')}
                            field="description"
                            token={_token}
                          />
                        )}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="value-list-line"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={
              <h3>{intl.get('hpfm.dimensionConfig.view.title.dataDefinition').d('维值定义')}</h3>
            }
          >
            {isTenant && isNotCurrentTenant ? null : (
              <div className="table-list-operator">
                <ButtonPermission
                  type="primary"
                  permissionList={[
                    {
                      code: `${match.path}.button.create`,
                      type: 'button',
                      meaning: '数据维度定义-新建',
                    },
                  ]}
                  onClick={this.showModal}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </ButtonPermission>
                <ButtonPermission
                  disabled={!this.state.selectedRows.length}
                  permissionList={[
                    {
                      code: `${match.path}.button.delete`,
                      type: 'button',
                      meaning: '数据维度定义-删除',
                    },
                  ]}
                  onClick={this.handleDeleteValues}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              </div>
            )}
            <Table
              bordered
              rowSelection={rowSelection}
              loading={loading}
              rowKey="lovValueId"
              dataSource={valueListLine.content}
              columns={columns}
              pagination={createPagination(valueListLine)}
              onChange={this.handleTableChange}
            />
          </Card>
        </Content>
        <DetailForm
          title={
            editValue.lovValueId
              ? intl.get('hpfm.dimensionConfig.view.title.editForm').d('编辑维值')
              : intl.get('hpfm.dimensionConfig.view.title.createForm').d('创建维值')
          }
          editValue={editValue}
          saveLoading={saving}
          modalVisible={modalVisible}
          onOk={this.handleAdd}
          onCancel={this.hideModal}
        />
      </>
    );
  }
}
