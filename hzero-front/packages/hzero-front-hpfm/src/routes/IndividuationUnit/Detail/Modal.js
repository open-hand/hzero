/* eslint-disable eqeqeq */
/* eslint-disable no-nested-ternary */
/* eslint-disable react/jsx-props-no-spreading */
/* eslint-disable react/state-in-constructor */
import React, { Component } from 'react';
import {
  Drawer,
  Form,
  Input,
  Button,
  Select,
  InputNumber,
  Row,
  Col,
  Checkbox,
  Badge,
} from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';
import { connect } from 'dva';

import notification from 'utils/notification';
import intl from 'utils/intl';
import Lov from 'components/Lov';
import ParamsModal from '@/components/CommonModal/ParamsConfigModal';

import {
  colOptions,
  getFieldCodeAlias,
  getFieldNameAlias,
  getAddFieldAlias,
  getEditFieldAlias,
  getDefaultActiveAlias,
  getSingleTenantValueCode,
} from '@/utils/constConfig.js';
import styles from '../style/index.less';

const FormItem = Form.Item;
const { Option } = Select;
const formsLayouts = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
const formLayout2 = {
  labelCol: { span: 9 },
  wrapperCol: { span: 15 },
};

@Form.create({ fieldNameProp: null })
@connect(({ loading = {} }) => ({
  saveLoading: loading.effects['individuationUnit/saveUnitField'],
}))
export default class Modal extends Component {
  state = {
    paramVisible: false,
    backUpParamList: [],
  };

  componentDidUpdate(prevProps) {
    if (prevProps.visible === false && this.props.visible === true) {
      // eslint-disable-next-line react/no-did-update-set-state
      this.setState({ backUpParamList: ((this.props.data || {}).paramList || []).map((i) => i) });
    }
  }

  @Bind()
  create() {
    const { unitInfo, form, dispatch, fetchUnitDetail, handleClose } = this.props;
    form.validateFields((err, values = {}) => {
      if (!err) {
        const { id } = unitInfo;
        dispatch({
          type: 'individuationUnit/saveUnitField',
          params: {
            ...values,
            unitId: id,
          },
        }).then((res) => {
          if (res) {
            notification.success();
            fetchUnitDetail({ unitId: id });
            handleClose();
          }
        });
      }
    });
  }

  @Bind()
  save() {
    const { form, dispatch, data = {}, handleClose, fetchUnitDetail } = this.props;
    const {
      unitId,
      modelId,
      fieldId,
      field: { model },
    } = data;
    form.validateFields((err) => {
      if (!err) {
        const fieldAlias =
          form.getFieldValue('isModelField') == 1
            ? form.getFieldValue('fieldAlias')
            : data.fieldAlias;
        dispatch({
          type: 'individuationUnit/saveUnitField',
          params: {
            ...data,
            ...form.getFieldsValue(),
            modelId: (model || {}).modelId || modelId,
            fieldId,
            fieldAlias,
          },
        }).then((res) => {
          if (res) {
            notification.success();
            fetchUnitDetail({ unitId });
            handleClose();
          }
        });
      }
    });
  }

