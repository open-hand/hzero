import React, { PureComponent } from 'react';
import { Button, Spin, Drawer } from 'hzero-ui';
// import { routerRedux } from 'dva/router';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import intl from 'utils/intl';
// import { DOCS_URI } from '@/constants/constants';
// import notification from 'utils/notification';
import { DETAIL_DEFAULT_CLASSNAME } from 'utils/constants';
import Form from './Form';

/**
 * 服务注册
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} services - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
export default class Services extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      activeinvokeStatisticsFlag: 0,
      dataSource: {},
      recipientKey: [{ _status: 'create' }], // 接收人
      enableService: [],
    };
  }

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, interfaceListActionRow = {} } = this.props;
    const { interfaceId } = interfaceListActionRow;

    return (
      visible &&
      !isUndefined(interfaceId) &&
      interfaceId !== (prevProps.interfaceListActionRow || {}).interfaceId
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchDetail();
    }
  }

  /**
   * fetchList - 获取列表数据
   * @param {Object} payload - 查询参数
   */
  @Bind()
  handleFetchDetail() {
    const {
      fetchMonitor = () => {},
      interfaceListActionRow = {},
      getTemplateServerDetail = () => {},
    } = this.props;
    fetchMonitor(interfaceListActionRow.interfaceId).then((res = {}) => {
      // this.setState({
      //   dataSource: res || {},
      //   recipientKey: res.interfaceMonitorTgtList.length > 0 ? res.interfaceMonitorTgtList : [{ _status: 'create'}],
      //   activeinvokeStatisticsFlag: res.invokeStatisticsFlag,
      //   activeHealthCheckFlag: res.healthCheckFlag,
      // });
      if (res && res.checkWarningMsgTplCode) {
        // 获取类型下拉框数据
        getTemplateServerDetail({
          tenantId: res.tenantId,
          messageCode: res.checkWarningMsgTplCode,
        }).then((res2) => {
          if (res2) {
            this.setState({
              dataSource: res || {},
              recipientKey:
                res.interfaceMonitorTgtList && res.interfaceMonitorTgtList.length > 0
                  ? res.interfaceMonitorTgtList
                  : [{ _status: 'create' }],
              activeinvokeStatisticsFlag: res.invokeStatisticsFlag,
              activeHealthCheckFlag: res.healthCheckFlag,
              enableService: !res2.failed ? res2 : [],
            });
          }
        });
      } else {
        this.setState({
          dataSource: res || {},
          recipientKey:
            res.interfaceMonitorTgtList && res.interfaceMonitorTgtList.length > 0
              ? res.interfaceMonitorTgtList
              : [{ _status: 'create' }],
          activeinvokeStatisticsFlag: res.invokeStatisticsFlag,
          activeHealthCheckFlag: res.healthCheckFlag,
          enableService: [],
        });
      }
    });
    // const filterValues = isUndefined(this.filterForm)
    //   ? {}
    //   : filterNullValueObject(this.filterForm.getFieldsValue());
    // dispatch({
    //   type: 'services/queryList',
    //   payload: {
    //     page: params,
    //     ...filterValues,
    //   },
    // });
  }

  /**
   * 添加接收者参数
   */
  @Bind()
  addRecipient() {
    const { recipientKey } = this.state;
    this.setState({
      recipientKey: recipientKey.concat([
        {
          _status: 'create',
        },
      ]),
    });
  }

  /**
   * 移除接收者键，值
   * @param {*}
   */
  @Bind()
  removeRecipient(k) {
    const { recipientKey } = this.state;
    if (recipientKey.length === 1) {
      return;
    }
    this.setState({
      recipientKey: recipientKey.filter((_, index) => index !== k),
    });
  }

  @Bind()
  save() {
    const {
      createMonitor = () => {},
      updateMonitor = () => {},
      interfaceListActionRow = {},
      serverCode,
    } = this.props;
    const { validateFields = () => {} } = this.editorForm;
    const { dataSource = {}, recipientKey } = this.state;
    const { interfaceCode } = interfaceListActionRow;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        const interfaceMonitorTgtList = [];
        if (values.healthCheckFlag) {
          recipientKey.forEach((item, k) => {
            interfaceMonitorTgtList[k] = {
              interfaceMonitorId: dataSource.interfaceMonitorId,
              typeCode: values[`recipientKey${k}`] || item.typeCode,
              userId: values[`recipientValue${k}`] || item.userId,
              agentId: values[`agentId${k}`] || item.agentId,
              _status: item.targetId ? 'update' : item._status,
              targetId: item.targetId,
              _token: item._token,
            };
          });
        }
        const data = {
          ...dataSource,
          ...values,
          serverCode,
          interfaceCode,
          interfaceMonitorTgtList,
        };
        if (!isUndefined(data.interfaceMonitorId)) {
          updateMonitor(interfaceListActionRow.interfaceId, data.interfaceMonitorId, data).then(
            (res) => {
              if (res && res.failed) {
                notification.error({ description: res.message });
              } else {
                notification.success();
                this.handleFetchDetail();
              }
            }
          );
        } else {
          createMonitor(interfaceListActionRow.interfaceId, data).then((res) => {
            if (res && res.failed) {
              notification.error({ description: res.message });
            } else {
              notification.success();
              this.handleFetchDetail();
            }
          });
        }
      }
    });
  }

  // updateMonitor,

  @Bind()
  cancel() {
    const { onCancel = () => {} } = this.props;
    const { resetFields = (e) => e } = this.editorForm;
    resetFields();
    // this.setState({
    //   // formDataSource: {},
    // });
    onCancel();
  }

  @Bind()
  onInvokeStatisticsFlagChange(activeinvokeStatisticsFlag) {
    this.setState({
      activeinvokeStatisticsFlag,
    });
  }

  @Bind()
  onHealthCheckFlagChange(activeHealthCheckFlag) {
    this.setState({
      activeHealthCheckFlag,
    });
  }

  /**
   * @function handleCheckWarningMsgTplCodeChange - 切换预警消息模板代码
   */
  @Bind()
  handleCheckWarningMsgTplCodeChange(_, params) {
    if (params && params.messageCode) {
      const { interfaceListActionRow = {}, getTemplateServerDetail = () => {} } = this.props;
      // 获取类型下拉框数据
      getTemplateServerDetail({
        tenantId: interfaceListActionRow.tenantId,
        messageCode: params.messageCode,
      }).then((res) => {
        if (res) {
          this.setState({
            enableService: !res.failed ? res : [],
          });
        }
      });
    }
  }

  render() {
    const {
      visible,
      logTypes,
      code = {},
      interfaceListActionRow = {},
      processing = {},
    } = this.props;
    const {
      dataSource = {},
      activeinvokeStatisticsFlag,
      activeHealthCheckFlag,
      enableService,
      recipientKey,
    } = this.state;
    const { interfaceId, tenantId } = interfaceListActionRow;
    const drawerProps = {
      title: intl.get('hitf.services.view.button.operationalConfig').d('运维配置'),
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.cancel,
      width: activeinvokeStatisticsFlag === 1 || activeHealthCheckFlag === 1 ? 750 : 550,
    };

    const formProps = {
      ref: (node) => {
        this.editorForm = node;
      },
      logTypes,
      tenantId,
      dataSource,
      enableService,
      recipientKey,
      code,
      interfaceId,
      onInvokeStatisticsFlagChange: this.onInvokeStatisticsFlagChange,
      activeinvokeStatisticsFlag,
      activeHealthCheckFlag,
      onHealthCheckFlagChange: this.onHealthCheckFlagChange,
      handleCheckWarningMsgTplCodeChange: this.handleCheckWarningMsgTplCodeChange,
      addRecipient: this.addRecipient,
      removeRecipient: this.removeRecipient,
    };

    return (
      <Drawer {...drawerProps}>
        <Spin
          spinning={processing.query || processing.update || processing.create || false}
          wrapperClassName={DETAIL_DEFAULT_CLASSNAME}
        >
          <Form {...formProps} />
        </Spin>
        <div
          style={{
            position: 'absolute',
            bottom: 0,
            width: '100%',
            borderTop: '1px solid #e8e8e8',
            padding: '10px 16px',
            textAlign: 'right',
            left: 0,
            background: '#fff',
            borderRadius: '0 0 4px 4px',
            zIndex: 1,
          }}
        >
          <Button onClick={this.cancel} style={{ marginRight: 8 }}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>

          <Button
            type="primary"
            onClick={this.save}
            disabled={processing.query}
            loading={processing.update || processing.create || false}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
