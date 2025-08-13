import React from 'react';
import { Form, Input, Select } from 'hzero-ui';

import ModalForm from 'components/Modal/ModalForm';
import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

@Form.create({ fieldNameProp: null })
export default class CreateForm extends ModalForm {
  renderForm() {
    const { form, editValue = {}, rateMethodList = [] } = this.props;
    const { typeCode, typeName, rateMethodCode, enabledFlag, rateTypeId, _token } = editValue;

    const formLayOut = {
      labelCol: { span: 5 },
      wrapperCol: { span: 15 },
    };

    return (
      <React.Fragment>
        <Form.Item
          {...formLayOut}
          label={intl.get('hpfm.rateType.model.rateType.typeCode').d('类型编码')}
        >
          {form.getFieldDecorator('typeCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.rateType.model.rateType.typeCode').d('类型编码'),
                }),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', { max: 30 }),
              },
              {
                pattern: CODE_UPPER,
                message: intl
                  .get('hzero.common.validation.codeUpper')
                  .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
            ],
            initialValue: typeCode,
          })(<Input trim typeCase="upper" inputChinese={false} disabled={!!rateTypeId} />)}
        </Form.Item>
        <Form.Item
          {...formLayOut}
          label={intl.get('hpfm.rateType.model.rateType.typeName').d('类型名称')}
        >
          {form.getFieldDecorator('typeName', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.rateType.model.rateType.typeName').d('类型名称'),
                }),
              },
            ],
            initialValue: typeName,
          })(
            <TLEditor
              label={intl.get('hpfm.rateType.model.rateType.typeName').d('类型名称')}
              field="typeName"
              token={_token}
            />
          )}
        </Form.Item>
        <Form.Item
          {...formLayOut}
          label={intl.get('hpfm.rateType.model.rateType.rateMethodCode').d('方式')}
        >
          {/* TODO: 应该从值集取值 */}
          {form.getFieldDecorator('rateMethodCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.rateType.model.rateType.rateMethodCode').d('方式'),
                }),
              },
            ],
            initialValue: rateMethodCode,
          })(
            <Select style={{ width: '100%' }} disabled={!!rateTypeId} allowClear>
              {rateMethodList.map(m => {
                return (
                  <Select.Option key={m.value} value={m.value}>
                    {m.meaning}
                  </Select.Option>
                );
              })}
            </Select>
          )}
        </Form.Item>
        <Form.Item {...formLayOut} label={intl.get('hzero.common.status.enable').d('启用')}>
          {form.getFieldDecorator('enabledFlag', {
            valuePropName: 'checked',
            initialValue: enabledFlag === 0 ? 0 : 1,
          })(<Switch />)}
        </Form.Item>
      </React.Fragment>
    );
  }
}
