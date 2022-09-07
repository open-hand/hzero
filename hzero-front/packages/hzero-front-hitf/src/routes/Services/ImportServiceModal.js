import React, { PureComponent } from 'react';
import { Collapse, Icon } from 'choerodon-ui';
import {
  DataSet,
  Form,
  Lov,
  TextField,
  Button,
  Password,
  Select,
  Tree,
  Modal,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isTenantRoleLevel, getResponse } from 'utils/utils';
import { isEmpty, omit } from 'lodash';
import ReactFileReader from 'react-file-reader';
import { loadWsdl, loadWsdlFile } from '@/services/servicesService';
import getLang from '@/langs/serviceLang';
import notification from 'utils/notification';
import { basicDS, advanceDS, treeDS } from '../../stores/Services/ImportServiceDS';

const { Panel } = Collapse;
export default class ImportServiceModal extends PureComponent {
  constructor(props) {
    super(props);
    this.basicDS = new DataSet(basicDS());
    this.advanceDS = new DataSet(advanceDS());
    this.treeDS = new DataSet(treeDS());
    this.state = {
      files: [],
      interfaceList: [],
      wsdlList: [],
      reaParams: {},
      loading: false,
    };
  }

  componentDidMount() {
    this.props.modal.update({
      onOk: this.handleOk,
    });
  }

  @Bind()
  async handleOk() {
    const { wsdlList, reaParams } = this.state;
    const { openDetail } = this.props;
    const validate = await this.basicDS.validate();
    if (!validate) {
      notification.error({
        message: getLang('SAVE_VALIDATE'),
      });
      return false;
    }
    if (isEmpty(wsdlList)) {
      const confirmResult = await Modal.confirm({
        okText: getLang('CONTINUE'),
        cancelText: getLang('BACK'),
        children: <div>{getLang('IMPORT_WARNING')}</div>,
      });
      if (confirmResult === 'cancel') {
        return false;
      }
    }
    this.basicDS.current.set('otherWsdlInfo', omit(reaParams, ['flatWsdlInterfaceServerList']));
    this.basicDS.current.set('wsdlInterfaceServerList', wsdlList);
    return this.basicDS.submit().then((res) => {
      if (res && res.success) {
        openDetail(res.content[0].interfaceServerId);
      }
    });
  }

  @Bind()
  handleFile(files) {
    if (window.FileReader) {
      const reader = new FileReader();
      reader.readAsDataURL(files[0]);
      reader.onload = () => {
        if (reader.result) {
          this.basicDS.current.set('importUrl', files[0].name);
          this.setState({
            files,
          });
        }
      };
    }
  }

  @Bind()
  loadWsdl() {
    const { files } = this.state;
    this.basicDS.current
      .getField('importUrl')
      .checkValidity()
      .then((validity) => {
        if (validity) {
          this.setState({ loading: true }, () => {
            const { importUrl: wsdl, serverName, serverCode } = this.basicDS.current.toData();
            if (files.length !== 0 && wsdl === files[0].name) {
              const formData = new FormData();
              formData.append('wsdlFile', files[0]);
              if (serverCode) {
                formData.append('serverCode', serverCode);
              }
              if (serverName) {
                formData.append('serverName', serverName);
              }
              loadWsdlFile({ formData }).then((res) => {
                this.handleWsdlResponse(res);
              });
            } else {
              const { username, password } = this.advanceDS.current.toData();
              loadWsdl({
                importUrl: wsdl,
                username,
                password,
                serverName,
                serverCode,
                soapFlag: true,
              }).then((res) => {
                this.handleWsdlResponse(res);
              });
            }
          });
        }
      });
  }

  /**
   * 处理加载wsdl返回的结果
   */
  @Bind()
  handleWsdlResponse(res) {
    if (getResponse(res)) {
      const { serverName, serverCode, flatWsdlInterfaceServerList = [] } = res;
      this.basicDS.current.set('serverName', serverName);
      this.basicDS.current.set('serverCode', serverCode);
      const temps = flatWsdlInterfaceServerList.map((item) => {
        if (!item.interfaceCode) {
          return {
            ...item,
            id: item.serverCode,
            name: item.serverName,
          };
        } else {
          return {
            ...item,
            id: item.interfaceCode + item.serverCode,
            name: item.interfaceName,
            parentId: item.serverCode,
          };
        }
      });
      this.treeDS.loadData(temps);

      this.setState({
        reaParams: res,
        interfaceList: temps,
        loading: false,
      });
    } else {
      this.setState({ loading: false });
    }
  }

  @Bind()
  nodeRenderer({ record }) {
    return record.get('name');
  }

  @Bind()
  handleCheck(data) {
    const { interfaceList } = this.state;
    let wsdlInterfaceList = [];
    const parentList = interfaceList.filter((item) => !item.interfaceCode);
    wsdlInterfaceList = parentList.map((wsdl) => {
      const wsdlList = [];
      interfaceList.forEach((item) => {
        if (
          (data.includes(item.id) || data.includes(wsdl.id)) &&
          item.interfaceCode !== undefined
        ) {
          if (wsdl.serverCode === item.serverCode) {
            wsdlList.push(item);
          }
        }
      });
      return {
        ...wsdl,
        wsdlInterfaceList: wsdlList,
      };
    });
    wsdlInterfaceList = wsdlInterfaceList.filter((item) => !isEmpty(item.wsdlInterfaceList));
    if (isEmpty(data)) {
      wsdlInterfaceList = [];
    }
    this.setState({
      wsdlList: [...wsdlInterfaceList],
    });
  }

  render() {
    const { loading, interfaceList } = this.state;
    return (
      <Collapse defaultActiveKey={['1', '3']}>
        <Panel header={getLang('BASIC_CONFIG')} key="1">
          <Form dataSet={this.basicDS} columns={4}>
            {!isTenantRoleLevel() && <Lov name="tenantLov" colSpan={4} />}
            <div colSpan={4} name="importUrl" style={{ display: 'flex' }}>
              <TextField
                style={{ width: 300, marginRight: 17 }}
                name="importUrl"
                placeholder={getLang('WSDL_PLACEHOLDER')}
              />
              <ReactFileReader fileTypes={['*']} handleFiles={this.handleFile}>
                <Button color="gray">
                  <Icon type="folder-o" style={{ fontSize: 12, marginRight: 8, marginBottom: 3 }} />
                  {getLang('PIC_FILES')}
                </Button>
              </ReactFileReader>
              <Button
                style={{ marginLeft: 10 }}
                color="primary"
                onClick={this.loadWsdl}
                loading={loading}
              >
                {getLang('LOAD_WSDL')}
              </Button>
            </div>
            <TextField name="serverName" colSpan={4} placeholder={getLang('TEXT_PLACEHOLDER')} />
            <TextField name="serverCode" colSpan={4} placeholder={getLang('TEXT_PLACEHOLDER')} />
            <Select name="publicFlag" colSpan={4} placeholder={getLang('IMPORT_PLACEHOLDER')} />
          </Form>
        </Panel>
        <Panel header={getLang('ADVANCE_CONFIG')} key="2">
          <Form columns={4} dataSet={this.advanceDS}>
            <TextField name="username" colSpan={4} />
            <Password name="password" colSpan={4} />
          </Form>
        </Panel>
        <Panel header={getLang('INTERFACE')} key="3">
          {interfaceList.length !== 0 && (
            <Tree
              dataSet={this.treeDS}
              checkable
              renderer={this.nodeRenderer}
              onCheck={this.handleCheck}
            />
          )}
        </Panel>
      </Collapse>
    );
  }
}
