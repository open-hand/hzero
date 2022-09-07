import React from 'react';
import { withRouter } from 'react-router';
import { Bind } from 'lodash-decorators';
import { Form, TextField, Spin, TextArea } from 'choerodon-ui/pro';

@withRouter
export default class Drawer extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      isSpin: false,
    };
  }

  async componentDidMount() {
    this.setState({
      isSpin: true,
    });
    await this.queryData();
  }

  @Bind()
  async queryData() {
    const {
      currentEditData: { eventId },
      detailDs,
    } = this.props;
    detailDs.eventId = eventId;
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
            <TextField name="apiVersion" disabled />
            <TextField name="serviceName" disabled />
            <TextField name="code" disabled />
            <TextField name="name" disabled />
            <TextField name="source" disabled />
            <TextField name="time" disabled />
            <TextField name="requestNumber" disabled />
            <TextArea name="requestParameters" disabled />
            <TextArea name="responseElements" disabled />
            <TextField name="errorCode" disabled />
            <TextField name="errorMessage" disabled />
            <TextField name="referencedResources" disabled />
          </Form>
        </Spin>
      </>
    );
  }
}
