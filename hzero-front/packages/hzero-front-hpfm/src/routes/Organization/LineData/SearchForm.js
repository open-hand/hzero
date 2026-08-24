import React from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Button, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import {
  SEARCH_FORM_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/hr/org/company/line-data/search-form' })
export default class SearchForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleSearchBtnClick(e) {
    e.preventDefault();
    const { onSearch } = this.props;
    onSearch();
  }

  render() {
    const { form, unitType = [] } = this.props;
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('entity.organization.code').d('组织编码')}
            >
              {form.getFieldDecorator('unitCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('entity.organization.name').d('组织名称')}
            >
              {form.getFieldDecorator('unitName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('entity.organization.type').d('组织类型')}
            >
              {form.getFieldDecorator('unitTypeCode')(
                <Select allowClear>
                  {unitType.map(item => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
