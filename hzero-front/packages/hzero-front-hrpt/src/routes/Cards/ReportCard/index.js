/**
 * ReportCard 报表卡片
 * @date: 2019-11-01
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React from 'react';
import { Card, Spin, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';
import formatterCollections from 'utils/intl/formatterCollections';
import ReportView from '@/components/ReportView';

import styles from './index.less';

@formatterCollections({ code: ['hrpt.reportCard'] })
export default class ReportCard extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      params: {},
      loading: true,
      name: ' ',
      key: 1,
    };
  }

  componentDidMount() {
    const { name = ' ', cardParams } = this.props;
    if (cardParams) {
      const params = {};
      cardParams.split('&').forEach((item) => {
        const arr = item.split('=');
        const content = arr[1];
        params[arr[0]] = content;
      });
      this.setState({
        name,
        params,
        loading: false,
      });
    } else {
      this.setState({
        name,
        loading: false,
      });
    }
  }

  @Bind()
  goToReport() {
    const { name = ' ' } = this.props;
    const { params = {} } = this.state;
    const { code, type } = params;
    openTab({
      key: `/hrpt/report-query/detail/${code}/${name}`,
      path: `/hrpt/report-query/detail/${code}/${name}`,
      title: intl.get('hrpt.reportCard.view.reportCard.details').d('报表详情'),
      search: {
        chartType: type,
      },
      closable: true,
    });
  }

  render() {
    const { params, loading, name, key } = this.state;
    const option = {
      grid: { bottom: '120', right: '120', left: '120' },
    };
    return (
      <Card
        key={name}
        className={styles.echarts}
        title={name}
        bordered={false}
        extra={
          <>
            {params.code && (
              <a onClick={this.goToReport}>
                {intl.get('hzero.common.button.details').d('查看详情')}
                <Icon className={styles['report-card-icon']} type="double-right" />
              </a>
            )}
            <a
              className={styles['report-card-button']}
              onClick={() => {
                this.setState({ key: uuid() });
              }}
            >
              {intl.get('hzero.common.button.reload').d('重新加载')}
              <Icon className={styles['report-card-icon']} type="reload" />
            </a>
          </>
        }
      >
        <div className={styles['echarts-warp']} key={key}>
          <Spin spinning={loading} wrapperClassName={styles['echarts-loading']}>
            {params.code ? (
              <ReportView
                type={params.type}
                code={params.code}
                selectView={false}
                option={option}
                rotate={40}
                radius={60}
                pieCenter={['50%', '55%']}
              />
            ) : (
              <div>{intl.get('hzero.common.components.noticeIcon.null').d('暂无数据')}</div>
            )}
          </Spin>
        </div>
      </Card>
    );
  }
}
