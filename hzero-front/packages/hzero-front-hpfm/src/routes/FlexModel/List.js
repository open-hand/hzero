import React, { Component } from 'react';
import { Table, Menu, Dropdown, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isNil from 'lodash/isNil';

import intl from 'utils/intl';
import styles from './style/index.less';

export default class List extends Component {
  @Bind()
  renderModelName(modelName, record = {}) {
    const { modelCode = '' } = record;
    const { rowKey, handleEditModel } = this.props;
    const menu = (
      <Menu>
        <Menu.Item key="edit" onClick={() => handleEditModel(record[rowKey])}>
          <Icon type="edit" />
          {intl.get('hzero.common.button.edit').d('编辑')}
        </Menu.Item>
      </Menu>
    );

    return (
      <div>
        <div className={styles['model-operator']}>
          <Dropdown overlay={menu} trigger={['click']}>
            <a>.&nbsp;.&nbsp;.</a>
          </Dropdown>
        </div>
        <div style={{ fontWeight: 600, color: '#666' }}>{modelName}</div>
        <div style={{ color: '#a5a5a5' }}>{modelCode}</div>
      </div>
    );
  }

  @Bind()
  getColumns() {
    return [
      {
        title: intl.get('hpfm.flexModel.model.flexModel.modelName').d('模型名称'),
        dataIndex: 'modelName',
        render: this.renderModelName,
        onCell: this.handleOnCell,
        width: 300,
      },
      {
        title: intl.get('hpfm.flexModel.model.flexModel.modelTable').d('模型表'),
        dataIndex: 'modelTable',
        width: 200,
      },
      {
        title: intl.get('hpfm.flexModel.model.flexModel.supportMultiLang').d('多语言'),
        dataIndex: 'supportMultiLang',
        width: 100,
        render: text => (!isNil(text) && text === 1 ? <Icon type="check" /> : null),
      },

      {
        title: intl.get('hpfm.flexModel.model.flexModel.serviceName').d('服务名称'),
        dataIndex: 'serviceName',
        width: 200,
      },
    ];
  }

  @Bind()
  handleChange(pagination) {
    const { handleFetchList } = this.props;
    handleFetchList({
      page: pagination,
    });
  }

  render() {
    const { rowKey, dataSource, pagination, queryListLoading } = this.props;

    return (
      <Table
        bordered
        rowKey={rowKey}
        columns={this.getColumns()}
        loading={queryListLoading}
        dataSource={dataSource}
        pagination={pagination}
        onChange={this.handleChange}
      />
    );
  }
}
