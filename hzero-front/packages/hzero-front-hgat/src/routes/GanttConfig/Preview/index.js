/**
 * index - 甘特图预览
 * @date: 2020-3-26
 * @author: JMY <mingyang.jin@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Button } from 'hzero-ui';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getResponse } from 'utils/utils';
import notification from 'utils/notification';
import {
  saveGanttTaskData,
  getGanttTaskData,
  saveGanttLinkData,
  getGanttLinkData,
} from 'services/api';

import Gantt from '@/components/Gantt';

@formatterCollections({ code: ['hgat.ganttConfig'] })
export default class App extends Component {
  @Bind()
  handleBind(ref) {
    this.testRef = ref || {};
  }

  @Bind()
  handleTabChange(type) {
    switch (type) {
      case 'day':
        // 进行缩放
        this.testRef.ext.zoom.setLevel('Days');
        break;
      case 'month':
        // 进行缩放
        this.testRef.ext.zoom.setLevel('Months');
        break;
      default:
        break;
    }
  }

  @Bind()
  getData() {
    getGanttTaskData(this.testRef).then((res) => {
      getGanttLinkData(this.testRef).then((resp) => {
        this.testRef.clearAll();
        this.testRef.parse({ data: res, links: resp });
      });
    });
  }

  @Bind()
  handleSave() {
    saveGanttTaskData(this.testRef).then((res) => {
      if (getResponse(res)) {
        saveGanttLinkData(this.testRef).then((resp) => {
          if (getResponse(resp)) {
            notification.success();
            this.getData();
          }
        });
      }
    });
  }

  render() {
    const { match } = this.props;
    const {
      params: { code = '', id = '' },
    } = match;
    return (
      <>
        <Header
          title={intl.get('hgat.ganttConfig.view.message.demoTitle').d('甘特图示例')}
          backPath={`/hpfm/gantt/detail/${id}/${code}`}
        >
          <Button icon="save" onClick={this.handleSave}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          {/* <Tabs onChange={this.handleTabChange}>
            <Tabs.TabPane tab={intl.get('hgat.ganttConfig.view.title.day').d('天')} key="day" />
            <Tabs.TabPane tab={intl.get('hgat.ganttConfig.view.title.month').d('月')} key="month" />
          </Tabs> */}
          <Gantt
            code={code}
            template={{}}
            config={{}}
            onRef={this.handleBind}
            onAfterConfig={this.getData}
            onChange={() => {
              console.log('onchange');
            }}
          />
        </Content>
      </>
    );
  }
}
