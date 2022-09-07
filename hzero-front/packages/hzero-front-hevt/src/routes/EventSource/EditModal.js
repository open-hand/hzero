/*
 * @Descripttion:  消费配置弹框
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-04-15 11:28:09
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import {
  Form,
  TextField,
  Switch,
  Select,
  Password,
  Modal,
  Table,
  DataSet,
  Button,
} from 'choerodon-ui/pro';
import { Divider } from 'choerodon-ui';
import { getResponse, encryptPwd } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { isEmpty } from 'lodash';
import { fetchConnectTest } from '@/services/eventSourceService';
import { eventSourceDS, EventHandleParamsDS } from '@/stores/EventSourceDS';

const modalKey = Modal.key();

class RenderForm extends Component {
  constructor(props) {
    super(props);
    const { eventSourceId, isCreate } = props;
    this.eventSourceDS = new DataSet({
      ...eventSourceDS(isCreate, eventSourceId),
      autoQuery: !isCreate,
      children: {
        eventConfigs: new DataSet({ ...EventHandleParamsDS(eventSourceId) }),
      },
    });
    props.onRef(this);
  }

  get columns() {
    return [
      {
        name: 'configCodeLov',
        editor: true,
      },
      {
        name: 'configValue',
        editor: true,
      },
    ];
  }

  render() {
    return (
      <>
        <Form dataSet={this.eventSourceDS} columns={1}>
          <TextField name="eventSourceCode" disabled={!this.props.isCreate} />
          <TextField name="eventSourceName" />
          <Select name="eventSourceType" disabled={!this.props.isCreate} />
          <TextField name="serviceAddress" />
          <TextField name="username" />
          <Password name="password" />
          <Switch name="enabledFlag" />
        </Form>
        <Divider orientation="left">
          <h3>{intl.get('hevt.eventSource.view.title.handleParams').d('数据源参数配置')}</h3>
        </Divider>
        <Table
          buttons={['add', 'delete']}
          dataSet={this.eventSourceDS.children.eventConfigs}
          queryBar="none"
          columns={this.columns}
        />
      </>
    );
  }
}

export default async function EditModalRender(isCreate, SourceDS, record, publicKey) {
  let renderClass;

  const handleConnectTest = async () => {
    const validate = await renderClass.eventSourceDS.validate();
    if (validate === false) {
      notification.error({
        message: intl.get('hevt.common.view.message.required').d('存在必输字段未填写'),
      });
    } else {
      const data = renderClass.eventSourceDS.toData()[0];
      const res = getResponse(await fetchConnectTest(data));
      if (res) {
        notification.success({
          message: intl.get('hevt.common.view.message.connect.success').d('连接成功'),
        });
      } else if (res === false) {
        notification.success({
          message: intl.get('hevt.common.view.message.connect.failure').d('连接失败'),
        });
      }
    }
  };

  Modal.open({
    title: isCreate
      ? intl.get('hevt.eventSource.view.title.eventSource.create').d('新建事件源')
      : intl.get('hevt.eventSource.view.title.eventSource.edit').d('编辑事件源'),
    drawer: true,
    style: {
      width: 600,
    },
    closable: true,
    key: modalKey,
    children: (
      <RenderForm
        eventSourceId={record?.get('eventSourceId')}
        isCreate={isCreate}
        onRef={(node) => {
          renderClass = node;
        }}
      />
    ),
    onCancel: () => renderClass.eventSourceDS.reset(),
    onClose: () => renderClass.eventSourceDS.reset(),
    footer: (okBtn, cancelBtn) => (
      <div>
        {okBtn}
        <Button color="primary" onClick={() => handleConnectTest()}>
          {intl.get('hevt.eventSource.view.button.testConnect').d('连接测试')}
        </Button>
        {cancelBtn}
      </div>
    ),
    onOk: async () => {
      const pwd = renderClass.eventSourceDS.current.get('password');
      if (pwd) {
        renderClass.eventSourceDS.current.set('password', encryptPwd(pwd, publicKey.publicKey));
      }
      const res = await renderClass.eventSourceDS.submit();
      if (!isEmpty(res) && res.failed && res.message) {
        return false;
      } else if (!isEmpty(res) && res.success) {
        SourceDS.query();
      } else if (res === undefined) {
        notification.warning({
          message: intl.get('hevt.common.view.message.form.noChange').d('表单未做修改'),
        });
        return false;
      } else if (res === false) {
        notification.error({
          message: intl.get('hevt.common.view.message.required').d('存在必输字段未填写'),
        });
        return false;
      } else {
        return false;
      }
    },
  });
}
