import React, { Component } from 'react';
import { Form, Row, Col, Input, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import styles from './style/index.less';

const FormItem = Form.Item;
const formItemLayout = {
  labelCol: { span: 7 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    props.bindRef(this);
  }

  /**
   * reset - 重置按钮事件
   */
  @Bind()
  reset() {
    const {
      form: { resetFields = () => {} },
    } = this.props;
    resetFields();
  }

  /**
   * search - 查询按钮事件
   */
  @Bind()
  search() {
    const { handleSearch = () => {}, form = {}, handleSelectProcessLines = () => {} } = this.props;
    const { getFieldsValue = () => {} } = form;
    handleSearch(getFieldsValue());
    // 查询时重置已选中的行数据
    handleSelectProcessLines([], []);
  }

  render() {
    const {
      form: { getFieldDecorator = () => {} },
    } = this.props;
    return (
      <div className={`table-list-search ${styles['process-table-list-search']}`}>
        <Form layout="inline">
          <Row type="flex" gutter={24} align="bottom">
            <Col span={6}>
              <FormItem
                {...formItemLayout}
                label={intl
                  .get('hwfp.automaticProcess.model.automaticProcess.processKey')
                  .d('流程编码')}
              >
                {getFieldDecorator('processKey')(<Input />)}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem
                {...formItemLayout}
                label={intl
                  .get('hwfp.automaticProcess.model.automaticProcess.processName')
                  .d('流程名称')}
              >
                {getFieldDecorator('processName')(<Input dbc2sbc={false} />)}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem>
                <Button style={{ marginRight: 8 }} onClick={this.reset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button type="primary" htmlType="submit" onClick={this.search}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </FormItem>
            </Col>
          </Row>
        </Form>
      </div>
    );
  }
}
