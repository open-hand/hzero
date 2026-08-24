/**
 * 个性化组件
 * @date: 2019-12-15
 * @version: 0.0.1
 * @author: zhaotong <tong.zhao@hand-china.com>
 * @copyright Copyright (c) 2019, Hands
 */

import React, { ReactNode } from 'react';
import { Row, Col } from 'hzero-ui';
import { isArray, omit, isEmpty, isNil } from 'lodash';
import { Bind } from 'lodash-decorators';
import { queryUnitCustConfig, coverConfig, getFieldValueObject } from './customizeTool';
import {
  generateFormSkeleton,
  generateTableColumns,
  generateFilterForm,
} from './customizeGenerate';
import { UnitConfig } from './interfaces';

interface DecoratorProps {
  unitCode: string[];
  query: any;
  manualQuery: boolean;
  // {[unitCode]: ['attribute1', 'attribute2']}
  // 临时方案，对特定的扩展字段添加queryUsePost: true属性，影响多选lov
  usePostMap: any;
}
export default function withCustomize(decoratorProps: DecoratorProps) {
  const { unitCode = [], query, manualQuery = false, usePostMap } = decoratorProps || {};
  return (Component) => {
    // eslint-disable-next-line react/no-redundant-should-component-update
    class WrapIndividual extends React.PureComponent<any> {
      state: {
        configModel: { [unitcode: string]: UnitConfig };
        loading: boolean;
        cache: any;
      };

      constructor(props, ...args) {
        super(props, ...args);
        this.state = {
          configModel: {},
          loading: true,
          cache: {},
        };
      }

      componentDidMount() {
        if (manualQuery) {
          return;
        }
        this.queryUnitConfig();
      }

      @Bind()
      queryUnitConfig(params = query, fn?: Function) {
        if (unitCode && isArray(unitCode) && unitCode.length > 0) {
          queryUnitCustConfig({ unitCode: unitCode.join(','), ...params })
            .then((res) => {
              // eslint-disable-next-line no-unused-expressions
              typeof fn === 'function' && fn(res);
              if (res) {
                Object.keys(res).forEach((code) => {
                  const unitConfig = res[code];
                  const { unitType, maxCol = 3 } = unitConfig;
                  if (unitType === 'FORM' || unitType === 'QUERYFORM') {
                    unitConfig.fields.sort(
                      (before, after) =>
                        (before.formRow || 0) * maxCol +
                        before.formCol -
                        (after.formRow || 0) * maxCol -
                        after.formCol
                    );
                  } else {
                    unitConfig.fields.sort((before, after) => (before.seq || 0) - (after.seq || 0));
                  }
                });
                if (usePostMap) {
                  Object.keys(usePostMap).forEach((code) => {
                    const unitConfig = res[code];
                    const usePostList = usePostMap[code];
                    unitConfig.fields = unitConfig.fields.map((field) =>
                      usePostList.includes(field.fieldCode)
                        ? { ...field, queryUsePost: true }
                        : field
                    );
                  });
                }
                this.setState({
                  configModel: res,
                });
              }
            })
            .finally(() => {
              this.setState({ loading: false });
            });
        } else {
          this.setState({ loading: false });
        }
        // 每一次查询单元配置，便会重置window变量下的缓存对象，目前用于优化下拉框频繁调用查询接口，c7n暂时不需要
        (window as any).CUSTOMIZECACHE = {};
      }

      @Bind()
      customizeForm(options: any = {}, formComponent) {
        const {
          code,
          form,
          dataSource = {},
          readOnly = false,
          // dataSourceLoading, // 解决在dataSource查询完成前缓存dataSource的问题
        } = options;
        const { configModel: config, loading, cache } = this.state;
        if (loading) return null;
        if (!code || isEmpty(config[code])) return formComponent;
        const dataSourceLoading = isEmpty(dataSource);
        if (!cache[code] && !dataSourceLoading) {
          cache[code] = {
            form,
            dataSource,
            validRes: {}, // 缓存校验错误信息
            count: 0, // 渲染计数，避免第一次没有数据时进行自定义校验
          };
          this.setState({
            cache,
          });
        }
        const { unitAlias = [] } = config[code];
        const unitData = getFieldValueObject(unitAlias, this.getCache, code); // 获取当前单元的关联单元数据
        return customizeFormCompatible(formComponent, config[code], {
          form,
          cache: cache[code],
          code,
          dataSource,
          dataSourceLoading,
          unitData,
          readOnly,
          getValueFromCache: this.getValueFromCache,
        });
      }

      @Bind()
      customizeTable(options: any = {}, table) {
        const { configModel: config, loading, cache } = this.state;
        const { code, readOnly } = options;
        if (loading) {
          // eslint-disable-next-line no-param-reassign
          table.props.columns = [];
          // eslint-disable-next-line no-param-reassign
          table.props.scroll = {
            x: 0,
          };
          return table;
        }
        if (!code || isEmpty(config[code])) return table;
        if (!cache[code]) {
          cache[code] = { validRes: {}, needUpdate: [] };
          this.setState({
            cache,
          });
        }
        const { unitAlias = [] } = config[code];
        const unitData = getFieldValueObject(unitAlias, this.getCache, code); // 获取当前单元的关联单元数据
        const { columns, scroll } = table.props;
        const { columns: newColumns, noWidthCount, scrollWidth } = generateTableColumns(
          columns,
          config[code],
          {
            readOnly,
            unitData,
            code,
            getValueFromCache: this.getValueFromCache,
            cache: cache[code],
          }
        );
        // eslint-disable-next-line no-param-reassign
        table.props.columns = newColumns;
        if (scroll !== undefined) {
          const originScroll = table.props.scroll;
          // eslint-disable-next-line no-param-reassign
          table.props.scroll = { ...originScroll, x: scrollWidth + noWidthCount * 200 };
        }
        return table;
      }

      @Bind()
      customizeFilterForm(options: any = {}, formComponent) {
        const { code, form, expand } = options;
        const { configModel: config, loading } = this.state;
        if (loading) return null;
        if (!code || isEmpty(config[code])) return formComponent;
        const { unitAlias = [] } = config[code];
        const unitData = getFieldValueObject(unitAlias, this.getCache, code); // 获取当前单元的关联单元数据
        return filterFormCompatible(formComponent, config[code], {
          form,
          expand,
          unitData,
          getValueFromCache: this.getValueFromCache,
        });
      }

      @Bind()
      customizeCollapse(options: any = {}, collapse) {
        const { code } = options;
        const { configModel: config, loading } = this.state;
        if (loading) return null;
        if (!code || isEmpty(config[code])) return collapse;
        const { fields = [] } = config[code];
        fields.sort((p, n) => (p.seq === undefined || n.seq === undefined ? -1 : p.seq - n.seq));
        const childrenMap = {};
        const newChildren: Array<ReactNode> = [];
        const refTabs = collapse;
        const refChildren = refTabs.props.children;
        if (isArray(refChildren)) {
          refChildren.forEach((i) => {
            if (i.props && i.key !== undefined) {
              childrenMap[i.key] = i;
            }
          });
        } else if (refChildren && refChildren.props && refChildren.key) {
          childrenMap[refChildren.key] = refChildren;
        }
        const defaultActive: string[] = [];
        fields.every((field) => field.defaultActive === 1 && defaultActive.push(field.fieldCode));
        if (defaultActive.length > 0) {
          refTabs.props.activeKey = defaultActive;
        }
        fields.forEach((i) => {
          const { fieldName, fieldCode, conditionHeaderDTOs } = i;
          const { visible } = coverConfig({ visible: i.visible }, conditionHeaderDTOs, {
            getValueFromCache: this.getValueFromCache,
          });
          const targetPane = childrenMap[fieldCode];
          if (!targetPane) return;
          if (fieldName !== undefined && targetPane && targetPane.props) {
            const oldHeader = targetPane.props.header;
            if (typeof oldHeader === 'function') {
              targetPane.props.header = oldHeader(fieldName);
            } else {
              targetPane.props.header = <h3>{fieldName}</h3>;
            }
          }
          if (visible !== 0) {
            newChildren.push(targetPane);
          }
          delete childrenMap[fieldCode];
        });
        Object.keys(childrenMap).forEach((i) => newChildren.push(childrenMap[i]));
        refTabs.props.children = newChildren;
        return collapse;
      }

      @Bind()
      customizeTabPane(options: any = {}, tabs) {
        const { code } = options;
        const { configModel: config, loading } = this.state;
        if (loading) return null;
        if (!code || isEmpty(config[code])) return tabs;
        const { fields = [] } = config[code];
        fields.sort((p, n) => (p.seq === undefined || n.seq === undefined ? -1 : p.seq - n.seq));
        const childrenMap = {};
        const newChildren: Array<ReactNode> = [];
        const refTabs = tabs;
        const refChildren = refTabs.props.children;
        if (isArray(refChildren)) {
          refChildren.forEach((i) => {
            if (i.props && i.key !== undefined) {
              childrenMap[i.key] = i;
            }
          });
        } else if (refChildren && refChildren.props && refChildren.key) {
          childrenMap[refChildren.key] = refChildren;
        }
        const defaultActive = fields.find((field) => field.defaultActive === 1);
        if (defaultActive) {
          refTabs.props.activeKey = defaultActive.fieldCode;
        }
        fields.forEach((i) => {
          const { fieldName, fieldCode, conditionHeaderDTOs } = i;
          const { visible } = coverConfig({ visible: i.visible }, conditionHeaderDTOs, {
            getValueFromCache: this.getValueFromCache,
          });
          const targetPane = childrenMap[fieldCode];
          if (!targetPane) return;
          if (fieldName !== undefined && targetPane && targetPane.props) {
            targetPane.props.tab = fieldName;
          }
          if (visible !== 0) {
            newChildren.push(targetPane);
          }
          delete childrenMap[fieldCode];
        });
        Object.keys(childrenMap).forEach((i) => newChildren.push(childrenMap[i]));
        refTabs.props.children = newChildren;
        return tabs;
      }

      @Bind()
      getCache(code) {
        return this.state.cache[code] || {};
      }

      @Bind()
      getValueFromCache(uCode, fieldCode) {
        const { cache } = this.state;
        if (!cache[uCode]) return {};
        const { form, dataSource = {} } = cache[uCode];
        const allValue = {
          ...dataSource,
          ...(form ? form.getFieldsValue() : {}),
        };
        return allValue[fieldCode];
      }

      render() {
        const newProps = {
          ...this.props,
          customizeForm: this.customizeForm,
          customizeTable: this.customizeTable,
          customizeFilterForm: this.customizeFilterForm,
          customizeTabPane: this.customizeTabPane,
          customizeCollapse: this.customizeCollapse,
          queryUnitConfig: this.queryUnitConfig,
          custLoading: this.state.loading, // 解决页面偶尔不刷新的问题
        };

        return <Component {...newProps} ref={this.props.forwardRef} />;
      }
    }
    return React.forwardRef((props, ref) => <WrapIndividual {...props} forwardRef={ref} />);
  };
}
function filterFormCompatible(
  formComponent,
  config: UnitConfig = {},
  { expand = false, form, unitData, getValueFromCache }
) {
  const {
    maxCol = 3,
    fields,
    labelCol: unitLabelCol = 10,
    wrapperCol: unitWrapperCol = 14,
  } = config;
  const { props: { children: wrapRow } = { children: {} } } = formComponent;
  const filter = wrapRow.props ? wrapRow.props.children : [];
  if (filter.length < 2) return formComponent;
  if (
    !filter[0].props ||
    !filter[1].props ||
    !filter[0].props.children ||
    !filter[1].props.children ||
    !(filter[1].props.children.props || {}).children
  ) {
    return;
  }
  const fieldRows = isArray(filter[0].props.children)
    ? filter[0].props.children
    : [filter[0].props.children];
  const controller = filter[1].props.children;
  const formItemObj = {};
  const order = [];
  fieldRows.forEach(({ props }) => {
    traversalFilterForm(props.children, formItemObj, order);
  });
  const newFormItem = generateFilterForm(formItemObj, fields, {
    form,
    unitLabelCol,
    unitWrapperCol,
    unitData,
    getValueFromCache,
  });
  fieldRows[0] = <Row />;
  if (newFormItem.length > maxCol) {
    fieldRows[1] = <Row style={{ display: expand ? 'block' : 'none' }} />;
  } else {
    [filter[0].props.children] = fieldRows;
    controller.props.children = (controller.props.children || []).slice(1);
  }
  newFormItem.forEach((i, index) => {
    let target = -1;
    if (index > 2) {
      target = 1;
    } else {
      target = 0;
    }
    if (isArray(fieldRows[target].props.children)) {
      fieldRows[target].props.children.push(<Col span={Math.floor(24 / maxCol)}>{i}</Col>);
    } else {
      fieldRows[target].props.children = [<Col span={Math.floor(24 / maxCol)}>{i}</Col>];
    }
  });
  filter[0].props.children = fieldRows;
  return formComponent;
}

