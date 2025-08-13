import React, { PureComponent } from 'react';
import { Modal, Form, Input, Select, Button, Table, Row, Col } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind, Throttle } from 'lodash-decorators';

import intl from 'utils/intl';
import { valueMapMeaning } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import {
  DEBOUNCE_TIME,
  FORM_COL_3_4_LAYOUT,
  FORM_COL_3_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

import Lov from 'components/Lov';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class TemplateDrawer extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

  componentWillUnmount() {
    this.toggleForm.cancel();
  }

  /**
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { templateListRowSelection, onCancel, onOk } = this.props;
    if (isEmpty(templateListRowSelection)) {
      onCancel();
    } else {
      onOk();
    }
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (isEmpty(err)) {
          // 如果验证成功,则执行search
          onSearch();
        }
      });
    }
  }

  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  renderSearchForm() {
    const {
      form: { getFieldDecorator },
      templateTypeCode = [],
      tenantRoleLevel,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
              {!tenantRoleLevel && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator(
                      'tenantId',
                      {}
                    )(<Lov code="HPFM.TENANT" style={{ width: '130px' }} textField="tenantName" />)}
                  </Form.Item>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('entity.template.code').d('模板代码')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator(
                    'templateCode',
                    {}
                  )(
                    <Input typeCase="upper" trim inputChinese={false} style={{ width: '130px' }} />
                  )}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('entity.template.name').d('模板名称')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('templateName', {})(<Input style={{ width: '130px' }} />)}
                </Form.Item>
              </Col>
              {tenantRoleLevel && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('entity.template.type').d('模板类型')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator(
                      'templateTypeCode',
                      {}
                    )(
                      <Select allowClear style={{ width: '130px' }}>
                        {templateTypeCode &&
                          templateTypeCode.map((item) => (
                            <Option key={item.value} value={item.value}>
                              {item.meaning}
                            </Option>
                          ))}
                      </Select>
                    )}
                  </Form.Item>
                </Col>
              )}
            </Row>
            <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('entity.template.type').d('模板类型')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator(
                    'templateTypeCode',
                    {}
                  )(
                    <Select allowClear style={{ width: '130px' }}>
                      {templateTypeCode &&
                        templateTypeCode.map((item) => (
                          <Option key={item.value} value={item.value}>
                            {item.meaning}
                          </Option>
                        ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
            </Row>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              {!tenantRoleLevel && (
                <Button onClick={this.toggleForm}>
                  {expandForm
                    ? intl.get('hzero.common.button.collected').d('收起查询')
                    : intl.get('hzero.common.button.viewMore').d('更多查询')}
                </Button>
              )}
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

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      confirmLoading,
      dataSource,
      onChange,
      visible,
      onCancel,
      templateListRowSelection,
      templateListPagination,
      tenantRoleLevel,
      templateTypeCode = [],
    } = this.props;
    const columns = [
      !tenantRoleLevel && {
        title: intl.get('hzero.common.model.tenantName').d('租户'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.tempCode').d('模板代码'),
        dataIndex: 'templateCode',
        width: 200,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.tempName').d('模板名称'),
        dataIndex: 'templateName',
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.tempType').d('模板类型'),
        dataIndex: 'templateTypeCode',
        width: 100,
        render: (val) => valueMapMeaning(templateTypeCode, val),
      },
    ].filter(Boolean);
    return (
      <Modal
        destroyOnClose
        width={820}
        title={intl.get('hrpt.reportDefinition.view.message.templateTitle').d('添加模板')}
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        confirmLoading={confirmLoading}
      >
        {this.renderSearchForm()}
        <Table
          bordered
          rowKey="templateId"
          loading={loading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={templateListPagination}
          rowSelection={templateListRowSelection}
          onChange={(page) => onChange(page)}
        />
      </Modal>
    );
  }
}
