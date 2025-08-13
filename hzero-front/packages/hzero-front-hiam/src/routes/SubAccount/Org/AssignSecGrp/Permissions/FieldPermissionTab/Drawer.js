/**
 * 安全组字段权限 - 侧滑
 * @date: 2019-11-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Modal, Table, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';

export default class FieldConfigDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      pagination: {},
    };
  }

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, currentInterface = {} } = this.props;
    const { id } = currentInterface;

    return visible && !isEmpty(id) && id !== (prevProps.currentInterface || {}).id;
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchFieldConigList();
    }
  }

  @Bind()
  handleFetchFieldConigList() {
    const { onFetchDetail = () => {}, currentInterface = {} } = this.props;
    const { id } = currentInterface;
    onFetchDetail(id).then(({ dataSource = [], pagination = {} }) => {
      this.setState({
        dataSource,
        pagination,
      });
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel = () => {} } = this.props;
    onCancel();
    this.setState({
      dataSource: [],
      pagination: {},
    });
  }

  render() {
    const { visible = false, loading } = this.props;
    const { dataSource, pagination } = this.state;
    const columns = [
      {
        title: intl.get('hiam.subAccount.model.fieldPermission.fieldName').d('字段名称'),
        dataIndex: 'fieldName',
      },
      {
        title: intl.get('hiam.subAccount.model.fieldPermission.fieldType').d('字段类型'),
        dataIndex: 'fieldTypeMeaning',
      },
      {
        title: intl.get('hiam.subAccount.model.fieldPermission.rule').d('权限规则'),
        dataIndex: 'permissionTypeMeaning',
      },
      {
        title: intl.get('hiam.subAccount.model.fieldPermission.desensitize').d('脱敏规则'),
        dataIndex: 'permissionRule',
      },
      {
        title: intl.get('hiam.securityGroup.model.securityGroup.remark').d('说明'),
        dataIndex: 'remark',
      },
    ];
    const tableProps = {
      dataSource,
      pagination,
      loading,
      columns,
      bordered: true,
    };
    return (
      <Modal
        width={800}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        title={intl.get('hiam.subAccount.view.title.fieldPermission').d('接口字段权限配置')}
        onCancel={this.handleCancel}
        footer={
          <Button type="primary" onClick={this.handleCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Table {...tableProps} />
      </Modal>
    );
  }
}
