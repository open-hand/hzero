/**
 * Detail - 值集视图编辑界面
 * @date: 2018-6-26
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Table, Col, Row, Input, Modal, InputNumber, Card, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
  FORM_FIELD_CLASSNAME,
} from 'utils/constants';

import DetailForm from './DetailForm';

/**
 * lov维护
 * @extends {Component} - React.Component
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} lovSetting - 数据源
 * @reactProps {Object} fetchLineLoading - 数据加载是否完成
 * @reactProps {Object} saveHeadLoading - 数据保存加载是否完成
 * @reactProps {Object} addLineLoading - 数据详情加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ lovSetting, loading }) => ({
  lovSetting,
  currentTenantId: getCurrentOrganizationId(),
  fetchHeadLoading: loading.effects['lovSetting/fetchHead'],
  fetchLineLoading: loading.effects['lovSetting/fetchLine'],
  saveHeadLoading: loading.effects['lovSetting/saveHead'],
  addLineLoading: loading.effects['lovSetting/addLine'],
  isTenant: isTenantRoleLevel(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hpfm.lov', 'entity.tenant', 'hpfm.valueList'],
})
export default class Detail extends React.Component {
  /**
   *内部状态
   */
  state = {
    selectedRow: [],
    modalVisible: false,
    editRecordData: {},
  };

  /**
   * 组件挂载后执行方法
   */
  componentDidMount() {
    const { match, dispatch } = this.props;
    const data = {
      viewHeaderId: match.params.id,
    };
    dispatch({
      type: 'lovSetting/updateState',
      payload: {
        // head data
        headData: {},
        mainKey: {},
        // line data
        rowData: {},
      },
    });
    dispatch({
      type: 'lovSetting/fetchHead',
      payload: data,
    });
    dispatch({
      type: 'lovSetting/fetchLine',
      payload: {
        ...data,
      },
    });
    dispatch({
      type: 'lovSetting/queryValueList',
    });
  }

  /**
   *生成表头字段
   * @returns
   */
  @Bind()
  getColumns() {
    // op改造，通过层级控制平台级功能的使用，租户可编辑通用的功能
    // const { lovSetting: { headData = {} } } = this.props;
    // const { tenantId } = headData;
    // const notEditable = tenantId === 0 && isTenantRoleLevel();
    const notEditable = false;
    const { match } = this.props;
    return [
      {
        title: intl.get('hpfm.lov.model.lov.fieldName').d('表格字段名'),
        dataIndex: 'fieldName',
        align: 'left',
      },
      {
        title: intl.get('hpfm.lov.model.lov.display').d('表格列标题'),
        dataIndex: 'display',
      },
      {
        title: intl.get('hpfm.lov.model.lov.tableFieldWidth').d('列宽度'),
        dataIndex: 'tableFieldWidth',
        width: 100,
        align: 'center',
      },
      {
        title: intl.get('hpfm.lov.model.lov.orderSeq').d('列序号'),
        dataIndex: 'orderSeq',
        width: 80,
        align: 'center',
      },
      {
        title: intl.get('hpfm.lov.model.lov.dataType').d('字段类型'),
        dataIndex: 'dataTypeMeaning',
        width: 100,
        align: 'center',
      },
      {
        title: intl.get('hpfm.lov.model.lov.queryFieldFlag').d('查询字段'),
        dataIndex: 'queryFieldFlag',
        width: 100,
        align: 'center',
        render: yesOrNoRender,
      },
      {
        title: intl.get('hpfm.lov.model.lov.tableFieldFlag').d('表格列'),
        dataIndex: 'tableFieldFlag',
        width: 80,
        align: 'center',
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 80,
        dataIndex: 'enabledFlag',
        align: 'center',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.edit').d('编辑'),
        width: 80,
        align: 'center',
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: notEditable ? (
                <div style={{ color: '#999' }}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </div>
              ) : (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '值集视图配置-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showEditModal(true, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
  }

  /**
   * 删除行数据
   */
  @Bind()
  removeLineData() {
    const { dispatch } = this.props;
    const onOk = () => {
      dispatch({
        type: 'lovSetting/removeLineData',
        payload: this.state.selectedRow,
      }).then((response) => {
        if (response) {
          this.refreshLine();
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
   * 表格勾选
   * @param {null} _ 占位
   * @param {object} selectedRow 选中行
   */
  @Bind()
  onSelectChange(_, selectedRow) {
    this.setState({ selectedRow });
  }

  /**
   * 刷新行数据
   */
  @Bind()
  refreshLine() {
    const {
      dispatch,
      lovSetting: { mainKey = {} },
    } = this.props;
    const data = {
      viewHeaderId: mainKey.viewHeaderId,
    };
    dispatch({
      type: 'lovSetting/fetchLine',
      payload: data,
    }).then(() => {
      this.setState({
        selectedRow: [],
      });
    });
  }

  /**
   * 添加行数据
   * @param {object} fieldsValue 表单数据
   * @param {object} form 当前表单
   */
  @Bind()
  handleAdd(fieldsValue, form) {
    const {
      dispatch,
      lovSetting: { mainKey = {} },
    } = this.props;
    const data = {
      ...mainKey,
      ...fieldsValue,
      queryFieldFlag: fieldsValue.queryFieldFlag ? 1 : 0,
      tableFieldFlag: fieldsValue.tableFieldFlag ? 1 : 0,
      enabledFlag: fieldsValue.enabledFlag ? 1 : 0,
      tenantId: '0',
    };
    dispatch({
      type: 'lovSetting/addLine',
      payload: data,
      callback: () => {
        form.resetFields();
        this.refreshLine();
      },
    }).then(notification.success());
  }

  /**
   * 编辑
   * @param {object} fieldsValue 表单数据
   */
  @Bind()
  handleEditLineData(fieldsValue) {
    const {
      dispatch,
      lovSetting: { mainKey = {} },
    } = this.props;
    const { objectVersionNumber, viewLineId } = this.state.editRecordData;
    const data = {
      ...mainKey,
      ...fieldsValue,
      queryFieldFlag: fieldsValue.queryFieldFlag ? 1 : 0,
      tableFieldFlag: fieldsValue.tableFieldFlag ? 1 : 0,
      enabledFlag: fieldsValue.enabledFlag ? 1 : 0,
      viewLineId,
      objectVersionNumber,
    };
    dispatch({
      type: 'lovSetting/editLineData',
      payload: data,
      callback: this.refreshLine,
    }).then(notification.success());
  }

  /**
   * hook事件 传入modal中
   * @param {object} form 表单
   * @param {object} editRecordData 当前编辑状态时的数据
   */
  @Bind()
  formHook(form, editRecordData) {
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        if (!editRecordData.viewLineId) {
          this.handleAdd(fieldsValue, form);
          this.showEditModal(false);
        } else {
          this.handleEditLineData(fieldsValue);
          form.resetFields();
          this.showEditModal(false);
        }
      }
    });
  }

  /**
   * 控制modal的显隐
   * @param {boolean} flag 显/隐标记
   * @param {object} record 行数据
   * @param {object} form 表单
   */
  @Bind()
  showEditModal(flag, record) {
    if (record) {
      this.setState({
        modalVisible: !!flag,
        editRecordData: record,
      });
    } else {
      this.setState({
        modalVisible: !!flag,
        editRecordData: {},
      });
    }
  }

  /**
   * 保存lov头信息
   */
  @Bind()
  saveHead() {
    const {
      match,
      dispatch,
      form,
      lovSetting: { headData = {} },
    } = this.props;
    const headKey = {
      viewHeaderId: match.params.id,
    };
    const callback = () => {
      notification.success();
      dispatch({
        type: 'lovSetting/fetchHead',
        payload: headKey,
      });
    };
    form.validateFields((err, fieldsValue) => {
      const data = {
        ...headData,
        ...fieldsValue,
        // viewCode: lodash.trim(fieldsValue.viewCode), // viewCode 在编辑模式下 不可编辑
        delayLoadFlag: fieldsValue.delayLoadFlag ? 1 : 0,
        enabledFlag: fieldsValue.enabledFlag ? 1 : 0,
      };
      if (!err) {
        dispatch({
          type: 'lovSetting/saveHead',
          payload: data,
        }).then((response) => {
          if (response) {
            callback();
          }
        });
      }
    });
  }

  /**
   * 分页切换
   * @param {object} pagination 分页信息
   */
  @Bind()
  handleStandardTableChange(pagination = {}) {
    const {
      dispatch,
      lovSetting: { mainKey = {} },
    } = this.props;
    dispatch({
      type: 'lovSetting/fetchLine',
      payload: {
        page: pagination,
        viewHeaderId: mainKey.viewHeaderId,
      },
    });
  }

  /**
   * 渲染方法
   * @returns
   */
  render() {
    const {
      match,
      currentTenantId,
      lovSetting: { rowData, headData, dataTypeList = [] },
      fetchHeadLoading = false,
      fetchLineLoading = false,
      saveHeadLoading = false,
      addLineLoading = false,
      form,
      isTenant,
    } = this.props;
    const {
      viewCode,
      valueField,
      pageSize,
      viewName,
      displayField,
      title,
      height,
      delayLoadFlag,
      enabledFlag,
      lovId,
      lovName,
      childrenFieldName,
      tenantId,
      tenantName,
      _token,
    } = headData;
    const { modalVisible, editRecordData, selectedRow } = this.state;
    // 当前租户是否和数据中的租户对应
    const isNotCurrentTenant = tenantId !== undefined ? tenantId !== currentTenantId : false;
    const basePath = match.path.substring(0, match.path.indexOf('/lov-list'));
    const dataSource = rowData.list;
    const columns = this.getColumns();
    const rowSelection = {
      // selectedRow: this.state.selectedRow,
      onChange: this.onSelectChange,
      selectedRowKeys: selectedRow.map((n) => n.viewLineId),
    };
    const notEditable = false;
    return (
      <React.Fragment>
        <Header
          title={intl.get('hpfm.lov.view.message.title.lovSetting').d('值集视图配置')}
          backPath={`${basePath}/hpfm/lov-view/lov-view-list`}
        >
          {isTenant && isNotCurrentTenant ? null : (
            <ButtonPermission
              icon="save"
              type="primary"
              permissionList={[
                {
                  code: `${match.path}.button.save`,
                  type: 'button',
                  meaning: '值集视图配置-保存',
                },
              ]}
              loading={saveHeadLoading}
              disabled={fetchHeadLoading}
              onClick={this.saveHead}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Spin
            spinning={
              fetchHeadLoading || fetchLineLoading || addLineLoading || saveHeadLoading || false
            }
          >
            <Card
              key="lov-setting-header"
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>{intl.get('hpfm.valueList.lovSetting.title.lovSetting').d('值集视图')}</h3>
              }
            >
              <Form>
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.viewCode').d('视图代码')}
                    >
                      {viewCode}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.viewName').d('视图名称')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? viewName
                        : form.getFieldDecorator('viewName', {
                            initialValue: viewName,
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl.get('hpfm.lov.model.lov.viewName').d('视图名称'),
                                }),
                              },
                              {
                                max: 80,
                                message: intl.get('hzero.common.validation.max', {
                                  max: 80,
                                }),
                              },
                            ],
                          })(
                            <TLEditor
                              label={intl.get('hpfm.lov.model.lov.viewName').d('视图名称')}
                              field="viewName"
                              token={_token}
                            />
                          )}
                    </Form.Item>
                  </Col>
                  {!isTenant && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        {...EDIT_FORM_ITEM_LAYOUT}
                        label={intl.get('entity.tenant.name').d('租户名称')}
                      >
                        {tenantName}
                      </Form.Item>
                    </Col>
                  )}
                </Row>
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.valueField').d('值字段名')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? valueField
                        : form.getFieldDecorator('valueField', {
                            initialValue: valueField,
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl.get('hpfm.lov.model.lov.valueField').d('值字段名'),
                                }),
                              },
                              {
                                max: 30,
                                message: intl.get('hzero.common.validation.max', {
                                  max: 30,
                                }),
                              },
                            ],
                          })(<Input inputChinese={false} className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.displayField').d('显示字段名')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? displayField
                        : form.getFieldDecorator('displayField', {
                            initialValue: displayField,
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl.get('hpfm.lov.model.lov.displayField').d('显示字段名'),
                                }),
                              },
                              {
                                max: 30,
                                message: intl.get('hzero.common.validation.max', {
                                  max: 30,
                                }),
                              },
                            ],
                          })(<Input inputChinese={false} className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.lovId').d('值集')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? lovName
                        : form.getFieldDecorator('lovId', {
                            initialValue: lovId,
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl.get('hpfm.lov.model.lov.lovId').d('值集'),
                                }),
                              },
                            ],
                          })(
                            <Lov
                              textValue={lovName}
                              code={
                                isTenantRoleLevel()
                                  ? 'HPFM.LOV.LOV_DETAIL.ORG'
                                  : 'HPFM.LOV.LOV_DETAIL'
                              }
                              className={FORM_FIELD_CLASSNAME}
                              queryParams={
                                isTenant ? { lovQueryFlag: 1 } : { lovQueryFlag: 1, tenantId }
                              }
                            />
                          )}
                    </Form.Item>
                  </Col>
                </Row>
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.childrenFieldName').d('子字段名')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? childrenFieldName
                        : form.getFieldDecorator('childrenFieldName', {
                            initialValue: childrenFieldName,
                            rules: [
                              {
                                max: 30,
                                message: intl.get('hzero.common.validation.max', {
                                  max: 30,
                                }),
                              },
                            ],
                          })(<Input inputChinese={false} className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.title').d('标题')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? title
                        : form.getFieldDecorator('title', {
                            initialValue: title,
                            rules: [
                              {
                                max: 20,
                                message: intl.get('hzero.common.validation.max', {
                                  max: 20,
                                }),
                              },
                            ],
                          })(
                            <TLEditor
                              label={intl.get('hpfm.lov.model.lov.title').d('标题')}
                              field="title"
                              token={_token}
                            />
                          )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.pageSize').d('页大小')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? pageSize
                        : form.getFieldDecorator('pageSize', {
                            initialValue: pageSize,
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl.get('hpfm.lov.model.lov.pageSize').d('页大小'),
                                }),
                              },
                            ],
                          })(<InputNumber className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                </Row>
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.height').d('高度')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? height
                        : form.getFieldDecorator('height', {
                            initialValue: height,
                          })(<InputNumber className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.lov.model.lov.delayLoadFlag').d('加载延时')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? yesOrNoRender(delayLoadFlag === undefined ? 0 : delayLoadFlag)
                        : form.getFieldDecorator('delayLoadFlag', {
                            initialValue: delayLoadFlag === undefined ? 0 : delayLoadFlag,
                          })(<Switch />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hzero.common.status.enable').d('启用')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? enableRender(enabledFlag === undefined ? 0 : enabledFlag)
                        : form.getFieldDecorator('enabledFlag', {
                            initialValue: enabledFlag === undefined ? 0 : enabledFlag,
                          })(<Switch />)}
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
            </Card>
            <Card
              key="value-list-table"
              bordered={false}
              className={DETAIL_CARD_TABLE_CLASSNAME}
              title={
                <h3>
                  {intl.get('hpfm.valueList.lovSetting.title.lovSettingField').d('值集视图字段')}
                </h3>
              }
            >
              <div className="table-list-operator">
                {isTenant && isNotCurrentTenant ? null : (
                  <React.Fragment>
                    <ButtonPermission
                      type="primary"
                      permissionList={[
                        {
                          code: `${match.path}.button.add`,
                          type: 'button',
                          meaning: '值集视图配置-新增表格字段',
                        },
                      ]}
                      onClick={() => this.showEditModal(true)}
                    >
                      {intl.get('hpfm.lov.view.detail.button.create').d('新增表格字段')}
                    </ButtonPermission>
                    <ButtonPermission
                      permissionList={[
                        {
                          code: `${match.path}.button.delete`,
                          type: 'button',
                          meaning: '值集视图配置-删除表格字段',
                        },
                      ]}
                      onClick={this.removeLineData}
                      disabled={selectedRow.length <= 0}
                    >
                      {intl.get('hpfm.lov.view.detail.button.delete').d('删除表格字段')}
                    </ButtonPermission>
                  </React.Fragment>
                )}
              </div>
              <Table
                rowKey="viewLineId"
                dataSource={dataSource}
                columns={columns}
                bordered
                rowSelection={notEditable ? false : rowSelection}
                pagination={rowData.pagination || {}}
                onChange={this.handleStandardTableChange}
              />
            </Card>
          </Spin>
          <DetailForm
            title={
              editRecordData.fieldName !== undefined
                ? intl.get('hpfm.lov.view.message.editTableRecord').d('编辑表格字段')
                : intl.get('hpfm.lov.view.message.createTableRecord').d('新建表格字段')
            }
            formHook={this.formHook}
            showEditModal={this.showEditModal}
            dataTypeList={dataTypeList}
            isTenant={isTenant}
            isNotCurrentTenant={isNotCurrentTenant}
            modalVisible={modalVisible}
            editRecordData={editRecordData}
            loading={addLineLoading}
          />
        </Content>
      </React.Fragment>
    );
  }
}
