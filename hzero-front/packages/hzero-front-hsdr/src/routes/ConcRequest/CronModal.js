import React from 'react';
import { Col, Form, Modal, Row, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Cron from '@/components/Cron';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    const { cron } = form.getFieldsValue();
    onOk(cron);
  }

  render() {
    const {
      visible,
      onCancel,
      form: { getFieldDecorator },
      initialValue,
    } = this.props;
    return (
      <Modal
        destroyOnClose
        title="Cron"
        visible={visible}
        width={700}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Spin spinning={false}>
          <Form>
            <Row>
              <Col>
                <FormItem>
                  {getFieldDecorator('cron', {
                    initialValue: initialValue || '* * * * * ?',
                  })(<Cron />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
