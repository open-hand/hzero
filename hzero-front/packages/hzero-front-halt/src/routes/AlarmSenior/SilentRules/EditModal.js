/*
 * @Description: 静默规则侧滑
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-24 14:02:31
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { TextField, Form, TextArea, Table, DatePicker, Select } from 'choerodon-ui/pro';
import { Divider } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import intl from 'utils/intl';

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
      alarmSeniorDS.children.targetAlertMatchList.length ===
      alarmSeniorDS.children.targetAlertMatchList.selected.length
    ) {
      notification.warning({
        message: intl
          .get('halt.alertAdvanced.validation.messsage.silent.notNull')
          .d('被静默警报匹配规则不能为空,至少有一条数据'),
      });
      return false;
    } else {
      this.props.alarmSeniorDS.children.targetAlertMatchList.delete(
        this.props.alarmSeniorDS.children.targetAlertMatchList.selected
      );
    }
  };

  handleCreate = () => {
    const { alarmSeniorDS } = this.props;
    alarmSeniorDS.children.targetAlertMatchList.create({});
  };

  render() {
    const { alarmSeniorDS } = this.props;
    return (
      <React.Fragment>
        <Form dataSet={alarmSeniorDS} columns={2}>
          <TextField
            name="alertSilenceCode"
            disabled={alarmSeniorDS.current?.get('alertSilenceId')}
          />
          <TextField name="alertSilenceName" />
          <DatePicker mode="dateTime" name="startTime" />
          <DatePicker mode="dateTime" name="endTime" />
          <TextArea name="remark" colSpan={2} />
        </Form>
        <Divider orientation="left">
          <h3>
            {intl.get('halt.alertAdvanced.view.title.silent.match.rule').d('被静默警报匹配规则')}
          </h3>
        </Divider>
        <Table
          buttons={[
            ['add', { onClick: this.handleCreate }],
            ['delete', { onClick: this.handleClick }],
          ]}
          dataSet={alarmSeniorDS.children.targetAlertMatchList}
          queryBar="none"
          columns={this.columns}
        />
      </React.Fragment>
    );
  }
}
