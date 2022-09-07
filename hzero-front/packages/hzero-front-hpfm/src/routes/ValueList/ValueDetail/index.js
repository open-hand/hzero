import React, { Component } from 'react';
import { Button, Card, Col, Form, Input, Modal, Row, Table, Icon, Tooltip, Select } from 'hzero-ui';
import { connect } from 'dva';
import moment from 'moment';
import classnames from 'classnames';
import queryString from 'querystring';

import Switch from 'components/Switch';
import { Content, Header } from 'components/Page';
import TLEditor from 'components/TLEditor';
import { Button as ButtonPermission } from 'components/Permission';

import { dateRender, enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
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
  FORM_FIELD_CLASSNAME,
  FORM_COL_3_4_LAYOUT,
  FORM_COL_4_LAYOUT,
} from 'utils/constants';
import notification from 'utils/notification';
import {
  createPagination,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  getRefFormData,
} from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';
import { openTab } from 'utils/menuTab';

import DetailForm from './DetailForm';
import FilterForm from './FilterForm';

const { TextArea } = Input;
const { Option } = Select;
@connect(({ valueList, loading }) => ({
  valueList,
  loading: loading.effects['valueList/queryLovValues'],
  saving: loading.effects['valueList/saveLovValues'],
  saveHeaderLoading: loading.effects['valueList/saveLovHeaders'],
  tenantId: getCurrentOrganizationId(),
  isTenant: isTenantRoleLevel(),
}))
@formatterCollections({
  code: ['hpfm.valueList', 'hpfm.common'],
})
@Form.create({ fieldNameProp: null })
export default class EditForm extends Component {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
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
      type: 'valueList/queryLovHeadersList',
      payload: { tenantId },
    });
    dispatch({
      type: 'valueList/queryLovHeader',
      payload: { lovId, tenantId },
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
    startDateActive = startDateActive && moment(startDateActive).format(DEFAULT_DATETIME_FORMAT);
    endDateActive = endDateActive && moment(endDateActive).format(DEFAULT_DATETIME_FORMAT);
    dispatch({
      type: 'valueList/saveLovValues',
      payload: {
        ...editValue,
        ...fieldsValue,
        startDateActive,
        endDateActive,
        lovId,
        lovCode,
        tenantId,
      },
    }).then((response) => {
      if (response) {
        this.hideModal();
        this.loadValueListLine(lovId);
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
    const { parentLovCode, parentTenantId } = this.state.valueListHeader;
    this.setState(
      {
        editValue: {
          ...record,
          parentLovCode,
          parentTenantId,
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
    const { parentLovCode, parentTenantId } = this.state.valueListHeader;

    if (flag === false) {
      this.setState({
        editValue: {
          parentTenantId,
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
    const fieldsValue = getRefFormData(this.filterFormRef);
    dispatch({
      type: 'valueList/queryLovValues',
      payload: {
        tenantId,
        lovId: headerId,
        page: 0,
        size: 10,
        ...fieldsValue,
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
      lovId,
      page: pagination.current - 1,
      size: pagination.pageSize,
    };

    dispatch({
      type: 'valueList/queryLovValues',
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
   * 保存值集头
   */
  @Bind()
  handleLovHeader() {
    const { form, dispatch, history, tenantId } = this.props;
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
        type: 'valueList/saveLovHeaders',
        payload: params,
      }).then((res) => {
        if (res) {
          notification.success();
          this.setState({
            valueListHeader: res,
            editValue: {
              parentTenantId: res.parentTenantId,
              parentLovCode: res.parentLovCode,
            },
          });
          if (res.lovTypeCode === 'IDP') {
            this.loadValueListLine(res.lovId);
          } else {
            history.push(`/hpfm/value-list/list`);
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
    const { history } = this.props;
    history.push(`/hpfm/value-list/list`);
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
        type: 'valueList/deleteLovValues',
        payload: { tenantId, deleteRows: selectedRows },
      }).then(() => {
        this.loadValueListLine(lovId);
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

  @Bind()
  handleImport() {
    const {
      valueListHeader: { lovId },
    } = this.state;
    const args = {
      lovId,
    };
    openTab({
      key: `/hpfm/prompt/import-data/HPFM.LOV_VALUE`,
      title: 'hpfm.valueList.button.import',
      search: queryString.stringify({
        action: 'hpfm.valueList.button.import',
        args: JSON.stringify(args),
      }),
    });
  }

  render() {
    const {
      match,
      saving,
      loading,
      form: { getFieldDecorator },
      tenantId: currentTenantId,
      saveHeaderLoading = false,
      valueList: { requestMethods = [] },
      isTenant,
    } = this.props;
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
    const { lovId } = match.params;
    const columns = [
      {
        title: intl.get('hpfm.valueList.model.line.value').d('值'),
        width: 100,
        dataIndex: 'value',
      },
      {
        title: intl.get('hpfm.valueList.model.line.meaning').d('含义'),
        width: 100,
        dataIndex: 'meaning',
      },
      {
        title: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
        width: 100,
        dataIndex: 'orderSeq',
      },
      {
        title: intl.get('hpfm.valueList.model.line.parentValue').d('父级值集值'),
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
          const operators = [
            {
              key: 'edit',
              ele: tenantDisable ? (
                <span style={{ color: '#aaa' }}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </span>
              ) : (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '值集数据定义-编辑',
                    },
                  ]}
                  onClick={() => this.showEditModal(record)}
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

    const basePath = match.path.substring(0, match.path.indexOf('/detail'));

    return (
      <React.Fragment>
        <Header
          title={intl.get('hpfm.valueList.view.title.valueDetail').d('值集数据定义')}
          backPath={`${basePath}/list`}
        >
          {isTenant && isNotCurrentTenant ? null : (
            <React.Fragment>
              <ButtonPermission
                type="primary"
                icon="save"
                permissionList={[
                  {
                    code: `${match.path}.button.save`,
                    type: 'button',
                    meaning: '值集数据定义-保存',
                  },
                ]}
                onClick={this.handleLovHeader}
                loading={saveHeaderLoading}
              >
                {intl.get('hzero.common.button.save').d('保存')}
              </ButtonPermission>
              <Button icon="rollback" onClick={this.handleCancel}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </Button>
            </React.Fragment>
          )}
        </Header>
        <Content>
          <Card
            key="value-list-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hpfm.valueList.view.title.head').d('值集')}</h3>}
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
                    label={intl.get('hpfm.valueList.model.header.lovCode').d('值集编码')}
                  >
                    {valueListHeader.lovCode}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.valueList.model.header.lovName').d('值集名称')}
                  >
                    {isTenant && tenantId !== currentTenantId
                      ? valueListHeader.lovName
                      : getFieldDecorator('lovName', {
                          initialValue: valueListHeader.lovName,
                          rules: [
                            {
                              max: 240,
                              message: intl.get('hzero.common.validation.max', {
                                max: 240,
                              }),
                            },
                          ],
                        })(
                          <TLEditor
                            label={intl.get('hpfm.valueList.model.header.lovName').d('值集名称')}
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
                    label={intl.get('hpfm.valueList.model.header.lovTypeCode').d('值集类型')}
                  >
                    {valueListHeader.lovTypeMeaning}
                  </Form.Item>
                </Col>
                {valueListHeader.lovTypeCode === 'URL' || valueListHeader.lovTypeCode === 'SQL' ? (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.valueList.model.header.routeName').d('目标路由名')}
                    >
                      {valueListHeader.routeName}
                    </Form.Item>
                  </Col>
                ) : (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.valueList.model.header.parentLovCode').d('父级值集')}
                    >
                      {valueListHeader.parentLovName}
                    </Form.Item>
                  </Col>
                )}
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
                className={isTenant && isNotCurrentTenant ? 'read-row' : 'inclusion-row'}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item {...EDIT_FORM_ITEM_LAYOUT} label="valueField">
                    {isTenant && isNotCurrentTenant
                      ? valueListHeader.valueField
                      : getFieldDecorator('valueField', {
                          initialValue: valueListHeader.valueField,
                          rules: [
                            {
                              max: 30,
                              message: intl.get('hzero.common.validation.max', {
                                max: 30,
                              }),
                            },
                          ],
                        })(<Input className={FORM_FIELD_CLASSNAME} />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item {...EDIT_FORM_ITEM_LAYOUT} label="displayField">
                    {isTenant && isNotCurrentTenant
                      ? valueListHeader.displayField
                      : getFieldDecorator('displayField', {
                          initialValue: valueListHeader.displayField,
                          rules: [
                            {
                              max: 30,
                              message: intl.get('hzero.common.validation.max', {
                                max: 30,
                              }),
                            },
                          ],
                        })(<Input className={FORM_FIELD_CLASSNAME} />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.valueList.model.header.mustPageFlag').d('是否分页')}
                  >
                    {isTenant && isNotCurrentTenant
                      ? yesOrNoRender(valueListHeader.mustPageFlag)
                      : getFieldDecorator('mustPageFlag', {
                          initialValue: valueListHeader.mustPageFlag,
                        })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row
                {...EDIT_FORM_ROW_LAYOUT}
                className={
                  // eslint-disable-next-line no-nested-ternary
                  valueListHeader.lovTypeCode === 'IDP' || valueListHeader.lovTypeCode === 'URL'
                    ? isTenant && isNotCurrentTenant
                      ? 'read-row'
                      : 'inclusion-row'
                    : isTenant && isNotCurrentTenant
                    ? 'read-half-row'
                    : 'half-row'
                }
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.valueList.model.header.publicFlag').d('是否公开')}
                  >
                    {isTenant && isNotCurrentTenant
                      ? yesOrNoRender(valueListHeader.publicFlag)
                      : getFieldDecorator('publicFlag', {
                          initialValue:
                            valueListHeader.lovTypeCode === 'IDP' ? 1 : valueListHeader.publicFlag,
                        })(<Switch disabled={valueListHeader.lovTypeCode === 'IDP'} />)}
                  </Form.Item>
                </Col>
                {valueListHeader.lovTypeCode === 'URL' && (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hpfm.valueList.model.header.requestMethod').d('请求方式')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? valueListHeader.requestMethod
                        : getFieldDecorator('requestMethod', {
                            initialValue: valueListHeader.requestMethod || 'GET',
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl
                                    .get('hpfm.valueList.model.header.requestMethod')
                                    .d('请求方式'),
                                }),
                              },
                            ],
                          })(
                            <Select style={{ width: '100%' }}>
                              {requestMethods.map((item) => (
                                <Option value={item.value} key={item.value}>
                                  {item.meaning}
                                </Option>
                              ))}
                            </Select>
                          )}
                    </Form.Item>
                  </Col>
                )}
                <Col
                  span={
                    valueListHeader.lovTypeCode === 'IDP' || valueListHeader.lovTypeCode === 'URL'
                      ? 8
                      : 12
                  }
                >
                  <Form.Item
                    {...(valueListHeader.lovTypeCode === 'IDP' ||
                    valueListHeader.lovTypeCode === 'URL'
                      ? EDIT_FORM_ITEM_LAYOUT
                      : EDIT_FORM_ITEM_LAYOUT_COL_2)}
                    label={intl.get('hpfm.valueList.model.header.description').d('描述')}
                  >
                    {isTenant && isNotCurrentTenant
                      ? valueListHeader.description
                      : getFieldDecorator('description', {
                          initialValue: valueListHeader.description,
                          rules: [
                            {
                              max: 480,
                              message: intl.get('hzero.common.validation.max', {
                                max: 480,
                              }),
                            },
                          ],
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
              {valueListHeader.lovTypeCode === 'URL' ? (
                <Row
                  {...EDIT_FORM_ROW_LAYOUT}
                  className={isTenant && isNotCurrentTenant ? 'read-half-row' : 'half-row'}
                >
                  <Col {...FORM_COL_2_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT_COL_2}
                      label={intl.get('hpfm.valueList.model.header.customUrl').d('查询 URL')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? valueListHeader.customUrl || ''
                        : getFieldDecorator('customUrl', {
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl
                                    .get('hpfm.valueList.model.header.customUrl')
                                    .d('查询 URL'),
                                }),
                              },
                            ],
                            initialValue: valueListHeader.customUrl || '',
                          })(<Input className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                </Row>
              ) : null}
              {valueListHeader.lovTypeCode === 'SQL' ? (
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={
                        <span>
                          <Tooltip
                            title={intl
                              .get('hpfm.valueList.model.header.message.encryptField')
                              .d('多个字段使用“,”分隔')}
                          >
                            {intl.get('hpfm.valueList.model.header.encryptField').d('加密字段')}
                            <Icon type="question-circle-o" />
                          </Tooltip>
                        </span>
                      }
                    >
                      {isTenant && isNotCurrentTenant
                        ? valueListHeader.encryptField || ''
                        : getFieldDecorator('encryptField', {
                            rules: [
                              {
                                max: 480,
                                message: intl.get('hzero.common.validation.max', {
                                  max: 480,
                                }),
                              },
                            ],
                            initialValue: valueListHeader.encryptField || '',
                          })(<Input className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                </Row>
              ) : null}
              {(!isTenantRoleLevel() || VERSION_IS_OP) && valueListHeader.lovTypeCode === 'SQL' ? (
                <Row
                  {...EDIT_FORM_ROW_LAYOUT}
                  className={isTenant && isNotCurrentTenant ? 'read-half-row' : 'half-row'}
                >
                  <Col {...FORM_COL_2_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT_COL_2}
                      label={intl.get('hpfm.valueList.model.header.customSql').d('查询 SQL')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? valueListHeader.customSql || ''
                        : getFieldDecorator('customSql', {
                            rules: [
                              {
                                required: true,
                                message: intl.get('hzero.common.validation.notNull', {
                                  name: intl
                                    .get('hpfm.valueList.model.header.customSql')
                                    .d('查询 SQL'),
                                }),
                              },
                            ],
                            initialValue: valueListHeader.customSql || '',
                          })(<TextArea rows={9} className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                </Row>
              ) : null}
              {((!isTenantRoleLevel() || VERSION_IS_OP) && valueListHeader.lovTypeCode === 'SQL') ||
              valueListHeader.lovTypeCode === 'URL' ? (
                <Row
                  {...EDIT_FORM_ROW_LAYOUT}
                  className={isTenant && isNotCurrentTenant ? 'read-half-row' : 'half-row'}
                >
                  <Col {...FORM_COL_2_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT_COL_2}
                      label={intl.get('hpfm.valueList.model.header.translationSql').d('翻译 SQL')}
                    >
                      {isTenant && isNotCurrentTenant
                        ? valueListHeader.translationSql || ''
                        : getFieldDecorator('translationSql', {
                            initialValue: valueListHeader.translationSql || '',
                          })(<TextArea rows={9} className={FORM_FIELD_CLASSNAME} />)}
                    </Form.Item>
                  </Col>
                </Row>
              ) : null}
            </Form>
          </Card>
          {/* 独立值集展示表格 */}
          {valueListHeader.lovTypeCode === 'IDP' ? (
            <Card
              key="value-list-line"
              bordered={false}
              className={DETAIL_CARD_TABLE_CLASSNAME}
              title={<h3>{intl.get('hpfm.valueList.view.title.valueListIDP').d('独立值集')}</h3>}
            >
              <Row>
                <Col {...FORM_COL_3_4_LAYOUT}>
                  <FilterForm
                    wrappedComponentRef={this.filterFormRef}
                    onSearch={() => {
                      this.loadValueListLine(lovId);
                    }}
                  />
                </Col>
                <Col {...FORM_COL_4_LAYOUT}>
                  {isTenant && isNotCurrentTenant ? null : (
                    <div className="table-list-operator" style={{ margin: '5px 0 0 0' }}>
                      <ButtonPermission
                        permissionList={[
                          {
                            code: `${match.path}.button.add`,
                            type: 'button',
                            meaning: '值集数据定义-新增值',
                          },
                        ]}
                        onClick={this.showModal}
                      >
                        {intl.get('hpfm.valueList.view.button.add').d('新增值')}
                      </ButtonPermission>
                      <ButtonPermission
                        permissionList={[
                          {
                            code: `${match.path}.button.delete`,
                            type: 'button',
                            meaning: '值集数据定义-删除值',
                          },
                        ]}
                        onClick={this.handleDeleteValues}
                        disabled={this.state.selectedRows.length === 0}
                      >
                        {intl.get('hpfm.valueList.view.button.delete').d('删除值')}
                      </ButtonPermission>
                      {/* <ButtonPermission
                        permissionList={[
                          {
                            code: `${match.path}.button.Import`,
                            type: 'button',
                            meaning: '独立值集-导入',
                          },
                        ]}
                        icon="to-top"
                        onClick={this.handleImport}
                      >
                        {intl.get('hzero.common.button.import').d('导入')}
                      </ButtonPermission> */}
                    </div>
                  )}
                </Col>
              </Row>
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
          ) : null}
        </Content>
        <DetailForm
          title={
            editValue.lovValueId
              ? intl.get('hpfm.valueList.view.title.editForm').d('编辑值')
              : intl.get('hpfm.valueList.view.title.createForm').d('创建值')
          }
          editValue={editValue}
          saveLoading={saving}
          modalVisible={modalVisible}
          onOk={this.handleAdd}
          onCancel={this.hideModal}
        />
      </React.Fragment>
    );
  }
}
