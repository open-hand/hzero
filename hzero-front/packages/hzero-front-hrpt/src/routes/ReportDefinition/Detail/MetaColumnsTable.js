import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { isEqual, filter } from 'lodash';
import { Bind } from 'lodash-decorators';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';

import intl from 'utils/intl';
import { valueMapMeaning, yesOrNoRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

import BodyRow from './BodyRow';

const rowSource = {
  beginDrag(props) {
    return {
      index: props.index,
    };
  },
};

const rowTarget = {
  drop(props, monitor) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }

    // Time to actually perform the action
    props.moveRow(dragIndex, hoverIndex);

    // Note: we're mutating the monitor item here!
    // Generally it's better to avoid mutations,
    // but it's good here for the sake of performance
    // to avoid expensive index searches.
    //  eslint-disable-next-line
    monitor.getItem().index = hoverIndex;
  },
};

const DragableBodyRow = DropTarget('row', rowTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
  sourceClientOffset: monitor.getSourceClientOffset(),
}))(
  DragSource('row', rowSource, (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    dragRow: monitor.getItem(),
    clientOffset: monitor.getClientOffset(),
    initialClientOffset: monitor.getInitialClientOffset(),
  }))(BodyRow)
);

/**
 * 列信息数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class MetaColumnsTable extends Component {
  state = {
    tableDataSource: [],
  };

  //  eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    if (isEqual(nextProps.dataSource, this.props.dataSource)) {
      this.setState({
        tableDataSource: nextProps.dataSource || [],
      });
    }
  }

  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.onEdit(record);
  }

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  @Bind()
  moveRow(dragIndex, hoverIndex) {
    const { tableDataSource } = this.state;
    const dragRow = tableDataSource[dragIndex];

    // 先删除拖动的那条数据，再在删除后的数组中的hoverIndex处插入拖动的那条数据
    const newAllData = filter(tableDataSource, (item, index) => index !== dragIndex);
    newAllData.splice(hoverIndex, 0, dragRow);

    this.setState({ tableDataSource: newAllData });
    for (let i = 0; i < newAllData.length; i++) {
      newAllData[i].ordinal = i + 1;
    }
    this.updateDataSource(newAllData);
  }

  @Bind()
  updateDataSource(tableDataSource) {
    //  eslint-disable-next-line
    const { metaColumns = [], ...otherValues } = this.props.header;
    this.props.dispatch({
      type: 'reportDefinition/updateState',
      payload: {
        header: { metaColumns: tableDataSource, ...otherValues },
      },
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource = [], metaColumnsRowSelection, type, sortType } = this.props;
    const columns = [
      {
        title: intl.get('hrpt.common.view.serialNumber').d('序号'),
        dataIndex: 'ordinal',
        width: 60,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.name').d('列名'),
        dataIndex: 'name',
        width: 120,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.text').d('标题'),
        dataIndex: 'text',
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.type').d('列类型'),
        dataIndex: 'type',
        width: 100,
        render: (val) => valueMapMeaning(type, val),
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.dataType').d('数据类型'),
        dataIndex: 'dataType',
        width: 100,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.format').d('格式掩码'),
        dataIndex: 'format',
        width: 100,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.width').d('宽度'),
        dataIndex: 'width',
        width: 60,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.decimals').d('精度'),
        dataIndex: 'decimals',
        width: 60,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.sortType').d('排序类型'),
        dataIndex: 'sortType',
        width: 100,
        render: (val) => valueMapMeaning(sortType, val),
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.configure').d('配置'),
        children: [
          {
            title: intl.get('hrpt.reportDefinition.model.reportDefinition.percent').d('百分比'),
            dataIndex: 'percent',
            width: 80,
            render: yesOrNoRender,
          },
          {
            title: intl.get('hrpt.reportDefinition.model.reportDefinition.hidden').d('隐藏'),
            dataIndex: 'hidden',
            width: 80,
            render: yesOrNoRender,
          },
        ],
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        width: 60,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <a onClick={() => this.editOption(record)}>
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators);
        },
      },
    ];
    return (
      <DndProvider backend={HTML5Backend}>
        <Table
          bordered
          rowKey="ordinal"
          rowSelection={metaColumnsRowSelection}
          loading={loading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          components={this.components}
          onRow={(record, index) => ({
            index,
            moveRow: this.moveRow,
          })}
          pagination={false}
        />
      </DndProvider>
    );
  }
}
