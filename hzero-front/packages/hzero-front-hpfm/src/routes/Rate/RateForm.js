import React, { PureComponent } from 'react';
import { DatePicker, Form, Input, InputNumber, Modal } from 'hzero-ui';
import moment from 'moment';
import { isEmpty, multiply, round } from 'lodash';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import Switch from 'components/Switch';

import { getCurrentOrganizationId, getDateFormat } from 'utils/utils';
import { DEFAULT_DATE_FORMAT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class RateForm extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      rateMethodCode: '',
      tenantId: getCurrentOrganizationId(),
    };
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { modalVisible } = nextProps;
    if (!modalVisible) {
      this.setState({
        rateMethodCode: '',
      });
    }
  }

  // 保存
  @Bind()
  saveBtn() {
    const { form, onHandleAdd } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        onHandleAdd(values);
      }
    });
  }

  render() {
    const { form, initData, title, anchor, modalVisible, onCancel, confirmLoading } = this.props;
    const { getFieldDecorator } = form;
    const { rateMethodCode, tenantId } = this.state;
    const {
      fromCurrencyCode,
      fromCurrencyName,
      toCurrencyCode,
      toCurrencyName,
      rateTypeCode,
      rateTypeName,
      rateDate,
      rate,
      enabledFlag = 1,
    } = initData;
    const validStartDate = (_, date, callback) => {
      const end = form.getFieldsValue().endDate;
      const start = moment(date).unix();
      if (!!end && start > moment(end).unix()) {
        callback(
          intl.get('hzero.common.validation.date.after', {
            startDate: intl.get('hpfm.rate.model.rate.startDate').d('起始时间'),
            endDate: intl.get('hpfm.rate.model.rate.endDate').d('结束时间'),
          })
        );
      } else {
        callback();
      }
    };
    const validDate = (_, date, callback) => {
      const start = form.getFieldsValue().startDate;
      const end = moment(date).unix();
      if (!!start && end < moment(start).unix()) {
        callback(intl.get('hpfm.rate.view.validation.data').d('结束时间不能早于起始时间'));
      } else {
        callback();
      }
    };
    return (
      <Modal
        destroyOnClose
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={modalVisible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        confirmLoading={confirmLoading}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.fromCurrencyCode').d('币种代码')}
          >
            {getFieldDecorator('fromCurrencyCode', {
              initialValue: fromCurrencyCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.fromCurrencyCode').d('币种代码'),
                  }),
                },
                {
                  validator: (rule, value, callback) => {
                    if (value && form.getFieldValue('toCurrencyCode') === value) {
                      callback(
                        new Error(
                          intl.get('hpfm.rate.view.validation.notSame').d('不能选择相同的币种代码')
                        )
                      );
                    } else {
                      callback();
                    }
                  },
                },
              ],
            })(
              <Lov
                disabled={!!fromCurrencyCode}
                textValue={initData.fromCurrencyCode || ''}
                code="HPFM.CURRENCY"
                queryParams={{ tenantId, enabledFlag: 1 }}
                onChange={(text, record) => {
                  form.setFieldsValue({ fromCurrencyName: record && record.currencyName });
                }}
              />
            )}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.fromCurrencyName').d('币种名称')}
          >
            {getFieldDecorator('fromCurrencyName', {
              initialValue: fromCurrencyName,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.fromCurrencyName').d('币种名称'),
                  }),
                },
              ],
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.toCurrencyCode').d('兑换币种代码')}
          >
            {getFieldDecorator('toCurrencyCode', {
              initialValue: toCurrencyCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.toCurrencyCode').d('兑换币种代码'),
                  }),
                },
                {
                  validator: (rule, value, callback) => {
                    if (value && form.getFieldValue('fromCurrencyCode') === value) {
                      callback(
                        new Error(
                          intl.get('hpfm.rate.view.validation.notSame').d('不能选择相同的币种代码')
                        )
                      );
                    } else {
                      callback();
                    }
                  },
                },
              ],
            })(
              <Lov
                disabled={!!toCurrencyCode}
                textValue={toCurrencyCode}
                code="HPFM.CURRENCY"
                queryParams={{ tenantId, enabledFlag: 1 }}
                onChange={(text, record) => {
                  form.setFieldsValue({ toCurrencyName: record.currencyName });
                }}
              />
            )}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.toCurrencyName').d('兑换币种名称')}
          >
            {getFieldDecorator('toCurrencyName', {
              initialValue: toCurrencyName,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.toCurrencyName').d('兑换币种名称'),
                  }),
                },
              ],
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.rateTypeName').d('汇率类型')}
          >
            {getFieldDecorator('rateTypeCode', {
              initialValue: rateTypeCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.rateTypeName').d('汇率类型'),
                  }),
                },
              ],
            })(
              <Lov
                allowClear={false}
                disabled={!!rateTypeCode}
                textValue={rateTypeName}
                code="HPFM.EXCHANGE_RATE_TYPE"
                queryParams={{ tenantId, enabledFlag: 1 }}
                onChange={(text, record) => {
                  this.setState({
                    rateMethodCode: record.rateMethodCode,
                  });
                }}
              />
            )}
          </FormItem>
          {form.getFieldValue('rateTypeCode') === 'Current Rate' ? (
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.rate.model.rate.rateValue').d('汇率')}
            >
              {getFieldDecorator('rateValue', {
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.rate.model.rate.rateValue').d('汇率'),
                    }),
                  },
                ],
              })(<Input disabled />)}
            </FormItem>
          ) : (
            ''
          )}
          {rateDate && (
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.rate.model.rate.rateDate').d('兑换日期')}
            >
              {getFieldDecorator('rateDate', {
                initialValue: rateDate ? moment(rateDate, DEFAULT_DATE_FORMAT) : '',
              })(
                <DatePicker
                  placeholder=""
                  style={{ width: '100%' }}
                  disabled
                  format={getDateFormat()}
                />
              )}
            </FormItem>
          )}
          {rateMethodCode === 'FR' ? (
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.rate.model.rate.startDate').d('起始时间')}
            >
              {getFieldDecorator('startDate', {
                initialValue: rateDate ? moment(rateDate, DEFAULT_DATE_FORMAT) : '',
                rules: [
                  {
                    type: 'object',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.rate.model.rate.startDate').d('起始时间'),
                    }),
                  },
                  {
                    validator: validStartDate,
                  },
                ],
              })(
                <DatePicker
                  placeholder=""
                  style={{ width: '100%' }}
                  disabledDate={current => {
                    return (
                      (current && moment(current).subtract(-1, 'days') < moment().endOf('day')) ||
                      moment(current).subtract(30, 'days') > moment().endOf('day')
                    );
                  }}
                  disabled={form.getFieldValue('rateTypeCode') === 'Current Rate' || !!rateDate}
                  format={getDateFormat()}
                />
              )}
            </FormItem>
          ) : (
            ''
          )}
          {rateMethodCode === 'FR' && (
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.rate.model.rate.endDate').d('结束时间')}
            >
              {getFieldDecorator('endDate', {
                initialValue: rateDate ? moment(rateDate, DEFAULT_DATE_FORMAT) : '',
                rules: [
                  {
                    type: 'object',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.rate.model.rate.endDate').d('结束时间'),
                    }),
                  },
                  {
                    validator: validDate,
                  },
                ],
              })(
                <DatePicker
                  placeholder=""
                  style={{ width: '100%' }}
                  disabled={form.getFieldValue('rateTypeCode') === 'Current Rate' || !!rateDate}
                  disabledDate={current => {
                    return (
                      (current && moment(current).subtract(-1, 'days') < moment().endOf('day')) ||
                      moment(current).subtract(30, 'days') > moment().endOf('day')
                    );
                  }}
                  format={getDateFormat()}
                />
              )}
            </FormItem>
          )}
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.currencyNumber').d('货币数量')}
          >
            {getFieldDecorator('currencyNumber', {
              initialValue: rate ? '1' : '',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.currencyNumber').d('货币数量'),
                  }),
                },
                {
                  pattern: /^[0-9]*$/,
                  message: intl.get('hpfm.rate.validation.digital').d('只能输入数字'),
                },
                {
                  validator: (rule, value, callback) => {
                    if (value > 0) {
                      callback();
                    } else {
                      callback(
                        new Error(
                          intl
                            .get('hpfm.rate.view.validation.correctCurrencyNum')
                            .d('请输入正确的货币数量')
                        )
                      );
                    }
                  },
                },
              ],
            })(<InputNumber precision={0} min={0} style={{ width: '100%' }} />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.exchangeNumber').d('兑换数量')}
          >
            {getFieldDecorator('exchangeNumber', {
              initialValue: rate ? round(multiply(1, rate), 8).toString() : '',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.exchangeNumber').d('兑换数量'),
                  }),
                },
                {
                  validator: (rule, value, callback) => {
                    if (value > 0) {
                      callback();
                    } else {
                      callback(
                        new Error(
                          intl
                            .get('hpfm.rate.view.validation.correctExchangeNum')
                            .d('请输入正确的兑换数量')
                        )
                      );
                    }
                  },
                },
              ],
            })(<InputNumber precision={8} step={0.00000001} min={0} style={{ width: '100%' }} />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hzero.common.status.enable').d('启用')}
          >
            {getFieldDecorator('enabledFlag', {
              initialValue: enabledFlag,
            })(<Switch />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
