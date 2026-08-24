import React from 'react';
import { withRouter } from 'react-router';
import {
  Form,
  TextField,
  Switch,
  Select,
  Lov,
  Password,
  Spin,
  DataSet,
  IntlField,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';

import { detailDs } from '../../stores/CallServerDS';

let newDetailDs = new DataSet(detailDs());
@withRouter
export default class Drawer extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      isSpin: false,
      dataSource: {},
      otherFields: [],
    };
  }

  async componentDidMount() {
    const { isCopy, currentEditData, otherFields, isEdit } = this.props;
    newDetailDs.create({});
    // 新建的时候也要传给父组件
    this.props.onTest(newDetailDs);
    if (isCopy) {
      newDetailDs.get(0).set('serverCode', currentEditData.serverCode);
      newDetailDs.get(0).set('serverName', currentEditData.serverName);
      newDetailDs.get(0).set('serverTypeCode', currentEditData.serverTypeCode);
      newDetailDs.get(0).set('serverId', currentEditData.serverId);
      newDetailDs.get(0).set('enabledFlag', currentEditData.enabledFlag);
      // 复制的时候传给父组件
      this.props.formConfigDs.setQueryParameter(
        'formCode',
        `HMSG.CALL.${currentEditData.serverTypeCode}`
      );
      this.props.formConfigDs.query().then((res) => {
        const newFields = res.map((item) => ({
          name: item.itemCode,
          label: item.itemName,
          type: 'string',
          required: item.requiredFlag === 1,
        }));
        const fields = [...detailDs().fields, ...newFields];
        const init = newDetailDs.toData()[0];
        newDetailDs = new DataSet({
          ...detailDs(),
          fields,
        });
        newDetailDs.create(init);
        this.props.onTest(newDetailDs);
        this.setState({
          otherFields: res,
        });
      });
    }
    if (isEdit) {
      newDetailDs.setQueryParameter('serverId', currentEditData.serverId);
      await newDetailDs.query().then((res) => {
        if (res) {
          const ext = `${res.extParam}`;
          const dataSource = {
            ...res,
            ...JSON.parse(`${ext}`),
          };
          this.setState({
            dataSource,
            otherFields,
          });
        }
      });
      this.props.formConfigDs.setQueryParameter(
        'formCode',
        `HMSG.CALL.${currentEditData.serverTypeCode}`
      );
      this.props.formConfigDs.query().then((res) => {
        const newFields = res.map((item) => ({
          name: item.itemCode,
          label: item.itemName,
          type: 'string',
          required: item.requiredFlag === 1,
        }));
        const fields = [...detailDs().fields, ...newFields];
        newDetailDs = new DataSet({
          ...detailDs(),
          fields,
          data: [
            {
              ...this.state.dataSource,
            },
          ],
        });
        this.props.onTest(newDetailDs);
        this.setState({
          otherFields: res,
        });
      });
    }
  }

  @Bind()
  async handelChange(record) {
    const { isEdit } = this.props;
    this.props.formConfigDs.setQueryParameter('formCode', `HMSG.CALL.${record}`);
    this.props.formConfigDs.query().then((res) => {
      const newFields = res.map((item) => ({
        name: item.itemCode,
        label: item.itemName,
        type: 'string',
        required: item.requiredFlag === 1,
      }));
      const fields = [...detailDs().fields, ...newFields];
      const init = newDetailDs.toData()[0];
      newDetailDs = new DataSet({
        ...detailDs(),
        fields,
        // data: [
        //   {
        //     ...newDetailDs.toData()[0],
        //   },
        // ],
      });
      if (isEdit) {
        newDetailDs.loadData([init]);
      } else {
        newDetailDs.create(init);
      }
      this.props.onTest(newDetailDs);
      this.setState({
        otherFields: res,
      });
    });
  }

  render() {
    const { isEdit } = this.props;
    const { isSpin, otherFields } = this.state;
    return (
      <>
        <Spin spinning={isSpin}>
          <Form dataSet={newDetailDs} labelWidth={110}>
            {!isTenantRoleLevel() && <Lov name="tenantIdLov" disabled={isEdit} />}
            <TextField name="serverCode" disabled={isEdit} />
            <IntlField name="serverName" />
            <Select name="serverTypeCode" onChange={this.handelChange} />
            <TextField name="accessKey" />
            <Password
              name="accessSecret"
              placeholder={intl.get('hzero.common.validation.notChange').d('未更改')}
            />
            {otherFields.map((item) => (
              <TextField name={item.itemCode} />
            ))}
            <Switch name="enabledFlag" />
          </Form>
        </Spin>
      </>
    );
  }
}
