import React, { PureComponent } from 'react';
import { Spin } from 'choerodon-ui';
import { Form, TextField, Select, UrlField, TextArea, Lov } from 'choerodon-ui/pro';
import { FRONTAL_MACHINE_STATUS } from '@/constants/constants';

export default class EditDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
    };
  }

  componentDidMount() {
    const { isNew } = this.props;
    if (!isNew) {
      this.handleFetchDetail();
    }
  }

  /**
   * 明细查询
   */
  async handleFetchDetail() {
    const { record, drawerFormDS } = this.props;
    this.setState({ detailLoading: true });
    drawerFormDS.setQueryParameter('frontalId', record.get('frontalId'));
    await drawerFormDS.query();
    this.setState({ detailLoading: false });
  }

  render() {
    const { tenantRoleLevel, isNew, drawerFormDS, record } = this.props;
    const { detailLoading } = this.state;
    const disableFlag = !isNew && !FRONTAL_MACHINE_STATUS.EDIT.includes(record.get('statusCode'));
    return (
      <Spin spinning={detailLoading}>
        <Form dataSet={drawerFormDS} columns={1}>
          <TextField name="frontalCode" restrict="a-zA-Z0-9-_./" disabled={!isNew} />
          <TextField name="frontalName" disabled={disableFlag} />
          <UrlField name="requestUrl" disabled={disableFlag} />
          <Select name="statusCode" disabled={disableFlag} />
          {!tenantRoleLevel && <Lov name="tenantLov" disabled={disableFlag} />}
          <Lov name="clientLov" disabled={disableFlag} />
          <TextArea name="remark" disabled={disableFlag} />
        </Form>
      </Spin>
    );
  }
}
