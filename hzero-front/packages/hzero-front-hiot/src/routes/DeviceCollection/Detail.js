import React, { Component } from 'react';
import { Header, Content } from 'components/Page';
import { observer } from 'mobx-react';
import { action, observable } from 'mobx';
import {
  Lov,
  Form,
  Table,
  DataSet,
  TextField,
  Switch,
  Icon,
  Dropdown,
  Menu,
  Modal,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { Card } from 'choerodon-ui';
import { enableRender } from 'utils/renderer';
import { DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { queryIdpValue } from 'services/api';
import { detailFormDS, CollectionDS } from '../../stores/DeviceCollectionDS';
import styles from './Detail.less';
import { fixStatus } from '../../services/deviceCollectionService';

import FormDrawer from './FormDrawer';

const { Column } = Table;
const modelPrompt = 'hiot.deviceCollection';

@formatterCollections({
  code: ['hiot.deviceCollection', 'hiot.common'],
})
@observer
export default class CollectionMaintenancePage extends Component {
  @observable editor = false;

  state = {
    status: true, // 可编辑
    expandForm: true,
    valueList: [],
  };

  formDrawerDs;

  formDs = new DataSet(detailFormDS());

  CollectionDs = new DataSet(CollectionDS());

  stateRecord = (record, name) => {
    const { status } = this.state;

    if (
      this.editor &&
      record.status === 'add' &&
      ['triggerFlag', 'recordChangesFlag', 'publishedFlag', 'enableFlag'].includes(name)
    ) {
      return <Switch />;
    } else if (this.editor && record.status === 'add') {
      return true;
    } else if (
      this.editor &&
      record.status !== 'add' &&
      status &&
      ['triggerFlag', 'recordChangesFlag', 'publishedFlag', 'enableFlag'].includes(name)
    ) {
      return <Switch />;
    } else if (this.editor && record.status !== 'add' && status) {
      return true;
    } else if (this.editor && record.status !== 'add') {
      return false;
    } else if (!this.editor) {
      return false;
    }
  };

  getStatus = (record, name) => {
    return this.stateRecord(record, name);
  };

  componentDidMount = async () => {
    const { match } = this.props;
    const matches = match.params;

    try {
      this.formDs.setQueryParameter('dcDeviceId', matches.id);
      await this.formDs.query();
    } catch (err) {
      //
    }

    try {
      this.CollectionDs.setQueryParameter('dcDeviceId', matches.id);
      await this.CollectionDs.query();
    } catch (err) {
      //
    }

    queryIdpValue('HIOT.EDGINK.DC.STATUS_CHANGE').then((res) => {
      if (res) {
        this.setState({
          valueList: res,
        });
      }
    });
  };

  @action
  onEdit = async () => {
    this.editor = !this.editor;
    if (!this.editor) {
      await this.setStates(false);
    } else {
      await this.setStates(true);
    }
  };

  setStates = async (status) => {
    this.setState({
      status,
    });
  };

  onSave = async () => {
    try {
      await this.CollectionDs.submit();
      this.editor = !this.editor;
      await this.CollectionDs.query();
    } catch (err) {
      //
    }
    this.setState({
      status: false,
    });
  };

  parameter = ({ record }) => {
    return (
      <a
        // className="action-link"
        // className={styles.action}
        onClick={() => this.handleOpen(false, record.get('dcDeviceTagId') || '')}
      >
        {record.get('parameter')}
      </a>
    );
  };

  // 删除
  handleDel = async () => {
    if (!this.CollectionDs.selected.length) {
      notification.error({
        message: intl.get(`${modelPrompt}.view.message.deleteSelect`).d('请勾选数据'),
      });
      return;
    }
    const res = await this.CollectionDs.delete(
      this.CollectionDs.selected,
      <div>
        {intl
          .get(`${modelPrompt}.view.message.detailDeleteInfo`)
          .d('是否确认删除选中的设备采集项？')}
      </div>
    );
    if (res && res.failed && res.message) {
      notification.error({
        message: res.message,
      });
    } else {
      this.CollectionDs.query();
    }
  };

  toggleForm = () => {
    const { expandForm } = this.state;
    this.setState({ expandForm: !expandForm });
  };

  menuClick = async ({ key }) => {
    if (!this.CollectionDs.selected.length) {
      notification.error({
        message: intl.get(`${modelPrompt}.view.message.deleteSelect`).d('请勾选数据'),
      });
      return;
    }
    const tagId = this.CollectionDs.selected.map((item) => item.data.dcDeviceTagId);
    const obj = {
      tagIds: tagId,
    };
    obj[key] = 1;
    await fixStatus(obj).then((res) => {
      if (res && !res.failed) {
        this.CollectionDs.query();
      }
    });
  };

  @Bind()
  handleOpen(isNew, dcDeviceTagId) {
    this.formDrawerDs = new DataSet(CollectionDS());
    Modal.open({
      drawer: true,
      key: 'fromDrawer',
      destroyOnClose: true,
      closable: true,
      style: { width: 600 },
      className: styles['dev-collection-modal'],
      title: isNew
        ? intl.get(`${modelPrompt}.view.title.NewDcDeviceTag`).d('新建设备采集项')
        : intl.get(`${modelPrompt}.view.title.EditDcDeviceTag`).d('编辑设备采集项'),
      children: <FormDrawer formDs={this.formDrawerDs} isNew={isNew} tagId={dcDeviceTagId} />,
      onOk: this.onFormDrawerSave,
    });
  }

  @Bind()
  async onFormDrawerSave() {
    try {
      this.formDrawerDs.current.set('dcDeviceId', this.props.match.params.id || '');
      const res = await this.formDrawerDs.submit();
      if (!res) {
        return false;
      }
      this.CollectionDs.query();
    } catch (err) {
      //
    }
    // }
  }

  render() {
    const { expandForm, valueList = [] } = this.state;
    const title = (
      <span>
        {intl.get(`${modelPrompt}view.button.detailDeviceInfo`).d('设备属性')}
        <a style={{ marginLeft: '16px', fontSize: '12px' }} onClick={this.toggleForm}>
          {expandForm
            ? intl.get(`hzero.common.button.up`).d('收起')
            : intl.get(`hzero.common.button.expand`).d('展开')}
          <Icon type={expandForm ? 'up' : 'down'} />
        </a>
      </span>
    );

    const menu = (
      <Menu>
        {valueList.map((item) => (
          <Menu.Item key={item.value} onClick={this.menuClick}>
            {item.meaning}
          </Menu.Item>
        ))}
      </Menu>
    );

    const titleT = (
      <span>
        {intl.get(`${modelPrompt}.view.title.deviceTagInfo`).d('设备采集项属性')}
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${this.props.match.path}.button.detailCreate`,
              type: 'button',
              meaning: '设备采集-编辑页-新增',
            },
          ]}
          color="primary"
          icon="add"
          onClick={() => {
            this.handleOpen(true);
          }}
          key="add"
          style={{ float: 'right' }}
        >
          {intl.get('hzero.common.button.add').d('新增')}
        </ButtonPermission>
        <Dropdown overlay={menu}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${this.props.match.path}.button.statusChange`,
                type: 'button',
                meaning: '设备采集-批量处理',
              },
            ]}
            style={{ float: 'right', marginRight: '8px' }}
            icon="repeat"
          >
            {intl.get(`${modelPrompt}.view.button.statusChange`).d('批量处理')}
          </ButtonPermission>
        </Dropdown>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${this.props.match.path}.button.detailDelete`,
              type: 'button',
              meaning: '设备采集-详情页-删除',
            },
          ]}
          icon="delete"
          onClick={this.handleDel}
          style={{ float: 'right', marginRight: '8px' }}
        >
          {intl.get(`hzero.common.button.delete`).d('删除')}
        </ButtonPermission>
      </span>
    );
    return (
      <>
        <div className={styles.collectionMaintenance}>
          <Header
            title={intl.get(`${modelPrompt}.view.title.detailMaintenance`).d('采集设备维护')}
            backPath="/hiot/device-collection/list"
          />
          <Content>
            <Card
              key="code-rule-header"
              title={title}
              bordered={false}
              className={DETAIL_CARD_TABLE_CLASSNAME}
              size="small"
            />
            <div style={expandForm ? null : { display: 'none' }}>
              <Form className="normalTable" dataSet={this.formDs} disabled columns={3}>
                <Lov name="gatewayObject" />
                <TextField name="dcDeviceCode" />
                <TextField name="description" />
                <TextField name="packageName" />
                <TextField name="heartbeatCycle" />
                <Switch name="simulatorFlag" />
                <TextField colSpan={2} name="connectInfo" />
                <Switch name="enableFlag" />
              </Form>
            </div>
            <Card
              key="code-rule-header"
              title={titleT}
              bordered={false}
              className={DETAIL_CARD_TABLE_CLASSNAME}
              size="small"
            />
            <Table key="CollectionMaintenanceDS" dataSet={this.CollectionDs}>
              <Column
                name="parameter"
                editor={this.getStatus}
                width={130}
                lock
                renderer={this.parameter}
                sortable
              />
              <Column name="address" editor={this.getStatus} lock width={130} sortable />
              <Column name="description" editor={this.getStatus} lock width={130} sortable />
              <Column name="orderCode" editor={this.getStatus} width={130} sortable />
              <Column name="dataType" editor={this.getStatus} width={130} />
              <Column name="clientAccess" width={130} editor={this.getStatus} />
              <Column name="frequency" width={130} editor={this.getStatus} />
              <Column name="multiple" editor={this.getStatus} width={130} />

              <Column
                name="triggerFlag"
                editor={this.getStatus}
                width={130}
                renderer={({ value }) => enableRender(value)}
                help={intl
                  .get(`${modelPrompt}.view.message.helpMsg`)
                  .d('当轮巡采集时，主动采集设备数据，否则不主动采集')}
              />
              <Column
                name="recordChangesFlag"
                editor={this.getStatus}
                width={130}
                renderer={({ value }) => enableRender(value)}
                help={intl
                  .get(`${modelPrompt}.view.message.helpDevMsg`)
                  .d('当设备采集项变化时持久化存储，否则定时存储')}
              />
              <Column
                name="publishedFlag"
                editor={this.getStatus}
                width={120}
                renderer={({ value }) => enableRender(value)}
                help={intl
                  .get(`${modelPrompt}.view.message.helpRedisMsg`)
                  .d('设备数据变化时，是否使用redis将数据推送')}
              />
              <Column
                name="enableFlag"
                editor={this.getStatus}
                renderer={({ value }) => enableRender(value)}
              />
            </Table>
          </Content>
        </div>
      </>
    );
  }
}
