/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 导览工作台
 */
import React from 'react';
import { Row, Col } from 'choerodon-ui';
import { DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Map } from './BMap';
import DeviceWorkbench from '@/routes/DeviceManage/Detail';
import WorkbenchTree from './WorkbenchTree';
import ProjectWorkbench from './ProjectWorkbench';

@formatterCollections({ code: ['hiot.workbench', 'hiot.common'] })
export default class Workbench extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      level: 'project', // 导览工作台层级 project：项目层 device：设备层
      projectInfo: {}, // 项目信息
      deviceId: '', // 设备id
      mode: '', // 控制设备详情显示的样式 workbench deviceCard normal
      guid: '',
      firstRender: false, // 用于确保百度地图api加载完成后再加载设备地图组件
    };
    this.saveDeviceInfoDS = new DataSet({}); // 用来保存设备卡片查询条件
  }

  componentDidMount() {
    Map().then(this.setState({ firstRender: true }));
  }

  /**
   * 层级树切换
   * @param nodeInfo 节点信息
   * @param level 层级
   * @param mode 控制设备详情显示的样式
   */
  @Bind()
  changeTreeNode(nodeInfo, level, mode, guid) {
    if (level === 'project') {
      this.setState({ projectInfo: nodeInfo, level });
    } else {
      this.setState({ deviceId: nodeInfo, level, mode: mode || 'deviceWorkbench', guid });
      if (this.deviceWorkbench) {
        this.deviceWorkbench.setState(
          {
            deviceId: nodeInfo,
          },
          () => {
            this.deviceWorkbench.handleQuery();
          }
        );
      }
    }
  }

  /**
   * 点击设备详情返回按钮
   */
  @Bind()
  handleDeviceBack() {
    // 设置激活的标签页
    const saveInfo = this.saveDeviceInfoDS.toData();
    if (!isEmpty(saveInfo)) {
      const { tabKey } = saveInfo[0];
      this.setState({ tabKey });
    }
    this.setState({ level: 'project' });
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { level, projectInfo, deviceId, tabKey, mode, guid, firstRender } = this.state;
    return (
      <>
        <Header title={intl.get('hiot.workbench.view.header').d('导览工作台')} />
        <Content>
          <Row>
            <Col span={4}>
              <WorkbenchTree onTreeNodeSelect={this.changeTreeNode} />
            </Col>
            <Col span={20} style={{ paddingLeft: 10 }}>
              {level === 'project' ? (
                <ProjectWorkbench
                  path={path}
                  tabKey={tabKey}
                  infoDS={this.saveDeviceInfoDS}
                  projectInfo={projectInfo}
                  onLinkToDevice={this.changeTreeNode}
                  firstRender={firstRender}
                />
              ) : (
                <DeviceWorkbench
                  mode={mode}
                  onRef={(child) => {
                    this.deviceWorkbench = child;
                  }}
                  key={guid}
                  onBackBtnClick={this.handleDeviceBack}
                  workbenchDeviceId={deviceId}
                  workbenchGuid={guid}
                  isButton={false}
                />
              )}
            </Col>
          </Row>
        </Content>
      </>
    );
  }
}