  @Bind()
  renderOtherOptions() {
    const { unitInfo = {}, form, data = {}, gridFixedOptions = [] } = this.props;
    const { getFieldDecorator = () => {} } = form;
    const { formRow, formCol, gridSeq, gridFixed, rowSpan, colSpan } = data;
    const { unitType } = unitInfo;
    const isFormType = unitType === 'FORM' || unitType === 'QUERYFORM';
    if (isFormType) {
      return (
        <Row className={styles['unit-editor-form2']}>
          <Col span={11}>
            <FormItem
              label={`${intl.get('hpfm.individuationUnit.model.individuationUnit.row').d('行')}:`}
              {...formLayout2}
            >
              {getFieldDecorator('formRow', {
                initialValue: formRow,
              })(<InputNumber style={{ width: '100%' }} />)}
            </FormItem>
          </Col>
          <Col span={11} offset={2}>
            <FormItem
              label={`${intl.get('hpfm.individuationUnit.model.individuationUnit.col').d('列')}:`}
              {...formLayout2}
            >
              {getFieldDecorator('formCol', {
                initialValue: formCol,
              })(<InputNumber style={{ width: '100%' }} />)}
            </FormItem>
          </Col>
          <Col span={11}>
            <FormItem
              label={intl.get('hpfm.individual.model.config.rowSpan').d('跨行')}
              {...formLayout2}
            >
              {form.getFieldDecorator('rowSpan', {
                initialValue: rowSpan || 1,
              })(<InputNumber style={{ width: '100%' }} precision={0} min={1} />)}
            </FormItem>
          </Col>
          <Col span={11} offset={2}>
            <FormItem
              label={intl.get('hpfm.individual.model.config.colSpan').d('跨列')}
              {...formLayout2}
            >
              {form.getFieldDecorator('colSpan', {
                initialValue: colSpan || 1,
              })(<InputNumber style={{ width: '100%' }} precision={0} min={1} />)}
            </FormItem>
          </Col>
        </Row>
      );
    }
    if (unitType === 'GRID') {
      return (
        <Row className={styles['unit-editor-form2']}>
          <Col span={11}>
            <FormItem
              label={intl.get('hpfm.individuationUnit.model.individuationUnit.position').d('位置')}
              {...formLayout2}
            >
              {getFieldDecorator('gridSeq', {
                initialValue: gridSeq,
              })(<InputNumber style={{ width: '100%' }} />)}
            </FormItem>
          </Col>
          <Col span={11} offset={2}>
            <FormItem
              label={intl.get('hpfm.individuationUnit.model.individuationUnit.fixed').d('冻结')}
              {...formLayout2}
            >
              {getFieldDecorator('gridFixed', {
                initialValue: gridFixed,
              })(
                <Select allowClear style={{ width: '100%' }}>
                  {gridFixedOptions.map((item) => (
                    <Option value={item.value}>{item.meaning}</Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          {/* <Col span={12}>
              <FormItem
                label={intl.get('hpfm.individuationUnit.model.individuationUnit.width').d('宽度')}
                {...formsLayouts}
              >
                {getFieldDecorator('gridWidth', {
                  initialValue: gridWidth,
                })(<InputNumber />)}
              </FormItem>
            </Col> */}
        </Row>
      );
    }
    if (unitType === 'FILTER' || unitType === 'TABPANE' || unitType === 'COLLAPSE') {
      return (
        <Row className={styles['unit-editor-form2']}>
          <Col span={11}>
            <FormItem
              label={intl.get('hpfm.individuationUnit.model.individuationUnit.position').d('位置')}
              {...formLayout2}
            >
              {getFieldDecorator('gridSeq', {
                initialValue: gridSeq,
              })(<InputNumber />)}
            </FormItem>
          </Col>
        </Row>
      );
    }
  }

  @Bind()
  handleChangeField(e, record) {
    const { fieldCategoryMeaning, fieldName, fieldCodeCamel } = record;
    const { form } = this.props;
    form.setFieldsValue({ fieldName });
    form.setFieldsValue({
      fieldName,
      fieldAlias: fieldCodeCamel,
      fieldCategoryMeaning,
    });
  }

  @Bind()
  toggleParamsModal() {
    const { paramVisible } = this.state;
    this.setState({
      paramVisible: !paramVisible,
    });
  }

  @Bind()
  saveParamList(paramList) {
    const { data } = this.props;
    data.paramList = paramList;
  }

  @Bind()
  handleClose() {
    const { data, handleClose = () => {} } = this.props;
    data.paramList = this.state.backUpParamList;
    handleClose();
  }

  render() {
    const {
      unitInfo = {},
      data = {},
      visible,
      readOnly,
      relationModals = [],
      renderOptions = [],
      condOptions = [],
      widgetType,
      saveLoading,
      createFieldLoading,
      unitList,
      fieldList,
      form: { getFieldDecorator = () => {}, getFieldValue = () => {}, setFieldsValue },
    } = this.props;
    const {
      field = {},
      modelId,
      fieldVisible,
      fieldRequired,
      fieldEditable,
      fieldName,
      fieldCode,
      fieldAlias,
      labelCol,
      wrapperCol,
      defaultActive,
      bindField,
      renderOptions: fieldRenderOptions,
    } = data;
    const { paramVisible } = this.state;
    const { modelName, fieldCategoryMeaning } = field;
    const { id, unitType } = unitInfo;
    const pureVirtual = unitType === 'TABPANE' || unitType === 'COLLAPSE';
    const isFormType = unitType === 'FORM' || unitType === 'QUERYFORM';
    const isCreate = isEmpty(data);
    const title = isCreate ? getAddFieldAlias(unitType) : getEditFieldAlias(unitType);
    return (
      <Drawer
        width={400}
        title={title}
        visible={visible}
        closable
        destroyOnClose
        onClose={this.handleClose}
      >
        <Form className={styles['unit-editor-form2']}>
          <FormItem style={{ display: isCreate && !pureVirtual ? 'block' : 'none' }}>
            {getFieldDecorator('isModelField', {
              initialValue: pureVirtual || (!isCreate && modelId == -1) ? 0 : 1,
            })(
              <Checkbox
                checkedValue={1}
                unCheckedValue={0}
                onChange={(v) =>
                  setFieldsValue({
                    modelId: !v.target.checked ? -1 : (relationModals[0] || {}).modelId,
                  })
                }
              >
                {intl
                  .get('hpfm.individuationUnit.model.individuationUnit.isModelField')
                  .d('创建模型字段')}
              </Checkbox>
            )}
          </FormItem>
          <FormItem
            label={getDefaultActiveAlias(unitType)}
            labelCol={{ span: 9 }}
            wrapperCol={{ span: 15 }}
            style={{ display: pureVirtual ? 'block' : 'none' }}
          >
            {getFieldDecorator('defaultActive', {
              initialValue: isCreate ? -1 : defaultActive,
            })(
              <Select style={{ width: '100%' }}>
                {condOptions.map((item) => (
                  <Option value={Number(item.value)}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        </Form>
        <Form layout="vertical" className={styles['unit-editor-form']}>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.modelName')
              .d('所属模型')}
            style={{ display: getFieldValue('isModelField') == 1 ? 'block' : 'none' }}
          >
            {getFieldDecorator('modelId', {
              initialValue:
                getFieldValue('isModelField') == 0
                  ? -1
                  : !isCreate
                  ? modelName
                  : (relationModals[0] || {}).modelId,
              rules: [
                {
                  required: isCreate,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.modelName')
                        .d('所属模型'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.modelName')
                        .d('所属模型')}不能为空`
                    ),
                },
              ],
            })(
              !isCreate ? (
                <Input disabled />
              ) : (
                <Select>
                  {relationModals.map((item) => (
                    <Option value={item.modelId}>{item.modelName}</Option>
                  ))}
                </Select>
              )
            )}
          </FormItem>
          <FormItem label={getFieldCodeAlias(unitType)}>
            {getFieldValue('isModelField') == 1
              ? getFieldDecorator('fieldId', {
                  initialValue: getFieldValue('isModelField') == 0 ? -1 : !isCreate && fieldCode,
                  rules: [
                    {
                      required: isCreate,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: getFieldCodeAlias(unitType),
                      }),
                    },
                  ],
                })(
                  !isCreate ? (
                    <Input disabled />
                  ) : (
                    <Lov
                      disabled={!getFieldValue('modelId')}
                      code={getSingleTenantValueCode('HPFM.CUST.UNIT_FIELD.VIEW')}
                      queryParams={{
                        modelId: getFieldValue('modelId'),
                        unitId: id,
                      }}
                      lovOptions={{ displayField: 'fieldCode' }}
                      onChange={this.handleChangeField}
                    />
                  )
                )
              : getFieldDecorator('fieldId', { initialValue: -1 })}
            {getFieldValue('isModelField') == 0 &&
              getFieldDecorator('fieldCode', {
                initialValue: fieldCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: getFieldCodeAlias(unitType),
                    }),
                  },
                ],
              })(<Input trimAll inputChinese={false} disabled={!isCreate} />)}
          </FormItem>
          <FormItem label={getFieldNameAlias(unitType)}>
            {getFieldDecorator('fieldName', {
              initialValue: !isCreate ? fieldName : null,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: getFieldNameAlias(unitType),
                    })
                    .d(`${getFieldNameAlias(unitType)}不能为空`),
                },
              ],
            })(<Input />)}
          </FormItem>
          {getFieldValue('isModelField') == 1 ? (
            <FormItem
              label={intl
                .get('hpfm.individuationUnit.model.individuationUnit.fieldAlias')
                .d('字段别名')}
            >
              {getFieldDecorator('fieldAlias', {
                initialValue: !isCreate ? fieldAlias : undefined,
              })(<Input />)}
            </FormItem>
          ) : null}
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.fieldType')
              .d('字段类型')}
            style={{ display: getFieldValue('isModelField') == 1 ? 'block' : 'none' }}
          >
            {getFieldDecorator('fieldCategoryMeaning', {
              initialValue: !isCreate ? fieldCategoryMeaning : '',
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.bindField')
              .d('字段绑定')}
            style={{ display: !pureVirtual ? 'block' : 'none' }}
          >
            {getFieldDecorator('bindField', {
              initialValue: bindField,
            })(<Input trim inputChinese={false} />)}
          </FormItem>
          {isFormType ? (
            <FormItem
              label={intl
                .get('hpfm.individuationUnit.model.individuationUnit.labelWrapperCol')
                .d('标签组件比例')}
            >
              {getFieldDecorator('labelCol', {
                initialValue: labelCol,
              })(
                <Select
                  allowClear
                  showSearch
                  style={{ width: '46%', float: 'left', marginRight: '8%' }}
                  placeholder={intl
                    .get('hpfm.individuationUnit.model.individuationUnit.label')
                    .d('标签')}
                >
                  {colOptions.map((i) => (
                    <Option value={i}>{i}</Option>
                  ))}
                </Select>
              )}
              {getFieldDecorator('wrapperCol', {
                initialValue: wrapperCol,
              })(
                <Select
                  allowClear
                  showSearch
                  style={{ width: '46%' }}
                  placeholder={intl
                    .get('hpfm.individuationUnit.model.individuationUnit.wrapper')
                    .d('组件')}
                >
                  {colOptions.map((i) => (
                    <Option value={i}>{i}</Option>
                  ))}
                </Select>
              )}
            </FormItem>
          ) : null}
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.renderType')
              .d('渲染方式')}
          >
            {getFieldDecorator('renderOptions', {
              initialValue:
                getFieldValue('isModelField') == 1 ? fieldRenderOptions || 'WIDGET' : 'TEXT',
              rules: [
                {
                  required: getFieldValue('isModelField') == 1,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.renderType')
                        .d('渲染方式'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.renderType')
                        .d('渲染方式')}不能为空`
                    ),
                },
              ],
            })(
              <Select>
                {renderOptions.map((item) => {
                  if (isCreate && readOnly && item.value === 'FORM') {
                    return null;
                  }
                  return <Option value={item.value}>{item.meaning}</Option>;
                })}
              </Select>
            )}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.widgetType')
              .d('组件类型')}
            style={{ display: getFieldValue('renderOptions') === 'WIDGET' ? 'block' : 'none' }}
          >
            {getFieldDecorator('fieldWidget', {
              initialValue: ((data.field || {}).modelFieldWidget || {}).fieldWidget,
            })(
              <Select disabled={getFieldValue('isModelField') == 1}>
                {widgetType.map((item) => (
                  <Option value={item.value}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        </Form>
        <Form className={styles['unit-editor-form2']} style={{ width: '69%' }}>
          <FormItem
            {...formsLayouts}
            label={intl.get('hpfm.individuationUnit.model.individuationUnit.visible').d('显示')}
            style={{ marginBottom: 0 }}
          >
            {getFieldDecorator('fieldVisible', {
              initialValue: fieldVisible === undefined ? -1 : fieldVisible,
            })(
              <Select style={{ width: '93%' }}>
                {condOptions.map((item) => (
                  <Option value={Number(item.value)}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formsLayouts}
            label={intl.get('hpfm.individuationUnit.model.individuationUnit.editable').d('编辑')}
            style={{
              display:
                getFieldValue('renderOptions') === 'WIDGET' && getFieldValue('isModelField') == 1
                  ? 'block'
                  : 'none',
              marginBottom: 0,
            }}
          >
            {getFieldDecorator('fieldEditable', {
              initialValue: fieldEditable === undefined ? -1 : fieldEditable,
            })(
              <Select style={{ width: '93%' }}>
                {condOptions.map((item) => (
                  <Option value={Number(item.value)}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            {...formsLayouts}
            label={intl.get('hpfm.individuationUnit.model.individuationUnit.required').d('必输')}
            style={{
              display:
                getFieldValue('renderOptions') === 'WIDGET' && getFieldValue('isModelField') == 1
                  ? 'block'
                  : 'none',
              marginBottom: 0,
            }}
          >
            {getFieldDecorator('fieldRequired', {
              initialValue: fieldRequired === undefined ? -1 : fieldRequired,
            })(
              <Select style={{ width: '93%' }}>
                {condOptions.map((item) => (
                  <Option value={Number(item.value)}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
        </Form>
        <Form style={{ marginBottom: 50 }}>
          {this.renderOtherOptions()}
          {((data.field || {}).modelFieldWidget || {}).fieldWidget === 'LOV' && (
            <Row>
              <Button
                icon="setting"
                type="primary"
                onClick={this.toggleParamsModal}
                style={{ width: '100%' }}
              >
                {intl.get('hpfm.individual.common.setLovParams').d('设置值集参数')}
                <Badge
                  style={{ marginLeft: '8px', height: '16px', lineHeight: '16px' }}
                  count={(data.paramList || []).length}
                />
              </Button>
            </Row>
          )}
        </Form>
        <div className={styles['model-bottom-button']}>
          <Button
            style={{ marginRight: 8 }}
            disabled={createFieldLoading || saveLoading || false}
            onClick={this.handleClose}
          >
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            htmlType="submit"
            loading={createFieldLoading || saveLoading || false}
            onClick={isCreate ? this.create : this.save}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
        {paramVisible && (
          <ParamsModal
            type="unit"
            id={id}
            unitList={unitList}
            fieldList={fieldList}
            paramList={data.paramList}
            onSave={this.saveParamList}
            visible={paramVisible}
            onClose={this.toggleParamsModal}
          />
        )}
      </Drawer>
    );
  }
}
