/**
 * @since 2020-1-5
 * @author MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { Tabs, Row, Col, Form, Button } from 'hzero-ui';
import { CodeArea } from 'choerodon-ui/pro';
import JSONFormatter from 'choerodon-ui/pro/lib/code-area/formatters/JSONFormatter';
import 'choerodon-ui/pro/lib/code-area/lint/json';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import { words } from 'lodash';
import notification from 'utils/notification';
import intl from 'utils/intl';
import SqlSearch from './SqlSearch';
import ConfigSearch from './ConfigSearch';
import TestPageModal from './TestPageModal';

// const options = { mode: { name: 'javascript', json: true } };

@Form.create({ fieldNameProp: null })
@connect(({ inquiryConfig }) => ({
  inquiryConfig,
}))
export default class Search extends React.Component {
  constructor(props) {
    super(props);
    this.configSearchRef = React.createRef();
    this.state = {
      currentTab: 'ConfigSearch',
      showModal: false,
      fieldArray: [],
      result: '', // 执行的返回结果
    };
  }

  componentDidMount() {
    // this.fetchDataSet();
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.indexId !== this.props.indexId) {
      this.fetchDataSet(nextProps.indexId);
    }
  }

  @Bind()
  fetchDataSet(indexId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'inquiryConfig/fetchList',
      payload: { indexId },
    });
  }

  @Bind()
  tabChange(currentTab) {
    this.setState({
      currentTab,
    });
  }

  @Bind()
  transformSearch(SearchList, filterList) {
    const elasticSearch = { query: { bool: {} } };
    SearchList.forEach((val) => {
      if (!elasticSearch.query.bool[val.relation]) {
        elasticSearch.query.bool[val.relation] = [];
      }
      val.conditionList.forEach((item) => {
        if (item.logicSymbol === 'term') {
          elasticSearch.query.bool[val.relation].push({ term: { [item.indexCode]: item.param } });
        } else if (item.logicSymbol === 'gt' || item.logicSymbol === 'lt') {
          let isExist = false;
          elasticSearch.query.bool[val.relation].forEach((query) => {
            if (query.range && query.range[item.indexCode]) {
              // eslint-disable-next-line no-param-reassign
              query.range[item.indexCode][item.logicSymbol] = item.param;
              isExist = true;
            }
          });
          if (!isExist) {
            elasticSearch.query.bool[val.relation].push({
              range: { [item.indexCode]: { [item.logicSymbol]: item.param } },
            });
          }
        }
      });
    });
    filterList.forEach((val) => {
      if (!elasticSearch.query.bool.filter) {
        elasticSearch.query.bool.filter = { bool: {} };
      }
      if (!elasticSearch.query.bool.filter.bool[val.relation]) {
        elasticSearch.query.bool.filter.bool[val.relation] = [];
      }
      val.conditionList.forEach((item) => {
        if (item.logicSymbol === 'term') {
          elasticSearch.query.bool.filter.bool[val.relation].push({
            match: { [item.indexCode]: item.param },
          });
        } else if (item.logicSymbol === 'gt' || item.logicSymbol === 'lt') {
          let isExist = false;
          elasticSearch.query.bool.filter.bool[val.relation].forEach((query) => {
            if (query.range && query.range[item.indexCode]) {
              // eslint-disable-next-line no-param-reassign
              query.range[item.indexCode][item.logicSymbol] = item.param;
              isExist = true;
            }
          });
          if (!isExist) {
            elasticSearch.query.bool.filter.bool[val.relation].push({
              range: { [item.indexCode]: { [item.logicSymbol]: item.param } },
            });
          }
        }
      });
    });
    return elasticSearch;
  }

  /**
   * 封装数据
   * @param {*} configFrom  -表单数据
   * @param {*} currentSearchList -现有数据
   */
  @Bind()
  getFormData(configFrom, currentSearchList) {
    currentSearchList.forEach((val) => {
      const searchItem = val;
      searchItem.relation = configFrom.getFieldValue(`${searchItem.id}-relation`);
      searchItem.conditionList.forEach((res) => {
        const conditionItem = res;
        conditionItem.indexCode = configFrom.getFieldValue(`${conditionItem.id}-indexCode`);
        conditionItem.logicSymbol = configFrom.getFieldValue(`${conditionItem.id}-logicSymbol`);
        conditionItem.param = configFrom.getFieldValue(`${conditionItem.id}-param`);
      });
    });
  }

  @Bind()
  createRespondParam() {
    const { dispatch, form } = this.props;
    const { currentTab } = this.state;
    const indexCode = form.getFieldValue(`indexCode`);
    const sqlBody = form.getFieldValue('requireUrl');
    const customQuery = form.getFieldValue('CustomQuery');
    if (currentTab === 'ConfigSearch') {
      const configFrom = this.configSearchRef.current.props.form;
      const searchList = this.configSearchRef.current.state.search;
      const filterList = this.configSearchRef.current.state.filter;
      // 将form表单数据同步到SearchList中
      this.getFormData(configFrom, searchList);
      this.getFormData(configFrom, filterList);
      // 将currentSearchList转化为 elasticsearch query
      const elasticSearch = JSON.stringify(
        this.transformSearch(searchList, filterList),
        null,
        2
      ).replace(/\\/g, '');
      // form.setFieldsValue({ respondParam: elasticSearch });
      this.props.respondParamOnChange(elasticSearch);
    } else if (currentTab === 'SqlSearch') {
      const esSqlTranslateDTO = {
        indexCode,
        sqlBody,
      };
      dispatch({
        type: 'inquiryConfig/sqlRequest',
        payload: esSqlTranslateDTO,
      }).then((res) => {
        if (res.error) {
          notification.warning({
            message: JSON.stringify(res),
          });
        } else {
          this.props.respondParamOnChange(JSON.stringify(res));
        }
      });
    } else if (currentTab === 'CustomQuery') {
      try {
        JSON.parse(customQuery);
        this.props.respondParamOnChange(customQuery);
        return true;
      } catch (e) {
        notification.warning({
          message: intl
            .get('hsrh.InquiryConfig.view.message.queryJsonValidate')
            .d('查询JSON为空或格式不正确！'),
        });
        return false;
      }
    }
  }

  @Bind()
  showTestModal() {
    const { respondParam } = this.props;
    if (respondParam && respondParam.length !== 0) {
      const fieldArray = words(respondParam, /#{[A-Za-z0-9]+}/g).map((item) =>
        item.replace(/#|{|}/g, '')
      ); // 获取json格式的字符串中的占位符
      this.setState({
        showModal: true,
        fieldArray,
      });
    } else {
      notification.warning({
        message: intl
          .get('hsrh.InquiryConfig.view.message.queryJsonValidate')
          .d('查询JSON为空或格式不正确！'),
      });
    }
  }

  @Bind()
  handleCloseCacheModal() {
    this.setState({
      showModal: false,
      result: '',
    });
  }

  /**
   * 测试页面执行
   */
  @Bind()
  handleExecute(value) {
    const { dispatch, configCode } = this.props;
    // const indexCode = form.getFieldValue(`indexCode`);
    // if (!indexCode) {
    //   notification.warning({
    //     message: intl
    //       .get('hzero.InquiryConfig.view.message.indexCodeValidate')
    //       .d('索引不能为空'),
    //   });
    //   return;
    // }
    // const placeholderList = words(respondParam, /#{[A-Za-z0-9]+}/g);
    // let newRespondParam = respondParam;
    // if (placeholderList) {
    //   placeholderList.forEach((val) => {
    //     const key = val.replace(/#|{|}/g, '');
    //     newRespondParam = respondParam.replace(val, value[key]);
    //   });
    // }
    const httpRequestDTO = {
      configCode,
      queryMap: value,
    };
    dispatch({
      type: 'inquiryConfig/configRequest',
      payload: httpRequestDTO,
    }).then((res) => {
      this.setState({
        result: JSON.stringify(res),
      });
    });
  }

  render() {
    const {
      form,
      inquiryConfig: { indexList },
      respondParam,
    } = this.props;
    const { getFieldDecorator } = form;
    const formLayoutPro = {
      labelCol: { span: 3 },
      wrapperCol: { span: 20 },
    };
    const { currentTab, showModal, fieldArray, result } = this.state;
    const configSearchProps = {
      form,
      indexList,
      ref: this.configSearchRef,
    };
    const sqlSearchProps = {
      form,
      indexList,
    };
    const testPageProps = {
      visible: showModal,
      fieldArray,
      result,
      handleCancel: this.handleCloseCacheModal,
      handleExecute: this.handleExecute,
    };

    return (
      <React.Fragment>
        <Row>
          <Col span={12}>
            <Tabs tabPosition="left" defaultActiveKey={currentTab} onChange={this.tabChange}>
              <Tabs.TabPane
                tab={intl
                  .get('hsrh.inquiryConfig.model.inquiryConfig.configureQuery')
                  .d('配置查询')}
                key="ConfigSearch"
              >
                <ConfigSearch {...configSearchProps} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hsrh.inquiryConfig.model.inquiryConfig.sqlQuery').d('SQL查询')}
                key="SqlSearch"
              >
                <SqlSearch {...sqlSearchProps} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hsrh.inquiryConfig.model.inquiryConfig.customQuery').d('自定义参数')}
                key="CustomQuery"
              >
                <Form.Item>
                  {getFieldDecorator(
                    'CustomQuery',
                    {}
                  )(<CodeArea formatter={JSONFormatter} style={{ height: 397, lineHeight: 1 }} />)}
                </Form.Item>
              </Tabs.TabPane>
            </Tabs>
          </Col>
          <Col span={12}>
            <Form.Item
              // key={respondParam}
              {...formLayoutPro}
              // style={{ fontSize: 6 }}
              label={intl.get('hsrh.inquiryConfig.model.inquiryConfig.respondParam').d('返回参数')}
            >
              {getFieldDecorator('respondParam', {
                initialValue: respondParam,
              })(
                <CodeArea
                  options={{ theme: 'idea', mode: { name: 'javascript', json: true } }}
                  readOnly
                  formatter={JSONFormatter}
                  style={{ height: 450, lineHeight: 1 }}
                />
              )}
            </Form.Item>
          </Col>
        </Row>
        <div style={{ textAlign: 'center', marginLeft: 30 }}>
          <Button type="primary" onClick={this.createRespondParam}>
            {intl.get(`hzero.common.button.generate`).d('生成')}
          </Button>
          <Button type="primary" onClick={this.showTestModal} style={{ marginLeft: 180 }}>
            {intl.get(`hzero.common.button.test`).d('测试')}
          </Button>
        </div>
        <TestPageModal {...testPageProps} />
      </React.Fragment>
    );
  }
}
