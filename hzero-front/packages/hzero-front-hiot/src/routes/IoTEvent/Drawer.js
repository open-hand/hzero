import React from 'react';
import { Form, TextField, SelectBox } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    this.handleQuery();
  }

  /**
   * 查询详情
   * @param {string} value
   */
  @Bind()
  async handleQuery() {
    const { code, drawerDs } = this.props;
    drawerDs.setQueryParameter('alertCode', code);
    await drawerDs.query();
  }

  render() {
    const { drawerDs } = this.props;

    return (
      <Form dataSet={drawerDs} disabled>
        <TextField name="alertCode" />
        <TextField name="alertName" />
        <TextField name="ruleCode" />
        <TextField name="ruleName" />
        <TextField name="alertLevelMeaning" />
        <TextField name="sourceTypeMeaning" />
        <SelectBox name="targetTypeList" />
        <TextField name="remark" />
      </Form>
    );
  }
}
