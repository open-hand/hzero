/**
 * tenants - 租户维护Modal
 * @date: 2018-8-4
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, InputNumber } from 'hzero-ui';

import Switch from 'components/Switch';
import ModalForm from 'components/Modal/ModalForm';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { CODE } from 'utils/regExp';

/**
 * 组织信息模态框表单
 * @extends {ModalForm} - React.ModalForm
 * @reactProps {Function} handleAdd - 表单提交
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class TenantForm extends ModalForm {
  renderForm() {
    const { data = {}, form = {} } = this.props;
    const { getFieldDecorator = e => e } = form;
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 14 },
    };
    return (
      <>
        <Form.Item label={intl.get('entity.tenant.code').d('租户编码')} {...formLayout}>
          {getFieldDecorator('tenantNum', {
            initialValue: data.tenantNum,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('entity.tenant.code').d('租户编码'),
                }),
              },
              {
                pattern: CODE,
                message: intl
                  .get('hzero.common.validation.code')
                  .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', {
                  max: 30,
                }),
              },
            ],
          })(<Input trim inputChinese={false} disabled={data.tenantId !== undefined} />)}
        </Form.Item>
        <Form.Item label={intl.get('entity.tenant.name').d('租户名称')} {...formLayout}>
          {getFieldDecorator('tenantName', {
            initialValue: data.tenantName,
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('entity.tenant.name').d('租户名称'),
                }),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', {
                  max: 30,
                }),
              },
            ],
          })(
            <TLEditor
              label={intl.get('entity.tenant.name').d('租户名称')}
              field="tenantName"
              inputSize={{ zh: 64, en: 64 }}
              token={data._token}
            />
          )}
        </Form.Item>
        <Form.Item
          {...formLayout}
          label={intl.get('hiam.tenants.model.tenant.limitUserQty').d('限制用户数')}
        >
          {getFieldDecorator('limitUserQty', {
            initialValue: data.limitUserQty,
          })(<InputNumber precision={0} min={0} />)}
        </Form.Item>
        <Form.Item label={intl.get('hzero.common.status.enable').d('启用')} {...formLayout}>
          {getFieldDecorator('enabledFlag', {
            initialValue: data.enabledFlag,
          })(<Switch />)}
        </Form.Item>
      </>
    );
  }
}
