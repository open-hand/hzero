import React, { Component } from 'react';
import { Form, Row, Col, Input, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { filterNullValueObject } from 'utils/utils';

import styles from './style/index.less';

const FormItem = Form.Item;
const formItemLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 16 },
};

@Form.create({ fieldNameProp: null })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    props.onRef(this);
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
    const { handleSearch = () => {}, form = {} } = this.props;
    const { getFieldsValue = () => {} } = form;
    handleSearch(filterNullValueObject(getFieldsValue()));
  }

  render() {
    const {
      form: { getFieldDecorator = () => {} },
    } = this.props;
    return (
      <div className={`table-list-search ${styles['model-table-list-search']}`}>
        <Form layout="inline">
          <Row gutter={24}>
            <Col span={18}>
              <Row gutter={24} style={{ textAlign: 'right' }}>
                <Col span={8}>
                  <FormItem
                    {...formItemLayout}
                    label={intl.get('hpfm.flexModel.model.flexModel.modelCode').d('模型编码')}
                  >
                    {getFieldDecorator('modelCode')(<Input trim inputChinese={false} />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem
                    {...formItemLayout}
                    label={intl.get('hpfm.flexModel.model.flexModel.modelName').d('模型名称')}
                  >
                    {getFieldDecorator('modelName')(<Input />)}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem
                    {...formItemLayout}
                    label={intl.get('hpfm.flexModel.model.flexModel.modelTable').d('模型表')}
                  >
                    {getFieldDecorator('modelTable')(<Input />)}
                  </FormItem>
                </Col>
              </Row>
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
