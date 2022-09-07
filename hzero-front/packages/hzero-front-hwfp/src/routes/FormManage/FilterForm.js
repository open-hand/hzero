import React, { PureComponent } from 'react';
import { Form, Button, Input, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

const { Option } = Select;
/**
 * 表单管理查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  /**
   * 查询
   */
  @Bind()
  handleFetch() {
    const { onSearch, form, onStore } = this.props;
    if (onSearch) {
      form.validateFields((err, values) => {
        if (!err) {
          // 如果验证成功,则执行search
          onSearch();
          onStore(values);
        }
      });
    }
  }

  /**
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { getFieldDecorator } = this.props.form;
    const { category } = this.props;
    return (
      <>
        <Form layout="inline">
          <Form.Item label={intl.get('hwfp.common.model.process.class').d('流程分类')}>
            {getFieldDecorator(
              'category',
              {}
            )(
              <Select allowClear style={{ width: '185px' }}>
                {category &&
                  category.map((item) => (
                    <Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item label={intl.get('hwfp.common.model.common.code').d('编码')}>
            {getFieldDecorator('code', {})(<Input typeCase="upper" trim inputChinese={false} />)}
          </Form.Item>
          <Form.Item label={intl.get('hwfp.formManage.model.formManage.invokeFlag').d('是否回调')}>
            {getFieldDecorator(
              'invokeFlag',
              {}
            )(
              <Select style={{ width: '175px' }} allowClear>
                <Option value={1}>{intl.get('hzero.common.status.yes').d('是')}</Option>
                <Option value={0}>{intl.get('hzero.common.status.no').d('否')}</Option>
              </Select>
            )}
          </Form.Item>
          <Form.Item>
            <Button data-code="search" type="primary" htmlType="submit" onClick={this.handleFetch}>
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
            <Button data-code="reset" style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
          </Form.Item>
        </Form>
      </>
    );
  }
}
