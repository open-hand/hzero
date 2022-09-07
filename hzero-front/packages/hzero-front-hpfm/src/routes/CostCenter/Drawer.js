import React from 'react';
import { Form, Modal, Spin, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import Switch from 'components/Switch';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { getCurrentOrganizationId } from 'utils/utils';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tenantId: getCurrentOrganizationId(),
    };
  }

  @Bind()
  handleOk() {
    const { form, onOk, initData } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({ ...initData, ...fieldsValue, enabledFlag: fieldsValue.enabledFlag ? 1 : 0 });
      }
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const { modalVisible, title, loading, form, editflag, initData = {} } = this.props;
    const { getFieldDecorator } = form;
    const { companyId, ouId, companyName, ouName, costCode, costName, enabledFlag } = initData;
    return (
      <Modal
        destroyOnClose
        title={title}
        // width={800}
        visible={modalVisible}
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCancel}
        onOk={this.handleOk}
      >
        <Spin spinning={false}>
          <Form>
            <Row>
              <Col>
                <FormItem
                  label={intl.get('hpfm.costCenter.model.costCenter.companyName').d('公司名称')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('companyId', {
                    initialValue: companyId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`hpfm.costCenter.model.project.companyName`).d('公司'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      textValue={companyName}
                      queryParams={{
                        tenantId: this.state.tenantId,
                      }}
                      code="HPFM.USER_AUTHORITY.COMPANY"
                      disabled={editflag}
                      allowClear
                    />
                  )}
                </FormItem>
              </Col>
              <Col>
                <FormItem
                  label={intl.get(`hpfm.costCenter.model.project.ouId`).d('业务实体')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('ouId', {
                    initialValue: ouId,
                  })(
                    <Lov
                      textValue={ouName}
                      code="HPFM.USER_AUTH.OU"
                      allowClear
                      disabled={!form.getFieldValue('companyId') || editflag}
                      queryParams={{
                        tenantId: this.state.tenantId,
                        companyId: form.getFieldValue('companyId'),
                        enabledFlag: 1,
                      }}
                    />
                  )}
                </FormItem>
              </Col>
              <Col>
                <Form.Item
                  label={intl.get(`hpfm.costCenter.model.costCenter.code`).d('成本中心编码')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator(`costCode`, {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`hpfm.costCenter.model.costCenter.code`).d('成本中心编码'),
                        }),
                      },
                      {
                        max: 24,
                        message: intl.get('hzero.common.validation.max', {
                          max: 24,
                        }),
                      },
                    ],
                    initialValue: costCode,
                  })(<Input trim inputChinese={false} typeCase="upper" disabled={editflag} />)}
                </Form.Item>
              </Col>
              <Col>
                <Form.Item
                  label={intl.get(`hpfm.costCenter.model.costCenter.name`).d('成本中心名称')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('costName', {
                    initialValue: costName,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`hpfm.costCenter.model.costCenter.name`).d('成本中心名称'),
                        }),
                      },
                      {
                        max: 120,
                        message: intl.get('hzero.common.validation.max', {
                          max: 120,
                        }),
                      },
                    ],
                  })(<Input trim />)}
                </Form.Item>
              </Col>

              <Col>
                <Form.Item
                  label={intl.get('hzero.common.status').d('状态')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator(`enabledFlag`, {
                    initialValue: enabledFlag,
                    valuePropName: 'checked',
                  })(<Switch />)}
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
