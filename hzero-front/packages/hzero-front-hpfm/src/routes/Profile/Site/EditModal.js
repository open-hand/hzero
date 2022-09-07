/**
 * EditModal
 * @description
 * @author WY yang.wang06@hand-china.com
 * @date 2018/10/11
 */

import React from 'react';

import { Affix, Col, Form, Input, Modal, Popconfirm, Row, Select } from 'hzero-ui';
import { cloneDeep, filter, map } from 'lodash';
import uuid from 'uuid/v4';
import { Bind } from 'lodash-decorators';

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
    if (!this.columns) {
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
                    onChange={(value) => {
                      return this.handleRecordLevelCodeChange(value, record);
                    }}
                    className={styles['full-width']}
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
                    disabled={tenantId === undefined}
                    textValue={record.levelValueDescription}
                    code="HIAM.SITE.USER"
                    onChange={(value) => this.handleRecordChange(value, record)}
                    queryParams={{ organizationId: tenantId, enabled: 1 }}
                    className={styles['full-width']}
                  />
                );
                break;
              case 'ROLE':
                $levelValueInputComponent = (
                  <Lov
                    disabled={tenantId === undefined}
                    textValue={record.levelValueDescription}
                    code="HIAM.SITE.ROLE"
                    onChange={(value) => this.handleRecordChange(value, record)}
                    queryParams={{ tenantId, isEnabled: 1 }}
                    className={styles['full-width']}
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
          dataIndex: 'value',
          width: 200,
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
                })(<Input onChange={(e) => this.handleRecordChange(e.target.value, record)} />)}
              </FormItem>
            );
          },
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          width: 120,
          render: (_, record) => {
            const operators = [
              {
                key: 'delete',
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
                          meaning: '配置维护(平台)配置值-删除',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              },
            ];
            return operatorRender(operators);
          },
        },
      ];
    }
    return this.columns;
  }

  @Bind()
  renderProfileForm() {
    const { profileValue = {}, form, isCreate, match } = this.props;
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
              })(<Input disabled={!isCreate} typeCase="upper" inputChinese={false} />)}
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
                />
              )}
            </FormItem>
          </Col>
          <Col md={12} sm={24}>
            <FormItem
              required
              label={intl.get('hpfm.profile.model.profile.tenant').d('租户')}
              labelCol={{ span: 6 }}
              wrapperCol={{ span: 18 }}
            >
              {form.getFieldDecorator('tenantId', {
                initialValue: profileValue.tenantId,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.profile.model.profile.tenant').d('租户'),
                    }),
                  },
                ],
              })(
                <Lov
                  textValue={profileValue.tenantName}
                  disabled={!isCreate}
                  code="HPFM.TENANT"
                  onChange={this.handleProfileTenantIdChange}
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row style={{ backgroundColor: '#fff' }}>
          <Col>
            <FormItem
              label={intl.get('hpfm.profile.model.profile.profileValue').d('配置值')}
              labelCol={{ span: 3 }}
              wrapperCol={{ span: 21 }}
            >
              <ButtonPermission
                permissionList={[
                  {
                    code: `${match.path}.button.valueAdd`,
                    type: 'button',
                    meaning: '配置维护(平台)配置值-新增配置值',
                  },
                ]}
                onClick={this.handleRecordAddBtnClick}
                htmlType="button"
              >
                {intl.get('hpfm.profile.view.form.create').d('新增配置值')}
              </ButtonPermission>
            </FormItem>
          </Col>
        </Row>
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
    const { isCreate } = this.props;
    if (isCreate) {
      const { form } = this.props;
      return form.getFieldValue('tenantId');
    }
    const { profileValue = {} } = this.props;
    return profileValue.tenantId;
  }

  /**
   * handleRecordAddBtnClick-配置值新增按钮点击
   */
  @Bind()
  handleRecordAddBtnClick() {
    const { isCreate, profileValue } = this.props;
    const { dataSource } = this.state;
    const nRecord = {
      updateStatus: 'create',
      _status: 'create',
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
    // 不需要更新 页面, 只需要更新标志位
    // eslint-disable-next-line
    updateRecord.updateStatus = 'update';
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
   * handleRecordChange - 更新 record 的 updateStatus 标志为编辑
   * @param {*}       value 更新的值
   * @param {Object}  updateRecord 更新的记录
   */
  @Bind()
  handleRecordChange(value, updateRecord) {
    // 不需要更新 页面, 只需要更新标志位
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
    const { isCreate, form, onOk } = this.props;
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
          saveProfile.profileLevel = fields.profileLevel;
          saveProfile._tls = fields._tls;

          const saveDataSource = getEditTableData(dataSource, ['profileValueId', 'updateStatus']);
          if (dataSource.length !== 0 && saveDataSource.length !== dataSource.length) {
            hasLineError = true;
            return; // 有错误退出后续代码
          }
          // 租户级
          saveProfile.tenantId = fields.tenantId;
          saveProfile.profileValueList = map(saveDataSource, (profileValue) => {
            return {
              levelCode: profileValue.levelCode,
              levelValue: profileValue.levelValue,
              value: profileValue.value,
            };
          });
        }
      });
    } else {
      const { profileValue } = this.props;
      form.validateFields((err, fields) => {
        if (!err) {
          hasHeadError = false;
          saveProfile = { ...profileValue };
          saveProfile.description = fields.description;
          saveProfile._tls = fields._tls;
          // 租户级
          saveProfile.tenantId = profileValue.tenantId;
          const editDataSource = filter(dataSource, (r) => r.updateStatus);
          const saveDataSource = getEditTableData(editDataSource, [
            'profileValueId',
            'updateStatus',
          ]);
          if (editDataSource.length !== 0 && saveDataSource.length !== editDataSource.length) {
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
    const { profileValue, ...modalProps } = this.props;
    const { dataSource } = this.state;
    return (
      <Modal
        {...modalProps}
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCloseModal}
        onOk={this.handleOkBtnClick}
      >
        <div ref={this.containerRef}>
          <Content>
            <Affix target={this.getEditModalContentContainer}>{this.renderProfileForm()}</Affix>
            <Row type="flex">
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
          </Content>
        </div>
      </Modal>
    );
  }
}
