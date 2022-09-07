import React, { PureComponent } from 'react';
import { Col, Form, Row } from 'hzero-ui';
import { toSafeInteger } from 'lodash';

import intl from 'utils/intl';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class EditorForm extends PureComponent {
  state = {};

  parserSort(value) {
    return toSafeInteger(value);
  }

  render() {
    const { dataSource = {} } = this.props;

    const { code, name, description } = dataSource;
    return (
      <>
        <Form>
          <Row type="flex">
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.clientRole.model.clientRole.code').d('角色编码')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {code}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.clientRole.model.clientRole.name').d('角色名称')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {name}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.clientRole.model.clientRole.remark').d('说明')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {description}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </>
    );
  }
}
