import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import {
  FORM_FIELD_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(props.form);
  }

  @Bind()
  onClick() {
    const { onSearch = (e) => e } = this.props;
    onSearch();
  }

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e },
    } = this.props;
    const formLayout = {
      labelCol: {
        span: 7,
      },
      wrapperCol: {
        span: 17,
      },
    };
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col span={16}>
            <FormItem
              {...formLayout}
              label={intl.get('hitf.clientRole.model.clientRole.codeOrName').d('查询条件')}
            >
              {getFieldDecorator('interfaceName')(
                <Input
                  className={FORM_FIELD_CLASSNAME}
                  placeholder={intl
                    .get('hitf.clientRole.model.clientRole.searchName')
                    .d('接口代码|接口名称|服务代码|服务名称')}
                />
              )}
            </FormItem>
          </Col>
          <Col span={8} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
