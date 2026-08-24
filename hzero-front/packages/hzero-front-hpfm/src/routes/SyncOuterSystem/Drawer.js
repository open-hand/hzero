import React from 'react';
import { withRouter } from 'react-router';
import { Bind } from 'lodash-decorators';
import { Form, TextField, Switch, Select, Spin, Password } from 'choerodon-ui/pro';

import intl from 'utils/intl';

@withRouter
export default class Drawer extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isSpin: false,
    };
  }

  async componentDidMount() {
    const { isCreate } = this.props;
    if (!isCreate) {
      this.setState({
        isSpin: true,
      });
      await this.queryData();
    }
  }

  /**
   * 获取初始数据
   */
  @Bind()
  async queryData() {
    const {
      currentEditData: { syncId },
      tableDetailDs,
    } = this.props;
    tableDetailDs.syncId = syncId;
    await tableDetailDs.query().then(res => {
      if (res) {
        this.setState({
          isSpin: false,
        });
      }
    });
  }

  render() {
    const { isCreate, tableDetailDs, customizeForm } = this.props;
    const { isSpin } = this.state;
    return (
      <React.Fragment>
        <Spin spinning={isSpin}>
          {customizeForm(
            { code: 'HPFM.SYNC_OUTER_SYSTEM.EDIT_FORM' },
            <Form dataSet={tableDetailDs}>
              <TextField name="appId" />
              <Select name="syncTypeCode" disabled={!isCreate} />
              <Password
                name="appSecret"
                placeholder={intl.get('hpfm.syncOuterSystem.view.message.unChange').d('未更改')}
                maxLength={110}
              />
              <Select name="authType" />
              <TextField name="authAddress" />
              <Switch name="enabledFlag" />
            </Form>
          )}
        </Spin>
      </React.Fragment>
    );
  }
}
