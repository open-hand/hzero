import React, { PureComponent } from 'react';
import { Radio, InputNumber, List } from 'choerodon-ui';
import { Output } from 'choerodon-ui/pro';
import styles from './index.less';
import COMMON_LANG from '@/langs/commonLang';

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
              <Output value={COMMON_LANG.NOT_SPECIFY} />
            </List.Item>
            <List.Item>
              <Radio value="*" />
              <Output value={COMMON_LANG.EVERY_YEAR} />
            </List.Item>
            <List.Item>
              <Radio value="period" />
              <Output value={COMMON_LANG.PERIOD} />
              &nbsp;
              <InputNumber
                min={0}
                defaultValue={2018}
                value={start}
                placeholder={COMMON_LANG.SINGLE_YEAR}
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
                placeholder={COMMON_LANG.SINGLE_YEAR}
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
