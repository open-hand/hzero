import React from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Button, Col, Form, Input, Row } from 'hzero-ui';

import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import { VERSION_IS_OP } from 'utils/config';

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hrpt/data-set/list' })
export default class SearchForm extends React.Component {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields(err => {
        if (!err) {
          // 如果验证成功,则执行search
          onSearch();
        }
      });
    }
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { form } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hrpt.reportDataSet.model.reportDataSet.datasetCode').d('数据集代码')}
            >
              {form.getFieldDecorator('datasetCode')(
                <Input trim inputChinese={false} typeCase="upper" />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hrpt.reportDataSet.model.reportDataSet.datasetName').d('数据集名称')}
            >
              {form.getFieldDecorator('datasetName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hrpt.reportDataSet.model.reportDataSet.datasourceCode').d('数据源')}
            >
              {form.getFieldDecorator('datasourceCode')(
                <Lov
                  code={VERSION_IS_OP ? 'HPFM.DATASOURCE' : 'HPFM.SITE.DATASOURCE'}
                  queryParams={{ dsPurposeCode: 'DR' }}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
