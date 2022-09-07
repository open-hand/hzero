import React from 'react';
import { withRouter } from 'react-router';
import { Bind } from 'lodash-decorators';
import { Form, TextField, Switch, IntlField, Spin } from 'choerodon-ui/pro';

@withRouter
export default class Drawer extends React.PureComponent {
  async componentDidMount() {
    const {
      isEdit,
      detailDs,
      isChild,
      currentEditData,
      currentLevelPath = '',
      deviceId,
      currentName = '',
    } = this.props;
    if (isEdit) {
      await this.queryData();
    }
    if (currentEditData) {
      detailDs.current.set('parentName', currentEditData.name);
    }
    if (isEdit) {
      detailDs.current.set('parentName', currentEditData.parentName);
    } else if (isChild) {
      detailDs.current.set('parentId', deviceId);
      detailDs.current.set('levelPath', currentLevelPath);
    }
    detailDs.current.set('parentName', currentName);
  }

  @Bind()
  async queryData() {
    const {
      currentEditData: { thingGroupId },
      detailDs,
    } = this.props;
    detailDs.projectId = thingGroupId;
    await detailDs.query().then((res) => {
      if (res) {
        this.setState({
          // isSpin: false,
        });
      }
    });
  }

  @Bind()
  onChange(value) {
    const { detailDs, currentLevelPath = '' } = this.props;
    if (value) {
      detailDs.current.set(
        'levelPath',
        `${currentLevelPath}${currentLevelPath ? '/' : ''}${value}`
      );
    } else {
      detailDs.current.set('levelPath', currentLevelPath);
    }
  }

  render() {
    const { detailDs, isEdit, isChild = false } = this.props;
    return (
      <>
        <Spin dataSet={detailDs}>
          <Form dataSet={detailDs}>
            <IntlField name="name" />
            <TextField name="code" disabled={isEdit} onChange={this.onChange} />\
            {isChild && <TextField name="parentName" disabled />}
            <TextField name="levelPath" disabled />
            <TextField name="description" />
            <Switch name="enabledFlag" />
          </Form>
        </Spin>
      </>
    );
  }
}
