import React from 'react';
import { Form, Input, Modal, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import Lov from 'components/Lov';

import intl from 'utils/intl';

const formLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};
@Form.create({ fieldNameProp: null })
export default class CreatePoint extends React.Component {
  // constructor(props) {
  //   super(props);
  //   this.state = {
  //     record: {},
  //   };
  // }

  @Bind()
  handleOk() {
    const { form, onOk = e => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({
          ...fieldsValue,
          rangePointId: uuid(),
          isCreate: true,
        });
      }
    });
  }

  @Bind()
  selectPoint(_, record) {
    const { form } = this.props;
    // this.setState({ record })
    form.setFieldsValue({
      serviceName: record.serviceName,
      className: record.className,
      methodArgs: record.methodArgs,
      methodName: record.methodName,
      packageName: record.packageName,
    });
  }

  render() {
    const { form, visible, onCancel } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={intl.get('hpfm.customize.view.title.point.create').d('创建切入点')}
        width="620px"
        visible={visible}
        onCancel={onCancel}
        onOk={this.handleOk}
        footer={[
          <Lov isButton code="HPFM.CUSTOMIZE_POINT" onChange={this.selectPoint}>
            {intl.get('hzero.common.button.select').d('选择')}
          </Lov>,
          <Button key="back" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button key="submit" type="primary" onClick={this.handleOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <Form>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.customize.model.customize.point.serviceName').d('服务名')}
          >
            {getFieldDecorator('serviceName', {
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.customize.model.customize.point.serviceName').d('服务名'),
                  }),
                },
                {
                  max: 60,
                  message: intl.get('hzero.common.validation.max', {
                    max: 60,
                  }),
                },
              ],
            })(<Input trim inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.customize.model.customize.point.packageName').d('包名')}
          >
            {getFieldDecorator('packageName', {
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.customize.model.customize.point.packageName').d('包名'),
                  }),
                },
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
            })(<Input trim inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.customize.model.customize.point.className').d('类名')}
          >
            {getFieldDecorator('className', {
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.customize.model.customize.point.className').d('类名'),
                  }),
                },
                {
                  max: 180,
                  message: intl.get('hzero.common.validation.max', {
                    max: 180,
                  }),
                },
              ],
            })(<Input trim inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.customize.model.customize.point.methodName').d('方法名')}
          >
            {getFieldDecorator('methodName', {
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.customize.model.customize.point.methodName').d('方法名'),
                  }),
                },
                {
                  max: 180,
                  message: intl.get('hzero.common.validation.max', {
                    max: 180,
                  }),
                },
              ],
            })(<Input trim inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.customize.model.customize.point.methodArgs').d('方法参数列表')}
          >
            {getFieldDecorator('methodArgs')(<Input trim inputChinese={false} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
