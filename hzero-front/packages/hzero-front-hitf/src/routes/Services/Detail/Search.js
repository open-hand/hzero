import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import {
  FORM_FIELD_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import { isFunction } from 'lodash';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
    if (isFunction(props.onRef)) {
      props.onRef(this);
    }
  }

  // 查询条件展开/收起
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  @Bind()
  onClick() {
    const { onSearch = (e) => e } = this.props;
    onSearch();
  }

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      serviceTypes = [], // 发布类型
      interfaceStatus = [], // 状态
      form: { getFieldDecorator = (e) => e },
    } = this.props;
    const { expandForm } = this.state;

    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col span={5}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hitf.services.model.services.interfaceCode').d('接口编码')}
            >
              {getFieldDecorator('interfaceCode')(
                <Input inputChinese={false} className={FORM_FIELD_CLASSNAME} />
              )}
            </FormItem>
          </Col>
          <Col span={5}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hitf.services.model.services.interfaceName').d('接口名称')}
            >
              {getFieldDecorator('interfaceName')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </FormItem>
          </Col>
          <Col span={5}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hitf.services.model.services.publishType').d('发布类型')}
            >
              {getFieldDecorator('publishType')(
                <Select className={FORM_FIELD_CLASSNAME}>
                  {serviceTypes.map((n) => (
                    <Option key={n.value} value={n.value}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col span={9} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? 'block' : 'none' }}>
          <Col span={5}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.status').d('状态')}
            >
              {getFieldDecorator('status')(
                <Select>
                  {interfaceStatus.map((n) => (
                    <Option key={n.value} value={n.value}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
