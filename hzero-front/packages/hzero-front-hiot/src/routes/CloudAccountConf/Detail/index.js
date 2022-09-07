/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/20
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 云账户配置详情页
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import {
  Button,
  DataSet,
  Form,
  Lov,
  Select,
  TextArea,
  TextField,
  IntlField,
  Switch,
} from 'choerodon-ui/pro';
import { Card, Col, Row } from 'choerodon-ui';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { formConfigDS, cloudAccountDetailDS } from '@/stores/cloudAccountConfDS';

const prefix = 'hiot.cloudAccount';

@formatterCollections({ code: ['hiot.cloudAccount', 'hiot.common'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    const { operation = 'new', id } = props.match.params;
    this.state = {
      operation,
      isReadOnly: operation === 'detail',
      isEdit: operation === 'edit',
      recordId: id,
      formConfigList: [], // 表单配置
      enabledFlag: 0,
      platform: '',
    };
    this.cloudAccountDetailDS = new DataSet(cloudAccountDetailDS());
  }

  formConfigDs = null;

  componentDidMount() {
    const { recordId, operation } = this.state;
    if (operation !== 'new') {
      this.cloudAccountDetailDS.setQueryParameter('recordId', recordId);
      this.cloudAccountDetailDS.query().then((res) => {
        const { platform, addition, enabledFlag } = res;
        const codeObj = {
          ALI: 'HIOT.ACCOUNT_ALI',
          BAIDU: 'HIOT.ACCOUNT_BDC',
          OWN: 'HIOT.ACCOUNT_OWN',
        };
        let endpoints = [];
        let formatData = {};
        if (addition) {
          const other = `${addition}`;
          formatData = JSON.parse(`${other}`);
          const { hubAddressOriginal, hubAddressDefault } = formatData;
          if (platform === 'BAIDU') {
            endpoints = Object.values(JSON.parse(hubAddressOriginal));
          }
          this.cloudAccountDetailDS.current.set('hubAddressOriginalShow', endpoints.join('\n'));
          this.setState({
            enabledFlag,
            configFormData: formatData,
            hubAddressDefault: endpoints,
            addrIsEmpty: !!hubAddressDefault,
          });
        }
        this.setState({
          platform,
        });
        this.formConfigDs = new DataSet({
          ...formConfigDS(),
          queryParameter: {
            formCode: codeObj[platform],
          },
          feedback: {
            loadSuccess: (data) => {
              if (Array.isArray(data)) {
                data.forEach((item) => {
                  const isNumber = item.itemTypeCode === 'Number' || item.itemCode === 'accountId';
                  const field = {
                    name: item.itemCode,
                    label: item.itemName,
                    type: 'string',
                    pattern: isNumber ? /[\d]/g : '',
                    required: item.requiredFlag === 1,
                    defaultValidationMessages: {
                      patternMismatch: isNumber
                        ? intl.get(`${prefix}.view.validation.numberMsg`).d('请输入数字')
                        : '',
                    },
                  };
                  this.cloudAccountDetailDS.addField(item.itemCode, field);
                });
                this.setState({
                  formConfigList: data,
                });
                const initData = this.cloudAccountDetailDS.current.toData();
                this.cloudAccountDetailDS.loadData([initData]);
              }
            },
          },
        });
        this.formConfigDs.query();
        const currRec = this.cloudAccountDetailDS.get(0);
        currRec.set('hubAddressOriginalShow', endpoints.join('\n'));
      });
    } else {
      this.cloudAccountDetailDS.create({ operation });
    }
  }

  /**
   * 处理返回列表页
   */
  @Bind()
  handleBack() {
    const { history } = this.props;
    history.push('/hiot/cloud-account/config/list');
  }

  /**
   * 页面转为编辑
   */
  @Bind()
  handleEdit() {
    this.setState({
      isEdit: true,
      isReadOnly: false,
      operation: 'edit',
    });
  }

  @Bind()
  handleConfig() {
    this.cloudAccountDetailDS.validate().then((pass) => {
      if (pass) {
        this.cloudAccountDetailDS.submit().then(({ content }) => {
          const { history } = this.props;
          const { configId } = content[0];
          history.push(`/hiot/cloud-account/config/action/edit/${configId}`);
        });
      }
    });
  }

  @Bind()
  async handleSave() {
    const data = this.cloudAccountDetailDS.current.toData();
    const formatData = {};
    const { formConfigList = [] } = this.state;
    if (formConfigList.length) {
      formConfigList.forEach((item) => {
        formatData[item.itemCode] = data[item.itemCode];
      });
      this.cloudAccountDetailDS.current.set('addition', JSON.stringify(formatData));
    }
    const validate = await this.cloudAccountDetailDS.current.validate(true);
    if (validate) {
      await this.cloudAccountDetailDS.submit();
      this.handleBack();
    }
  }

  @Bind()
  renderForm(data) {
    const {
      isReadOnly,
      addrIsEmpty,
      hubAddressDefault = [],
      configFormData = {},
      platform,
      enabledFlag,
    } = this.state;
    const { itemCode } = data;
    switch (itemCode) {
      case 'accountId':
        return <TextField name={itemCode} />;
      case 'host':
        return <TextField name={itemCode} disabled={platform === 'OWN' ? !!enabledFlag : true} />;
      case 'hubAddressDefault':
        return (
          <Select
            name="hubAddressDefault"
            disabled={isReadOnly || !!addrIsEmpty}
            onChange={(value) => {
              const protocol = value.split(':')[0].toUpperCase();
              this.cloudAccountDetailDS.get(0).set('protocol', protocol);
            }}
          >
            {hubAddressDefault.map((item) => (
              <Select.Option value={item} key={item}>
                {item}
              </Select.Option>
            ))}
          </Select>
        );
      default:
        return <TextField name={itemCode} disabled={!!configFormData[itemCode]} />;
    }
  }

  @Bind()
  pageHeader() {
    const { operation } = this.state;
    const header = (title, { icon, handleAction, text }) => (
      <Header title={title} backPath="/hiot/cloud-account/config/list">
        <Button icon={icon} color="primary" onClick={handleAction}>
          {text}
        </Button>
      </Header>
    );
    const title = {
      new: intl.get(`${prefix}.view.new.configuration`).d('新建云账号配置'),
      edit: intl.get(`${prefix}.view.configuration.edit`).d('云账号配置编辑'),
      detail: intl.get(`${prefix}.view.configuration.detail`).d('云账号配置详情'),
    };
    const button = {
      new: {
        icon: 'save',
        handleAction: this.handleConfig,
        text: intl.get(`${prefix}.view.button.config`).d('配置'),
      },
      edit: {
        icon: 'save',
        handleAction: this.handleSave,
        text: intl.get('hzero.common.button.save').d('保存'),
      },
      detail: {
        icon: 'save',
        handleAction: this.handleEdit,
        text: intl.get('hzero.common.button.edit').d('编辑'),
      },
    };
    return header(title[operation], button[operation]);
  }

  render() {
    const { isReadOnly, isEdit, formConfigList = [], enabledFlag } = this.state;
    return (
      <>
        {this.pageHeader()}
        <Content>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get(`${prefix}.view.title.baseInfo`).d('基本信息')}
          >
            <Row>
              <Col span={18}>
                <Form labelWidth={140} columns={2} dataSet={this.cloudAccountDetailDS}>
                  <IntlField name="configName" disabled={isEdit || isReadOnly} />
                  <TextField name="configCode" disabled={isEdit || isReadOnly} />
                  <Lov
                    name="platformInfo"
                    disabled={isEdit || isReadOnly}
                    onChange={(val) => {
                      if (val && val.value === 'OWN') {
                        this.cloudAccountDetailDS.current.set('endpointInfo', {});
                      }
                    }}
                  />
                  {!isEdit && <TextField name="accessKey" />}
                  {!isEdit && <TextField name="secretKey" />}
                  <Lov name="endpointInfo" noCache disabled={isEdit || isReadOnly} />
                  {/* {isALI && <TextField name="accountId" disabled={isReadOnly} />} */}
                  <TextArea name="description" disabled={isReadOnly} />
                  <Switch name="enabledFlag" />
                </Form>
              </Col>
            </Row>
          </Card>
          {(isEdit || isReadOnly) && (
            <Card
              className={DETAIL_CARD_CLASSNAME}
              bordered={false}
              title={intl.get(`${prefix}.view.title.conn-info`).d('连接信息')}
            >
              <Row>
                <Col span={18}>
                  <Form
                    labelWidth={140}
                    columns={2}
                    dataSet={this.cloudAccountDetailDS}
                    disabled={!!enabledFlag}
                  >
                    {formConfigList.map((item) => this.renderForm(item))}
                  </Form>
                </Col>
              </Row>
            </Card>
          )}
        </Content>
      </>
    );
  }
}
