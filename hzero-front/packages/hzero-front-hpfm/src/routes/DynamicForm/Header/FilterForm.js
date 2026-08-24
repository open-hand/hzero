/**
 * 动态表单配置行
 * @author liang.xiong@hand-china.com
 * @date 2019-07-15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Button, Input, Select, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import CacheComponent from 'components/CacheComponent';
import {
  SEARCH_FORM_CLASSNAME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const { Option } = Select;

@Form.create({ fieldNameProp: null })
@CacheComponent({ cacheKey: 'xcor.dynamicForm.header' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          onSearch();
        }
      });
    }
  }

  /**
   * 表单重置
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form: { getFieldDecorator },
      configGroupList,
    } = this.props;
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.dynamicForm.header.formCode').d('配置编码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('formCode', {})(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.dynamicForm.header.formName').d('配置名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('formName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hpfm.dynamicForm.header.formGroupCode').d('配置归类')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('formGroupCode')(
                <Select>
                  {configGroupList.map((config) => (
                    <Option key={config.value} value={config.value}>
                      {config.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
