import React, { PureComponent } from 'react';
import { Radio, InputNumber, message, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';
import COMMON_LANG from '@/langs/commonLang';

const { Group } = Radio;
export default class Hour extends PureComponent {
  constructor(props) {
    super(props);
    this.formatHourOptions();
  }

  formatHourOptions() {
    this.hourOptions = [];
    for (let x = 0; x < 24; x++) {
      this.hourOptions.push({
        label: x < 10 ? `0${x}` : x,
        value: `${x}`,
      });
    }
  }

  changeParams(type, value) {
    const state = { ...this.props.hour };
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
      hour: { type, start, end, begin, some, beginEvery },
    } = this.props;
    return (
      <div>
        <Group value={type} onChange={this.changeType}>
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value={COMMON_LANG.EVERY_HOUR} />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value={COMMON_LANG.PERIOD} />
              <Output value={COMMON_LANG.FROM} />
              &nbsp;
              <InputNumber
                min={0}
                max={24}
                defaultValue={0}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.SINGLE_HOUR}
                size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.TO} />
              &nbsp;
              <InputNumber
                min={0}
                max={24}
                defaultValue={24}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.SINGLE_HOUR}
                value={end}
                size="small"
                onChange={(value) => {
                  this.changeParams('end', value);
                }}
              />
            </List.Item>
            <List.Item>
              <Radio value="beginInterval" />
              <Output value={COMMON_LANG.FROM_CERTAIN} />
              &nbsp;
              <InputNumber
                min={0}
                max={24}
                defaultValue={0}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.SINGLE_HOUR}
                size="small"
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.PER_FROM_HOUR} />
              &nbsp;
              <InputNumber
                min={0}
                max={24}
                defaultValue={1}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.SINGLE_HOUR}
                size="small"
                value={beginEvery}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.ONE_TIME_FOR_HOUR} />
            </List.Item>
            <List.Item>
              <Radio value="some" />
              <Output value={COMMON_LANG.SPECIFY} style={{ whiteSpace: 'nowrap' }} />
              &nbsp;
              <Checkbox.Group
                value={some}
                onChange={(value) => {
                  if (value.length < 1) {
                    return message.warn(COMMON_LANG.MIN_NUM);
                  }
                  this.changeParams('some', value);
                }}
                options={this.hourOptions}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
