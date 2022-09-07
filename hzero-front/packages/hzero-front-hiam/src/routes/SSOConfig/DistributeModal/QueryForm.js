import React, { PureComponent } from 'react';
import { Form, Button, Row, Col } from 'hzero-ui';
import { isUndefined } from 'lodash';
import intl from 'utils/intl';
import Lov from 'components/Lov';
import {
  FORM_COL_3_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class QueryForm extends PureComponent {
  onClick() {
    const {
      handleFetchData = (e) => e,
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const data = getFieldsValue() || {};
    handleFetchData({
      ...data,
    });
  }

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e, getFieldValue = (e) => e },
      disabled,
      tenantId,
      isSiteFlag,
    } = this.props;
    return (
      <Form>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          {isSiteFlag && (
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.ssoConfig.model.ssoConfig.tenantName').d('租户名称')}
              >
                {getFieldDecorator('tenantId')(<Lov allowClear code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.ssoConfig.model.ssoConfig.companyName').d('公司名称')}
            >
              {getFieldDecorator('companyId')(
                <Lov
                  queryParams={{
                    tenantId: isSiteFlag ? getFieldValue('tenantId') : tenantId,
                  }}
                  code="HPFM.COMPANY"
                  allowClear
                  disabled={isSiteFlag && isUndefined(getFieldValue('tenantId'))}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get(`hzero.common.button.reset`).d('重置')}
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                onClick={this.onClick.bind(this)}
                disabled={disabled}
              >
                {intl.get(`hzero.common.button.search`).d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
