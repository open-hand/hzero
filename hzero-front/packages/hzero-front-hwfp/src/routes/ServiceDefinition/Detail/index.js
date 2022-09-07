import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Button, Card, Col, Form, Input, Row, Select, Spin, Table } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  // EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';
import { yesOrNoRender, operatorRender } from 'utils/renderer';

import ParamsDrawer from './ParamsDrawer';
import ExpressionParamter from './ExpressionParamter';

import styles from '../index.less';

const EDIT_FORM_ITEM_LAYOUT = {
  labelCol: {
    span: 7,
  },
  wrapperCol: {
    span: 17,
  },
};

@Form.create({ fieldNameProp: null })
@connect(({ loading, serviceDefinition }) => ({
  serviceDefinition,
  currentTenantId: getCurrentOrganizationId(),
  isSiteFlag: !isTenantRoleLevel(),
  detailLoading: loading.effects['serviceDefinition/fetchDetail'],
  createLoading: loading.effects['serviceDefinition/createService'],
  updateLoading: loading.effects['serviceDefinition/updateService'],
  paramsLoading: loading.effects['serviceDefinition/queryParams'],
  paramSaving: loading.effects['serviceDefinition/updateParam'],
}))
@formatterCollections({ code: ['hwfp.serviceDefinition', 'hpfm.valueList'] })
export default class Detail extends React.Component {
  state = {
    paramsModalVisible: false,
    viewName: '',
  };

  componentDidMount() {
    const {
      dispatch,
      serviceDefinition: { serviceTypeList = [], paramterSourceList = [] },
    } = this.props;
    if (serviceTypeList.length === 0) {
      dispatch({
        type: 'serviceDefinition/init',
      });
    }
    if (paramterSourceList.length === 0) {
      dispatch({
        type: 'serviceDefinition/initParamter',
      });
    }
    this.fetchDetail();
  }

  @Bind()
  fetchDetail() {
    const { dispatch, match } = this.props;
    const {
      params: { serviceId },
    } = match;
    if (serviceId !== 'create') {
      dispatch({
        type: 'serviceDefinition/fetchDetail',
        payload: { serviceId },
      }).then((res) => {
        if (res) {
          this.setState({ viewName: res.viewName });
        }
      });
    } else {
      dispatch({
        type: 'serviceDefinition/updateState',
        payload: {
          serviceDetail: {},
          parameterList: [],
        },
      });
    }
  }

  @Bind()
  handleUpdateParam(data = {}) {
    const { parameterSource } = data;
    this.setState({ paramsModalVisible: true, paramEditData: data });
    if (parameterSource === 'VARIABLE') {
      this.handleChangeSource(parameterSource);
    }
  }

  @Bind()
  handleHideParams() {
    this.setState({ paramsModalVisible: false, paramEditData: {} });
  }

  @Bind()
  handleParamOk(fieldsValue = {}) {
    const {
      dispatch,
      serviceDefinition: { parameterList = [] },
    } = this.props;
    const { paramEditData } = this.state;
    const updateList = parameterList.map((item) => {
      if (item.interfaceParameterId === paramEditData.interfaceParameterId) {
        return { ...paramEditData, ...fieldsValue };
      } else {
        return item;
      }
    });
    dispatch({
      type: 'serviceDefinition/updateState',
      payload: {
        parameterList: updateList,
      },
    });
    this.handleHideParams();
  }

