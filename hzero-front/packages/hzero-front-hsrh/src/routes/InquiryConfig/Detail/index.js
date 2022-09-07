/**
 * @since 2019-10-16
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Card } from 'choerodon-ui';
import {
  Table,
  Form,
  Button,
  TextField,
  Select,
  ModalContainer,
  DataSet,
  Modal,
  Lov,
  Switch,
  CodeArea,
  DateTimePicker,
  Tabs,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { words } from 'lodash';
import 'codemirror/theme/idea.css';
import JSONFormatter from 'choerodon-ui/pro/lib/code-area/formatters/JSONFormatter';
import 'choerodon-ui/pro/lib/code-area/lint/json';
import 'codemirror/mode/javascript/javascript';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { yesOrNoRender, operatorRender } from 'utils/renderer';

import { detailFormDS, detailTestDS, detailTableDS } from '@/stores/inquiryConfigGroupDS';

import Search from './Search';

const { Option } = Select;

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      isSuccess: false,
      configId: null,
      configCode: '',
      respondParam: JSON.stringify({
        query: { match_all: {} },
      }),
      activeStartTime: null,
      activeEndTime: null,
    };
    this.detailFormDS = new DataSet(detailFormDS());
    this.detailTestDS = new DataSet(detailTestDS());
    this.detailTableDS = new DataSet(detailTableDS(this.detailFormDS));
  }

  componentDidMount() {
    this.refresh();
  }

  component;

  // 退出销毁数据
  componentWillUnmount() {
    this.detailFormDS.reset();
    this.detailTableDS.reset();
  }

  // 刷新页面
  @Bind()
  async refresh() {
    const { match } = this.props;
    const {
      params: { type, configId = '' },
    } = match;
    if (type === 'create') {
      this.detailFormDS.create();
    } else {
      this.setState({ configId });
      this.detailFormDS.setQueryParameter('configId', configId);
      this.detailTableDS.setQueryParameter('configId', configId);
      const res = await this.detailFormDS.query().then((val) => {
        if (val.queryJson) {
          this.setState({
            respondParam: val.queryJson,
          });
        }
        if (val.activeStartTime) {
          this.setState({
            activeStartTime: val.activeStartTime,
          });
        }
        if (val.activeEndTime) {
          this.setState({
            activeEndTime: val.activeEndTime,
          });
        }
      });
      this.detailTableDS.query();
      if (res) {
        const { configCode } = res;
        this.setState({ configCode });
      }
    }
  }

  get columns() {
    const { match } = this.props;
    const {
      params: { type },
    } = match;
    const judge = type === 'detail';
    return [
      {
        name: 'fieldName',
        align: 'left',
      },
      {
        name: 'visibleFlag',
        align: 'left',
        renderer: ({ value }) => yesOrNoRender(value),
      },
      {
        name: 'sortFlag',
        align: 'left',
        renderer: ({ value }) => yesOrNoRender(value),
      },
      {
        name: 'sortDirect',
        align: 'left',
        renderer: ({ value }) =>
          value === 1
            ? intl.get('hsrh.inquiryConfig.view.title.reverseOrder.').d('倒序')
            : intl.get('hsrh.inquiryConfig.view.title.order').d('正序'),
      },
      {
        name: 'weight',
        align: 'left',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 200,
        renderer: ({ record }) => {
          const actions = [];
          actions.push(
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleEdit(record);
                  }}
                  disabled={judge}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleDelete(record);
                  }}
                  disabled={judge}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 编辑行
   */
  @Bind()
  handleEdit(record) {
    this.openModal(record);
  }

  /**
   * 删除行
   */
  @Bind()
  async handleDelete(record) {
    await this.detailTableDS.delete(record);
  }

  /**
   * 保存头
   */
  @Bind()
  async handleSave(isCreate) {
    const { respondParam } = this.state;
    const {
      match: {
        params: { type },
      },
      history,
    } = this.props;
    const validate = await this.detailFormDS.validate();
    this.detailFormDS.map((record) => record.set('queryJson', respondParam));
    if (isCreate && validate) {
      try {
        const res = await this.detailFormDS.submit();
        const { content, success } = res;
        const { configId, configCode } = content[0];
        if (success) {
          if (type === 'create') {
            history.push(`/hsrh/inquiry-config/edit/${configId}`);
          } else {
            this.detailFormDS.setQueryParameter('configId', configId);
            this.detailFormDS.query().then((val) => {
              if (val.queryJson) {
                this.setState({
                  respondParam: val.queryJson,
                });
              }
            });
            this.setState({
              isSuccess: true,
              configId,
              configCode,
            });
          }
        }
      } catch (err) {
        // 猪齿鱼自动弹出提示
      }
    }
  }

  /**
   * 打开模态框
   */
  @Bind()
  openModal(record, isNew) {
    let isCancel = false;
    const { match } = this.props;
    const {
      params: { type },
    } = match;
    const judge = type === 'create';
    Modal.open({
      title: judge
        ? intl.get('hsrh.inquiryConfig.view.message.crete').d('新建字段信息')
        : intl.get('hsrh.inquiryConfig.view.message.edit').d('编辑字段信息'),
      drawer: true,
      width: 520,
      children: (
        <Form dataSet={this.detailTableDS}>
          <Select name="visibleFlag">
            <Option value={1}>{intl.get('hzero.common.status.yes').d('是')}</Option>
            <Option value={0}>{intl.get('hzero.common.status.no').d('否')}</Option>
          </Select>
          <Select name="sortFlag">
            <Option value={1}>{intl.get('hzero.common.status.yes').d('是')}</Option>
            <Option value={0}>{intl.get('hzero.common.status.no').d('否')}</Option>
          </Select>
          <Select name="sortDirect">
            <Option value={0}>{intl.get('hsrh.inquiryConfig.view.title.order').d('正序')}</Option>
            <Option value={1}>
              {intl.get('hsrh.inquiryConfig.view.title.reverseOrder.').d('倒序')}
            </Option>
          </Select>
          <Lov name="fieldIdSet" />
          <TextField name="weight" />
        </Form>
      ),
      onOk: async () => {
        const validate = this.detailTableDS.validate();
        if (validate) {
          const res = await this.detailTableDS.submit();
          if (res) {
            const { configId } = this.state;
            this.detailTableDS.setQueryParameter('configId', configId);
            await this.detailTableDS.query();
          }
          return res;
        } else {
          return false;
        }
      },
      onCancel: () => {
        isCancel = true;
        return isCancel;
      },
      afterClose: () => isCancel && isNew && this.detailTableDS.remove(record),
    });
  }

  /**
   * 修改生效开始/结束日期
   */
  @Bind()
  dateTimeChange(active, Time) {
    if (active === 'start') {
      this.setState({
        activeStartTime: Time._i,
      });
    } else {
      this.setState({
        activeEndTime: Time._i,
      });
    }
  }

  /**
   * 新建行
   */
  @Bind()
  createItem() {
    const { configId } = this.state;
    this.openModal(this.detailTableDS.create({ configId }), true);
  }

  /**
   * 列表button
   */
  createButton = (
    <Button funcType="flat" color="primary" icon="playlist_add" onClick={this.createItem} key="add">
      {intl.get('hzero.common.button.add').d('新增')}
    </Button>
  );

  /**
   * 测试
   */
  @Bind()
  handleTest() {
    const [{ queryJson }] = this.detailFormDS.toData();
    const fieldArray = words(queryJson, /#{[A-Za-z0-9]+}/g).map((item) =>
      item.replace(/#|{|}/g, '')
    ); // 获取json格式的字符串中的占位符
    if (fieldArray.length !== 0) {
      this.openTestModal();
    } else {
      notification.warning({
        message: intl
          .get('hsrh.InquiryConfig.view.message.queryJsonValidate')
          .d('查询JSON为空或格式不正确！'),
      });
    }
  }

  /**
   * 打开测试模态框
   */
  openTestModal() {
    const { configCode } = this.state;
    const [{ queryJson }] = this.detailFormDS.toData();
    const fieldArray = words(queryJson, /#{[A-Za-z0-9]+}/g).map((item) =>
      item.replace(/#|{|}/g, '')
    ); // 获取json格式的字符串中的占位符
    fieldArray.forEach((item) => {
      this.detailTestDS.addField(item, { label: item, type: 'string' });
    });
    const nodeArray = fieldArray.map((item) => <TextField name={item} />);
    const nodeForm = [<Form dataSet={this.detailTestDS}>{nodeArray}</Form>];
    const updateModal = Modal.open({
      destroyOnClose: true,
      title: intl.get('hsrh.inquiryConfig.view.title.interfaceTest').d('接口测试'),
      drawer: true,
      width: 1000,
      okText: intl.get('hzero.common.button.trigger').d('执行'),
      children: <React.Fragment>{nodeForm}</React.Fragment>,
      onOk: async () => {
        this.detailTestDS.setQueryParameter('configCode', configCode);
        const res = await this.detailTestDS.query();
        updateModal.update({
          children: (
            <React.Fragment>
              {nodeForm}
              {res ? (
                <CodeArea
                  formatter={JSONFormatter}
                  options={{ theme: 'idea', mode: { name: 'javascript', json: true } }}
                  style={{ height: 320 }}
                  defaultValue={JSON.stringify(res, null, 4)}
                />
              ) : null}
            </React.Fragment>
          ),
        });
        return false;
      },
      onCancel: () => true,
      afterClose: () => this.detailTestDS.remove(),
    });
  }

  @Bind()
  respondParamOnChange(respondParam) {
    this.setState({
      respondParam,
    });
  }

  render() {
    const { isSuccess, respondParam, activeStartTime, activeEndTime } = this.state;
    const { match, location } = this.props;
    const {
      params: { type },
    } = match;
    const detail = this.detailFormDS.toData()[0];
    const judge = type === 'create' || type === 'edit';
    const configCodeFlag = type === 'edit';
    const tableNameEdit = type !== 'create';
    const buttonConfig = (judge && isSuccess) || type === 'edit' ? [this.createButton] : [];
    const searchProps = {
      indexId: detail ? detail.indexId : undefined,
      configCode: detail ? detail.configCode : undefined,
      respondParam,
      respondParamOnChange: this.respondParamOnChange,
    };

    return (
      <React.Fragment>
        <Header
          title={
            // eslint-disable-next-line no-nested-ternary
            judge
              ? type === 'create'
                ? intl.get('hzero.common.title.createSearchConfig').d('新建查询配置')
                : intl.get('hzero.common.title.editSearchConfig').d('编辑查询配置')
              : intl.get('hzero.common.title.inquiryConfigDetail').d('查询配置详情')
          }
          backPath="/hsrh/inquiry-config/list"
        >
          {type !== 'detail' ? (
            <React.Fragment>
              <Button color="primary" icon="save" onClick={() => this.handleSave(true)}>
                {intl.get('hzero.common.button.save').d('保存')}
              </Button>
              {/* <Button icon="APItest" onClick={() => this.handleTest()} disabled={testFlag}>
                {intl.get('hzero.common.button.test').d('测试')}
              </Button> */}
            </React.Fragment>
          ) : null}
        </Header>
        <Content>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={
              <h3>{intl.get('hsrh.inquiryConfig.view.title.basicInformation').d('基本信息')}</h3>
            }
          >
            <Form labelLayout="horizontal" dataSet={this.detailFormDS} columns={3}>
              <TextField name="configCode" disabled={!judge || configCodeFlag} />
              <Lov name="indexCodeSet" disabled={tableNameEdit} />
              <Switch name="enabledFlag" unCheckedValue={0} checkedValue={1} disabled={!judge} />
              <TextField name="remark" disabled={!judge} />
              <DateTimePicker
                onChange={(time) => this.dateTimeChange('start', time)}
                max={activeEndTime}
                name="activeStartTime"
                disabled={!judge}
              />
              <DateTimePicker
                onChange={(time) => this.dateTimeChange('end', time)}
                min={activeStartTime}
                name="activeEndTime"
                disabled={!judge}
              />
            </Form>
          </Card>
          <Tabs defaultActiveKey="Search" onChange={this.callback}>
            <Tabs.TabPane
              tab={intl.get('hsrh.inquiryConfig.view.title.search').d('查询')}
              key="Search"
            >
              <Search {...searchProps} />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={intl.get('hsrh.inquiryConfig.view.title.field').d('显示字段')}
              key="tableList"
            >
              <Table dataSet={this.detailTableDS} columns={this.columns} buttons={buttonConfig} />
            </Tabs.TabPane>
          </Tabs>
          <ModalContainer location={location} />
        </Content>
      </React.Fragment>
    );
  }
}
