/**
 * deviceDrawer - 设备采集抽屉
 * @date 2020-3-23
 * @author 许碧婷 <qiang.wu01@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import React, { Component } from 'react';
import { Form, TextField, IntlField, Switch, Select } from 'choerodon-ui/pro';
import intl from 'utils/intl';

const modelPrompt = 'hiot.deviceCollection';

export default class FormDrawer extends Component {
  componentDidMount = () => {
    const { isNew, tagId, formDs } = this.props;
    if (!isNew) {
      formDs.setQueryParameter('tagId', tagId);

      formDs.query();
    }
  };

  render() {
    const { formDs } = this.props;

    return (
      <Form dataSet={formDs} columns={2}>
        <TextField name="parameter" />
        <TextField name="address" />
        <IntlField
          name="description"
          title={intl.get(`${modelPrompt}.model.device.gatewayDescription`).d('服务器描述')}
        />
        <TextField name="orderCode" />
        <Select name="dataType" />
        <Select name="clientAccess" />
        <Select name="frequency" />
        <TextField name="multiple" />
        <Switch name="triggerFlag" />
        <Switch name="recordChangesFlag" />
        <Switch name="publishedFlag" />
        <Switch name="enableFlag" />
      </Form>
    );
  }
}
