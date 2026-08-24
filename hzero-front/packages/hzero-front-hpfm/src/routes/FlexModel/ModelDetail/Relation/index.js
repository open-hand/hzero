import React, { Component } from 'react';
import { Table, Dropdown, Menu, Popconfirm, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';

import intl from 'utils/intl';
import styles from '../../style/index.less';

const defaultRowKey = 'id';

@connect(({ loading = {} }) => ({
  queryModelRelationListLoading: loading.effects['flexModel/queryModelRelationList'],
}))
export default class Relation extends Component {
  @Bind()
  renderRelationCell(slaveModelName, record = {}) {
    const { slaveModelCode } = record;
    const { handleEdit, handleRemoveRelation } = this.props;
    const menu = (
      <Menu>
        <Menu.Item key="edit" onClick={() => handleEdit(record[defaultRowKey])}>
          <Icon type="eye-o" />
          {intl.get('hzero.common.button.view').d('查看')}
        </Menu.Item>
        <Menu.Item key="remove">
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
            onConfirm={() => handleRemoveRelation(record[defaultRowKey])}
            okText={intl.get('hzero.common.status.yes').d('是')}
            cancelText={intl.get('hzero.common.status.no').d('否')}
          >
            <Icon type="delete" style={{ marginRight: 8 }} />
            {intl.get('hzero.common.button.delete').d('删除')}
          </Popconfirm>
        </Menu.Item>
      </Menu>
    );

    return (
      <div className={styles['table-extra']}>
        <div className={styles['model-operator']}>
          <Dropdown overlay={menu} trigger={['click']}>
            <a>.&nbsp;.&nbsp;.</a>
          </Dropdown>
        </div>
        <div style={{ fontWeight: 600, color: '#666' }}>{slaveModelName}</div>
        <div style={{ color: '#a5a5a5' }}>{slaveModelCode}</div>
      </div>
    );
  }

  @Bind()
  getColumns() {
    return [
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.relationModel').d('相关模型'),
        dataIndex: 'slaveModelName',
        width: 300,
        render: this.renderRelationCell,
      },
      {
        title: intl
          .get('hpfm.flexModelDetail.model.flexModelDetail.relateModelTable')
          .d('关联模型表'),
        dataIndex: 'slaveTableName',
        width: 200,
      },
      {
        title: intl
          .get('hpfm.flexModelDetail.model.flexModelDetail.relateModelField')
          .d('关联字段'),
        dataIndex: 'slaveFieldName',
        width: 200,
        render: (val, record) => (
          <React.Fragment>
            <div style={{ fontWeight: 600, color: '#666' }}>{val}</div>
            <div style={{ color: '#a5a5a5' }}>{record.slaveFieldCode}</div>
          </React.Fragment>
        ),
      },
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.relation').d('关系类型'),
        dataIndex: 'fieldNameMeaning',
        width: 100,
      },
    ];
  }

  render() {
    const { queryModelRelationListLoading, dataSource } = this.props;
    return (
      <Table
        bordered
        rowKey={defaultRowKey}
        loading={queryModelRelationListLoading}
        columns={this.getColumns()}
        dataSource={dataSource}
        pagination={false}
      />
    );
  }
}
