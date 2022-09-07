import React, { PureComponent } from 'react';
import { Popconfirm, Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { enableRender, yesOrNoRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 审批规则数据列表
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
export default class ListTable extends PureComponent {
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.editContent(record);
  }

  /**
   * 删除
   * @param {object} record - 数据对象
   */
  deleteOption(record) {
    this.props.deleteContent(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource = [], path } = this.props;
    const columns = [
      {
        title: intl.get('hsdr.concurrent.model.concurrent.orderSeq').d('排序号'),
        dataIndex: 'orderSeq',
        width: 100,
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.paramCode').d('参数名称'),
        dataIndex: 'paramCode',
        width: 150,
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.paramName').d('参数描述'),
        dataIndex: 'paramName',
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.paramFormatCode').d('参数格式'),
        dataIndex: 'paramFormatMeaning',
        width: 150,
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.paramEditTypeCode').d('编辑类型'),
        dataIndex: 'paramEditTypeMeaning',
        width: 150,
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.defaultValue').d('默认值'),
        dataIndex: 'defaultValue',
        width: 120,
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.notnullFlag').d('是否必须'),
        dataIndex: 'notnullFlag',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hsdr.concurrent.model.concurrent.showFlag').d('是否展示'),
        dataIndex: 'showFlag',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        fixed: 'right',
        width: 110,
        render: (_, record) => (
          <span className="action-link">
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.edit`,
                  type: 'button',
                  meaning: '请求定义详情-编辑',
                },
              ]}
              onClick={() => this.editOption(record)}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
            <Popconfirm
              placement="topRight"
              title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
              onConfirm={() => this.deleteOption(record)}
            >
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.delete`,
                    type: 'button',
                    meaning: '请求定义详情-删除',
                  },
                ]}
                style={{ cursor: 'pointer' }}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
            </Popconfirm>
          </span>
        ),
      },
    ];
    return (
      <Table
        bordered
        rowKey="concParamId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={false}
      />
    );
  }
}
