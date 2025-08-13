import React, { Component } from 'react';
import { Button, Modal, Form, Row, Input, Radio } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import styles from '../style/index.less';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class FieldModal extends Component {
  @Bind()
  addField() {
    const { form, handleAddField } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
        };
        handleAddField(params);
      }
    });
  }

  render() {
    const { form, visible, hideModal, addFieldLoading } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        title={intl.get('hpfm.flexModelDetail.view.message.title.addField').d('新建字段')}
        visible={visible}
        destroyOnClose
        onCancel={hideModal}
        width={400}
        footer={[
          <Button key="back" disable={addFieldLoading} onClick={hideModal}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button key="submit" type="primary" loading={addFieldLoading} onClick={this.addField}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <Form layout="inline">
          <Row className={styles['virtual-field-radio-row']}>
            <FormItem
              label={intl
                .get('hpfm.flexModelDetail.model.flexModelDetail.virtualField')
                .d('虚拟字段')}
            >
              {getFieldDecorator('fieldCategory', {
                initialValue: 'VIRTUAL',
              })(
                <>
                  <Radio disabled defaultChecked>
                    {intl.get('hzero.common.status.yes').d('是')}
                  </Radio>
                  <Radio disabled>{intl.get('hzero.common.status.no').d('否')}</Radio>
                </>
              )}
            </FormItem>
          </Row>
          <Row>
            <FormItem
              label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldCode').d('字段编码')}
            >
              {getFieldDecorator('fieldCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.flexModelDetail.model.flexModelDetail.fieldCode')
                        .d('字段编码'),
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
            </FormItem>
          </Row>
          <Row>
            <FormItem
              label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldName').d('字段名称')}
            >
              {getFieldDecorator('fieldName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.flexModelDetail.model.flexModelDetail.fieldName')
                        .d('字段名称'),
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
            </FormItem>
          </Row>
        </Form>
      </Modal>
    );
  }
}
