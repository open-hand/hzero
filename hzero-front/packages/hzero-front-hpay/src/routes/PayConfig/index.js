import React from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { encryptPwd } from 'utils/utils';

import Filter from './Filter';
import List from './List';
import Editor from './Editor';
import Cert from './Cert';

@formatterCollections({ code: ['hpay.payConfig'] })
@connect(({ payConfig, loading }) => ({
  payConfig,
  listLoading: loading.effects['payConfig/fetchConfigList'],
  detailLoading: loading.effects['payConfig/fetchConfigDetail'],
  createLoading: loading.effects['payConfig/createConfig'],
  updateLoading: loading.effects['payConfig/updateConfig'],
  certLoading: loading.effects['payConfig/fetchCert'],
  deleteCertLoading: loading.effects['payConfig/deleteCert'],
}))
export default class PayConfig extends React.Component {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
    this.state = {
      editorVisible: false,
      certVisible: false,
      currentRecord: {},
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'payConfig/init' });
    this.fetchConfigList();
    this.fetchPublicKey();
  }

  @Bind()
  fetchConfigList(params = {}) {
    const { dispatch, payConfig: { pagination = {} } = {} } = this.props;
    const filterValue = this.filterFormRef.current.getFieldsValue();
    dispatch({
      type: 'payConfig/fetchConfigList',
      payload: { page: pagination, ...filterValue, ...params },
    });
  }

  @Bind()
  showModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'payConfig/updateState',
      payload: { payConfigDetail: {} },
    });
    this.setState({ editorVisible: true });
  }

  @Bind()
  hideModal() {
    this.setState({ editorVisible: false });
  }

  @Bind()
  fetchCert(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'payConfig/fetchCert',
      payload: { configId: params.configId },
    });
  }

  @Bind()
  handleCert(record = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'payConfig/updateState',
      payload: { certDetail: {} },
    });
    this.fetchCert(record);
    this.setState({ certVisible: true, currentRecord: record });
  }

  @Bind()
  clearCert(data) {
    const {
      dispatch,
      payConfig: { certDetail = {} },
    } = this.props;
    const { currentRecord = {} } = this.state;
    dispatch({
      type: 'payConfig/deleteCert',
      payload: {
        ...certDetail,
        ...data,
        channelCode: currentRecord.channelCode,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideCert();
        this.fetchConfigList();
      }
    });
  }

  @Bind()
  hideCert() {
    this.setState({ certVisible: false });
  }

  @Bind()
  handleEdit({ configId }) {
    const { dispatch } = this.props;
    dispatch({
      type: 'payConfig/fetchConfigDetail',
      payload: {
        configId,
      },
    });
    this.showModal();
  }

  @Bind()
  saveConfig(data = {}) {
    const {
      dispatch,
      payConfig: { payConfigDetail = {}, publicKey },
    } = this.props;
    const newData = { ...data };
    if (data.certPwd) {
      newData.certPwd = encryptPwd(data.certPwd, publicKey);
    }
    const type =
      payConfigDetail.configId !== undefined ? 'payConfig/updateConfig' : 'payConfig/createConfig';
    const payloadData =
      payConfigDetail.configId !== undefined ? { ...payConfigDetail, ...newData } : newData;
    dispatch({
      type,
      payload: payloadData,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchConfigList();
        this.hideModal();
      }
    });
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    const { dispatch = () => {} } = this.props;
    dispatch({
      type: 'payConfig/getPublicKey',
    });
  }

  render() {
    const {
      listLoading = false,
      detailLoading = false,
      createLoading = false,
      updateLoading = false,
      deleteCertLoading = false,
      certLoading = false,
      payConfig: {
        payConfigList = [],
        payConfigDetail = {},
        pagination = {},
        channelCodeList = [],
        certDetail = {},
      } = {},
    } = this.props;
    const { editorVisible = false, certVisible, currentRecord } = this.state;

    const listProps = {
      pagination,
      loading: listLoading,
      dataSource: payConfigList,
      onEdit: this.handleEdit,
      onCert: this.handleCert,
      onChange: this.fetchConfigList,
    };

    const editorProps = {
      channelCodeList,
      loading: createLoading || updateLoading,
      title: payConfigDetail.configId
        ? intl.get('hpay.payConfig.view.message.edit').d('编辑支付配置')
        : intl.get('hpay.payConfig.view.message.create').d('新建支付配置'),
      initLoading: detailLoading,
      visible: editorVisible,
      initData: payConfigDetail,
      onCancel: this.hideModal,
      onOk: this.saveConfig,
    };

    const certProps = {
      certDetail,
      visible: certVisible,
      initData: currentRecord,
      initLoading: certLoading,
      deleteLoading: deleteCertLoading,
      title: `${payConfigDetail.channelMeaning || currentRecord.channelMeaning}${intl
        .get('hpay.payConfig.view.message.cert')
        .d('证书')}`,
      onCert: this.fetchCert,
      onCancel: this.hideCert,
      onClearCert: this.clearCert,
    };

    return (
      <>
        <Header title={intl.get('hpay.payConfig.view.message.title').d('支付配置')}>
          <Button icon="plus" type="primary" onClick={this.showModal}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <Filter
            channelCodeList={channelCodeList}
            onSearch={this.fetchConfigList}
            ref={this.filterFormRef}
          />
          <List {...listProps} />
          {editorVisible && <Editor {...editorProps} />}
          {certVisible && <Cert {...certProps} />}
        </Content>
      </>
    );
  }
}
