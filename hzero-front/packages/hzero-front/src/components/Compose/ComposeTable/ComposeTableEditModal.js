import React from 'react';
import { filter, findIndex, forEach, isFunction, map, omit } from 'lodash';
import { Modal } from 'hzero-ui';
import uuid from 'uuid/v4';

import intl from 'utils/intl';

import BaseComposeTable from './BaseComposeTable';
import ComposeForm from '../ComposeForm';
import { getComponentProps } from '../ComposeForm/utils';
import { getDisplayValue, getWidthFromWord } from '../utils';
import Upload from '../../Upload';

/**
 * 获取计算得来的属性(且耗时多)
 * @param {Object} props
 */
function getComputeTableProps(props) {
  const { onRowEdit, fields, editable, addable, context, removable } = props;
  // let index = 0;
  // columnWith 在 删除模式下 需要 加上 checkbox 的宽度
  let columnsWidth = removable ? 60 : 0;
  const columns = map(fields, field => {
    // const required = field.requiredFlag !== 0;
    const columnWidth = getWidthFromWord({
      word: field.fieldDescription,
      minWidth: 60,
      fontWidth: 12,
      defaultWidth: 100,
      paddingWidth: 36,
    });
    columnsWidth += columnWidth;
    const column = {
      dataIndex: field.fieldCode,
      title: field.fieldDescription,
      width: columnWidth,
    };
    if (field.componentType === 'Upload') {
      const uploadProps = getComponentProps({
        field,
        componentType: 'Upload',
        context,
      });
      column.render = (_, record) => {
        const attachmentUUID = record[field.fieldCode];
        return (
          attachmentUUID && <Upload {...uploadProps} viewOnly attachmentUUID={attachmentUUID} />
        );
      };
    } else if (field.componentType === 'Checkbox' || field.componentType === 'Switch') {
      column.render = item =>
        item === 1
          ? intl.get('hzero.common.status.yes').d('是')
          : intl.get('hzero.common.status.no').d('否');
    } else {
      column.render = (_, record) => getDisplayValue(field, record);
    }
    return column;
  });
  if (editable) {
    columns.unshift({
      title: intl.get('hzero.common.button.edit').d('编辑'),
      key: 'edit',
      width: 80,
      render: (_, record) => (
        <a
          onClick={() => {
            onRowEdit(record);
          }}
        >
          {intl.get('hzero.common.button.edit').d('编辑')}
        </a>
      ),
    });
  } else if (addable) {
    columns.unshift({
      title: intl.get('hzero.common.button.edit').d('编辑'),
      key: 'edit',
      width: 80,
      render: (_, record) => {
        if (record.isCreate) {
          return (
            <a
              onClick={() => {
                onRowEdit(record);
              }}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </a>
          );
        }
      },
    });
  }
  return {
    scroll: { x: columnsWidth },
    columns,
  };
}

export default class ComposeTableEditModal extends React.PureComponent {
  state = {};

  // composeForm 的this
  composeForm;

  /**
   * 控制半受控属性 dataSource
   * 当 父组件 dataSource 改变时, 使用父组件的 dataSource, 之后都是本组件自己的dataSource
   * @param {Object} nextProps - 接收的属性
   * @param {Object} prevState - 上一个State
   */
  static getDerivedStateFromProps(nextProps, prevState) {
    if (nextProps.dataSource !== prevState.prevDataSource) {
      return {
        dataSource: nextProps.dataSource,
        prevDataSource: nextProps.dataSource,
      };
    }
    return null;
  }

  constructor(props) {
    super(props);
    this.getDataSource = this.getDataSource.bind(this);
    this.getValidateDataSource = this.getValidateDataSource.bind(this);
    this.refComposeForm = this.refComposeForm.bind(this);
    this.handleRowSelectionChange = this.handleRowSelectionChange.bind(this);
    this.handleRemove = this.handleRemove.bind(this);
    this.dataSourceRemove = this.dataSourceRemove.bind(this);
    this.handleAdd = this.handleAdd.bind(this);
    this.handleRowEdit = this.handleRowEdit.bind(this);
    this.handleModalOkBtnClick = this.handleModalOkBtnClick.bind(this);
    this.handleModalCancelBtnClick = this.handleModalCancelBtnClick.bind(this);
    this.handleAfterModalClose = this.handleAfterModalClose.bind(this);
  }

  componentDidMount() {
    const { onGetValidateDataSourceHook, onGetDataSourceHook } = this.props;
    if (isFunction(onGetValidateDataSourceHook)) {
      onGetValidateDataSourceHook(this.getValidateDataSource);
    }
    if (isFunction(onGetDataSourceHook)) {
      onGetDataSourceHook(this.getDataSource);
    }
  }

  getDataSource() {
    const { dataSource } = this.state;
    return map(filter(dataSource, r => r.isCreate || r.isUpdate), r =>
      omit(r, ['isCreate', 'isUpdate'])
    );
  }

  getValidateDataSource() {
    const { dataSource } = this.state;
    const { rowKey = 'id' } = this.props;
    return Promise.resolve(
      map(filter(dataSource, r => r.isCreate || r.isUpdate), r => {
        if (r.isCreate) {
          return omit(r, ['isCreate', 'isUpdate', rowKey]);
        }
        return omit(r, ['isCreate', 'isUpdate']);
      })
    );
  }

  refComposeForm(composeForm) {
    const { refEditComposeForm } = this.props;
    this.composeForm = composeForm;
    if (isFunction(refEditComposeForm)) {
      refEditComposeForm(composeForm);
    }
  }

