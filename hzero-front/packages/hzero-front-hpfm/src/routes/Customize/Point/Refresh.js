import React from 'react';
import { Col, Form, Input, Modal, Row, Icon, Tooltip } from 'hzero-ui';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT, EDIT_FORM_CLASSNAME } from 'utils/constants';

function Refresh(props) {
  const {
    form,
    initData = {},
    title = '',
    visible = false,
    loading = false,
    onCancel = e => e,
  } = props;
  const { getFieldDecorator } = form;
  const { serviceName, packageNames } = initData;
  const handleOk = () => {
    const { onOk } = props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  };

  return (
    <Modal
      destroyOnClose
      wrapClassName="ant-modal-sidebar-right"
      transitionName="move-right"
      title={title}
      visible={visible}
      confirmLoading={loading}
      onCancel={onCancel}
      onOk={handleOk}
    >
      <Form className={EDIT_FORM_CLASSNAME}>
        <Row>
          <Col>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.customize.model.customize.point.serviceName').d('服务名')}
            >
              {getFieldDecorator('serviceName', {
                initialValue: serviceName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.customize.model.customize.point.serviceName')
                        .d('服务名'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(<Lov code="HADM.ROUTE_INFORMATION" />)}
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={
                <React.Fragment>
                  {intl.get('hpfm.customize.model.customize.point.packageName').d('包名')}
                  <Tooltip
                    title={intl
                      .get('hpfm.customize.model.customize.point.msg')
                      .d('输入扫描的包名，多个包用逗号隔开')}
                  >
                    <Icon type="question-circle-o" style={{ verticalAlign: 'baseline' }} />
                  </Tooltip>
                </React.Fragment>
              }
            >
              {getFieldDecorator('packageNames', {
                initialValue: packageNames,
                rules: [
                  {
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
              })(<Input.TextArea autosize={{ minRows: 6, maxRows: 20 }} />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
}

export default Form.create({ fieldNameProp: null })(Refresh);
