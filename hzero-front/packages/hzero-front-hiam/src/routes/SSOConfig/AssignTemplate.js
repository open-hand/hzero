/**
 * 二级域名模板分配--模板配置
 * @date: 2019-7-11
 * @author: XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Col, Form, Input, Modal, Row, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { CODE_UPPER } from 'utils/regExp';

import intl from 'utils/intl';
import {
  FORM_COL_3_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldPropName: null })
export default class AssignTemplate extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedRows: [],
    };
  }

  /**
   * 勾选回调
   * @param {*} _
   * @param {*} selectedRows - 勾选的值集行
   */
  @Bind()
  handleSelectRows(_, selectedRows) {
    this.setState({
      selectedRows,
    });
  }

  // 模态框点击确定
  @Bind()
  handleOnOk() {
    const { onOk } = this.props;
    const { selectedRows } = this.state;
    onOk(selectedRows);
    this.setState({
      selectedRows: [],
    });
  }

  @Bind()
  handleOnCancel() {
    const { onCancel } = this.props;
    this.setState({
      selectedRows: [],
    });
    onCancel();
  }

  // 重置表单
  @Bind()
  handleReset() {
    const { form, reset } = this.props;
    reset(form);
  }

  @Bind()
  handleTableChange(assignablePagination) {
    const { onTableChange } = this.props;
    onTableChange(assignablePagination);
  }

  // 查询表单
  @Bind()
  handleSearch() {
    const { form, search } = this.props;
    search(form);
  }

  getColumns() {
    return [
      {
        dataIndex: 'templateCode',
        title: intl.get('hiam.ssoConfig.model.ssoConfig.templateCode').d('模板编码'),
      },
      {
        dataIndex: 'templateName',
        title: intl.get('hiam.ssoConfig.model.ssoConfig.templateName').d('模板名称'),
      },
      {
        dataIndex: 'templatePath',
        title: intl.get('hiam.ssoConfig.model.ssoConfig.templatePath').d('模板路径'),
      },
    ];
  }

  render() {
    const {
      form,
      modalVisible,
      assignablePagination,
      assignableList,
      getAssignableListLoading,
    } = this.props;
    const columns = this.getColumns();
    const { selectedRows } = this.state;
    const rowSelection = {
      selectedRowKeys: selectedRows.map(n => n.templateId),
      onChange: this.handleSelectRows,
    };
    return (
      <Modal
        visible={modalVisible}
        destroyOnClose
        title={intl.get('hiam.ssoConfig.view.message.title').d('分配模板')}
        width={1000}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onOk={this.handleOnOk}
        onCancel={this.handleOnCancel}
      >
        <Form className={SEARCH_FORM_CLASSNAME}>
          <Row {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.ssoConfig.model.ssoConfig.templateCode').d('模板编码')}
              >
                {form.getFieldDecorator('templateCode', {
                  rules: [
                    {
                      pattern: CODE_UPPER,
                      message: intl
                        .get('hzero.common.validation.codeUpper')
                        .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                  ],
                })(<Input trim inputChinese={false} typeCase="upper" />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.ssoConfig.model.ssoConfig.templateName').d('模板名称')}
              >
                {form.getFieldDecorator('templateName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
              <Form.Item>
                <Button onClick={this.handleReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button
                  style={{ marginLeft: 8 }}
                  htmlType="submit"
                  type="primary"
                  onClick={this.handleSearch}
                >
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
        <Table
          bordered
          rowKey="templateId"
          columns={columns}
          loading={getAssignableListLoading}
          dataSource={assignableList}
          pagination={assignablePagination}
          rowSelection={rowSelection}
          onChange={this.handleTableChange}
        />
      </Modal>
    );
  }
}
