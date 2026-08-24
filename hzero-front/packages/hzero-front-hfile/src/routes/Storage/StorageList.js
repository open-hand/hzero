import React from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { yesOrNoRender, operatorRender } from 'utils/renderer';

export default class StorageList extends React.Component {
  @Bind()
  handleEdit(record) {
    const { onEdit = e => e } = this.props;
    onEdit(record);
  }

  @Bind()
  handleDelete(record) {
    const { onDelete = e => e } = this.props;
    onDelete(record);
  }

  render() {
    const {
      type = '1',
      path,
      loading = false,
      storageDataList = [],
      serverProviderList = [],
    } = this.props;
    const columns = [
      {
        title: intl.get('hfile.storage.model.storage.storageCode').d('存储编码'),
        dataIndex: 'storageCode',
        width: 150,
      },
      {
        title: intl.get('hfile.storage.model.storage.prefixStrategy').d('文件名前缀策略'),
        dataIndex: 'prefixStrategyMeaning',
        width: 150,
      },
      type !== '11' && {
        title: ['2', '3', '4', '6'].includes(type)
          ? intl.get('hfile.storage.model.storage.Domain2').d('代理地址')
          : intl.get('hfile.storage.model.storage.Domain').d('域名(Domain)'),
        dataIndex: 'domain',
        width: 200,
      },
      {
        title:
          type === '6'
            ? intl.get('hfile.storage.model.storage.storageRoute').d('存储路径')
            : type === '11'
            ? intl.get('hfile.storage.model.storage.endPointMeaning').d('区域')
            : intl.get('hfile.storage.model.storage.EndPoint').d('EndPoint'),
        dataIndex: type === '11' ? 'endPointMeaning' : 'endPoint',
      },
      type !== '6' && {
        title: intl.get('hfile.storage.model.storage.AccessKeyId').d('AccessKeyId'),
        dataIndex: 'accessKeyId',
      },
      type === '4' && {
        title: intl.get('hfile.storage.model.storage.AppId').d('AppId'),
        dataIndex: '',
        width: 200,
      },
      ['2', '4', '7', '8', '12'].includes(type) && {
        title: intl.get('hfile.storage.model.storage.region').d('Bucket所属地区'),
        dataIndex: 'region',
        width: 200,
      },
      type !== '6' && {
        title: intl.get('hfile.storage.model.storage.accessControl').d('bucket权限控制'),
        dataIndex: 'accessControl',
        width: 200,
        render: value => {
          let types = {};
          serverProviderList.forEach(item => {
            if (item.value === type) {
              if (item.children) {
                types = item.children.find(children => value === children.value) || {};
              }
            }
          });
          return types.meaning;
        },
      },
      type !== '6' && {
        title: intl.get('hfile.storage.model.storage.prefix').d('bucket前缀'),
        dataIndex: 'bucketPrefix',
        width: 200,
      },
      {
        title: intl.get('hfile.storage.model.storage.defaultFlag').d('默认'),
        dataIndex: 'defaultFlag',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'action',
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
                      meaning: '文件存储配置-编辑',
                    },
                  ]}
                  onClick={() => this.handleEdit(record)}
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
                  onConfirm={() => this.handleDelete(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: '文件存储配置-删除',
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
    ].filter(Boolean);
    return (
      <Table
        bordered
        scroll={{ x: tableScrollWidth(columns, 200) }}
        rowKey="storageConfigId"
        columns={columns}
        loading={loading}
        dataSource={storageDataList}
        pagination={false}
      />
    );
  }
}
