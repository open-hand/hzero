import React, { PureComponent } from 'react';
import { Radio, InputNumber, Select, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';

import styles from './index.less';
import COMMON_LANG from '@/langs/commonLang';

const { Group } = Radio;
export default class Week extends PureComponent {
  weekOptions = [
    {
      label: COMMON_LANG.MONDAY,
      value: '1',
    },
    {
      label: COMMON_LANG.TUESDAY,
      value: '2',
    },
    {
      label: COMMON_LANG.WEDNESDAY,
      value: '3',
    },
    {
      label: COMMON_LANG.THURSDAY,
      value: '4',
    },
    {
      label: COMMON_LANG.FRIDAY,
      value: '5',
    },
    {
      label: COMMON_LANG.SATURDAY,
      value: '6',
    },
    {
      label: COMMON_LANG.SUNDAY,
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
            state.type = e.target.value;
            this.props.onChange(state);
          }}
        >
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value={COMMON_LANG.EVERY_WEEK} />
            </List.Item>
            <List.Item>
              <Radio value="?" />
              <Output value={COMMON_LANG.NOT_SPECIFY} />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value={COMMON_LANG.PERIOD} />
              <Output value={COMMON_LANG.FROM} />
              &nbsp;
              <Select
                style={{ width: 100 }}
                defaultValue="1"
                placeholder={COMMON_LANG.SINGLE_WEEK}
                size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
                // 滚动区域的scrollArea由调用方设置id="scrollArea"，不设置默认滚动区域为document.body
                getPopupContainer={() => document.getElementById('scrollArea') || document.body}
              >
                {this.getWeekOptions()}
              </Select>
              &nbsp;
              <Output value={COMMON_LANG.TO} />
              &nbsp;
              <Select
                style={{ width: 100 }}
                defaultValue="2"
                placeholder={COMMON_LANG.SINGLE_WEEK}
                value={end}
                size="small"
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
              <Output value={COMMON_LANG.CERTAIN} />
              &nbsp;
              <InputNumber
                style={{ width: 120 }}
                min={1}
                defaultValue={1}
                placeholder={COMMON_LANG.SINGLE_WEEK}
                size="small"
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.THE_WEEK} />
              &nbsp;
              <Select
                style={{ width: 100 }}
                defaultValue="1"
                placeholder={COMMON_LANG.WEEK_DAY}
                value={beginEvery}
                size="small"
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
              <Output value={COMMON_LANG.LAST_ONE_MONTH} />
              &nbsp;
              <Select
                style={{ width: 100 }}
                defaultValue={1}
                placeholder={COMMON_LANG.WEEK_DAY}
                size="small"
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
              <Output value={COMMON_LANG.SPECIFY} />
              &nbsp;
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
