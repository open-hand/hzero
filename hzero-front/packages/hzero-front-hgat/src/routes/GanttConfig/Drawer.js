import React from 'react';
import { Form, TextField, TextArea, Switch, IntlField } from 'choerodon-ui/pro';

export default class Drawer extends React.Component {
  componentDidMount() {
    const { ds, id } = this.props;

    if (id) {
      ds.setQueryParameter('id', id);
      ds.query();
    }
  }

  render() {
    const { ds, isCreate } = this.props;
    return (
      <Form dataSet={ds}>
        <TextField name="ganttCode" maxLength={30} disabled={!isCreate} />
        <IntlField name="ganttName" maxLength={240} />
        <TextArea name="remark" />
        <Switch name="enabledFlag" />
      </Form>
    );
  }
}
