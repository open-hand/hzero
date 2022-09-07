/**
 * 日志告警表单
 * @date: 2020-4-1
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import intl from 'utils/intl';
import { Form, Select, TextField, NumberField, Switch, TextArea } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

export default class LoggingForm extends Component {
  // 切换触发类型
  @Bind()
  handleChangeLogAlertType() {
    const { ds, onUpdate } = this.props;
    ds.current.set('compareValueList', '');
    ds.current.set('compareValueList', '');
    onUpdate();
  }

  // 切换日志索引
  @Bind()
  handleChangeIndexPattern() {
    const { ds, onUpdate } = this.props;
    ds.current.set('compareKey', null);
    onUpdate();
  }

  render() {
    const { ds, onUpdate } = this.props;
    const logAlertType = ds.current && ds.current.get('logAlertType');
    const indexPattern = ds.current && ds.current.get('indexPattern');
    return (
      <Form dataSet={ds} columns={2}>
        <Select name="logAlertType" onChange={this.handleChangeLogAlertType} />
        <NumberField
          name="timeframe"
          addonAfter={intl.get('halt.alertRule.view.message.minute').d('分钟')}
        />
        <Select name="indexPattern" searchable onChange={this.handleChangeIndexPattern} />
        {['blacklist', 'whitelist'].includes(logAlertType) && (
          <Select name="compareKey" searchable disabled={!indexPattern} />
        )}
        <Select name="timestampField" />
        <Select name="timestampType" onChange={onUpdate} />
        {ds.current && ds.current.get('timestampType') === 'custom' && (
          <TextField name="timestampFormat" />
        )}
        {logAlertType === 'whitelist' && <Switch name="ignoreNull" />}
        {['blacklist', 'whitelist'].includes(logAlertType) && (
          <TextField
            label={
              logAlertType === 'whitelist'
                ? intl.get('halt.alertRule.view.message.placeholder.whitelist').d('值不在此白名单')
                : intl.get('halt.alertRule.view.message.placeholder.blacklist').d('值在此黑名单')
            }
            name="compareValueList"
            colSpan={2}
            newLine
          />
        )}
        {['frequency', 'spike'].includes(logAlertType) && (
          <TextArea name="filterDsl" resize="vertical" newLine colSpan={2} />
        )}
        {logAlertType === 'frequency' && (
          <NumberField
            name="numEvents"
            addonAfter={intl.get('halt.alertRule.view.message.time').d('次')}
          />
        )}
        {logAlertType === 'spike' && <Select name="spikeType" />}
        {logAlertType === 'spike' && <NumberField name="spikeHeight" />}
        {logAlertType === 'spike' && (
          <NumberField
            name="thresholdRef"
            addonAfter={intl.get('halt.alertRule.view.message.time').d('次')}
          />
        )}
        {logAlertType === 'spike' && (
          <NumberField
            name="thresholdCur"
            addonAfter={intl.get('halt.alertRule.view.message.time').d('次')}
          />
        )}
      </Form>
    );
  }
}
