/* eslint-disable no-return-assign */

/*
 * HttpConfigModal - http配置弹窗
 * @date: 2019-12-3
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Modal, Popconfirm, Button, Form, InputNumber, Select } from 'hzero-ui';
import { remove, isEmpty, toSafeInteger, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import { Button as ButtonPermission } from 'components/Permission';
import getLang from '@/langs/serviceLang';
import { operatorRender } from 'utils/renderer';
import EditTable from 'components/EditTable';
import intl from 'utils/intl';
import QuestionPopover from './QuestionPopover';
import styles from '../routes/Services/index.less';

const { Option } = Select;

/**
 * http配置弹窗
 * @extends {Component} - Component
 * @reactProps {boolean} visible - 是否显示代弹窗
 * @reactProps {Function} onOk - 确认
 * @reactProps {Function} onCreate - 创建
 * @reactProps {Function} onEditLine - 编辑
 * @reactProps {Function} onDelete - 删除
 * @reactProps {object} dataSource - 表格数据源
 * @reactProps {array} codeArr - 可选参数值
 * @reactProps {boolean} testLoading - 测试中标志
 * @return React.element
 */
export default class HttpConfigModal extends PureComponent {
  selectedCodes = [];

  constructor(props) {
    super(props);
    this.state = {
      candidateSelectedCodes: [],
    };
  }

  getSnapshotBeforeUpdate(prevProps) {
    const { dataSource } = this.props;
    return dataSource.length !== prevProps.dataSource.length;
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      const { dataSource } = this.props;
      const unChangedCodes = dataSource
        .filter((item) =>
          '_status' in item
            ? !isUndefined(item.$form.getFieldValue('propertyCode')) ||
              !isUndefined(item.propertyCode)
            : item.deleteFlag !== 1
        )
        .map((row) =>
          '_status' in row
            ? row.$form.getFieldValue('propertyCode') || row.propertyCode
            : row.propertyCode
        );
      this.selectedCodes = [...new Set([...unChangedCodes])];
    }
  }

  /**
   * 获取当前行参数值的取值范围
   */
  @Bind()
  handleFetchPropertyCode() {
    const { codeArr } = this.props;
    let candidateSelectedCodes = codeArr;
    if (this.selectedCodes.length) {
      candidateSelectedCodes = codeArr.filter((item) => !this.selectedCodes.includes(item.value));
    }
    this.setState({ candidateSelectedCodes });
  }

  /**
   * 选中参数值
   * @param {string} value - 参数值
   * @param {object} record - 参数值数据
   */
  @Bind()
  handleSelectPropertyCode(value, record) {
    const fieldValue = record.$form.getFieldValue('propertyCode');
    if (
      !isUndefined(fieldValue) &&
      !this.select.props.children.map((item) => item.key).includes(fieldValue)
    ) {
      remove(this.selectedCodes, (item) => item === fieldValue);
    }
    this.selectedCodes.push(value);
    this.handleFetchPropertyCode();
  }

  /**
   * 清除行
   * @param {object} record - 行数据
   */
  @Bind()
  handleCleanLine(record) {
    const { onCleanLine } = this.props;
    if (!isEmpty(this.selectedCodes)) {
      remove(this.selectedCodes, (item) => item === record.$form.getFieldValue('propertyCode'));
    }
    onCleanLine(record);
  }

  /**
   * 删除行
   * @param {object} record - 行数据
   */
  @Bind()
  handleDelLine(record) {
    const { onDelete } = this.props;
    if (!isEmpty(this.selectedCodes)) {
      remove(this.selectedCodes, (item) => item === record.propertyCode);
    }
    onDelete(record);
  }

  /**
   * 关闭弹窗
   */
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    this.selectedCodes = [];
    onCancel();
  }

  render() {
    const { visible, onOk, onCreate, dataSource, onEditLine, isHistory, isPublished } = this.props;
    const filteredDataSource = dataSource.filter((item) => item.deleteFlag !== 1);
    const { candidateSelectedCodes } = this.state;
    const isDisabled = isHistory || isPublished;
    const columns = [
      {
        title: intl.get('hitf.services.model.services.paramName').d('参数名'),
        dataIndex: 'propertyCode',
        width: '45%',
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('propertyCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.paramName').d('参数名'),
                    }),
                  },
                ],
                initialValue: val,
              })(
                <Select
                  ref={(node) => (this.select = node)}
                  style={{ width: '100%' }}
                  disabled={isDisabled}
                  onFocus={this.handleFetchPropertyCode}
                  onSelect={(value) => this.handleSelectPropertyCode(value, record)}
                >
                  {candidateSelectedCodes.map((item) => (
                    <Option key={item.value} value={item.value}>
                      {item.value}
                    </Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hitf.services.model.services.paramValue').d('参数值'),
        dataIndex: 'propertyValue',
        disabled: true,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('propertyValue', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.paramValue').d('参数值'),
                    }),
                  },
                ],
                initialValue: val,
              })(
                <InputNumber
                  min={0}
                  initialValue={5000}
                  step={5000}
                  max={2147483647}
                  disabled={isDisabled}
                  className={styles.inputNumberUnlimited}
                  parser={toSafeInteger}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: '15%',
        render: (_, record) => {
          const operators = [];
          if (record._status === 'create') {
            operators.push({
              key: 'clean',
              ele: (
                <ButtonPermission
                  type="text"
                  disabled={isDisabled}
                  onClick={() => {
                    this.handleCleanLine(record);
                  }}
                >
                  {intl.get('hzero.common.button.clean').d('清除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.clean').d('清除'),
            });
          } else if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <ButtonPermission
                  type="text"
                  disabled={isDisabled}
                  onClick={() => onEditLine(record, false)}
                >
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </ButtonPermission>
                // <a onClick={() => onEditLine(record, false)}>
                //   {intl.get('hzero.common.button.cancel').d('取消')}
                // </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          } else {
            operators.push(
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    disabled={isDisabled}
                    onClick={() => onEditLine(record, true)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                  // <a onClick={() => onEditLine(record, true)}>
                  //   {intl.get('hzero.common.button.edit').d('编辑')}
                  // </a>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'delete',
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    placement="topRight"
                    onConfirm={() => this.handleDelLine(record)}
                  >
                    <ButtonPermission type="text" disabled={isDisabled}>
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              }
            );
          }
          return operatorRender(operators, record);
        },
      },
    ];

    const tableProps = {
      dataSource: filteredDataSource,
      columns,
      bordered: true,
      pagination: false,
      rowKey: 'httpConfigId',
    };

    return (
      <Modal
        title={
          <QuestionPopover
            text={getLang('HTTP_CONN_CONFIG')}
            message={getLang('HTTP_CONN_CONFIG_TIP')}
          />
        }
        visible={visible}
        onOk={onOk}
        onCancel={this.handleCancel}
        footer={[
          <Button key="back" onClick={this.handleCancel}>
            {intl.get('hitf.common.button.cancel').d('取消')}
          </Button>,
          <Button hidden={isDisabled} key="submit" type="primary" onClick={onOk}>
            {intl.get('hitf.common.button.confirm').d('确定')}
          </Button>,
        ]}
      >
        <div className="table-list-operator" style={{ textAlign: 'right' }}>
          <Button hidden={isDisabled} type="primary" onClick={onCreate}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </div>
        <EditTable {...tableProps} />
      </Modal>
    );
  }
}
