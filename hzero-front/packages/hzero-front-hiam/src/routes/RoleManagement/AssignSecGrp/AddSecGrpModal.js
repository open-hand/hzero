/**
 * AddSecGrpModal - 新建安全组弹窗
 * @author: lingfangzi.hu01@hand-china.com
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Modal, Table, Button, notification, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { filterNullValueObject } from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * 使用 Form.Item 组件
 */
const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class AddSecGrpModal extends React.Component {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      addRows: [],
    };
  }

  /**
   * 点击确定触发事件
   */
  @Bind()
  okHandle() {
    const { addData } = this.props;
    const { addRows } = this.state;
    if (addRows.length) {
      addData(addRows);
    } else {
      notification.warning({
        message: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
      });
    }
  }

  /**
   * 点击取消触发事件
   */
  @Bind()
  cancelHandle() {
    const { onHideAddModal } = this.props;
    if (onHideAddModal) {
      onHideAddModal(false);
    }
  }

  /**
   *分页change事件
   */
  @Bind()
  handleTableChange(pagination = {}) {
    const { fetchModalData, form } = this.props;
    const filterValues = filterNullValueObject(form.getFieldsValue());
    if (fetchModalData) {
      fetchModalData({
        page: pagination,
        ...filterValues,
      });
    }
  }

  /**
   * 表格勾选
   * @param {null} _ 占位
   * @param {object} selectedRow 选中行
   */
  @Bind()
  onSelectChange(_, selectedRow) {
    this.setState({ addRows: selectedRow });
  }

  /**
   * 表单重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 查询方法
   */
  @Bind()
  queryValue() {
    const { form, fetchModalData } = this.props;
    if (fetchModalData) {
      const filterValues = filterNullValueObject(form.getFieldsValue());
      fetchModalData(filterValues || {});
    }
    this.setState({ addRows: [] });
  }

  @Bind()
  renderForm() {
    const { queryCode, queryName, queryCodeDesc, queryNameDesc } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom">
          <Col span={8}>
            <FormItem {...SEARCH_FORM_ITEM_LAYOUT} label={queryNameDesc}>
              {getFieldDecorator(`${queryName}`)(<Input />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem {...SEARCH_FORM_ITEM_LAYOUT} label={queryCodeDesc}>
              {getFieldDecorator(`${queryCode}`)(<Input trim inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem>
              <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.queryValue}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    const {
      title,
      modalVisible,
      loading,
      confirmLoading,
      rowKey,
      columns = [],
      dataSource = [],
      pagination = {},
      isQuery = true,
    } = this.props;
    const { addRows } = this.state;
    const rowSelection = {
      onChange: this.onSelectChange,
      selectedRowKeys: addRows.map(n => n[rowKey]),
    };
    return (
      <Modal
        destroyOnClose
        confirmLoading={confirmLoading}
        title={title}
        visible={modalVisible}
        onOk={this.okHandle}
        width={800}
        onCancel={this.cancelHandle}
      >
        {isQuery && <div className="table-list-search">{this.renderForm()}</div>}
        <Table
          bordered
          rowKey={rowKey}
          columns={columns}
          loading={loading}
          dataSource={dataSource}
          rowSelection={rowSelection}
          pagination={pagination}
          onChange={this.handleTableChange}
        />
      </Modal>
    );
  }
}
