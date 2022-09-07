import React, { PureComponent } from 'react';
import { Spin } from 'choerodon-ui';
import { Form, TextField, Select, Lov } from 'choerodon-ui/pro';
import Cron from '@/components/cron';

export default class EditDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
    };
  }

  componentDidMount() {
    const { isNew } = this.props;
    if (!isNew) {
      this.handleFetchDetail();
    }
  }

  /**
   * 明细查询
   */
  async handleFetchDetail() {
    const { record, drawerFormDS } = this.props;
    this.setState({ detailLoading: true });
    drawerFormDS.setQueryParameter('jobId', record.get('jobId'));
    await drawerFormDS.query();
    this.setState({ detailLoading: false });
  }

  render() {
    const { isNew, drawerFormDS, record } = this.props;
    const { detailLoading } = this.state;
    const disableFlag = !isNew && record.get('enabledFlag');
    return (
      <Spin spinning={detailLoading}>
        <Form dataSet={drawerFormDS} columns={1} id="scrollArea">
          <Lov name="frontalLov" disabled={disableFlag} />
          <TextField name="jobCode" restrict="a-zA-Z0-9-_./" disabled={!isNew} />
          <TextField name="jobName" disabled={disableFlag} />
          <TextField name="jobClassName" disabled={disableFlag} />
          <Select name="statusCode" disabled={disableFlag} />
          <Select name="jobType" disabled={disableFlag} />
          <TextField name="jobClass" disabled={disableFlag} />
          <TextField name="jobClassMethod" disabled={disableFlag} />
          <Cron
            onChange={(val) => drawerFormDS.current.set('jobCron', val)}
            value={(!isNew && record.get('jobCron')) || ''}
            name="jobCron"
            // width="100%"
            // lang="zh_CN"
            // type={['second', 'minute', 'hour', 'day', 'month', 'week', 'year']}
          />
        </Form>
      </Spin>
    );
  }
}