function customizeFormCompatible(formComponent: any, config, options) {
  const {
    props: { children: rowChildren, className = '' } = { children: undefined },
  } = formComponent;
  const formItemObj = {};
  // eslint-disable-next-line no-param-reassign
  options.className = className;
  if (isArray(rowChildren)) {
    rowChildren.forEach((row, index) => {
      if (!isNil(row) && typeof row === 'object') {
        traversalStandardForm(
          row.props.children,
          formItemObj,
          index,
          0,
          omit(row.props, ['children'])
        );
      }
    });
  } else {
    traversalStandardForm(
      (rowChildren.props || {}).children,
      formItemObj,
      0,
      0,
      omit(rowChildren.props, ['children'])
    );
  }
  return generateFormSkeleton(formItemObj, config, options);
}

function traversalStandardForm(reactElement, formObj = {}, row, col = 0, rowProps) {
  if (!reactElement) return;
  if (isArray(reactElement)) {
    reactElement.forEach((i, _index) => traversalStandardForm(i, formObj, row, _index, rowProps));
  } else if (reactElement.props.span) {
    const { children: singleFormItem } = reactElement.props;
    if (
      singleFormItem.type &&
      singleFormItem.type.defaultProps &&
      singleFormItem.type.defaultProps.prefixCls === 'ant-form'
    ) {
      let fieldCode;
      const formChildren = singleFormItem.props.children;
      if (isArray(formChildren)) {
        for (let i = 0; i < formChildren.length; i++) {
          if (formChildren[i].props && formChildren[i].props['data-__field'] !== undefined) {
            fieldCode = formChildren[i].props['data-__field'].name;
            break;
          }
        }
      } else if (formChildren.props && formChildren.props['data-__field']) {
        fieldCode = formChildren.props['data-__field'].name;
      }
      // eslint-disable-next-line no-param-reassign
      formObj[fieldCode] = {
        formItem: singleFormItem,
        row: row + 1,
        col: col + 1,
        rowProps,
        colProps: reactElement.props,
      };
    }
  }
}
function traversalFilterForm(reactElement, formObj = {}, order: string[] = []) {
  if (!reactElement) return;
  if (isArray(reactElement)) {
    reactElement.forEach((i) => traversalFilterForm(i, formObj, order));
  } else if (reactElement.props.span) {
    const { children: singleFormItem } = reactElement.props;
    if (
      singleFormItem.type &&
      singleFormItem.type.defaultProps &&
      singleFormItem.type.defaultProps.prefixCls === 'ant-form'
    ) {
      let fieldCode;
      const formChildren = singleFormItem.props.children;
      if (isArray(formChildren)) {
        for (let i = 0; i < formChildren.length; i++) {
          if (formChildren[i].props && formChildren[i].props['data-__field'] !== undefined) {
            fieldCode = formChildren[i].props['data-__field'].name;
            break;
          }
        }
      } else if (formChildren.props && formChildren.props['data-__field']) {
        fieldCode = formChildren.props['data-__field'].name;
      }
      // eslint-disable-next-line no-param-reassign
      formObj[fieldCode] = singleFormItem;
      order.push(fieldCode);
    }
  }
}
