import React, { PureComponent } from 'react';
import { Radio, InputNumber, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';

const { Group } = Radio;
export default class Month extends PureComponent {
  changeParams(type, value) {
    const state = { ...this.props.month };
    state[type] = value;
    this.props.onChange(state);
  }

  eachMonthOptions() {
    const options = [];
    for (let i = 1; i < 13; i++) {
      const temp = i < 10 ? `0${i}` : i;
      options.push({ label: `${temp}月`, value: `${i}` });
    }
    return options;
  }

  changeType = (e) => {
    const state = { ...this.props.month };
    if (e.target.value === 'some') {
      state.some = ['1'];
    }
    if (e.target.value === 'period') {
      state.start = 1;
      state.end = 12;
    }
    if (e.target.value === 'beginInterval') {
      state.begin = 0;
      state.beginEvery = 1;
    }
    state.type = e.target.value;
    this.props.onChange(state);
  };

  render() {
    const {
      month: { type, start, end, beginEvery, begin, some },
    } = this.props;
    return (
      <div>
        <Group value={type} onChange={this.changeType}>
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value="每月" />
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
                max={12}
                defaultValue={1}
                placeholder="月"
                size="small"
                style={{ width: 100 }}
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              <Output value="&nbsp;到&nbsp;" />
              <InputNumber
                min={1}
                max={12}
                defaultValue={12}
                placeholder="月"
                value={end}
                style={{ width: 100 }}
                size="small"
                onChange={(value) => {
                  this.changeParams('end', value);
                }}
              />
            </List.Item>
            <List.Item>
              <Radio value="beginInterval" />
              <InputNumber
                min={1}
                max={12}
                defaultValue={1}
                placeholder="天"
                size="small"
                style={{ width: 100 }}
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              <Output value="&nbsp;日开始，每 &nbsp;" />
              <InputNumber
                min={1}
                max={12}
                defaultValue={12}
                placeholder="月"
                value={beginEvery}
                size="small"
                style={{ width: 100 }}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              <Output value="&nbsp;月执行一次" />
            </List.Item>
            <List.Item>
              <Radio value="some" />
              <Output value="指定&nbsp;" style={{ whiteSpace: 'nowrap' }} />
              <Checkbox.Group
                value={some}
                onChange={(value) => {
                  this.changeParams('some', value);
                }}
                options={this.eachMonthOptions()}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
