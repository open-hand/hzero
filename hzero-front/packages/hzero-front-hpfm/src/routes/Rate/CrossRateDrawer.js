import React from 'react';
import { DatePicker, Form, Modal } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import { getCurrentOrganizationId, getDateFormat } from 'utils/utils';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class CrossRateDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tenantId: getCurrentOrganizationId(),
    };
  }

  // 保存
  @Bind()
  saveBtn() {
    const { form, onSaveCrossRate } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        onSaveCrossRate(values);
      }
    });
  }

  render() {
    const { form, anchor, crossRate, confirmLoading, onCancelDrawer } = this.props;
    const { getFieldDecorator } = form;
    const { tenantId } = this.state;
    return (
      <Modal
        destroyOnClose
        title={intl.get('hpfm.rate.view.message.crossRate').d('交叉汇率')}
        width={480}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={crossRate}
        onOk={this.saveBtn}
        onCancel={() => onCancelDrawer(false)}
        confirmLoading={confirmLoading}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <div style={{ textAlign: 'center' }}>
          <span>
            {intl
              .get('hpfm.rate.view.crossExchangeRate')
              .d('提示：若生成的交叉汇率在当前兑换日期下已经存在则以原有的汇率为准！')}
          </span>
        </div>
        <br />
        <Form>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.fromCurrencyName').d('币种名称')}
          >
            {getFieldDecorator('currencyCode', {
              initialValue: '',
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.fromCurrencyName').d('币种名称'),
                  }),
                },
              ],
            })(
              <Lov
                lovOptions={{ displayField: 'currencyName', valueField: 'currencyCode' }}
                code="HPFM.CURRENCY"
                queryParams={{ tenantId, enabledFlag: 1 }}
              />
            )}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.rateTypeName').d('汇率类型')}
          >
            {getFieldDecorator('rateTypeCode', {
              initialValue: '',
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
                code="HPFM.EXCHANGE_RATE_TYPE"
                queryParams={{ tenantId, enabledFlag: 1 }}
              />
            )}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.rate.model.rate.rateDate').d('兑换日期')}
          >
            {getFieldDecorator('rateDate', {
              initialValue: '',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.rate.model.rate.rateDate').d('兑换日期'),
                  }),
                },
              ],
            })(<DatePicker placeholder="" style={{ width: '100%' }} format={getDateFormat()} />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
