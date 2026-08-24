import React from 'react';
import { Form, Input, Select } from 'hzero-ui';
import { connect } from 'dva';

import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';
import ModalForm from 'components/Modal/ModalForm';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@connect(({ bank }) => ({
  bank,
}))
@Form.create({ fieldNameProp: null })
export default class BankForm extends ModalForm {
  renderForm() {
    const { form, data, bank } = this.props;
    return (
      <React.Fragment>
        <Form.Item {...MODAL_FORM_ITEM_LAYOUT} label={intl.get('hpfm.bank.model.bank.bankCode').d('银行代码')}>
          {form.getFieldDecorator('bankCode', {
            initialValue: data.bankCode,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.bank.model.bank.bankCode').d('银行代码'),
                }),
              },
              {
                pattern: CODE_UPPER,
                message: intl
                  .get('hzero.common.validation.codeUpper')
                  .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
            ],
          })(<Input trim inputChinese={false} typeCase="upper" disabled={!!data.bankId} />)}
        </Form.Item>
        <Form.Item {...MODAL_FORM_ITEM_LAYOUT} label={intl.get('hpfm.bank.model.bank.bankType').d('银行类型')}>
          {form.getFieldDecorator('bankTypeCode', {
            initialValue: data.bankTypeCode,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.bank.model.bank.bankType').d('银行类型'),
                }),
              },
            ],
          })(
            <Select style={{ width: '100%' }}>
              {bank.bankTypeList.map(m => (
                <Select.Option key={m.value} value={m.value}>
                  {m.meaning}
                </Select.Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item {...MODAL_FORM_ITEM_LAYOUT} label={intl.get('hpfm.bank.model.bank.bankName').d('银行名称')}>
          {form.getFieldDecorator('bankName', {
            initialValue: data.bankName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.bank.model.bank.bankName').d('银行名称'),
                }),
              },
            ],
          })(
            <TLEditor
              label={intl.get('hpfm.bank.model.bank.bankName').d('银行名称')}
              field="bankName"
              token={data._token}
            />
          )}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.bank.model.bank.bankShortName').d('银行简称')}
        >
          {form.getFieldDecorator('bankShortName', {
            initialValue: data.bankShortName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.bank.model.bank.bankShortName').d('银行简称'),
                }),
              },
            ],
          })(
            <TLEditor
              label={intl.get('hpfm.bank.model.bank.bankShortName').d('银行简称')}
              field="bankShortName"
              token={data._token}
            />
          )}
        </Form.Item>
        <Form.Item {...MODAL_FORM_ITEM_LAYOUT} label={intl.get('hzero.common.status.enable').d('启用')}>
          {form.getFieldDecorator('enabledFlag', {
            initialValue: data.enabledFlag,
          })(<Switch />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}
