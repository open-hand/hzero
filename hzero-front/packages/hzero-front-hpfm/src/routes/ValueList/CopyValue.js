/**
 * PreviewModal - lov预览
 * @date: 2018-7-9
 * @author: lokya <kan.li01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Form, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';

const fromLayOut = {
  labelCol: { span: 6 },
  wrapperCol: { span: 12 },
};

@Form.create({ fieldNameProp: null })
export default class PreviewModal extends PureComponent {
  @Bind()
  handleOk() {
    const { form, onOk = e => e } = this.props;
    form.validateFields((error, values) => {
      if (!error) {
        onOk(values);
      }
    });
  }

  render() {
    const { form, onCancel, visible, loading } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        title={intl.get('hpfm.valueList.view.title.copyValue').d('值集复制')}
        visible={visible}
        width={500}
        confirmLoading={loading}
        onOk={this.handleOk}
        onCancel={onCancel}
      >
        <React.Fragment>
          <Form>
            <Form.Item
              label={intl.get('hpfm.valueList.model.header.tenantName').d('所属租户')}
              {...fromLayOut}
            >
              {getFieldDecorator('tenantId', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.valueList.model.header.tenantName').d('所属租户'),
                    }),
                  },
                ],
              })(<Lov allowClear={false} code="HPFM.TENANT" />)}
            </Form.Item>
          </Form>
        </React.Fragment>
      </Modal>
    );
  }
}
