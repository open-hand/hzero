import React from 'react';
import { Lov, Form } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

export default class DelegateDrawer extends React.Component {
  @Bind()
  handleChange(record) {
    const { lovDs } = this.props;
    const csGroupUser = lovDs.getField('csGroupUser');
    if (record) {
      const { categoryId } = record;
      csGroupUser.setLovPara('categoryId', categoryId);
    } else {
      csGroupUser.setLovPara('categoryId', undefined);
    }
  }

  render() {
    const { lovDs } = this.props;
    return (
      <Form dataSet={lovDs}>
        <Lov name="csGroup" />
        <Lov name="csGroupUserTag" onChange={this.handleChange} />
        <Lov name="csGroupUser" />
      </Form>
    );
  }
}
