import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { getCurrentOrganizationId } from 'utils/utils';
import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';
import { SEARCH_FORM_ITEM_LAYOUT, SEARCH_FORM_ROW_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

const organizationId = getCurrentOrganizationId();

/**
 * 流程定义查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hwfp/process-define/list' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

  // 查询条件展开/收起
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
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
          // 如果验证成功,则执行search
          onSearch();
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
    const {
      isSiteFlag,
      form: { getFieldDecorator },
    } = this.props;
    return (
      <>
        <Form layout="inline" className="more-fields-search-form">
          <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
            <Col span={18}>
              {isSiteFlag ? (
                <>
                  <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
                    <Col span={8}>
                      <Form.Item
                        {...SEARCH_FORM_ITEM_LAYOUT}
                        label={intl.get('hwfp.common.model.process.class').d('流程分类')}
                      >
                        {getFieldDecorator('category')(<Lov code="HWFP.PROCESS_CATEGORY" />)}
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item
                        {...SEARCH_FORM_ITEM_LAYOUT}
                        label={intl.get('hwfp.common.model.process.code').d('流程编码')}
                      >
                        {getFieldDecorator('key')(
                          <Input typeCase="upper" trim inputChinese={false} />
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item
                        {...SEARCH_FORM_ITEM_LAYOUT}
                        label={intl.get('hwfp.common.model.process.name').d('流程名称')}
                      >
                        {getFieldDecorator('name')(<Input dbc2sbc={false} />)}
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              ) : (
                <>
                  <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
                    <Col span={8}>
                      <Form.Item
                        {...SEARCH_FORM_ITEM_LAYOUT}
                        label={intl.get('hwfp.common.model.process.class').d('流程分类')}
                      >
                        {getFieldDecorator('category')(
                          <Lov
                            code="HWFP.PROCESS_CATEGORY"
                            queryParams={{ tenantId: organizationId }}
                          />
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item
                        {...SEARCH_FORM_ITEM_LAYOUT}
                        label={intl.get('hwfp.common.model.process.code').d('流程编码')}
                      >
                        {getFieldDecorator('key')(
                          <Input typeCase="upper" trim inputChinese={false} />
                        )}
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item
                        {...SEARCH_FORM_ITEM_LAYOUT}
                        label={intl.get('hwfp.common.model.process.name').d('流程名称')}
                      >
                        {getFieldDecorator('name')(<Input dbc2sbc={false} />)}
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              )}
            </Col>
            <Col span={6} className="search-btn-more">
              <Form.Item>
                <Button data-code="reset" onClick={this.handleFormReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button
                  data-code="search"
                  type="primary"
                  htmlType="submit"
                  onClick={this.handleSearch}
                >
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </>
    );
  }
}
