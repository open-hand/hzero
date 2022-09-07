import React, { PureComponent } from 'react';
import { Radio, InputNumber, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';

const { Group } = Radio;
export default class Day extends PureComponent {
  constructor(props) {
    super(props);
    this.formatDayOptions();
  }

  formatDayOptions() {
    this.dayOptions = [];
    for (let x = 1; x < 32; x++) {
      this.dayOptions.push({
        label: x < 10 ? `0${x}` : x,
        value: `${x}`,
      });
    }
  }

  changeParams(type, value) {
    const state = { ...this.props.day };
    state[type] = value;
    this.props.onChange(state);
  }

  changeType = (e) => {
    const state = { ...this.props.month };
    if (e.target.value === 'some') {
      state.some = ['1'];
    }
    if (e.target.value === 'period') {
      state.start = 1;
      state.end = 31;
    }
    if (e.target.value === 'beginInterval') {
      state.begin = 0;
      state.beginEvery = 1;
    }
    if (e.target.value === 'closeWorkDay') {
      state.closeWorkDay = 1;
    }
    if (e.target.value === 'last') {
      state.last = 1;
    }
    state.type = e.target.value;
    this.props.onChange(state);
  };

  render() {
    const {
      day: { type, start, end, some, begin, beginEvery, last, closeWorkDay },
    } = this.props;
    return (
      <div>
        <Group value={type} onChange={this.changeType}>
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value="每日" />
            </List.Item>
            <List.Item>
              <Radio value="?" />
              <Output value="不指定" />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value="周期" />
              <Output value="从&nbsp;" />
              <InputNumber
                min={1}
                max={31}
                defaultValue={1}
                style={{ width: 100 }}
                placeholder="日"
                size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              <Output value="&nbsp;到&nbsp;" />
              <InputNumber
                min={1}
                max={31}
                defaultValue={2}
                style={{ width: 100 }}
                placeholder="日"
                value={end}
                size="small"
                onChange={(value) => {
                  this.changeParams('end', value);
                }}
              />
            </List.Item>
            <List.Item>
              <Radio value="beginInterval" />
              <Output value="从第&nbsp;" />
              <InputNumber
                min={1}
                defaultValue={1}
                placeholder="日"
                size="small"
                style={{ width: 100 }}
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              <Output value="&nbsp;日开始，每&nbsp;" />
              <InputNumber
                min={1}
                defaultValue={1}
                placeholder="天"
                size="small"
                style={{ width: 100 }}
                value={beginEvery}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              <Output value="&nbsp;天执行一次" />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="closeWorkDay" />
              <Output value="每月&nbsp;" />
              <InputNumber
                min={1}
                defaultValue={1}
                placeholder="日"
                size="small"
                style={{ width: 100 }}
                value={closeWorkDay}
                onChange={(value) => {
                  this.changeParams('closeWorkDay', value);
                }}
              />
              <Output value="&nbsp;日最近的那个工作日" />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="last" />
              <Output value="本月最后&nbsp;" />
              <InputNumber
                min={0}
                placeholder="天"
                size="small"
                style={{ width: 100 }}
                value={last}
                onChange={(value) => {
                  this.changeParams('last', value);
                }}
              />
              <Output value="&nbsp;天" />
            </List.Item>
            <List.Item>
              <Radio value="some" />
              <Output value="指定&nbsp;" style={{ whiteSpace: 'nowrap' }} />
              <Checkbox.Group
                value={some}
                onChange={(value) => {
                  this.changeParams('some', value);
                }}
                options={this.dayOptions}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
