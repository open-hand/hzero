import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { Form, Input, Button, Select, Row, Col } from 'hzero-ui';
import {
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';

const FormItem = Form.Item;

const modelPrompt = 'hiam.menuConfig.model.menuConfig';
const tenantRoleLevel = isTenantRoleLevel();

@Form.create({ fieldNameProp: null })
export default class QueryForm extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      expandForm: true,
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

  @Bind()
  onClick() {
    const {
      handleQueryList = (e) => e,
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const data = getFieldsValue() || {};
    const { labels = [] } = data;
    const param = isEmpty(labels) ? '' : labels.join(',');
    handleQueryList(
      {
        ...data,
        labels: param,
      },
      true
    );
  }

  @Bind()
  handleLevelSelect(value) {
    const {
      handleQueryList = (e) => e,
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const data = getFieldsValue() || {};
    handleQueryList({
      ...data,
      level: value,
    });
  }

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e },
      searchLabels = [],
      levelCode,
      enabledFlag,
    } = this.props;
    const { expandForm } = this.state;
    const levelCodeList = (levelCode && levelCode.filter((o) => o.value !== 'org')) || [];
    const [levelObj = {}] = levelCodeList;
    const { value: levelValue = '' } = levelObj;
    return (
      <Form className="more-fields-search-form">
        <>
          <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.name`).d('目录/菜单')}
              >
                {getFieldDecorator('name')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.parentName`).d('上级目录')}
              >
                {getFieldDecorator('parentName')(<Input />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              {!tenantRoleLevel ? (
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get(`${modelPrompt}.level`).d('层级')}
                >
                  {getFieldDecorator('level', {
                    initialValue: levelValue,
                  })(
                    <Select allowClear onSelect={this.handleLevelSelect}>
                      {levelCodeList.map((n) => (
                        <Select.Option key={n.value} value={n.value}>
                          {n.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              ) : (
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get(`${modelPrompt}.quickIndex`).d('快速索引')}
                >
                  {getFieldDecorator('quickIndex')(<Input inputChinese={false} />)}
                </FormItem>
              )}
            </Col>
            <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
              <Form.Item>
                <Button
                  style={{ display: expandForm ? 'none' : 'inline-block' }}
                  onClick={this.toggleForm}
                >
                  {intl.get(`hzero.common.button.viewMore`).d('更多查询')}
                </Button>
                <Button
                  style={{ display: expandForm ? 'inline-block' : 'none' }}
                  onClick={this.toggleForm}
                >
                  {intl.get('hzero.common.button.collected').d('收起查询')}
                </Button>
                <Button data-code="reset" onClick={this.onReset.bind(this)}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button type="primary" htmlType="submit" onClick={this.onClick}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
          <Row style={{ display: expandForm ? '' : 'none' }} {...SEARCH_FORM_ROW_LAYOUT}>
            {!tenantRoleLevel && (
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get(`${modelPrompt}.quickIndex`).d('快速索引')}
                >
                  {getFieldDecorator('quickIndex')(<Input inputChinese={false} />)}
                </FormItem>
              </Col>
            )}
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`${modelPrompt}.labels`).d('标签')}
              >
                {getFieldDecorator('labels')(
                  <Select allowClear mode="multiple">
                    {searchLabels.map((n) => (
                      <Select.Option key={n.id} value={n.name}>
                        {n.name}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get(`hzero.common.status`).d('状态')}
              >
                {getFieldDecorator('enabledFlag', {
                  initialValue: '1',
                })(
                  <Select allowClear>
                    {enabledFlag.map((n) => (
                      <Select.Option key={n.value} value={n.value}>
                        {n.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            </Col>
          </Row>
        </>
      </Form>
    );
  }
}
