import React from 'react';

import { Content, Header } from 'components/Page';
import {
  Button,
  Form,
  Lov,
  DataSet,
  TextField,
  DatePicker,
  Select,
  CheckBox,
  Tooltip,
  ModalContainer,
} from 'choerodon-ui/pro';
import axios from 'axios';
import queryString from 'query-string';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { isTenantRoleLevel, getCurrentOrganizationId, getAccessToken } from 'utils/utils';
import { HZERO_RPT, API_HOST } from 'utils/config';
import Icons from 'components/Icons';

import PrintElement from '@/components/PrintElement';
import { PrintDS as printDs } from '@/stores/labelTemplateDS';
import styles from './index.less';

const Print = (props) => {
  const { location = {} } = props;
  const { search = {} } = location;
  const { tenantId } = queryString.parse(search);
  const { Option } = Select;

  const printDomRef = React.useRef(null);

  const PrintDS = React.useMemo(() => new DataSet(printDs()), []);

  const [formData, setFormData] = React.useState([]);

  const [label, setLabel] = React.useState({});

  const [visible, setVisible] = React.useState(false);

  // const [printLoading, setPrintLoading] = React.useState(false);

  const [viewLoading, setViewLoading] = React.useState(false);

  React.useEffect(() => {
    const {
      match: {
        params: { templateCode },
      },
    } = props;
    // FIXME: 为啥用query不行
    // PrintDS.setQueryParameter('templateCode', templateCode);
    // await PrintDS.query();
    // const axios = getConfig('axios');
    axios({
      url: isTenantRoleLevel()
        ? `${HZERO_RPT}/v1/${getCurrentOrganizationId()}/label-prints/meta?labelTemplateCode=${templateCode}`
        : `${HZERO_RPT}/v1/label-prints/meta?labelTemplateCode=${templateCode}&labelTenantId=${tenantId}`,
      method: 'GET',
    })
      .then((res) => {
        if (res) {
          setFormData(res && res.formElements);
          if (res && res.formElements) {
            res.formElements.forEach((item) => {
              PrintDS.addField(item.name, {
                label: item.text,
                required: item.isRequired,
                type: item.dataType,
                lovCode: item.valueSource,
                lookupCode: item.valueSource,
                width: item.width,
              });
            });
            PrintDS.create({});
          }
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }, []);

  const handleView = () => {
    const {
      match: {
        params: { templateCode },
      },
    } = props;
    setViewLoading(true);
    axios({
      url: isTenantRoleLevel()
        ? `${HZERO_RPT}/v1/${getCurrentOrganizationId()}/label-prints/view/html?labelTemplateCode=${templateCode}`
        : `${HZERO_RPT}/v1/label-prints/view/html?labelTemplateCode=${templateCode}&labelTenantId=${tenantId}`,
      method: 'GET',
      params: PrintDS.toData()[0],
      baseURL: `${API_HOST}`,
      headers: { Authorization: `bearer ${getAccessToken()}` },
    })
      .then((res) => {
        if (res) {
          setLabel(res.label);
          setVisible(true);
          setViewLoading(false);
        }
      })
      .catch((err) => {
        setViewLoading(false);
        notification.error({
          message: err.message,
        });
      });
  };

  return (
    <>
      <Header
        title={intl.get('hrpt.labelTemplate.view.message.title.print').d('标签打印')}
        backPath="/hrpt/label-template/list"
      >
        <Button loading={viewLoading} color="primary" onClick={handleView} icon="sync">
          {intl.get('hrpt.labelTemplate.view.message.title.view').d('标签渲染')}
        </Button>
        {visible && (
          <Tooltip
            placement="top"
            title={intl.get('hrpt.labelTemplate.view.message.title.print').d('标签打印')}
          >
            <Icons
              type="dayin"
              size={30}
              style={{ color: '#0303ab', cursor: 'pointer', marginRight: 6 }}
              onClick={() => {
                PrintElement({
                  content: printDomRef.current,
                });
              }}
            />
          </Tooltip>
        )}
      </Header>
      <Content>
        {formData && formData.length !== 0 && (
          <div className={styles['model-title']}>
            {intl.get('hrpt.labelTemplate.view.message.labelParams').d('标签参数')}
          </div>
        )}
        <Form dataSet={PrintDS} columns={3} labelWidth={140}>
          {formData &&
            formData.map((item) => {
              switch (item.type) {
                case 'Lov':
                  return (
                    <Lov name={item.name} style={{ height: item.height, width: item.width }} />
                  );
                case 'Select':
                  return (
                    <Select name={item.name} style={{ height: item.height, width: item.width }}>
                      {item.value.map((i) => {
                        return <Option value={i.value}>{i.meaning}</Option>;
                      })}
                    </Select>
                  );
                case 'Checkbox':
                  return (
                    <CheckBox name={item.name} style={{ height: item.height, width: item.width }} />
                  );
                case 'DatetimePicker':
                  return (
                    <DatePicker
                      name={item.name}
                      style={{ height: item.height, width: item.width }}
                    />
                  );
                default:
                  return (
                    <TextField
                      name={item.name}
                      style={{ height: item.height, width: item.width }}
                    />
                  );
              }
            })}
        </Form>
        {visible && (
          <div className={styles['model-title']}>
            {intl.get('hrpt.reportQuery.view.message.buildResult').d('生成结果')}
          </div>
        )}
        {visible && (
          <div
            style={{ paddingBottom: 20, overflow: 'hidden' }}
            ref={printDomRef}
            dangerouslySetInnerHTML={{ __html: label }}
          />
        )}
        <ModalContainer location={props.location} />
      </Content>
    </>
  );
};

export default Print;
