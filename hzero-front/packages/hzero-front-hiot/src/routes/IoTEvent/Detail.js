/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019/10/14 2:59 下午
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: iot告警事件-详情页面
 */
import React from 'react';
import { DataSet, Col, Form, Row, Table, TextField } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';

import { Header, Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import { iotEventDS, concreteTableDS } from '@/stores/iotEventDS';
@formatterCollections({
  code: ['hiot.iotWarnEvent', 'hiot.common'],
})
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const { match: { params = {} } = {} } = this.props;
    const { alertEventId } = params;
    this.state = {
      alertEventId,
    };
    this.concreteTableDS = new DataSet(concreteTableDS());
    this.iotEventDS = new DataSet({
      ...iotEventDS(),
      children: { itemList: this.concreteTableDS },
    });
  }

  componentDidMount() {
    const { alertEventId } = this.state;
    this.iotEventDS.setQueryParameter('alertEventId', alertEventId);
    this.iotEventDS.query();
  }

  /**
   * 具体信息的列
   * @returns {*[]}
   */
  get concreteInfoColumns() {
    return [
      { name: 'propertyName' },
      { name: 'itemTypeMeaning' },
      { name: 'value' },
      { name: 'unitCode' },
    ];
  }

  render() {
    return (
      <>
        <Header
          title={intl.get('hiot.iotWarnEvent.view.title.detail').d('查看详情')}
          backPath="/hiot/iot-warn/event/list"
        />
        <Content>
          <Card bordered={false} title={intl.get('hiot.common.view.baseInfo').d('基本信息')}>
            <Row>
              <Col span={16}>
                <Form disabled dataSet={this.iotEventDS} columns={2}>
                  <TextField name="eventTime" />
                  <TextField name="alertCode" />
                  <TextField name="alertLevelMeaning" />
                  <TextField name="recoveredFlagMeaning" />
                </Form>
              </Col>
            </Row>
          </Card>
          <Card
            bordered={false}
            title={intl.get('hiot.iotWarnEvent.view.title.concreteInfo').d('具体信息')}
          >
            <Table
              selectionMode="click"
              dataSet={this.concreteTableDS}
              columns={this.concreteInfoColumns}
            />
          </Card>
        </Content>
      </>
    );
  }
}
