import React, { PureComponent } from 'react';
import { Radio, InputNumber, List } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';

const { Group } = Radio;
export default class Year extends PureComponent {
  changeParams(type, value) {
    const state = { ...this.props.year };
    state[type] = value;
    this.props.onChange(state);
  }

  render() {
    const {
      year: { type, start, end },
    } = this.props;
    return (
      <div>
        <Group
          value={type}
          onChange={(e) => {
            this.changeParams('type', e.target.value);
          }}
          defaultValue=""
        >
          <List size="small" bordered className={styles['cron-list']}>
            <List.Item>
              <Radio value="" />
              <Output value="不指定" />
            </List.Item>
            <List.Item>
              <Radio value="*" />
              <Output value="每年" />
            </List.Item>
            <List.Item>
              <Radio value="period" />
              <Output value="周期&nbsp;" />
              <InputNumber
                min={0}
                defaultValue={2018}
                value={start}
                placeholder="年"
                size="small"
                style={{ width: 100 }}
                onChange={(value) => {
                  this.changeParams('start', value);
                }}
              />
              <Output value="&nbsp;-&nbsp;" />
              <InputNumber
                min={0}
                defaultValue={2019}
                value={end}
                placeholder="年"
                size="small"
                style={{ width: 100 }}
                onChange={(value) => {
                  this.changeParams('end', value);
                }}
              />
            </List.Item>
          </List>
        </Group>
      </div>
    );
  }
}
