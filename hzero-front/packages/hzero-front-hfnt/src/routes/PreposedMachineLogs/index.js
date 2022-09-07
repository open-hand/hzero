import React from 'react';
import { Header, Content } from 'components/Page';
import { Table, Modal, DataSet, TextArea } from 'choerodon-ui/pro';
import JsonArea from 'react-json-view';
import formatterCollections from 'utils/intl/formatterCollections';
import { tableDS } from '@/stores/PreposedMachineLogs/PreposedMachineLogsDS';

import FONTAL_LOGS_LANG from '@/langs/frontalLogsLang';

@formatterCollections({ code: ['hfnt.frontalLogs'] })
export default class FrontalLogs extends React.Component {
  modal;

  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS);
  }

  async handleRetry(record) {
    const { frontalLogId } = record.toData();
    // const url = `${HZERO_HFNT}/v1${level}/frontal-logs/retry?frontalLogId=${frontalLogId}`;
    // await axios.get(url);
    this.tableDS.setQueryParameter('retry', true);
    this.tableDS.setQueryParameter('frontalLogId', frontalLogId);
    await this.tableDS.query();
    this.tableDS.query();
  }

  handleParse(response) {
    let data = '';
    try {
      data = JSON.parse(response || '{}');
    } catch (error) {
      data = { error: FONTAL_LOGS_LANG.TRANSLATE_ERROR };
    }
    return data;
  }

  openMethodParamValueModal(paramText) {
    const commonSetting = {
      name: null,
      displayDataTypes: false,
    };
    const methodParamValue = {
      ...commonSetting,
      src: this.handleParse(paramText),
    };
    Modal.open({
      key: Modal.key(),
      title: FONTAL_LOGS_LANG.METHOD_VALUE,
      children: <JsonArea {...methodParamValue} />,
      closable: true,
    });
  }

  openErrorStackModal(errorStack) {
    Modal.open({
      key: Modal.key(),
      title: FONTAL_LOGS_LANG.ERROR_STACK,
      children: <TextArea value={errorStack} cols={300} rows={25} />,
      closable: true,
    });
  }

  get logColumns() {
    return [
      {
        name: 'creationDate',
        width: 200,
      },
      {
        name: 'cacheDate',
        width: 180,
      },
      {
        name: 'sourceType',
        width: 150,
      },
      {
        name: 'cacheFolder',
        width: 200,
      },
      {
        header: FONTAL_LOGS_LANG.STATUS_CODE,
        name: 'statusDesc',
        width: 150,
      },
      {
        name: 'tenantName',
        width: 180,
      },
      {
        name: 'frontalCode',
        width: 240,
      },
      {
        name: 'frontalName',
        width: 240,
      },
      {
        name: 'className',
        width: 240,
      },
      {
        name: 'methodName',
        width: 240,
      },
      {
        name: 'paramText',
        width: 200,
        renderer: ({ value }) => (
          <a onClick={() => this.openMethodParamValueModal(value)}>{value}</a>
        ),
      },
      {
        name: 'errorStack',
        width: 240,
        renderer: ({ value }) => <a onClick={() => this.openErrorStackModal(value)}>{value}</a>,
      },
      {
        header: FONTAL_LOGS_LANG.OPERATION,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => (
          <a onClick={() => this.handleRetry(record)}>{FONTAL_LOGS_LANG.RETRY}</a>
        ),
      },
    ];
  }

  render() {
    return (
      <>
        <Header title={FONTAL_LOGS_LANG.HEADER} />
        <Content>
          <Table dataSet={this.tableDS} columns={this.logColumns} />
        </Content>
      </>
    );
  }
}
