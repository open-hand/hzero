import React from 'react';
import { Form, TextField, Table, DataSet, Switch } from 'choerodon-ui/pro';

import intl from 'utils/intl';

import { CustomerDS } from '../../stores/innerCustomerGroupDS';

export default class Drawer extends React.Component {
  customerDS = new DataSet(CustomerDS());

  innerGroupDS = this.props.innerGroupDS;

  async componentDidMount() {
    const { id } = this.props;
    if (id !== undefined) {
      this.innerGroupDS.setQueryParameter('innerCsGroupId', id);
      this.customerDS.setQueryParameter('innerCsGroupId', id);
      await this.customerDS.query();
      await this.innerGroupDS.query();
    }
  }

  get columns() {
    return [
      {
        name: 'userLov',
        editor: true,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        command: () => [['delete', { color: 'red' }]],
        align: 'left',
      },
    ];
  }

  get buttons() {
    const { id } = this.props;
    this.customerDS.setQueryParameter('innerCsGroupId', id);
    return [['add', { disabled: !id }]];
  }

  render() {
    const { id } = this.props;
    return (
      <>
        <Form dataSet={this.innerGroupDS}>
          <TextField name="groupKey" disabled={id !== undefined} />
          <TextField name="groupName" />
          <TextField name="description" />
          <Switch name="enabledFlag" />
        </Form>
        <Table
          dataSet={this.customerDS}
          columns={this.columns}
          buttons={this.buttons}
          editMode="inline"
        />
      </>
    );
  }
}
