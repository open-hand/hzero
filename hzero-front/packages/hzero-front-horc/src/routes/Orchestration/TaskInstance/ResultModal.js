import React from 'react';
import { CodeArea, Form, Output, TextArea } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { TagRender, dateTimeRender } from 'utils/renderer';
import { TAG_ORCH_TEXT_TYPE } from '@/constants/constants';
import JSONFormatter from 'choerodon-ui/pro/lib/code-area/formatters/JSONFormatter';
import YAMLFormatter from 'choerodon-ui/pro/lib/code-area/formatters/YAMLFormatter';
import style from './index.less';

dateTimeRender('2020-09-27 11:31:06');

class ResultModal extends React.Component {
  componentDidMount() {
    this.props.resultTableDS.current.set('taskName', this.props.taskName);
  }

  @Bind()
  rendererVOList({ value }) {
    return (
      value &&
      value.map((item) => {
        return (
          <>
            <a onClick={() => this.props.openResultModal(item.taskId, item.taskName)}>
              {item.taskName}
            </a>
            <br />
          </>
        );
      })
    );
  }

  @Bind()
  rendererResult({ value, record }) {
    const {
      data: { textType },
    } = record;
    const areaStyle = { height: 200, width: 400 };
    switch (textType.toLowerCase()) {
      case 'json':
        return <CodeArea value={value} formatter={JSONFormatter} style={areaStyle} />;
      case 'xml':
        return <CodeArea value={value} formatter={YAMLFormatter} style={areaStyle} />;
      case 'text':
      default:
        return <TextArea value={value} formatter style={areaStyle} />;
    }
  }

  render() {
    return (
      <div>
        <Form
          dataSet={this.props.resultTableDS}
          useColon
          labelAlign="right"
          className={style['horc-form-td']}
        >
          <Output name="taskName" />
          <Output name="taskInstanceVOList" renderer={this.rendererVOList} />
          <Output name="textType" renderer={({ value }) => TagRender(value, TAG_ORCH_TEXT_TYPE)} />
          <Output name="contentType" />
          <Output name="processTime" renderer={({ value }) => dateTimeRender(value)} />
          <Output name="remark" />
          <Output name="result" renderer={this.rendererResult} />
        </Form>
      </div>
    );
  }
}

export default ResultModal;
