/**
 * @since 2020-1-5
 * @author MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, Button, Card, Icon } from 'hzero-ui';
// import Lov from 'components/Lov';
// import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { isUndefined } from 'lodash';
import uuidv4 from 'uuid/v4';
import SearchGroupItem from './searchCondition/SearchGroupItem';

export default class ConfigSearch extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      search: [
        {
          id: uuidv4(),
          relation: 'must',
          conditionList: [
            {
              id: uuidv4(),
              indexCode: '',
              logicSymbol: '',
              param: '',
            },
          ],
        },
      ],
      filter: [],
    };
  }

  componentDidMount() {}

  /**
   * 查询增加/删除条件
   * @param {*} type - 操作类型
   * @param {*} searchId  -查询块ID
   * @param {*} conditionId  -条件块ID
   */
  @Bind()
  handConditions(type, searchId, conditionId) {
    const { search } = this.state;
    const newConditionItem = {
      id: uuidv4(),
      indexCode: '',
      logicSymbol: '',
      param: '',
    };
    const newSearchBlock = {
      id: uuidv4(),
      relation: 'must',
      conditionList: [newConditionItem],
    };
    if (type === 'search') {
      // 添加查询块
      search.push(newSearchBlock);
    } else if (type === 'delete') {
      search.forEach((val, index) => {
        const searchItem = val;
        if (searchItem.id === searchId) {
          searchItem.conditionList = searchItem.conditionList.filter(
            item => item.id !== conditionId
          );
          if (searchItem.conditionList.length === 0) {
            search.splice(index, 1);
          }
        }
      });
    } else if (type === 'add') {
      search.forEach(val => {
        const searchItem = val;
        if (searchItem.id === searchId) {
          searchItem.conditionList.push(newConditionItem);
        }
      });
    }
    this.setState({
      search,
    });
  }

  @Bind()
  closeBlock(type, id) {
    const { search, filter } = this.state;
    if (type === 'search') {
      const newWearch = search.filter(item => item.id !== id);
      this.setState({
        search: newWearch,
      });
    } else {
      const newFilter = filter.filter(item => item.id !== id);
      this.setState({
        filter: newFilter,
      });
    }
  }

  /**
   * 查询增加/删除条件
   * @param {*} type - 操作类型
   * @param {*} searchId  -查询块ID
   * @param {*} conditionId  -条件块ID
   */
  @Bind()
  handFilter(type, searchId, conditionId) {
    const { filter } = this.state;
    const newConditionItem = {
      id: uuidv4(),
      indexCode: '',
      logicSymbol: '',
      param: '',
    };
    const newSearchBlock = {
      id: uuidv4(),
      relation: 'must',
      conditionList: [newConditionItem],
    };
    if (type === 'search') {
      // 添加查询块
      filter.push(newSearchBlock);
    } else if (type === 'delete') {
      filter.forEach((val, index) => {
        const searchItem = val;
        if (searchItem.id === searchId) {
          searchItem.conditionList = searchItem.conditionList.filter(
            item => item.id !== conditionId
          );
          if (searchItem.conditionList.length === 0) {
            filter.splice(index, 1);
          }
        }
      });
    } else if (type === 'add') {
      filter.forEach(val => {
        const searchItem = val;
        if (searchItem.id === searchId) {
          searchItem.conditionList.push(newConditionItem);
        }
      });
    }
    this.setState({
      filter,
    });
  }

  @Bind()
  indexOnChange(indexId, record) {
    const { form } = this.props;
    if (indexId) {
      if (isUndefined(form.getFieldValue('indexCode'))) {
        // 往外层form配置indexCode表单域
        form.registerField('indexCode');
      }
      form.setFieldsValue({ indexCode: record.indexCode });
    } else {
      form.setFieldsValue({ indexCode: null });
    }
  }

  render() {
    const { form, indexList } = this.props;
    const { search, filter } = this.state;
    return (
      <React.Fragment>
        <Form>
          <Card
            title={intl.get('hsrh.inquiryConfig.view.title.search').d('查询')}
            bordered={false}
            extra={
              <Button onClick={() => this.handConditions('search')}>
                {intl.get('hzero.common.button.add').d('新增')}
              </Button>
            }
          >
            {search.map(groupItem => {
              const props = {
                groupItem,
                form,
                handConditions: this.handConditions,
                indexList,
              };
              return (
                <Card style={{ marginTop: 10 }}>
                  <Icon
                    style={{ position: 'absolute', right: 10, top: 10, cursor: 'pointer' }}
                    type="close"
                    onClick={() => this.closeBlock('search', groupItem.id)}
                  />
                  <SearchGroupItem {...props} />
                </Card>
              );
            })}
          </Card>
          <Card
            title={intl.get('hsrh.inquiryConfig.view.title.filter').d('过滤')}
            bordered={false}
            extra={
              <Button onClick={() => this.handFilter('search')}>
                {intl.get('hzero.common.button.add').d('新增')}
              </Button>
            }
          >
            {filter.map(groupItem => {
              const props = {
                groupItem,
                form,
                handConditions: this.handFilter,
                indexList,
              };
              return (
                <Card style={{ marginTop: 10 }}>
                  <Icon
                    style={{ position: 'absolute', right: 10, top: 10, cursor: 'pointer' }}
                    type="close"
                    onClick={() => this.closeBlock('filter', groupItem.id)}
                  />
                  <SearchGroupItem {...props} />
                </Card>
              );
            })}
          </Card>
        </Form>
      </React.Fragment>
    );
  }
}
