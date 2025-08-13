import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { valueMapMeaning, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { Button as ButtonPermission } from 'components/Permission';

/**
 * 模板明细行数据列表
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
export default class TemplateLineTable extends PureComponent {
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.onEdit(record);
  }

  @Bind()
  handleWordEditClick(record) {
    const { onRowWordEdit } = this.props;
    onRowWordEdit(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      dataSource = [],
      templateLineRowSelection,
      supportLanguage,
      linePagination,
      onChange,
      templateTypeCodeValue,
    } = this.props;
    const columns = [
      {
        title: intl.get('entity.lang.tag').d('语言'),
        dataIndex: 'lang',
        width: 200,
        render: val => valueMapMeaning(supportLanguage, val),
      },
      templateTypeCodeValue === 'html'
        ? {}
        : {
            title: intl.get('hrpt.templateManage.model.templateManage.fileName').d('模板文件名'),
            dataIndex: 'templateFileName',
          },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        width: 160,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                onClick={() => {
                  this.editOption(record);
                }}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          if (
            record.templateUrl &&
            (record.templateUrl.endsWith('.doc') || record.templateUrl.endsWith('docx'))
          ) {
            operators.push({
              key: 'word-edit',
              ele: (
                <ButtonPermission
                  type="text"
                  onClick={() => {
                    this.handleWordEditClick(record);
                  }}
                >
                  {intl.get('hrpt.templateManage.model.templateManage.templeEdit').d('模板编辑')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hrpt.templateManage.model.templateManage.templeEdit').d('模板编辑'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="templateDtlId"
        rowSelection={templateLineRowSelection}
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={linePagination}
        onChange={page => onChange(page)}
      />
    );
  }
}
