import React, { useState } from 'react';
import {
  Form,
  Spin,
  Select,
  Switch,
  IntlField,
  TextField,
  Password,
  NumberField,
} from 'choerodon-ui/pro';
import { sortBy } from 'lodash';

import intl from 'utils/intl';

const Drawer = (props) => {
  const codeObj = {
    KAFKA: 'HIOT.DATA_SINK.KAFKA	',
    ROCKET_MQ: 'HIOT.DATA_SINK.ROCKET_MQ',
    BEAN: 'HIOT.DATA_SINK.BEAN',
    REST_TEMPLATE: 'HIOT.DATA_SINK.REST_TEMPLATE',
  };

  const { drawerDs, isEdit, currentEditData, formConfigDs } = props;
  const [formList, setFormList] = useState([]);

  React.useEffect(() => {
    if (isEdit) {
      queryData();
    }
  }, []);

  const queryData = async () => {
    try {
      const { dataSinkId } = currentEditData;
      drawerDs.setQueryParameter('dataSinkId', dataSinkId);
      await drawerDs.query().then((res) => {
        const { dataSinkTypeCode } = res;
        if (dataSinkTypeCode) {
          const code = codeObj[dataSinkTypeCode];
          queryFormList(code);
        }
      });
    } catch (e) {
      //
    }
  };

  const onChange = (value) => {
    if (value) {
      const code = codeObj[value];
      queryFormList(code);
    } else {
      formConfigDs.setQueryParameter('formCode', '');
      setFormList([]);
    }
  };

  const queryFormList = (code) => {
    formConfigDs.setQueryParameter('formCode', code);
    formConfigDs.query().then((res) => {
      const data = Array.isArray(res) ? res : [];
      const newData = sortBy(data, 'orderSeq');
      data.forEach((item) => {
        let field = {
          name: item.itemCode,
          label: item.itemName,
          type: 'string',
          required: item.requiredFlag === 1,
        };
        if (item.itemCode === 'serverAddress') {
          field = {
            ...field,
            // eslint-disable-next-line no-useless-escape
            pattern: /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\:([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/,
            multiple: ',',
            defaultValidationMessages: {
              patternMismatch: intl
                .get(`hiot.dataDestination.view.message.serverAddressMsg`)
                .d('请输入正确服务器地址，如：X.X.X.X:XXXX'),
            },
          };
        }
        if (drawerDs) {
          drawerDs.addField(item.itemCode, field);
        }
      });
      setFormList(newData);
    });
  };

  return (
    <>
      <Spin dataSet={drawerDs}>
        <Form dataSet={drawerDs} labelWidth={110}>
          <TextField name="dataSinkCode" disabled={isEdit} />
          <IntlField name="dataSinkName" />
          <Select name="dataSinkTypeCode" disabled={isEdit} onChange={onChange} />
          {formList.map((item) => {
            switch (item.itemTypeCode) {
              case 'PASSWORD':
                return (
                  <Password
                    name={item.itemCode}
                    label={item.itemName}
                    placeholder={
                      isEdit ? intl.get('hzero.common.validation.notChange').d('未更改') : ''
                    }
                  />
                );
              case 'NUMBER':
                return <NumberField name={item.itemCode} label={item.itemName} />;
              default:
                return <TextField name={item.itemCode} label={item.itemName} />;
            }
          })}
          <IntlField name="remarks" />
          <Switch name="enabledFlag" />
        </Form>
      </Spin>
    </>
  );
};

export default Drawer;
