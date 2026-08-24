/* eslint-disable eqeqeq */
/* eslint-disable jsx-a11y/click-events-have-key-events */
/* eslint-disable no-underscore-dangle */
/* eslint-disable jsx-a11y/anchor-is-valid */
import React from 'react';
import { Form, Modal, Popconfirm, Icon, Select, Input } from 'hzero-ui';
import intl from 'utils/intl';
import EditTable from 'components/EditTable';
import TLEditor from 'components/TLEditor';
import { getEditTableData } from 'utils/utils';
import { isNil } from 'lodash';
import { Bind } from 'lodash-decorators';
import { queryMapIdpValue } from 'services/api';
import { getContextParams } from 'components/Customize/hzero/customizeTool';
import Lov from 'components/Lov';
import styles from './index.less';

const FormItem = Form.Item;
const { Option } = Select;

function getFilter(type) {
  switch (type) {
    case 'INPUT_NUMBER':
      return ['>', '<', '>=', '<=', '=', '!='];
    case 'DATE_PICKER':
      return ['BEFORE', 'AFTER', '~BEFORE', '~AFTER', 'SAME', 'NOTSAME'];
    default:
      return ['LIKE', 'UNLIKE', '~LIKE', '~UNLIKE', '=', '!=', 'ISNULL', 'NOTNULL'];
  }
}

