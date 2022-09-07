/**
 * ParamMapping - 参数映射Tab
 * @date: 2019/8/27
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Tabs } from 'hzero-ui';
import { groupBy, isEmpty, isArray } from 'lodash';
import intl from 'utils/intl';
import ParamTable from './ParamTable';
import styles from './index.less';

const { TabPane } = Tabs;

/**
 * 参数映射Tab
 * @extends {Component} - React.Component
 * @reactProps {object} instanceDetail - 实例详情
 * @reactProps {Function} onEditLine - 编辑行
 * @return React.element
 */

export default class ParamMapping extends Component {
  render() {
    const {
      instanceDetail: { applicationInstMapList = [], ...rest },
      onEditLine = () => {},
      interfaceId,
    } = this.props;
    let dataSource = {};
    if (isArray(applicationInstMapList) && applicationInstMapList.length) {
      // 分成REQ和RESP两组
      dataSource = groupBy(applicationInstMapList, 'actionType');
      if (!isEmpty(dataSource) && dataSource.REQ) {
        dataSource.REQ = groupBy(dataSource.REQ, 'sourceParamNameType');
      }
      if (!isEmpty(dataSource) && dataSource.RESP) {
        dataSource.RESP = groupBy(dataSource.REQ, 'sourceParamNameType');
      }
    }

    const commonProps = {
      instInterfaceId: rest.instInterfaceId,
      onEditLine,
      isReq: true,
      interfaceId,
    };
    const requestHeaderProps = {
      dataSource: dataSource.REQ && dataSource.REQ.HEADER,
      ...commonProps,
      ref: (node) => {
        this.requestHeaderRef = node;
      },
    };

    const queryParamsProps = {
      dataSource: dataSource.REQ && dataSource.REQ.GET,
      ...commonProps,
      ref: (node) => {
        this.queryParamsRef = node;
      },
    };

    const pathParamsProps = {
      dataSource: dataSource.REQ && dataSource.REQ.PATH,
      ...commonProps,
      ref: (node) => {
        this.pathParamsRef = node;
      },
    };

    const bodyParamsProps = {
      dataSource: dataSource.REQ && dataSource.REQ.BODY,
      ...commonProps,
      ref: (node) => {
        this.bodyRef = node;
      },
    };

    const respProps = {
      dataSource: dataSource.RESP && dataSource.RESP.HEADER,
      ...commonProps,
      isReq: false,
      ref: (node) => {
        this.respRef = node;
      },
    };

    const respBodyProps = {
      dataSource: dataSource.RESP && dataSource.RESP.BODY,
      ...commonProps,
      isReq: false,
      ref: (node) => {
        this.respBodyRef = node;
      },
    };

    return (
      <div>
        <Tabs
          defaultActiveKey="params"
          animated={false}
          className={styles['top-tabs']}
          onChange={this.changeTab}
          forceRender
        >
          <TabPane
            tab={intl.get('hitf.document.view.message.title.request.mapping').d('请求参数映射')}
            key="params"
          >
            <Tabs
              animated={false}
              defaultActiveKey="requestHeader"
              tabPosition="left"
              className={styles['sub-params-tabs']}
            >
              <TabPane
                tab={intl.get('hitf.document.view.title.requestHeader').d('请求头部')}
                key="requestHeader"
              >
                <ParamTable {...requestHeaderProps} />
              </TabPane>
              <TabPane
                tab={intl.get('hitf.document.view.title.queryParams').d('GET/URL参数')}
                key="queryParams"
              >
                <ParamTable {...queryParamsProps} />
              </TabPane>
              <TabPane
                tab={intl.get('hitf.document.view.title.pathParams').d('路径参数')}
                key="pathParams"
              >
                <ParamTable {...pathParamsProps} />
              </TabPane>
              <TabPane
                tab={intl.get('hitf.document.view.title.bodyParams').d('BODY参数')}
                key="body"
              >
                <ParamTable {...bodyParamsProps} />
              </TabPane>
            </Tabs>
          </TabPane>
          <TabPane
            tab={intl.get('hitf.document.view.message.title.response.mapping').d('响应参数映射')}
            key="response"
          >
            <Tabs
              animated={false}
              defaultActiveKey="responseHeader"
              tabPosition="left"
              className={styles['sub-params-tabs']}
            >
              <TabPane
                tab={intl.get('hitf.document.view.title.responseHeader').d('响应头部')}
                key="responseHeader"
              >
                <ParamTable {...respProps} />
              </TabPane>
              <TabPane
                tab={intl.get('hitf.document.view.title.responseBody').d('响应结果')}
                key="body"
              >
                <ParamTable {...respBodyProps} />
              </TabPane>
            </Tabs>
          </TabPane>
        </Tabs>
      </div>
    );
  }
}
