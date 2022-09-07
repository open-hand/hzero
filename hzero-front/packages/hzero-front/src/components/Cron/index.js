/* eslint-disable prefer-destructuring */
import React, { PureComponent } from 'react';
import { Tabs, Row, Col, Input, List } from 'antd';
import Year from './Content/Year';
import Month from './Content/Month';
import Week from './Content/Week';
import Day from './Content/Day';
import Hour from './Content/Hour';
import Minute from './Content/Minute';
import Second from './Content/Second';

const { TabPane } = Tabs;
class Cron extends PureComponent {
  constructor(props) {
    super(props);
    const date = new Date();
    this.state = {
      activeKey: 'second',
      year: {
        type: '',
        start: date.getFullYear(),
        end: date.getFullYear(),
      },
      month: {
        start: '',
        end: '',
        begin: '',
        beginEvery: '',
        type: '*',
        some: [],
      },
      week: {
        start: '',
        end: '',
        last: '',
        begin: '',
        beginEvery: '',
        type: '?',
        some: [],
      },
      day: {
        last: '',
        closeWorkDay: '',
        start: '',
        end: '',
        begin: '',
        beginEvery: '',
        type: '*',
        some: [],
      },
      hour: {
        start: '',
        end: '',
        begin: '',
        beginEvery: '',
        type: '*',
        some: [],
      },
      minute: {
        start: '',
        end: '',
        begin: '',
        beginEvery: '',
        type: '*',
        some: [],
      },
      second: {
        start: '',
        end: '',
        begin: '',
        beginEvery: '',
        type: '*',
        some: [],
      },
    };
  }

  initValue() {
    const value = this.props.value ? this.props.value.toUpperCase() : '* * * * * *';
    const valuesArray = value.split(' ');
    const newState = { ...this.state };
    newState.second.value = valuesArray[0] || '';
    newState.minute.value = valuesArray[1] || '';
    newState.hour.value = valuesArray[2] || '';
    newState.day.value = valuesArray[3] || '';
    newState.month.value = valuesArray[4] || '';
    newState.week.value = valuesArray[5] || '';
    newState.year.value = valuesArray[6] || '';
    this.setState(newState, () => {
      this.parse();
    });
  }

  componentDidMount(props) {
    this.initValue(props);
  }

  componentDidUpdate(props) {
    if (props.value !== this.props.value && this.props.value) {
      this.initValue();
    }
  }

  parse() {
    const { year, month, week, day, hour, minute, second } = this.state;
    if (year.value.indexOf('-') > -1) {
      year.type = 'period';
      const period = year.value.split('-');
      year.start = period[0];
      year.end = period[1];
    } else {
      year.type = year.value;
    }
    if (week.value.indexOf('-') > -1) {
      week.type = 'period';
      const period = week.value.split('-');
      week.start = period[0];
      week.end = period[1];
    } else if (week.value.indexOf('L') > -1) {
      week.type = 'last';
      week.last = week.value.split('L')[0] || 1;
    } else if (week.value.indexOf('#') > -1) {
      week.type = 'beginInterval';
      week.begin = week.value.split('#')[0];
      week.beginEvery = week.value.split('#')[1];
    } else if (week.value.indexOf(',') > -1 || /^[0-9]+$/.test(week.value)) {
      week.type = 'some';
      week.some = week.value.split(',');
    } else {
      week.type = week.value || '?';
    }

    if (month.value.indexOf('-') > -1) {
      month.type = 'period';
      month.start = month.value.split('-')[0];
      month.end = month.value.split('-')[1];
    } else if (month.value.indexOf('/') > -1) {
      month.type = 'beginInterval';
      month.begin = month.value.split('/')[0];
      month.beginEvery = month.value.split('/')[1];
    } else if (month.value.indexOf(',') > -1 || /^[0-9]+$/.test(month.value)) {
      month.type = 'some';
      month.some = month.value.split(',');
    } else {
      month.type = month.value || '?';
    }

    if (day.value.indexOf('-') > -1) {
      day.type = 'period';
      day.start = day.value.split('-')[0];
      day.end = day.value.split('-')[1];
    } else if (day.value.indexOf('W') > -1) {
      day.type = 'closeWorkDay';
      day.closeWorkDay = day.value.split('W')[0];
    } else if (day.value.indexOf('L') > -1) {
      day.type = 'last';
      day.last = day.value.split('L')[0] || 1;
    } else if (day.value.indexOf('/') > -1) {
      day.type = 'beginInterval';
      day.begin = day.value.split('/')[0];
      day.beginEvery = day.value.split('/')[1];
    } else if (day.value.indexOf(',') > -1 || /^[0-9]+$/.test(day.value)) {
      day.type = 'some';
      day.some = day.value.split(',');
    } else {
      day.type = day.value || '?';
    }

    if (hour.value.indexOf('-') > -1) {
      hour.type = 'period';
      hour.start = hour.value.split('-')[0];
      hour.end = hour.value.split('-')[1];
    } else if (hour.value.indexOf('/') > -1) {
      hour.type = 'beginInterval';
      hour.begin = hour.value.split('/')[0];
      hour.beginEvery = hour.value.split('/')[1];
    } else if (hour.value.indexOf(',') > -1 || /^[0-9]+$/.test(hour.value)) {
      hour.type = 'some';
      hour.some = hour.value.split(',');
    } else {
      hour.type = hour.value || '?';
    }

    if (minute.value.indexOf('-') > -1) {
      minute.type = 'period';
      minute.start = minute.value.split('-')[0];
      minute.end = minute.value.split('-')[1];
    } else if (minute.value.indexOf('/') > -1) {
      minute.type = 'beginInterval';
      minute.begin = minute.value.split('/')[0];
      minute.beginEvery = minute.value.split('/')[1];
    } else if (minute.value.indexOf(',') > -1 || /^[0-9]+$/.test(minute.value)) {
      minute.type = 'some';
      minute.some = minute.value.split(',');
    } else {
      minute.type = minute.value || '?';
    }

    if (second.value.indexOf('-') > -1) {
      second.type = 'period';
      second.start = second.value.split('-')[0];
      second.end = second.value.split('-')[1];
    } else if (second.value.indexOf('/') > -1) {
      second.type = 'beginInterval';
      second.begin = second.value.split('/')[0];
      second.beginEvery = second.value.split('/')[1];
    } else if (second.value.indexOf(',') > -1 || /^[0-9]+$/.test(second.value)) {
      second.type = 'some';
      second.some = second.value.split(',');
    } else {
      second.type = second.value || '?';
    }
    this.setState({
      year: { ...year },
      month: { ...month },
      week: { ...week },
      day: { ...day },
      hour: { ...hour },
      minute: { ...minute },
      second: { ...second },
    });
  }

