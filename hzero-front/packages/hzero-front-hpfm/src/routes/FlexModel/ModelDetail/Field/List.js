import React, { Component } from 'react';
import { Table, Dropdown, Menu, Popconfirm, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isNil from 'lodash/isNil';

import intl from 'utils/intl';

import styles from '../../style/index.less';

export default class List extends Component {
  @Bind()
  renderFieldName(fieldName, record = {}) {
    const { fieldCode = '', fieldCategory } = record;
    const { rowKey, handleEditField, handleRemoveField, primaryKey } = this.props;
    const menu = (
      <Menu>
        <Menu.Item key="edit" onClick={() => handleEditField(record[rowKey])}>
          <Icon type="edit" />
          {intl.get('hzero.common.button.edit').d('编辑')}
        </Menu.Item>
        {fieldCategory === 'VIR' && (
          <Menu.Item key="remove">
            <Popconfirm
              title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
              onConfirm={() => handleRemoveField(record[rowKey])}
              okText={intl.get('hzero.common.status.yes').d('是')}
              cancelText={intl.get('hzero.common.status.no').d('否')}
            >
              <Icon type="delete" style={{ marginRight: 8 }} />
              {intl.get('hzero.common.button.delete').d('删除')}
            </Popconfirm>
          </Menu.Item>
        )}
      </Menu>
    );

    return (
      <div className={styles['model-table-extra']}>
        <div className={styles['model-operator']}>
          <Dropdown overlay={menu} trigger={['click']}>
            <a>.&nbsp;.&nbsp;.</a>
          </Dropdown>
        </div>
        <div style={{ fontWeight: 600, color: '#666' }}>
          {fieldName}
          {fieldCode === primaryKey && (
            <Icon type="key" style={{ marginLeft: '8px', color: '#efc706' }} />
          )}
        </div>
        <div style={{ color: '#a5a5a5' }}>{fieldCode}</div>
      </div>
    );
  }

  @Bind()
  getColumns() {
    return [
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldName').d('字段名称'),
        dataIndex: 'fieldName',
        render: this.renderFieldName,
        width: 300,
      },
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldCategory').d('字段类型'),
        dataIndex: 'fieldCategoryMeaning',
        width: 80,
      },
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldType').d('数据类型'),
        dataIndex: 'fieldTypeMeaning',
        width: 60,
      },
      {
        title: intl
          .get('hpfm.flexModelDetail.model.flexModelDetail.modelFieldWidget')
          .d('默认组件'),
        dataIndex: 'fieldWidgetMeaning',
        width: 60,
      },
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.valueCode').d('值集编码'),
        dataIndex: 'modelFieldWidget.sourceCode',
        width: 130,
      },
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.notNull').d('非空'),
        dataIndex: 'notNull',
        width: 50,
        align: 'center',
        render: (text) => (!isNil(text) && text === 1 ? <Icon type="check" /> : null),
      },
      {
        title: intl.get('hpfm.flexModelDetail.model.flexModelDetail.MultileField').d('多语言字段'),
        dataIndex: 'fieldMultiLang',
        width: 50,
        align: 'center',
        render: (text) => (!isNil(text) && text === 1 ? <Icon type="check" /> : null),
      },
    ];
  }

  render() {
    const { dataSource, loading, rowKey } = this.props;

    return (
      <Table
        bordered
        rowKey={rowKey}
        columns={this.getColumns()}
        loading={loading}
        dataSource={dataSource}
        pagination={false}
      />
    );
  }
}
