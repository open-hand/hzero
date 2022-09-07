/**
 *节点详情
 * @author baitao.huang@hand-china.com
 * @date 2020/5/27
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Slider, Card } from 'choerodon-ui';
import {
  Form,
  TextField,
  DataSet,
  TextArea,
  Icon,
  Select,
  Switch,
  SelectBox,
  Lov,
} from 'choerodon-ui/pro';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import { withPropsAPI } from 'gg-editor';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import { ORCH_PRIORITY_NUM, WORK_GROUP } from '@/constants/constants';
import QuestionPopover from '@/components/QuestionPopover';
import { commonNodeFormDS } from '@/stores/Orchestration/orchestrationDS';
import styles from './index.less';

const marks = {
  0: {
    style: {
      color: 'rgb(42, 135, 52)',
    },
    label: <strong>{ORCHESTRATION_LANG.LOWEST}</strong>,
  },
  25: {
    style: {
      color: 'rgb(42, 135, 52)',
    },
    label: <strong>{ORCHESTRATION_LANG.LOW}</strong>,
  },
  50: {
    style: {
      color: 'rgb(234, 125, 36)',
    },
    label: <strong>{ORCHESTRATION_LANG.MEDIUM}</strong>,
  },
  75: {
    style: {
      color: 'rgb(255, 0, 0)',
    },
    label: <strong>{ORCHESTRATION_LANG.HIGH}</strong>,
  },
  100: {
    style: {
      color: 'rgb(255, 0, 0)',
    },
    label: <strong>{ORCHESTRATION_LANG.HIGHEST}</strong>,
  },
};

class CommonNodeCard extends React.Component {
  constructor(props) {
    super(props);
    this.commonNodeFormDS = new DataSet(commonNodeFormDS());
    props.onRef(this);
    this.state = {
      showTimeout: false,
      priority: 50,
    };
  }

  componentDidMount() {
    if (!isEmpty(this.nodeItem.model)) {
      this.init();
    } else {
      this.commonNodeFormDS.create();
    }
  }

  /**
   * 获取当前节点
   */
  get nodeItem() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0] || {};
  }

  /**
   * 数据加载
   */
  init() {
    const { params, taskInstancePriority, timeout = {}, ...other } = this.nodeItem.model;
    const { strategy } = timeout;
    const formData = {
      ...other,
      ...timeout,
      strategy: isEmpty(strategy) ? [] : strategy.split(','),
      taskInstancePriority: ORCH_PRIORITY_NUM[taskInstancePriority],
    };
    this.commonNodeFormDS.create(formData);
    this.setState({
      showTimeout: timeout.alertFlag,
      priority: ORCH_PRIORITY_NUM[taskInstancePriority],
    });
  }

  @Bind()
  handleWarningChange(value) {
    this.setState({ showTimeout: value });
  }

  /**
   * 优先级tip渲染
   */
  @Bind()
  tipFormatter(value) {
    return marks[value].label;
  }

  /**
   * 优先级
   */
  @Bind()
  handlePriorityChange(value) {
    this.setState({ priority: value });
    this.commonNodeFormDS.current.set('taskInstancePriority', value);
  }

  render() {
    const { disabledFlag } = this.props;
    const { showTimeout, priority } = this.state;
    return (
      <Card
        title={
          <h3>
            {
              <QuestionPopover
                text={ORCHESTRATION_LANG.BASIC}
                message={ORCHESTRATION_LANG.BASIC_TIP}
              />
            }
          </h3>
        }
      >
        <Form dataSet={this.commonNodeFormDS} labelWidth={120} columns={2} disabled={disabledFlag}>
          <TextField
            name="name"
            colSpan={2}
            label={
              <QuestionPopover
                text={ORCHESTRATION_LANG.TASK_NAME}
                message={ORCHESTRATION_LANG.TASK_NAME_TIP}
              />
            }
          />
          <TextArea name="description" colSpan={2} />
          <Select
            name="workGroup"
            label={
              <QuestionPopover
                text={ORCHESTRATION_LANG.WORKER_GROUP}
                message={ORCHESTRATION_LANG.WORKER_GROUP_TIP}
              />
            }
            colSpan={2}
          >
            {WORK_GROUP.map((item) => (
              <Select.Option value={item.value}>{item.meaning}</Select.Option>
            ))}
          </Select>
          <Slider
            name="taskInstancePriority"
            marks={marks}
            step={null}
            range={false}
            tipFormatter={this.tipFormatter}
            colSpan={2}
            style={{ width: '89%' }}
            className={styles['horc-slider']}
            value={priority}
            disabled={disabledFlag}
            onChange={this.handlePriorityChange}
          />
          <TextField
            name="retryTimes"
            label={
              <QuestionPopover
                text={ORCHESTRATION_LANG.RETRY_TIMES}
                message={ORCHESTRATION_LANG.RETRY_TIMES_TIP}
              />
            }
            restrict="0-99"
            addonAfter={ORCHESTRATION_LANG.TIMES}
            addonBefore={<Icon type="crop_free" />}
          />
          <TextField
            name="retryInterval"
            restrict="0-99999"
            label={
              <QuestionPopover
                text={ORCHESTRATION_LANG.RETRY_INTERVAL}
                message={ORCHESTRATION_LANG.RETRY_INTERVAL_TIP}
              />
            }
            addonAfter={ORCHESTRATION_LANG.MINUTES}
            addonBefore={<Icon type="av_timer" />}
          />
          <Select
            name="threadMechanism"
            label={
              <QuestionPopover
                text={ORCHESTRATION_LANG.THREAD_MECHANISM}
                message={ORCHESTRATION_LANG.THREAD_MECHANISM_TIP}
              />
            }
          />
          <Select
            name="failureStrategy"
            label={
              <QuestionPopover
                text={ORCHESTRATION_LANG.FAILURE_STRATEGY}
                message={ORCHESTRATION_LANG.FAILURE_STRATEGY_TIP}
              />
            }
          />
          <Switch
            label={
              <QuestionPopover
                text={ORCHESTRATION_LANG.ALERT_FLAG}
                message={ORCHESTRATION_LANG.ALERT_FLAG_TIP}
              />
            }
            name="alertFlag"
            onChange={this.handleWarningChange}
          />
          {showTimeout && (
            <Lov
              name="alertCodeLov"
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.ALERT_CODE}
                  message={ORCHESTRATION_LANG.ALERT_CODE_TIP}
                />
              }
            />
          )}
          {showTimeout && (
            <SelectBox
              name="strategy"
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.TIMEOUT_STRATEGY}
                  message={ORCHESTRATION_LANG.TIMEOUT_STRATEGY_TIP}
                />
              }
              multiple
            >
              <SelectBox.Option value="WARN">
                {ORCHESTRATION_LANG.TIMEOUT_STRATEGY_WARNING}
              </SelectBox.Option>
              // TODO: 注释掉失败选项
              {/* <SelectBox.Option value="FAILED">
                {ORCHESTRATION_LANG.TIMEOUT_STRATEGY_FAILURE}
              </SelectBox.Option> */}
            </SelectBox>
          )}
          {showTimeout && (
            <TextField
              name="interval"
              restrict="0-9"
              addonAfter={ORCHESTRATION_LANG.SECONDS}
              addonBefore={<Icon type="av_timer" />}
            />
          )}
        </Form>
      </Card>
    );
  }
}
export default withPropsAPI(CommonNodeCard);
