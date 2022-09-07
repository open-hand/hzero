import React from 'react';
import { Form, Input, InputNumber, Modal, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import TLEditor from 'components/TLEditor';
import Lov from 'components/Lov';
import Switch from 'components/Switch';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class CurrencyForm extends React.Component {
  @Bind()
  handleOk() {
    const { form, onOk = e => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      onOk(fieldsValue, form);
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel = e => e } = this.props;
    onCancel();
  }

  render() {
    const {
      form,
      modalVisible,
      editRowData,
      loading,
      initLoading,
      isCreateFlag = false,
    } = this.props;
    const {
      currencyCode,
      currencyName,
      countryId,
      financialPrecision,
      defaultPrecision,
      currencySymbol,
      enabledFlag,
      countryName,
      _token,
    } = editRowData;
    return (
      <Modal
        confirmLoading={loading}
        title={
          isCreateFlag
            ? intl.get('hpfm.currency.view.message.title.create').d('新建币种')
            : intl.get('hpfm.currency.view.message.title.edit').d('编辑币种')
        }
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={modalVisible}
        onOk={this.handleOk}
        width={500}
        onCancel={this.handleCancel}
      >
        <Spin spinning={initLoading}>
          <Form.Item
            label={intl.get('hpfm.currency.model.currency.currencyCode').d('币种代码')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {form.getFieldDecorator('currencyCode', {
              initialValue: currencyCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.currency.model.currency.currencyCode').d('币种代码'),
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
            })(<Input trim typeCase="upper" inputChinese={false} disabled={!!currencyCode} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.currency.model.currency.currencyName').d('币种名称')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {form.getFieldDecorator('currencyName', {
              initialValue: currencyName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.currency.model.currency.currencyName').d('币种名称'),
                  }),
                },
                {
                  max: 40,
                  message: intl.get('hzero.common.validation.max', {
                    max: 40,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.currency.model.currency.currencyName').d('币种名称')}
                field="currencyName"
                token={_token}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.currency.model.currency.countryName').d('国家/地区')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {form.getFieldDecorator('countryId', {
              initialValue: countryId,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.currency.model.currency.countryName').d('国家/地区'),
                  }),
                },
              ],
            })(
              <Lov textValue={countryName} code="HPFM.COUNTRY" queryParams={{ enabledFlag: 1 }} />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.currency.model.currency.financialPrecision').d('财务精度')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {form.getFieldDecorator('financialPrecision', {
              initialValue: financialPrecision,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.currency.model.currency.financialPrecision').d('财务精度'),
                  }),
                },
              ],
            })(<InputNumber min={0} maxLength={8} style={{ width: '100%' }} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.currency.model.currency.defaultPrecision').d('精度')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {form.getFieldDecorator('defaultPrecision', {
              initialValue: defaultPrecision,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.currency.model.currency.defaultPrecision').d('精度'),
                  }),
                },
              ],
            })(<InputNumber min={0} maxLength={8} style={{ width: '100%' }} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.currency.model.currency.currencySymbol').d('货币符号')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {form.getFieldDecorator('currencySymbol', {
              initialValue: currencySymbol,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.currency.model.currency.currencySymbol').d('货币符号'),
                  }),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hzero.common.status.enable').d('启用')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {form.getFieldDecorator('enabledFlag', {
              initialValue: enabledFlag === undefined ? 1 : enabledFlag,
            })(<Switch />)}
          </Form.Item>
        </Spin>
      </Modal>
    );
  }
}
