import React from 'react';
import { Form, TextField, Table, DataSet, Switch, Modal, Tooltip } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import axios from 'axios';

import intl from 'utils/intl';
import { HZERO_IM } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import { CustomerDS, TagDS, knowledgeDs } from '../../stores/customerGroupDS';

const organizationId = getCurrentOrganizationId();
const apiPrefix = `${HZERO_IM}/v1/${organizationId}`;

export default class Drawer extends React.Component {
  customerDS = new DataSet(CustomerDS());

  TagDS = new DataSet(TagDS());

  knowledgeDs = new DataSet(knowledgeDs());

  async componentDidMount() {
    const { id } = this.props;
    if (id !== undefined) {
      this.customerDS.csGroupId = id;
      await this.customerDS.query();
    }
  }

  tagButton = (
    <Tooltip title={intl.get('hims.customerGroup.view.message.editTag').d('标签维护')}>
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${this.props.path}.button.editTag`,
            type: 'button',
            meaning: '客服群组-标签维护',
          },
        ]}
        funcType="flat"
        color="primary"
        icon="knowledge"
        onClick={this.handleOpenTags}
        key="knowledge"
      />
    </Tooltip>
  );

  addButton = (
    <ButtonPermission
      type="c7n-pro"
      permissionList={[
        {
          code: `${this.props.path}.button.add`,
          type: 'button',
          meaning: '客服群组-新增',
        },
      ]}
      funcType="flat"
      color="primary"
      icon="playlist_add"
      onClick={this.handleOpenAddTags}
      key="knowledge"
    >
      {intl.get('hzero.common.button.add').d('新增')}
    </ButtonPermission>
  );

  get columns() {
    return [
      {
        name: 'userLov',
        editor: true,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        command: () => [['delete', { color: 'red' }], this.tagButton],
        align: 'left',
      },
    ];
  }

  get tagColumns() {
    return [
      {
        name: 'categoryCode',
        width: 200,
      },
      {
        name: 'categoryName',
        width: 200,
      },
    ];
  }

  get buttons() {
    const { id } = this.props;
    return [['add', { disabled: !id }]];
  }

  get tagButtons() {
    return [this.addButton, ['delete', { onClick: this.deleteTag }]];
  }

  @Bind()
  async deleteTag() {
    await this.TagDS.delete(this.TagDS.selected);
    this.TagDS.query();
  }

  @Bind()
  handleOpenTags() {
    const {
      editData: {
        data: { csGroupId },
      },
    } = this.props;
    this.TagDS.csGroupId = csGroupId;
    this.TagDS.userId = this.customerDS.current.data.id;
    this.TagDS.query();
    Modal.open({
      closable: true,
      key: 'TAG',
      title: intl.get('hims.customerGroup.view.message.tag').d('知识标签'),
      drawer: true,
      style: {
        width: 720,
      },
      children: (
        <Table
          buttons={this.tagButtons}
          dataSet={this.TagDS}
          highLightRow={false}
          columns={this.tagColumns}
        />
      ),
      onCancel: () => {},
      onClose: () => {},
    });
  }

  @Bind()
  handleOpenAddTags() {
    this.knowledgeDs.query();
    Modal.open({
      closable: true,
      key: 'knowledge',
      title: intl.get('hims.customerGroup.view.message.knowledge').d('知识类别'),
      style: {
        width: 720,
      },
      children: (
        <Table
          mode="tree"
          dataSet={this.knowledgeDs}
          columns={this.tagColumns}
          highLightRow={false}
        />
      ),
      onOk: this.handleAddTags,
    });
  }

  @Bind()
  async handleAddTags() {
    if (this.knowledgeDs.selected.length > 0) {
      const arr = this.knowledgeDs.selected.map((item) => item.toData());
      const { csGroupId, userId } = this.TagDS;
      axios({
        url: `${apiPrefix}/cs-group-user-tags/${csGroupId}/${userId}`,
        data: arr,
        method: 'POST',
      })
        .then((res) => {
          if (res) {
            this.TagDS.query();
            notification.success();
          }
        })
        .catch((err) => {
          notification.error({
            message: err.message,
          });
        });
    }
  }

  render() {
    const { editData, id } = this.props;
    return (
      <>
        <Form record={editData}>
          <TextField name="groupKey" disabled={id !== undefined} />
          <TextField name="groupName" />
          <TextField name="description" />
          <Switch name="enabledFlag" />
        </Form>
        <Table
          dataSet={this.customerDS}
          columns={this.columns}
          buttons={this.buttons}
          editMode="inline"
        />
      </>
    );
  }
}
