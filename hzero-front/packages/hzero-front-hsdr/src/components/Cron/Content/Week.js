import React, { PureComponent } from 'react';
import { Radio, InputNumber, Select, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';

import styles from './index.less';

const { Group } = Radio;
export default class Week extends PureComponent {
  weekOptions = [
    {
      label: '星期日',
      value: '1',
    },
    {
      label: '星期一',
      value: '2',
    },
    {
      label: '星期二',
      value: '3',
    },
    {
      label: '星期三',
      value: '4',
    },
    {
      label: '星期四',
      value: '5',
    },
    {
      label: '星期五',
      value: '6',
    },
    {
      label: '星期六',
      value: '7',
    },
  ];

  getWeekOptions() {
    return this.weekOptions.map((item) => {
      return (
        <Select.Option value={item.value} key={item.value}>
          {item.label}
        </Select.Option>
      );
    });
  }

  changeParams(type, value) {
    const state = { ...this.props.week };
    state[type] = value;
    this.props.onChange(state);
  }

  render() {
    const {
      week: { type, start, end, some, begin, beginEvery, last },
    } = this.props;
    return (
      <div>
        <Group
          value={type}
          onChange={(e) => {
            const state = { ...this.props.week };
            if (e.target.value === 'some') {
              state.some = ['1'];
            }
            if (e.target.value === 'beginInterval') {
              state.begin = 1;
            }
            state.type = e.target.value;
            this.props.onChange(state);
          }}
        >
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value="每周" />
            </List.Item>
            <List.Item>
              <Radio value="?" />
              <Output value="不指定" />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value="周期" />
              <Output value="从&nbsp;" />
              <Select
                style={{ width: 100 }}
                defaultValue="1"
                placeholder="周"
                // size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
                // 滚动区域的scrollArea由调用方设置id="scrollArea"，不设置默认滚动区域为document.body
                getPopupContainer={() => document.getElementById('scrollArea') || document.body}
              >
                {this.getWeekOptions()}
              </Select>
              <Output value="&nbsp;到&nbsp;" />
              <Select
                style={{ width: 100 }}
                defaultValue="2"
                placeholder="周"
                value={end}
                // size="small"
                onChange={(value) => {
                  this.changeParams('end', value);
                }}
                getPopupContainer={() => document.getElementById('scrollArea') || document.body}
              >
                {this.getWeekOptions()}
              </Select>
            </List.Item>
            <List.Item>
              <Radio value="beginInterval" />
              <Output value="第&nbsp;" />
              <InputNumber
                style={{ width: 120 }}
                min={1}
                defaultValue={1}
                placeholder="周"
                size="small"
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              <Output value="&nbsp;周的&nbsp;" />
              <Select
                style={{ width: 100 }}
                defaultValue="1"
                placeholder="星期"
                value={beginEvery}
                // size="small"
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
                getPopupContainer={() => document.getElementById('scrollArea') || document.body}
              >
                {this.getWeekOptions()}
              </Select>
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="last" />
              <Output value="本月最后一个&nbsp;" />
              <Select
                style={{ width: 100 }}
                defaultValue={1}
                placeholder="星期"
                // size="small"
                value={last}
                onChange={(value) => {
                  this.changeParams('last', value);
                }}
                getPopupContainer={() => document.getElementById('scrollArea') || document.body}
              >
                {this.getWeekOptions()}
              </Select>
            </List.Item>
            <List.Item>
              <Radio value="some" />
              <Output value="指定&nbsp;" />
              <Checkbox.Group
                value={some}
                defaultValue={['1']}
                onChange={(value) => {
                  this.changeParams('some', value);
                }}
                options={this.weekOptions}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