export default class ConditionModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = this.initState();
  }

  // componentDidUpdate(prev) {
  //   if (this.props.visible && !prev.visible) {
  //     // eslint-disable-next-line react/no-did-update-set-state
  //     this.setState(this.initState());
  //   }
  // }

  initState() {
    const { selfValidator, fieldList = {} } = this.props;
    let { conditionList, validatorList } = this.props;
    if (selfValidator) {
      conditionList = selfValidator.lines || [];
      validatorList = selfValidator.valids || [];
    }
    const newLines = [...conditionList];
    if (conditionList.length === 0) newLines.push({ conCode: 1 });
    const querySelectOptions = {};
    const conditionNo = [1, 1];
    conditionList.forEach((i) => {
      conditionNo[i.conCode] = 1;
      querySelectOptions[i.conCode] = i.sourceFieldValueCode;
    });
    queryMapIdpValue(querySelectOptions)
      .then((res = {}) => {
        this.setState({
          selectOptions: (res && !res.failed && res) || {},
        });
      })
      .finally(() => {
        this.setState({
          selectOptions: {},
          selectOptionsLoading: false,
        });
      });
    const validConCodeList = [];
    const validatorDelKey = [];
    validatorList.forEach((i) => {
      validConCodeList[i.conCode] = 1;
    });
    validConCodeList.forEach((i, index) => {
      if (i !== 1) {
        validatorDelKey.push(index);
      }
    });
    const fieldMapObj = {};
    Object.keys(fieldList).forEach((item) => {
      const subList = fieldList[item];
      subList.forEach((field) => {
        fieldMapObj[field.modelFieldId] = field;
      });
    });
    return {
      fieldMapObj, // 关联字段配置信息集合
      conditionNo,
      conditionList: newLines,
      selectOptions: {},
      selectOptionsLoading: true,
      validatorDelKey,
      // 每一次初始化重新生成status
      validatorList:
        validatorList.length > 0
          ? validatorList.map((i) => ({ ...i, _status: 'update' }))
          : [{ conCode: 0, _status: 'create' }],
    };
  }

  @Bind()
  onOk() {
    const { form, onClose, updateSelfValidator, headerProps } = this.props;
    const { conditionList, validatorList } = this.state;
    const validateData = getEditTableData(validatorList, ['_status']) || [];
    if (validatorList.length > 0 && validateData.length === 0) {
      return;
    }
    form.validateFields((err, values) => {
      if (err) return;
      const newConditionList = conditionList
        .map((lineData) => {
          if (!lineData) return false;
          const { conCode } = lineData;
          return {
            ...lineData,
            sourceUnitId: values[`sourceUnitId#${conCode}`],
            sourceUnitCode: values[`sourceUnitCode#${conCode}`],
            sourceFieldId: values[`sourceFieldId#${conCode}`],
            sourceFieldCode: values[`sourceFieldCode#${conCode}`],
            sourceModelId: values[`sourceModelId#${conCode}`],
            sourceFieldValueCode: values[`sourceFieldValueCode#${conCode}`],
            conExpression: values[`conExpression#${conCode}`],
            targetType: values[`targetType#${conCode}`],
            targetValue: values[`targetValue#${conCode}`],
            targetValueMeaning: values[`targetValueMeaning#${conCode}`],
            targetFieldId: values[`targetFieldId#${conCode}`],
            targetFieldCode: values[`targetFieldCode#${conCode}`],
            targetModelId: values[`targetModelId#${conCode}`],
          };
        })
        .filter(Boolean);
      if (typeof updateSelfValidator === 'function') {
        updateSelfValidator({
          ...headerProps,
          conType: 'valid',
          lines: newConditionList,
          valids: validateData,
        });
      }
      onClose();
    });
  }

  onDelete(conCode) {
    const { conditionList } = this.state;
    const delNo = conCode;
    this.delNo(delNo);
    this.clearFields(1, conCode);
    this.setState({
      conditionList: conditionList.map((_) => (_ && conCode === _.conCode ? undefined : _)),
    });
  }

  registerNo() {
    const { conditionNo } = this.state;
    for (let i = 1; i < conditionNo.length; i++) {
      if (conditionNo[i] === undefined) {
        conditionNo[i] = 1;
        return i;
      }
    }
    conditionNo[conditionNo.length] = 1;
    return conditionNo.length - 1;
  }

  delNo(no) {
    const { conditionNo } = this.state;
    conditionNo[no] = undefined;
  }

  @Bind()
  addMap() {
    const { conditionList } = this.state;
    const newNo = this.registerNo();
    this.setState({ conditionList: [{ conCode: newNo }, ...conditionList] });
  }

  getTargetEditComp(type, conCode, config = {}) {
    const { form } = this.props;
    const { selectOptions, selectOptionsLoading, fieldMapObj } = this.state;
    const modelFieldId = form.getFieldValue(`sourceFieldId#${conCode}`);
    const { sourceFieldValueCode, widgetType } = fieldMapObj[modelFieldId] || {};
    let comp;
    switch (type || widgetType) {
      case 'LOV':
        comp = (
          <Lov
            placeholder={intl.get('hpfm.individual.model.config.fieldValue').d('字段值')}
            code={form.getFieldValue(`sourceFieldValueCode#${conCode}`) || sourceFieldValueCode}
            textField={`targetValueMeaning#${conCode}`}
            textValue={config.targetValueMeaning}
            queryParams={getContextParams(form.getFieldValue(`paramList#${conCode}`), {
              isConfig: true,
            })}
          />
        );
        break;
      case 'SELECT':
        comp = (
          <Select
            allowClear
            style={{ width: '100%' }}
            onChange={(v) => {
              const obj = (selectOptions[config.conCode] || []).find((i) => i.value === v) || {};
              form.setFieldsValue({ [`targetValueMeaning#${conCode}`]: obj.meaning });
            }}
          >
            {form.getFieldValue(`selectOptionsLoading#${conCode}`) || selectOptionsLoading ? (
              <Option key="loading">
                <Icon type="loading" />
              </Option>
            ) : (
              (selectOptions[config.conCode] || []).map((n) => (
                <Option value={n.value}>{n.meaning}</Option>
              ))
            )}
          </Select>
        );
        break;
      default:
        comp = (
          <Input
            style={{ width: '100%' }}
            placeholder={intl.get('hpfm.individual.model.config.fieldValue').d('字段值')}
          />
        );
    }
    return (
      <FormItem style={{ maxWidth: '200px', flex: 1 }}>
        {form.getFieldDecorator(`targetValue#${conCode}`, {
          initialValue: config.targetValue,
          rules: [
            {
              required: true,
              message: intl.get('hzero.common.validation.notNull', {
                name: intl.get('hpfm.individual.model.config.fieldValue').d('字段值'),
              }),
            },
          ],
        })(comp)}
        {form.getFieldDecorator(`targetValueMeaning#${conCode}`, {
          initialValue: config.targetValueMeaning,
        })}
        {form.getFieldDecorator(`selectOptionsLoading#${conCode}`, {
          initialValue: false,
        })}
      </FormItem>
    );
  }

  setFieldOneInfo(id, conCode, config) {
    const { fieldList, form } = this.props;
    const { selectOptions } = this.state;
    const fieldObj = (fieldList[form.getFieldValue(`sourceUnitId#${conCode}`)] || []).find(
      (i) => i.modelFieldId == id
    );
    if (fieldObj) {
      if (fieldObj.widgetType === 'SELECT') {
        queryMapIdpValue({ lovCode: fieldObj.sourceFieldValueCode })
          .then((res = {}) => {
            selectOptions[config.conCode] = (res && !res.failed && res.lovCode) || [];
            this.setState({
              selectOptions,
            });
          })
          .finally(() => {
            form.setFieldsValue({
              [`selectOptionsLoading#${conCode}`]: false,
            });
          });
      }
      form.setFieldsValue({
        [`paramList#${conCode}`]: fieldObj.paramList || [],
        [`selectOptionsLoading#${conCode}`]: true,
        [`sourceFieldWidget#${conCode}`]: fieldObj.widgetType,
        [`sourceFieldCode#${conCode}`]: fieldObj.unitFieldCode,
        [`sourceFieldValueCode#${conCode}`]: fieldObj.sourceFieldValueCode,
        [`sourceModelId#${conCode}`]: fieldObj.modelId,
      });
    }
  }

  setFieldTwoInfo(id, conCode) {
    const { form, unitId, fieldList } = this.props;
    const fieldObj = (fieldList[unitId] || []).find((i) => i.modelFieldId == id);
    if (fieldObj) {
      form.setFieldsValue({
        [`targetModelId#${conCode}`]: fieldObj.modelId,
        [`targetFieldCode#${conCode}`]: fieldObj.unitFieldCode,
      });
    }
  }

  @Bind()
  clearFields(level, conCode) {
    const { form } = this.props;
    switch (level) {
      case 1:
        form.setFieldsValue({
          [`sourceFieldWidget#${conCode}`]: undefined,
          [`sourceFieldId#${conCode}`]: undefined,
          [`sourceFieldCode#${conCode}`]: undefined,
          [`sourceFieldValueCode#${conCode}`]: undefined,
          [`sourceModelId#${conCode}`]: undefined,
          [`targetModelId#${conCode}`]: undefined,
          [`targetFieldCode#${conCode}`]: undefined,
          [`targetValue#${conCode}`]: undefined,
          [`targetValueMeaning#${conCode}`]: undefined,
          [`targetFieldId#${conCode}`]: undefined,
          [`selectOptionsLoading#${conCode}`]: false,
          [`paramList#${conCode}`]: [],
        });
        break;
      case 2:
        form.setFieldsValue({
          [`targetValue#${conCode}`]: undefined,
          [`targetValueMeaning#${conCode}`]: undefined,
          [`targetFieldId#${conCode}`]: undefined,
          [`selectOptionsLoading#${conCode}`]: false,
        });
        break;
      default:
    }
  }

  @Bind()
  delValidator(index) {
    const { validatorDelKey, validatorList } = this.state;
    const delData = validatorList.find((line) => line.conCode === index);
    if (!isNil(delData)) {
      validatorDelKey.push(delData.conCode);
    }
    this.setState({ validatorList: validatorList.filter((line) => line.conCode !== index) });
  }

  @Bind()
  addValidator() {
    const { validatorDelKey, validatorList } = this.state;
    let conCode = validatorList.length;
    if (validatorDelKey.length > 0) {
      [conCode] = validatorDelKey;
      this.setState({ validatorDelKey: validatorDelKey.slice(1) });
    }
    this.setState({ validatorList: [{ conCode, _status: 'create' }, ...validatorList] });
  }

  getValidatorColumns() {
    const { conditionNo } = this.state;
    return [
      {
        title: intl.get('hpfm.individual.view.message.title.calculatLogic').d('筛选逻辑'),
        dataIndex: 'conExpression',
        width: 350,
        render: (val, record) => (
          <FormItem wrapperCol={{ span: 24 }}>
            {record.$form.getFieldDecorator(`conExpression`, {
              initialValue: val,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hpfm.individual.view.message.title.calculatLogic')
                      .d('筛选逻辑'),
                  }),
                },
                {
                  validator: (_, value, cb) => {
                    const array = (value !== undefined && value.match(/\s?\d+\s?/g)) || [];
                    for (let i = 0; i < array.length; i += 1) {
                      const no = array[i].match(/(\d+)/)[0];
                      if (conditionNo[no] !== 1) {
                        cb(
                          intl
                            .get('hpfm.individual.model.config.conditionValidator', {
                              no,
                            })
                            .d(`条件${no}不存在`)
                        );
                        return;
                      }
                    }
                    cb();
                  },
                },
                {
                  validator: (_, value, cb) => {
                    const array = (value !== undefined && value.match(/[^0-9()\s]+/g)) || [];
                    const equalOrAnd =
                      array.length > 0
                        ? array.reduce((prev, next) => prev && /OR|AND/.test(next), true)
                        : false;
                    if (array.length > 0 && !equalOrAnd) {
                      cb(
                        intl
                          .get('hpfm.individual.model.config.conditionValidator.tips1')
                          .d('不允许输入字母及 ( )  OR AND 以外的字符')
                      );
                      return;
                    }
                    cb();
                  },
                },
              ],
            })(<Input inputChinese={false} />)}
          </FormItem>
        ),
      },
      {
        title: intl.get('hpfm.individual.model.config.errorMessage').d('错误信息'),
        dataIndex: 'errorMessage',
        render: (val, record) => (
          <FormItem>
            {record.$form.getFieldDecorator(`errorMessage`, {
              initialValue: val,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.individual.model.config.errorMessage').d('错误信息'),
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.individual.model.config.errorMessage').d('错误信息')}
                field="errorMessage"
                token={record._token}
              />
            )}
          </FormItem>
        ),
      },
      {
        title: intl.get('hzero.common.action').d('操作'),
        dataIndex: '_op',
        width: 60,
        render: (_, record) => (
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录')}
            okText={intl.get('hzero.common.status.yes').d('是')}
            cancelText={intl.get('hzero.common.status.no').d('否')}
            onConfirm={() => this.delValidator(record.conCode)}
          >
            <a className="delete" role="button" style={{ color: '#333' }}>
              <Icon type="delete" />
            </a>
          </Popconfirm>
        ),
      },
    ];
  }

  render() {
    const { validatorList, conditionList } = this.state;
    const {
      visible,
      onClose,
      form,
      unitId,
      unitType,
      targetProp,
      unitList,
      fieldList,
      codes: { relationShip = [] },
    } = this.props;
    const isFormType = unitType === 'FORM' || unitType === 'QUERYFORM';
    return (
      <Modal
        destroyOnClose
        maskClosable
        width={1007}
        visible={visible}
        onCancel={onClose}
        onOk={this.onOk}
        bodyStyle={{ padding: '12px' }}
        wrapClassName={styles[`cond-modal-wrapper`]}
        title={intl.get('hpfm.individual.view.message.title.conditionConfig').d('条件配置')}
      >
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            marginLeft: '12px',
            marginRight: '16px',
          }}
        >
          <div className={styles.title}>
            {intl.get('hpfm.individual.view.message.title.conditionList').d('判断条件')}
          </div>
          <div
            onClick={this.addMap}
            style={{
              display: 'flex',
              lineHeight: '18px',
              height: '28px',
              alignItems: 'center',
              marginLeft: '8px',
              cursor: 'pointer',
            }}
          >
            <Icon
              type="plus-circle-o"
              style={{ fontSize: '18px', color: '#34a6f8', marginRight: '8px' }}
            />
            {intl.get('hzero.common.button.addCondition').d('添加条件')}
          </div>
        </div>
        <div style={{ minHeight: '150px' }}>
          {conditionList.map((i) => {
            if (!i) return null;
            const { conCode } = i;
            const relationIsNull =
              form.getFieldValue(`conExpression#${conCode}`) === 'ISNULL' ||
              form.getFieldValue(`conExpression#${conCode}`) === 'NOTNULL';
            return (
              <div className={styles['condition-row']}>
                <span className="conCode">{conCode}</span>
                <FormItem style={{ width: '200px' }}>
                  {form.getFieldDecorator(`sourceUnitId#${conCode}`, {
                    initialValue: i.sourceUnitId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.individual.model.config.unitWhich').d('所属单元'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      style={{ width: '100%' }}
                      placeholder={intl.get('hpfm.individual.model.config.unitWhich').d('所属单元')}
                      optionLabelProp="title"
                      dropdownClassName={styles['condition-dropdown']}
                      onChange={(v) => {
                        const obj = unitList.find((k) => k.unitId == v) || {};
                        form.setFieldsValue({ [`sourceUnitCode#${conCode}`]: obj.unitCode });
                        this.clearFields(1, conCode);
                      }}
                    >
                      {unitList.map((unit) => (
                        <Option
                          value={unit.unitId}
                          title={unit.unitName}
                          disabled={
                            unit.unitType === 'GRID' &&
                            (isFormType ||
                              (unitType === 'GRID' &&
                                (targetProp === 'visible' || unitId != unit.unitId)))
                          }
                        >
                          <div className="option-title">{unit.unitName}</div>
                          <div className="option-value">{unit.unitCode}</div>
                        </Option>
                      ))}
                    </Select>
                  )}
                  {form.getFieldDecorator(`sourceUnitCode#${conCode}`, {
                    initialValue: i.sourceUnitCode,
                  })}
                </FormItem>
                <FormItem style={{ maxWidth: '200px', flex: 1 }}>
                  {form.getFieldDecorator(`sourceFieldId#${conCode}`, {
                    initialValue: i.sourceFieldId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.individual.model.config.fieldSelect').d('字段选择'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      style={{ width: '100%' }}
                      placeholder={intl
                        .get('hpfm.individual.model.config.fieldSelect')
                        .d('字段选择')}
                      onChange={(id) => this.setFieldOneInfo(id, conCode, i)}
                      disabled={form.getFieldValue(`sourceUnitId#${conCode}`) === undefined}
                      optionLabelProp="title"
                      dropdownClassName={styles['condition-dropdown']}
                    >
                      {(fieldList[form.getFieldValue(`sourceUnitId#${conCode}`)] || []).map(
                        (f1) => (
                          <Option value={f1.modelFieldId} title={f1.unitFieldName}>
                            <div className="option-title">{f1.unitFieldName}</div>
                            <div className="option-value">{f1.unitFieldCode}</div>
                          </Option>
                        )
                      )}
                    </Select>
                  )}
                  {form.getFieldDecorator(`sourceModelId#${conCode}`, {
                    initialValue: i.sourceModelId,
                  })}
                  {form.getFieldDecorator(`sourceFieldWidget#${conCode}`, {
                    initialValue: i.sourceFieldWidget,
                  })}
                  {form.getFieldDecorator(`sourceFieldCode#${conCode}`, {
                    initialValue: i.sourceFieldCode,
                  })}
                  {form.getFieldDecorator(`sourceFieldValueCode#${conCode}`, {
                    initialValue: i.sourceFieldValueCode,
                  })}
                  {form.getFieldDecorator(`paramList#${conCode}`, {
                    initialValue: i.paramList || [],
                  })}
                </FormItem>
                <FormItem style={{ width: '85px' }}>
                  {form.getFieldDecorator(`conExpression#${conCode}`, {
                    initialValue: i.conExpression || '=',
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.individual.model.config.relation').d('关系'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      style={{ width: '100%' }}
                      placeholder={intl.get('hpfm.individual.model.config.relation').d('关系')}
                      dropdownClassName={styles['condition-dropdown']}
                    >
                      {relationShip
                        .filter((k) =>
                          getFilter(form.getFieldValue(`sourceFieldWidget#${conCode}`)).includes(
                            k.value
                          )
                        )
                        .map((k) => (
                          <Option value={k.value}>{k.meaning}</Option>
                        ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  style={{ flex: 1, maxWidth: '200px', display: relationIsNull ? 'none' : 'block' }}
                >
                  {form.getFieldDecorator(`targetType#${conCode}`, {
                    initialValue: i.targetType || 'fixed',
                    rules: [
                      {
                        required: !relationIsNull,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.individual.model.config.targetValueFrom')
                            .d('取值来源'),
                        }),
                      },
                    ],
                  })(
                    <Select
                      style={{ width: '100%' }}
                      placeholder={intl
                        .get('hpfm.individual.model.config.targetValueFrom')
                        .d('取值来源')}
                      dropdownClassName={styles['condition-dropdown']}
                      onChange={() => this.clearFields(2, conCode)}
                    >
                      <Option value="formNow">
                        {intl.get('hpfm.individual.model.config.formNow').d('本单元')}
                      </Option>
                      <Option value="fixed">
                        {intl.get('hpfm.individual.model.config.fixed').d('固定值')}
                      </Option>
                    </Select>
                  )}
                </FormItem>
                {form.getFieldValue(`targetType#${conCode}`) === 'formNow' ? (
                  <FormItem
                    style={{
                      maxWidth: '200px',
                      flex: 1,
                      display: relationIsNull ? 'none' : 'block',
                    }}
                  >
                    {form.getFieldDecorator(`targetFieldId#${conCode}`, {
                      initialValue: i.targetFieldId,
                      rules: [
                        {
                          required: !relationIsNull,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.individual.model.config.fieldSelect')
                              .d('字段选择'),
                          }),
                        },
                      ],
                    })(
                      <Select
                        style={{ width: '100%' }}
                        placeholder={intl
                          .get('hpfm.individual.model.config.fieldSelect')
                          .d('字段选择')}
                        onChange={(id) => this.setFieldTwoInfo(id, conCode)}
                        optionLabelProp="title"
                        dropdownClassName={styles['condition-dropdown']}
                      >
                        {(fieldList[unitId] || []).map((f2) => (
                          <Option value={f2.modelFieldId} title={f2.unitFieldName}>
                            <div className="option-title">{f2.unitFieldName}</div>
                            <div className="option-value">{f2.unitFieldCode}</div>
                          </Option>
                        ))}
                      </Select>
                    )}
                    {form.getFieldDecorator(`targetModelId#${conCode}`, {
                      initialValue: i.targetModelId,
                    })}
                    {form.getFieldDecorator(`targetFieldCode#${conCode}`, {
                      initialValue: i.targetFieldCode,
                    })}
                    {form.getFieldDecorator(`targetUnitId#${conCode}`, {
                      initialValue: unitId,
                    })}
                  </FormItem>
                ) : (
                  !relationIsNull &&
                  this.getTargetEditComp(
                    form.getFieldValue(`sourceFieldWidget#${conCode}`),
                    conCode,
                    i
                  )
                )}
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录')}
                  okText={intl.get('hzero.common.status.yes').d('是')}
                  cancelText={intl.get('hzero.common.status.no').d('否')}
                  onConfirm={() => this.onDelete(conCode)}
                >
                  {/* eslint-disable-next-line jsx-a11y/anchor-is-valid */}
                  <a
                    className="delete"
                    role="button"
                    style={{ color: '#333', position: 'absolute', right: '28px' }}
                  >
                    <Icon type="delete" />
                  </a>
                </Popconfirm>
              </div>
            );
          })}
        </div>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            marginLeft: '12px',
            marginRight: '16px',
          }}
        >
          <div className={styles.title}>
            {intl.get('hpfm.individual.view.message.title.validatorList').d('校验清单')}
          </div>
          <div
            onClick={this.addValidator}
            style={{
              display: 'flex',
              lineHeight: '18px',
              height: '28px',
              alignItems: 'center',
              marginLeft: '8px',
              cursor: 'pointer',
            }}
          >
            <Icon
              type="plus-circle-o"
              style={{ fontSize: '18px', color: '#34a6f8', marginRight: '8px' }}
            />
            {intl.get('hzero.common.button.addValidator').d('添加校验')}
          </div>
        </div>

        <div style={{ minHeight: '150px' }}>
          <EditTable
            bordered
            columns={this.getValidatorColumns()}
            dataSource={validatorList}
            pagination={false}
          />
        </div>
      </Modal>
    );
  }
}
