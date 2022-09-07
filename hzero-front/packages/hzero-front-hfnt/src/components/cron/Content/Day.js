import React, { PureComponent } from 'react';
import { Radio, InputNumber, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import COMMON_LANG from '@/langs/commonLang';
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
              <Output value={COMMON_LANG.EVERY_DAY} />
            </List.Item>
            <List.Item>
              <Radio value="?" />
              <Output value={COMMON_LANG.NOT_SPECIFY} />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value={COMMON_LANG.PERIOD} />
              <Output value={`${COMMON_LANG.FROM} `} />
              <InputNumber
                min={1}
                max={31}
                defaultValue={1}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.DAY}
                size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              <Output value={` ${COMMON_LANG.TO} `} />
              <InputNumber
                min={1}
                max={31}
                defaultValue={2}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.DAY}
                value={end}
                size="small"
                onChange={(value) => {
                  this.changeParams('end', value);
                }}
              />
            </List.Item>
            <List.Item>
              <Radio value="beginInterval" />
              <Output value={`${COMMON_LANG.FROM_CERTAIN} `} />
              <InputNumber
                min={1}
                defaultValue={1}
                placeholder={COMMON_LANG.DAY}
                size="small"
                style={{ width: 100 }}
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              <Output value={` ${COMMON_LANG.PER_DAY_START} `} />
              <InputNumber
                min={1}
                defaultValue={1}
                placeholder={COMMON_LANG.DAY}
                size="small"
                style={{ width: 100 }}
                value={beginEvery}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              <Output value={` ${COMMON_LANG.ONE_TIME_FOR_DAY}`} />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="closeWorkDay" />
              <Output value={`${COMMON_LANG.EVERY_MONTH} `} />
              <InputNumber
                min={1}
                defaultValue={1}
                placeholder={COMMON_LANG.DAY}
                size="small"
                style={{ width: 100 }}
                value={closeWorkDay}
                onChange={(value) => {
                  this.changeParams('closeWorkDay', value);
                }}
              />
              <Output value={` ${COMMON_LANG.LASTEST_WORK_DAY}`} />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="last" />
              <Output value={`${COMMON_LANG.LAST_THE_MONTH} `} />
              <InputNumber
                min={0}
                placeholder={COMMON_LANG.DAY}
                size="small"
                style={{ width: 100 }}
                value={last}
                onChange={(value) => {
                  this.changeParams('last', value);
                }}
              />
              <Output value={` ${COMMON_LANG.DAY}`} />
            </List.Item>
            <List.Item>
              <Radio value="some" />
              <Output value={`${COMMON_LANG.SPECIFY} `} style={{ whiteSpace: 'nowrap' }} />
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
