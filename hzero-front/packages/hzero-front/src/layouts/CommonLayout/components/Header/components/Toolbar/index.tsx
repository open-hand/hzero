/* eslint-disable no-nested-ternary */
/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import classNames from 'classnames';
import React from 'react';
import { getEnvConfig } from 'utils/iocUtils';
import { isTenantRoleLevel } from 'utils/utils';

import DefaultLanguageSelect from '@/layouts/components/DefaultLanguageSelect';
import DefaultNoticeIcon from '@/layouts/components/DefaultNoticeIcon';
import DefaultTraceLog from '@/layouts/components/DefaultTraceLog';
import NormalTenantSelect from '@/layouts/DefaultLayout/components/NormalTenantSelect';
import NormalDataHierarchiesSelect from '@/layouts/DefaultLayout/components/NormalDataHierarchiesSelect';
import NormalDataHierarchies from '@/layouts/DefaultLayout/components/NormalDataHierarchies';
import ThemeButton from '@/layouts/DefaultLayout/components/NormalHeader/ThemeButton';

import CollapsedIcon from '../CollapsedIcon';
import CommonSelect from '../CommonSelect';

import { getClassName as getCommonLayoutClassName } from '../../../../utils';

const {
  VERSION_IS_OP,
  WEBSOCKET_URL,
  TRACE_LOG_ENABLE,
  MULTIPLE_SKIN_ENABLE,
  MULTIPLE_LANGUAGE_ENABLE,
} = getEnvConfig();

interface Element {
  sort: number;
  ele: any | false;
  key: string;
}

interface OverrideElements {
  'language-switch': { show: boolean; sort: number };
  'extra-header-right': { show: boolean; sort: number };
  'notice-message': { show: boolean; sort: number };
  'tenant-select': { show: boolean; sort: number };
  'collapsed-icon': { show: boolean; sort: number };
  'trace-log': { show: boolean; sort: number };
  'theme-config-entry': { show: Boolean; sort: number };
  'data-hierarchy-code': { show: Boolean; sort: number };
  'data-hierarchy-code2': { show: Boolean; sort: number };
}

interface ToolbarProps {
  getClassName: (cls: string) => string;
  dataHierarchyFlag: number;
  extraHeaderRight: React.FunctionComponentElement<any>;
  overrideElement?: OverrideElements;
  extraElement?: Element[];
  setCollapsed: (collapsed: boolean) => void;
  collapsed: boolean;
  dispatch: () => void;
  hierarchicalSelectList: any[];
  isModal: number;
  isSelect: number;
}

