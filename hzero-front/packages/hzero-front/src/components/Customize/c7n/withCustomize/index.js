import React from 'react';
import { Bind } from 'lodash-decorators';
import { isArray, Bind as B } from 'lodash';
import { queryUnitCustConfig } from './customizeTool';
import custTable from './custX/custTable';
import custForm from './custX/custForm';
import custCollapseForm from './custX/custCollapseForm';
import custVTable from './custX/custVTable';
import custCollapse from './custX/custCollapse';
import custTabPane from './custX/custTabPane';

export default function withCustomize({ unitCode = [], query, manualQuery = false } = {}) {
  return (Component) => {
    class WrapIndividual extends React.Component {
      constructor(props, ...args) {
        super(props, ...args);
        this.state = {
          // eslint-disable-next-line react/no-unused-state
          custConfig: {},
          loading: true,
          cacheType: {},
          // eslint-disable-next-line react/no-unused-state
          cache: {},
          dataMap: new Map(),
          arrayDataMap: {},
          lastUpdateUnit: '',
        };
      }

      @Bind()
      setDataMap(code, value) {
        this.state.dataMap.set(code, value);
      }

      @Bind()
      getDataValue(code) {
        return this.state.dataMap.get(code) || {};
      }

      @Bind()
      setArrayDataMap(code, value, index) {
        const { arrayDataMap } = this.state;
        if (!arrayDataMap[code]) {
          arrayDataMap[code] = new Map();
        }
        arrayDataMap[code].set(index, value);
      }

      @Bind()
      getArrayDataValue(code, index) {
        const { arrayDataMap } = this.state;
        if (!arrayDataMap[code]) {
          return {};
        }
        return arrayDataMap[code].get(index) || {};
      }

      @Bind()
      getCacheType(code) {
        return this.state.cacheType[code];
      }

      @Bind()
      getToolFuns() {
        return {
          setArrayDataMap: this.setArrayDataMap,
          getArrayDataValue: this.getArrayDataValue,
          setDataMap: this.setDataMap,
          getDataValue: this.getDataValue,
          getCacheType: this.getCacheType,
        };
      }

      componentDidMount() {
        if (manualQuery) {
          return;
        }
        this.queryUnitConfig();
      }

      @Bind()
      queryUnitConfig(params = query, fn) {
        if (unitCode && isArray(unitCode) && unitCode.length > 0) {
          queryUnitCustConfig({ unitCode: unitCode.join(','), ...params })
            .then((res) => {
              // eslint-disable-next-line no-unused-expressions
              typeof fn === 'function' && fn(res);
              if (res) {
                this.setState({
                  // eslint-disable-next-line react/no-unused-state
                  custConfig: res || {},
                });
              }
            })
            .finally(() => {
              this.setState({ loading: false });
            });
        } else {
          this.setState({ loading: false });
        }
      }

      customizeForm = B(custForm, this);

      customizeCollapseForm = B(custCollapseForm, this);

      custTable = B(custTable, this);

      customizeVTable = B(custVTable, this);

      customizeCollapse = B(custCollapse, this);

      customizeTabPane = B(custTabPane, this);

      render() {
        const { loading = true, lastUpdateUnit } = this.state;
        const newProps = {
          ...this.props,
          custLoading: loading,
          lastUpdateUnit,
          customizeTable: this.custTable,
          customizeVTable: this.customizeVTable,
          customizeForm: this.customizeForm,
          customizeCollapseForm: this.customizeCollapseForm,
          customizeTabPane: this.customizeTabPane,
          queryUnitConfig: this.queryUnitConfig,
        };

        return <Component {...newProps} ref={this.props.forwardRef} />;
      }
    }
    return React.forwardRef((props, ref) => <WrapIndividual {...props} forwardRef={ref} />);
  };
}