  format() {
    const { year, month, week, day, hour, minute, second } = this.state;
    return `${second.value} ${minute.value} ${hour.value} ${day.value} ${month.value} ${week.value} ${year.value}`;
  }

  changeState(state) {
    this.setState(state, () => {
      this.culcCron();
    });
  }

  // 计算用户的cron
  culcCron() {
    const { n2s } = this;
    const { year, month, week, day, hour, minute, second } = this.state;
    if (year.type === 'period') {
      year.value = `${n2s(year.start)}-${n2s(year.end)}`;
    } else {
      year.value = year.type;
    }
    if (month.type === 'period') {
      month.value = `${n2s(month.start)}-${n2s(month.end)}`;
    } else if (month.type === 'beginInterval') {
      month.value = `${n2s(month.begin)}/${n2s(month.beginEvery)}`;
    } else if (month.type === 'some') {
      month.value = month.some.join(',');
    } else {
      month.value = month.type;
    }
    if (week.type === 'period') {
      week.value = `${n2s(week.start)}-${n2s(week.end)}`;
    } else if (week.type === 'beginInterval') {
      week.value = `${n2s(week.begin)}#${n2s(week.beginEvery)}`;
    } else if (week.type === 'last') {
      week.value = `${n2s(week.last)}L`;
    } else if (week.type === 'some') {
      week.value = week.some.join(',');
    } else {
      week.value = week.type;
    }
    if (day.type === 'period') {
      day.value = `${n2s(day.start)}-${n2s(day.end)}`;
    } else if (day.type === 'beginInterval') {
      day.value = `${n2s(day.begin)}/${n2s(day.beginEvery)}`;
    } else if (day.type === 'closeWorkDay') {
      day.value = `${n2s(day.closeWorkDay)}W`;
    } else if (day.type === 'last') {
      day.value = `${n2s(day.last)}L`;
    } else if (day.type === 'some') {
      day.value = day.some.join(',');
    } else {
      day.value = day.type;
    }
    if (hour.type === 'period') {
      hour.value = `${n2s(hour.start)}-${n2s(hour.end)}`;
    } else if (hour.type === 'beginInterval') {
      hour.value = `${n2s(hour.begin)}/${n2s(hour.beginEvery)}`;
    } else if (hour.type === 'some') {
      hour.value = hour.some.join(',');
    } else {
      hour.value = hour.type;
    }
    if (minute.type === 'period') {
      minute.value = `${n2s(minute.start)}-${n2s(minute.end)}`;
    } else if (minute.type === 'beginInterval') {
      minute.value = `${n2s(minute.begin)}/${n2s(minute.beginEvery)}`;
    } else if (minute.type === 'some') {
      minute.value = minute.some.join(',');
    } else {
      minute.value = minute.type;
    }
    if (second.type === 'period') {
      second.value = `${n2s(second.start)}-${n2s(second.end)}`;
    } else if (second.type === 'beginInterval') {
      second.value = `${n2s(second.begin)}/${n2s(second.beginEvery)}`;
    } else if (second.type === 'some') {
      second.value = second.some.join(',');
    } else {
      second.value = second.type;
    }
    this.setState(
      {
        year: { ...year },
        month: { ...month },
        week: { ...week },
        day: { ...day },
        hour: { ...hour },
        minute: { ...minute },
        second: { ...second },
      },
      () => {
        this.triggerChange();
      }
    );
  }

