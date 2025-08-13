/**
 *
 * @date: 2019-10-16
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Table, Form, DataSet, Button } from 'choerodon-ui/pro';
import { Row, Col } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel, getAccessToken } from 'utils/utils';

import { listDS } from '../../stores/OnlineDS';

@connect(({ online }) => ({
  online,
}))
@formatterCollections({ code: ['hpfm.online', 'entity.time'] })
export default class Online extends React.Component {
  onlineTimer;

  tableDS = new DataSet(listDS());

  componentDidMount() {
    this.onlineTimer = setInterval(() => {
      this.tableDS.loadData(this.tableDS.toData(), this.tableDS.totalCount);
    }, 10000);
  }

  componentWillUnmount() {
    clearInterval(this.onlineTimer);
    this.onlineTimer = null;
  }

  get columns() {
    return [
      {
        name: 'loginName',
        header: intl.get('hpfm.online.model.online.loginName').d('登录名'),
        // width: 160,
      },
      !isTenantRoleLevel() && {
        name: 'tenantName',
        header: intl.get('hpfm.online.model.online.tenantName').d('当前租户'),
        width: 160,
      },
      !isTenantRoleLevel() && {
        name: 'organizationName',
        header: intl.get('hpfm.online.model.online.organizationName').d('所属租户'),
        width: 160,
      },
      {
        name: 'loginDate',
        header: intl.get('hpfm.online.model.online.loginDate').d('登陆时间'),
        width: 160,
      },
      {
        header: intl.get('hpfm.online.model.online.duration').d('在线时长'),
        width: 120,
        renderer: ({ record }) => {
          const loginDate = record.get('loginDate');
          const time = moment();
          const diffTime = time - loginDate;
          const duration = moment.duration(diffTime, 'ms');
          if (!loginDate) {
            return;
          }
          if (diffTime < 0) {
            return intl.get('hpfm.online.model.online.second').d('数秒前');
          } else if (diffTime < 60000) {
            return `${duration.get('seconds')}${intl.get('entity.time.time.second').d('秒')}`;
          } else if (diffTime > 60000 && diffTime < 3600000) {
            return `${duration.get('minutes')}${intl
              .get('entity.time.time.minute')
              .d('分')}${duration.get('seconds')}${intl.get('entity.time.time.second').d('秒')}`;
          } else if (diffTime > 3600000 && diffTime < 86400000) {
            return `${duration.get('hours')}${intl
              .get('entity.time.time.hour')
              .d('时')}${duration.get('minutes')}${intl.get('entity.time.time.minute').d('分')}`;
          } else {
            return `${duration.get('days')}${intl
              .get('entity.time.time.day')
              .d('日')}${duration.get('hours')}${intl.get('entity.time.time.hour').d('时')}`;
          }
        },
      },
      {
        name: 'loginIp',
        header: intl.get('hpfm.online.model.online.loginIp').d('登录地址'),
        width: 140,
      },
      {
        name: 'phone',
        header: intl.get('hpfm.online.model.online.phone').d('电话'),
        width: 120,
      },
      {
        name: 'email',
        header: intl.get('hpfm.online.model.online.email').d('邮箱'),
        width: 300,
      },
      // {
      //   header: intl.get('hzero.common.button.action').d('操作'),
      //   width: 90,
      //   renderer: ({ record }) => {
      //     return (
      //       <span className="action-link">
      //         <a
      //           onClick={() => {
      //             this.handleLogout(record);
      //           }}
      //         >
      //           {intl.get('hzero.common.button.logout').d('强制登出')}
      //         </a>
      //       </span>
      //     );
      //   },
      //   lock: 'right',
      // },
    ].filter(Boolean);
  }

  @Bind()
  handleFetchList() {
    this.tableDS.query();
  }

  @Bind()
  handleLogout(record) {
    const { dispatch } = this.props;
    const accessToken = record.get('accessToken');
    if (getAccessToken() === accessToken) {
      dispatch({
        type: 'login/logout',
      });
    } else {
      dispatch({
        type: 'online/logout',
        payload: record.toData(),
      });
    }
    this.handleFetchList();
  }

  @Bind()
  renderBar({ queryFields, buttons, dataSet, queryDataSet }) {
    if (queryDataSet && !isTenantRoleLevel()) {
      return (
        <Row type="flex" className="c7n-form-line-with-btn" gutter={24}>
          <Col span={6}>
            <Form
              columns={1}
              dataSet={queryDataSet}
              onKeyDown={(e) => {
                if (e.keyCode === 13) return this.handleFetchList();
              }}
            >
              {queryFields}
            </Form>
          </Col>
          <Col span={6} className="c7n-form-btn">
            <div
              style={{
                flexShrink: 0,
                display: 'flex',
                alignItems: 'center',
              }}
            >
              <Button
                onClick={() => {
                  queryDataSet.current.reset();
                  dataSet.fireEvent('queryBarReset', {
                    dataSet,
                    queryFields,
                  });
                }}
              >
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                dataSet={null}
                onClick={() => {
                  this.handleFetchList();
                }}
                color="primary"
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
              {buttons}
            </div>
          </Col>
        </Row>
      );
    }
    return null;
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hpfm.online.view.message.title.online').d('在线用户')}>
          {isTenantRoleLevel() && (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}/refresh`,
                  type: 'button',
                  meaning: '在线人员统计-刷新',
                },
              ]}
              icon="sync"
              color="primary"
              onClick={() => {
                this.handleFetchList();
              }}
            >
              {intl.get('hzero.common.button.refresh').d('刷新')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Table
            dataSet={this.tableDS}
            columns={this.columns}
            queryBar={this.renderBar}
            queryFieldsLimit={3}
          />
        </Content>
      </>
    );
  }
}
