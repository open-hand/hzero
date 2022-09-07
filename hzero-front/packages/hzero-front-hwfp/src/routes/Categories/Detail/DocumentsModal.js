/*
 * DocumentsModal.js - 分类弹窗
 * @date: 2019-04-29
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Input, Button, Table, Modal } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { createPagination, getCurrentOrganizationId } from 'utils/utils';

import styles from './index.less';

const rowKey = 'documentId';

@Form.create({ fieldNameProp: null })
export default class DocumentsModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [], // 数据源
      pagination: {}, // 分页
      selectedListRows: [], // 选中的行
    };
  }

  getSnapshotBeforeUpdate(preProps) {
    const { visible } = preProps;
    if (!visible && visible !== this.props.visible) {
      return true;
    }
    return false;
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (snapshot) {
      this.handleFetchList();
    }
  }

  /**
   * handleFetchList - 查询列表行数据
   * @param {object} page - 查询条件
   */
  @Bind()
  handleFetchList(page = {}) {
    const {
      headerInfo = {},
      dataSource = [],
      form: { getFieldsValue },
      onHandleSearchDocuments,
    } = this.props;
    const { tenantId = getCurrentOrganizationId() } = headerInfo;
    onHandleSearchDocuments({
      ...getFieldsValue(),
      documentIdSet: dataSource.map(n => n[rowKey]),
      page,
      tenantId,
    }).then(res => {
      if (res) {
        this.setState({
          dataSource: res.content || [],
          pagination: createPagination(res),
        });
      }
    });
  }

  /**
   * 选中行改变回调
   * @param {Array} selectedListRows
   * @param {Object} selectedRows
   */
  @Bind()
  handleRowSelectedChange(_, selectedRows) {
    this.setState({ selectedListRows: selectedRows });
  }

  // 重置表单
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  /**
   * 新增流程单据
   */
  @Bind()
  handleAddCategories() {
    const { selectedListRows } = this.state;
    const { onHandleAddCategories, onCloseCategoryModal } = this.props;
    onHandleAddCategories(selectedListRows);
    this.setState({ selectedListRows: [] });
    onCloseCategoryModal();
  }

  /**
   * 关闭流程单据弹窗
   */
  @Bind()
  handleModalCancel() {
    const { onCloseCategoryModal } = this.props;
    this.setState({ selectedListRows: [] });
    onCloseCategoryModal();
  }

  render() {
    const {
      visible,
      loading,
      form: { getFieldDecorator },
    } = this.props;
    const { dataSource = [], pagination = {}, selectedListRows = [] } = this.state;
    const columns = [
      {
        title: intl.get(`hwfp.common.model.common.documentCode`).d('流程单据编码'),
        width: 180,
        dataIndex: 'documentCode',
      },
      {
        title: intl.get(`hwfp.common.model.common.documentDescription`).d('流程单据描述'),
        dataIndex: 'description',
      },
    ];
    const tableProps = {
      loading,
      dataSource,
      pagination,
      columns,
      rowKey,
      bordered: true,
      onChange: this.handleFetchList,
      rowSelection: {
        selectedRowKeys: selectedListRows.map(n => n[rowKey]),
        onChange: this.handleRowSelectedChange,
      },
    };
    return (
      <Modal
        className={styles['category-add-modal']}
        destroyOnClose
        visible={visible}
        title={intl.get(`hwfp.common.view.message.title.document`).d('流程单据')}
        width={900}
        okButtonProps={{ disabled: isEmpty(selectedListRows) }}
        onOk={this.handleAddCategories}
        onCancel={this.handleModalCancel}
      >
        <Form layout="inline">
          <Form.Item label={intl.get(`hwfp.common.model.common.documentCode`).d('流程单据编码')}>
            {getFieldDecorator('documentCode')(<Input typeCase="upper" inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            label={intl.get(`hwfp.common.model.common.documentDescription`).d('流程单据描述')}
          >
            {getFieldDecorator('description')(<Input />)}
          </Form.Item>
          <Form.Item>
            <Button onClick={this.handleFormReset}>
              {intl.get(`hzero.common.button.reset`).d('重置')}
            </Button>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" onClick={() => this.handleFetchList()}>
              {intl.get(`hzero.common.button.search`).d('查询')}
            </Button>
          </Form.Item>
        </Form>
        <Table {...tableProps} />
      </Modal>
    );
  }
}
