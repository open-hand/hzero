/*
 * @Description: 路由配置侧滑
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-05-18 14:32:33
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import {
  TextField,
  Form,
  Select,
  Table,
  NumberField,
  Lov,
  TextArea,
  Switch,
} from 'choerodon-ui/pro';
import { Divider } from 'choerodon-ui';
import intl from 'utils/intl';
import notification from 'utils/notification';

export default class EditModal extends Component {
  get columns() {
    return [
      {
        name: 'matchCode',
        editor: () => {
          return <TextField />;
        },
      },
      {
        name: 'matchValue',
        editor: () => {
          return <TextField />;
        },
      },
      {
        name: 'matchRegexFlag',
        editor: () => {
          return <Select />;
        },
      },
    ];
  }

  componentWillUnmount() {
    const { RouterConfigDS } = this.props;
    RouterConfigDS.queryParameter = {};
  }

  handleClick = () => {
    const { RouterConfigDS } = this.props;
    if (
      RouterConfigDS.children.alertMatchList.length ===
      RouterConfigDS.children.alertMatchList.selected.length
    ) {
      notification.warning({
        message: '匹配条件不能为空,至少有一条数据',
      });
      return false;
    } else {
      RouterConfigDS.children.alertMatchList.delete(
        RouterConfigDS.children.alertMatchList.selected
      );
    }
  };

  render() {
    const { RouterConfigDS } = this.props;
    return (
      <React.Fragment>
        <Form dataSet={RouterConfigDS} columns={2}>
          <TextField
            name="alertRouteCode"
            colSpan={2}
            disabled={RouterConfigDS.current?.get('alertRouteId')}
          />
          <Lov name="sendConfig" colSpan={2} />
          <Lov name="receiverGroup" colSpan={2} />
          <TextArea name="remark" colSpan={2} />
        </Form>
        <Divider orientation="left">
          <h3>{intl.get('halt.alertAdvanced.view.title.match.rule').d('匹配条件')}</h3>
        </Divider>
        <Form dataSet={RouterConfigDS}>
          <Switch name="continueFlag" />
        </Form>
        <Table
          buttons={['add', ['delete', { onClick: this.handleClick }]]}
          dataSet={RouterConfigDS.children.alertMatchList}
          queryBar="none"
          columns={this.columns}
        />
        <Divider orientation="left">
          <h3>{intl.get('halt.alertAdvanced.view.title.group.rule').d('分组规则')}</h3>
        </Divider>
        <Form dataSet={RouterConfigDS} columns={2}>
          <TextField name="groupBy" colSpan={2} />
          <NumberField
            name="groupWait"
            addonAfter={intl.get('halt.alertAdvanced.view.title.second').d('秒')}
          />
          <NumberField
            name="groupInterval"
            addonAfter={intl.get('halt.alertAdvanced.view.title.minute').d('分钟')}
          />
          <NumberField
            name="repeatInterval"
            addonAfter={intl.get('halt.alertAdvanced.view.title.hour').d('小时')}
          />
        </Form>
      </React.Fragment>
    );
  }
}
