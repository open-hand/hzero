/*
 * @Description: 报警高级配置
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-24 11:50:37
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { DataSet, Modal, Tabs } from 'choerodon-ui/pro';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import { withRouter } from 'react-router';

import intl from 'utils/intl';
import { Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import {
  InhibitionRulesDS,
  SilentRulesDS,
  matchRuleDS,
  RouterConfigDS,
} from '@/stores/AlertAdvancedDS';
import { HeaderButtons } from './HeaderButtons';
import InhibitionRules from '../InhibitionRules';
import SilentRules from '../SilentRules';
import RouterConfig from '../RouterConfig';
import InhibitionRulesEditModal from '../InhibitionRules/EditModal';
import SilentRulesEditModal from '../SilentRules/EditModal';
import RouterConfigEditModal from '../RouterConfig/EditModal';

const modalKey = Modal.key();
const { TabPane } = Tabs;

@withRouter
@formatterCollections({ code: ['halt.common', 'halt.alertAdvanced'] })
export default class ObjectTag extends Component {
  constructor(props) {
    super(props);
    const {
      match: { path },
    } = props;
    const matchRuleObj = matchRuleDS();
    this.InhibitionRulesDS = new DataSet({
      ...InhibitionRulesDS(),
      children: {
        sourceAlertMatchList: new DataSet({
          ...matchRuleObj,
        }),
        targetAlertMatchList: new DataSet({
          ...matchRuleObj,
        }),
      },
    });
    this.SilentRulesDS = new DataSet({
      ...SilentRulesDS(),
      children: {
        targetAlertMatchList: new DataSet({
          ...matchRuleObj,
        }),
      },
    });
    this.RouterConfigDS = new DataSet({
      ...RouterConfigDS(),
      children: {
        alertMatchList: new DataSet({
          ...matchRuleObj,
        }),
      },
    });
    this.state = {
      activeKey: 'RouterConfig', // 抑制
      path,
    };
  }

  /**
   * 新建路由配置弹框
   */
  @Bind
  handleRouterConfigModal() {
    this.RouterConfigDS.create({
      parentId: null,
    });
    const modalPropertys = {
      title: intl.get('halt.alertAdvanced.view.title.alertAdvanced.RC.create').d('创建路由规则'),
      drawer: true,
      closable: true,
      style: {
        width: 700,
      },
      key: modalKey,
      children: <RouterConfigEditModal RouterConfigDS={this.RouterConfigDS} />,
      onCancel: () => this.RouterConfigDS.reset(),
      onClose: () => this.RouterConfigDS.reset(),
      onOk: async () => {
        const { alertMatchList } = this.RouterConfigDS.current.toData();
        if (alertMatchList.length === 0) {
          notification.warning({
            message: intl
              .get('halt.common.validation.messsage.alertMatchList.notNull')
              .d('匹配条件不能为空'),
          });
          return false;
        }
        const res = await this.RouterConfigDS.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          return false;
        } else if (!isEmpty(res) && res.success) {
          this.RouterConfigDS.query();
        } else if (res === undefined) {
          notification.warning({
            message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
          });
          return false;
        } else if (res === false) {
          notification.error({
            message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
          });
          return false;
        } else {
          return false;
        }
      },
    };
    Modal.open(modalPropertys);
  }

  /**
   * 新建抑制弹框
   */
  @Bind
  handleInhibitionRulesModal() {
    this.InhibitionRulesDS.create({});
    const modalPropertys = {
      title: intl.get('halt.alertAdvanced.view.title.alertAdvanced.in.create').d('创建抑制规则'),
      drawer: true,
      closable: true,
      style: {
        width: 700,
      },
      key: modalKey,
      children: <InhibitionRulesEditModal alarmSeniorDS={this.InhibitionRulesDS} />,
      onCancel: () => this.InhibitionRulesDS.reset(),
      onClose: () => this.InhibitionRulesDS.reset(),
      onOk: async () => {
        const {
          sourceAlertMatchList,
          targetAlertMatchList,
        } = this.InhibitionRulesDS.current.toData();
        if (sourceAlertMatchList.length === 0 || targetAlertMatchList.length === 0) {
          notification.warning({
            message: intl
              .get('halt.common.validation.messsage.sourceTargetAlert.notNull')
              .d('来源警报匹配和被抑制警报匹配不能为空'),
          });
          return false;
        }
        const res = await this.InhibitionRulesDS.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          return false;
        } else if (!isEmpty(res) && res.success) {
          this.InhibitionRulesDS.query();
        } else if (res === undefined) {
          notification.warning({
            message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
          });
          return false;
        } else if (res === false) {
          notification.error({
            message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
          });
          return false;
        } else {
          return false;
        }
      },
    };
    Modal.open(modalPropertys);
  }

  /**
   * 新建静默弹框
   */
  @Bind
  handleSilentRulesModal() {
    this.SilentRulesDS.create({
      startTime: new Date(),
    });
    const modalPropertys = {
      title: intl
        .get('halt.alertAdvanced.view.title.alertAdvanced.silent.create')
        .d('创建静默规则'),
      drawer: true,
      closable: true,
      style: {
        width: 700,
      },
      key: modalKey,
      children: <SilentRulesEditModal alarmSeniorDS={this.SilentRulesDS} />,
      onCancel: () => this.SilentRulesDS.reset(),
      onClose: () => this.SilentRulesDS.reset(),
      onOk: async () => {
        const { targetAlertMatchList } = this.SilentRulesDS.current.toData();
        if (targetAlertMatchList.length === 0) {
          notification.warning({
            message: intl
              .get('halt.alertAdvanced.validation.messsage.silent.notNull')
              .d('被静默警报匹配规则不能为空,至少有一条数据'),
          });
          return false;
        }
        const res = await this.SilentRulesDS.submit();
        if (!isEmpty(res) && res.failed && res.message) {
          return false;
        } else if (!isEmpty(res) && res.success) {
          this.SilentRulesDS.query();
        } else if (res === undefined) {
          notification.warning({
            message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
          });
          return false;
        } else if (res === false) {
          notification.error({
            message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
          });
          return false;
        } else {
          return false;
        }
      },
    };
    Modal.open(modalPropertys);
  }

  render() {
    const { activeKey, path } = this.state;
    return (
      <>
        <HeaderButtons
          activeKey={activeKey}
          SilentRulesDS={this.SilentRulesDS}
          InhibitionRulesDS={this.InhibitionRulesDS}
          // RouterConfigDS={this.RouterConfigDS}
          onCreateInhibitionRules={this.handleInhibitionRulesModal}
          onCreateSilentRules={this.handleSilentRulesModal}
          onCreateRouterConfig={this.handleRouterConfigModal}
          path={path}
        />
        <Content>
          <Tabs
            animated={false}
            activeKey={activeKey}
            onChange={(newActiveKey) =>
              this.setState({
                activeKey: newActiveKey,
              })
            }
          >
            <TabPane
              tab={intl.get('halt.alertAdvanced.view.title.alertAdvanced.route').d('路由配置')}
              key="RouterConfig"
            >
              <RouterConfig path={path} RouterConfigDS={this.RouterConfigDS} />
            </TabPane>
            <TabPane
              tab={intl.get('halt.alertAdvanced.view.title.alertAdvanced.inhibition').d('抑制规则')}
              key="InhibitionRules"
            >
              <InhibitionRules path={path} InhibitionRulesDS={this.InhibitionRulesDS} />
            </TabPane>
            <TabPane
              tab={intl.get('halt.alertAdvanced.view.title.alertAdvanced.silent').d('静默规则')}
              key="SilentRules"
            >
              <SilentRules path={path} SilentRulesDS={this.SilentRulesDS} />
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
