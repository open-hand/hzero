/**
 * 配置维护-租户级
 * @date 2018/10/11
 * @author wangyang <yang.wang06@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Button, Col, Form, Input, Popconfirm, Row, Table, Tag } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

import EditModal from './EditModal';

const { Item: FormItem } = Form;

@connect(({ loading, profileOrg }) => ({
  fetching: loading.effects['profileOrg/profileFetchList'],
  saving: loading.effects['profileOrg/profileSave'],
  profile: profileOrg,
  organizationId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.profile'] })
export default class ProfileOrg extends React.Component {
  componentDidMount() {
    this.fetchBatchEnums();
    this.queryList();
  }

  @Bind()
  fetchBatchEnums() {
    const { dispatch } = this.props;
    dispatch({
      type: 'profileOrg/fetchBatchEnums',
    });
  }

  @Bind()
  handleResetForm() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * form submit
   */
  @Bind()
  fetchList(e) {
    e.preventDefault();
    this.queryList();
  }

  @Bind()
  queryList(page, sort) {
    // queryList
    const { dispatch, form, organizationId } = this.props;
    const formValues = form.getFieldsValue();
    dispatch({
      type: 'profileOrg/profileFetchList',
      payload: {
        organizationId,
        payload: { page, sort, body: formValues },
      },
    });
  }

  @Bind()
  reloadList() {
    // reloadList
    const {
      profile: { pagination },
    } = this.props;
    this.queryList(pagination);
  }

  @Bind()
  getColumns() {
    const { organizationId, match } = this.props;
    if (!this.columns) {
      this.columns = [
        {
          title: intl.get('hpfm.profile.model.profile.name').d('配置编码'),
          dataIndex: 'profileName',
          width: 300,
        },
        {
          title: intl.get('hpfm.profile.model.profile.description').d('配置描述'),
          dataIndex: 'description',
        },
        {
          title: intl.get('hzero.common.source').d('来源'),
          width: 100,
          key: 'sourcing',
          render: (_, record) => {
            return organizationId === record.tenantId ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            );
          },
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          width: 120,
          fixed: 'right',
          key: 'operator',
          render: (_, record) => {
            const actions = [];
            if (organizationId === record.tenantId) {
              actions.push({
                key: 'edit',
                len: 2,
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '配置维护(租户)-编辑',
                      },
                    ]}
                    onClick={() => {
                      this.handleProfileEdit(record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
              });
              actions.push({
                key: 'remove',
                len: 2,
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={() => {
                      this.handleProfileRemove(record);
                    }}
                  >
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.delete`,
                          type: 'button',
                          meaning: '配置维护(租户)-删除',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
              });
            } else {
              actions.push({
                key: 'view',
                len: 2,
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.view`,
                        type: 'button',
                        meaning: '配置维护(租户)-查看',
                      },
                    ]}
                    onClick={() => {
                      this.handleProfileView(record);
                    }}
                  >
                    {intl.get('hzero.common.button.view').d('查看')}
                  </ButtonPermission>
                ),
              });
            }
            return operatorRender(actions);
          },
        },
      ];
    }
    return this.columns;
  }

  @Bind()
  handleAddBtnClick() {
    // open new Modal
    const { dispatch } = this.props;
    dispatch({
      type: 'profileOrg/openNewModal',
    });
  }

  /**
   * remove profile
   * @param {Object} profile
   */
  @Bind()
  handleProfileRemove(profile) {
    // remove profile
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'profileOrg/profileRemoveOne',
      payload: {
        organizationId,
        payload: profile,
      },
    }).then((res) => {
      if (res) {
        // 返回第一页
        notification.success();
        this.queryList();
      }
    });
  }

  @Bind()
  handleProfileEdit(record) {
    // edit
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'profileOrg/openEditModal',
      payload: {
        organizationId,
        payload: record.profileId,
      },
    });
  }

  /**
   * 查看当前配置值
   * @param {Object} record - 配置头
   */
  @Bind()
  handleProfileView(record) {
    // edit
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'profileOrg/openViewModal',
      payload: {
        organizationId,
        payload: record.profileId,
      },
    });
  }

  @Bind()
  closeModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'profileOrg/closeModal',
    });
  }

  @Bind()
  handleSaveProfile(profile) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'profileOrg/profileSave',
      payload: {
        organizationId,
        payload: profile,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.closeModal();
        this.reloadList();
      }
    });
  }

  @Bind()
  handleProfileValueRemove(removeRecord) {
    const { dispatch, organizationId } = this.props;
    return dispatch({
      type: 'profileOrg/profileValueRemove',
      payload: {
        organizationId,
        payload: removeRecord,
      },
    });
  }

  render() {
    const {
      form: { getFieldDecorator },
      profile: {
        enumMap: { levelCode = [] },
        list = [],
        pagination = false,
        editModalVisible = false,
        editModalEditable = false,
        profileValue = {},
        isCreate = true,
      },
      match,
      organizationId,
      fetching = false,
      saving = false,
    } = this.props;
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.profile.view.message.title.profile').d('配置维护')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '配置维护(租户)-新建',
              },
            ]}
            onClick={this.handleAddBtnClick}
            icon="plus"
            type="primary"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div>
            <Form>
              <Row {...SEARCH_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_4_LAYOUT}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hpfm.profile.model.profile.name').d('配置编码')}
                  >
                    {getFieldDecorator(
                      'profileName',
                      {}
                    )(<Input typeCase="upper" inputChinese={false} />)}
                  </FormItem>
                </Col>
                <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
                  <FormItem>
                    <Button onClick={this.handleResetForm}>
                      {intl.get('hzero.common.button.reset').d('重置')}
                    </Button>
                    <Button onClick={this.fetchList} type="primary" htmlType="submit">
                      {intl.get('hzero.common.button.search').d('查询')}
                    </Button>
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </div>
          <Table
            bordered
            rowKey="profileId"
            columns={this.getColumns()}
            dataSource={list}
            pagination={pagination}
            onChange={this.queryList}
            loading={fetching}
          />
          <EditModal
            title={intl.get('hpfm.profile.view.message.title').d('配置维护')}
            width={1000}
            visible={editModalVisible}
            match={match}
            editModalEditable={editModalEditable}
            profileValue={profileValue}
            isCreate={isCreate}
            onCancel={this.closeModal}
            levelCode={levelCode}
            onRecordRemove={this.handleProfileValueRemove}
            onOk={this.handleSaveProfile}
            loading={saving}
            tenantId={organizationId}
          />
        </Content>
      </React.Fragment>
    );
  }
}
