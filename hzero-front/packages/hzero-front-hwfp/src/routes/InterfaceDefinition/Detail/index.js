/**
 * approveAuth - 流程设置/审批权限管理
 * @date: 2018-8-15
 * @author: WH <heng.wei@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Card, Col, Form, Input, Row, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import Switch from 'components/Switch';

import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import ListTable from './ListTable';
import ParamsDrawer from './ParamsDrawer';
import ResultDrawer from './ResultDrawer';

@formatterCollections({
  code: ['hwfp.interfaceDefinition', 'hwfp.common', 'hwfp.serviceDefinition', 'entity.tenant'],
})
@Form.create({ fieldNameProp: null })
@connect(({ interfaceDefinition, loading }) => ({
  interfaceDefinition,
  isSiteFlag: !isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  detailLoading: loading.effects['interfaceDefinition/fetchDetail'],
  saving:
    loading.effects['interfaceDefinition/createInterface'] ||
    loading.effects['interfaceDefinition/updateInterface'],
  paramSaving:
    loading.effects['interfaceDefinition/updateParam'] ||
    loading.effects['interfaceDefinition/createParam'],
  paramDeleteLoading: loading.effects['interfaceDefinition/deleteParam'],
  resultLoading: loading.effects['interfaceDefinition/resultInterface'],
}))
export default class Detail extends Component {
  state = {
    paramsModalVisible: false,
    paramEditData: {}, // 参数编辑数据
    resultVisible: false, // 测试模态框
    resultData: {}, // 测试响应数据
    permissionCodeText: '',
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'interfaceDefinition/init',
    });
    this.fetchDetail();
  }

  @Bind()
  fetchDetail() {
    const { dispatch, match } = this.props;
    const {
      params: { interfaceId },
    } = match;
    if (interfaceId !== 'create') {
      dispatch({
        type: 'interfaceDefinition/fetchDetail',
        payload: { interfaceId },
      }).then((res) => {
        if (res) {
          this.setState({ permissionCodeText: res.permissionCode });
        }
      });
    } else {
      dispatch({
        type: 'interfaceDefinition/updateState',
        payload: {
          interfaceDefinitionDetail: {},
          parameterList: [],
        },
      });
    }
  }

  @Bind()
  handleSave() {
    const { dispatch, form, match, history, interfaceDefinition = {} } = this.props;
    const {
      params: { interfaceId },
    } = match;
    const { interfaceDefinitionDetail = {} } = interfaceDefinition;
    const { parameterList, ...other } = interfaceDefinitionDetail;
    form.validateFields((error, values) => {
      if (!error) {
        if (interfaceId === 'create') {
          dispatch({
            type: 'interfaceDefinition/createInterface',
            payload: values,
          }).then((res) => {
            if (res) {
              notification.success();
              history.push(`/hwfp/interface-definition/detail/${res.interfaceId}`);
              this.fetchDetail();
            }
          });
        } else {
          dispatch({
            type: 'interfaceDefinition/updateInterface',
            payload: {
              ...other,
              ...values,
            },
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
  handleUpdateParam(data = {}) {
    this.setState({ paramsModalVisible: true, paramEditData: data });
  }

  @Bind()
  handleHideParams() {
    this.setState({ paramsModalVisible: false, paramEditData: {} });
  }

  @Bind()
  handleParamDelete(record) {
    const { dispatch, match } = this.props;
    const {
      params: { interfaceId },
    } = match;
    dispatch({
      type: 'interfaceDefinition/deleteParam',
      payload: { interfaceId, ...record },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchDetail();
      }
    });
  }

  @Bind()
  handleParamOk(fieldsValue = {}) {
    const { dispatch, interfaceDefinition = {} } = this.props;
    const { paramEditData } = this.state;
    const { interfaceDefinitionDetail } = interfaceDefinition;
    const { interfaceId } = interfaceDefinitionDetail;
    if (paramEditData.parameterId) {
      dispatch({
        type: 'interfaceDefinition/updateParam',
        payload: {
          interfaceId,
          ...paramEditData,
          ...fieldsValue,
        },
      }).then((res) => {
        if (res) {
          this.handleHideParams();
          notification.success();
          this.fetchDetail();
        }
      });
    } else {
      dispatch({
        type: 'interfaceDefinition/createParam',
        payload: {
          interfaceId,
          ...fieldsValue,
        },
      }).then((res) => {
        if (res) {
          this.handleHideParams();
          notification.success();
          this.fetchDetail();
        }
      });
    }
  }

  @Bind()
  handleShowResult(flag = false) {
    this.setState({ resultVisible: flag, resultData: {} });
  }

  @Bind()
  handleResult(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'interfaceDefinition/resultInterface',
      payload: data,
    }).then((res) => {
      this.setState({ resultData: res });
    });
  }

  @Bind()
  serviceNameChange() {
    const { form } = this.props;
    form.setFieldsValue({ permissionCode: '' });
    this.setState({ permissionCodeText: '' });
  }

  render() {
    const {
      form,
      match,
      isSiteFlag,
      tenantId: currentTenantId,
      resultLoading = false,
      saving = false,
      detailLoading = false,
      paramSaving = false,
      paramDeleteLoading = false,
      interfaceDefinition = {},
    } = this.props;
    const {
      paramsModalVisible,
      paramEditData,
      resultVisible,
      resultData,
      permissionCodeText,
    } = this.state;
    const {
      params: { interfaceId },
    } = match;
    const {
      interfaceDefinitionDetail = {},
      paramTypeList = [],
      parameterList = [],
    } = interfaceDefinition;
    const {
      tenantId,
      tenantName,
      interfaceCode,
      serviceName,
      permissionCode,
      description,
      enabledFlag = 1,
    } = interfaceDefinitionDetail;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = form;
    // 是否预定义按钮控制
    const isPredefined = currentTenantId !== tenantId;
    // 是否新建
    const isCreate = interfaceId === undefined || interfaceId === 'create';
    // 编辑逻辑控制
    const editControl = !isSiteFlag ? isPredefined && !isCreate : false;

    const listProps = {
      isSiteFlag,
      isPredefined,
      paramDeleteLoading,
      detail: interfaceDefinitionDetail,
      dataSource: parameterList,
      pagination: false,
      onCreate: this.handleUpdateParam,
      onEdit: this.handleUpdateParam,
      onDelete: this.handleParamDelete,
    };

    const paramsDrawerProps = {
      paramSaving,
      paramTypeList,
      title: intl.get('hwfp.interfaceDefinition.view.title.editParams').d('编辑参数'),
      modalVisible: paramsModalVisible,
      initData: paramEditData,
      onCancel: this.handleHideParams,
      onOk: this.handleParamOk,
    };

    const resultProps = {
      resultData,
      parameterList: parameterList.map((item) => ({ ...item, _status: 'create' })),
      currentRecord: interfaceDefinitionDetail,
      visible: resultVisible,
      loading: resultLoading,
      onCancel: () => this.handleShowResult(false),
      onResult: this.handleResult,
    };

    return (
      <>
        <Header
          title={
            interfaceId !== undefined && interfaceId !== 'create'
              ? intl.get('hwfp.serviceDefinition.view.title.edit').d('编辑接口定义')
              : intl.get('hwfp.serviceDefinition.view.title.create').d('新建接口定义')
          }
          backPath="/hwfp/interface-definition/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSave}
            loading={saving}
            disabled={detailLoading || editControl}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button
            icon="file-text"
            onClick={this.handleShowResult}
            loading={saving}
            disabled={isCreate}
          >
            {intl.get('hzero.common.button.test').d('测试')}
          </Button>
        </Header>
        <Content>
          <Spin spinning={detailLoading}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>
                  {intl.get('hwfp.serviceDefinition.view.title.interfaceDefinition').d('接口定义')}
                </h3>
              }
              loading={false}
            >
              <Form>
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
                          <Lov
                            allowClear={false}
                            disabled={!isCreate}
                            textValue={tenantName}
                            code="HPFM.TENANT"
                          />
                        )}
                      </Form.Item>
                    </Col>
                  )}
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hwfp.interfaceDefinition.model.interface.interfaceCode')
                        .d('接口编码')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('interfaceCode', {
                        initialValue: interfaceCode,
                        rules: [
                          {
                            required: isCreate,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.interfaceDefinition.model.interface.interfaceCode')
                                .d('接口编码'),
                            }),
                          },
                          {
                            pattern: CODE_UPPER,
                            message: intl
                              .get('hzero.common.validation.codeUpper')
                              .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                          },
                          {
                            max: 60,
                            message: intl.get('hzero.common.validation.max', {
                              max: 60,
                            }),
                          },
                        ],
                      })(
                        <Input
                          trim
                          typeCase="upper"
                          inputChinese={false}
                          disabled={editControl || !!interfaceCode}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hwfp.interfaceDefinition.model.interface.serviceName')
                        .d('服务名称')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('serviceName', {
                        initialValue: serviceName,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.interfaceDefinition.model.interface.serviceName')
                                .d('服务名称'),
                            }),
                          },
                        ],
                      })(
                        <Lov
                          code={
                            isSiteFlag ? 'HADM.ROUTE.SERVICE_CODE' : 'HADM.ROUTE.SERVICE_CODE.ORG'
                          }
                          textValue={serviceName}
                          allowClear={false}
                          disabled={editControl}
                          onChange={this.serviceNameChange}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hwfp.interfaceDefinition.model.interface.permissionCode')
                        .d('接口权限编码')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('permissionCode', {
                        initialValue: permissionCode,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hwfp.interfaceDefinition.model.interface.permissionCode')
                                .d('接口权限编码'),
                            }),
                          },
                        ],
                      })(
                        <Lov
                          allowClear={false}
                          key={`${getFieldValue('serviceName')}`}
                          code="HIAM.INTERFACE_PERMISSION"
                          queryParams={{
                            level: 'organization',
                            serviceName: getFieldValue('serviceName'),
                          }}
                          textValue={permissionCodeText}
                          onChange={(text, record) => {
                            setFieldsValue({ description: record.description });
                          }}
                          disabled={editControl || !getFieldValue('serviceName')}
                        />
                      )}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hwfp.interfaceDefinition.model.interface.description')
                        .d('接口说明')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('description', {
                        initialValue: description,
                      })(<Input disabled={editControl || !getFieldValue('serviceName')} />)}
                    </Form.Item>
                  </Col>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('hzero.common.status.enable').d('启用')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('enabledFlag', {
                        initialValue: enabledFlag,
                      })(<Switch disabled={editControl} />)}
                    </Form.Item>
                  </Col>
                </Row>
              </Form>
            </Card>
            <Card
              bordered={false}
              className={DETAIL_CARD_TABLE_CLASSNAME}
              title={<h3>{intl.get('hwfp.common.model.param').d('参数')}</h3>}
            >
              <ListTable {...listProps} />
            </Card>
          </Spin>
          <ParamsDrawer {...paramsDrawerProps} />
          {resultVisible && <ResultDrawer {...resultProps} />}
        </Content>
      </>
    );
  }
}
