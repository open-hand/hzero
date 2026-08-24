/**
 * Event - 事件创建窗口
 * @date: 2018-6-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import SideBar from 'components/Modal/SideBar';
import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { DETAIL_DEFAULT_CLASSNAME, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class EventCreateForm extends React.Component {
  resetForm() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  onOk() {
    const { form, handleAdd } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      handleAdd(fieldsValue);
    });
  }

  render() {
    const {
      modalVisible,
      hideModal,
      confirmLoading = false,
      title,
      isSiteFlag,
      ...otherProps
    } = this.props;
    const { form } = this.props;
    return (
      <SideBar
        title={title}
        visible={modalVisible}
        onCancel={hideModal}
        onOk={this.onOk}
        confirmLoading={confirmLoading}
        className={DETAIL_DEFAULT_CLASSNAME}
        {...otherProps}
      >
        <Row>
          {isSiteFlag && (
            <Col>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hzero.common.model.common.tenantId').d('租户')}
              >
                {form.getFieldDecorator('tenantId', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hzero.common.model.common.tenantId').d('租户'),
                      }),
                    },
                  ],
                })(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.event.model.event.code').d('事件编码')}
            >
              {form.getFieldDecorator('eventCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.event.model.event.code').d('事件编码'),
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
              })(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.event.model.event.description').d('事件描述')}
            >
              {form.getFieldDecorator('eventDescription', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.event.model.event.description').d('事件描述'),
                    }),
                  },
                  {
                    max: 80,
                    message: intl.get('hzero.common.validation.max', {
                      max: 80,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('hcuz.custButton.view.title.description').d('事件描述')}
                  field="eventDescription"
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </SideBar>
    );
  }
}
