import React, { Component } from 'react';
import { Form, DataSet, TextField, Select, TextArea, IntlField } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';

import intl from 'utils/intl';
import notification from 'utils/notification';

import { dataPointTypeDetailDS } from '@/stores/dataPointTypeManagementDS';

const categoryMap = new Map([
  ['MEASURING_POINT', ['NUMBER']],
  ['STATUS_POINT', ['BOOL', 'ENUM']],
  ['CONTROL_PARAMETER', ['NUMBER', 'BOOL', 'ENUM', 'DATE', 'DATE_TIME']],
]);

/**
 * 数据点类型新建、详情、编辑
 */
@formatterCollections({ code: ['hiot.dataPointTypeManagement', 'hiot.common'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    const { operation, id } = props.match.params;
    this.state = {
      operation,
      isReadOnly: operation === 'detail',
      isEdit: operation === 'edit',
      typeId: id,
      selectedCategory: '',
    };
    this.detailDS = new DataSet(dataPointTypeDetailDS());
  }

  componentDidMount() {
    const { typeId, isReadOnly, isEdit } = this.state;
    if (isReadOnly || isEdit) {
      this.detailDS.setQueryParameter('typeId', typeId);
      this.detailDS.query();
    } else {
      this.detailDS.create({}, 0);
    }
  }

  /**
   * 保存数据
   */
  @Bind()
  async handleSave() {
    const { operation } = this.state;
    const validFlag = await this.detailDS.validate();
    if (validFlag) {
      // 校验分类与数据类型是否对应
      const record = this.detailDS.get(0);
      const category = record.get('category');
      const dataType = record.get('dataType');
      if (categoryMap.get(category).indexOf(dataType) === -1) {
        notification.error({
          message: intl
            .get('hiot.dataPointTypeManagement.message.error.category')
            .d('分类与数据类型不匹配！'),
        });
        return;
      }
      // 编辑的情况下，判断数据是否有改动
      if (operation === 'edit' && !this.detailDS.isModified()) {
        this.goBack();
        return;
      }
      const resp = await this.detailDS.submit();
      const { success } = resp;
      if (success) {
        this.goBack();
      }
    }
  }

  /**
   * 回到上一级页面
   */
  @Bind()
  goBack() {
    const { history } = this.props;
    history.push('/hiot/dptm');
  }

  /**
   * 处理编辑
   */
  @Bind()
  handleEdit() {
    this.setState({
      operation: 'edit',
      isReadOnly: false,
      isEdit: true,
    });
  }

  @Bind()
  handleCategoryChange(value) {
    this.setState({
      selectedCategory: value,
    });
  }

  @Bind
  optionFilter(record) {
    const { selectedCategory } = this.state;
    if (selectedCategory) {
      return categoryMap.get(selectedCategory).indexOf(record.get('value')) > -1;
    }
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { isReadOnly, isEdit, operation } = this.state;
    const headers = {
      edit: (
        <Header
          title={intl
            .get('hiot.dataPointTypeManagement.view.title.header.edit')
            .d('数据点类型编辑')}
          backPath="/hiot/dptm"
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '数据点类型编辑-保存',
              },
            ]}
            icon="save"
            color="primary"
            // type="submit"
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
      ),
      detail: (
        <Header
          title={intl
            .get('hiot.dataPointTypeManagement.view.title.header.detail')
            .d('数据点类型详情')}
          backPath="/hiot/dptm"
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.edit`,
                type: 'button',
                meaning: '数据点类型详情-编辑',
              },
            ]}
            icon="mode_edit"
            color="primary"
            onClick={this.handleEdit}
          >
            {intl.get('hzero.common.button.edit').d('编辑')}
          </ButtonPermission>
        </Header>
      ),
      new: (
        <Header
          title={intl.get('hiot.dataPointTypeManagement.view.title.header.new').d('新建数据点类型')}
          backPath="/hiot/dptm"
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '新建数据点类型-保存',
              },
            ]}
            icon="save"
            color="primary"
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
      ),
    };
    return (
      <>
        {headers[operation || 'new']}
        <Content>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get('hiot.common.view.baseInfo').d('基本信息')}
          >
            <Form columns={3} dataSet={this.detailDS}>
              <TextField
                name="typeCode"
                disabled={isReadOnly || isEdit}
                required // 此处的required会使标签带星号，dataset中的required会使文本框变黄色
              />
              <IntlField name="typeName" disabled={isReadOnly || isEdit} required />
              <Select
                newLine
                name="category"
                required
                disabled={isReadOnly || isEdit}
                onChange={this.handleCategoryChange}
              />
              <Select
                name="dataType"
                required
                disabled={isReadOnly || isEdit}
                optionsFilter={this.optionFilter}
              />
              <TextArea
                newLine
                colSpan={1.5}
                rows={3}
                name="description"
                disabled={(isReadOnly || !isEdit) && !!operation}
              />
            </Form>
          </Card>
        </Content>
      </>
    );
  }
}
