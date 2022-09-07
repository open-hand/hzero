/**
 * 告警规则配置 - 新建/编辑侧滑
 * @date: 2020-5-25
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Form, TextField, Switch, TextArea, Select } from 'choerodon-ui/pro';

const Drawer = (props) => {
  const { drawerDs, isEdit } = props;

  return (
    <Form dataSet={drawerDs}>
      <TextField name="alertCode" disabled={isEdit} />
      <TextField name="alertName" />
      <Select name="alertLevel" />
      <TextArea name="remark" colSpan={2} />
      <Switch name="enabledFlag" />
    </Form>
  );
};

export default Drawer;
