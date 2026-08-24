/**
 * @since 2019-12-22
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Form, TextField, TextArea } from 'choerodon-ui/pro';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';

import { detailFormDS } from '@/stores/missPermissionRecordDS';

export default class MissPermissionRecord extends React.Component {
  constructor(props) {
    super(props);
    this.detailFormDS = new DataSet(detailFormDS());
  }

  componentDidMount() {
    const {
      match: { params },
    } = this.props;
    const { permissionCheckId } = params;
    this.detailFormDS.setQueryParameter('permissionCheckId', permissionCheckId);
    this.detailFormDS.query();
  }

  render() {
    return (
      <>
        <Header
          title={intl.get('hiam.missPermission.view.title.detail').d('缺失权限详情')}
          backPath="/hiam/miss-permission-record/list"
        />
        <Content>
          <Form dataSet={this.detailFormDS} labelLayout="horizontal" columns={3} pristine>
            <TextField name="permissionCode" />
            <TextField name="serviceName" />
            <TextField name="apiPath" />
            <TextField name="checkState" />
            <TextField name="checkStateMeaning" />
            <TextField name="apiMethodMeaning" />
            <TextField name="permissionType" />
            <TextField name="creationDate" />
            <TextArea newLine colSpan={2} rows={3} name="routeDetails" resize="both" />
            <TextArea newLine colSpan={2} rows={7} name="permissionDetails" resize="both" />
            <TextArea newLine colSpan={2} rows={7} name="userDetails" resize="both" />
          </Form>
        </Content>
      </>
    );
  }
}
