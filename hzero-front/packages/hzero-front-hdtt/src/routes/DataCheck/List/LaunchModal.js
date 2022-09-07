/**
 * LanchModal - 发起核对弹窗
 * @date: 2019-7-29
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Modal, Form, Radio, Table, Input, Button, Row, Col } from 'hzero-ui';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import notification from 'utils/notification';
import { SEARCH_FORM_ROW_LAYOUT, FORM_COL_2_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
const formLayOut = {
  labelCol: {
    span: 8,
  },
  wrapperCol: {
    span: 16,
  },
};

/**
 * 发起核对弹窗
 * @extends {Component} - React.Component
 * @reactProps {Boolean} isShowModal - 是否显示弹窗
 * @reactProps {Array} levelTypes - 核对层级值集
 * @reactProps {Array} sourceList - 来源表数据
 * @reactProps {Object} sourcePagination - 来源表分页器
 * @reactProps {Array} targetList - 目标表数据
 * @reactProps {Object} targetPagination - 目标表分页器
 * @reactProps {Boolean} sourceLoading - 来源表加载标志
 * @reactProps {Boolean} targetLoading - 目标表加载标志
 * @reactProps {Boolean} confirmLoading - 确认核对加载标志
 * @reactProps {Function} onCancel - 关闭弹窗
 * @reactProps {Function} onSearchSourceList - 来源表分页查询
 * @reactProps {Function} onSearchTargetList - 目标表分页查询
 * @reactProps {Function} onLaunch - 确认核对
 */
