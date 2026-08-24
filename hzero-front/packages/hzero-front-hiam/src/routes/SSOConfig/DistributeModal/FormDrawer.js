import React from 'react';
import { Form, Modal, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import Lov from 'components/Lov';

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
    const { form, onOk = (e) => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel = (e) => e } = this.props;
    onCancel();
  }

  render() {
    const { modalVisible, title, loading, form, isSiteFlag } = this.props;
    const { getFieldDecorator } = form;

    return (
      <Modal
        destroyOnClose
        title={title}
        width={500}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={this.handleCancel}
        onOk={this.handleOk}
      >
        <Form>
          <Row>
            <Col>
              {isSiteFlag && (
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.tenantName').d('租户名称')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('tenantId', {
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ssoConfig.model.ssoConfig.tenantName').d('租户名称'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HPFM.TENANT"
                      onChange={(val) => {
                        this.setState({ tenantId: val });
                        form.setFieldsValue({
                          companyId: undefined,
                        });
                      }}
                      allowClear
                    />
                  )}
                </FormItem>
              )}
              <FormItem
                label={intl.get('hiam.ssoConfig.model.ssoConfig.companyName').d('公司名称')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator(
                  'companyId',
                  {}
                )(
                  <Lov
                    queryParams={{
                      tenantId: this.state.tenantId,
                    }}
                    code="HPFM.COMPANY"
                    disabled={!isSiteFlag ? false : isNil(form.getFieldValue('tenantId'))}
                    allowClear
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
