import React from 'react';
import _ from 'lodash';
import QuestionPopover from '@/components/QuestionPopover';
import COMMON_LANG from '@/langs/commonLang';
import SourceData from './sourceData';
import TargetData from './targetData';
import DrawLines from './drawLines';
import { calCoord, getRelationScript, transformLine, insertScriptHeader } from './util';
import AceEditor from './AceEditor';
import './fieldMapping.less';

class FieldMapping extends React.Component {
  sourceCom;

  targetCom;

  static defaultProps = {
    relation: [],
    source: {
      data: [],
      onChange: () => {},
      columns: [],
      mutiple: false,
    },
    target: {
      data: [],
      onChange: () => {},
      columns: [],
      mutiple: false,
    },
    edit: true,
  };

  constructor(props) {
    super(props);
    this.state = {
      relation: [],
      currentRelation: {},
      aceEditorWidth: 500,
      aceEditorHeight: 1200,
      relationScript: insertScriptHeader(''),
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.relation !== this.props.relation) {
      const relation = calCoord(_.assign([], nextProps.relation), this);
      this.changeRelation(relation, false);
    }
    this.setAceEditorSize();
    if (nextProps.script) {
      this.setState({ relationScript: nextProps.script });
    }
  }

  componentDidMount() {
    const relation = calCoord(_.assign([], this.props.relation), this);
    if (relation) {
      this.changeRelation(relation, false);
    }
    this.setAceEditorSize();
    this.setState({ relationScript: this.props.script });
  }

  uniqWith(data) {
    return _.uniqWith(data, (n1, n2) => {
      return n1.key === n2.key;
    }).filter((item) => !!item.key);
  }

  changeRelation(relation, isUpdate = true) {
    this.setState(
      {
        relation,
      },
      () => {
        // eslint-disable-next-line no-unused-expressions
        isUpdate && this.props.onChange && this.props.onChange(relation);
      }
    );
  }

  changeIconStatus(iconStatus) {
    this.setState({
      iconStatus,
    });
  }

  overActive(item, type, active) {
    const relation = _.assign([], this.state.relation);
    let currentRelation = {};
    relation.forEach((n) => {
      if (n[type].key === item.key) {
        if (active === 'enter') {
          currentRelation = n;
        } else if (active === 'leave') {
          currentRelation = {};
        }
      }
    });
    this.setState({
      currentRelation,
    });
  }

  changeSource(oldIndex, newIndex) {
    const {
      source: { data: sourceData = [], onChange = () => {} },
    } = this.props;
    let data = _.assign([], sourceData);
    const item = data.slice(oldIndex, oldIndex + 1);
    data.splice(oldIndex, 1);
    const dataS = data.slice(0, newIndex);
    const dataE = data.slice(newIndex, data.length);
    data = dataS.concat(item).concat(dataE);
    onChange(data);
    const relation = calCoord(_.assign([], this.props.relation), this);
    this.changeRelation(relation, false);
  }

  changeTarget(oldIndex, newIndex) {
    const {
      target: { data: targetData = [], onChange = () => {} },
    } = this.props;
    let data = _.assign([], targetData);
    const item = data.slice(oldIndex, oldIndex + 1);
    data.splice(oldIndex, 1);
    const dataS = data.slice(0, newIndex);
    const dataE = data.slice(newIndex, data.length);
    data = dataS.concat(item).concat(dataE);
    onChange(data);
    const relation = calCoord(_.assign([], this.props.relation), this);
    this.changeRelation(relation, false);
  }

  /**
   * 输入的脚本保存
   */
  handleSetScript(value) {
    this.props.onGetValue(value);
    this.setState({ relationScript: value }, () => {
      this.scriptToLine(value);
    });
  }

  /**
   * 由脚本实时更新连线
   */
  scriptToLine(script) {
    const {
      source: { data: sourceData = [] },
      target: { data: targetData = [] },
    } = this.props;
    const relation = transformLine(
      script,
      (sourceData[0] || {}).children,
      (targetData[0] || {}).children
    );
    const newRelation = calCoord(_.assign([], relation), this);
    this.changeRelation(newRelation);
  }

  /**
   * 设置右边代码框大小
   */
  setAceEditorSize() {
    const sourceEle = this.sourceCom.boxEle.querySelector('.column-content');
    const targetEle = this.targetCom.boxEle.querySelector('.column-content');
    const sourceNum = sourceEle.getElementsByTagName('li').length;
    const targetNum = targetEle.getElementsByTagName('li').length;
    const num = _.max([sourceNum, targetNum]);
    this.setState({
      aceEditorHeight: num === 0 ? 500 : (num + 1) * 36,
    });
  }

  /**
   * 由关系数组推导出脚本
   */
  handleGetRelationScript(relation) {
    const relationScript = getRelationScript(relation);
    // this.scriptToLine(relationScript);
    this.props.onGetValue(relationScript);
    this.setState({ relationScript });
  }

  render() {
    const {
      relation,
      iconStatus,
      currentRelation,
      relationScript,
      aceEditorWidth,
      aceEditorHeight,
    } = this.state;
    const {
      source: { data: sourceData = [], columns: sourceCols = [], mutiple: sourceMutiple = false },
      target: { data: targetData = [], columns: targetCols = [], mutiple: targetMutiple = false },
      className = '',
      style = {},
      isSort = false,
      onDrawStart,
      onDrawing,
      onDrawEnd,
      edit,
      closeIcon,
    } = this.props;
    const sourceOpt = {
      ref: (me) => {
        this.sourceCom = me;
      },
      iconStatus,
      relation,
      columns: sourceCols,
      data: sourceData,
      currentRelation,
      isSort,
      edit,
      changeData: this.changeSource.bind(this),
      overActive: this.overActive.bind(this),
    };
    const targetOpt = {
      ref: (me) => {
        this.targetCom = me;
      },
      iconStatus,
      relation,
      columns: targetCols,
      data: targetData,
      currentRelation,
      isSort,
      edit,
      changeData: this.changeTarget.bind(this),
      overActive: this.overActive.bind(this),
    };
    const drawLinesOpt = {
      sourceData,
      targetData,
      sourceMutiple,
      targetMutiple,
      onDrawStart,
      onDrawing,
      onDrawEnd,
      relation,
      edit,
      closeIcon,
      currentRelation,
      getRelationScript: this.handleGetRelationScript.bind(this),
      onChange: this.changeRelation.bind(this),
      changeIconStatus: this.changeIconStatus.bind(this),
    };
    const aceEditorProps = {
      disabled: !edit,
      value: relationScript,
      width: aceEditorWidth,
      height: aceEditorHeight,
      onChange: this.handleSetScript.bind(this),
    };
    return (
      <div className="field-relation">
        <div style={style} className={`react-field-mapping-box ${className}`}>
          <SourceData {...sourceOpt} />
          <DrawLines {...drawLinesOpt} />
          <TargetData {...targetOpt} />
        </div>
        <div className="relation-script">
          <AceEditor {...aceEditorProps} />
        </div>
        <h3>
          <QuestionPopover
            message={
              <>
                {COMMON_LANG.DW_SCRIPT_TIP}(
                <a
                  href="https://docs.mulesoft.com/mule-runtime/4.3/dataweave-language-guide"
                  // eslint-disable-next-line react/jsx-no-target-blank
                  target="_blank"
                >
                  https://docs.mulesoft.com/mule-runtime/4.3/dataweave-language-guide
                </a>
                )
              </>
            }
          />
        </h3>
      </div>
    );
  }
}
export default FieldMapping;
