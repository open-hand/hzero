import React from 'react';
import { Form, TextField, Switch } from 'choerodon-ui/pro';

export default class Drawer extends React.Component {
  render() {
    const { refreshDs = {} } = this.props;
    return (
      <Form dataSet={refreshDs}>
        <TextField name="serviceName" />
        <TextField name="metaVersion" />
        <Switch name="cleanPermission" />
      </Form>
    );
  }
}
