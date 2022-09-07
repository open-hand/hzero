import React, { PureComponent } from 'react';
import { Button, Col, DatePicker, Form, Input, Row, Select, TreeSelect } from 'hzero-ui';
import moment from 'moment';
import { Bind, Throttle } from 'lodash-decorators';

import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getDateFormat, getCurrentOrganizationId } from 'utils/utils';

import {
  DEBOUNCE_TIME,
  FORM_COL_3_4_LAYOUT,
  FORM_COL_3_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const dateFormat = getDateFormat();

/**
 * 抄送流程查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} search - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hwfp/carbon-copy-task/list' })
export default class FilterForm extends PureComponent {
  /**
   * state初始化
   */
  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
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
   * 多查询条件展示
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
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
      tenantId,
      form: { getFieldDecorator, getFieldValue },
      processStatus = [],
    } = this.props;
    const { expandForm } = this.state;
    const processStatusList = processStatus.map((item) => ({
      title: item.meaning,
      value: item.value,
      key: item.value,
    }));
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hwfp.common.model.process.description').d('流程描述')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('processDescriptionLike')(<Input dbc2sbc={false} />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hzero.common.date.creation.from').d('创建日期从')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('startedAfter')(
                    <DatePicker
                      format={dateFormat}
                      placeholder=""
                      disabledDate={(currentDate) =>
                        getFieldValue('startedBefore') &&
                        moment(getFieldValue('startedBefore')).isBefore(currentDate, 'day')
                      }
                    />
                  )}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hzero.common.date.creation.to').d('创建日期至')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('startedBefore')(
                    <DatePicker
                      format={dateFormat}
                      placeholder=""
                      disabledDate={(currentDate) =>
                        getFieldValue('startedAfter') &&
                        moment(getFieldValue('startedAfter')).isAfter(currentDate, 'day')
                      }
                    />
                  )}
                </Form.Item>
              </Col>
            </Row>
            <Row style={{ display: !expandForm ? 'none' : 'block' }} {...SEARCH_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hwfp.common.model.process.ID').d('流程标识')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('processInstanceId')(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hwfp.common.model.process.name').d('流程名称')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('processDefinitionNameLike')(<Input dbc2sbc={false} />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hwfp.common.model.apply.owner').d('申请人')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('startUserName')(
                    <Lov code="HPFM.EMPLOYEE" queryParams={{ tenantId }} />
                  )}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hwfp.common.model.documents.class').d('流程单据')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('documentId')(
                    <Lov
                      code="HWFP.PROCESS_DOCUMENT"
                      queryParams={{ tenantId: getCurrentOrganizationId() }}
                      textField="documentCode"
                    />
                  )}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hwfp.carbonCopyTask.view.message.priority').d('状态')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('readFlag')(
                    <Select allowClear>
                      <Select.Option key="1" value={1}>
                        {intl.get('hzero.common.process.readFlagY').d('已读')}
                      </Select.Option>
                      <Select.Option key="0" value={0}>
                        {intl.get('hzero.common.process.readFlagN').d('未读')}
                      </Select.Option>
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col span={16} style={{ paddingLeft: 3 }}>
                <Form.Item
                  label={intl.get('hwfp.common.model.process.approvalStatus').d('审批状态')}
                  labelCol={{
                    span: 5,
                  }}
                  wrapperCol={{
                    span: 19,
                  }}
                >
                  {getFieldDecorator('processStatusList')(
                    <TreeSelect treeCheckable allowClear treeData={processStatusList} />
                  )}
                </Form.Item>
              </Col>
            </Row>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button data-code="reset" style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
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
    );
  }
}
