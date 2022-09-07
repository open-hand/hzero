/**
 * 打印配置
 * FIXME: 直接查询了 值集, 有什么更好的方法
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/17
 * @copyright HAND ® 2019
 */

import React from 'react';
import { Card } from 'choerodon-ui';
import { DataSet, Form, NumberField, Select, SelectBox, Spin, Switch } from 'choerodon-ui/pro';
import { isUndefined } from 'lodash';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { isTenantRoleLevel, getCurrentOrganizationId, getResponse } from 'utils/utils';

import { queryUnifyIdpValue } from 'hzero-front/lib/services/api';

import { labelTemplatePrintSettingsConfig } from '@/stores/labelTemplateDS';

import styles from './styles.less';

const PrintSettings = ({ labelTemplate, modal, tenantId }) => {
  const [loading, setLoading] = React.useState(true);
  const labelPrintDS = React.useMemo(() => {
    const datasetConfig = labelTemplatePrintSettingsConfig(labelTemplate);
    if (!isTenantRoleLevel()) {
      datasetConfig.queryParameter = {
        labelTenantId: tenantId,
      };
    }
    return new DataSet(datasetConfig);
  }, [labelTemplate]);
  const [paperSizeMap, setPaperSizeMap] = React.useState({});
  React.useEffect(() => {
    // 查询值集
    setLoading(true);
    const ready = () => {
      setLoading(false);
    };
    queryUnifyIdpValue('HRPT.LABEL.PAPER_SIZE')
      .then((res) => {
        const p = getResponse(res);
        if (p) {
          const pMap = {};
          p.forEach((item) => {
            if (!pMap[item.parentValue]) {
              pMap[item.parentValue] = {};
            }
            pMap[item.parentValue][item.tag] = +item.value;
          });
          setPaperSizeMap(pMap);
        }
      })
      .then(ready);
  }, [labelPrintDS, setLoading]);
  React.useEffect(() => {
    setLoading(true);
    const ready = () => {
      labelPrintDS.status = 'ready';
      setLoading(false);
    };
    labelPrintDS
      .query()
      .then((res) => {
        if (isUndefined(res)) {
          // create
          labelPrintDS.loadData([
            {
              status: 'create',
              labelTemplateCode: labelTemplate.templateCode,
              tenantId: getCurrentOrganizationId(),
            },
          ]);
        } else {
          // edit
          labelPrintDS.loadData([
            {
              ...res,
              _paperNoLimit: !res.paperHigh, // 没有高度, 则是高度无限制状态
            },
          ]);
        }
      })
      .then(ready);
    const handleOk = async () => {
      // 手动校验 其他 数据 (标签数量)
      function add(...adds) {
        return adds.reduce((n, c) => {
          if (isNaN(+c)) {
            throw new Error('some add is not valid');
          } else {
            return n + +c;
          }
        }, 0);
      }

      // 校验宽
      function validateWidth(data) {
        try {
          const w = add(
            (data.wideQty || 1) * labelTemplate.templateWidth,
            data.marginLeft || 0,
            data.marginRight || 0
          );
          return w <= (data.printDirection ? data.paperWidth : data.paperHigh);
        } catch (e) {
          // return false;
        }
        return false;
      }

      // 校验高
      function validateHeight(data) {
        if (data._paperNoLimit) {
          return true;
        }
        try {
          const h = add(
            (data.highQty || 1) * labelTemplate.templateHigh,
            (data.highSpace || 0) * ((data.highQty || 1) - 1),
            data.marginTop || 0,
            data.marginBottom || 0
          );
          return h <= (data.printDirection ? data.paperHigh : data.paperWidth);
        } catch (e) {
          // return false;
        }
        return false;
      }

      const valid = await labelPrintDS.validate();
      if (valid) {
        const data = labelPrintDS.current.toJSONData();
        const errorMessages = [];
        if (!validateWidth(data)) {
          errorMessages.push(
            intl.get('hrpt.labelTemplate.view.validation.wideQty').d('打印区域超过纸张宽度')
          );
          notification.error({
            message: intl
              .get('hrpt.labelTemplate.view.validation.wideQty')
              .d('打印区域超过纸张宽度'),
          });
        }
        if (!validateHeight(data)) {
          errorMessages.push(
            intl.get('hrpt.labelTemplate.view.validation.heightQty').d('打印区域超过纸张高度')
          );
          notification.error({
            message: intl
              .get('hrpt.labelTemplate.view.validation.heightQty')
              .d('打印区域超过纸张高度'),
          });
        }
        if (errorMessages && errorMessages.length === 0) {
          try {
            labelPrintDS.setQueryParameter('tenantId', tenantId);
            const rr = await labelPrintDS.submit();
            return (rr || {}).success || false;
            // if(!rr) {
            //   return false;
            // }
          } catch {
            // return false;
          }
        }
        // if (errorMessages.length > 0) {
        //   notification.error({ message: <pre>{errorMessages.join('\n')}</pre> });
        // } else {
        //   try {
        //     const rr = await labelPrintDS.submit();
        //     // return (rr || {}).success || false;
        //     if(!rr) {
        //       return false;
        //     }
        //   } catch {
        //     return false;
        //   }
        // }
        // return false;
      }
      return false;
    };
    const listenForceUpdate = ({ name, value }) => {
      switch (name) {
        case 'paperSize':
          if (value === 'customize') {
            // 自定义
          } else {
            labelPrintDS.current.set(
              'paperWidth',
              paperSizeMap[value] && paperSizeMap[value].width
            );
            labelPrintDS.current.set(
              'paperHigh',
              paperSizeMap[value] && paperSizeMap[value].height
            );
            labelPrintDS.current.set('_paperNoLimit', false);
          }
          break;
        case '_paperNoLimit':
          if (value) {
            labelPrintDS.current.set('paperHigh', undefined);
            labelPrintDS.current.set('highQty', undefined);
            labelPrintDS.current.set('marginBottom', undefined);
          }
          break;
        default:
          break;
      }
    };
    labelPrintDS.addEventListener('update', listenForceUpdate);
    modal.handleOk(handleOk);
    return () => {
      // not method to cancel hook
      labelPrintDS.removeEventListener('update', listenForceUpdate);
    };
  }, [labelPrintDS, modal.handleOk, paperSizeMap]);

  return (
    <div className={styles['label-print']}>
      <Spin spinning={loading}>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hrpt.labelTemplate.view.title.paper').d('纸张设置')}</h3>}
        >
          <Form columns={2} dataSet={labelPrintDS}>
            <Select name="paperSize" colSpan={2} newLine />
            <NumberField name="paperWidth" colSpan={2} newLine />
            <NumberField name="paperHigh" />
            <Switch name="_paperNoLimit" />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hrpt.labelTemplate.view.title.direction').d('方向设置')}</h3>}
        >
          <Form columns={2} dataSet={labelPrintDS}>
            <SelectBox name="printDirection">
              <SelectBox.Option value={0}>
                {intl.get('hrpt.labelTemplate.view.option.printDirection.ver').d('横向')}
              </SelectBox.Option>
              <SelectBox.Option value={1}>
                {intl.get('hrpt.labelTemplate.view.option.printDirection.hor').d('纵向')}
              </SelectBox.Option>
            </SelectBox>
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hrpt.labelTemplate.view.title.padding').d('页边距')}</h3>}
        >
          <Form columns={2} dataSet={labelPrintDS}>
            <NumberField name="marginTop" />
            <NumberField name="marginLeft" />
            <NumberField name="marginBottom" />
            <NumberField name="marginRight" />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hrpt.labelTemplate.view.title.count').d('标签数量')}</h3>}
        >
          <Form columns={2} dataSet={labelPrintDS}>
            <NumberField name="wideQty" step={1} min={1} />
            <NumberField name="highQty" step={1} min={1} />
            <NumberField name="highSpace" />
          </Form>
        </Card>
      </Spin>
    </div>
  );
};

export default PrintSettings;
