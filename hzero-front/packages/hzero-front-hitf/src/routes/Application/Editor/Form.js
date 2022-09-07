import React, { PureComponent } from 'react';
import { Form, Input, Row, Col } from 'hzero-ui';
import { toSafeInteger, isUndefined } from 'lodash';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class EditorForm extends PureComponent {
  state = {};

  parserSort(value) {
    return toSafeInteger(value);
  }

  render() {
    const {
      form: { getFieldDecorator = e => e, getFieldValue = e => e, setFieldsValue = e => e },
      editable,
      dataSource = {},
      setTenantId = e => e,
      tenantRoleLevel,
    } = this.props;

    const {
      applicationCode = '',
      tenantId,
      tenantName,
      oauthClientId,
      clientName,
      applicationName,
    } = dataSource;

    return (
      <>
        <Form>
          <Row type="flex">
            {!tenantRoleLevel && (
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hitf.application.model.application.tenant').d('所属租户')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('tenantId', {
                    initialValue: tenantId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hitf.application.model.application.tenant').d('所属租户'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      disabled={editable}
                      textValue={tenantName}
                      code="HPFM.TENANT"
                      onOk={setTenantId}
                      allowClear={false}
                      onChange={() => setFieldsValue({ oauthClientId: undefined })}
                    />
                  )}
                </FormItem>
              </Col>
            )}
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.application.model.application.code').d('应用代码')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('applicationCode', {
                  initialValue: applicationCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hitf.application.model.application.code').d('应用代码'),
                      }),
                    },
                    {
                      pattern: CODE_UPPER,
                      message: intl
                        .get('hzero.common.validation.codeUpper')
                        .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                  ],
                })(<Input trim typeCase="upper" inputChinese={false} disabled={editable} />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.application.model.application.name').d('应用名称')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('applicationName', {
                  initialValue: applicationName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hitf.application.model.application.name').d('应用名称'),
                      }),
                    },
                  ],
                })(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.application.model.application.client').d('客户端')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('oauthClientId', {
                  initialValue: oauthClientId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hitf.application.model.application.client').d('客户端'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    textValue={clientName}
                    code="HITF.APPLICATION.CLIENT"
                    queryParams={{
                      tenantId: !tenantRoleLevel ? getFieldValue('tenantId') : tenantId,
                    }}
                    disabled={!tenantRoleLevel ? isUndefined(getFieldValue('tenantId')) : false}
                    allowClear={false}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </>
    );
  }
}
