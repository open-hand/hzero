import React, { useState, useEffect } from 'react';
import { Tabs, Spin } from 'choerodon-ui';
import { Icon, Table, Form, Output, DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
// import YAMLFormatter from 'choerodon-ui/pro/lib/code-area/formatters/YAMLFormatter';
import 'choerodon-ui/pro/lib/code-area/lint/yaml';

import {
  detailDS as detailDs,
  tableDS as tableDs,
  codeDS as codeDs,
} from '../../stores/InstanceDS';
// import AceEditor from '../../components/yamlAce';
import emptyApi from '../../assets/icons/noright.svg';
import './detail.less';

const { TabPane } = Tabs;
const { Column } = Table;

// 处理 codemirror 的SSR问题， 如无需SSR，请用import代替require;
if (typeof window !== 'undefined') {
  // 提供对应语言的语法高亮
  // eslint-disable-next-line global-require
  require('codemirror/mode/yaml/yaml');
}

const Detail = (props) => {
  const { id: instanceId, isRefresh } = props;

  const detailDS = React.useMemo(() => new DataSet(detailDs()), []);

  const tableDS = React.useMemo(() => new DataSet(tableDs()), []);

  const codeDS = React.useMemo(() => new DataSet(codeDs()), []);

  // const configInfo = (detailDS.current && detailDS.current.get('configInfoYml')) || {};
  // const envInfo = (detailDS.current && detailDS.current.get('envInfoYml')) || {};
  const [loading, setLoading] = useState(true);
  async function loadData() {
    setLoading(true);
    detailDS.setQueryParameter('instanceId', instanceId);
    await detailDS.query().then((res) => {
      if (res) {
        let { metadata } = res;
        const { configInfoYml, envInfoYml } = res;
        metadata = Object.keys(metadata).map((key) => ({
          key,
          value: metadata[key],
        }));
        tableDS.loadData(metadata);
        codeDS.loadData([
          {
            configInfo: configInfoYml.yaml,
            envInfo: envInfoYml.yaml,
          },
        ]);
      }
    });
    setLoading(false);
  }
  useEffect(() => {
    if (instanceId) {
      loadData();
    }
  }, [instanceId]);

  function getInstanceInfo() {
    return (
      <div className="instanceInfoContainer">
        <div className="instanceInfo">
          <Form labelLayout="horizontal" labelAlign="left" dataSet={detailDS}>
            <Output name="instanceId" />
            <Output name="hostName" />
            <Output name="ipAddr" />
            <Output name="app" />
            <Output name="port" />
            <Output name="version" />
            <Output name="registrationTime" />
            <Output name="metadata" renderer={() => ''} />
          </Form>
        </div>
        <Table dataSet={tableDS} queryBar="none">
          <Column name="key" />
          <Column name="value" />
        </Table>
      </div>
    );
  }

  // function getConfigInfo() {
  //   return (
  //     <div className="configContainer">
  //       <div>
  //         <p>{intl.get('hadm.instance.view.message.title.config').d('配置信息')}</p>
  //         {/* <AceEditor readOnly="nocursor" value={configInfo.yaml} /> */}
  //         <CodeArea
  //           dataSet={codeDS}
  //           name="configInfo"
  //           style={{ height: '100%' }}
  //           formatter={YAMLFormatter}
  //           disabled
  //         />
  //       </div>
  //       <div>
  //         <p>{intl.get('hadm.instance.view.message.title.env').d('环境信息')}</p>
  //         {/* <AceEditor readOnly="nocursor" value={envInfo.yaml} /> */}
  //         <CodeArea
  //           dataSet={codeDS}
  //           name="envInfo"
  //           style={{ height: '100%' }}
  //           formatter={YAMLFormatter}
  //           disabled
  //         />
  //       </div>
  //     </div>
  //   );
  // }

  function getEmpty() {
    const rightContent = (
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          height: 250,
          margin: '88px auto',
          padding: '50px 75px',
          width: '7rem',
        }}
      >
        <img src={emptyApi} alt="" />
        <div style={{ marginLeft: 40 }}>
          <div style={{ fontSize: '14px', color: 'rgba(0,0,0,0.65)' }}>
            {intl.get('hadm.instance.view.message.title.unSelect').d('检测到您未选择任何实例')}
          </div>
          <div style={{ fontSize: '20px', marginTop: 10 }}>
            {intl
              .get('hadm.instance.view.message.title.select')
              .d('请在左侧树状图中选择您要查看的实例')}
          </div>
        </div>
      </div>
    );
    return rightContent;
  }

  if (instanceId === null || isRefresh) {
    return getEmpty();
  } else {
    return loading ? (
      <Spin size="large" style={{ paddingTop: 242, margin: '0 auto', width: '100%' }} />
    ) : (
      <>
        <div className="instanceTitle">
          <h2 style={{ marginTop: 20, marginLeft: 15 }}>
            <Icon type="instance_outline" />
            {instanceId}
          </h2>
        </div>
        <Tabs>
          <TabPane
            tab={intl.get('hadm.instance.view.message.title.instanceInfo').d('实例信息')}
            key="instanceinfo"
          >
            {getInstanceInfo()}
          </TabPane>
          {/* <TabPane
            tab={intl.get('hadm.instance.view.message.title.configInfo').d('配置环境信息')}
            key="configenvInfo"
          >
            {getConfigInfo()}
          </TabPane> */}
        </Tabs>
      </>
    );
  }
};

export default Detail;
