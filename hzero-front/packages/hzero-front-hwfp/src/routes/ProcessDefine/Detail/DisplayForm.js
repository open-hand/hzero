import React, { Component } from 'react';
import { Form, Row, Col } from 'hzero-ui';

import { FORM_COL_3_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

import styles from '../style/index.less';

const FormItem = Form.Item;
const formsLayouts = { labelCol: { span: 4 }, wrapperCol: { span: 13 } };
const UEDDisplayFormItem = props => {
  const { label, value } = props;
  return (
    <FormItem label={label} {...formsLayouts}>
      {value}
    </FormItem>
  );
};

export default class DisplayForm extends Component {
  render() {
    const { processInfo = {} } = this.props;
    const { processKey, processName } = processInfo;

    return (
      <Form>
        <Row gutter={48}>
          <Col {...FORM_COL_3_LAYOUT}>
            <UEDDisplayFormItem
              className={styles['process-code']}
              label={intl.get('hwfp.common.model.process.code').d('流程编码')}
              value={processKey}
            />
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <UEDDisplayFormItem
              className={styles['process-name']}
              label={intl.get('hwfp.common.model.process.name').d('流程名称')}
              value={processName}
            />
          </Col>
        </Row>
      </Form>
    );
  }
}
