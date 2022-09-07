/**
 * Detail - 待办事项明细
 * @date: 2018-8-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component, Fragment } from 'react';
import { Button, Col, Divider, Form, Input, Modal, Row, Spin, Tabs, Tag } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { forEach, uniqWith, isEqual, isEmpty, isObject } from 'lodash';
import { routerRedux } from 'dva/router';
import classNames from 'classnames';
import moment from 'moment';

import UploadModal from 'components/Upload';
import { Content, Header } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { closeTab, updateTab, getActiveTabKey } from 'utils/menuTab';
import notification from 'utils/notification';
import { HZERO_FILE, BKT_HWFP } from 'utils/config';

import ApproveHistory from '../../components/ApproveHistory';
import FlowChart from '../../components/FlowChart';
import EmployeeDrawer from './EmployeeDrawer';
import ApproveForm from '../../components/ApproveForm';
import ApproverDrawer from './ApproverDrawer';
import OrderFlowDrawer from './OrderFlowDrawer';
import JumpModal from './JumpModal';
import ApproveHistoryRecord from '../../components/ApproveHistoryRecord';
import styles from './index.less';

const { TextArea } = Input;

@Form.create({
  fieldNameProp: null,
})
@formatterCollections({
  code: ['hwfp.task', 'entity.position', 'entity.department', 'entity.employee', 'hwfp.common'],
})
@connect(({ task, loading }) => ({
  task,
  fetchForecastLoading: loading.effects['task/fetchForecast'],
  fetchDetailLoading: loading.effects['task/fetchDetail'],
  fetchEmployeeLoading: loading.effects['task/fetchEmployeeList'],
  fetchHistoryApprovalLoading: loading.effects['task/fetchHistoryApproval'],
  getAllUserListLoading: loading.effects['task/getAllUserList'],
  approveLoading: loading.effects['task/taskAgree'],
  refuseLoading: loading.effects['task/taskRefuse'],
  tenantId: getCurrentOrganizationId(),
}))
export default class Detail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      delegateOrAddUser: '',
      drawerVisible: false, // 侧边框是否可见
      // organizationName: '我不是',
      taskId: this.props.match.params.id,
      // approveResult: [{ action: 'Approved', name: intl.get('hwfp.task.view.option.agree').d('同意') }, { action: 'Rejected', name: intl.get('hwfp.task.view.option.refuse').d('拒绝') }],
      approveResult: [],
      approveAction: '',
      approveBtnTitle: {},
      approvalCommentRequire: false,
      comment: '', // 审批意见
      commentNotice: '', // 审批意见提示语
      approveCommentTitle: '审批意见', // 审批意见form标题
      isLoadApproveResult: false,
      isCarbon: false,
      isAssignNextApprover: false,
      carbonCopyUser: [],
      currentDetailData: {},
      carbonCopyUserList: [], // 存储抄送人数据
      approverDrawerVisible: false, // 审批人选择侧边框
      approveList: [], // 审批人列表数据
      jumpVisible: false,
      attachmentUuid: '',
      currentAddApprover: '',
      sequenceList: [], // 顺序流节点列表数据
      orderFlowDrawerVisible: false, // 指定顺序流列表框
      // specifiedVariableName: '', //
      formInvokeFlag: false, // the flag of invoke approveForm submit
      historyApprovalRecords: [],
      mergeHistoryFlag: true, // 合并审批历史展示标识
    };

    this.selectedCopyUsersId = null;
    this.approveFormChildren = null;

    this.executionVariables = {};
    this.approveResultWithForm = '';
  }

  /**
   * 生命周期函数
   *render调用后，获取页面展示数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    const { taskId } = this.state;
    // 清除缓存
    dispatch({
      type: 'task/updateDetailState',
      payload: { taskId, detail: {}, uselessParam: 'init' },
    });
    this.handleSearch();
    this.getJumpList();
    this.fetchUuid();
  }

  /**
   * 加载流程图
   */
  @Bind()
  loadForecastDiagram() {
    const {
      dispatch,
      tenantId,
      match: {
        params: { processInstanceId },
      },
    } = this.props;
    dispatch({
      type: 'task/fetchForecast',
      payload: {
        tenantId,
        Id: processInstanceId,
      },
    });
  }

  @Bind()
  handleSearch() {
    const { dispatch, tenantId } = this.props;
    const { taskId, isLoadApproveResult } = this.state;
    const { approveResult, approveCommentTitle } = this.state;
    let newApproveCommentTitle = approveCommentTitle;
    const approveBtnTitle = {
      delegate: intl.get('hwfp.task.view.option.delegate', { name: '转交' }).d('转交'),
      addSign: intl.get('hwfp.task.view.option.addUser', { name: '加签' }).d('加签'),
      ApproveAndAddSign: intl
        .get('hwfp.task.view.option.ApproveAndAddSign', { name: `同意并加签` })
        .d('同意并加签'),
      Rejected: intl.get('hwfp.task.view.option.rejected', { name: '审批拒绝' }).d('审批拒绝'),
      Approved: intl.get('hwfp.task.view.option.approved', { name: '审批通过' }).d('审批通过'),
      Jumped: intl.get('hwfp.task.view.option.jumped', { name: '驳回' }).d('驳回'),
    };
    dispatch({
      type: 'task/fetchDetail',
      payload: {
        tenantId,
        taskId,
      },
    }).then((res) => {
      this.handleSelectableJump();
      if (res) {
        this.handleUpdateTab(res);
        let formInvokeFlag = false;
        let approvalCommentRequire = false;
        let commentNotice = '';
        this.loadForecastDiagram(res);
        const { formProperties } = res.formData;
        // 审批人数据
        // const { nextNodeApprover = [] } = res;
        // const { candidates = [] } = nextNodeApprover;

        if (isLoadApproveResult) {
          return;
        }
        forEach(formProperties, (prop) => {
          // if (prop.id === 'CUSTOM_APPROVE_RESULT' && prop.type === 'enum') {
          //   // approveResult = [];
          //   forEach(prop.enumValues, result => {
          //     //
          //     const approve = {};
          //     approve.action = result.id;
          //     approve.name = result.name;
          //     approveResult.push(approve);
          //   });
          // }
          if (prop.id === 'APPROVAL_ACTION' && prop.type === 'enum') {
            forEach(prop.enumValues, (result) => {
              //
              if (
                result.id === 'ACTION_DELEGATE' &&
                result.name === 'Y'
                // delegationState !== 'pending'
              ) {
                const delegate = {};
                delegate.action = 'delegate';
                approveResult.push(delegate);
              } else if (result.id === 'ACTION_ADD_SIGN' && result.name === 'Y') {
                const sign = {};
                const approveSign = {};
                sign.action = 'addSign';
                approveResult.push(sign);
                approveSign.action = 'ApproveAndAddSign';
                approveResult.push(approveSign);
              } else if (result.id === 'ACTION_REJECT' && result.name === 'Y') {
                const reject = {};
                reject.action = 'Rejected';
                approveResult.push(reject);
              } else if (result.id === 'ACTION_APPROVE' && result.name === 'Y') {
                const approve = {};
                approve.action = 'Approved';
                approveResult.push(approve);
              } else if (result.id === 'ACTION_JUMP' && result.name === 'Y') {
                const jump = {};
                jump.action = 'Jumped';
                approveResult.push(jump);
              }
            });
          }

          // 审批按钮文字自定义
          if (prop.id === 'APPROVAL_ACTION_TITLE' && prop.type === 'enum') {
            forEach(prop.enumValues, (result) => {
              const { name } = result;
              if (name) {
                if (result.id === 'ACTION_DELEGATE') {
                  approveBtnTitle.delegate = intl
                    .get('hwfp.task.view.option.delegate', { name })
                    .d(name);
                } else if (result.id === 'ACTION_ADD_SIGN') {
                  approveBtnTitle.addSign = intl
                    .get('hwfp.task.view.option.addUser', { name })
                    .d(name);
                  approveBtnTitle.ApproveAndAddSign = intl
                    .get('hwfp.task.view.option.ApproveAndAddSign', { name: `同意并${name}` })
                    .d(`同意并${name}`);
                } else if (result.id === 'ACTION_REJECT') {
                  approveBtnTitle.Rejected = intl
                    .get('hwfp.task.view.option.rejected', { name })
                    .d(name);
                } else if (result.id === 'ACTION_APPROVE') {
                  approveBtnTitle.Approved = intl
                    .get('hwfp.task.view.option.approved', { name })
                    .d(name);
                } else if (result.id === 'ACTION_JUMP') {
                  approveBtnTitle.Jumped = intl
                    .get('hwfp.task.view.option.jumped', { name })
                    .d(name);
                }
              }
            });
          }
          // 审批意见文字自定义
          if (prop.id === 'ACTION_COMMENT_TITLE') {
            newApproveCommentTitle = prop.name;
          }

          // 审批同意时审批意见必输标识
          if (prop.id === 'APPROVAL_NEED_OPINION_FLAG') {
            approvalCommentRequire = prop.name === 'Y';
          }

          if (res.formKey && prop.id === 'FORM_INVOKE_FLAG') {
            formInvokeFlag = true;
          }
        });
        //
        if (res.comment) {
          commentNotice = intl
            .get('hwfp.task.view.message.restoredLatestDraft')
            .d('已恢复上一次保存的草稿');
        }
        this.setState({
          approveResult,
          approveBtnTitle,
          comment: res.comment || '',
          commentNotice,
          approveCommentTitle: newApproveCommentTitle,
          approvalCommentRequire,
          isLoadApproveResult: true,
          currentDetailData: res,
          // approveList: candidates,
          formInvokeFlag,
          mergeHistoryFlag: res.mergeHistory,
        });
        this.fetchHistoryRecord(res);
      }
    });
  }

  @Bind()
  handleUpdateTab(res = {}) {
    const { assigneeName, processInstance = {} } = res;
    const { processDefinitionName } = processInstance;
    if (assigneeName && processDefinitionName) {
      updateTab({
        key: getActiveTabKey(),
        title: `${processDefinitionName}-${assigneeName}`,
      });
    }
  }

  @Bind()
  getJumpList() {
    const { dispatch, tenantId } = this.props;
    const { taskId } = this.state;
    dispatch({
      type: 'task/getJumpList',
      payload: {
        tenantId,
        taskId,
      },
    });
  }

  @Bind()
  fetchUuid() {
    const { dispatch } = this.props;
    dispatch({
      type: 'task/fetchUuid',
    }).then((res) => {
      if (res) {
        this.setState({
          attachmentUuid: res.content,
        });
      }
    });
  }

  // 查询审批历史
  @Bind()
  fetchHistoryRecord(currentDetailData) {
    const { businessKey } = currentDetailData;
    if (businessKey) {
      this.props
        .dispatch({
          type: 'task/fetchHistoryApproval',
          params: {
            businessKey,
          },
        })
        .then((res) => {
          if (res) {
            this.setState({
              historyApprovalRecords: res || [],
            });
          }
        });
    }
  }

  @Bind()
  taskAction(dataParams) {
    const { carbonCopyUserList = [], taskId, attachmentUuid } = this.state;
    const {
      tenantId,
      form,
      dispatch,
      match,
      task: { [taskId]: { detail = {} } } = {},
    } = this.props;
    const { nextNodeApprover, owner } = detail;
    const taskDetails = detail;
    const data = dataParams || {};
    data.action = data.action || 'complete';
    if (taskDetails.delegationState === 'pending' && data.action !== 'delegate') {
      data.action = 'resolve';
    }
    const variables = [];
    let comment = form.getFieldValue('comment');
    // 同意申请时设置默认审议意见
    if (!comment) {
      comment = intl.get('hwfp.task.view.option.approved', { name: '审批通过' }).d('审批通过');
    }
    if (data.action !== 'delegate') {
      const formVars = {};
      formVars.approveResult = data.approveResult || 'Approved';
      formVars.comment = comment;
      // 可以指定审批人时才处理
      const isAddSign =
        owner && (owner.startsWith('addSign') || owner.startsWith('ApproveAndAddSign'));
      if (!isAddSign) {
        nextNodeApprover.map((item) => {
          if (item.check !== 'N') {
            const appointor = form
              .getFieldValue(`nextActId-${item.nextActId}`)
              .map((r) => r.employeeCode)
              .join();
            variables.push({ name: `nextActId-${item.nextActId}`, value: appointor });
          }
          return variables;
        });
      }
      for (const k in formVars) {
        if ({}.hasOwnProperty.call(formVars, k)) {
          variables.push({ name: k, value: formVars[k] });
        }
      }
    }
    this.handleFetchCount().then((res) => {
      if (res !== undefined) {
        const params = {
          type: 'task/taskAgree',
          payload: {
            tenantId,
            comment,
            variables: variables.filter((r) => r.value !== ''),
            carbonCopyUsers: carbonCopyUserList.map((item) => item.employeeNum).join(),
            currentTaskId: taskDetails.id,
            [data.action === 'addSign' || data.action === 'ApproveAndAddSign'
              ? 'assignList'
              : 'assignee']: data.targetUser || null,
            action: data.action,
            jumpTarget: data.jumpTarget || null,
            jumpTargetName: data.jumpTargetName || null,
            attachmentUuid: res > 0 ? attachmentUuid : null,
          },
        };
        dispatch(params).then((r) => {
          if (r) {
            notification.success();
            // todo审批完跳转到列表页面(并要刷新) 当列表界面和办理页面之间还有其他Tab标签页时，会跳到那个标签页
            this.setState(
              {
                drawerVisible: false,
              },
              () => {
                closeTab(`/hwfp/task/detail/${match.params.id}/${match.params.processInstanceId}`);
                dispatch(routerRedux.push({ pathname: `/hwfp/task` }));
              }
            );
          }
        });
      }
    });
  }

  // 渲染审批按钮
  @Bind()
  renderApproveBtn() {
    const {
      approveAction,
      approveResult,
      taskId,
      approveBtnTitle = {},
      currentDetailData = {},
    } = this.state;
    const { owner } = currentDetailData;
    const {
      approveLoading,
      refuseLoading,
      task: { [taskId]: { jumpList = [] } = {} },
    } = this.props;
    // 前加签、后加签只需要审批同意或拒绝
    const isAddSign =
      owner && (owner.startsWith('addSign') || owner.startsWith('ApproveAndAddSign'));
    const jumpItem = approveResult.find((item) => item.action === 'Jumped');
    return (
      <React.Fragment>
        {approveResult.map((result) => {
          let typeValue = 'default';
          if (result.action === 'Rejected') {
            typeValue = 'danger';
          } else if (result.action === 'Approved') {
            typeValue = 'primary';
          } else if (isAddSign) {
            return null;
          } else if (result.action === 'Jumped') {
            return null;
          }
          return (
            <Button
              key={result.action}
              type={typeValue}
              loading={approveAction === result.action && (approveLoading || refuseLoading)}
              onClick={() => this.executeTaskAction(result.action)}
              style={{ marginRight: 12 }}
            >
              {approveBtnTitle[result.action]}
            </Button>
          );
        })}
        {!isAddSign && jumpItem && jumpList.length > 0 && (
          <Button onClick={() => this.hideJump(true)}>{approveBtnTitle.Jumped}</Button>
        )}
      </React.Fragment>
    );
  }

  @Bind()
  jumpActivity(data, jumpData) {
    const {
      dispatch,
      tenantId,
      match: {
        params: { id, processInstanceId },
      },
    } = this.props;
    dispatch({
      type: 'task/taskAgree',
      payload: {
        currentTaskId: id,
        tenantId,
        assignee: jumpData.approver,
        action: 'jump',
        comment: data.comment,
        jumpTarget: jumpData.actId,
        jumpTargetName: jumpData.actName,
      },
    }).then((res) => {
      if (res) {
        this.hideJump(false);
        notification.success();
        closeTab(`/hwfp/task/detail/${id}/${processInstanceId}`);
        dispatch(routerRedux.push({ pathname: `/hwfp/task` }));
      }
    });
  }

  @Bind()
  hideJump(flag) {
    this.setState({ jumpVisible: flag });
  }

  // 获取审批动作名称
  @Bind()
  getActionName(action) {
    const { approveBtnTitle = {} } = this.state;
    return approveBtnTitle[action];
  }

  // 处理选择的抄送人显示格式
  @Bind()
  processCarbonCopy() {
    const { taskId } = this.state;
    const {
      task: {
        [taskId]: { changeEmployee = [] },
      },
    } = this.props;
    this.setState({
      drawerVisible: false,
      carbonCopyUser: changeEmployee.slice(),
    });
    const { carbonCopyUserList } = this.state;
    const filterList = uniqWith([...carbonCopyUserList, ...changeEmployee], isEqual);
    this.selectedCopyUsersId = filterList.map((item) => item.employeeId);
    // 保存选择的抄送人
    this.setState({
      carbonCopyUserList: filterList,
    });
  }

  /**
   * 关闭抄送人标签处理
   * @param {object} e - 事件对象
   */
  @Bind()
  closeCarbonCopyUser(item) {
    const { carbonCopyUserList = [] } = this.state;
    const filterUser = carbonCopyUserList.filter((user) => user.employeeId !== item.employeeId);
    this.setState({ carbonCopyUserList: filterUser });
  }

  @Bind()
  handleCancel() {
    this.setState({
      drawerVisible: false,
    });
  }

  @Bind()
  showCarbonCopyModal() {
    const { carbonCopyUser, taskId } = this.state;
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'task/updateDetailState',
      payload: { taskId, changeEmployee: carbonCopyUser },
    });
    dispatch({
      type: 'task/fetchEmployeeList',
      payload: {
        tenantId,
        lovCode: 'HPFM.EMPLOYEE',
        enabledFlag: 1,
      },
    });
    this.setState({
      isCarbon: true,
      isAssignNextApprover: false,
      drawerVisible: true,
      delegateOrAddUser: intl.get('hwfp.task.view.option.addCc').d('添加抄送'),
    });
  }

  /**
   * 显示审批人选择窗口
   */
  @Bind()
  showApproverModal(record) {
    const { form, dispatch, tenantId } = this.props;
    if (record.needAppoint === 'Y') {
      // this.queryUserList();
      dispatch({
        type: 'task/fetchEmployeeList',
        payload: {
          tenantId,
          lovCode: 'HPFM.EMPLOYEE',
          enabledFlag: 1,
        },
      });
    }
    this.setState({
      approveList: form.getFieldValue(`nextActId-${record.nextActId}`),
      drawerVisible: true,
      isCarbon: false,
      isAssignNextApprover: true,
      currentAddApprover: record,
      appointerList: record.needAppoint === 'N' ? record.candidates : [],
    });
  }

  /**
   * 查询所有用户列表
   * @param {*} params
   */
  @Bind()
  queryUserList(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'task/getAllUserList',
      payload: params,
    });
  }

  /**
   * 关闭审批人选择窗口
   */
  @Bind()
  handleApproverCancel() {
    this.setState({ approverDrawerVisible: false });
  }

  /**
   * 前端过滤审批人
   * @param {*} params
   */
  @Bind()
  filterCandidates(params = {}) {
    const { employeeNum, name } = params;
    const { currentAddApprover } = this.state;
    let appointerList = currentAddApprover.needAppoint === 'N' ? currentAddApprover.candidates : [];
    if (employeeNum) {
      appointerList = appointerList.filter((item) => item.employeeCode.includes(employeeNum));
    }
    if (name) {
      appointerList = appointerList.filter((item) => item.name.includes(name));
    }
    this.setState({
      appointerList: appointerList || [],
    });
  }

  /**
   * 添加指定审批人
   * @param {*} approveList
   * @param {*} func
   */
  @Bind()
  handleApproverOk(selectedRows = [], func) {
    const { currentAddApprover, approveList } = this.state;
    const { form } = this.props;
    let newSelectedRows = selectedRows;
    if (currentAddApprover.needAppoint === 'Y') {
      newSelectedRows = newSelectedRows.map((item) => ({
        ...item,
        employeeCode: item.employeeNum,
      }));
    }
    // 数据去重 lodash的uniqWith(object, isEqual) 方法
    const filterList = uniqWith([...newSelectedRows, ...approveList], isEqual);
    form.setFieldsValue({
      [`nextActId-${currentAddApprover.nextActId}`]: filterList,
    });
    if (func && typeof func === 'function') {
      func();
    }
    this.setState({
      isAssignNextApprover: false,
      drawerVisible: false,
    });
  }

  /**
   * 删除指定审批人
   * 优化前：采用state存储指定审批人列表信息，会导致不知道是哪条记录的state
   * 优化后： 从表单中取值，也从表单中删除
   * @param {*} item
   * @param {*} n
   */
  @Bind()
  closeApprover(item, n) {
    const { form } = this.props;
    const filterList = form
      .getFieldValue(`nextActId-${n.nextActId}`)
      .filter((user) => user.employeeId !== item.employeeId);
    form.setFieldsValue({
      [`nextActId-${n.nextActId}`]: filterList,
    });
  }

  /**
   * 查询表单
   * @param {*} fieldsValue
   */
  @Bind()
  handleUserSearch(fieldsValue) {
    // 无法实现模糊查询，需后端优化
    this.queryUserList({
      ...fieldsValue,
    });
  }

  /**
   * 分页切换
   * @param {*} pagination
   */
  @Bind()
  handlePageChange(pagination = {}, fieldsValue) {
    // TODO: 工作流分页存在问题
    this.queryUserList({
      page: pagination,
      pageSize: pagination.pageSize,
      ...fieldsValue,
    });
  }

  /**
   * 执行审批动作.
   * @param action 执行审批动作
   */
  executeTaskAction(action) {
    const { form } = this.props;
    const { currentDetailData, approvalCommentRequire } = this.state;
    const { nextNodeApprover, owner } = currentDetailData;
    if (!isEmpty(nextNodeApprover)) {
      let checkNextNodeApprover = true;
      const requiredNode = nextNodeApprover.find(
        (item) => item.rejectedNeedAppoint === (action === 'Rejected' ? 'Y' : 'N')
      );
      if (requiredNode) {
        const nextActId = form.getFieldValue(`nextActId-${requiredNode.nextActId}`);
        if (action === 'Rejected' && isEmpty(nextActId)) {
          checkNextNodeApprover = false;
        } else if (
          action !== 'Rejected' &&
          requiredNode.needAppoint === 'Y' &&
          isEmpty(nextActId)
        ) {
          checkNextNodeApprover = false;
        }
      }
      // 加签、转交，驳回，回退上一审批人（暂无该按钮）都不判断，同意、拒绝、同意并加签需要判断
      const isAddSign =
        owner && (owner.startsWith('addSign') || owner.startsWith('ApproveAndAddSign'));
      // 被加签人不需要判断
      if (!isAddSign && !['addSign', 'delegate'].includes(action) && !checkNextNodeApprover) {
        Modal.warning({
          title: intl
            .get('hwfp.task.view.message.title.needNextApprover', {
              nextActName: requiredNode.nextActName,
            })
            .d(`请指定【${requiredNode.nextActName}】的审批人`),
        });
        return;
      }
    }
    // 审批同意时 审批意见给默认值
    if (action === 'Approved' && !approvalCommentRequire) {
      this.openModal(action);
    } else {
      this.props.form.validateFields((err) => {
        if (!err) {
          this.openModal(action);
        }
      });
    }
  }

  @Bind()
  openModal(action) {
    this.setState({ approveAction: action });
    if (action === 'delegate' || action === 'addSign' || action === 'ApproveAndAddSign') {
      const { dispatch, tenantId } = this.props;
      dispatch({
        type: 'task/fetchEmployeeList',
        payload: {
          tenantId,
          lovCode: 'HPFM.EMPLOYEE',
          enabledFlag: 1,
        },
      });
      this.setState({
        drawerVisible: true,
        isCarbon: false,
        isAssignNextApprover: false,
        delegateOrAddUser: action,
      });
    } else {
      Modal.confirm({
        className: styles['confirm-modal'],
        title: intl.get('hwfp.common.view.message.confirm').d('确认'),
        content: intl
          .get('hwfp.task.view.message.title.confirmTip', {
            actionName: this.getActionName(action),
          })
          .d(`确认${this.getActionName(action)}吗?`),
        onOk: () => {
          const { formInvokeFlag } = this.state;
          // 如果没有审批表单或不需要回调 直接执行审批动作；
          // 否则向审批表单界面发送审批意见消息,监听审批表单返回消息后再执行审批动作
          if (!formInvokeFlag) {
            this.taskAction({ approveResult: action });
          } else if (this.approveFormChildren) {
            // 向审批表单界面 发送审批意见消息
            this.approveFormChildren.submit(action);
          }
        },
      });
    }
  }

  /**
   * 获取顺序流节点
   */
  @Bind
  handleSelectableJump() {
    const {
      dispatch,
      tenantId,
      // task: { [taskId]: { detail: { processInstanceId } } = {} },
      match: {
        params: { processInstanceId },
      },
    } = this.props;
    if (processInstanceId) {
      dispatch({
        type: 'task/fetchOrderFlowJump',
        payload: {
          processInstanceId,
          tenantId,
        },
      });
    }
  }

  /**
   * 显示顺序流节点选择窗口
   */
  @Bind
  showOrderFlowModal() {
    const { taskId } = this.state;
    const {
      task: { [taskId]: { orderFlowList: [{ sequenceList }] } = {} },
    } = this.props;
    this.setState({
      sequenceList,
      orderFlowDrawerVisible: true,
    });
  }

  /**
   * 关闭顺序流节点选择窗口
   */
  @Bind
  handleOrderFlowCancel() {
    this.setState({ orderFlowDrawerVisible: false });
  }

  /**
   * 提交选中顺序流节点
   * @param {*} [selectedRows=[]] - 选中的节点数据
   */
  @Bind
  handleOrderFlowOk(selectedRows = []) {
    const [{ id }] = selectedRows;
    const { taskId } = this.state;
    const {
      dispatch,
      tenantId,
      task: {
        [taskId]: {
          orderFlowList: [{ specifiedVariableName }],
          detail: { processInstanceId },
        } = {},
      },
    } = this.props;
    dispatch({
      type: 'task/fetchOrderFlowNode',
      payload: {
        tenantId,
        processInstanceId,
        specifiedVariableName,
        specifiedVariableValue: id,
      },
    });
  }

  @Bind()
  handleFetchCount() {
    const { dispatch } = this.props;
    const { attachmentUuid } = this.state;
    return dispatch({
      type: 'task/fetchFileCount',
      payload: {
        attachmentUUID: attachmentUuid,
      },
    });
  }

  @Bind()
  handleSaveComment(event) {
    const newComment = event.target.value;
    const { taskId, comment = '' } = this.state;
    const { dispatch } = this.props;
    if (newComment !== comment) {
      dispatch({
        type: 'task/saveTaskComment',
        params: {
          taskId,
          comment: newComment,
        },
      }).then((res) => {
        if (isObject(res) && isEmpty(res)) {
          this.setState({
            comment: newComment,
            commentNotice: intl
              .get('hwfp.task.view.message.saveDraft')
              .d(`于${moment().format('YYYY-MM-DD H:mm:ss')}时保存草稿`),
          });
        }
      });
    }
  }

  render() {
    const {
      drawerVisible,
      delegateOrAddUser,
      isCarbon,
      isAssignNextApprover,
      taskId,
      currentDetailData,
      carbonCopyUserList,
      approverDrawerVisible,
      orderFlowDrawerVisible,
      appointerList,
      approveList = [],
      jumpVisible = false,
      attachmentUuid,
      currentAddApprover,
      sequenceList,
      comment,
      commentNotice,
      approveCommentTitle,
      historyApprovalRecords = [],
      mergeHistoryFlag,
    } = this.state;
    const {
      form: { getFieldDecorator, getFieldValue },
      approveLoading = false,
      fetchDetailLoading,
      fetchForecastLoading,
      fetchEmployeeLoading,
      getAllUserListLoading,
      fetchHistoryApprovalLoading,
      task: {
        [taskId]: {
          pagination = {},
          detail = {},
          employeeList = [],
          forecast = [],
          changeEmployee,
          uselessParam,
          jumpList = [],
          orderFlowList = [],
        } = {},
      },
      task: { allUserList = [], userPagination = {} },
      task = {},
      location = {},
      tenantId,
      match,
      dispatch,
    } = this.props;
    const { processInstance = {}, nextNodeApprover = [], formKey = null, owner } = detail;
    const isAddSign =
      owner && (owner.startsWith('addSign') || owner.startsWith('ApproveAndAddSign'));
    const historyProps = {
      mergeHistoryFlag,
      detail,
      historyApprovalRecords,
      loading: fetchDetailLoading || fetchHistoryApprovalLoading,
      onFetchFileCount: this.handleFetchCount,
    };
    const flowProps = {
      dispatch,
      location,
      forecastData: forecast,
      tenantId,
      match: { params: { id: match.params.processInstanceId } },
      processInstance,
      uselessParam,
      loading: fetchForecastLoading,
    };
    const historyRecordProps = {
      loading: fetchHistoryApprovalLoading,
      records: historyApprovalRecords,
    };
    const formProps = {
      tenantId,
      detail: currentDetailData,
      ref: (ref) => {
        this.approveFormChildren = ref;
      },
      onAction: this.taskAction,
    };

    const employeeProps = {
      task,
      taskId,
      pagination:
        isAssignNextApprover && currentAddApprover.needAppoint === 'N' ? false : pagination,
      drawerVisible,
      tenantId,
      isCarbon,
      isAssignNextApprover,
      employeeList,
      appointerList,
      changeEmployee,
      needAppoint: currentAddApprover.needAppoint,
      dispatch,
      delegateOrAddUser,
      approveLoading,
      disabledUserList: this.selectedCopyUsersId,
      loading: fetchEmployeeLoading,
      anchor: 'right',
      onCancel: this.handleCancel,
      getActionName: this.getActionName,
      processCarbonCopy: this.processCarbonCopy,
      handleApproverOk: this.handleApproverOk,
      taskAction: this.taskAction,
      filterCandidates: this.filterCandidates,
    };

    const approverDrawerProps = {
      title: intl.get('hwfp.task.view.message.approverUsers').d('指定审批人'),
      visible: approverDrawerVisible,
      onCancel: this.handleApproverCancel,
      dataSource:
        currentAddApprover.needAppoint === 'N' ? currentAddApprover.candidates : allUserList,
      selectedApproveList: approveList,
      onSelect: this.handleApproverOk,
      onOk: this.handleApproverOk,
      userPagination: currentAddApprover.needAppoint === 'N' ? false : userPagination,
      onFormSearch: this.handleUserSearch,
      onPageChange: this.handlePageChange,
      loading: getAllUserListLoading,
      currentAddApprover,
    };

    const OrderFlowDrawerProps = {
      title: intl.get('hwfp.task.view.option.addOrderFlow').d('指定顺序流'),
      visible: orderFlowDrawerVisible,
      dataSource: sequenceList.length > 0 ? sequenceList : [],
      onCancel: this.handleOrderFlowCancel,
      onOk: this.handleOrderFlowOk,
    };

    const jumpModalProps = {
      jumpList,
      approveLoading,
      visible: jumpVisible,
      onOk: this.jumpActivity,
      onCancel: () => this.hideJump(false),
    };

    // const priority =
    //   detail.priority < 34
    //     ? intl.get('hzero.common.priority.low').d('低')
    //     : detail.priority > 66
    //     ? intl.get('hzero.common.priority.high').d('高')
    //     : intl.get('hzero.common.priority.medium').d('中');
    const { startUserId, startUserName } = processInstance;
    const name = startUserName ? `${startUserName}(${startUserId})` : '';
    return (
      <Fragment>
        <Header title={intl.get('hwfp.task.view.message.title.detail').d('待办明细')} />
        <Content>
          <Spin spinning={fetchDetailLoading || approveLoading}>
            {/* 审批事项 */}
            <div className={classNames(styles['label-col'])}>
              {intl.get('hwfp.common.model.approval.item').d('审批事项')}
            </div>
            <Row
              style={{ borderBottom: '1px dashed #dcdcdc', paddingBottom: 4, marginBottom: 20 }}
              type="flex"
              justify="space-between"
              align="bottom"
            >
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.process.name').d('流程名称')}:
                  </Col>
                  <Col md={16}> {processInstance.processDefinitionName}</Col>
                </Row>
              </Col>
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.process.ID').d('流程标识')}:
                  </Col>
                  <Col md={16}> {processInstance.id}</Col>
                  {/* <Col md={16}> {detail.processInstanceId}</Col>  猪齿鱼取得是id */}
                </Row>
              </Col>
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.apply.owner').d('申请人')}:
                  </Col>
                  <Col md={16}> {name}</Col>
                </Row>
              </Col>
            </Row>
            <Row
              style={{ borderBottom: '1px dashed #dcdcdc', paddingBottom: 4, marginBottom: 40 }}
              type="flex"
              justify="flex-start"
              align="bottom"
            >
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.apply.time').d('申请时间')}:
                  </Col>
                  <Col md={16}> {detail.createTime}</Col>
                </Row>
              </Col>
              <Col md={8}>
                <Row>
                  <Col md={6} style={{ color: '#999' }}>
                    {intl.get('hwfp.common.model.process.description').d('流程描述')}:
                  </Col>
                  <Col md={16}> {detail.description}</Col>
                </Row>
              </Col>
            </Row>
            {formKey && (
              <React.Fragment>
                <div className={classNames(styles['label-col'])}>
                  {intl.get('hwfp.common.model.approval.form').d('审批表单')}
                </div>
                <ApproveForm {...formProps} />
              </React.Fragment>
            )}
            <Tabs defaultActiveKey="record" animated={false} style={{ marginBottom: '20px' }}>
              <Tabs.TabPane
                tab={intl.get('hwfp.common.model.approval.record').d('审批记录')}
                key="record"
                forceRender
              >
                <ApproveHistory {...historyProps} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hwfp.common.model.process.graph').d('流程图')}
                key="graph"
                forceRender
              >
                <FlowChart {...flowProps} />
              </Tabs.TabPane>
              {!mergeHistoryFlag && historyApprovalRecords.length > 0 && (
                <Tabs.TabPane
                  tab={intl.get('hwfp.common.model.approval.history').d('审批历史')}
                  key="historyRecord"
                >
                  <ApproveHistoryRecord {...historyRecordProps} />
                </Tabs.TabPane>
              )}
            </Tabs>
            <Divider style={{ margin: '0 0 24px 0' }} />
            <Form className={styles['btn-form']}>
              <div>
                <Form.Item
                  label={
                    <Button
                      // icon="plus"
                      className="label-btn"
                      onClick={this.showCarbonCopyModal}
                    >
                      {intl.get('hwfp.task.view.option.addCc').d('添加抄送人')}
                    </Button>
                  }
                  style={{ display: 'flex' }}
                >
                  {getFieldDecorator('carbonCopyUsers', {
                    initialValue: carbonCopyUserList,
                  })(<div />)}
                  {carbonCopyUserList.map((item) => (
                    <Tag
                      closable
                      key={item.employeeId}
                      onClose={() => this.closeCarbonCopyUser(item)}
                    >
                      {`${item.name}(${item.employeeNum})`}
                    </Tag>
                  ))}
                </Form.Item>
              </div>
              {!isAddSign &&
                nextNodeApprover.map(
                  (n) =>
                    n.check !== 'N' && (
                      <React.Fragment>
                        <div>
                          <Form.Item
                            label={
                              <Button
                                // icon="plus"
                                className="label-btn"
                                onClick={() => this.showApproverModal(n)}
                              >
                                {intl
                                  .get('hwfp.task.view.option.addNextApprover', {
                                    nextActName: n.nextActName,
                                  })
                                  .d(`指派【${n.nextActName}】审批人`)}
                              </Button>
                            }
                            style={{ display: 'flex' }}
                          >
                            {getFieldDecorator(`nextActId-${n.nextActId}`, {
                              initialValue: [],
                            })(<div />)}
                            {(getFieldValue(`nextActId-${n.nextActId}`) || []).map((item) => (
                              <Tag
                                closable
                                visible
                                key={item.employeeId}
                                onClose={() => this.closeApprover(item, n)}
                              >
                                {`${item.name}(${item.employeeCode})`}
                              </Tag>
                            ))}
                          </Form.Item>
                        </div>
                      </React.Fragment>
                    )
                )}
              {orderFlowList.length > 0 && (
                <React.Fragment>
                  <div>
                    <span
                      style={{
                        marginRight: '24px',
                        lineHeight: '40px',
                        color: 'rgb(0,0,0,0.85)',
                        fontWeight: 'bold',
                      }}
                    >
                      {intl.get('hwfp.task.view.option.addOrderFlow').d('指定顺序流')}:
                    </span>
                    <Button
                      icon="plus"
                      className="label-btn"
                      onClick={() => this.showOrderFlowModal()}
                    >
                      {orderFlowList[0].sourceName}
                    </Button>
                    <Form.Item />
                  </div>
                  <Divider style={{ margin: '0 0 24px 0' }} />
                </React.Fragment>
              )}
              <Form.Item
                label={
                  <>
                    {intl
                      .get('hwfp.common.model.approval.opinion', { title: approveCommentTitle })
                      .d(approveCommentTitle)}
                    <span style={{ color: '#aaa', marginLeft: '16px' }}>{commentNotice}</span>
                  </>
                }
                style={{ marginBottom: 0 }}
              >
                {getFieldDecorator('comment', {
                  initialValue: comment,
                  rules: [
                    {
                      required: true,
                      message: intl
                        .get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hwfp.common.model.approval.opinion', {
                              title: approveCommentTitle,
                            })
                            .d('approveCommentTitle'),
                        })
                        .d(
                          `${intl
                            .get('hwfp.common.model.approval.opinion', {
                              title: approveCommentTitle,
                            })
                            .d(`${approveCommentTitle}不能为空`)}`
                        ),
                    },
                  ],
                })(
                  <TextArea
                    className={styles['explain-content']}
                    maxLength={600}
                    style={{
                      resize: 'none',
                      height: '120px',
                      marginBottom: 8,
                    }}
                    onBlur={this.handleSaveComment}
                  />
                )}
              </Form.Item>
              <div style={{ marginBottom: '20px' }}>
                <span>
                  <UploadModal
                    action={
                      isTenantRoleLevel()
                        ? `${HZERO_FILE}/v1/${tenantId}/files/attachment/multipart`
                        : `${HZERO_FILE}/v1/files/attachment/multipart`
                    }
                    attachmentUUID={attachmentUuid}
                    bucketName={BKT_HWFP}
                    bucketDirectory="hwfp01"
                  />
                </span>
              </div>
              {this.renderApproveBtn()}
            </Form>
            <EmployeeDrawer {...employeeProps} />
            {currentAddApprover.check !== 'N' && <ApproverDrawer {...approverDrawerProps} />}
            <JumpModal {...jumpModalProps} />
            <OrderFlowDrawer {...OrderFlowDrawerProps} />
          </Spin>
        </Content>
      </Fragment>
    );
  }
}
