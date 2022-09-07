/* eslint-disable react/jsx-no-target-blank */
/* eslint-disable react/button-has-type */
import React, { Component } from 'react';
import {
  Query,
  Builder,
  Utils,
  // types:
  ImmutableTree,
  Config,
  BuilderProps,
  JsonTree,
  JsonLogicTree,
} from '@/components/react-awesome-query-builder';
import throttle from 'lodash/throttle';
import _ from 'lodash';
import loadedConfig from './config';
// import './empty.css';
// import './modal.css';
import './styles.css';

// const styles = require('./index.less');

const { stringify } = JSON;
const { jsonLogicFormat, queryString, checkTree, loadTree, uuid, loadFromJsonLogic } = Utils;

const emptyInitValue: JsonTree = { id: uuid(), type: 'group' };

interface QueryBuilderState {
  tree: ImmutableTree;
  config: Config;
  finalValue: string;
}

export default class QueryBuilder extends Component<any, QueryBuilderState> {
  constructor(T: any) {
    super(T);
    const initLogic: JsonLogicTree =
      T.value && Object.keys(T.value).length > 0 ? T.value : undefined;
    const config = {
      ...loadedConfig,
      fields: this.loadFields(initLogic),
      settings: _.merge(loadedConfig.settings, {
        customFieldSelectProps: {
          onSearch: this.setField,
          onBlur: this.getFieldVal.bind(this),
        },
        immutableGroupsMode: T.readOnly,
        immutableFieldsMode: T.readOnly,
        immutableOpsMode: T.readOnly,
        immutableValuesMode: T.readOnly,
        canReorder: !T.readOnly,
      }),
    };
    const initTree: ImmutableTree = checkTree(loadFromJsonLogic(initLogic, config), config);
    this.state = {
      config,
      finalValue: '',
      tree: initTree,
    };
  }

  private immutableTree!: ImmutableTree;

  private config!: Config;

  setField = (val) => {
    const { config } = this.state;
    if (!_.isEmpty(val)) {
      this.setState({
        config: {
          ...config,
          fields: {
            ...config.fields,
            [val]: {
              label: val,
              type: 'text',
              operators: [
                'equal',
                'not_equal',
                'less',
                'less_or_equal',
                'greater',
                'greater_or_equal',
                'is_empty',
                'is_not_empty',
                // 'select_any_in',
                // 'select_not_any_in',
              ],
            },
          },
        },
        finalValue: val,
      });
    }
  };

  getFieldVal() {
    const { tree, config, finalValue } = this.state;
    this.setState({
      config: {
        ...config,
        fields: this.getFields(tree, config, finalValue),
      },
    });
  }

  getFields(tree, config, finalValue) {
    const { logic } = jsonLogicFormat(tree, config);
    const options: any[] = this.getFieldsFromLogic(logic);
    const { fields } = config;
    let temps = {};
    options.forEach((option) => {
      temps = { ...temps, [option]: fields[option] };
    });
    temps = !_.isEmpty(finalValue)
      ? {
          ...temps,
          [finalValue]: {
            label: finalValue,
            type: 'text',
            operators: [
              'equal',
              'not_equal',
              'less',
              'less_or_equal',
              'greater',
              'greater_or_equal',
              'is_empty',
              'is_not_empty',
              // 'select_any_in',
              // 'select_not_any_in',
            ],
          },
        }
      : temps;
    return temps;
  }

  getFieldsFromLogic(logic) {
    let options: any[] = [];
    const recursion = (data) => {
      _.keys(data).forEach((key) => {
        const item = data[key];
        const temp = !_.isArray(item) && _.isObject(item) && _.values(item)[0];
        if (_.isArray(item)) {
          recursion(item);
        } else if (_.isArray(temp)) {
          recursion(temp);
        } else {
          const { var: value } = item;
          if (!_.isUndefined(value)) {
            options.push(value);
            options = [...options, value];
          }
        }
      });
    };
    recursion(logic);
    return options;
  }

  loadFields(logic) {
    const options: any[] = this.getFieldsFromLogic(logic);
    let fields = {};
    options.forEach((option) => {
      fields = {
        ...fields,
        [option]: {
          label: option,
          type: 'text',
          operators: [
            'equal',
            'not_equal',
            'less',
            'less_or_equal',
            'greater',
            'greater_or_equal',
            'is_empty',
            'is_not_empty',
            // 'select_any_in',
            // 'select_not_any_in',
          ],
        },
      };
    });
    return fields;
  }

  render = () => {
    return (
      <div>
        <Query
          {...this.state.config}
          value={this.state.tree}
          onChange={this.onChange}
          renderBuilder={this.renderBuilder}
        />
      </div>
    );
  };

  // resetValue = () => {
  //   this.setState({
  //     tree: initTree,
  //   });
  // };

  clearValue = () => {
    this.setState({
      tree: loadTree(emptyInitValue),
    });
  };

  renderBuilder = (props: BuilderProps) => (
    <div className="query-builder-container" style={{ padding: '10px' }}>
      <div className="query-builder qb-lite">
        <Builder {...props} />
      </div>
    </div>
  );

  onChange = (immutableTree: ImmutableTree, config: Config) => {
    this.immutableTree = immutableTree;
    this.config = config;
    this.updateResult();

    // `jsonTree` or `logic` can be saved to backend
    // (and then loaded with `loadTree` or `loadFromJsonLogic` as seen above)
    const { logic } = jsonLogicFormat(immutableTree, config);
    let tempString = stringify(queryString(immutableTree, config)) || '';
    // TODO: config中配置equal和not_equal的label直接带到这边了，不知道为何没有生效，暂时先替换字符串的方法解决
    tempString = tempString.replace(/不等于/g, '!=');
    tempString = tempString.replace(/等于/g, '==');
    this.props.onGetFormat({
      stringFormat: tempString,
      jsonLogicFormat: stringify(logic),
    });
  };

  updateResult = throttle(() => {
    this.setState({ tree: this.immutableTree, config: this.config });
  }, 100);
}
