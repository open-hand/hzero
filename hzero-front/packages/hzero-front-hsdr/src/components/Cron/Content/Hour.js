import React, { PureComponent } from 'react';
import { Radio, InputNumber, message, List, Checkbox } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';

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
    if (e.target.value === 'period') {
      state.start = 1;
      state.end = 24;
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
      hour: { type, start, end, begin, some, beginEvery },
    } = this.props;
    return (
      <div>
        <Group value={type} onChange={this.changeType}>
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="*" />
              <Output value="每小时" />
            </List.Item>
            <List.Item style={{ marginBottom: 5 }}>
              <Radio value="period" />
              <Output value="周期" />
              <Output value="从 &nbsp;" />
              <InputNumber
                min={0}
                max={24}
                defaultValue={0}
                style={{ width: 100 }}
                placeholder="时"
                size="small"
                value={start}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              <Output value="&nbsp;到&nbsp;" />
              <InputNumber
                min={0}
                max={24}
                defaultValue={24}
                style={{ width: 100 }}
                placeholder="时"
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
                min={0}
                max={24}
                defaultValue={0}
                style={{ width: 100 }}
                placeholder="时"
                size="small"
                value={begin}
                onChange={(value) => {
                  this.changeParams('begin', value);
                }}
              />
              <Output value="&nbsp;时开始，&nbsp;每&nbsp;" />
              <InputNumber
                min={0}
                max={24}
                defaultValue={1}
                style={{ width: 100 }}
                placeholder="小时"
                size="small"
                value={beginEvery}
                onChange={(value) => {
                  this.changeParams('beginEvery', value);
                }}
              />
              <Output value="&nbsp;时执行一次" />
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
                options={this.hourOptions}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
