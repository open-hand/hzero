import React from 'react';
import { withRouter } from 'react-router';
import { Bind } from 'lodash-decorators';
import { Form, TextField, Switch, Spin, TextArea } from 'choerodon-ui/pro';

@withRouter
export default class Drawer extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      isSpin: false,
    };
  }

  async componentDidMount() {
    const { isEdit } = this.props;
    if (isEdit) {
      this.setState({
        isSpin: true,
      });
      await this.queryData();
    }
  }

  @Bind()
  async queryData() {
    const {
      currentEditData: { proxyId },
      detailDs,
    } = this.props;
    detailDs.proxyId = proxyId;
    await detailDs.query().then(res => {
      if (res) {
        this.setState({
          isSpin: false,
        });
      }
    });
  }

  render() {
    const { detailDs } = this.props;
    const { isSpin } = this.state;
    return (
      <>
        <Spin spinning={isSpin}>
          <Form dataSet={detailDs}>
            <TextField name="name" />
            <TextField name="host" />
            <TextField name="port" />
            <Switch name="enabledFlag" />
            <Switch name="sslEnabledFlag" />
            <TextField name="remark" />
            <TextArea name="trustedCaFile" />
            <TextArea name="certFile" />
            <TextArea name="keyFile" />
          </Form>
        </Spin>
      </>
    );
  }
}
