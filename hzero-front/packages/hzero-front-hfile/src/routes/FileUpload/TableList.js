import React, { PureComponent } from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

export default class TableList extends PureComponent {
  /**
   * 编辑
   *
   * @param {*} record
   * @memberof ListTable
   */
  @Bind()
  editModal(record) {
    const { onGetRecordData } = this.props;
    onGetRecordData(record);
  }

  /**
   * 删除
   *
   * @param {*} record
   * @memberof ListTable
   */
  @Bind()
  deleteData(record) {
    const { onDelete } = this.props;
    onDelete(record);
  }

  render() {
    const {
      listConfig = {},
      loading,
      pagination = {},
      onChangePage,
      match: { path },
    } = this.props;
    const columns = [
      {
        title: intl.get('hfile.fileUpload.model.fileUpload.bucketName').d('分组'),
        dataIndex: 'bucketName',
        width: 200,
      },
      {
        title: intl.get('hfile.fileUpload.model.fileUpload.directory').d('上传目录'),
        dataIndex: 'directory',
        width: 200,
      },
      {
        title: intl.get('hfile.fileUpload.model.fileUpload.contentType').d('文件分类'),
        dataIndex: 'contentTypeMeaning',
        width: 200,
      },
      {
        title: intl.get('hfile.fileUpload.model.fileUpload.fileFormat').d('文件格式'),
        dataIndex: 'fileFormat',
      },
      {
        title: intl.get('hfile.fileUpload.model.fileUpload.storageSize').d('文件大小限制'),
        dataIndex: 'storageSize',
        width: 150,
      },
      {
        title: intl.get('hfile.fileUpload.model.fileUpload.storageUnit').d('文件大小单位'),
        dataIndex: 'storageUnitMeaning',
        width: 150,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'option',
        fixed: 'right',
        width: 120,
        render: (_, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '文件上传配置-编辑',
                    },
                  ]}
                  onClick={() => this.editModal(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.deleteData(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: '文件上传配置-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];
    return (
      <Table
        bordered
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        rowKey="uploadConfigId"
        dataSource={listConfig.content || []}
        pagination={pagination}
        loading={loading}
        onChange={page => onChangePage(page)}
      />
    );
  }
}
