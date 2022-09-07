import React from 'react';
import { Button, Icon, TextArea, Form } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import styles from './index.less';

export default class Assessment extends React.Component {
  state = { count: [1, 1, 1, 1, 1] };

  @Bind()
  handleSelect(value) {
    const { count } = this.state;
    const arr = count.map((_, index) => (index < value ? 1 : 0));
    this.setState({ count: arr });
  }

  render() {
    const { onAssess = e => e, onCancel = e => e, record, assessmentDs } = this.props;
    const { count } = this.state;
    return (
      <>
        <div className={styles['im-customer-assessment']}>
          <p className={styles['im-assessment-title']}>
            {intl
              .get('hims.messageCenter.view.message.assessment.content')
              .d('请您对本次服务进行评价')}
          </p>
          <div className={styles['im-assessment-star']}>
            <ul>
              {count.map((item, index) => (
                <li
                  onClick={() => {
                    this.handleSelect(index + 1);
                  }}
                >
                  <Icon
                    className={
                      item
                        ? `${styles['im-assessment-icon']} ${styles['im-assessment-icon-on']}`
                        : styles['im-assessment-icon']
                    }
                    type="star"
                  />
                </li>
              ))}
            </ul>
            <div style={{ width: 384 }}>
              <Form dataSet={assessmentDs}>
                <TextArea name="remark" cols={30} />
              </Form>
            </div>
          </div>
          <Button
            className={styles['im-assessment-submit']}
            onClick={() => {
              onAssess(count.lastIndexOf(1) + 1, record);
            }}
          >
            {intl.get('hims.messageCenter.view.message.assessment.confirm').d('提交评价')}
          </Button>
          <div>
            <a
              onClick={() => {
                onCancel(record);
              }}
            >
              {intl.get('hims.messageCenter.view.message.assessment.cancel').d('暂不评价')}
            </a>
          </div>
        </div>
      </>
    );
  }
}
