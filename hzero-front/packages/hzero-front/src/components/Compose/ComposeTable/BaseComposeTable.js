import React from 'react';
import { Button, Table } from 'hzero-ui';
import { isFunction, map, omit } from 'lodash';
import Upload from 'components/Upload';
import intl from 'utils/intl';
import { getWidthFromWord } from '../utils';
import { getComponentProps } from '../ComposeForm/utils';

/**
 * 获取计算得来的属性(且耗时多)
 * @param {Object} props
 */
function getNoEditableComputeTableProps(props) {
  const { fields, context } = props;
  // let index = 0;
  let columnsWidth = 0;
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
      dataIndex:
        field.componentType === 'ValueList' || field.componentType === 'Lov'
          ? `${field.fieldCode}Meaning`
          : field.fieldCode,
      title: field.fieldDescription,
      width: columnWidth,
    };
    if (field.componentType === 'Checkbox' || field.componentType === 'Switch') {
      column.render = item =>
        item === 1
          ? intl.get('hzero.common.status.yes').d('是')
          : intl.get('hzero.common.status.no').d('否');
    } else if (field.componentType === 'Upload') {
      const uploadProps = getComponentProps({
        field,
        componentType: 'Upload',
        context,
      });
      column.render = item => item && <Upload {...uploadProps} viewOnly attachmentUUID={item} />;
    }
    return column;
  });
  return {
    scroll: { x: columnsWidth },
    columns,
  };
}

function getNoComputeTableProps(props) {
  const { rowKey = 'id', dataSource = [] } = props;
  return {
    rowKey,
    dataSource,
  };
}
const omitProps = [
  'addable',
  'removable',
  'editable',
  'fields',
  'onRef',
  'onGetValidateDataSourceHook',
  'fieldLabelWidth',
  'onRowEdit',
  'onAdd',
  'onRemove',
  'onRowSelectionChange',
];

export default class BaseComposeTable extends React.PureComponent {
  state = {
    prevState: {},
    computeTableProps: {},
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const nextState = {
      prevState: prevState.prevState,
      computeTableProps: prevState.computeTableProps,
    };
    const {
      fields,
      editable,
      addable,
      removable,
      getComputeTableProps = getNoEditableComputeTableProps,
    } = nextProps;
    const {
      fields: prevFields,
      editable: prevEditable,
      addable: prevAddable,
      removable: prevRemovable,
    } = prevState.prevState;
    if (
      fields !== prevFields ||
      addable !== prevAddable ||
      editable !== prevEditable ||
      removable !== prevRemovable
    ) {
      nextState.computeTableProps = getComputeTableProps(nextProps);
      nextState.prevState.fields = fields;
      nextState.prevState.editable = editable;
      nextState.prevState.addable = addable;
      nextState.prevState.removable = removable;
    }
    nextState.noComputeTableProps = getNoComputeTableProps(nextProps);
    return nextState;
  }

  constructor(props) {
    super(props);
    this.handleBtnAddClick = this.handleBtnAddClick.bind(this);
    this.handleBtnRemoveClick = this.handleBtnRemoveClick.bind(this);
  }

  componentDidMount() {
    const { onRef } = this.props;
    if (isFunction(onRef)) {
      onRef(this);
    }
  }

  handleBtnAddClick() {
    const { onAdd } = this.props;
    if (isFunction(onAdd)) {
      onAdd();
    }
  }

  handleBtnRemoveClick() {
    const { onRemove } = this.props;
    if (isFunction(onRemove)) {
      onRemove();
    }
  }

  render() {
    const { computeTableProps = {}, noComputeTableProps = {} } = this.state;
    const { removable, addable, onRowSelectionChange, selectedRowKeys = [] } = this.props;
    const otherProps = omit(this.props, omitProps);
    let rowSelection = null;
    const buttons = [];
    if (addable) {
      buttons.push(
        <Button key="add" onClick={this.handleBtnAddClick}>
          {intl.get('hzero.common.button.add').d('新增')}
        </Button>
      );
    }
    if (removable) {
      buttons.push(
        <Button
          key="remove"
          onClick={this.handleBtnRemoveClick}
          disabled={selectedRowKeys.length === 0}
        >
          {intl.get('hzero.common.button.delete').d('删除')}
        </Button>
      );
      rowSelection = {
        selectedRowKeys,
        onChange: onRowSelectionChange,
      };
    }
    return (
      <React.Fragment key="base-compose-table">
        {buttons.length > 0 && (
          <div key="base-compose-table-options" className="table-list-operator">
            {buttons}
          </div>
        )}
        <Table
          bordered
          pagination={false}
          key="base-compose-table-table"
          {...otherProps}
          {...noComputeTableProps}
          {...computeTableProps}
          rowSelection={rowSelection}
        />
      </React.Fragment>
    );
  }
}
