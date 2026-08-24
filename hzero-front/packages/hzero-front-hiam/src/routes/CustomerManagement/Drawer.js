import React from 'react';
import { Form, TextField, Switch, Lov } from 'choerodon-ui/pro';

export default class Drawer extends React.Component {
  render() {
    const { refreshDs = {} } = this.props;
    return (
      <Form dataSet={refreshDs}>
        <Lov name="serviceLov" />
        <TextField name="metaVersion" />
        <Switch name="cleanPermission" />
      </Form>
    );
  }
}
