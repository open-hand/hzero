import React from 'react';
import { Form, Select, Output, IntlField } from 'choerodon-ui/pro';

import { yesOrNoRender } from 'utils/renderer';

export default class Drawer extends React.Component {
  render() {
    const { record = {}, labelList } = this.props;

    return (
      <Form record={record}>
        <IntlField name="description" />
        <Select name="pageTag" multiple>
          {labelList.map((n) => (
            <Select.Option key={n.name} value={n.name} disabled={n.type === 'A'} on>
              {n.name}
            </Select.Option>
          ))}
        </Select>
        {/* <Select name="backgroundTag" disabled /> */}
        <Output name="code" />
        <Output name="path" />
        <Output name="methodMeaning" />
        <Output name="levelMeaning" />
        <Output name="serviceName" />
        <Output name="code" />
        <Output name="action" />
        <Output name="publicAccess" renderer={({ value }) => yesOrNoRender(value)} />
        <Output name="loginAccess" renderer={({ value }) => yesOrNoRender(value)} />
        <Output name="within" renderer={({ value }) => yesOrNoRender(value)} />
        <Output name="signAccess" renderer={({ value }) => yesOrNoRender(value)} />
      </Form>
    );
  }
}
