import React from 'react';
import { withRouter } from 'react-router';
import { Bind } from 'lodash-decorators';
import {
  Form,
  TextField,
  Switch,
  Select,
  Lov,
  Password,
  Spin,
  NumberField,
  IntlField,
} from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';

@withRouter
export default class Drawer extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      isSpin: false,
    };
  }

  async componentDidMount() {
    const { isEdit, isCopy, currentEditData, detailDs } = this.props;
    if (isEdit) {
      this.setState({
        isSpin: true,
      });
      await this.queryData();
    }
    if (isCopy) {
      detailDs.get(0).set('serverCode', currentEditData.serverCode);
      detailDs.get(0).set('serverName', currentEditData.serverName);
      detailDs.get(0).set('authType', currentEditData.authType);
      detailDs.get(0).set('enabledFlag', currentEditData.enabledFlag);
    }
  }

  @Bind()
  async queryData() {
    const {
      currentEditData: { serverId },
      detailDs,
    } = this.props;
    detailDs.serverId = serverId;
    await detailDs.query().then((res) => {
      if (res) {
        this.setState({
          isSpin: false,
        });
      }
    });
  }

  render() {
    const { isEdit, detailDs } = this.props;
    const { isSpin } = this.state;
    return (
      <>
        <Spin spinning={isSpin}>
          <Form dataSet={detailDs} labelWidth={110}>
            {!isTenantRoleLevel() && <Lov name="tenantIdLov" disabled={isEdit} />}
            <TextField name="serverCode" disabled={isEdit} />
            <IntlField name="serverName" />
            <Select name="authType" />
            <TextField name="appKey" />
            <NumberField name="agentId" step={1} min={0} />
            <Password
              name="appSecret"
              placeholder={intl.get('hzero.common.validation.notChange').d('未更改')}
              maxLength={110}
            />
            <TextField name="authAddress" />
            <Switch name="enabledFlag" />
          </Form>
        </Spin>
      </>
    );
  }
}
