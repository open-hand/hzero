/**
 * 参数弹窗
 * @author baitao.huang@hand-china.com
 * @date 2020/8/24
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, DataSet, Output } from 'choerodon-ui/pro';
import { paramInfoDS } from '@/stores/Orchestration/orchestrationDS';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';

class ParamModal extends React.Component {
  constructor(props) {
    super(props);
    this.paramInfoDS = new DataSet(paramInfoDS());
    this.state = {};
  }

  componentDidMount() {
    const { record } = this.props;
    this.paramInfoDS.loadData([record]);
  }

  render() {
    const { type } = this.props;
    return type === 'startParamInfo' ? (
      <Form dataSet={this.paramInfoDS} columns={1}>
        <Output name="failureStrategyMeaning" />
        <Output name="warningTypeMeaning" />
        <Output name="instancePriorityMeaning" />
        <Output name="workerGroup" />
      </Form>
    ) : (
      <>
        <Form header={ORCHESTRATION_LANG.GLOBAL_PARAM}>
          <Output />
        </Form>
        <Form header={ORCHESTRATION_LANG.LOCAL_PARAM}>
          <Output />
        </Form>
      </>
    );
  }
}
export default ParamModal;
