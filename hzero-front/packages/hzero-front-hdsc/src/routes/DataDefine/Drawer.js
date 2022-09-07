import React, { useState, useMemo, useEffect } from 'react';
import { Form, TextField, Table, Select, Password } from 'choerodon-ui/pro';

const Drawer = (props) => {
  const [showFlag, setShowFlag] = useState(false);

  const { configDs, isEdit, drawerDs, streamId } = props;

  useEffect(() => {
    if (isEdit) {
      drawerDs.setQueryParameter('streamId', streamId);
      drawerDs.query().then((res) => {
        handleChange('', res.configValue);
      });
    } else {
      drawerDs.create({}, 0);
    }
  }, []);

  const columns = useMemo(
    () => [
      { name: 'key', width: 200, align: 'left' },
      {
        name: 'value',
        editor: (record) => {
          if (record.get('key') === 'password') {
            return <Password />;
          }
          return record.get('key') !== 'type';
        },
        renderer: ({ text, record }) => {
          if (record.get('key') === 'password') {
            return text.replace(/[\S\s]/g, '·');
          }
          return text;
        },
      },
    ],
    []
  );

  const handleChange = (configValue, initValue) => {
    if (configValue || initValue) {
      let str = '{}';
      str = initValue || drawerDs.getField('streamTypeCode').getLookupData(configValue).description;
      try {
        const obj = JSON.parse(str);
        const data = Object.keys(obj).map((key) => {
          return { key, value: obj[key] };
        });
        configDs.loadData(data);
        setShowFlag(true);
      } catch {
        // notification
      }
    } else {
      setShowFlag(false);
      configDs.loadData([]);
    }
  };

  return (
    <>
      <Form dataSet={drawerDs} labelWidth={120}>
        <TextField name="streamCode" maxLength={60} disabled={isEdit} />
        <Select name="streamPurposeCode" clearButton={false} />
        <Select
          name="streamTypeCode"
          onChange={(v) => {
            handleChange(v, undefined);
          }}
        />
        {showFlag && <Table dataSet={configDs} columns={columns} name="configValue" />}
      </Form>
    </>
  );
};

export default Drawer;
