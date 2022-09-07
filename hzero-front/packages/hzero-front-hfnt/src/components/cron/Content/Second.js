import React, { PureComponent } from 'react';
import { Radio, InputNumber, message, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';
import COMMON_LANG from '@/langs/commonLang';

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
            state.type = e.target.value;
            this.props.onChange(state);
          }}
        >
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value={COMMON_LANG.EVERY_SECOND} />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value={COMMON_LANG.PERIOD} />
              <Output value={COMMON_LANG.FROM} />
              &nbsp;
              <InputNumber
                min={0}
                max={59}
                defaultValue={0}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.SINGLE_SECOND}
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
                max={59}
                defaultValue={59}
                style={{ width: 100 }}
                placeholder={COMMON_LANG.SINGLE_SECOND}
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
                defaultValue={0}
                placeholder={COMMON_LANG.SINGLE_SECOND}
                size="small"
                style={{ width: 100 }}
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.PER_FROM_SECOND} />
              &nbsp;
              <InputNumber
                min={0}
                defaultValue={1}
                placeholder={COMMON_LANG.SINGLE_SECOND}
                size="small"
                value={beginEvery}
                style={{ width: 100 }}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              &nbsp;
              <Output value={COMMON_LANG.ONE_TIME_FOR_SECOND} />
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
                options={this.secondOptions}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
