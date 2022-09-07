import React, { Component } from 'react';
import { Form, Row, Col } from 'hzero-ui';
import isNil from 'lodash/isNil';
import isNumber from 'lodash/isNumber';

import { FORM_COL_3_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

import styles from '../style/index.less';

const FormItem = Form.Item;
const formsLayouts = { labelCol: { span: 7 }, wrapperCol: { span: 13 } };
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
    const { name, serviceName, approveMethod, approveMethodValue } = processInfo;
    const newApproveMethodValue =
      !isNil(approveMethodValue) && isNumber(approveMethodValue)
        ? approveMethodValue * 100
        : approveMethodValue;
    return (
      <Form className={styles['right-content-detail-form']}>
        <Row gutter={48}>
          <Col {...FORM_COL_3_LAYOUT}>
            <UEDDisplayFormItem
              label={intl.get('hwfp.processDefine.model.processDefine.name').d('审批链名称')}
              value={name}
            />
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <UEDDisplayFormItem
              label={intl.get('hwfp.processDefine.model.processDefine.approveMethod').d('审批方式')}
              value={serviceName}
            />
          </Col>
          {approveMethod === 8 && (
            <Col {...FORM_COL_3_LAYOUT}>
              <UEDDisplayFormItem
                label={intl.get('hwfp.processDefine.model.processDefine.proportion').d('比例')}
                value={newApproveMethodValue}
              />
            </Col>
          )}
        </Row>
      </Form>
    );
  }
}
