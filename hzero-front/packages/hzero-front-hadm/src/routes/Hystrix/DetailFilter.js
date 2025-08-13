/*
 * DetailFilter - 熔断设置详情表单
 * @date: 2018/09/11 10:44:00
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Button, Select } from 'hzero-ui';
import { map } from 'lodash';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

const formItemLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class DetailFilter extends PureComponent {
  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onFilterChange, form } = this.props;
    if (onFilterChange) {
      form.validateFields((err, values) => {
        onFilterChange(values);
      });
    }
  }

  render() {
    const {
      form: { getFieldDecorator },
      propertyNameList,
    } = this.props;
    return (
      <Form layout="inline">
        <Form.Item
          label={intl.get(`hadm.hystrix.model.hystrix.propertyName`).d(`参数代码`)}
          {...formItemLayout}
        >
          {getFieldDecorator('propertyName')(
            <Select style={{ width: '200px' }} onChange={this.setRemark} allowClear>
              {map(propertyNameList, e => (
                <Option value={e.value} key={e.value}>
                  {e.value}
                </Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item>
          <Button data-code="search" type="primary" htmlType="submit" onClick={this.handleSearch}>
            {intl.get(`hzero.common.button.search`).d('查询')}
          </Button>
        </Form.Item>
      </Form>
    );
  }
}
