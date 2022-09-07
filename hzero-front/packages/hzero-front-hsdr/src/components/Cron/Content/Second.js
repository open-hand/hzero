import React, { PureComponent } from 'react';
import { Radio, InputNumber, message, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';

const { Group } = Radio;
export default class Second extends PureComponent {
  constructor(props) {
    super(props);
    this.formatSecondOptions();
  }

  formatSecondOptions() {
    this.secondOptions = [];
    for (let x = 0; x <= 59; x++) {
      this.secondOptions.push({
        label: x < 10 ? `0${x}` : x,
        value: `${x}`,
      });
    }
  }

  changeParams(type, value) {
    const state = { ...this.props.second };
    state[type] = value;
    this.props.onChange(state);
  }

  render() {
    const {
      second: { type, start, end, begin, beginEvery, some },
    } = this.props;
    return (
      <div>
        <Group
          value={type}
          onChange={(e) => {
            const state = { ...this.props.second };
            if (e.target.value === 'some') {
              state.some = ['0'];
            }
            if (e.target.value === 'period') {
              state.start = 1;
              state.end = 59;
            }
            if (e.target.value === 'beginInterval') {
              state.begin = 0;
              state.beginEvery = 1;
            }
            state.type = e.target.value;
            this.props.onChange(state);
          }}
        >
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value="每秒" />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value="周期" />
              <Output value="从 &nbsp;" />
              <InputNumber
                min={0}
                max={59}
                defaultValue={0}
                style={{ width: 100 }}
                placeholder="秒"
                size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              <Output value="&nbsp;到&nbsp;" />
              <InputNumber
                min={0}
                max={59}
                defaultValue={59}
                style={{ width: 100 }}
                placeholder="秒"
                value={end}
                size="small"
                onChange={(value) => {
                  this.changeParams('end', value);
                }}
              />
            </List.Item>
            <List.Item>
              <Radio value="beginInterval" />
              <Output value="从第 &nbsp;" />
              <InputNumber
                min={0}
                defaultValue={0}
                placeholder="秒"
                size="small"
                style={{ width: 100 }}
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              <Output value=" &nbsp;秒开始，每 &nbsp;" />
              <InputNumber
                min={0}
                defaultValue={1}
                placeholder="秒"
                size="small"
                value={beginEvery}
                style={{ width: 100 }}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              <Output value="&nbsp;秒执行一次" />
            </List.Item>
            <List.Item>
              <Radio value="some" />
              <Output value="指定&nbsp;" style={{ whiteSpace: 'nowrap' }} />
              <Checkbox.Group
                value={some}
                onChange={(value) => {
                  if (value.length < 1) {
                    return message.warn('至少选择一项');
                  }
                  this.changeParams('some', value);
                }}
                options={this.secondOptions}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
