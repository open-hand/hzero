/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/25
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 项目管理列表页
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { isEmpty, isNumber } from 'lodash';
import { DataSet, Table, Modal, Tooltip } from 'choerodon-ui/pro';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender, enableRender } from 'utils/renderer';
import intl from 'utils/intl';
// import classNames from 'classnames';

import { Button as ButtonPermission } from 'components/Permission';
import { projectManageDS, detailDS } from '@/stores/projectManageDS';
import { fetchLevelPath } from '@/services/projectManageService';
import Drawer from './Drawer';
import styles from './index.less';

const prefix = 'hiot.thingVolumes';

@formatterCollections({
  code: [prefix, 'hiot.common'],
})
export default class ProjectManage extends Component {
  constructor(props) {
    super(props);
    this.projectManageDS = new DataSet({
      ...projectManageDS(),
    });

    this.state = {
      codeList: [], // 设备编码列表
      thingGroupIdList: [], // 设备组ID列表
      thingGroupName: '',
    };
  }

  detailDS;

  componentDidMount() {
    const {
      match: { params },
    } = this.props;
    const { deviceId } = params;
    if (!isEmpty(params)) {
      this.handleFetchLevelPath();
    }
    this.projectManageDS.setQueryParameter('parentId', deviceId);
    this.projectManageDS.query();
  }

  componentDidUpdate(prevProps) {
    const { params = {}, url = '' } = this.props.match;
    const { deviceId } = params;
    if (prevProps.match.url !== url) {
      this.projectManageDS.setQueryParameter('parentId', deviceId);
      this.handleFetchLevelPath();
      this.projectManageDS.query();
      this.forceUpdate();
    }
  }

  @Bind()
  handleFetchLevelPath() {
    const {
      match: { params },
    } = this.props;
    const { deviceId } = params;
    fetchLevelPath({ thingGroupId: deviceId }).then((res) => {
      if (res && !res.failed) {
        const { thingCodes = [], thingGroupIds = [], thingGroupName } = res;
        this.setState({
          codeList: thingCodes,
          thingGroupIdList: thingGroupIds,
          thingGroupName,
        });
      }
    });
  }

  @Bind()
  async handleOk() {
    try {
      const validate = await this.detailDS.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    this.projectManageDS.query();
  }

  /**
   * 处理新建操作
   */
  @Bind()
  handleNew(isEdit, isMainPage, record) {
    this.detailDS = new DataSet(detailDS());
    const {
      match: { params },
    } = this.props;
    const { codeList = [], thingGroupName = '' } = this.state;
    const { deviceId } = params;
    this.detailDS.create({}, 0);
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hzero.common.view.title.create').d('新建')
      : intl.get('hzero.common.view.title.edit').d('编辑');
    let currentLevelPath = '';
    if (!isMainPage) {
      currentLevelPath = codeList.join('/');
    }
    const drawerProps = {
      currentLevelPath,
      currentEditData,
      isEdit,
      deviceId,
      isChild: !isMainPage,
      detailDs: this.detailDS,
      currentName: thingGroupName,
    };
    Modal.open({
      drawer: true,
      key: 'bankAccount',
      destroyOnClose: true,
      closable: true,
      title,
      children: <Drawer {...drawerProps} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: this.handleOk,
      onCancel: () => {
        this.detailDS.removeAll();
      },
      onClose: () => {
        this.detailDS.removeAll();
      },
    });
  }

  @Bind()
  handleToSubDevicePage(record) {
    const { history } = this.props;
    const { thingGroupIdList } = this.state;
    if (!isNumber(record)) {
      // 点击子设备按钮进行跳转
      const thingGroupId = record.get('thingGroupId');
      history.push({
        pathname: `/hiot/project-manage/sub-device/${thingGroupId}`,
      });
    } else if (record === -1) {
      history.push('/hiot/project-manage/list');
    } else {
      // 点击标题进行跳转
      history.push({
        pathname: `/hiot/project-manage/sub-device/${thingGroupIdList[record]}`,
      });
    }
  }

  get title() {
    const { codeList } = this.state;
    const titleName = intl.get(`${prefix}.view.thingVolumes.manage`).d('设备分组');
    return (
      <>
        <a onClick={() => this.handleToSubDevicePage(-1, true)}>{titleName}</a>
        {codeList.map((item, index) => {
          return (
            <span style={{ marginLeft: 2 }}>
              /
              <Tooltip placement="top" title={item}>
                <a
                  style={{ marginLeft: 2 }}
                  onClick={() => this.handleToSubDevicePage(index, true)}
                >
                  <span className={styles['project-title-span']}>{`${item}`}</span>
                </a>
              </Tooltip>
            </span>
          );
        })}
      </>
    );
  }

  get backPath() {
    const { thingGroupIdList = [] } = this.state;
    const len = thingGroupIdList.length;
    if (len === 1) {
      return '/hiot/project-manage/list';
    } else if (len > 1) {
      return `/hiot/project-manage/sub-device/${thingGroupIdList[len - 2]}`;
    }
    return '';
  }

  render() {
    const {
      match: { path, params },
    } = this.props;
    const isMainPage = isEmpty(params); // params 为空，当前是主页面
    const title = intl.get(`${prefix}.view.thingVolumes.manage`).d('设备分组');
    const columns = [
      { name: 'name' },
      { name: 'code' },
      {
        name: 'enabledFlag',
        width: 100,
        renderer: ({ value }) => enableRender(value),
      },

      { name: 'subGroupNum', align: 'left' },

      {
        name: 'action',
        width: 160,
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        renderer: ({ record }) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '设备组-编辑',
                    },
                  ]}
                  onClick={() => this.handleNew(true, isMainPage, record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'subDevice',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.addSubDevice`,
                      type: 'button',
                      meaning: '设备组-添加子设备',
                    },
                  ]}
                  onClick={() => this.handleToSubDevicePage(record)}
                >
                  {intl.get(`${prefix}.view.thingVolumes.addSubDevice`).d('添加子设备组')}
                </ButtonPermission>
              ),
              len: 6,
              title: intl.get(`${prefix}.view.thingVolumes.addSubDevice`).d('添加子设备组'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Header backPath={isMainPage ? '' : this.backPath} title={isMainPage ? title : this.title}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '设备组-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleNew(false, isMainPage)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            className={styles['tree-table-demo']}
            dataSet={this.projectManageDS}
            queryFieldsLimit={2}
            columns={columns}
          />
        </Content>
      </>
    );
  }
}
