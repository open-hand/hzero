/**
 * 头弹窗
 * @author baitao.huang@hand-china.com
 * @date 2020/5/27
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, TextField, DataSet, TextArea, Icon, Switch, Lov } from 'choerodon-ui/pro';
import { headerFormDS } from '@/stores/Orchestration/orchestrationDS';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import QuestionPopover from '@/components/QuestionPopover';
import { joinField, joinUpperField } from '@/utils/utils';

class HeaderModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      timeOutFlag: false,
      // utils中的joinField会使用到，不可删除！！！
      // eslint-disable-next-line react/no-unused-state
      orchType: props.orchType,
    };
    this.headerFormDS = new DataSet({
      ...headerFormDS({ ref: this }),
    });
    props.onRef(this);
  }

  componentDidMount() {
    this.init();
  }

  init() {
    const { graph } = this.props;
    const { remark, alertCode, timeout = 0 } = graph.toData();
    const name = graph.toData()[joinField(this, 'name')];
    this.headerFormDS.create({
      remark,
      timeout,
      alertCode,
      [joinField(this, 'name')]: name,
      timeoutFlag: timeout > 0,
    });
    this.setState({ timeOutFlag: timeout > 0 });
  }

  render() {
    const { timeOutFlag } = this.state;
    return (
      <Form dataSet={this.headerFormDS} labelWidth={120} columns={2}>
        <TextField
          name={joinField(this, 'name')}
          label={
            <QuestionPopover
              text={ORCHESTRATION_LANG[joinUpperField(this, 'name')]}
              message={ORCHESTRATION_LANG.ORC_NAME_TIP}
            />
          }
          colSpan={2}
        />
        <TextArea name="remark" colSpan={2} />
        <Switch name="timeoutFlag" onChange={(value) => this.setState({ timeOutFlag: value })} />
        {timeOutFlag && <Lov newLine name="alertCodeLov" />}
        {timeOutFlag && (
          <TextField
            restrict="0-9"
            addonAfter={ORCHESTRATION_LANG.SECONDS}
            name="timeout"
            colSpan={2}
            addonBefore={<Icon type="av_timer" />}
          />
        )}
      </Form>
    );
  }
}
export default HeaderModal;
