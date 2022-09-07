/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-09 11:13:09
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: iot告警模板详情/编辑页面
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
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { DETAIL_ACTION, EDIT_ACTION } from '@/utils/constants';
import { iotWarnTemplateDS } from '@/stores/iotWarnTemplateDS';

@formatterCollections({ code: ['hiot.dataPointTemplate', 'hiot.iotWarnTemplate', 'hiot.common'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const { match: { params = {} } = {} } = this.props;
    const { action, messageTemplateId } = params;
    this.state = {
      action,
      messageTemplateId,
    };
    this.iotWarnTemplateDS = new DataSet(iotWarnTemplateDS());
  }

  componentDidMount() {
    const { messageTemplateId } = this.state;
    this.iotWarnTemplateDS.setQueryParameter('messageTemplateId', messageTemplateId);
    this.iotWarnTemplateDS.query().then((resp) => {
      const { templateContent, templateCode, messageTemplateId: tmpId, templateName } = resp;
      this.iotWarnTemplateDS.get(0).set('messageTemplate', {
        templateContent,
        templateCode,
        messageTemplateId: tmpId,
        templateName,
      });
    });
  }

  /**
   * 将页面修改为可编辑的状态
   */
  @Bind()
  handleEdit() {
    this.setState({
      action: EDIT_ACTION,
    });
  }

  /**
   * 修改通知模版
   * @param record
   */
  @Bind()
  handleChangeMessageTemplate(record) {
    const { templateContent = '' } = record || {};

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
    const { action } = this.state;
    return (
      <>
        <Header
          title={
            action === EDIT_ACTION
              ? intl.get('hiot.iotTemplate.view.title.edit.header').d('告警模版编辑')
              : intl.get('hiot.iotTemplate.view.title.detail.header').d('告警模版详情')
          }
          backPath="/hiot/iot-warn/template/list"
        >
          {action === EDIT_ACTION ? (
            <Button icon="save" color="primary" onClick={this.handleIotWarnTemplateSave}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          ) : (
            <Button icon="mode_edit" color="primary" onClick={this.handleEdit}>
              {intl.get('hzero.common.button.edit').d('编辑')}
            </Button>
          )}
        </Header>
        <Content>
          <Card bordered={false} title={intl.get('hiot.common.view.baseInfo').d('基本信息')}>
            <Row>
              <Col span={16}>
                <Form
                  disabled={action === DETAIL_ACTION}
                  dataSet={this.iotWarnTemplateDS}
                  columns={2}
                >
                  <TextField name="alertModelCode" disabled />
                  <TextField name="alertModelName" disabled />
                  <Select name="alertLevel" disabled />
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
                  <Lov
                    noCache
                    name="messageTemplate"
                    disabled={action === DETAIL_ACTION}
                    onChange={this.handleChangeMessageTemplate}
                  />
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
                  <Lov noCache name="receiverName" disabled={action === DETAIL_ACTION} />
                </Form>
              </Col>
            </Row>
          </Card>
        </Content>
      </>
    );
  }
}