  n2s(number) {
    // eslint-disable-next-line use-isnan
    if (typeof number === 'number' && number !== NaN) {
      return `${number}`;
    }
    return number;
  }

  triggerChange() {
    // eslint-disable-next-line no-unused-expressions
    this.props.onChange && this.props.onChange(this.format());
  }

  // 发生表单值改变，重新计算
  onChange = (type, value) => {
    this.state[type].value = value;

    this.setState({ ...this.state }, () => {
      this.parse();
      this.triggerChange();
    });
  };

  renderOverLay() {
    const { activeKey } = this.state;
    return (
      <Tabs
        activeKey={activeKey}
        onChange={(key) => {
          this.setState({ activeKey: key });
        }}
      >
        <TabPane tab="秒" key="second">
          <Second
            {...this.state}
            onChange={(state) => {
              this.changeState({ second: state });
            }}
          />
        </TabPane>
        <TabPane tab="分钟" key="minute">
          <Minute
            {...this.state}
            onChange={(state) => {
              this.changeState({ minute: state });
            }}
          />
        </TabPane>
        <TabPane tab="小时" key="hour">
          <Hour
            {...this.state}
            onChange={(state) => {
              this.changeState({ hour: state });
            }}
          />
        </TabPane>
        <TabPane tab="日" key="day">
          <Day
            {...this.state}
            onChange={(state) => {
              this.changeState({ day: state });
            }}
          />
        </TabPane>
        <TabPane tab="月" key="month">
          <Month
            {...this.state}
            onChange={(state) => {
              this.changeState({ month: state });
            }}
          />
        </TabPane>
        <TabPane tab="周" key="week">
          <Week
            {...this.state}
            onChange={(state) => {
              this.changeState({ week: state });
            }}
          />
        </TabPane>

        <TabPane tab="年" key="year">
          <Year
            {...this.state}
            onChange={(state) => {
              this.changeState({ year: state });
            }}
          />
        </TabPane>
      </Tabs>
    );
  }

  render() {
    const state = JSON.parse(JSON.stringify(this.state));
    const { year, month, week, day, hour, minute, second } = state;
    return (
      <div className="antd-cron">
        {this.renderOverLay()}
        <List bordered style={{ marginTop: 10 }}>
          <List.Item>
            <Row type="flex" gutter={5} style={{ width: '100%', textAlign: 'center' }}>
              <Col span={3}>秒</Col>
              <Col span={3}>分</Col>
              <Col span={3}>小时</Col>
              <Col span={3}>天</Col>
              <Col span={3}>月</Col>
              <Col span={3}>星期</Col>
              <Col span={3}>年</Col>
            </Row>
          </List.Item>
          <List.Item>
            <Row type="flex" gutter={5} style={{ width: '100%', textAlign: 'center' }}>
              <Col span={3}>
                <Input
                  value={second.value}
                  onChange={(e) => {
                    this.onChange('second', e.target.value);
                  }}
                />
              </Col>
              <Col span={3}>
                <Input
                  value={minute.value}
                  onChange={(e) => {
                    this.onChange('minute', e.target.value);
                  }}
                />
              </Col>
              <Col span={3}>
                <Input
                  value={hour.value}
                  onChange={(e) => {
                    this.onChange('hour', e.target.value);
                  }}
                />
              </Col>
              <Col span={3}>
                <Input
                  value={day.value}
                  onChange={(e) => {
                    this.onChange('day', e.target.value);
                  }}
                />
              </Col>
              <Col span={3}>
                <Input
                  value={month.value}
                  onChange={(e) => {
                    this.onChange('month', e.target.value);
                  }}
                />
              </Col>
              <Col span={3}>
                <Input
                  value={week.value}
                  onChange={(e) => {
                    this.onChange('week', e.target.value);
                  }}
                />
              </Col>
              <Col span={3}>
                <Input
                  value={year.value}
                  onChange={(e) => {
                    this.onChange('year', e.target.value);
                  }}
                />
              </Col>
            </Row>
          </List.Item>
        </List>
      </div>
    );
  }
}
export default Cron;
