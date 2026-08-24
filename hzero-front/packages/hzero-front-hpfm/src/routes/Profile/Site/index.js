/**
 * index.js
 * @author WY
 * @date 2018/10/11
 * @email yang.wang06@hand-china.com
 */

import React from 'react';
import { connect } from 'dva';
import { Button, Form, Input, Popconfirm, Table, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

import EditModal from './EditModal';

const { Item: FormItem } = Form;

@connect(({ loading, profile }) => ({
  fetching: loading.effects['profile/profileFetchList'],
  saving: loading.effects['profile/profileSave'],
  profile,
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.profile'] })
export default class ProfileSite extends React.Component {
  componentDidMount() {
    this.fetchBatchEnums();
    this.queryList();
  }

  @Bind()
  fetchBatchEnums() {
    const { dispatch } = this.props;
    dispatch({
      type: 'profile/fetchBatchEnums',
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
    const { dispatch, form } = this.props;
    const formValues = form.getFieldsValue();
    dispatch({
      type: 'profile/profileFetchList',
      payload: {
        page,
        sort,
        body: formValues,
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
    const { match } = this.props;
    if (!this.columns) {
      this.columns = [
        {
          title: intl.get('hpfm.profile.model.profile.name').d('配置编码'),
          dataIndex: 'profileName',
          width: 300,
        },
        {
          title: intl.get('hpfm.profile.model.profile.tenant').d('租户'),
          dataIndex: 'tenantName',
          width: 200,
        },
        {
          title: intl.get('hpfm.profile.model.profile.description').d('配置描述'),
          dataIndex: 'description',
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          width: 120,
          fixed: 'right',
          render: (item, record) => {
            const operators = [
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '配置维护(平台)-编辑',
                      },
                    ]}
                    onClick={() => {
                      this.handleProfileEdit(record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'delete',
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
                          meaning: '配置维护(平台)-删除',
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
  handleAddBtnClick() {
    // open new Modal
    const { dispatch } = this.props;
    dispatch({
      type: 'profile/openNewModal',
    });
  }

  /**
   * remove profile
   * @param {Object} profile
   */
  @Bind()
  handleProfileRemove(profile) {
    // remove profile
    const { dispatch } = this.props;
    dispatch({
      type: 'profile/profileRemoveOne',
      payload: profile,
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
    const { dispatch } = this.props;
    dispatch({
      type: 'profile/openEditModal',
      payload: record.profileId,
    });
  }

  @Bind()
  closeModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'profile/closeModal',
    });
  }

  @Bind()
  handleSaveProfile(profile) {
    const { dispatch } = this.props;
    dispatch({
      type: 'profile/profileSave',
      payload: profile,
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
    const { dispatch } = this.props;
    return dispatch({
      type: 'profile/profileValueRemove',
      payload: removeRecord,
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
        profileValue = {},
        isCreate = true,
      },
      match,
      fetching,
      saving,
    } = this.props;
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.profile.view.message.title.profile').d('配置维护')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '配置维护(平台)-新建',
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
            profileValue={profileValue}
            isCreate={isCreate}
            onCancel={this.closeModal}
            levelCode={levelCode}
            onRecordRemove={this.handleProfileValueRemove}
            onOk={this.handleSaveProfile}
            okButtonProps={{
              loading: saving,
            }}
          />
        </Content>
      </React.Fragment>
    );
  }
}
