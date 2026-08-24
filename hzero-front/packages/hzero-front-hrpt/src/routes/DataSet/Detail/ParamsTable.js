import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';
import { yesOrNoRender, valueMapMeaning, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 参数数据列表
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
export default class ParamsTable extends PureComponent {
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.onEdit(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      dataSource = [],
      dataSourceType = [],
      formElement = [],
      dataType = [],
      paramsRowSelection,
    } = this.props;
    const columns = [
      {
        title: intl.get('hrpt.common.view.serialNumber').d('序号'),
        dataIndex: 'ordinal',
        width: 60,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.paramsName').d('参数名'),
        dataIndex: 'name',
        width: 150,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.text').d('标题'),
        dataIndex: 'text',
        width: 150,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.defaultValue').d('默认值'),
        dataIndex: 'defaultValue',
        width: 120,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.defaultText').d('默认值显示'),
        dataIndex: 'defaultText',
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.formElement').d('表单控件'),
        dataIndex: 'formElement',
        width: 100,
        render: (val) => valueMapMeaning(formElement, val),
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.dataSource').d('来源类型'),
        dataIndex: 'dataSource',
        width: 100,
        render: (val) => valueMapMeaning(dataSourceType, val),
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.dataType').d('数据类型'),
        dataIndex: 'dataType',
        width: 100,
        render: (val) => valueMapMeaning(dataType, val),
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.width').d('宽度'),
        dataIndex: 'width',
        width: 100,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.height').d('高度'),
        dataIndex: 'height',
        width: 100,
      },
      {
        title: intl.get('hrpt.reportDataSet.model.reportDataSet.isRequired').d('是否必输'),
        dataIndex: 'isRequired',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        width: 100,
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
      <Table
        bordered
        rowKey="ordinal"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={false}
        rowSelection={paramsRowSelection}
      />
    );
  }
}
