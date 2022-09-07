/**
 * 头弹窗
 * @author baitao.huang@hand-china.com
 * @date 2020/5/27
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, DataSet, Radio, Select, Output, TextField, Lov } from 'choerodon-ui/pro';
import { executeFormDS } from '@/stores/Orchestration/definitionDS';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import { WORK_GROUP } from '@/constants/constants';

class HeaderModal extends React.Component {
  constructor(props) {
    super(props);
    this.executeFormDS = new DataSet(executeFormDS());
    props.onRef(this);
    this.state = {
      showAlertCode: false,
    };
  }

  componentDidMount() {
    const { executeRecord } = this.props;
    this.executeFormDS.create({
      definitionId: executeRecord.get('definitionId'),
      definitionName: executeRecord.get('definitionName'),
    });
  }

  render() {
    const { failureStrategyList = [] } = this.props;
    const { showAlertCode } = this.state;
    return (
      <Form dataSet={this.executeFormDS} columns={1}>
        <Output name="definitionName" />
        <div label={ORCHESTRATION_LANG.FAILED_STRATEGY} style={{ paddingTop: '3px' }}>
          {failureStrategyList.map((item) => (
            <Radio name="failureStrategy" value={item.value}>
              {item.meaning}
            </Radio>
          ))}
        </div>
        <Select
          name="warningType"
          onChange={(val) => this.setState({ showAlertCode: val !== 'NONE' })}
        />
        {showAlertCode && <Lov name="alertCodeLov" />}
        <Select name="instancePriority" />
        <Select name="workerGroup">
          {WORK_GROUP.map((item) => (
            <Select.Option value={item.value}>{item.meaning}</Select.Option>
          ))}
        </Select>
        <TextField name="preference" placeholder={ORCHESTRATION_LANG.PREFERENCE_PLACEHOLDER} />
        // TODO: 待开发
        {/* <Switch name="complement" />
        <div label={ORCHESTRATION_LANG.EXEC_METHOD} style={{ paddingTop: '3px' }}>
          <Radio name="statementType" value="serial">
            {ORCHESTRATION_LANG.SERIAL_EXEC}
          </Radio>
          <Radio name="statementType" value="parallel">
            {ORCHESTRATION_LANG.PARALLEL_EXEC}
          </Radio>
        </div>
        <DateTimePicker name="scheduleTime" /> */}
      </Form>
    );
  }
}
export default HeaderModal;
