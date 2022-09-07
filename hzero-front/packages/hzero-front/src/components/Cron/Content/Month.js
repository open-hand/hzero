import React, { PureComponent } from 'react';
import { Radio, InputNumber, List, Checkbox } from 'antd';

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
      options.push({ label: `${i}月`, value: `${i}` });
    }
    return options;
  }

  changeType = (e) => {
    const state = { ...this.props.month };
    // if (e.target.value === 'some') {
    //   state.some = ['1'];
    // }
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
          <List size="small" bordered>
            <List.Item>
              <Radio value="*">每月</Radio>
            </List.Item>
            <List.Item>
              <Radio value="?">不指定</Radio>
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period">周期</Radio>
              从&nbsp;{' '}
              <InputNumber
                min={1}
                max={12}
                defaultValue={1}
                placeholder="月"
                size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />{' '}
              &nbsp;到&nbsp;
              <InputNumber
                min={1}
                max={12}
                defaultValue={12}
                placeholder="月"
                value={end}
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
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              &nbsp;日开始， 每 &nbsp;
              <InputNumber
                min={1}
                max={12}
                defaultValue={12}
                placeholder="月"
                value={beginEvery}
                size="small"
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              &nbsp;月执行一次
            </List.Item>
            <List.Item>
              <Radio value="some">指定</Radio>
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
