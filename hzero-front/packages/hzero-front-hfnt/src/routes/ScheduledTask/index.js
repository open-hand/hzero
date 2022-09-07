/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/4/27
 * @copyright HAND ® 2020
 */
import React from 'react';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal, NumberField } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { operatorRender, TagRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import SCHEDULED_TASK_LANG from '@/langs/scheduledTaskLang';
import { tableDS, drawerFormDS, modalTableDS } from '@/stores/ScheduledTask/ScheduledTaskDS';
import { FRONTAL_JOB_JOB_STATUS, FRONTAL_JOB_TAG_JOB_STATUS } from '@/constants/constants';
import EditDrawer from './EditDrawer';

@formatterCollections({ code: ['hfnt.scheduledTask'] })
export default class ScheduledTask extends React.Component {
  modal;

  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS);
    this.drawerFormDS = new DataSet(drawerFormDS);
    this.modalTableDS = new DataSet(modalTableDS);
  }

  /**
   * 查询
   */
  async handleFetchDetail() {
    await this.tableDS.query();
  }

  /**
   * 新建
   */
  @Bind()
  handleCreate() {
    this.drawerFormDS.create({
      statusCode: 'NEW',
      jobType: 'CRON',
    });
    this.openConsumerDrawer(true);
  }

  /**
   * 打开定时任务滑窗
   */
  openConsumerDrawer(isNew, record) {
    const {
      match: { path },
    } = this.props;
    const scheduledTaskProps = {
      isNew,
      record,
      drawerFormDS: this.drawerFormDS,
    };
    this.modal = Modal.open({
      title: isNew ? SCHEDULED_TASK_LANG.CREATE_LINE : SCHEDULED_TASK_LANG.EDIT_LINE,
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 750 },
      children: <EditDrawer {...scheduledTaskProps} />,
      okText: SCHEDULED_TASK_LANG.SAVE,
      afterClose: () => this.drawerFormDS.reset(),
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '定时任务配置-保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            onClick={this.handleConsumerOk}
          >
            {SCHEDULED_TASK_LANG.SAVE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 定时任务保存
   */
  @Bind()
  async handleConsumerOk() {
    if (this.drawerFormDS.current.status === 'sync') {
      notification.warning({
        message: SCHEDULED_TASK_LANG.SAVE_EMPTY,
      });
      return Promise.reject();
    }
    const validate = await this.drawerFormDS.validate();
    if (!validate) {
      notification.error({
        message: SCHEDULED_TASK_LANG.SAVE_VALIDATE,
      });
      return Promise.reject();
    }
    const res = await this.drawerFormDS.submit();
    if (res) {
      this.handleFetchDetail();
      this.modal.close();
    } else {
      return Promise.reject();
    }
    return Promise.resolve();
  }

  /**
   * 发布/失效
   */
  @Bind()
  async handleToggle(record, requestType) {
    record.set('_requestType', requestType);
    await this.tableDS.submit();
    this.handleFetchDetail();
  }

  /**
   * 参数弹窗按钮
   */
  get paramButtons() {
    const { match: path } = this.props;
    const createBtn = (
      <ButtonPermission
        permissionList={[
          {
            code: `${path}.button.createParam`,
            type: 'button',
            meaning: '参数维护-新建',
          },
        ]}
        type="c7n-pro"
        color="primary"
        icon="add"
        onClick={this.handleCreateParam}
      >
        {SCHEDULED_TASK_LANG.CREATE}
      </ButtonPermission>
    );
    return [createBtn, 'delete'];
  }

  /**
   * 打开参数维护弹窗
   */
  @Bind()
  openParamModal(record) {
    const {
      match: { path },
    } = this.props;
    const viewOnly = !FRONTAL_JOB_JOB_STATUS.EDIT.includes(record.get('statusCode'));
    this.modalTableDS.setQueryParameter('jobId', record.get('jobId'));
    this.modalTableDS.query();
    this.paramModal = Modal.open({
      title: SCHEDULED_TASK_LANG.PARAM_TITLE,
      closable: true,
      key: Modal.key(),
      style: { width: 650 },
      children: (
        <Table
          buttons={viewOnly ? [] : this.paramButtons}
          dataSet={this.modalTableDS}
          columns={this.paramColumns}
        />
      ),
      okText: SCHEDULED_TASK_LANG.SAVE,
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          {!viewOnly && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.saveParam`,
                  type: 'button',
                  meaning: '参数维护-保存',
                },
              ]}
              type="c7n-pro"
              color="primary"
              onClick={this.handleSaveParam}
            >
              {SCHEDULED_TASK_LANG.SAVE}
            </ButtonPermission>
          )}
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 新建参数
   */
  @Bind()
  handleCreateParam() {
    const paramRecord = this.modalTableDS.create();
    paramRecord.set('seqNum', paramRecord.index + 1);
    paramRecord.set('jobId', this.tableDS.current.get('jobId'));
  }

  /**
   * 参数保存
   */
  @Bind()
  async handleSaveParam() {
    const validate = await this.modalTableDS.validate();
    if (!validate) {
      notification.error({
        message: SCHEDULED_TASK_LANG.SAVE_VALIDATE,
      });
      return Promise.reject();
    }
    const res = await this.modalTableDS.submit();
    if (res && res.success) {
      this.paramModal.close();
    }
  }

  get paramColumns() {
    return [
      {
        name: 'seqNum',
        width: 80,
        align: 'center',
        editor: <NumberField />,
      },
      {
        name: 'paramType',
        width: 150,
        editor: true,
      },
      {
        name: 'paramName',
        width: 150,
        editor: true,
      },
      {
        name: 'paramValue',
        width: 150,
        editor: true,
      },
    ];
  }

  get scheduledTaskColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'frontalCode',
        width: 200,
      },
      {
        name: 'frontalName',
        width: 180,
      },
      {
        name: 'jobCode',
        width: 150,
      },
      {
        name: 'jobName',
        width: 180,
      },
      {
        name: 'jobClassName',
        width: 180,
      },
      {
        name: 'jobType',
        width: 120,
      },
      {
        name: 'jobCron',
        width: 150,
      },
      // {
      //   header: SCHEDULED_TASK_LANG.CRON_MEANING,
      //   width: 280,
      //   renderer: ({ record }) => parserCron(record.get('jobCron')),
      // },
      {
        name: 'jobClass',
        width: 180,
      },
      {
        name: 'jobClassMethod',
        width: 150,
      },
      {
        name: 'statusCode',
        width: 100,
        align: 'center',
        renderer: ({ value }) => TagRender(value, FRONTAL_JOB_TAG_JOB_STATUS),
      },
      {
        header: SCHEDULED_TASK_LANG.OPERATOR,
        width: 200,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            FRONTAL_JOB_JOB_STATUS.EDIT.includes(record.get('statusCode')) && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '定时任务列表-编辑',
                    },
                  ]}
                  onClick={() => this.openConsumerDrawer(false, record)}
                >
                  {SCHEDULED_TASK_LANG.EDIT}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: SCHEDULED_TASK_LANG.EDIT,
            },
            FRONTAL_JOB_JOB_STATUS.PUBLISH.includes(record.get('statusCode')) && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enabled`,
                      type: 'button',
                      meaning: '定时任务列表-发布',
                    },
                  ]}
                  onClick={() => this.handleToggle(record, 'publish')}
                >
                  {SCHEDULED_TASK_LANG.PUBLISH}
                </ButtonPermission>
              ),
              key: 'enabled',
              len: 2,
              title: SCHEDULED_TASK_LANG.PUBLISH,
            },
            FRONTAL_JOB_JOB_STATUS.DISABLE.includes(record.get('statusCode')) && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.disabled`,
                      type: 'button',
                      meaning: '定时任务列表-失效',
                    },
                  ]}
                  onClick={() => this.handleToggle(record, 'disable')}
                >
                  {SCHEDULED_TASK_LANG.DISABLED}
                </ButtonPermission>
              ),
              key: 'disabled',
              len: 2,
              title: SCHEDULED_TASK_LANG.DISABLED,
            },
            FRONTAL_JOB_JOB_STATUS.ENABLE.includes(record.get('statusCode')) && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enabled`,
                      type: 'button',
                      meaning: '定时任务列表-启用',
                    },
                  ]}
                  onClick={() => this.handleToggle(record, 'enable')}
                >
                  {SCHEDULED_TASK_LANG.ENABLED}
                </ButtonPermission>
              ),
              key: 'enabled',
              len: 2,
              title: SCHEDULED_TASK_LANG.ENABLED,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.param`,
                      type: 'button',
                      meaning: '定时任务列表-参数',
                    },
                  ]}
                  onClick={() => this.openParamModal(record)}
                >
                  {FRONTAL_JOB_JOB_STATUS.EDIT.includes(record.get('statusCode'))
                    ? SCHEDULED_TASK_LANG.PARAM_MAINTAIN
                    : SCHEDULED_TASK_LANG.PARAM_VIEW}
                </ButtonPermission>
              ),
              key: 'param',
              len: 4,
              title: FRONTAL_JOB_JOB_STATUS.EDIT.includes(record.get('statusCode'))
                ? SCHEDULED_TASK_LANG.PARAM_MAINTAIN
                : SCHEDULED_TASK_LANG.PARAM_VIEW,
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={SCHEDULED_TASK_LANG.HEADER}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '定时任务维护-新建',
              },
            ]}
            icon="add"
            type="c7n-pro"
            color="primary"
            onClick={this.handleCreate}
          >
            {SCHEDULED_TASK_LANG.CREATE}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDS} columns={this.scheduledTaskColumns} />
        </Content>
      </>
    );
  }
}
