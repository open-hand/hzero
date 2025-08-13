/**
 * EditModal
 * @description
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/11
 */

import React from 'react';
import { Affix, Col, Form, Input, Modal, Popconfirm, Row, Select, Button } from 'hzero-ui';
import { cloneDeep, filter, forEach } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import { Content } from 'components/Page';
import ValueList from 'components/ValueList';
import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import { operatorRender } from 'utils/renderer';

import styles from '../index.less';

const { Item: FormItem } = Form;

@Form.create({ fieldNameProp: null })
export default class EditModal extends React.Component {
  containerRef = React.createRef();

  state = {
    dataSource: [],
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    if (nextProps.profileValue !== prevState.profileValue) {
      const { profileValue = {} } = nextProps;
      return {
        profileValue: nextProps.profileValue,
        dataSource: cloneDeep(profileValue.profileValueDTOList || []).map((record) => ({
          ...record,
          _status: 'update',
        })),
      };
    }
    return null;
  }

  getEditDisabled(beforeDisabled) {
    const { editModalEditable = false } = this.props;
    return beforeDisabled || !editModalEditable;
  }

  /**
   * getParent-获取 dom 的parent
   * @param {HTMLElement} dom
   * @return {HTMLElement}
   */
  @Bind()
  getParent(dom) {
    const parent = dom && dom.parentNode;
    return parent && parent.nodeType !== 11 ? parent : null;
  }

  /**
   * getEditModalContentContainer-获取给 Affix 组件使用的元素
   * @return {HTMLElement}
   */
  @Bind()
  getEditModalContentContainer() {
    const parent = this.getParent(this.containerRef.current);
    return parent || document.body;
  }

