/**
 * @since 2019-12-19
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Table, TextField } from 'choerodon-ui/pro';
import { Popconfirm, Row, Col } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';

import { detailFormDS, detailTableDS } from '@/stores/apiLimitDS';
import { addTOBlackList } from '@/services/apiLimitService';

export default class ViewMonitor extends React.Component {
  constructor(props) {
    super(props);
    this.detailFormDS = new DataSet(detailFormDS());
    this.detailTableDS = new DataSet(detailTableDS());
  }

  componentDidMount() {
    this.queryData();
  }

  get columns() {
    return [
      {
        name: 'monitorKey',
        width: 130,
      },
      {
        name: 'monitorUrl',
        width: 270,
      },
      {
        name: 'minStatistics',
        align: 'left',
      },
      {
        name: 'maxStatistics',
        align: 'left',
      },
      {
        name: 'avgStatistics',
        align: 'left',
      },
      {
        name: 'avgFailedStatistics',
        align: 'left',
      },
      {
        name: 'sumStatistics',
        align: 'left',
      },
      {
        name: 'sumFailedStatistics',
        align: 'left',
      },
      {
        name: 'startDate',
        width: 160,
        align: 'left',
      },
      {
        name: 'endDate',
        width: 160,
        align: 'left',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 112,
        renderer: ({ record }) => {
          let actions = [];
          const { inBlacklist } = record.toData();
          actions = [
            !inBlacklist
              ? {
                  ele: (
                    <Popconfirm
                      placement="topRight"
                      title={intl
                        .get('hadm.apiLimit.message.confirm.addBlackList')
                        .d('是否确定加入黑名单？')}
                      okText={intl.get('hzero.common.button.ok').d('确定')}
                      cancelText={intl.get('hzero.common.button.cancel').d('取消')}
                      onConfirm={() => this.addBlack(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${this.props.match.path}.button.addBlack`,
                            type: 'button',
                            meaning: 'API访问控制-加入黑名单',
                          },
                        ]}
                      >
                        {intl.get('hadm.apiLimit.view.button.addBlack').d('加入黑名单')}
                      </ButtonPermission>
                    </Popconfirm>
                  ),
                  key: 'addBlack',
                  len: 4,
                  title: intl.get('hadm.apiLimit.view.button.addBlack').d('加入黑名单'),
                }
              : null,
          ];
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'left',
      },
    ];
  }

  @Bind()
  queryData() {
    const {
      match: { params },
    } = this.props;
    const { monitorRuleId = '' } = params;
    this.detailTableDS.setQueryParameter('monitorRuleId', monitorRuleId);
    this.detailTableDS.query();
  }

  @Bind()
  async addBlack(record) {
    const { monitorKey, monitorRuleId } = record.toData();
    const res = await addTOBlackList({ monitorKey, monitorRuleId });
    if (res && res.failed) {
      notification.error({
        message: res.message,
      });
    } else if (res && !res.failed) {
      this.queryData();
      notification.success({
        message: intl.get('hzero.common.notification.success').d('操作成功'),
      });
    }
  }

  @Bind()
  handleRefresh() {
    this.queryData();
  }

  render() {
    const { location: { search, pathname } = {} } = this.props;
    const { timeWindowSize = '', urlPattern = '', access_token: accessToken } = queryString.parse(
      search.substring(1)
    );
    return (
      <>
        <Header
          title={intl.get('hadm.apiManagement.view.title.apiLimit').d('API访问控制')}
          backPath={
            pathname.indexOf('/private') === 0
              ? `/private/hadm/api-limit/list?access_token=${accessToken}`
              : '/hadm/api-limit/list'
          }
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${this.props.match.path}.button.refresh`,
                type: 'button',
                meaning: 'API访问控制-刷新',
              },
            ]}
            icon="refresh"
            onClick={this.handleRefresh}
          >
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </ButtonPermission>
        </Header>
        <Content>
          <Row>
            <Col span={3}>
              <span style={{ fontSize: 14, lineHeight: 2 }}>
                {intl.get('hadm.apiLimit.model.apiLimit.urlPattern').d('匹配规则')}：
              </span>
            </Col>
            <Col>
              <TextField defaultValue={urlPattern} disabled />
            </Col>
          </Row>
          <Row style={{ marginTop: 16, marginBottom: 16 }}>
            <Col span={3}>
              <span style={{ fontSize: 14, lineHeight: 2 }}>
                {intl.get('hadm.apiLimit.model.apiLimit.timeWindowSize').d('时间窗口大小(s)')}：
              </span>
            </Col>
            <Col>
              <TextField defaultValue={timeWindowSize} disabled />
            </Col>
          </Row>
          <Table dataSet={this.detailTableDS} columns={this.columns} />
        </Content>
      </>
    );
  }
}