  // 删除

  /**
   *
   * @param {Array} selectedRowKeys - 选中记录的rowKey
   * @param {Array} selectedRows - 选中记录
   */
  handleRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  handleRemove() {
    const { dataSource, selectedRowKeys } = this.state;
    const { rowKey } = this.props;
    const removeRows = [];
    forEach(dataSource, r => {
      if (findIndex(selectedRowKeys, rowId => rowId === r[rowKey]) !== -1) {
        if (!r.isCreate) {
          removeRows.push(r);
        }
      }
    });
    if (removeRows.length > 0) {
      // 调用 父组件 传进来的 方法
      const { onRemove } = this.props;
      if (isFunction(onRemove)) {
        this.setState({
          removeLoading: true,
        });
        onRemove(map(removeRows, row => row[rowKey]), removeRows, { onOk: this.dataSourceRemove });
      }
    } else {
      this.dataSourceRemove();
    }
  }

  dataSourceRemove() {
    const { dataSource, selectedRowKeys } = this.state;
    const { rowKey } = this.props;
    const nextDataSource = [];
    forEach(dataSource, r => {
      if (findIndex(selectedRowKeys, rowId => rowId === r[rowKey]) === -1) {
        nextDataSource.push(r);
      }
    });
    this.setState({
      selectedRowKeys: [],
      selectedRows: [],
      dataSource: nextDataSource,
      removeLoading: false,
    });
  }

  // 编辑

  /**
   * 打开新增模态框
   */
  handleAdd() {
    this.setState({
      isUpdate: false,
      editRecord: {},
      modalProps: {
        visible: true,
      },
      composeFormProps: {
        dataSource: {},
      },
    });
  }

  /**
   * 点击编辑触发
   * @param {Object} record - 编辑的属性
   */
  handleRowEdit(record) {
    this.setState({
      isUpdate: true,
      editRecord: record,
      modalProps: {
        visible: true,
      },
      composeFormProps: {
        dataSource: record,
      },
    });
  }

  /**
   * 编辑模态框 确认按钮点击
   */
  handleModalOkBtnClick() {
    if (this.composeForm) {
      const { form } = this.composeForm.props;
      form.validateFields((err, fieldsValue) => {
        if (!err) {
          const { isUpdate } = this.state;
          if (isUpdate) {
            this.rowUpdate(fieldsValue);
          } else {
            this.rowCreate(fieldsValue);
          }
        }
      });
    }
  }

  /**
   * 更新记录
   * @param {Object}} fieldsValue - 编辑的数据
   */
  rowUpdate(fieldsValue) {
    const { editRecord = {}, dataSource = [] } = this.state;
    this.setState({
      editRecord: null,
      modalProps: {},
      composeFormProps: {},
      dataSource: map(dataSource, r => {
        if (r === editRecord) {
          return {
            ...r,
            ...fieldsValue,
            isUpdate: true,
          };
        }
        return r;
      }),
    });
  }

  /**
   * 新增记录
   * @param {Object}} fieldsValue - 编辑的数据
   */
  rowCreate(fieldsValue) {
    const { dataSource = [] } = this.state;
    const { rowKey } = this.props;
    this.setState({
      editRecord: null,
      modalProps: {},
      composeFormProps: {},
      dataSource: [...dataSource, { ...fieldsValue, [rowKey]: uuid(), isCreate: true }],
    });
  }

  /**
   * 编辑模态框 取消按钮点击
   */
  handleModalCancelBtnClick() {
    this.setState({
      editRecord: null,
      modalProps: {},
      composeFormProps: {},
    });
  }

  /**
   * 编辑模态框 关闭
   */
  handleAfterModalClose() {
    if (this.composeForm) {
      const { form } = this.composeForm.props;
      form.resetFields();
    }
  }

  render() {
    const {
      modalProps = {},
      composeFormProps = {},
      dataSource,
      confirmLoading,
      removeLoading,
      selectedRowKeys,
      selectedRows,
    } = this.state;
    const {
      fields,
      editModalTitle,
      fieldLabelWidth = 200,
      organizationId,
      context,
      loading,
    } = this.props;
    const composeTableProps = this.props;
    return (
      <>
        <BaseComposeTable
          getComputeTableProps={getComputeTableProps}
          {...composeTableProps}
          onRef={this.refComposeTable}
          dataSource={dataSource}
          onRowEdit={this.handleRowEdit}
          onAdd={this.handleAdd}
          onRemove={this.handleRemove}
          onRowSelectionChange={this.handleRowSelectionChange}
          selectedRowKeys={selectedRowKeys}
          selectedRows={selectedRows}
          loading={loading || removeLoading}
        />
        {modalProps.visible && (
          <Modal
            {...modalProps}
            title={editModalTitle}
            onOk={this.handleModalOkBtnClick}
            onCancel={this.handleModalCancelBtnClick}
            wrapClassName="ant-modal-sidebar-right"
            transitionName="move-right"
            width={1000}
            afterClose={this.handleAfterModalClose}
            confirmLoading={confirmLoading}
          >
            <ComposeForm
              {...composeFormProps}
              context={context}
              organizationId={organizationId}
              fieldLabelWidth={fieldLabelWidth}
              editable
              col={2}
              fields={fields}
              onRef={this.refComposeForm}
            />
          </Modal>
        )}
      </>
    );
  }
}
