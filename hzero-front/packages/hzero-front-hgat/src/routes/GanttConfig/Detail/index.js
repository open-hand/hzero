/**
 * @since 2020-03-26
 * @author JMY <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, ModalContainer, Switch, DataSet, Select } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import axios from 'axios';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { detailFormDS } from '@/stores/ganttConfigDS';

@formatterCollections({ code: ['hgat.ganttConfig'] })
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.detailFormDS = new DataSet(detailFormDS());
    this.state = {
      detail: [],
      loading: false,
    };
  }

  componentDidMount() {
    this.refresh();
  }

  // 刷新页面
  @Bind()
  async refresh() {
    const { match } = this.props;
    const {
      params: { id = '' },
    } = match;
    if (id) {
      this.detailFormDS.setQueryParameter('id', id);
      const res = await this.detailFormDS.query();
      const obj = {};
      if (res && res.length > 0) {
        res.map((item) => {
          obj[item.configCode] = item.configValue;
          return true;
        });
      }
      this.detailFormDS.create(obj, 0);
      this.setState({ detail: res });
    }
  }

  @Bind()
  async handleSave() {
    const tenantId = getCurrentOrganizationId();
    const { detail } = this.state;
    const { match } = this.props;
    const {
      params: { id = '' },
    } = match;
    const arr = [];
    const { __id, _status, ...other } = this.detailFormDS.toData()[0];
    Object.keys(other).forEach((key) => {
      arr.push({
        ...detail.find((item) => item.configCode === key),
        tenantId,
        ganttId: id,
        configCode: key,
        configValue: other[key],
      });
    });
    this.setState({ loading: true });
    axios({
      url: `${
        isTenantRoleLevel() ? `${HZERO_PLATFORM}/v1/${tenantId}` : `${HZERO_PLATFORM}/v1`
      }/gantt-configs/${id}`,
      method: 'POST',
      data: arr,
    })
      .then((res) => {
        this.setState({ loading: false });
        if (res) {
          notification.success();
          this.refresh();
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }

  @Bind()
  handlePreview() {
    const { history, match } = this.props;
    const {
      params: { code = '', id = '' },
    } = match;
    history.push(`${HZERO_PLATFORM}/gantt/preview/${id}/${code}`);
  }

  render() {
    const { location, match } = this.props;
    const { loading } = this.state;
    return (
      <>
        <Header
          title={intl.get('hgat.ganttConfig.view.title.ganttConfigDetail').d('甘特图配置详情')}
          backPath="/hpfm/gantt/list"
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '甘特图配置-保存',
              },
            ]}
            color="primary"
            icon="save"
            loading={loading}
            onClick={this.handleSave}
          >
            {intl.get(`hzero.common.button.save`).d('保存')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.preview`,
                type: 'button',
                meaning: '甘特图配置-预览',
              },
            ]}
            loading={loading}
            onClick={this.handlePreview}
          >
            {intl.get(`hzero.common.button.preview`).d('预览')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>
                {intl.get('hgat.ganttConfig.view.title.basicInformation').d('甘特图基础配置')}
              </h3>
            }
          >
            <Form labelWidth={120} labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              <Select name="TIME_DISPLAY_MODE" />
              <Select name="GANTT_LAYOUT" />
              {/* <Select name="SKIN_STYLE" /> */}
              <Switch name="DOUBLE_MODE" />
              <Switch name="EXPEND_TREE" />
              <Switch name="READONLY" />
            </Form>
          </Card>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>
                {intl.get('hgat.ganttConfig.view.title.otherInformation').d('甘特图其他配置')}
              </h3>
            }
          >
            <Form labelWidth={120} labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              {/* <DatePicker name="CREATE_TASK_START_DATE" />
              <DatePicker name="CREATE_TASK_END_DATE" /> */}
              <Switch name="FIT_TASKS" />
              <Switch name="ENABLE_AUTO_SCROLL" />
              <Switch name="ROUND_DND_DATES" />
              <Switch name="SHOW_TASKS_OUTSIDE_TIMESCALE" />
            </Form>
          </Card>
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