const noticeIconPopupAlign: {
  offset: [number, number];
} = { offset: [25, -8] };
const Toolbar: React.FC<ToolbarProps> = ({
  getClassName = getCommonLayoutClassName,
  dataHierarchyFlag,
  extraHeaderRight,
  overrideElement,
  extraElement,
  collapsed,
  setCollapsed,
  dispatch,
  hierarchicalSelectList = [],
  isModal = 0,
  isSelect = 0,
}) => {
  const _extraHeaderRight = React.useMemo(() => {
    if (!extraHeaderRight) {
      return undefined;
    }
    if (Array.isArray(extraHeaderRight)) {
      return (
        <>
          {extraHeaderRight.map((com) => {
            return React.isValidElement(com) ? com : React.createElement(com);
          })}
        </>
      );
    } else if (React.isValidElement(extraHeaderRight)) {
      return extraHeaderRight;
    } else {
      return (React.createElement as any)(extraHeaderRight);
    }
  }, [extraHeaderRight]);

  const hasWebsocketUrl = React.useMemo(() => {
    let websocketUrlSeted;
    if (
      WEBSOCKET_URL !== ['BUILD_', 'WEBSOCKET_', 'HOST'].join('') &&
      WEBSOCKET_URL !== 'undefined'
    ) {
      websocketUrlSeted = WEBSOCKET_URL;
    } else {
      websocketUrlSeted = false;
    }
    return websocketUrlSeted;
  }, []);

  const isTraceLog = React.useMemo(() => {
    let traceLog = false;
    try {
      traceLog = TRACE_LOG_ENABLE ? JSON.parse(TRACE_LOG_ENABLE) : false;
    } catch (e) {
      traceLog = false;
    }
    return traceLog;
  }, []);

  const isUed = React.useMemo(() => {
    let ued = false;
    try {
      ued = MULTIPLE_SKIN_ENABLE ? JSON.parse(MULTIPLE_SKIN_ENABLE) : false;
    } catch (e) {
      ued = false;
    }
    return ued;
  }, []);

  const hasMultiLanguage = React.useMemo(() => {
    let multiLanguage = true;
    try {
      multiLanguage = MULTIPLE_LANGUAGE_ENABLE ? JSON.parse(MULTIPLE_LANGUAGE_ENABLE) : true;
    } catch (e) {
      multiLanguage = true;
    }
    return multiLanguage;
  }, []);

  const dealOverrideElement: Element[] = React.useMemo(() => {
    const dealOverrideElementConfig: OverrideElements = overrideElement || {
      'language-switch': { show: true, sort: 70 },
      'tenant-select': { show: true, sort: 60 },
      'extra-header-right': { show: true, sort: 80 },
      'notice-message': { show: true, sort: 10 },
      'collapsed-icon': { show: true, sort: -10 },
      'trace-log': { show: true, sort: 30 },
      'theme-config-entry': { show: true, sort: 20 },
      'data-hierarchy-code': { show: true, sort: 40 },
      'data-hierarchy-code2': { show: true, sort: 50 },
    };
    return [
      {
        key: 'collapsed-icon',
        ele: dealOverrideElementConfig['collapsed-icon'].show ? (
          <CollapsedIcon collapsed={collapsed} setCollapsed={setCollapsed} />
        ) : (
          false
        ),
        sort: dealOverrideElementConfig['collapsed-icon'].sort || -10,
      },
      {
        key: 'language-switch',
        ele: hasMultiLanguage ? (
          dealOverrideElementConfig['language-switch'].show ? (
            <DefaultLanguageSelect />
          ) : (
            false
          )
        ) : (
          false
        ),
        sort: dealOverrideElementConfig['language-switch'].sort || 70,
      },
      {
        key: 'tenant-select',
        ele: !VERSION_IS_OP && dealOverrideElementConfig['tenant-select'].show && (
          <NormalTenantSelect
            className={classNames(
              getClassName('header-toolbar-item'),
              getClassName('header-toolbar-item-tenant-select')
            )}
          />
        ),
        sort: dealOverrideElementConfig['tenant-select'].sort || 60,
      },
      {
        key: 'extra-header-right',
        ele: _extraHeaderRight
          ? dealOverrideElementConfig['extra-header-right'].show
            ? _extraHeaderRight
            : false
          : false,
        sort: dealOverrideElementConfig['extra-header-right'].sort || 80,
      },
      {
        key: 'data-hierarchy-code',
        ele:
          isTenantRoleLevel() && dataHierarchyFlag && !!isSelect ? (
            <>
              {hierarchicalSelectList.map((item) => {
                return <NormalDataHierarchiesSelect dataHierarchyCode={item.dataHierarchyCode} />;
              })}
            </>
          ) : (
            false
          ),
        sort: dealOverrideElementConfig['data-hierarchy-code'].sort || 40,
      },
      {
        key: 'data-hierarchy-code2',
        ele:
          isTenantRoleLevel() && dataHierarchyFlag && !!isModal ? (
            <NormalDataHierarchies
              className={classNames(
                // getClassName('header-toolbar-item'),
                getClassName('header-toolbar-item-data-hierarchy')
              )}
            />
          ) : (
            false
          ),
        sort: dealOverrideElementConfig['data-hierarchy-code2'].sort || 50,
      },
      {
        key: 'trace-log',
        ele: isTraceLog ? (
          dealOverrideElementConfig['trace-log'].show ? (
            <DefaultTraceLog dispatch={dispatch} />
          ) : (
            false
          )
        ) : (
          false
        ),
        sort: dealOverrideElementConfig['trace-log'].sort || 30,
      },
      {
        key: 'theme-config-entry',
        ele: isUed ? (
          dealOverrideElementConfig['theme-config-entry'].show ? (
            <ThemeButton />
          ) : (
            false
          )
        ) : (
          false
        ),
        sort: dealOverrideElementConfig['theme-config-entry'].sort || 20,
      },
      {
        key: 'notice-message',
        ele: dealOverrideElementConfig['notice-message'].show ? (
          <DefaultNoticeIcon popupAlign={noticeIconPopupAlign} />
        ) : (
          false
        ),
        sort: dealOverrideElementConfig['notice-message'].sort || 10,
      },
      {
        key: 'common-switch',
        ele: <CommonSelect />,
        sort: 0,
      },
    ];
  }, [
    overrideElement,
    isTraceLog,
    isUed,
    isModal,
    isSelect,
    hasWebsocketUrl,
    collapsed,
    setCollapsed,
  ]);
  const { leftElements, rightElements } = React.useMemo(() => {
    const map = new Map<string, Element>();
    dealOverrideElement.forEach((ele) => {
      if (ele.ele) {
        map.set(ele.key, ele);
      }
    });
    (extraElement || []).forEach((ele) => {
      if (ele.ele) {
        map.set(ele.key, ele);
      }
    });
    const eles: Element[] = [];
    map.forEach((ele) => {
      eles.push(ele);
    });
    const leftEles: any[] = [];
    const rightEles: any[] = [];
    eles
      .sort((e1, e2) => e1.sort - e2.sort)
      .forEach((ele) => {
        const element = ele.ele;
        if (ele.sort >= 0) {
          rightEles.push(
            React.cloneElement(element, {
              key: ele.key,
              className: classNames(
                getClassName('header-toolbar-item'),
                element.props && element.props.className
              ),
            })
          );
        } else {
          leftEles.push(
            React.cloneElement(element, {
              key: ele.key,
              className: classNames(
                getClassName('header-toolbar-item'),
                element.props && element.props.className
              ),
            })
          );
        }
      });
    return {
      leftElements: leftEles,
      rightElements: rightEles,
    };
  }, [dealOverrideElement, extraElement]);
  return (
    <div className={getClassName('header-toolbar')}>
      <div className={getClassName('header-toolbar-left')}>{leftElements}</div>
      <div className={getClassName('header-toolbar-right')}>{rightElements}</div>
    </div>
  );
};

export default Toolbar;
