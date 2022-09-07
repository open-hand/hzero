/**
 * RuleForm - 计费规则表单
 * @date: 2019/8/29
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import moment from 'moment';
import { Form, Input, Row, Col, InputNumber, Select, DatePicker } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import intl from 'utils/intl';
import { dateRender } from 'utils/renderer';
import { getDateFormat } from 'utils/utils';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT, DEFAULT_DATE_FORMAT } from 'utils/constants';

const FormItem = Form.Item;
const promptCode = 'hchg.serviceCharge.model.serviceCharge';

/**
 * 计费规则表单
 * @extends {Component} - React.Component
 * @reactProps {string} type - 计费类型
 * @reactProps {boolean} isPublished - 计费组类型是否为已发布
 * @reactProps {boolean} isCreate - 是否为新建
 * @reactProps {object} ruleDetail - 计费规则详情
 * @reactProps {number} chargeGroupId - 计费组id
 * @reactProps {array} chargeMethodTypes - 计费方式值集
 * @reactProps {array} chargeTypes - 计费类型值集
 * @reactProps {array} Types - 计量单位值集
 * @reactProps {array} chargeBasisTypes - 计费单位值集
 * @reactProps {array} intervalCycleTypes - 阶梯周期值集
 * @reactProps {array} intervalMeasureTypes - 阶梯单位值集
 * @reactProps {function} onChargeTypeChange - 切换计费类型
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class RuleForm extends PureComponent {
  /**
   * 切换计费方式
   * @param {string} value - 计费方式
   */
  @Bind()
  handleChangeChargeMethodType(value) {
    const {
      form: { setFieldsValue = () => {}, getFieldValue = () => {} },
      onChargeInfoChange = () => {},
    } = this.props;
    switch (value) {
      case 'PRICE':
        setFieldsValue({
          measureBasis: 'TIMES',
          chargeType: 'FIXED',
          chargeBasis: 'AMOUNT',
          quantity: 1,
        });
        break;
      case 'DISCOUNT':
        setFieldsValue({ measureBasis: 'BILL_CYCLE', quantity: 1, chargeBasis: 'DISCOUNT' });
        break;
      case 'MINUS':
        setFieldsValue({ measureBasis: 'BILL_CYCLE', quantity: 1, chargeBasis: 'AMOUNT' });
        break;
      case 'CYCLE':
        setFieldsValue({ measureBasis: undefined, chargeType: 'FIXED', chargeBasis: 'AMOUNT' });
        break;
      default:
        setFieldsValue({ measureBasis: undefined, chargeType: 'FIXED', chargeBasis: undefined });
        break;
    }
    onChargeInfoChange(value, getFieldValue('chargeType'));
  }

  /**
   * 切换计量单位
   * @param {string} value - 计量单位
   */
  @Bind()
  handleChangeMeasureBasis(value) {
    const {
      form: { setFieldsValue = () => {} },
      type,
    } = this.props;
    if (type === 'POST' && (value === 'BILL_CYCLE' || value === 'TIMES')) {
      setFieldsValue({ quantity: 1 });
    }
  }

  /**
   * 切换计费类型
   * @param {string} value - 计费类型
   */
  @Bind()
  handleChangeChargeType(value) {
    const {
      onChargeInfoChange = () => {},
      form: { setFieldsValue = () => {}, getFieldValue = () => {} },
    } = this.props;
    if (value === 'INTERVAL') {
      setFieldsValue({ charge: undefined });
    }
    const chargeMethod = getFieldValue('chargeMethod');
    onChargeInfoChange(chargeMethod, value);
  }

  /**
   * 渲染计量单位
   */
  @Bind()
  renderMeasureBasisSelect() {
    const {
      measureBasisTypes = [],
      form: { getFieldValue = () => {} },
    } = this.props;
    const chargeMethod = getFieldValue('chargeMethod') || '';
    const filteredTypes = measureBasisTypes.filter(item => item.parentValue === chargeMethod);
    //  计费方式为折扣或者优惠时，计量单位字段不可选择，默认为：账单周期
    const isDisabled = ['DISCOUNT', 'MINUS', 'PRICE'].includes(getFieldValue('chargeMethod'));
    return (
      <Select allowClear onSelect={this.handleChangeMeasureBasis} disabled={isDisabled}>
        {filteredTypes.length &&
          filteredTypes.map(item => (
            <Select.Option value={item.value} key={item.value}>
              {item.meaning}
            </Select.Option>
          ))}
      </Select>
    );
  }

  /**
   * 渲染计费单位
   */
  @Bind()
  renderChargeBasisSelect() {
    const {
      chargeBasisTypes = [],
      form: { getFieldValue = () => {} },
    } = this.props;
    let filteredTypes = [...chargeBasisTypes];
    // 计费方式为优惠时，计费单位可选择次数、金额
    if (getFieldValue('chargeMethod') === 'MINUS') {
      filteredTypes = chargeBasisTypes.filter(
        item => item.value === 'AMOUNT' || item.value === 'TIMES'
      );
    }

    const mainChargeMethods = ['PRICE', 'CYCLE', 'DISCOUNT'];
    return (
      <Select allowClear disabled={mainChargeMethods.includes(getFieldValue('chargeMethod'))}>
        {filteredTypes.length &&
          filteredTypes.map(item => (
            <Select.Option value={item.value} key={item.value}>
              {item.meaning}
            </Select.Option>
          ))}
      </Select>
    );
  }

  @Bind()
  renderCharge() {
    const {
      form: { getFieldValue = () => {} },
    } = this.props;
    let otherProps = {};
    if (getFieldValue('chargeMethod') === 'DISCOUNT') {
      otherProps = {
        max: 1,
        min: 0,
        step: 0.1,
      };
    }
    return (
      <InputNumber
        style={{ width: '100%' }}
        disabled={getFieldValue('chargeType') === 'INTERVAL'}
        {...otherProps}
      />
    );
  }

  /**
   * 渲染计费组已发布的规则表单
   */
  @Bind()
  renderPublishedForm() {
    const { ruleDetail = {}, type } = this.props;
    const {
      priority,
      ruleName,
      chargeMethod,
      chargeType,
      quantity,
      charge,
      discountLineName,
      startDate,
      endDate,
      intervalMeasureMeaning,
      chargeMethodMeaning,
      chargeTypeMeaning,
      measureBasisMeaning,
      chargeBasisMeaning,
      intervalCycleMeaning,
    } = ruleDetail;

    return (
      <Form>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.priority`).d('优先级')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {priority}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.ruleName`).d('说明')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {ruleName}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.chargeMethod`).d('计费方式')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {chargeMethodMeaning}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.chargeType`).d('计费类型')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {chargeTypeMeaning}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.measureBasis`).d('计量单位')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {measureBasisMeaning}
            </FormItem>
          </Col>

          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.quantity`).d('计量')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {quantity}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.chargeBasis`).d('计费单位')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {chargeBasisMeaning}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.charge`).d('计费')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {charge || intl.get(`${promptCode}.empty`).d('暂无')}
            </FormItem>
          </Col>
        </Row>
        <Row>
          {type === 'PRE' && (chargeMethod === 'DISCOUNT' || chargeMethod === 'MINUS') && (
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get(`${promptCode}.discountLineId`).d('折扣行')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {discountLineName}
              </FormItem>
            </Col>
          )}
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.startDate`).d('生效日期从')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {startDate ? dateRender(startDate) : intl.get(`${promptCode}.empty`).d('暂无')}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.endDate`).d('生效日期至')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {endDate ? dateRender(endDate) : intl.get(`${promptCode}.empty`).d('暂无')}
            </FormItem>
          </Col>
          {chargeType === 'INTERVAL' && (
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get(`${promptCode}.intervalCycle`).d('阶梯周期')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {intervalCycleMeaning}
              </FormItem>
            </Col>
          )}
        </Row>
        {chargeType === 'INTERVAL' && (
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get(`${promptCode}.intervalMeasure`).d('阶梯单位')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {intervalMeasureMeaning}
              </FormItem>
            </Col>
          </Row>
        )}
      </Form>
    );
  }

  /**
   * 渲染计费组未发布的规则表单
   */
  @Bind()
  renderForm() {
    const {
      form: { getFieldDecorator, getFieldValue = () => {} },
      ruleDetail = {},
      chargeGroupId,
      type,
      isCreate,
      chargeMethodTypes,
      chargeTypes,
      intervalCycleTypes,
      intervalMeasureTypes,
    } = this.props;
    const {
      priority,
      ruleName,
      chargeMethod,
      chargeType,
      measureBasis,
      quantity,
      chargeBasis,
      charge,
      discountLineId,
      discountLineName,
      startDate,
      endDate,
      intervalCycle,
      intervalMeasure,
    } = ruleDetail;
    return (
      <Form>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.priority`).d('优先级')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('priority', {
                initialValue: priority,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.priority`).d('优先级'),
                    }),
                  },
                ],
              })(<InputNumber style={{ width: '100%' }} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.ruleName`).d('说明')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('ruleName', {
                initialValue: ruleName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.ruleName`).d('说明'),
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.chargeMethod`).d('计费方式')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('chargeMethod', {
                initialValue: chargeMethod,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.chargeMethod`).d('计费方式'),
                    }),
                  },
                ],
              })(
                <Select
                  allowClear
                  onChange={this.handleChangeChargeMethodType}
                  disabled={!isCreate}
                >
                  {chargeMethodTypes.length &&
                    chargeMethodTypes.map(item => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.chargeType`).d('计费类型')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('chargeType', {
                initialValue: chargeType,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.chargeType`).d('计费类型'),
                    }),
                  },
                ],
              })(
                <Select
                  allowClear
                  onChange={this.handleChangeChargeType}
                  disabled={
                    (type === 'PRE' && getFieldValue('chargeMethod') === 'PRICE') ||
                    getFieldValue('chargeMethod') === 'CYCLE' ||
                    !isCreate
                  }
                >
                  {chargeTypes.length &&
                    chargeTypes.map(item => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.measureBasis`).d('计量单位')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('measureBasis', {
                initialValue: measureBasis,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.measureBasis`).d('计量单位'),
                    }),
                  },
                ],
              })(this.renderMeasureBasisSelect())}
            </FormItem>
          </Col>

          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.quantity`).d('计量')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('quantity', {
                initialValue: quantity,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.quantity`).d('计量'),
                    }),
                  },
                ],
              })(
                <InputNumber
                  style={{ width: '100%' }}
                  disabled={['PRICE', 'DISCOUNT', 'MINUS'].includes(getFieldValue('chargeMethod'))}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.chargeBasis`).d('计费单位')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('chargeBasis', {
                initialValue: chargeBasis,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.chargeBasis`).d('计费单位'),
                    }),
                  },
                ],
              })(this.renderChargeBasisSelect())}
            </FormItem>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.charge`).d('计费')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('charge', {
                initialValue: charge,
              })(this.renderCharge())}
            </FormItem>
          </Col>
        </Row>
        <Row>
          {type === 'PRE' &&
            (getFieldValue('chargeMethod') === 'DISCOUNT' ||
              getFieldValue('chargeMethod') === 'MINUS') && (
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get(`${promptCode}.discountLineId`).d('折扣行')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('discountLineId', {
                    initialValue: discountLineId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`${promptCode}.discountLineId`).d('折扣行'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      disabled={!isCreate}
                      code="HCHG.CHARGE.RULE"
                      queryParams={{ chargeGroupId }}
                      textValue={discountLineName}
                    />
                  )}
                </FormItem>
              </Col>
            )}
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.startDate`).d('生效日期从')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('startDate', {
                initialValue: startDate ? moment(startDate, DEFAULT_DATE_FORMAT) : undefined,
              })(
                <DatePicker
                  style={{ width: '100%' }}
                  format={getDateFormat()}
                  disabledDate={currentDate =>
                    getFieldValue('endDate') &&
                    moment(getFieldValue('endDate')).isBefore(currentDate, 'day')}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col {...FORM_COL_2_LAYOUT}>
            <FormItem
              label={intl.get(`${promptCode}.endDate`).d('生效日期至')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('endDate', {
                initialValue: endDate ? moment(endDate, DEFAULT_DATE_FORMAT) : undefined,
              })(
                <DatePicker
                  style={{ width: '100%' }}
                  format={getDateFormat()}
                  disabledDate={currentDate =>
                    getFieldValue('startDate') &&
                    moment(getFieldValue('startDate')).isAfter(currentDate, 'day')}
                />
              )}
            </FormItem>
          </Col>
          {getFieldValue('chargeType') === 'INTERVAL' && (
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get(`${promptCode}.intervalCycle`).d('阶梯周期')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('intervalCycle', {
                  initialValue: intervalCycle,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${promptCode}.intervalCycle`).d('阶梯周期'),
                      }),
                    },
                  ],
                })(
                  <Select allowClear disabled>
                    {intervalCycleTypes.length &&
                      intervalCycleTypes.map(item => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          )}
        </Row>
        {getFieldValue('chargeType') === 'INTERVAL' && (
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get(`${promptCode}.intervalMeasure`).d('阶梯单位')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('intervalMeasure', {
                  initialValue: intervalMeasure,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${promptCode}.intervalMeasure`).d('阶梯单位'),
                      }),
                    },
                  ],
                })(
                  <Select allowClear disabled>
                    {intervalMeasureTypes.length &&
                      intervalMeasureTypes.map(item => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
        )}
      </Form>
    );
  }

  render() {
    return this.props.isPublished ? this.renderPublishedForm() : this.renderForm();
  }
}
