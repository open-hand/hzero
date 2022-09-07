/*
 * @Description: 抑制规则侧滑
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-24 14:02:31
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { TextField, Form, Select, TextArea, Table } from 'choerodon-ui/pro';
import { Divider } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
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

  /**
   * 清除
   * @param {object} record - 行数据
   */
  @Bind()
  handleClear(record) {
    this.props.alarmSeniorDS.remove(record);
  }

  componentWillUnmount() {
    const { alarmSeniorDS } = this.props;
    alarmSeniorDS.queryParameter = {};
  }

  handleClick = () => {
    const { alarmSeniorDS } = this.props;
    if (
      alarmSeniorDS.children.sourceAlertMatchList.length ===
      alarmSeniorDS.children.sourceAlertMatchList.selected.length
    ) {
      notification.warning({
        message: intl
          .get('halt.alertAdvanced.validation.messsage.source.notNull')
          .d('来源警报匹配规则不能为空,至少有一条数据'),
      });
      return false;
    } else {
      alarmSeniorDS.children.sourceAlertMatchList.delete(
        alarmSeniorDS.children.sourceAlertMatchList.selected
      );
    }
  };

  handleClicksourceAlert = () => {
    const { alarmSeniorDS } = this.props;
    if (
      alarmSeniorDS.children.targetAlertMatchList.length ===
      alarmSeniorDS.children.targetAlertMatchList.selected.length
    ) {
      notification.warning({
        message: intl
          .get('halt.alertAdvanced.validation.messsage.inhibition.notNull')
          .d('被抑制警报匹配规则不能为空,至少有一条数据'),
      });
      return false;
    } else {
      alarmSeniorDS.children.targetAlertMatchList.delete(
        alarmSeniorDS.children.targetAlertMatchList.selected
      );
    }
  };

  handleCreate = (type) => {
    const { alarmSeniorDS } = this.props;
    if (type === 'sourceAlertMatchList') {
      alarmSeniorDS.children.sourceAlertMatchList.create({});
    } else {
      alarmSeniorDS.children.targetAlertMatchList.create({});
    }
  };

  render() {
    const { alarmSeniorDS } = this.props;
    return (
      <React.Fragment>
        <Form dataSet={alarmSeniorDS} columns={2}>
          <TextField
            name="alertInhibitCode"
            disabled={alarmSeniorDS.current?.get('alertInhibitId')}
          />
          <TextField name="alertInhibitName" />
          <TextField name="labelMatch" colSpan={2} />
          <TextArea name="remark" colSpan={2} />
        </Form>
        <Divider orientation="left">
          <h3>{intl.get('halt.alertAdvanced.view.title.source.warning').d('来源警报匹配')}</h3>
        </Divider>
        <Table
          // buttons={['add', 'delete']}
          buttons={[
            ['add', { onClick: () => this.handleCreate('sourceAlertMatchList') }],
            ['delete', { onClick: this.handleClick }],
          ]}
          dataSet={alarmSeniorDS.children.sourceAlertMatchList}
          queryBar="none"
          columns={this.columns}
        />
        <Divider orientation="left">
          <h3>
            {intl.get('halt.alertAdvanced.view.title.inhibition.warning').d('被抑制警报匹配')}
          </h3>
        </Divider>
        <Table
          buttons={[
            ['add', { onClick: () => this.handleCreate('targetAlertMatchList') }],
            ['delete', { onClick: this.handleClicksourceAlert }],
          ]}
          dataSet={alarmSeniorDS.children.targetAlertMatchList}
          queryBar="none"
          columns={this.columns}
        />
      </React.Fragment>
    );
  }
}