@Form.create({ fieldNameProp: null })
export default class LanchModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addRows: [],
      currentType: '',
    };
  }

  /**
   * 改变核对层级时
   */
  @Bind()
  handleChange(e) {
    const { onSearchSourceList, onSearchTargetList } = this.props;
    this.setState({
      currentType: e.target.value,
      addRows: [],
    });
    if (e.target.value === 'SOURCE_TABLE') {
      onSearchSourceList();
    } else if (e.target.value === 'TARGET_TABLE') {
      onSearchTargetList();
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
   * 切换分页
   * @param {object} page - 分页
   */
  @Bind()
  handlePageChange(page = {}) {
    const { currentType } = this.state;
    const { onSearchSourceList, onSearchTargetList, form } = this.props;
    const payload = {
      page: isEmpty(page) ? {} : page,
    };
    form.validateFields((err, values) => {
      if (!err) {
        if (currentType === 'SOURCE_TABLE') {
          if (values.tableName) {
            payload.tableName = values.tableName;
          }
          onSearchSourceList(payload);
        } else if (currentType === 'TARGET_TABLE') {
          if (values.consumerTable) {
            payload.consumerTable = values.consumerTable;
          }
          onSearchTargetList(payload);
        }
      }
    });
    this.setState({ addRows: [] });
  }

  /**
   * 重置表单
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    const { currentType } = this.state;
    if (currentType === 'SOURCE_TABLE') {
      form.resetFields('tableName');
    } else {
      form.resetFields('consumerTable');
    }
  }

  /**
   * 确认发起核对
   */
  @Bind()
  handleOk() {
    const { addRows, currentType } = this.state;
    const { onLaunch } = this.props;
    if (currentType === 'ALL') {
      onLaunch(addRows, currentType);
    } else if (currentType === 'SOURCE_TABLE' || currentType === 'TARGET_TABLE') {
      if (addRows.length) {
        onLaunch(addRows, currentType);
      } else {
        notification.warning({
          message: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
        });
      }
    } else {
      notification.warning({
        message: intl
          .get('hdtt.dataCheck.model.dataCheck.validation.checkLevel')
          .d('请选择核对维度'),
      });
    }
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    this.setState({
      addRows: [],
      currentType: '',
    });
    onCancel();
  }

  render() {
    const {
      isShowModal,
      levelTypes,
      confirmLoading,
      sourceList,
      sourcePagination,
      sourceLoading,
      targetList,
      targetPagination,
      targetLoading,
      form: { getFieldDecorator, getFieldValue },
    } = this.props;
    const { addRows, currentType } = this.state;
    const rowKey =
      currentType === 'SOURCE_TABLE'
        ? 'producerConfigId'
        : currentType === 'TARGET_TABLE'
        ? 'consDbConfigId'
        : '';
    const rowSelection = {
      onChange: this.onSelectChange,
      selectedRowKeys: addRows.map(n => n[rowKey]),
    };
    const sourceColumns = [
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.fromService').d('来源服务'),
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.fromDb').d('来源DB'),
        dataIndex: 'initDbCode',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.fromTable').d('来源表'),
        dataIndex: 'tableName',
      },
    ];
    const targetColumns = [
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.toService').d('目标服务'),
        dataIndex: 'consumerService',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.toDb').d('目标DB'),
        dataIndex: 'consumerDb',
      },
      {
        title: intl.get('hdtt.dataCheck.model.dataCheck.toTable').d('目标表'),
        dataIndex: 'consumerTable',
      },
    ];

    return (
      <Modal
        width={800}
        visible={isShowModal}
        destroyOnClose
        confirmLoading={confirmLoading}
        title={intl.get('hdtt.dataCheck.view.message.title.modal.params').d('参数选择')}
        onCancel={this.handleCancel}
        onOk={this.handleOk}
      >
        <Form layout="inline" className="more-fields-search-form">
          <Row {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hdtt.dataCheck.model.dataCheck.dimension').d('核对维度')}
                {...formLayOut}
              >
                {getFieldDecorator('dimension')(
                  <Radio.Group name="dimension" onChange={this.handleChange}>
                    {levelTypes &&
                      levelTypes.map(({ value, meaning }) => (
                        <Radio value={value} key={value}>
                          {meaning}
                        </Radio>
                      ))}
                  </Radio.Group>
                )}
              </FormItem>
            </Col>
          </Row>
          {(getFieldValue('dimension') === 'SOURCE_TABLE' ||
            getFieldValue('dimension') === 'TARGET_TABLE') && (
            <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ marginBottom: '15px' }}>
              {getFieldValue('dimension') === 'SOURCE_TABLE' && (
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl.get('hdtt.dataCheck.model.dataCheck.fromTable').d('来源表')}
                    {...formLayOut}
                  >
                    {getFieldDecorator('tableName')(<Input />)}
                  </FormItem>
                </Col>
              )}
              {getFieldValue('dimension') === 'TARGET_TABLE' && (
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl.get('hdtt.dataCheck.model.dataCheck.toTable').d('目标表')}
                    {...formLayOut}
                  >
                    {getFieldDecorator('consumerTable')(<Input />)}
                  </FormItem>
                </Col>
              )}
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem {...formLayOut}>
                  <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button type="primary" htmlType="submit" onClick={this.handlePageChange}>
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </FormItem>
              </Col>
            </Row>
          )}
          {getFieldValue('dimension') === 'SOURCE_TABLE' && (
            <Table
              bordered
              rowKey="producerConfigId"
              columns={sourceColumns}
              loading={sourceLoading}
              dataSource={sourceList}
              rowSelection={rowSelection}
              pagination={sourcePagination}
              scroll={{ x: tableScrollWidth(sourceColumns) }}
              onChange={page => this.handlePageChange(page)}
            />
          )}
          {getFieldValue('dimension') === 'TARGET_TABLE' && (
            <Table
              bordered
              rowKey="consDbConfigId"
              columns={targetColumns}
              loading={targetLoading}
              dataSource={targetList}
              rowSelection={rowSelection}
              pagination={targetPagination}
              scroll={{ x: tableScrollWidth(targetColumns) }}
              onChange={page => this.handlePageChange(page)}
            />
          )}
        </Form>
      </Modal>
    );
  }
}
