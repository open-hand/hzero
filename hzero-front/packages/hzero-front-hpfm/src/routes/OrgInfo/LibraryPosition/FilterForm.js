import React, { PureComponent, Fragment } from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import Lov from 'components/Lov';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

const modelPrompt = 'hpfm.libraryPosition.model.lp';

/**
 * 库位查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 表单查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  componentDidMount() {
    this.props.onRef(this);
  }

  /**
   * 表单查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err, values) => {
        if (!err) {
          // 如果验证成功,则执行onSearch
          onSearch(values);
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
      organizationId,
    } = this.props;
    return (
      <Fragment>
        <Form>
          <Row type="flex" gutter={24} align="bottom">
            <Col span={6}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.locationCode`).d('库位编码')}
              >
                {getFieldDecorator('locationCode')(<Input typeCase="upper" inputChinese={false} />)}
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.locationName`).d('库位名称')}
              >
                {getFieldDecorator('locationName')(<Input />)}
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.ouId`).d('业务实体')}
              >
                {getFieldDecorator('ouId')(
                  <Lov code="HPFM.OU" queryParams={{ organizationId, enabledFlag: 1 }} />
                )}
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item>
                <Button onClick={this.handleFormReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button
                  type="primary"
                  htmlType="submit"
                  style={{ marginLeft: 8 }}
                  onClick={this.handleSearch}
                >
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Fragment>
    );
  }
}
