import React, { PureComponent } from 'react';
import { Radio, InputNumber, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';
import COMMON_LANG from '@/langs/commonLang';

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
      options.push({ label: `${temp}${COMMON_LANG.SINGLE_MONTH}`, value: `${i}` });
    }
    return options;
  }

  changeType = (e) => {
    const state = { ...this.props.month };
    if (e.target.value === 'some') {
      state.some = ['1'];
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
              <Output value={COMMON_LANG.EVERY_MONTH} />
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
              <InputNumber
                min={1}
                max={12}
                defaultValue={1}
                placeholder={COMMON_LANG.SINGLE_MONTH}
                size="small"
                style={{ width: 100 }}
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.TO} />
              &nbsp;
              <InputNumber
                min={1}
                max={12}
                defaultValue={12}
                placeholder={COMMON_LANG.SINGLE_MONTH}
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
                placeholder={COMMON_LANG.SINGLE_DAY}
                size="small"
                style={{ width: 100 }}
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.PER_DAY_START} />
              &nbsp;
              <InputNumber
                min={1}
                max={12}
                defaultValue={12}
                placeholder={COMMON_LANG.SINGLE_MONTH}
                value={beginEvery}
                size="small"
                style={{ width: 100 }}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.ONE_TIME_FOR_MONTH} />
            </List.Item>
            <List.Item>
              <Radio value="some" />
              <Output value={COMMON_LANG.SPECIFY} style={{ whiteSpace: 'nowrap' }} />
              &nbsp;
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
