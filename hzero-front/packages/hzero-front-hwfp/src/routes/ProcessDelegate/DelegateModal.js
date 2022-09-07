import React, { Component } from 'react';
import { Modal, Form } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';

const formLayout = {
  labelCol: {
    span: 4,
  },
  wrapperCol: {
    span: 20,
  },
};

@Form.create({ fieldNameProp: null })
export default class DelegateModal extends Component {
  @Bind()
  checkTargetEmployee(_, value, callback) {
    const { selectedRows } = this.props;
    const sameApprover = selectedRows.find((item) => item.assignee === value);
    if (sameApprover) {
      callback(
        intl.get('hwfp.processDelegate.view.message.sameApprover').d('不能转交给当前处理人')
      );
    } else {
      callback();
    }
  }

  @Bind()
  handleOk() {
    const { form, onSubmit } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const { delegate } = values;
        onSubmit(delegate);
      }
    });
  }

  render() {
    const {
      visible,
      handleClose,
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Modal
        destroyOnClose
        title={intl.get('hwfp.task.view.option.delegate', { name: '转交' }).d('转交')}
        width={500}
        visible={visible}
        onOk={this.handleOk}
        onCancel={handleClose}
      >
        <Form>
          <Form.Item
            {...formLayout}
            label={intl.get('hwfp.automaticProcess.model.automaticProcess.delegater').d('转交人')}
          >
            {getFieldDecorator('delegate', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.automaticProcess.model.automaticProcess.delegater')
                        .d('转交人'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.automaticProcess.model.automaticProcess.delegater')
                        .d('转交人')}不能为空`
                    ),
                },
                {
                  validator: this.checkTargetEmployee,
                },
              ],
            })(
              <Lov
                code="HWFP.EMPLOYEE"
                queryParams={{ tenantId: getCurrentOrganizationId(), enabledFlag: 1 }}
                lovOptions={{
                  displayField: 'name',
                }}
              />
            )}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