  @Bind()
  queryParams(params) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceDefinition/queryParams',
      payload: {
        interfaceId: params,
      },
    });
  }

  @Bind
  handleServiceCodeChange(serviceMode) {
    const { dispatch, form } = this.props;
    if (serviceMode === 'LOV_VIEW') {
      form.setFieldsValue({ serviceType: 'APPROVAL_CANDIDATE_RULE' });
    } else {
      form.setFieldsValue({ serviceType: '' });
    }
    dispatch({
      type: 'serviceDefinition/updateState',
      payload: {
        parameterList: [],
      },
    });
  }

  @Bind()
  handleServiceChange(value) {
    this.queryParams(value);
  }

  @Bind()
  handleChangeSource() {
    const { dispatch, form } = this.props;
    const { categoryId, documentId } = form.getFieldsValue(['categoryId', 'documentId']);
    dispatch({
      type: 'serviceDefinition/fetchVariable',
      payload: {
        categoryId,
        documentId,
      },
    });
  }

  @Bind()
  handleSave() {
    const { dispatch, form, match, history, serviceDefinition = {} } = this.props;
    const {
      params: { serviceId },
    } = match;
    const { serviceDetail = {}, parameterList = [] } = serviceDefinition;
    form.validateFields((error, values) => {
      if (!error) {
        const isHaveValue = parameterList.find(
          (item) => item.parameterValue === undefined || item.parameterSource === undefined
        );
        if (serviceId === 'create') {
          if (isHaveValue && values.serviceMode === 'REMOTE') {
            notification.warning({
              message: intl
                .get('hwfp.serviceDefinition.view.message.setValueAndSource')
                .d('请设置参数值和参数来源'),
            });
            return;
          }
          const params = {
            ...values,
            parameterList: values.interfaceId
              ? parameterList.map((item) => {
                  const { _token, objectVersionNumber, parameterId, ...other } = item;
                  return { interfaceParameterId: parameterId, ...other };
                })
              : parameterList,
          };
          if (values.serviceMode === 'LOV_VIEW') {
            params.viewName = this.state.viewName;
          } else {
            delete params.viewCode;
            delete params.viewName;
          }
          dispatch({
            type: 'serviceDefinition/createService',
            payload: params,
          }).then((res) => {
            if (res) {
              notification.success();
              history.push(`/hwfp/service-definition/detail/${res.serviceId}`);
              this.fetchDetail();
            }
          });
        } else {
          if (isHaveValue) {
            notification.warning({
              message: intl
                .get('hwfp.serviceDefinition.view.message.setValueAndSource')
                .d('请设置参数值和参数来源'),
            });
            return;
          }
          const params = {
            ...serviceDetail,
            parameterList,
            ...values,
          };
          if (values.serviceMode === 'LOV_VIEW') {
            params.viewName = this.state.viewName;
          } else {
            delete params.viewCode;
            delete params.viewName;
          }
          dispatch({
            type: 'serviceDefinition/updateService',
            payload: params,
          }).then((res) => {
            if (res) {
              notification.success();
              this.fetchDetail();
            }
          });
        }
      }
    });
  }

  @Bind()
  handleChangeLov(_, record) {
    this.setState({ viewName: record.viewName });
  }

  @Bind()
  getColumns(isSiteFlag, isPredefined, isCreate) {
    if (!this.columns) {
      this.columns = [
        {
          title: intl.get('hwfp.serviceDefinition.model.param.parameterName').d('参数名称'),
          dataIndex: 'parameterName',
        },
        {
          title: intl
            .get('hwfp.serviceDefinition.model.service.interfaceParameterType')
            .d('参数类型'),
          dataIndex: 'interfaceParameterTypeMeaning',
          width: 150,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.param.defaultValue').d('默认值'),
          dataIndex: 'defaultValue',
          width: 150,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.param.parameterValue').d('参数值'),
          dataIndex: 'parameterValue',
          width: 150,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.service.parameterSource').d('参数来源'),
          dataIndex: 'parameterSourceMeaning',
          width: 150,
        },
        {
          title: intl.get('hwfp.serviceDefinition.model.param.description').d('参数描述'),
          dataIndex: 'description',
          width: 150,
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          dataIndex: 'edit',
          width: 80,
          render: (val, record) => {
            const operators = [
              {
                key: 'edit',
                ele: (
                  <a
                    onClick={() => {
                      this.handleUpdateParam(record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
            ];
            if (isSiteFlag || !isPredefined || isCreate) {
              return operatorRender(operators, record);
            } else {
              return null;
            }
          },
        },
      ];
    }
    return this.columns;
  }

  render() {
    const {
      form,
      dispatch,
      isSiteFlag,
      currentTenantId,
      paramSaving = false,
      detailLoading = false,
      updateLoading = false,
      createLoading = false,
      paramsLoading = false,
      serviceDefinition: {
        serviceTypeList = [],
        serviceModeList = [],
        serviceOperatorList = [],
        paramterSourceList = [],
        serviceDetail: {
          serviceModeMeaning,
          serviceId,
          tenantId,
          tenantName,
          interfaceId,
          documentDescription,
          interfaceDescription,
          serviceCode,
          viewCode,
          viewName,
          categoryId,
          categoryDescription,
          expression,
          simpleExpression,
          approveResultExpression,
          simpleApproveResultExpression,
          documentId,
          description,
          _token,
          serviceMode = '',
          serviceType = '',
          serviceTypeMeaning = '',
          enabledFlag = 1,
        } = {},
        variableList = [],
        parameterList = [],
      },
    } = this.props;
    const { paramsModalVisible, paramEditData } = this.state;
    const { getFieldDecorator, getFieldValue } = form;
    const paramsProps = {
      paramSaving,
      paramterSourceList,
      variableList,
      modalVisible: paramsModalVisible,
      initData: paramEditData,
      onChangeSource: this.handleChangeSource,
      onOk: this.handleParamOk,
      onCancel: this.handleHideParams,
    };
    // 是否预定义按钮控制
    const isPredefined = currentTenantId !== tenantId;
    // 是否新建
    const isCreate = serviceId === undefined || serviceId === 'create';
    // 编辑逻辑控制
    const editControl = !isSiteFlag ? isPredefined && !isCreate : false;
    return (
      <>
        <Header
          title={intl.get('hwfp.serviceDefinition.view.title.serviceDefinition').d('服务定义')}
          backPath="/hwfp/service-definition/list"
        >
          <Button
            icon="save"
            type="primary"
            disabled={detailLoading || editControl}
            loading={createLoading || updateLoading}
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Spin spinning={detailLoading}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>
                  {intl.get('hwfp.serviceDefinition.view.title.serviceDefinition').d('服务定义')}
                </h3>
              }
              loading={false}
            >
              <Form className={styles['edit-form']}>
                <Row
                  {...EDIT_FORM_ROW_LAYOUT}
                  type="flex"
                  justify="start"
                  className="inclusion-row"
                >
                  {isSiteFlag && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        label={intl.get('entity.tenant.tag').d('租户')}
                        {...EDIT_FORM_ITEM_LAYOUT}
                      >
                        {getFieldDecorator('tenantId', {
                          initialValue: tenantId,
                          rules: [
                            {
                              required: isCreate,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl.get('entity.tenant.tag').d('租户'),
                              }),
                            },
                          ],
                        })(
                          !isCreate ? (
                            <>{tenantName}</>
                          ) : (
                            <Lov textValue={tenantName} code="HPFM.TENANT" />
                          )
                        )}
                      </Form.Item>
                    </Col>
                  )}
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.categoryId')
                        .d('流程分类')}
                    >
                      {getFieldDecorator('categoryId', {
                        initialValue: categoryId,
                        rules: [
                          {
                            required: isCreate,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.serviceDefinition.model.service.categoryId')
                                .d('流程分类'),
                            }),
                          },
                        ],
                      })(
                        !isCreate ? (
                          <>{categoryDescription}</>
                        ) : (
                          <Lov
                            code="HWFP.PROCESS_CATEGORY"
                            disabled={
                              !isSiteFlag ? false : form.getFieldValue('tenantId') === undefined
                            }
                            queryParams={isSiteFlag ? {} : { tenantId: currentTenantId }}
                          />
                        )
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.serviceCode')
                        .d('服务编码')}
                    >
                      {getFieldDecorator('serviceCode', {
                        initialValue: serviceCode,
                        rules: [
                          {
                            required: isCreate,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.serviceDefinition.model.service.serviceCode')
                                .d('服务编码'),
                            }),
                          },
                          {
                            pattern: CODE_UPPER,
                            message: intl
                              .get('hzero.common.validation.codeUpper')
                              .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                          },
                          {
                            max: 30,
                            message: intl.get('hzero.common.validation.max', {
                              max: 30,
                            }),
                          },
                        ],
                      })(
                        !isCreate ? (
                          <>{serviceCode}</>
                        ) : (
                          <Input trim typeCase="upper" inputChinese={false} />
                        )
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.serviceMode')
                        .d('服务方式')}
                    >
                      {getFieldDecorator('serviceMode', {
                        initialValue: serviceMode,
                        rules: [
                          {
                            required: isCreate,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.serviceDefinition.model.service.serviceMode')
                                .d('服务方式'),
                            }),
                          },
                        ],
                      })(
                        !isCreate ? (
                          <>{serviceModeMeaning}</>
                        ) : (
                          <Select
                            allowClear
                            style={{ width: '100%' }}
                            onChange={this.handleServiceCodeChange}
                          >
                            {serviceModeList.map((item) => (
                              <Select.Option value={item.value} key={item.value}>
                                {item.meaning}
                              </Select.Option>
                            ))}
                          </Select>
                        )
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.serviceType')
                        .d('服务类别')}
                    >
                      {getFieldDecorator('serviceType', {
                        initialValue: serviceType,
                        rules: [
                          {
                            required: isCreate,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.serviceDefinition.model.service.serviceType')
                                .d('服务类别'),
                            }),
                          },
                        ],
                      })(
                        !isCreate ? (
                          <>{serviceTypeMeaning}</>
                        ) : (
                          <Select
                            style={{ width: '100%' }}
                            disabled={getFieldValue('serviceMode') === 'LOV_VIEW'}
                          >
                            {serviceTypeList.map((item) => (
                              <Select.Option value={item.value} key={item.value}>
                                {item.meaning}
                              </Select.Option>
                            ))}
                          </Select>
                        )
                      )}
                    </Form.Item>
                  </Col>
                  {form.getFieldValue('serviceMode') === 'LOV_VIEW' && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        {...EDIT_FORM_ITEM_LAYOUT}
                        label={intl.get('hpfm.valueList.lovSetting.title.lovSetting').d('值集视图')}
                      >
                        {getFieldDecorator('viewCode', {
                          initialValue: viewCode,
                          rules: [
                            {
                              required: isCreate,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hpfm.valueList.lovSetting.title.lovSetting')
                                  .d('值集视图'),
                              }),
                            },
                          ],
                        })(
                          !isCreate ? (
                            <>{viewName}</>
                          ) : (
                            <Lov
                              code="SPFM.LOV_VIEW.ORG"
                              textValue={viewName}
                              queryParams={
                                isTenantRoleLevel() && {
                                  tenantId: getCurrentOrganizationId(),
                                }
                              }
                              lovOptions={{ displayField: 'viewName', valueField: 'viewCode' }}
                              onChange={this.handleChangeLov}
                            />
                          )
                        )}
                      </Form.Item>
                    </Col>
                  )}
                  {form.getFieldValue('serviceMode') === 'REMOTE' ||
                  form.getFieldValue('serviceMode') === 'LOV_VIEW' ? (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        {...EDIT_FORM_ITEM_LAYOUT}
                        label={intl
                          .get('hwfp.serviceDefinition.model.interface.interfaceId')
                          .d('接口定义')}
                      >
                        {getFieldDecorator('interfaceId', {
                          initialValue: interfaceId,
                          rules: [
                            {
                              required: isCreate,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hwfp.serviceDefinition.model.interface.serviceId')
                                  .d('接口定义'),
                              }),
                            },
                          ],
                        })(
                          !isCreate ? (
                            <>{interfaceDescription}</>
                          ) : (
                            <Lov
                              allowClear={false}
                              code="HWFP.INTERFACE"
                              queryParams={
                                isTenantRoleLevel() && {
                                  organizationId: getCurrentOrganizationId(),
                                }
                              }
                              textValue={interfaceDescription}
                              onChange={this.handleServiceChange}
                            />
                          )
                        )}
                      </Form.Item>
                    </Col>
                  ) : (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        {...EDIT_FORM_ITEM_LAYOUT}
                        label={intl
                          .get('hwfp.serviceDefinition.model.interface.simpleExpression')
                          .d('表达式')}
                      >
                        {getFieldDecorator('simpleExpression', {
                          initialValue: simpleExpression,
                          rules: [
                            {
                              required: true,
                              message: intl.get('hzero.common.validation.notNull', {
                                name: intl
                                  .get('hwfp.serviceDefinition.model.interface.simpleExpression')
                                  .d('表达式'),
                              }),
                            },
                            {
                              max: 120,
                              message: intl.get('hzero.common.validation.max', {
                                max: 120,
                              }),
                            },
                          ],
                        })(editControl ? <>{expression}</> : <Input />)}
                      </Form.Item>
                    </Col>
                  )}
                  {form.getFieldValue('serviceMode') !== 'REMOTE' &&
                    form.getFieldValue('serviceMode') !== 'LOV_VIEW' &&
                    !isCreate && (
                      <Col {...FORM_COL_3_LAYOUT}>
                        <Form.Item
                          {...EDIT_FORM_ITEM_LAYOUT}
                          label={intl
                            .get('hwfp.serviceDefinition.model.interface.expression')
                            .d('执行表达式')}
                        >
                          {getFieldDecorator('expression', {
                            initialValue: expression,
                          })(<>{expression}</>)}
                        </Form.Item>
                      </Col>
                    )}
                  {form.getFieldValue('serviceType') === 'APPROVAL_STRATEGY' && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        {...EDIT_FORM_ITEM_LAYOUT}
                        label={intl
                          .get('hwfp.serviceDefinition.model.interface.resultSimpleExpression')
                          .d('审批结果表达式')}
                      >
                        {getFieldDecorator('simpleApproveResultExpression', {
                          initialValue: simpleApproveResultExpression,
                          rules: [
                            {
                              max: 510,
                              message: intl.get('hzero.common.validation.max', {
                                max: 510,
                              }),
                            },
                          ],
                        })(editControl ? <>{simpleApproveResultExpression}</> : <Input />)}
                      </Form.Item>
                    </Col>
                  )}
                  {form.getFieldValue('serviceType') === 'APPROVAL_STRATEGY' && !isCreate && (
                    <Col {...FORM_COL_3_LAYOUT}>
                      <Form.Item
                        {...EDIT_FORM_ITEM_LAYOUT}
                        label={intl
                          .get('hwfp.serviceDefinition.model.interface.resultExpression')
                          .d('审批结果执行表达式')}
                      >
                        {getFieldDecorator('approveResultExpression', {
                          initialValue: approveResultExpression,
                        })(<>{approveResultExpression}</>)}
                      </Form.Item>
                    </Col>
                  )}
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.documentId')
                        .d('流程单据')}
                    >
                      {getFieldDecorator('documentId', {
                        initialValue: documentId,
                      })(
                        !isCreate ? (
                          <>{documentDescription}</>
                        ) : (
                          <Lov
                            allowClear={false}
                            disabled={!form.getFieldValue('categoryId')}
                            queryParams={{
                              tenantId: form.getFieldValue('tenantId') || currentTenantId,
                              categoryId: form.getFieldValue('categoryId'),
                            }}
                            code="HWFP.PROCESS_DOCUMENT"
                            textValue={documentDescription}
                          />
                        )
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl
                        .get('hwfp.serviceDefinition.model.service.description')
                        .d('服务描述')}
                    >
                      {getFieldDecorator('description', {
                        initialValue: description,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.serviceDefinition.model.service.description')
                                .d('服务描述'),
                            }),
                          },
                          {
                            max: 240,
                            message: intl.get('hzero.common.validation.max', {
                              max: 240,
                            }),
                          },
                        ],
                      })(
                        editControl ? (
                          <>{description}</>
                        ) : (
                          <TLEditor
                            label={intl
                              .get('hwfp.serviceDefinition.model.service.description')
                              .d('服务描述')}
                            field="description"
                            inputSize={{ zh: 240, en: 240 }}
                            token={_token}
                            disabled={editControl}
                          />
                        )
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('hzero.common.status.enable').d('启用')}
                    >
                      {getFieldDecorator('enabledFlag', {
                        initialValue: enabledFlag,
                      })(
                        editControl ? (
                          <>{yesOrNoRender(enabledFlag)}</>
                        ) : (
                          <Switch disabled={editControl} />
                        )
                      )}
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
            </Card>
          </Spin>
          {form.getFieldValue('serviceMode') === 'REMOTE' ||
          form.getFieldValue('serviceMode') === 'LOV_VIEW' ? (
            <>
              <Card
                bordered={false}
                className={DETAIL_CARD_TABLE_CLASSNAME}
                title={<h3>{intl.get('hzero.common.model.param').d('参数')}</h3>}
              >
                <Table
                  bordered
                  loading={paramsLoading}
                  rowKey="parameterId"
                  dataSource={parameterList}
                  columns={this.getColumns(isSiteFlag, isPredefined, isCreate)}
                  pagination={false}
                />
              </Card>
              {paramsModalVisible && <ParamsDrawer {...paramsProps} />}
            </>
          ) : (
            <ExpressionParamter
              isSiteFlag={isSiteFlag}
              isPredefined={isPredefined}
              dispatch={dispatch}
              onChangeSource={this.handleChangeSource}
              variableList={variableList}
              parameterList={parameterList}
              serviceOperatorList={serviceOperatorList}
              paramterSourceList={paramterSourceList}
            />
          )}
        </Content>
      </>
    );
  }
}
