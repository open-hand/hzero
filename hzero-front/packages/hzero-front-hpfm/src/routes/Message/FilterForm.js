/**
 * Message API返回消息管理
 * @date: 2019-1-9
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';

import {
  DEBOUNCE_TIME,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import { CODE_LOWER } from 'utils/regExp';

import styles from './styles.less';

const FormItem = Form.Item;
const { Option } = Select;

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
  }

  componentWillUnmount() {
    this.toggleForm.cancel();
  }

  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  /**
   * 查询表单
   */
  @Bind()
  handleSearch() {
    const { form, search } = this.props;
    search(form);
  }

  /**
   * 重置表单
   */
  @Bind()
  handleReset() {
    const { form, reset } = this.props;
    reset(form);
  }

  render() {
    const { languageList, messageType, form } = this.props;
    const { getFieldDecorator } = form;
    const { expandForm } = this.state;
    return (
      <Form className={`${styles['message-search-form']} more-fields-search-form`}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.message.model.message.code').d('消息编码')}
            >
              {getFieldDecorator('code', {
                rules: [
                  {
                    max: 180,
                    message: intl.get('hzero.common.validation.max', {
                      max: 180,
                    }),
                  },
                  {
                    pattern: CODE_LOWER,
                    message: intl
                      .get('hzero.common.validation.codeLower')
                      .d('全小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input trim typeCase="lower" inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.message.model.message.type').d('消息类型')}
            >
              {getFieldDecorator('type')(
                <Select allowClear>
                  {messageType.map(item => (
                    <Option value={item.meaning} key={item.meaning}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.message.model.message.lang').d('语言')}
            >
              {getFieldDecorator('lang')(
                <Select allowClear>
                  {languageList.map(item => (
                    <Option value={item.code} key={item.code}>
                      {item.description}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.message.model.message.description').d('消息描述')}
            >
              {getFieldDecorator('description', {
                rules: [
                  {
                    max: 1000,
                    message: intl.get('hzero.common.validation.max', {
                      max: 1000,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
