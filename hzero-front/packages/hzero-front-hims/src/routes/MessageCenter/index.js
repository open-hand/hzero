import React from 'react';
import { Tag, Popover } from 'choerodon-ui';
import {
  Button,
  DataSet,
  Modal,
  ModalContainer,
  Spin,
  Table,
  Tabs,
  Icon,
  Progress,
  Select,
} from 'choerodon-ui/pro';
import { message } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import Viewer from 'react-viewer';
import axios from 'axios';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { operatorRender, yesOrNoRender } from 'utils/renderer';
import { getAccessToken, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { HZERO_IM } from 'utils/config';

import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';

import {
  closeConnectDs,
  groupDs,
  initDs,
  messageDs,
  messageListDs,
  assessmentDs,
  messageAmountDs,
  formDs,
  signDS,
  lovDs,
  iniDs,
  selfDs,
} from '../../stores/messageCenterDS';

import Drawer from './Drawer';
import ChatDrawer from './ChatDrawer';
import AssessmentDrawer from './Assessment';
import BaseDrawer from './BaseDrawer';
import EditDrawer from './EditDrawer';
import DelegateDrawer from './DelegateDrawer';

import 'react-viewer/dist/index.css';
import styles from './index.less';

const { TabPane } = Tabs;

@formatterCollections({ code: ['hims.messageCenter'] })
export default class CustomerGroup extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      groupList: [],
      groupKey: undefined,
      defaultStatus: 'server',
      messageAmount: 0,
      spinning: true,
      chatVisible: false, // 聊天模态框
      isSelected: false,
      currentKey: '', // 当前的客户id（KEY）
      ws: null, // websocket
      messageList: [], // 消息列表
      record: {},
      currentData: {},
      fetchMessageListLoading: false,
      languageType: [], // 语言类别
      previewImages: '',
      previewVisible: false,
      statistics: {
        score: {
          total: 0,
          average: 0,
        },
        evaluate: {
          total: 0,
          today: 0,
        },
        receptions: {
          total: 0,
          today: 0,
        },
        satisfaction: {
          today: {},
          total: {},
        },
      },
    };
  }

  initDs = new DataSet(initDs());

  groupDs = new DataSet(groupDs());

  messageDs = new DataSet(messageDs());

  messageListDs = new DataSet(messageListDs());

  closeConnectDs = new DataSet(closeConnectDs());

  assessmentDs = new DataSet(assessmentDs());

  signDs = new DataSet(signDS());

  lovDs = new DataSet(lovDs());

  iniDs = new DataSet(iniDs());

  selfDs = new DataSet(selfDs());

  messageAmountDs = new DataSet(messageAmountDs());

  staticTextEditorRef;

  get runningColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'csGroupId',
        header: intl.get('hims.messageCenter.model.messageCenter.csGroupId').d('群组'),
      },
      {
        name: 'id',
        header: intl.get('hims.messageCenter.model.messageCenter.id').d('会话KEY'),
      },
      {
        name: 'userName',
        header: intl.get('hims.messageCenter.model.messageCenter.userName').d('会话用户名'),
        width: 150,
      },
      {
        name: 'userEmail',
        header: intl.get('hims.messageCenter.model.messageCenter.userEmail').d('邮箱'),
        width: 200,
      },
      {
        name: 'clientIp',
        header: intl.get('hims.messageCenter.model.messageCenter.clientIp').d('IP'),
        width: 200,
      },
      {
        name: 'createAt',
        header: intl
          .get('hims.messageCenter.model.messageCenter.dialogueCreateAt')
          .d('会话创建时间'),
        width: 200,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        renderer: ({ record }) => {
          const actions = [];
          actions.push(
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.close`,
                      type: 'button',
                      meaning: '消息中心-关闭',
                    },
                  ]}
                  onClick={() => {
                    this.handleClose(record);
                  }}
                >
                  {intl.get('hims.messageCenter.view.button.close').d('关闭')}
                </ButtonPermission>
              ),
              key: 'close',
              len: 2,
              title: intl.get('hims.messageCenter.view.button.close').d('关闭'),
            },
            {
              ele: (
                // <Badge
                //   dot={record.get('serviceStatus') === 'wait_service'}
                //   style={{ zIndex: 1 }}
                //   offset={[6, 4]}
                // >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.start`,
                      type: 'button',
                      meaning: '消息中心-开始服务',
                    },
                  ]}
                  onClick={() => {
                    this.handleStart(record);
                  }}
                >
                  {intl.get('hims.messageCenter.view.button.start').d('开始服务')}
                </ButtonPermission>
                // </Badge>
              ),
              key: 'start',
              len: 4,
              title: intl.get('hims.messageCenter.view.button.start').d('开始服务'),
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delegate`,
                      type: 'button',
                      meaning: '消息中心-转交',
                    },
                  ]}
                  onClick={() => {
                    this.handleDelegate(record);
                  }}
                >
                  {intl.get('hzero.common.status.delegate').d('转交')}
                </ButtonPermission>
              ),
              key: 'view',
              len: 2,
              title: intl.get('hzero.common.status.delegate').d('转交'),
            }
          );
          return [operatorRender(actions)];
        },
        lock: 'right',
      },
    ];
  }

  get closeColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'csGroupId',
        header: intl.get('hims.messageCenter.model.messageCenter.csGroupId').d('群组'),
      },
      {
        name: 'id',
        header: intl.get('hims.messageCenter.model.messageCenter.id').d('会话KEY'),
      },
      {
        name: 'userName',
        header: intl.get('hims.messageCenter.model.messageCenter.userName').d('会话用户名'),
        width: 150,
      },
      {
        name: 'userEmail',
        header: intl.get('hims.messageCenter.model.messageCenter.userEmail').d('邮箱'),
        width: 200,
      },
      {
        name: 'clientIp',
        header: intl.get('hims.messageCenter.model.messageCenter.clientIp').d('IP'),
        width: 200,
      },
      {
        name: 'isTag',
        header: intl.get('hims.messageCenter.model.messageCenter.isTag').d('是否标记'),
        width: 100,
        renderer: ({ value }) => yesOrNoRender(value),
      },
      {
        name: 'updateAt',
        header: intl.get('hims.messageCenter.model.messageCenter.updateAt').d('会话最后更新时间'),
        width: 200,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        renderer: ({ record }) => {
          const actions = [];
          actions.push({
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.history`,
                    type: 'button',
                    meaning: '消息中心-历史消息',
                  },
                ]}
                onClick={() => {
                  this.handleStart(record);
                }}
              >
                {intl.get('hims.messageCenter.view.button.history').d('历史消息')}
              </ButtonPermission>
            ),
            key: 'history',
            len: 4,
            title: intl.get('hims.messageCenter.view.button.history').d('历史消息'),
          });
          if (record.get('isTag')) {
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.cancelMark`,
                      type: 'button',
                      meaning: '消息中心-取消标记',
                    },
                  ]}
                  onClick={() => {
                    this.handleMarked(record, 0);
                  }}
                >
                  {intl.get('hims.messageCenter.view.button.cancelMark').d('取消标记')}
                </ButtonPermission>
              ),
              key: 'cancelMark',
              len: 4,
              title: intl.get('hims.messageCenter.view.button.cancelMark').d('取消标记'),
            });
          } else {
            actions.push({
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.mark`,
                      type: 'button',
                      meaning: '消息中心-标记',
                    },
                  ]}
                  onClick={() => {
                    this.handleMarked(record, 1);
                  }}
                >
                  {intl.get('hims.messageCenter.view.button.mark').d('标记')}
                </ButtonPermission>
              ),
              key: 'cancelMark',
              len: 2,
              title: intl.get('hims.messageCenter.view.button.mark').d('标记'),
            });
          }
          return [operatorRender(actions)];
        },
        lock: 'right',
      },
    ];
  }

  get messageColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'content',
        header: intl.get('hims.messageCenter.model.messageCenter.content').d('反馈内容'),
      },
      {
        name: 'from',
        header: intl.get('hims.messageCenter.model.messageCenter.from').d('联系方式'),
        width: 200,
      },
      {
        name: 'createAt',
        header: intl.get('hims.messageCenter.model.messageCenter.createAt').d('创建时间'),
        width: 200,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        renderer: ({ record }) => {
          const actions = [];
          actions.push({
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.view`,
                    type: 'button',
                    meaning: '消息中心-查看',
                  },
                ]}
                onClick={() => {
                  this.handleViewMessage(record);
                }}
              >
                {intl.get('hzero.common.button.view').d('查看')}
              </ButtonPermission>
            ),
            key: 'view',
            len: 2,
            title: intl.get('hzero.common.button.view').d('查看'),
          });

          return [operatorRender(actions)];
        },
        lock: 'right',
      },
    ];
  }

  get signColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'categoryName',
        width: 150,
      },
      {
        name: 'questionTitle',
        width: 200,
      },
      {
        name: 'keyWord',
      },
      {
        name: 'answerDesc',
        width: 300,
      },
      {
        name: 'checkStatus',
        width: 90,
        renderer: ({ value, record }) => {
          const checkStatusName = record.get('checkStatusName');
          return this.checkStatusRender(value, checkStatusName);
        },
      },
      {
        name: 'rejectReason',
        width: 200,
      },
      {
        name: 'action',
        width: 70,
        renderer: ({ record }) => {
          const actions = [];
          actions.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '消息中心-编辑',
                  },
                ]}
                onClick={() => this.showEditModal(record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return [operatorRender(actions)];
        },
        lock: 'right',
      },
    ];
  }

  componentDidMount() {
    this.messageListDs.isScrollToBottom = true;
    queryUnifyIdpValue('HPFM.LANGUAGE').then((res) => {
      this.setState({
        languageType: res,
      });
    });
    if (!process.env.IM_WEBSOCKET_HOST) {
      console.error(
        intl.get('hims.messageCenter.view.message.pleaseCheckNetConfig').d('请检查网络配置')
      );
      return;
    }
    this.iniDs.query().then(() => {
      this.selfDs.query().then((res) => {
        if (res && res.data) {
          this.id = res.data.id;
          this.initWebsocket();
        }
      });
    });
    this.groupDs.query().then(() => {
      this.queryStatistics();
      const dataSource = this.groupDs.current.toData();
      const { data = [] } = dataSource;
      this.setState(
        {
          groupList: data,
          spinning: false,
        },
        () => {
          const groupKey = undefined;
          this.setState(
            {
              groupKey,
            },
            () => {
              const { groupList } = this.state;
              if (groupList.length > 0) {
                // this.initDs.setQueryParameter('groupKey', this.state.groupKey);
                // this.initDs.setQueryParameter('status', 'running');
                // this.initDs.query();
                this.queryAmount();
              }
            }
          );
        }
      );
    });
  }

  @Bind()
  handleMarked(record, flag) {
    axios({
      url: `${HZERO_IM}/v1/message/cs-message-mark`,
      method: 'POST',
      params: {
        relId: record.get('csGroupRelationId'),
        isTag: flag,
      },
    })
      .then((res) => {
        if (res) {
          notification.success();
          this.handleRefresh();
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }

  @Bind()
  queryAmount() {
    const { groupKey } = this.state;
    this.messageAmountDs.setQueryParameter('groupKey', groupKey);
    this.messageAmountDs.query().then((res) => {
      this.setState({
        messageAmount: res,
      });
    });
  }

  @Bind()
  queryStatistics() {
    const queryStatistics = {};
    axios({
      url: `${HZERO_IM}/v1/statistics/average-score`,
      method: 'GET',
    })
      .then((res) => {
        if (res) {
          queryStatistics.score = res.data;
          const { statistics } = this.state;
          this.setState({ statistics: { ...statistics, score: res.data } });
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
    axios({
      url: `${HZERO_IM}/v1/statistics/evaluate-num`,
      method: 'GET',
    })
      .then((res) => {
        if (res) {
          queryStatistics.evaluate = res.data;
          const { statistics } = this.state;
          this.setState({ statistics: { ...statistics, evaluate: res.data } });
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
    axios({
      url: `${HZERO_IM}/v1/statistics/satisfaction`,
      method: 'GET',
    })
      .then((res) => {
        if (res) {
          queryStatistics.satisfaction = res.data;
          const { statistics } = this.state;
          this.setState({ statistics: { ...statistics, satisfaction: res.data } });
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
    axios({
      url: `${HZERO_IM}/v1/statistics/receptions`,
      method: 'GET',
    })
      .then((res) => {
        if (res) {
          queryStatistics.receptions = res.data;
          const { statistics } = this.state;
          this.setState({ statistics: { ...statistics, receptions: res.data } });
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }

  @Bind()
  initWebsocket() {
    const socket = new WebSocket(`${process.env.IM_WEBSOCKET_HOST}/websocket?csid=${this.id}`);
    const heartCheck = {
      interval: 45000, // 45s
      intervalObj: null,
      reset() {
        clearInterval(this.intervalObj);
        this.start();
      },
      start() {
        this.intervalObj = setInterval(() => {
          socket.send(JSON.stringify({ content: 'hi', messageType: '0' }));
        }, this.interval);
      },
      remove() {
        clearInterval(this.intervalObj);
      },
    };
    socket.addEventListener('open', () => {
      heartCheck.start();
      message.success(
        intl.get('hims.messageCenter.view.message.success').d('WebSocket连接成功'),
        2
      );
    });
    socket.addEventListener('close', () => {
      this.setState({ ws: {} });
      heartCheck.remove();
    });
    socket.addEventListener('error', () => {
      message.error(intl.get('hims.messageCenter.view.message.error').d('WebSocket连接异常'));
      this.setState({ ws: {} });
      heartCheck.remove();
    });
    socket.addEventListener('message', ({ data }) => {
      let msgData = {};
      const { currentData = {} } = this.state;
      try {
        msgData = JSON.parse(data);
      } catch (e) {
        console.log(e);
      }
      if (msgData) {
        if (msgData.messageType === 10) {
          const { messageList = [] } = this.state;
          const newList = messageList.filter((item) => item.id !== msgData.id);
          this.messageListDs.isScrollToBottom = true;
          this.setState({
            messageList: newList,
          });
        } else if (msgData.messageType === 9) {
          const { messageList = [] } = this.state;
          const newList = messageList.filter((item) => item.id !== msgData.id);
          this.setState({
            messageList: newList,
          });
        } else if (currentData.id === msgData.from || msgData.to === currentData.id) {
          const { messageList = [] } = this.state;
          const newList = [...messageList, msgData];
          this.messageListDs.isScrollToBottom = true;
          this.setState({
            messageList: newList,
          });
        }
        if (this.id.toString() !== msgData.from && msgData.messageType === 1 && !msgData.code) {
          notification.success({
            key: msgData.groupId,
            message: (
              <>
                {intl.get('hims.messageCenter.view.message.title.newMessage1').d(`您在群组`)}
                <a
                  alt=""
                  onClick={() => {
                    this.handleCustomerChange(msgData.groupId);
                  }}
                >
                  {' '}
                  {msgData.groupId}{' '}
                </a>
                {intl
                  .get('hims.messageCenter.view.message.title.newMessage2')
                  .d(`下有一条新消息，请及时回复`)}
              </>
            ),
          });
        }
      }
    });
    this.setState({
      ws: socket,
    });
  }

  @Bind()
  handleDelegate(record) {
    Modal.open({
      closable: true,
      destroyOnClose: true,
      key: 'customer-group',
      title: intl.get('hims.messageCenter.view.message.delegate.title.detail').d('转交'),
      children: <DelegateDrawer lovDs={this.lovDs} />,
      onOk: async () => {
        const csUserId = record.get('id');
        const csGroupId = record.get('csGroupId');
        this.lovDs.setQueryParameter('userId', csUserId);
        this.lovDs.setQueryParameter('oldGroupKey', csGroupId);
        const res = await this.lovDs.submit();
        if (res) {
          this.initDs.query();
          return true;
        }
        return false;
      },
      afterClose: () => {
        this.lovDs.reset();
      },
    });
  }

  @Bind()
  handleChangeMessageList(arr) {
    this.setState({ messageList: arr });
  }

  @Bind()
  async handleDelegateSubmit(record) {
    const csUserId = record.get('id');
    const csGroupId = record.get('csGroupId');
    this.lovDs.setQueryParameter('userId', csUserId);
    this.lovDs.setQueryParameter('oldGroupKey', csGroupId);
    const res = await this.lovDs.submit();
    if (res) {
      this.initDs.query();
      return true;
    }
    return false;
  }

  @Bind()
  handleViewMessage(record) {
    Modal.open({
      footer: null,
      closable: true,
      destroyOnClose: true,
      key: 'customer-group',
      title: intl
        .get('hims.messageCenter.view.message.customerGroup.title.detail')
        .d('反馈消息详情'),
      children: <Drawer editData={(record && record.data) || this.messageDs.current} />,
      onOk: () => {
        this.initDs.reset();
      },
      onCancel: () => {
        this.initDs.reset();
      },
      onClose: () => {
        this.initDs.reset();
      },
    });
  }

  @Bind()
  handleClose(record) {
    const { csGroupRelationId: relId, id: csUserId, csGroupId } = record.toData();
    this.closeConnectDs.setQueryParameter('from', this.id);
    this.closeConnectDs.setQueryParameter('to', csUserId);
    this.closeConnectDs.setQueryParameter('groupId', csGroupId);
    // this.closeConnectDs.setQueryParameter('content', '关闭连接');
    this.closeConnectDs.setQueryParameter('contentType', '0');
    this.closeConnectDs.setQueryParameter('messageType', '7');
    this.closeConnectDs.setQueryParameter('chatType', '4');
    this.closeConnectDs.setQueryParameter('relId', relId);
    this.closeConnectDs.query().then(() => {
      notification.success();
      this.handleRefresh();
      this.modal = Modal.open({
        footer: null,
        closable: false,
        destroyOnClose: true,
        key: 'customer-assessment',
        title: intl.get('hims.messageCenter.view.message.assessment.title.detail').d('评价'),
        onCancel: () => {
          this.assessmentDs.reset();
        },
        onClose: () => {
          this.assessmentDs.reset();
        },
        children: (
          <AssessmentDrawer
            onAssess={this.handleAssess}
            onCancel={this.closeModal}
            record={record}
            assessmentDs={this.assessmentDs}
          />
        ),
      });
    });
  }

  @Bind()
  handleAssess(count, record) {
    const { csGroupRelationId, id: userId } = record.toData();
    const { remark } = this.assessmentDs.toData()[0] || {};
    this.assessmentDs.setQueryParameter('score', count);
    this.assessmentDs.setQueryParameter('csGroupRelId', csGroupRelationId);
    this.assessmentDs.setQueryParameter('csUserId', this.id);
    this.assessmentDs.setQueryParameter('evaluationType', 'csToUser');
    this.assessmentDs.setQueryParameter('remark', remark || '');
    this.assessmentDs.setQueryParameter('userId', userId);
    this.assessmentDs.query().then(() => {
      this.closeModal(record);
    });
  }

  /**
   * 关闭模态框
   */
  @Bind()
  closeModal(record, flag) {
    if (!flag) {
      this.modal.close();
    }
    const { csGroupRelationId: relId } = record.toData();
    Modal.confirm({
      children: (
        <span style={{ fontSize: '16px' }}>
          <Icon type="info" style={{ color: '#faad14', margin: '0 4px 4px 0' }} />
          {intl.get('hims.messageCenter.view.message.title.knowledgeDB').d('是否标记知识库？')}
        </span>
      ),
      onOk: () => {
        axios({
          url: `${HZERO_IM}/v1/message/cs-message-mark`,
          method: 'POST',
          params: {
            relId,
            isTag: 1,
          },
        })
          .then((res) => {
            if (res) {
              notification.success();
              this.handleRefresh();
            }
          })
          .catch((err) => {
            notification.error({
              message: err.message,
            });
          });
      },
    });
  }

  /**
   * 关闭模态框
   */
  @Bind()
  showBaseModal(content) {
    if (content.length > 0) {
      let str = '';
      content.map((item) => {
        str += item.content;
        return null;
      });
      this.formDs = new DataSet({
        ...formDs({ defaultValue: str }),
      });
      this.handleCancel();
      Modal.open({
        key: 'customer-base',
        destroyOnClose: true,
        title: intl.get('hims.messageCenter.view.message.base.title.detail').d('知识库录入'),
        children: <BaseDrawer formDs={this.formDs} />,
        onCancel: () => {
          this.formDs.reset();
        },
        onClose: () => {
          this.formDs.reset();
        },
        onOk: () => this.handleMark(),
      });
    } else {
      notification.warning({
        message: intl.get('hzero.common.message.confirm.selected.atLeast').d('请至少选择一行数据'),
      });
    }
  }

  @Bind()
  async handleMark() {
    const validateValue = await this.formDs.validate();
    if (!validateValue) {
      return false;
    }
    await this.formDs.submit();
  }

  @Bind()
  handleStart(record) {
    const { data = {} } = record;
    this.setState({
      record,
      chatVisible: true,
      currentKey: data && data.id,
      currentData: data,
      messageList: [],
      isSelected: data.isTag,
      fetchMessageListLoading: true,
    });
    this.messageListDs.setQueryParameter('userId', data && data.id);
    this.messageListDs.setQueryParameter('groupKey', data.csGroupId);
    this.messageListDs.setQueryParameter('endId', undefined);
    this.messageListDs.query().then(() => {
      const messageList = this.messageListDs.toData().reverse();
      this.setState({
        messageList,
        fetchMessageListLoading: false,
      });
    });
  }

  // @Bind()
  // handleUpdateMessageList() {
  //   const { currentData = {}, groupKey } = this.state;
  //   this.messageListDs.setQueryParameter('userId', currentData && currentData.id);
  //   this.messageListDs.setQueryParameter('groupKey', groupKey);
  //   this.messageListDs.query().then(() => {
  //     const messageList = this.messageListDs.toData();
  //     this.setState({
  //       messageList,
  //     });
  //   });
  // }

  @Bind()
  handleCustomerChange(groupKey) {
    this.setState(
      {
        groupKey,
      },
      () => {
        this.queryAmount();
        if (this.state.defaultStatus === 'feedback') {
          this.messageDs.setQueryParameter('groupKey', groupKey);
          this.messageDs.query();
        } else if (this.state.defaultStatus !== 'sign' && this.state.defaultStatus !== 'server') {
          this.initDs.setQueryParameter('groupKey', this.state.groupKey);
          this.initDs.setQueryParameter('status', this.state.defaultStatus);
          this.initDs.query();
        }
      }
    );
  }

  @Bind()
  handleStatusChange(status) {
    this.setState(
      {
        defaultStatus: status,
      },
      () => {
        const { groupKey } = this.state;
        if (status === 'feedback') {
          this.messageDs.setQueryParameter('groupKey', groupKey);
          this.messageDs.query();
        } else if (status === 'sign') {
          this.signDs.query();
        } else if (status !== 'server') {
          this.initDs.setQueryParameter('groupKey', groupKey);
          this.initDs.setQueryParameter('status', this.state.defaultStatus);
          this.initDs.query();
        }
      }
    );
  }

  @Bind()
  handleCancel() {
    this.setState({
      chatVisible: false,
      isSelected: false,
    });
  }

  @Bind()
  handleRefresh() {
    const { defaultStatus, groupKey, ws } = this.state;
    this.queryAmount();
    if (!ws.url) {
      this.initWebsocket();
    }
    if (defaultStatus === 'feedback') {
      this.messageDs.setQueryParameter('groupKey', groupKey);
      this.messageDs.query();
    } else if (defaultStatus === 'sign') {
      this.signDs.query();
    } else if (defaultStatus !== 'server') {
      this.initDs.setQueryParameter('groupKey', groupKey);
      this.initDs.setQueryParameter('status', defaultStatus);
      this.initDs.query();
    } else {
      this.queryStatistics();
    }
  }

  /**
   * 显示编辑模态框
   * @param {object} [record={}]
   */
  @Bind()
  showEditModal(record = {}) {
    const { languageType } = this.state;
    Modal.open({
      closable: true,
      key: 'message-center-edit',
      title: intl.get('hims.messageCenter.view.message.edit').d('编辑问题'),
      drawer: true,
      style: {
        width: 720,
      },
      children: (
        <EditDrawer
          editData={record}
          languageType={languageType}
          onHandleStaticTextEditorRef={this.handleStaticTextEditorRef}
        />
      ),
      onOk: this.handleSaveEdit,
      onCancel: () => {
        this.signDs.reset();
      },
      onClose: () => {
        this.signDs.reset();
      },
    });
  }

  /**
   * 保存编辑问题数据
   */
  @Bind()
  async handleSaveEdit() {
    if (this.staticTextEditorRef) {
      const { editor } = (this.staticTextEditorRef.staticTextEditor || {}).current;
      if (!editor || !editor.getData()) {
        notification.warning({
          message: intl
            .get('hims.knowledgeCategory.view.message.alert.contentRequired')
            .d('请输入答案内容'),
        });
        return false;
      } else {
        this.signDs.current.set('answerDesc', editor.getData());
      }
    }
    const validate = await this.signDs.submit();
    if (!validate) {
      return false;
    }
    this.signDs.query();
  }

  /**
   * 渲染审核状态
   * @param {string} value
   */
  @Bind()
  checkStatusRender(value, text) {
    switch (value) {
      case 'wait_check':
        return (
          <Tag color="orange" key="wait_check">
            {text}
          </Tag>
        );
      case 'checked':
        return (
          <Tag color="green" key="checked">
            {text}
          </Tag>
        );
      case 'rejected':
        return (
          <Tag color="red" key="rejected">
            {text}
          </Tag>
        );
      default:
        break;
    }
  }

  @Bind()
  handleStaticTextEditorRef(staticTextEditorRef) {
    this.staticTextEditorRef = staticTextEditorRef;
  }

  @Bind()
  handlePreviewCancel() {
    this.setState({
      previewImages: [],
      previewVisible: false,
    });
  }

  @Bind()
  handlePreview(url) {
    this.setState({
      previewImages: [
        {
          src: url || '',
          alt: '', // 由于下方会显示 alt 所以这里给空字符串 file.name,
        },
      ],
      previewVisible: true,
    });
  }

  render() {
    const {
      ws,
      record,
      spinning,
      groupKey,
      groupList = [],
      isSelected,
      currentKey,
      statistics,
      chatVisible,
      messageList,
      defaultStatus,
      messageAmount,
      previewImages,
      previewVisible,
      fetchMessageListLoading,
    } = this.state;
    const { location } = this.props;
    const chatDrawerProps = {
      isSelected,
      visible: chatVisible,
      currentKey,
      groupKey: record.get && record.get('csGroupId'),
      record,
      ws,
      id: this.id,
      messageList,
      // onUpdateMessageList: this.handleUpdateMessageList,
      fetchMessageListLoading,
      defaultStatus,
      accessToken: getAccessToken(),
      organizationId: getCurrentOrganizationId(),
      messageListDs: this.messageListDs,
      onUpdate: (arr) => {
        const { messageList: OriginMessageList } = this.state;
        this.setState({ messageList: [...arr, ...OriginMessageList] });
      },
      onCancel: this.handleCancel,
      onOpenModal: this.showBaseModal,
      onPreview: this.handlePreview,
      onChange: this.handleChangeMessageList,
    };
    return (
      <>
        <Header
          title={intl.get('hims.messageCenter.view.message.title.messageCenter').d('客服消息中心')}
        >
          {groupList.length > 0 && (
            <div className="im-message-center">
              <Select
                value={groupKey}
                placeholder={intl
                  .get('hims.messageCenter.view.message.title.allGroups')
                  .d('全部群组')}
                allowClear
                onChange={this.handleCustomerChange}
              >
                {groupList.map((item) => (
                  <Select.Option value={item.groupKey} key={item.groupKey}>
                    {item.groupName}
                  </Select.Option>
                ))}
              </Select>

              <Button style={{ marginLeft: '8px' }} icon="sync" onClick={this.handleRefresh}>
                {intl.get('hzero.common.button.refresh').d('刷新')}
              </Button>
            </div>
          )}
        </Header>
        <Content>
          <Spin spinning={spinning} wrapperClassName="im-message-center">
            {!spinning && groupList.length === 0 && (
              <p style={{ textAlign: 'center', color: 'grey' }}>
                {intl.get('hims.messageCenter.view.message.title.noGroup').d('当前暂无客服群组')}
              </p>
            )}

            {groupList.length !== 0 && (
              <Tabs
                defaultActiveKey={defaultStatus}
                tabPosition="left"
                animated={false}
                onChange={this.handleStatusChange}
              >
                <TabPane
                  tab={
                    <>
                      <Icon type="database" style={{ fontSize: '0.14rem' }} />
                      {intl.get('hims.messageCenter.model.message.title.myServer').d('统计数据')}
                    </>
                  }
                  key="server"
                >
                  <div className={styles['message-tips']}>
                    <div>
                      <div>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.satisfaction')
                            .d('今日会话满意度')}
                        </span>
                      </div>
                      <Popover
                        placement="left"
                        content={
                          <div>
                            <div style={{ textAlign: 'center' }}>
                              {intl
                                .get('hims.messageCenter.model.messageCenter.satisfactionDetail')
                                .d('评价详情')}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                {' '}
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.today &&
                                statistics.satisfaction.today.OneStar) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.today &&
                                statistics.satisfaction.today.TwoStars) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.today &&
                                statistics.satisfaction.today.ThreeStars) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.today &&
                                statistics.satisfaction.today.FourStars) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                :
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.today &&
                                statistics.satisfaction.today.FiveStars) ||
                                0}
                            </div>
                          </div>
                        }
                      >
                        <Progress
                          width={100}
                          type="circle"
                          value={
                            (statistics &&
                              statistics.satisfaction &&
                              statistics.satisfaction.today &&
                              statistics.satisfaction.today.Satisfied / statistics &&
                              statistics.satisfaction &&
                              statistics.satisfaction.today &&
                              statistics.satisfaction.today.TotalEvaTimes) * 100 || 0
                          }
                          format={(v) => `${v.toFixed(0)}%`}
                        />
                      </Popover>
                    </div>
                    <div className={styles['message-tip-item']}>
                      <Icon type="stars" style={{ fontSize: 118, color: '#08c' }} />
                      <div className={styles['message-tip-item-text']}>
                        <span className={styles['message-num']}>
                          {(statistics && statistics.score && statistics.score.today) || 0}
                        </span>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.score')
                            .d('今日访客评分')}
                        </span>
                      </div>
                    </div>
                    <div className={styles['message-tip-item']}>
                      <Icon type="people" style={{ fontSize: 118, color: '#08c' }} />
                      <div className={styles['message-tip-item-text']}>
                        <span className={styles['message-num']}>
                          {(statistics && statistics.receptions && statistics.receptions.today) ||
                            0}
                        </span>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.visitor')
                            .d('今日接待访客数')}
                        </span>
                      </div>
                    </div>
                    <div className={styles['message-tip-item']}>
                      <Icon type="number" style={{ fontSize: 118, color: '#08c' }} />
                      <div className={styles['message-tip-item-text']}>
                        <span className={styles['message-num']}>
                          {(statistics && statistics.evaluate && statistics.evaluate.today) || 0}
                        </span>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.evaluate')
                            .d('今日访客评价数')}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className={styles['message-tips']}>
                    <div>
                      <div>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.totalSatisfaction')
                            .d('总满意度')}
                        </span>
                      </div>
                      <Popover
                        placement="left"
                        content={
                          <div>
                            <div style={{ textAlign: 'center' }}>
                              {intl
                                .get('hims.messageCenter.model.messageCenter.satisfactionDetail')
                                .d('评价详情')}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                {' '}
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.total &&
                                statistics.satisfaction.total.OneStar) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.total &&
                                statistics.satisfaction.total.TwoStars) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.total &&
                                statistics.satisfaction.total.ThreeStars) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon type="star" style={{ fontSize: 12, color: '#6d7a80' }} />:
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.total &&
                                statistics.satisfaction.total.FourStars) ||
                                0}
                            </div>
                            <div className={styles['message-popover-item']}>
                              <div className={styles['message-popover-item-icons']}>
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                <Icon
                                  type="star"
                                  style={{ fontSize: 12, color: 'rgb(251, 187, 73)' }}
                                />
                                :
                              </div>
                              {(statistics &&
                                statistics.satisfaction &&
                                statistics.satisfaction.total &&
                                statistics.satisfaction.total.FiveStars) ||
                                0}
                            </div>
                          </div>
                        }
                      >
                        <Progress
                          width={100}
                          type="circle"
                          value={
                            (statistics &&
                              statistics.satisfaction &&
                              statistics.satisfaction.total &&
                              statistics.satisfaction.total.Satisfied / statistics &&
                              statistics.satisfaction &&
                              statistics.satisfaction.total &&
                              statistics.satisfaction.total.TotalEvaTimes) * 100 || 0
                          }
                          format={(v) => `${v.toFixed(0)}%`}
                        />
                      </Popover>
                    </div>
                    <div className={styles['message-tip-item']}>
                      <Icon type="stars" style={{ fontSize: 118, color: '#08c' }} />
                      <div className={styles['message-tip-item-text']}>
                        <span className={styles['message-num']}>
                          {(statistics && statistics.score && statistics.score.total) || 0}
                        </span>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.totalScore')
                            .d('总访客评分')}
                        </span>
                      </div>
                    </div>
                    <div className={styles['message-tip-item']}>
                      <Icon type="people" style={{ fontSize: 118, color: '#08c' }} />
                      <div className={styles['message-tip-item-text']}>
                        <span className={styles['message-num']}>
                          {(statistics && statistics.receptions && statistics.receptions.total) ||
                            0}
                        </span>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.totalVisitor')
                            .d('总接待访客数')}
                        </span>
                      </div>
                    </div>

                    <div className={styles['message-tip-item']}>
                      <Icon type="number" style={{ fontSize: 118, color: '#08c' }} />
                      <div className={styles['message-tip-item-text']}>
                        <span className={styles['message-num']}>
                          {(statistics && statistics.evaluate && statistics.evaluate.total) || 0}
                        </span>
                        <span className={styles['message-tip']}>
                          {intl
                            .get('hims.messageCenter.model.messageCenter.totalEvaluate')
                            .d('总访客评价数')}
                        </span>
                      </div>
                    </div>
                  </div>
                </TabPane>
                <TabPane
                  tab={
                    <>
                      <Icon type="message_notification" style={{ fontSize: '0.14rem' }} />
                      {intl.get('hims.messageCenter.view.message.title.running').d('会话列表')}
                      {messageAmount > 0 && (
                        <span className={styles['message-amount']}>{messageAmount}</span>
                      )}
                    </>
                  }
                  key="running"
                >
                  <Table columns={this.runningColumns} queryBar="none" dataSet={this.initDs} />
                </TabPane>
                <TabPane
                  tab={
                    <>
                      <Icon type="timer" style={{ fontSize: '0.14rem' }} />
                      {intl.get('hims.messageCenter.view.message.title.closed').d('历史会话')}
                    </>
                  }
                  key="closed"
                >
                  <Table columns={this.closeColumns} queryBar="none" dataSet={this.initDs} />
                </TabPane>
                <TabPane
                  tab={
                    <>
                      <Icon type="event_note" style={{ fontSize: '0.14rem' }} />
                      {intl.get('hims.messageCenter.view.message.title.feedback').d('反馈消息')}
                    </>
                  }
                  key="feedback"
                >
                  <Table columns={this.messageColumns} queryBar="none" dataSet={this.messageDs} />
                </TabPane>
                <TabPane
                  tab={
                    <>
                      <Icon type="knowledge" style={{ fontSize: '0.14rem' }} />
                      {intl.get('hims.messageCenter.view.message.title.sign').d('标记知识')}
                    </>
                  }
                  key="sign"
                >
                  <Table columns={this.signColumns} queryBar="none" dataSet={this.signDs} />
                </TabPane>
              </Tabs>
            )}
            <ModalContainer location={location} />
            <ChatDrawer {...chatDrawerProps} />
          </Spin>
          <Viewer
            noImgDetails
            noNavbar
            scalable={false}
            changeable={false}
            visible={previewVisible}
            onClose={this.handlePreviewCancel}
            images={previewImages}
          />
        </Content>
      </>
    );
  }
}
