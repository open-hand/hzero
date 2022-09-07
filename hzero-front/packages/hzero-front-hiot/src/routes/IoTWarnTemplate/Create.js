/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-09 11:13:09
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: iot告警模板创建页面
 */
import React from 'react';
import {
  Button,
  Form,
  TextField,
  NumberField,
  Row,
  Col,
  Lov,
  Switch,
  TextArea,
  Select,
  DataSet,
  IntlField,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { iotWarnTemplateDS } from '@/stores/iotWarnTemplateDS';

@formatterCollections({ code: ['hiot.dataPointTemplate', 'hiot.iotWarnTemplate', 'hiot.common'] })
export default class Create extends React.Component {
  constructor(props) {
    super(props);
    this.iotWarnTemplateDS = new DataSet(iotWarnTemplateDS());
  }

  componentDidMount() {
    this.iotWarnTemplateDS.create({ enabled: 0 }, 0);
  }

  /**
   * 修改通知模版
   * @param record
   */
  @Bind()
  handleChangeMessageTemplate(record = {}) {
    const { templateContent = '' } = record;

    this.iotWarnTemplateDS.get(0).set('templateContent', templateContent);
  }

  /**
   * 保存iot告警模板
   */
  @Bind()
  async handleIotWarnTemplateSave() {
    try {
      const res = await this.iotWarnTemplateDS.submit();
      if (!res) {
        return false;
      } else {
        this.props.history.push('/hiot/iot-warn/template/list');
      }
    } catch (err) {
      // const errTitle = intl.get('hzero.common.notification.error').d('操作失败');
      // notification.error({ message: `${errTitle}:${err.message}` });
    }
  }

  render() {
    return (
      <>
        <Header
          title={intl.get('hiot.iotTemplate.view.title.create.header').d('新建告警模板')}
          backPath="/hiot/iot-warn/template/list"
        >
          <Button color="primary" icon="save" onClick={this.handleIotWarnTemplateSave}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Card bordered={false} title={intl.get('hiot.common.view.baseInfo').d('基本信息')}>
            <Row>
              <Col span={16}>
                <Form dataSet={this.iotWarnTemplateDS} columns={2}>
                  <TextField name="alertModelCode" />
                  <IntlField name="alertModelName" />
                  <Select name="alertLevel" />
                  <NumberField name="dummyTime" min={0} max={999999} step={1} addonAfter="S" />
                  <Switch name="enabled" />
                  <TextArea name="description" rowSpan={2} />
                </Form>
              </Col>
            </Row>
          </Card>
          <Card
            bordered={false}
            title={intl.get('hiot.iotTemplate.model.iotTemplate.noticeTemplate').d('通知模板')}
          >
            <Row>
              <Col span={16}>
                <Form dataSet={this.iotWarnTemplateDS} columns={2}>
                  <Lov name="messageTemplate" onChange={this.handleChangeMessageTemplate} />
                  <Col span={4}>
                    <Button color="primary">
                      {intl.get('hiot.iotTemplate.model.iotTemplate.newTemplate').d('新增模板')}
                    </Button>
                  </Col>
                </Form>
              </Col>
            </Row>
            <Row>
              <Col span={12}>
                <Form dataSet={this.iotWarnTemplateDS} columns={1}>
                  <TextArea name="templateContent" disabled />
                </Form>
              </Col>
            </Row>
            <Row>
              <Col span={8}>
                <Form dataSet={this.iotWarnTemplateDS} columns={1}>
                  <Lov name="receiverName" />
                </Form>
              </Col>
            </Row>
          </Card>
        </Content>
      </>
    );
  }
}
