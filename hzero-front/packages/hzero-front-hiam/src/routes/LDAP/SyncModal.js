import React from 'react';
import { Modal, Tabs } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';

import UserForm from './UserForm';
import LeaveForm from './LeaveForm';

const { TabPane } = Tabs;

function getFieldsValueByWrappedComponentRef(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return form.getFieldsValue();
  }
  return {};
}

export default class Drawer extends React.PureComponent {
  constructor(props) {
    super(props);
    this.userRef = React.createRef();
    this.leaveRef = React.createRef();
    this.state = {
      type: 'user',
    };
  }

  @Bind()
  handleChangeTab(type) {
    this.setState({
      type,
    });
  }

  @Bind()
  handleOk() {
    const { type } = this.state;
    const { dispatch, syncLeaveDetail, syncUserDetail, ldapId } = this.props;
    const userFormData = getFieldsValueByWrappedComponentRef(this.userRef);
    userFormData.startDate =
      userFormData.startDate && userFormData.startDate.format(DEFAULT_DATETIME_FORMAT);
    userFormData.endDate =
      userFormData.endDate && userFormData.endDate.format(DEFAULT_DATETIME_FORMAT);
    const leaveFormData = getFieldsValueByWrappedComponentRef(this.leaveRef);
    leaveFormData.startDate =
      leaveFormData.startDate && leaveFormData.startDate.format(DEFAULT_DATETIME_FORMAT);
    leaveFormData.endDate =
      leaveFormData.endDate && leaveFormData.endDate.format(DEFAULT_DATETIME_FORMAT);
    if (type === 'user') {
      // eslint-disable-next-line no-unused-expressions
      this.userRef &&
        this.userRef.current &&
        this.userRef.current.props &&
        this.userRef.current.props.form.validateFields((err) => {
          if (!err) {
            dispatch({
              type: 'ldap/updateSyncUser',
              payload: {
                ...syncUserDetail,
                ...userFormData,
                tenantId: getCurrentOrganizationId(),
                ldapId,
                enabledFlag: userFormData.enabledFlag ? 1 : 0,
              },
            }).then((res) => {
              if (res) {
                notification.success();
              }
            });
          } else {
            notification.warning({
              message: intl.get('hiam.ldap.view.message.required').d('存在必输字段未填写'),
            });
          }
        });
    } else {
      // eslint-disable-next-line no-unused-expressions
      this.leaveRef &&
        this.leaveRef.current &&
        this.leaveRef.current.props &&
        this.leaveRef.current.props.form.validateFields((err) => {
          if (!err) {
            dispatch({
              type: 'ldap/updateSyncLeave',
              payload: {
                ...syncLeaveDetail,
                ...leaveFormData,
                tenantId: getCurrentOrganizationId(),
                ldapId,
                enabledFlag: leaveFormData.enabledFlag ? 1 : 0,
              },
            }).then((res) => {
              if (res) {
                notification.success();
              }
            });
          } else {
            notification.warning({
              message: intl.get('hiam.ldap.view.message.required').d('存在必输字段未填写'),
            });
          }
        });
    }
  }

  render() {
    const {
      visible,
      onCancel,
      frequencyList = [],
      ldap: {
        syncLeaveDetail = {},
        syncUserDetail = {},
        querySyncUserLoading,
        querySyncLeaveLoading,
      },
    } = this.props;
    const userProps = {
      wrappedComponentRef: this.userRef,
      frequencyList,
      initData: syncUserDetail,
      loading: querySyncUserLoading,
    };
    const leaveProps = {
      wrappedComponentRef: this.leaveRef,
      frequencyList,
      initData: syncLeaveDetail,
      loading: querySyncLeaveLoading,
    };
    return (
      <Modal
        destroyOnClose
        keyboard={false}
        title={intl.get('hiam.ldap.option.syncConfig').d('定时同步配置')}
        width={520}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        zIndex={1000}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Tabs tabBarGutter={10} defaultActiveKey="user" animated onChange={this.handleChangeTab}>
          <TabPane tab={intl.get('hiam.ldap.view.title.tab.user').d('定时同步用户')} key="user">
            <UserForm {...userProps} />
          </TabPane>
          <TabPane
            tab={intl.get('hiam.ldap.view.title.tab.leave').d('定时同步离职用户')}
            key="leave"
          >
            <LeaveForm {...leaveProps} />
          </TabPane>
        </Tabs>
      </Modal>
    );
  }
}
