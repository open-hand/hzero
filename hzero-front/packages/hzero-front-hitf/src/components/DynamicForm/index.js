/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/3/23
 * @copyright HAND ® 2020
 */
import React, { useCallback, useEffect, useState } from 'react';
import { DataSet, Form, TextField, Select, Lov, Password, Spin } from 'choerodon-ui/pro';
import { isEmpty } from 'lodash';
import { HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import uuid from 'uuid/v4';
import QuestionPopover from '@/components/QuestionPopover';

const DynamicForm = (props) => {
  const { onRef, onGetEmpty, formCode, formData, isNew, ...otherProps } = props;
  const organizationId = getCurrentOrganizationId();
  const [formConfigDS, setFormConfigDS] = useState(new DataSet());
  const [formConfigDetailDS, setFormConfigDetailDS] = useState(new DataSet());
  const [formConfigList, setFormConfigList] = useState([]);

  useEffect(() => {
    if (!isEmpty(formCode)) {
      fetchFormConfig(formCode);
    }
  }, [formCode]);

  useEffect(() => {
    if (!isEmpty(formData)) {
      formConfigDetailDS.loadData([formData]);
    } else {
      formConfigDetailDS.create();
    }
  }, [formConfigDetailDS]);

  const fetchFormConfig = useCallback(async (code) => {
    const level = isTenantRoleLevel() ? `/${organizationId}` : '';
    const configDS = new DataSet({
      paging: false,
      autoQuery: false,
      autoCreate: false,
      transport: {
        read: ({ data, params }) => {
          return {
            url: `${HZERO_PLATFORM}/v1${level}/form-lines/header-code`,
            method: 'GET',
            params: {
              ...params,
              ...data,
            },
          };
        },
      },
      queryParameter: { formCode: code },
      feedback: {
        loadSuccess: (data) => {
          if (Array.isArray(data)) {
            onGetEmpty(isEmpty(data));
            const detailDS = new DataSet();
            data
              .sort((item1, item2) => item1.orderSeq - item2.orderSeq)
              .forEach((item) => {
                const {
                  itemCode,
                  itemName,
                  requiredFlag,
                  itemTypeCode,
                  valueSet,
                  valueField,
                  displayField,
                  updatableFlag,
                  defaultValue,
                } = item;
                let field = {
                  defaultValue,
                  name: itemCode,
                  label: itemName,
                  type: 'string',
                  required: requiredFlag === 1,
                  readOnly: updatableFlag !== 1,
                };
                // 独立值集
                if (itemTypeCode === 'LOV' || itemTypeCode === 'MULTI_LOV') {
                  field = {
                    ...field,
                    lookupCode: valueSet,
                    multiple: itemTypeCode === 'MULTI_LOV',
                  };
                }
                // 值集视图
                if (itemTypeCode === 'LOV_VIEW' || itemTypeCode === 'MULTI_LOV_VIEW') {
                  field = {
                    ...field,
                    valueField,
                    displayField,
                    type: 'object',
                    lovCode: valueSet,
                    multiple: itemTypeCode === 'MULTI_LOV_VIEW',
                  };
                }
                if (itemTypeCode === 'PASSWORD') {
                  field = {
                    ...field,
                    required: requiredFlag === 1 && isNew,
                  };
                }
                detailDS.addField(itemCode, field);
              });
            setFormConfigDetailDS(detailDS);
            setFormConfigList(data);
          }
        },
      },
    });
    setFormConfigDS(configDS);
    await configDS.query();
  });

  /**
   * 表单渲染
   */
  const renderForm = (data) => {
    const { itemCode, itemTypeCode, itemDescription, itemName } = data;
    switch (itemTypeCode) {
      case 'TEXT':
        return (
          <TextField
            key={uuid()}
            name={itemCode}
            label={itemDescription && <QuestionPopover text={itemName} message={itemDescription} />}
          />
        );
      case 'LOV':
      case 'MULTI_LOV':
        return (
          <Select
            key={uuid()}
            name={itemCode}
            label={itemDescription && <QuestionPopover text={itemName} message={itemDescription} />}
          />
        );
      case 'LOV_VIEW':
      case 'MULTI_LOV_VIEW':
        return (
          <Lov
            key={uuid()}
            name={itemCode}
            label={itemDescription && <QuestionPopover text={itemName} message={itemDescription} />}
          />
        );
      case 'PASSWORD':
        return (
          <Password
            key={uuid()}
            name={itemCode}
            restrict="a-zA-Z0-9-_./"
            placeholder={
              !isEmpty(formData) && intl.get('hzero.common.validation.notChange').d('未更改')
            }
            label={itemDescription && <QuestionPopover text={itemName} message={itemDescription} />}
          />
        );
      default:
        return (
          <TextField
            key={uuid()}
            name={itemCode}
            label={itemDescription && <QuestionPopover text={itemName} message={itemDescription} />}
          />
        );
    }
  };

  /**
   * 保存
   */
  const handleSave = async () => {
    if (!formConfigDetailDS.current) {
      return {};
    }
    const formConfigValidate = await formConfigDetailDS.validate();
    if (!formConfigValidate) {
      return undefined;
    }
    const { __dirty, ...otherData } = formConfigDetailDS.current.toData();
    return otherData;
  };
  onRef(handleSave);

  return (
    <Spin dataSet={formConfigDS}>
      <Form {...otherProps} dataSet={formConfigDetailDS}>
        {formConfigList.map((config) => renderForm(config))}
      </Form>
    </Spin>
  );
};
export default DynamicForm;
