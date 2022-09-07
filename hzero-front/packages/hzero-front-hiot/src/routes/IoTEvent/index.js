/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019/10/14 1:51 下午
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 告警事件列表页面
 */
import React from 'react';
import { DataSet, Table, Modal, ModalContainer } from 'choerodon-ui/pro';
import { Badge } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import { drawerDS, iotEventDS } from '@/stores/iotEventDS';
import Drawer from './Drawer';

@formatterCollections({
  code: ['hiot.iotWarnEvent'],
})
export default class IoTEvent extends React.Component {
  constructor(props) {
    super(props);
    this.iotEventDS = new DataSet(iotEventDS());
    this.drawerDs = null;
  }

  componentDidMount() {
    this.iotEventDS.query();
  }

  /**
   * 告警事件列表表格列
   */
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'thingName' },
      { name: 'guid' },
      {
        name: 'alertCode',
        renderer: ({ value }) => <a onClick={() => this.onShowAlertDetail(value)}>{value}</a>,
      },
      { name: 'alertLevelMeaning', width: 100 },
      {
        name: 'alertRangeCodeMeaning',
        width: 100,
        align: 'center',
        renderer: ({ value, record }) => {
          let status = '';
          switch (record.get('alertRangeCode')) {
            case 'NORMAL':
              status = 'success';
              break;
            case 'ALERT':
              status = 'warning';
              break;
            case 'RISK':
              status = 'error';
              break;
            default:
              break;
          }
          return <Badge status={status} text={value} />;
        },
      },
      { name: 'eventTime' },
      {
        name: 'recoveredFlagMeaning',
        width: 100,
        align: 'center',
        renderer: ({ value, record }) => (
          <Badge status={record.get('recoveredFlag') === 1 ? 'success' : 'error'} text={value} />
        ),
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 80,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.detail`,
                    type: 'button',
                    meaning: '告警事件-详情',
                  },
                ]}
                onClick={() => this.handleDetail(record.get('alertEventId'))}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ];
  }

  /**
   * 跳转到详情页面
   */
  @Bind()
  handleDetail(alertEventId) {
    this.props.history.push(`/hiot/iot-warn/event/${alertEventId}`);
  }

  @Bind()
  onShowAlertDetail(value) {
    this.drawerDs = new DataSet(drawerDS());
    this.drawerDs.create({});
    Modal.open({
      closable: true,
      key: 'water-mark-config',
      title: intl.get('hiot.iotWarnEvent.view.title.alertDetail').d('配置详情'),
      drawer: true,
      style: {
        width: 500,
      },
      children: <Drawer code={value} drawerDs={this.drawerDs} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  render() {
    const { location } = this.props;
    return (
      <>
        <Header title={intl.get('hiot.iotWarnEvent.view.title.header').d('告警事件列表')} />
        <Content>
          <Table dataSet={this.iotEventDS} columns={this.columns} queryFieldsLimit={3} />
        </Content>
        <ModalContainer location={location} />
      </>
    );
  }
}
