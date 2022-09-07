/**
 * List  - 应用管理 - 查询
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Col, Form, Row } from 'hzero-ui';
import classnames from 'classnames';
import intl from 'utils/intl';
import {
  EDIT_FORM_CLASSNAME,
  ROW_READ_ONLY_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  FORM_COL_3_LAYOUT,
  DETAIL_CARD_TABLE_CLASSNAME,
} from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      // expandForm: false,
    };
  }

  render() {
    const { dataSource = {} } = this.props;
    const { interfaceCode, interfaceName } = dataSource;
    return (
      <Form
        className={classnames(
          // DETAIL_EDIT_FORM_CLASSNAME,
          EDIT_FORM_CLASSNAME,
          DETAIL_CARD_TABLE_CLASSNAME
        )}
      >
        <Row className={ROW_READ_ONLY_CLASSNAME}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hitf.application.model.application.interfaceCode').d('接口编码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {interfaceCode}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hitf.application.model.application.interfaceName').d('接口名称')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {interfaceName}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
