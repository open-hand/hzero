/**
 * KnowledgeVindicate 知识维护
 * @date: 2019-12-11
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React from 'react';
import { Table, Modal, Select, TextArea, ModalContainer, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { Tag } from 'choerodon-ui';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';

import { detailTableDS } from '../../stores/knowledgeVindicateDS';
import { checkKnowledgeQuestion } from '../../services/knowledgeVindicateServices';
import Drawer from './Drawer';

const { Option } = Select;
@formatterCollections({ code: ['hims.knowledge'] })
export default class KnowledgeManage extends React.Component {
  detailTableDs = new DataSet(detailTableDS());

  constructor(props) {
    super(props);
    this.state = {
      // categoryId: '', // 当前知识类别id
      languageType: [], // 语言类别
    };
  }

  staticTextEditorRef;

  componentDidMount() {
    this.detailTableDs.setQueryParameter('lang', 'zh_CN');
    queryUnifyIdpValue('HPFM.LANGUAGE').then((res) => {
      this.setState({
        languageType: res,
      });
    });
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

  get columns() {
    return [
      {
        name: 'categoryName',
        width: 150,
        lock: 'left',
      },
      {
        name: 'questionTitle',
        width: 320,
      },
      {
        name: 'keyWord',
        width: 280,
      },
      {
        name: 'checkStatus',
        renderer: ({ value, record }) => {
          const {
            data: { checkStatusName },
          } = record;
          return this.checkStatusRender(value, checkStatusName);
        },
      },
      {
        name: 'enabledFlag',
        width: 100,
        renderer: ({ value }) => enableRender(value),
        align: 'left',
      },
      {
        name: 'submitterName',
      },
      {
        name: 'submitDate',
        width: 170,
      },
      {
        name: 'checkerName',
      },
      {
        name: 'checkDate',
        width: 170,
      },
      {
        name: 'rejectReason',
        width: 200,
      },
      {
        name: 'action',
        width: 220,
        lock: 'right',
        renderer: ({ record }) => {
          const { path } = this.props;
          const { data = {} } = record;

          const operators = [];
          operators.push({
            key: 'view',
            ele: (
              <a onClick={() => this.openModal('view', record)}>
                {intl.get('hzero.common.button.view').d('查看')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.view').d('查看'),
          });
          if (data.checkStatus === 'checked') {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.manage-edit`,
                      type: 'button',
                      meaning: '知识类别-编辑问题',
                    },
                  ]}
                  onClick={() => this.openModal('edit', record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          if (data.checkStatus === 'wait_check') {
            operators.push(
              {
                key: 'approve',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.approve`,
                        type: 'button',
                        meaning: '知识类别-审核通过',
                      },
                    ]}
                    onClick={() => this.handleCheck(data, 'checked')}
                  >
                    {intl.get('hims.knowledge.view.button.approve').d('审核通过')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hims.knowledge.view.button.approve').d('审核通过'),
              },
              {
                key: 'reject',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.reject`,
                        type: 'button',
                        meaning: '知识类别-驳回',
                      },
                    ]}
                    onClick={() => this.handleCheck(data, 'rejected')}
                  >
                    {intl.get('hims.knowledge.view.button.reject').d('驳回')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hims.knowledge.view.button.reject').d('驳回'),
              }
            );
          }
          return operatorRender(operators, {}, { limit: 4 });
        },
      },
    ];
  }

  /**
   * 打开模态框
   * @param {string} status
   * @param {object} record
   * @memberof KnowledgeManage
   */
  @Bind()
  openModal(status, record) {
    let viewStatus = false;
    if (status === 'create') {
      this.detailTableDs.create();
    } else if (status === 'view') {
      viewStatus = true;
    }
    Modal.open({
      closable: true,
      key: 'knowledge-manage',
      title:
        // eslint-disable-next-line no-nested-ternary
        status === 'create'
          ? intl.get('hims.knowledge.view.message.crete').d('新建问题')
          : status === 'edit'
          ? intl.get('hims.knowledge.view.message.edit').d('编辑问题')
          : intl.get('hims.knowledge.view.message.view').d('查看问题'),
      drawer: true,
      style: {
        width: 720,
      },
      children: (
        <Drawer
          editData={record || this.detailTableDs.current}
          viewStatus={viewStatus}
          editStatus={status === 'edit'}
          onHandleStaticTextEditorRef={this.handleStaticTextEditorRef}
        />
      ),
      onOk: () => this.handleSave(viewStatus),
      onCancel: () => {
        this.detailTableDs.reset();
      },
      onClose: () => {
        this.detailTableDs.reset();
      },
      okCancel: !viewStatus,
      okText: viewStatus
        ? intl.get('hzero.common.button.close').d('关闭')
        : intl.get('hzero.common.button.ok').d('确定'),
    });
  }

  /**
   * 新建/编辑知识问题
   * @param {Boolean} viewStatus
   */
  @Bind()
  async handleSave(viewStatus) {
    if (!viewStatus) {
      if (this.staticTextEditorRef) {
        const { editor } = (this.staticTextEditorRef.staticTextEditor || {}).current;
        if (!editor || !editor.getData()) {
          notification.warning({
            message: intl
              .get('hims.knowledge.view.message.alert.contentRequired')
              .d('请输入答案内容'),
          });
          return false;
        } else {
          this.detailTableDs.current.set('answerDesc', editor.getData());
        }
      }
      const validate = await this.detailTableDs.submit();
      if (!validate) {
        return false;
      }
      this.detailTableDs.query();
    }
  }

  @Bind()
  async handleCheck(data, status) {
    const { checkerName, submitterName, category, ...other } = data;
    const params = {
      ...other,
      checkStatus: status,
    };
    let rejectReason = '';
    if (status === 'rejected') {
      Modal.open({
        closable: true,
        key: 'rejected-reason',
        title: intl.get('hims.knowledge.view.message.rejectedReason').d('驳回原因'),
        children: (
          <TextArea
            required
            rows={12}
            cols={400}
            name="rejectReason"
            label={intl.get('hims.knowledge.model.knowledge.rejectReason').d('驳回原因')}
            onChange={(value) => {
              rejectReason = value;
            }}
          />
        ),
        onOk: () => {
          if (rejectReason) {
            const newParams = {
              ...params,
              rejectReason,
            };
            this.sendCheckRequest(newParams);
          } else {
            return false;
          }
        },
      });
    } else {
      this.sendCheckRequest(params);
    }
  }

  @Bind()
  async sendCheckRequest(params) {
    await checkKnowledgeQuestion(params);
    this.detailTableDs.query();
  }

  @Bind()
  handleStaticTextEditorRef(staticTextEditorRef) {
    this.staticTextEditorRef = staticTextEditorRef;
  }

  /**
   * 选择器值改变时触发
   * @param {string} value
   */
  @Bind()
  onSelectChange(value) {
    this.detailTableDs.setQueryParameter('lang', value);
  }

  render() {
    const { location, match } = this.props;
    const { languageType } = this.state;
    return (
      <>
        <Header title={intl.get('hims.knowledge.view.message.knowledgeManage').d('知识维护')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${match.path}.button.manage-create`,
                type: 'button',
                meaning: '知识类别-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.openModal('create')}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <div>
            <span>{intl.get('hims.knowledge.model.knowledge.language').d('语言')}：</span>
            <Select
              style={{ width: '150px' }}
              clearButton={false}
              defaultValue="zh_CN"
              onChange={this.onSelectChange}
            >
              {languageType.map((item) => (
                <Option value={item.value} key={item.value}>
                  {item.meaning}
                </Option>
              ))}
            </Select>
          </div>
        </Header>
        <Content>
          <Table dataSet={this.detailTableDs} columns={this.columns} queryFieldsLimit={3} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
