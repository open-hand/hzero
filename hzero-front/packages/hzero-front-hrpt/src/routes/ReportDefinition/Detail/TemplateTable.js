import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Checkbox from 'components/Checkbox';

import intl from 'utils/intl';
import { valueMapMeaning } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 数据列表
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
export default class TemplateTable extends PureComponent {
  /**
   * 改变默认模板
   */
  @Bind()
  changeDefaultTemplate(record) {
    const { onChangeDefaultTemplate } = this.props;
    onChangeDefaultTemplate(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      tenantRoleLevel,
      templateRowSelection,
      templateTypeCode = [],
      pagination = {},
      dataSource = [],
    } = this.props;
    const columns = [
      !tenantRoleLevel && {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('entity.template.code').d('模板代码'),
        dataIndex: 'templateCode',
        width: 150,
      },
      {
        title: intl.get('entity.template.name').d('模板名称'),
        dataIndex: 'templateName',
      },
      {
        title: intl.get('entity.template.type').d('模板类型'),
        dataIndex: 'templateTypeCode',
        width: 100,
        render: val => valueMapMeaning(templateTypeCode, val),
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.defaultFlag').d('默认'),
        dataIndex: 'defaultFlag',
        width: 100,
        render: (val, record) => (
          <Checkbox
            checked={val}
            onChange={
              val !== 1
                ? () => {
                    this.changeDefaultTemplate(record);
                  }
                : undefined
            }
          />
        ),
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        rowKey="reportTemplateId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        rowSelection={templateRowSelection}
      />
    );
  }
}