  @Bind()
  getColumns() {
    const { match } = this.props;
    this.columns = [
      {
        title: intl.get('hpfm.profile.model.profileValue.levelCode').d('层级'),
        dataIndex: 'levelCode',
        width: 100,
        render: (item, record) => {
          // 当时平台级时,levelCode,levelValue 固定是 GLOBAL,且不能修改
          const { levelCode = [] } = this.props;
          const { $form } = record;
          return (
            <FormItem>
              {$form.getFieldDecorator('levelCode', {
                initialValue: item,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.profile.model.profileValue.levelCode').d('层级'),
                    }),
                  },
                ],
              })(
                <ValueList
                  options={levelCode}
                  onChange={(value) => this.handleRecordLevelCodeChange(value, record)}
                  className={styles['full-width']}
                  disabled={this.getEditDisabled(false)}
                />
              )}
            </FormItem>
          );
        },
      },
      {
        title: intl.get('hpfm.profile.model.profileValue.levelValue').d('层级值'),
        dataIndex: 'levelValue',
        width: 200,
        render: (item, record) => {
          const { $form } = record;
          const tenantId = this.getCurrentTenantId();
          const currentLevelCode = $form.getFieldValue('levelCode');
          // 当时平台级时,levelCode,levelValue 固定是 GLOBAL,且不能修改
          let $levelValueInputComponent;
          switch (currentLevelCode) {
            case 'USER':
              $levelValueInputComponent = (
                <Lov
                  textValue={record.levelValueDescription}
                  code="HIAM.TENANT.USER"
                  onChange={() => this.handleRecordChange(record)}
                  queryParams={{ organizationId: tenantId }}
                  className={styles['full-width']}
                  disabled={this.getEditDisabled(tenantId === undefined)}
                />
              );
              break;
            case 'ROLE':
              $levelValueInputComponent = (
                <Lov
                  textValue={record.levelValueDescription}
                  code="HIAM.TENANT.ROLE"
                  onChange={() => this.handleRecordChange(record)}
                  queryParams={{ organizationId: tenantId }}
                  className={styles['full-width']}
                  disabled={this.getEditDisabled(tenantId === undefined)}
                />
              );
              break;
            case 'GLOBAL':
              // 如果层级是 GLOBAL 那么层级值 只能是 GLOBAL
              $levelValueInputComponent = (
                <Select className={styles['full-width']} disabled>
                  <Select.Option value="GLOBAL" key="GLOBAL">
                    {intl.get('hpfm.profile.model.profileValue.levelValue.GLOBAL').d('全局')}
                  </Select.Option>
                </Select>
              );
              break;
            default:
              // 没有选择 层级 是不能选择层级值的.
              $levelValueInputComponent = <Input disabled />;
              break;
          }
          return (
            <FormItem>
              {$form.getFieldDecorator('levelValue', {
                initialValue: item,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.profile.model.profileValue.levelValue').d('层级值'),
                    }),
                  },
                ],
              })($levelValueInputComponent)}
            </FormItem>
          );
        },
      },
      {
        title: intl.get('hpfm.profile.model.profileValue.profileValue').d('配置值'),
        width: 200,
        dataIndex: 'value',
        render: (item, record) => {
          const { $form } = record;
          return (
            <FormItem>
              {$form.getFieldDecorator('value', {
                initialValue: item,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.profile.model.profileValue.profileValue').d('配置值'),
                    }),
                  },
                  {
                    max: 480,
                    message: intl.get('hzero.common.validation.max', {
                      max: 480,
                    }),
                  },
                ],
              })(
                <Input
                  onChange={() => this.handleRecordChange(record)}
                  disabled={this.getEditDisabled(false)}
                />
              )}
            </FormItem>
          );
        },
      },
      !this.getEditDisabled(false) && {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        render: (_, record) => {
          const actions = [];
          actions.push({
            key: 'remove',
            ele: (
              <Popconfirm
                placement="topRight"
                title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                onConfirm={() => {
                  this.handleRecordRemove(record);
                }}
              >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.valueDelete`,
                      type: 'button',
                      meaning: '配置维护(租户)配置值-删除',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              </Popconfirm>
            ),
          });
          return operatorRender(actions);
        },
      },
    ].filter(Boolean);
    return this.columns;
  }

  @Bind()
  renderProfileForm() {
    const { profileValue = {}, form, isCreate, match, editModalEditable = false } = this.props;
    return (
      <React.Fragment>
        <Row style={{ backgroundColor: '#fff' }}>
          <Col md={12} sm={24}>
            <FormItem
              required
              labelCol={{ span: 6 }}
              wrapperCol={{ span: 18 }}
              label={intl.get('hpfm.profile.model.profile.name').d('配置编码')}
            >
              {form.getFieldDecorator('profileName', {
                initialValue: profileValue.profileName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.profile.model.profile.name').d('配置编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={this.getEditDisabled(!isCreate)}
                />
              )}
            </FormItem>
          </Col>
          <Col md={12} sm={24}>
            <FormItem
              labelCol={{ span: 6 }}
              wrapperCol={{ span: 18 }}
              label={intl.get('hpfm.profile.model.profile.description').d('配置描述')}
            >
              {form.getFieldDecorator('description', {
                initialValue: profileValue.description,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.profile.model.profile.description').d('配置描述'),
                    }),
                  },
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', {
                      max: 240,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('hpfm.profile.model.profile.description').d('配置描述')}
                  field="description"
                  token={profileValue ? profileValue._token : null}
                  disabled={this.getEditDisabled(false)}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        {editModalEditable && (
          <Row style={{ backgroundColor: '#fff' }}>
            <Col>
              <FormItem
                label={intl.get('hpfm.profile.model.profile.profileValue').d('配置值')}
                labelCol={{ span: 3 }}
                wrapperCol={{ span: 21 }}
              >
                <ButtonPermission
                  htmlType="button"
                  permissionList={[
                    {
                      code: `${match.path}.button.valueAdd`,
                      type: 'button',
                      meaning: '配置维护(租户)配置值-新增配置值',
                    },
                  ]}
                  onClick={this.handleRecordAddBtnClick}
                >
                  {intl.get('hpfm.profile.view.form.create').d('新增配置值')}
                </ButtonPermission>
              </FormItem>
            </Col>
          </Row>
        )}
      </React.Fragment>
    );
  }

  @Bind()
  handleCloseModal() {
    const { form, onCancel } = this.props;
    form.resetFields();
    onCancel();
  }

  @Bind()
  getCurrentTenantId() {
    const { tenantId } = this.props;
    return tenantId;
  }

  @Bind()
  handleProfileLevelChange(profileLevel) {
    const { isCreate } = this.props;
    if (isCreate) {
      if (profileLevel === 'T') {
        // 从平台变为租户
      } else if (profileLevel === 'P') {
        // 从租户变为平台
        const { dataSource } = this.state;
        const { profileValue = {}, form } = this.props;
        const profileValues = [];
        forEach(dataSource, (record) => {
          if (record.value) {
            profileValues.push(record.value);
          }
        });
        // 变为 平台级
        this.setState({
          dataSource: [
            {
              levelCode: 'GLOBAL',
              levelValue: 'GLOBAL',
              value: profileValues.join(' '),
              isCreate: true,
              updateStatus: 'create',
              profileValueId: uuid(),
              profileId: profileValue.profileId,
            },
          ],
        });
        // 变为平台级后 没有租户
        form.setFieldsValue({ tenantId: undefined });
      }
    }
  }

  /**
   * handleRecordAddBtnClick-配置值新增按钮点击
   */
  @Bind()
  handleRecordAddBtnClick() {
    const { isCreate, profileValue } = this.props;
    const { dataSource } = this.state;
    const nRecord = {
      _status: 'create',
      updateStatus: 'create',
      profileValueId: uuid(),
    };
    if (!isCreate) {
      nRecord.profileId = profileValue.profileId;
    }
    this.setState({
      dataSource: [...dataSource, nRecord],
    });
  }

  // columns's method

  /**
   * handleRecordLevelCodeChange - 配置值的层级更改
   * @param {*} value 修改的值
   * @param {Object} updateRecord 修改的配置值
   */
  @Bind()
  handleRecordLevelCodeChange(value, updateRecord) {
    const { $form } = updateRecord;
    switch (value) {
      case 'ROLE':
      case 'USER':
        $form.setFieldsValue({
          levelValue: undefined,
        });
        break;
      case 'GLOBAL':
      default:
        $form.setFieldsValue({
          levelValue: 'GLOBAL',
        });
        break;
    }
  }

  /**
   * handleRecordChange - 标记 record 为 update
   * @param {Object}  updateRecord 更新的记录
   */
  @Bind()
  handleRecordChange(updateRecord) {
    // eslint-disable-next-line
    updateRecord.updateStatus = 'update';
  }

  /**
   * handleRecordRemove-配置值单条删除
   * 新建的    直接删除
   * 服务端的  调接口成功后从dataSource删除
   * @param {Object} removeRecord 即将删除的配置值
   */
  @Bind()
  handleRecordRemove(removeRecord) {
    const { onRecordRemove } = this.props;
    const { dataSource } = this.state;
    // eslint-disable-next-line no-underscore-dangle
    if (removeRecord._status === 'create') {
      // 如果是新建的,直接删除
      this.setState({
        dataSource: filter(dataSource, (r) => {
          return r.profileValueId !== removeRecord.profileValueId;
        }),
      });
    } else {
      // 如果是之前存在的,调接口删除
      onRecordRemove(removeRecord).then((response) => {
        if (response) {
          this.setState({
            dataSource: filter(dataSource, (r) => {
              return r.profileValueId !== removeRecord.profileValueId;
            }),
          });
        }
      });
    }
  }

  @Bind()
  handleOkBtnClick() {
    const { isCreate, form, onOk, tenantId } = this.props;
    const { dataSource } = this.state;
    let saveProfile = {};
    let hasLineError = false;
    let hasHeadError = true;
    if (isCreate) {
      // 新增的
      form.validateFields((err, fields) => {
        if (!err) {
          hasHeadError = false;
          saveProfile.profileName = fields.profileName;
          saveProfile.description = fields.description;
          saveProfile.profileLevel = 'T';
          saveProfile.tenantId = tenantId;
          saveProfile._tls = fields._tls;
          const saveDataSource = getEditTableData(dataSource, ['updateStatus', 'profileValueId']);
          if (dataSource.length !== 0 && dataSource.length !== saveDataSource.length) {
            hasLineError = true;
            return;
          }
          saveProfile.profileValueList = saveDataSource;
        }
      });
    } else {
      const { profileValue } = this.props;
      form.validateFields((err, fields) => {
        if (!err) {
          hasHeadError = false;
          saveProfile = profileValue;
          saveProfile.description = fields.description;
          saveProfile._tls = fields._tls;
          const editDataSource = filter(dataSource, (r) => r.updateStatus);
          const saveDataSource = getEditTableData(editDataSource, [
            'updateStatus',
            'profileValueId',
          ]);
          if (editDataSource.length !== 0 && editDataSource.length !== saveDataSource.length) {
            hasLineError = true;
            return;
          }
          saveProfile.profileValueList = saveDataSource;
        }
      });
    }
    if (!hasLineError && !hasHeadError) {
      onOk(saveProfile);
    }
  }

  render() {
    const { profileValue, tenantId, editModalEditable, loading, ...modalProps } = this.props;
    const { dataSource } = this.state;
    return (
      <Modal
        {...modalProps}
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        footer={
          profileValue.tenantId === tenantId || profileValue.tenantId === undefined
            ? [
                <Button key="cancel" onClick={this.handleCloseModal}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </Button>,
                <Button type="primary" key="save" onClick={this.handleOkBtnClick} loading={loading}>
                  {intl.get('hzero.common.button.ok').d('确定')}
                </Button>,
              ]
            : null
        }
      >
        <div ref={this.containerRef}>
          <Content>
            <Affix target={this.getEditModalContentContainer}>{this.renderProfileForm()}</Affix>
            {editModalEditable ? (
              <Row>
                <Col span={3} />
                <Col span={21}>
                  <EditTable
                    bordered
                    rowKey="profileValueId"
                    pagination={false}
                    dataSource={dataSource}
                    columns={this.getColumns()}
                  />
                </Col>
              </Row>
            ) : (
              <Form.Item
                label={intl.get('hpfm.profile.model.profile.profileValue').d('配置值')}
                labelCol={{ span: 3 }}
                wrapperCol={{ span: 21 }}
              >
                <EditTable
                  bordered
                  rowKey="profileValueId"
                  pagination={false}
                  dataSource={dataSource}
                  columns={this.getColumns()}
                />
              </Form.Item>
            )}
          </Content>
        </div>
      </Modal>
    );
  }
}
