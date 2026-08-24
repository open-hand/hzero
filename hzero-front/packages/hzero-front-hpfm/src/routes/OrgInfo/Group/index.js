/**
 * model 组织信息-集团
 * @date: 2018-7-3
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';

import GroupModal from './GroupModal';

@formatterCollections({ code: 'hpfm.group' })
@connect(({ loading, group }) => ({
  group,
  updateLoading: loading.effects['group/updateGroup'],
}))
export default class Group extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      organizationId: getCurrentOrganizationId(),
      modalVisible: false,
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render调用后获取页面数据
   */
  componentDidMount() {
    this.fetchGroup();
  }

  /**
   * 获取集团信息
   * @param {object} params - 请求参数
   */
  fetchGroup(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'group/fetchGroup',
      payload: { organizationId: this.state.organizationId, ...params },
    });
  }

  /**
   * 显示集团编辑模态框
   */
  @Bind()
  handleUpdateGroup() {
    this.handleModalVisible(true);
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 隐藏集团编辑模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  /**
   * 保存集团信息
   * @param {object} fieldsValue - 编辑的表单数据
   */
  @Bind()
  handleSaveGroup(fieldsValue) {
    const {
      dispatch,
      group: { groupData },
    } = this.props;
    const group = groupData[0] || {};
    dispatch({
      type: 'group/updateGroup',
      payload: {
        ...group,
        ...fieldsValue,
        organizationId: this.state.organizationId,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchGroup();
      }
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      group: { groupData },
      updateLoading,
      match,
    } = this.props;
    const { modalVisible } = this.state;
    const group = groupData[0] || {};
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.group.view.message.title').d('集团')} />
        <Content noCard>
          <div>
            {intl.get('hpfm.group.model.company.groupNum').d('集团编码')}：{group && group.groupNum}
          </div>
          <div style={{ marginTop: '8px' }}>
            {intl.get('hpfm.group.model.company.groupName').d('集团名称')}：
            {group && group.groupName}
            {group.groupName && (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${match.path}.button.edit`,
                    type: 'button',
                    meaning: '集团-编辑',
                  },
                ]}
                onClick={this.handleUpdateGroup}
              >
                <Icon type="edit" />
              </ButtonPermission>
            )}
          </div>
          <GroupModal
            initData={group}
            updateLoading={updateLoading}
            modalVisible={modalVisible}
            onCancel={this.hideModal}
            onOk={this.handleSaveGroup}
          />
        </Content>
      </React.Fragment>
    );
  }
}
