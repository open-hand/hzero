/**
 * SourceSystemConfig - 来源系统配置详情
 * @date: 2020-2-14
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { Form, TextField, TextArea, Switch } from 'choerodon-ui/pro';
import React, { Component } from 'react';

import { Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'hzero-front/lib/utils/intl';

/**
 * 来源系统配置详情
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hchg.sourceSystemConfig'] })
export default class SourceSystemConfigDetail extends Component {
  constructor(props) {
    super(props);

    this.state = {
      openType: this.props.openType,
    };
  }

  async componentDidMount() {
    const { formDS } = this.props;
    if (!formDS.current && formDS.queryParameter.sourceSystemId) {
      await formDS.query();
      this.setState({
        openType: this.props.openType,
      });
    }
  }

  render() {
    const { openType } = this.state;
    const { formDS } = this.props;
    return (
      <div>
        <Content>
          <Form disabled={openType === 'VIEW'} dataSet={formDS} columns={1}>
            <TextField
              name="systemNum"
              restrict="a-zA-Z0-9-_./"
              disabled={openType === 'EDIT'}
              placeholder={intl
                .get('hchg.sourceSystem.view.tip.systemNum')
                .d('若留空则自动生成系统编号')}
            />
            <TextField name="systemName" />
            <TextField
              name="callbackUrl"
              help={intl
                .get('hchg.sourceSystem.view.message.help')
                .d(
                  '示例：内部服务:http://hzero-xxx/v1/bill/callback，外部地址: https://xxx.xx.com/v1/bill/callback'
                )}
            />
            <TextArea name="remark" />
            <Switch name="enabledFlag" />
          </Form>
        </Content>
      </div>
    );
  }
}
