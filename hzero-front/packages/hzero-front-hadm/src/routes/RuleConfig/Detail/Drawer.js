import React, { useState, useEffect } from 'react';
import { Form, TextField, NumberField, Select, Output } from 'choerodon-ui/pro';

import styles from './index.less';

const Drawer = (props) => {
  const { ds } = props;
  const [showFlag, setShowFlag] = useState(ds.get('type'));
  const [libraryDisabled, setLibraryDisabled] = useState(true);
  const [tableDisabled, setTableDisabled] = useState(true);
  const [numLibraryFlag, setNumLibraryFlag] = useState(
    ds.get('subLibraryRuleType') === 'HADM.SHARDING_RULE_INLINE'
  );
  const [numTableFlag, setNumTableFlag] = useState(
    ds.get('subTableRuleType') === 'HADM.SHARDING_RULE_INLINE'
  );

  useEffect(() => {
    const subTableRuleType = ds.get('subTableRuleType');
    const subLibraryRuleType = ds.get('subLibraryRuleType');
    if (subLibraryRuleType && subLibraryRuleType !== 'HADM.SHARDING_RULE_NONE') {
      ds.getField('dbShardingTemplate').set('lookupCode', subLibraryRuleType);
      setLibraryDisabled(false);
    }
    if (subTableRuleType && subTableRuleType !== 'HADM.SHARDING_RULE_NONE') {
      ds.getField('tableShardingTemplate').set('lookupCode', subTableRuleType);
      setTableDisabled(false);
    }
  }, []);

  const handleChangeSubLibraryRuleType = (value) => {
    setLibraryDisabled(false);
    setNumLibraryFlag(false);
    ds.set('dbShardingTemplate', '');
    if (value === 'HADM.SHARDING_RULE_NONE' || !value) {
      setLibraryDisabled(true);
    } else if (value === 'HADM.SHARDING_RULE_INLINE') {
      setNumLibraryFlag(true);
      ds.getField('dbShardingTemplate').set('lookupCode', value);
    } else if (value) {
      ds.getField('dbShardingTemplate').set('lookupCode', value);
    }
  };

  const handleChangeSubTableRuleType = (value) => {
    setTableDisabled(false);
    setNumTableFlag(false);
    ds.set('tableShardingTemplate', '');
    if (value === 'HADM.SHARDING_RULE_NONE' || !value) {
      setTableDisabled(true);
    } else if (value === 'HADM.SHARDING_RULE_INLINE') {
      setNumTableFlag(true);
      ds.getField('tableShardingTemplate').set('lookupCode', value);
    } else if (value) {
      ds.getField('tableShardingTemplate').set('lookupCode', value);
    }
  };

  return (
    <Form record={ds} labelWidth={120}>
      <TextField name="tableName" maxLength={30} />
      <Select
        name="type"
        onChange={(e) => {
          setShowFlag(e);
        }}
        allowClear={false}
      />
      {showFlag !== '1' && (
        <Select
          name="subLibraryRuleType"
          onChange={handleChangeSubLibraryRuleType}
          allowClear={false}
        />
      )}
      {numLibraryFlag && showFlag !== '1' && <NumberField min={2} step={1} name="subLibraryNum" />}
      {ds.get('subLibraryRuleType') !== 'HADM.SHARDING_RULE_NONE' &&
        ds.get('subLibraryRuleType') &&
        showFlag !== '1' && (
          <TextField name="subLibraryField" maxLength={50} className={styles['input-item-style']} />
        )}
      {ds.get('subLibraryRuleType') !== 'HADM.SHARDING_RULE_NONE' &&
        ds.get('subLibraryRuleType') &&
        showFlag !== '1' && <Select name="dbShardingTemplate" disabled={libraryDisabled} />}
      {showFlag !== '2' && (
        <Select
          name="subTableRuleType"
          onChange={handleChangeSubTableRuleType}
          allowClear={false}
        />
      )}
      {numTableFlag && ds.get('subTableRuleType') && showFlag !== '2' && (
        <NumberField name="subTableNum" min={2} />
      )}
      {ds.get('subTableRuleType') !== 'HADM.SHARDING_RULE_NONE' &&
        ds.get('subTableRuleType') &&
        showFlag !== '2' && (
          <TextField name="subTableField" maxLength={50} className={styles['input-item-style']} />
        )}

      {ds.get('subTableRuleType') !== 'HADM.SHARDING_RULE_NONE' &&
        ds.get('subTableRuleType') &&
        showFlag !== '2' && <Select name="tableShardingTemplate" disabled={tableDisabled} />}
      <Select name="keyGeneratorStrategy" />
      <TextField name="keyGeneratorField" maxLength={50} />
      {ds.get('bindingTables') && <Output name="bindingTables" />}
    </Form>
  );
};

export default Drawer;
