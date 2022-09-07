/**
 * Mapping - 映射列表
 * @Author: dufangjun <fangjun.du@hand-china.com>
 * @Date: 2019/10/13 14:20
 * @LastEditTime: 2019/10/25 10:40
 * @Copyright: Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Header, Content } from 'components/Page';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { observer } from 'mobx-react';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import {
  DataSet,
  Table,
  TextField,
  Form,
  CheckBox,
  Lov,
  Button,
  Tabs,
  Select,
} from 'choerodon-ui/pro';
import querystring from 'querystring';
import formatterCollections from 'utils/intl/formatterCollections';
import { saveNode } from '@/utils/saveNode';
import { MappingDS, MappingInFieldDS, MappingOutFieldDS } from '../stores';

const { TabPane } = Tabs;

@connect()
@formatterCollections({ code: ['hres.mapping', 'hres.common', 'hres.rule'] })
@observer
export default class ListPage extends Component {
  constructor(props) {
    super(props);
    this.mappingInFieldDS = new DataSet({
      ...MappingInFieldDS(),
      queryParameter: {
        ruleCode: props.match.params.code,
        tenantId: getCurrentOrganizationId(),
        mappingName: querystring.parse(props.location.search.substring(1)).componentName,
      },
    });
    this.mappingOutFieldDS = new DataSet({
      ...MappingOutFieldDS(),
      queryParameter: {
        ruleCode: props.match.params.code,
        tenantId: getCurrentOrganizationId(),
        mappingName: querystring.parse(props.location.search.substring(1)).componentName,
      },
    });
    this.mappingDS = new DataSet({
      ...MappingDS(),
      queryParameter: {
        ruleCode: props.match.params.code,
        tenantId: getCurrentOrganizationId(),
      },
      children: {
        mappingOutparameters: this.mappingOutFieldDS,
        mappingInparameters: this.mappingInFieldDS,
      },
    });
  }

  /**
   * 进入页面自动查询
   */
  async componentDidMount() {
    const {
      location: { search },
      match,
    } = this.props;
    const { componentName } = querystring.parse(search.substring(1));
    const { code, id } = match.params;
    this.mappingInFieldDS.getField('inparameterNameLov').setLovPara('ruleCode', code);
    if (isEmpty(componentName)) {
      this.mappingDS.create({ id, ruleCode: code, tenantId: getCurrentOrganizationId() });
    } else {
      this.mappingDS.setQueryParameter('mappingName', componentName);
      this.mappingDS.query();
    }
  }

  /**
   * 获取来源字段的列
   */
  get inColumns() {
    const {
      location: { search },
    } = this.props;
    const { pageType } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    return [
      { name: 'inparameterNameLov', editor: !frozenFlag && this.inValueEditor },
      { name: 'columnNameLov', editor: !frozenFlag && this.inValueEditor },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        command: this.commandIn,
        align: 'center',
      },
    ];
  }

  /**
   * 入参操作按钮组
   */
  get inButtons() {
    const {
      location: { search },
    } = this.props;
    const { pageType } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    return [
      <Button
        key="create-in"
        icon="playlist_add"
        color="primary"
        funcType="flat"
        disabled={frozenFlag}
        onClick={() => this.addInLineField()}
      >
        {intl.get('hzero.common.button.create').d('新建')}
      </Button>,
    ];
  }

  /**
   * 出参操作按钮组
   */
  get outButtons() {
    const {
      location: { search },
    } = this.props;
    const { pageType } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    return [
      <Button
        key="create-out"
        icon="playlist_add"
        color="primary"
        funcType="flat"
        disabled={frozenFlag}
        onClick={() => this.addOutLineField()}
      >
        {intl.get('hzero.common.button.create').d('新建')}
      </Button>,
    ];
  }

  /**
   * 新增入参自增序号行
   */
  @Bind()
  async addInLineField() {
    const {
      location: { search },
    } = this.props;
    const { componentName } = querystring.parse(search.substring(1));
    if (!componentName) {
      notification.error({
        message: intl.get('hres.mapping.view.message.validate.header').d('请先保存映射组件名称'),
      });
      return false;
    }
    this.mappingInFieldDS.create({}, 0);
  }

  /**
   * 新增出参自增序号行
   */
  @Bind()
  async addOutLineField() {
    const {
      location: { search },
    } = this.props;
    const { componentName } = querystring.parse(search.substring(1));
    if (!componentName) {
      notification.error({
        message: intl.get('hres.mapping.view.message.validate.header').d('请先保存映射组件名称'),
      });
      return false;
    }
    this.mappingOutFieldDS.create({}, 0);
  }

  /**
   * 获取目标字段的列
   */
  get outColumns() {
    const {
      location: { search },
    } = this.props;
    const { pageType } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    return [
      { name: 'outparameterName', editor: !frozenFlag && this.outValueEditor },
      { name: 'columnNameLov', editor: !frozenFlag && this.outValueEditor },
      { name: 'fieldType', editor: !frozenFlag && this.outValueEditor },
      { name: 'maskCode', editor: !frozenFlag && this.outValueEditor },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        command: this.commandOut,
        align: 'center',
      },
    ];
  }

  /**
   * 表单提交
   */
  @Bind()
  async handleSubmit() {
    const isModified =
      this.mappingDS.isModified() ||
      this.mappingInFieldDS.isModified() ||
      this.mappingOutFieldDS.isModified();
    const {
      dispatch,
      location: { search },
      match,
    } = this.props;
    if (!(await this.mappingDS.validate())) {
      notification.error({
        message: intl.get('hres.common.notification.required').d('存在必输字段未填写'),
      });
      return false;
    }
    const { id, code } = match.params;
    const { pageType, ruleName, componentName } = querystring.parse(search.substring(1));
    if (!isModified) {
      notification.warning({
        message: intl.get('hres.common.notification.unmodified').d('表单未做修改'),
      });
      return false;
    } else if (!componentName) {
      this.mappingDS.setQueryParameter('mappingName', this.mappingDS.current.get('mappingName'));
      const res = await this.mappingDS.submit();
      if (res && res.failed && res.message) {
        notification.error({
          message: res.message,
        });
      } else if (!isEmpty(res) && !res.status) {
        await saveNode(res.content[0].processNode, id);
        const pathname = `/hres/rules/flow/mapping/detail/${code}/${id}`;
        dispatch(
          routerRedux.push({
            pathname,
            search: querystring.stringify({
              componentName: res.content[0].mappingComponentList[0].mappingName,
              pageType,
              ruleName,
            }),
          })
        );
      }
    } else {
      await this.mappingDS.submit();
      this.mappingDS.query();
    }
  }

  /**
   * 入参tab判断是否可输
   * @param {*} record
   * @param {*} name
   */
  @Bind()
  inValueEditor(record, name) {
    switch (name) {
      case 'inparameterNameLov':
        if (!record.get('objectVersionNumber')) {
          return <Lov noCache />;
        }
        return false;
      case 'columnNameLov':
        return <Lov noCache />;
      default:
        return false;
    }
  }

  /**
   * 出参tab判断是否可输
   * @param {*} record
   * @param {*} name
   */
  @Bind()
  outValueEditor(record, name) {
    switch (name) {
      case 'outparameterName':
        if (!record.get('objectVersionNumber')) {
          return <TextField restrict="\S" />;
        }
        return false;
      case 'maskCode':
        if (record.get('fieldType') === 'DATE') {
          return <Select />;
        }
        return false;
      case 'columnNameLov':
        return <Lov noCache />;
      case 'fieldType':
        return <Select onChange={() => this.handleValueChange(record)} />;
      default:
        return false;
    }
  }

  /**
   * type改变时掩码状态变化
   */
  @Bind()
  handleValueChange(record) {
    if (['STRING', 'DATE', 'NUMBER'].includes(record.get('fieldType'))) {
      record.set('maskCode', null);
    }
  }

  /**
   * 来源字段行内操作按钮组
   */
  @Bind()
  commandIn({ record }) {
    return [
      <span className="action-link">
        <a onClick={() => this.handleInDelete(record)}>
          {intl.get('hzero.common.button.delete').d('删除')}
        </a>
      </span>,
    ];
  }

  /**
   * 目标字段行内操作按钮组
   */
  @Bind()
  commandOut({ record }) {
    return [
      <span className="action-link">
        <a onClick={() => this.handleOutDelete(record)}>
          {intl.get('hzero.common.button.delete').d('删除')}
        </a>
      </span>,
    ];
  }

  /**
   * 单行删除来源字段
   */
  @Bind()
  async handleInDelete(record) {
    this.mappingInFieldDS.delete([record]);
  }

  /**
   * 单行删除目标字段
   */
  @Bind()
  async handleOutDelete(record) {
    this.mappingOutFieldDS.delete([record]);
  }

  @Bind
  cleanColumnNameLov() {
    this.mappingInFieldDS.data.map((item) => {
      item.set('columnNameLov', undefined);
      return item;
    });
    this.mappingOutFieldDS.data.map((item) => {
      item.set('columnNameLov', undefined);
      return item;
    });
  }

  render() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { pageType, componentName, ruleName } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    return (
      <React.Fragment>
        <Header
          title={`${intl.get('hres.mapping.model.mapping.mappingCmp').d('映射组件')}_${ruleName}`}
          backPath={`/hres/rules/flow/detail/${code}?pageType=${pageType}&ruleName=${ruleName}`}
        >
          {!frozenFlag && (
            <Button icon="save" color="primary" onClick={this.handleSubmit}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          )}
        </Header>
        <Content>
          <Form dataSet={this.mappingDS} columns={3} disabled={frozenFlag}>
            <TextField name="mappingName" restrict="\S" disabled={componentName} />
            <Lov name="viewNameLov" noCache onChange={this.cleanColumnNameLov} />
            <CheckBox name="cacheFlag" />
          </Form>
          <Tabs key="tabs">
            <TabPane
              tab={intl.get('hres.mapping.model.mapping.inparameterName').d('来源字段')}
              key="in"
            >
              <Table
                disabled={frozenFlag}
                key="in"
                buttons={!frozenFlag && this.inButtons}
                columns={this.inColumns}
                dataSet={this.mappingInFieldDS}
              />
            </TabPane>
            <TabPane
              tab={intl.get('hres.mapping.model.mapping.outparameterName').d('目标字段')}
              key="out"
            >
              <Table
                disabled={frozenFlag}
                key="out"
                buttons={!frozenFlag && this.outButtons}
                columns={this.outColumns}
                dataSet={this.mappingOutFieldDS}
              />
            </TabPane>
          </Tabs>
        </Content>
      </React.Fragment>
    );
  }
}
