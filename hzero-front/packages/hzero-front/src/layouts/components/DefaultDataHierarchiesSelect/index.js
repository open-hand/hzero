/**
 * 数据层级配置
 */
import React, { PureComponent } from 'react';
import { Form, Menu, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isArray } from 'lodash';
import qs from 'querystring';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
import notification from 'utils/notification';

import { queryLov, queryLovData } from '@/services/api';

import style from './index.less';

/**
 * 数据层级配置
 *
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!boolean} [modalVisible=false] - 是否显示选择数据层级的模态框
 * @reactProps {!array} [expandedRowKeys=[]] - 显示的数据层级值id
 * @reactProps {!array} [renderTree=[]] - 数据层级列表数据
 * @reactProps {!array} [queryParams=[]] - 值集视图配置
 * @reactProps {!string} [dataHierarchyCode] - 数据层级编码
 * @reactProps {!string} [dataHierarchyMeaning] - 数据层级值展示值
 * @returns React.element
 */
@Form.create({ fieldNameProp: null })
class DefaultDataHierarchies extends PureComponent {
  /**
   * constructor - constructor方法
   * 组件构造函数
   */
  constructor(props) {
    super(props);
    this.state = {
      dataHierarchyCode: '', // 配置编码
      // dataHierarchyMeaning: '', // 配置显示值
      // dataHierarchyValue: '',
      valueField: '',
      displayField: '',
      dataHierarchyName: '',
      hierarchicalList: [],
    };
  }

  componentDidMount() {
    const { dispatch, dataHierarchyCode = 'TEST.DH.TENANT' } = this.props;
    dispatch({
      type: 'user/fetchDataHierarchiesSelectList',
      payload: {
        organizationId: getCurrentOrganizationId(),
        dataHierarchyCode,
      },
    }).then((res) => {
      if (res && getResponse(res)) {
        this.setState({
          dataHierarchyName: res.dataHierarchyName,
          // dataHierarchyValue: res.dataHierarchyValue,
          trueDataHierarchyValue: res.dataHierarchyMeaning,
          valueSourceCode: res.valueSourceCode,
          // dataHierarchyMeaning: res.dataHierarchyMeaning,
          dataHierarchyCode,
        });

        queryLov({ viewCode: res.valueSourceCode }).then((resp) => {
          if (getResponse(resp)) {
            let ret = resp.queryUrl;
            const organizationRe = /\{organizationId\}|\{tenantId\}/g;
            if (organizationRe.test(resp.queryUrl)) {
              ret = ret.replace(organizationRe, getCurrentOrganizationId());
            }
            const { valueField, displayField } = resp;
            this.setState({ valueField, displayField });
            const queryIndex = ret.indexOf('?');
            let sourceQueryParams = {};
            if (queryIndex !== -1) {
              sourceQueryParams = qs.parse(ret.substr(queryIndex + 1));
            }
            const sourceParams = {
              page: 0,
              size: 5,
              ...sourceQueryParams,
              lovCode: resp.lovTypeCode === 'URL' ? undefined : res.valueSourceCode,
            };
            queryLovData(ret, sourceParams).then((res2) => {
              if (getResponse(res2)) {
                if (isArray(res2)) {
                  this.setState({ hierarchicalList: res2.slice(0, 5) || [] });
                } else {
                  this.setState({ hierarchicalList: res2.content || [] });
                }
              }
            });
          }
        });
      }
    });
  }

  /**
   * @function handleChange - 切换层级配置
   */
  @Bind()
  handleHierarchyChange({ key }) {
    const { valueField, displayField, hierarchicalList } = this.state;
    const { trueDataHierarchyValue } = this.props;
    const arr = hierarchicalList.filter((item) => {
      return item[valueField].toString() === key;
    });
    if (key !== 'see-all-hierarchical') {
      // this.setState({
      //   dataHierarchyValue: key,
      //   dataHierarchyMeaning: !isEmpty(arr) ? arr[0][displayField] : trueDataHierarchyValue,
      //   trueDataHierarchyValue: !isEmpty(arr) ? arr[0][displayField] : trueDataHierarchyValue,
      // },()=>{
      this.handleSwitch({
        [valueField]: key,
        [displayField]: !isEmpty(arr) ? arr[0][displayField] : trueDataHierarchyValue,
      });
      // });
    }
  }

  /**
   * @function handleSwitch - 切换层级配置请求
   */
  @Bind()
  handleSwitch(e) {
    const { dispatch } = this.props;
    const { valueField, displayField, dataHierarchyCode } = this.state;
    dispatch({
      type: 'user/switchDataHierarchies',
      payload: {
        dataHierarchyMeaning: e[displayField],
        dataHierarchyCode,
        dataHierarchyValue: e && e[valueField],
        organizationId: getCurrentOrganizationId(),
      },
    }).then((res) => {
      if (res) {
        if (e && e[valueField]) {
          this.setState({
            // dataHierarchyValue: e[displayField],
            trueDataHierarchyValue: e[displayField],
            // dataHierarchyMeaning: e[displayField],
          });
        }
        notification.success();
        window.location.reload();
      }
    });
  }

  render() {
    const { fetchLoading } = this.props;
    const {
      valueSourceCode,
      valueField,
      displayField,
      dataHierarchyName,
      trueDataHierarchyValue,
      hierarchicalList,
    } = this.state;

    return (
      <>
        <Menu className="default-hierarchical-select-dropdown" onClick={this.handleHierarchyChange}>
          {fetchLoading ? (
            <Menu.Item key="loading-spin">
              <Spin />
            </Menu.Item>
          ) : (
            <Menu.SubMenu
              className={style['select-hierarchical-wrap']}
              title={
                <div className={style['select-hierarchical']}>
                  <span>{trueDataHierarchyValue || dataHierarchyName}</span>
                </div>
              }
            >
              {hierarchicalList.map((n) => (
                <Menu.Item key={n[valueField]} className={style['select-hierarchical-item']}>
                  <div className={style['select-hierarchical-item-divider']}>{n[displayField]}</div>
                </Menu.Item>
              ))}
              <Menu.Item key="see-all-hierarchical" className={style['select-hierarchical-item']}>
                <Lov
                  isButton
                  code={valueSourceCode}
                  onOk={(e) => {
                    this.handleSwitch(e);
                  }}
                  queryParams={{ tenantId: getCurrentOrganizationId() }}
                  className={style.lovButton}
                >
                  {intl.get('hzero.common.view.hierarchical.seeAll').d('查看层级值')}
                </Lov>
              </Menu.Item>
            </Menu.SubMenu>
          )}
        </Menu>
      </>
    );
  }
}

export default connect(({ loading }) => ({
  fetchLoading:
    loading.effects['user/switchDataHierarchies'] ||
    loading.effects['user/fetchDataHierarchiesSelectList'],
}))(DefaultDataHierarchies);
