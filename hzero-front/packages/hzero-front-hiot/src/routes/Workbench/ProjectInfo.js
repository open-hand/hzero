/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 项目基本信息
 */
import React from 'react';
import { Card } from 'choerodon-ui';
import { Form, Output, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import intl from 'utils/intl';

export default class ProjectInfo extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
    this.projectInfoDS = new DataSet({
      fields: [
        {
          name: 'name',
          type: 'string',
          label: intl.get('hiot.workbench.model.workbench.deviceName').d('设备组名称'),
        },
        {
          name: 'code',
          type: 'string',
          label: intl.get('hiot.workbench.model.workbench.deviceCode').d('设备组编码'),
        },
      ],
    });
  }

  componentDidMount() {
    this.initProjectInfo();
  }

  shouldComponentUpdate(nextProps) {
    const {
      projectInfo: { thingGroupId: nextProjectId },
    } = nextProps;
    const {
      projectInfo: { thingGroupId },
    } = this.props;
    return !(nextProjectId === thingGroupId);
  }

  componentDidUpdate() {
    this.initProjectInfo();
  }

  // 初始化项目信息
  @Bind()
  initProjectInfo() {
    this.projectInfoDS.removeAll();
    const {
      projectInfo: { code, name },
    } = this.props;
    this.projectInfoDS.create({ code, name }, 0);
  }

  render() {
    return (
      <Card
        className={DETAIL_CARD_CLASSNAME}
        bordered={false}
        title={intl.get('hiot.common.view.baseInfo').d('基本信息')}
      >
        <Form dataSet={this.projectInfoDS} columns={3} labelAlign="left">
          <Output name="name" style={{ wordBreak: 'break-all' }} />
          <Output name="code" style={{ wordBreak: 'break-all' }} />
        </Form>
      </Card>
    );
  }
}
