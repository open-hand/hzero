import React, { PureComponent } from 'react';
import { Form, Input, Row, Col, Select } from 'hzero-ui';
import { toSafeInteger, isNumber } from 'lodash';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class EditorForm extends PureComponent {
  state = {};

  parserSort(value) {
    return toSafeInteger(value);
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e },
      dataSource = {},
      code,
    } = this.props;

    const { id, statisticsLevel = '', tenantId, name, remark } = dataSource;

    return (
      <>
        <Form>
          <Row type="flex">
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.application.model.application.client').d('客户端')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('name', {
                  initialValue: name,
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
                    textValue={name}
                    code="HITF.APPLICATION.CLIENT"
                    queryParams={{
                      tenantId,
                    }}
                    disabled={isNumber(id)}
                    allowClear={false}
                  />
                )}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.application.model.application.statisticsLevel').d('统计维度')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('statisticsLevel', {
                  initialValue: statisticsLevel,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hitf.application.model.application.statisticsLevel')
                          .d('统计维度'),
                      }),
                    },
                  ],
                })(
                  <Select allowClear>
                    {(code['HITF.STATISTICS_LEVEL'] || []).map((n) => (
                      <Option key={n.value} value={n.value}>
                        {n.meaning}
                      </Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.application.model.application.remark').d('说明')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('remark', {
                  initialValue: remark,
                  rules: [
                    {
                      max: 240,
                      message: intl.get('hzero.common.validation.max', {
                        max: 240,
                      }),
                    },
                  ],
                })(<Input />)}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </>
    );
  }
}
