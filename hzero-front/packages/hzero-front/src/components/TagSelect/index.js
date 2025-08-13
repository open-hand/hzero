import React, { Component } from 'react';
import classNames from 'classnames';
import { Tag, Icon } from 'hzero-ui';

import intl from 'utils/intl';

import styles from './index.less';

const { CheckableTag } = Tag;

const TagSelectOption = ({ children, checked, onChange, value }) => (
  <CheckableTag checked={checked} key={value} onChange={state => onChange(value, state)}>
    {children}
  </CheckableTag>
);

TagSelectOption.isTagSelectOption = true;

class TagSelect extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: false,
      value: props.value || props.defaultValue || [],
    };
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    if ('value' in nextProps && nextProps.value) {
      this.setState({ value: nextProps.value });
    }
  }

  onChange = value => {
    const { onChange } = this.props;
    if (!('value' in this.props)) {
      this.setState({ value });
    }
    if (onChange) {
      onChange(value);
    }
  };

  onSelectAll = checked => {
    let checkedTags = [];
    if (checked) {
      checkedTags = this.getAllTags();
    }
    this.onChange(checkedTags);
  };

  getAllTags() {
    let { children } = this.props;
    children = React.Children.toArray(children);
    const checkedTags = children
      .filter(child => this.isTagSelectOption(child))
      .map(child => child.props.value);
    return checkedTags || [];
  }

  handleTagChange = (value, checked) => {
    const { value: v } = this.state;
    const checkedTags = [...v];

    const index = checkedTags.indexOf(value);
    if (checked && index === -1) {
      checkedTags.push(value);
    } else if (!checked && index > -1) {
      checkedTags.splice(index, 1);
    }
    this.onChange(checkedTags);
  };

  handleExpand = () => {
    const { expand } = this.state;
    this.setState({
      expand: !expand,
    });
  };

  isTagSelectOption = node =>
    node &&
    node.type &&
    (node.type.isTagSelectOption || node.type.displayName === 'TagSelectOption');

  render() {
    const { value, expand } = this.state;
    const { children, className, style, expandable } = this.props;
    const allTagsLength = this.getAllTags().length;
    const checkedAll = allTagsLength !== 0 && allTagsLength === value.length;

    const cls = classNames(styles.tagSelect, className, {
      [styles.hasExpandTag]: expandable,
      [styles.expanded]: expand,
    });
    return (
      <div className={cls} style={style}>
        <CheckableTag checked={checkedAll} key="tag-select-__all__" onChange={this.onSelectAll}>
          {intl.get('hzero.common.scope.all').d('全部')}
        </CheckableTag>
        {value &&
          React.Children.map(children, child => {
            if (this.isTagSelectOption(child)) {
              return React.cloneElement(child, {
                key: `tag-select-${child.props.value}`,
                value: child.props.value,
                checked: value.indexOf(child.props.value) > -1,
                onChange: this.handleTagChange,
              });
            }
            return child;
          })}
        {expandable && (
          <a className={styles.trigger} onClick={this.handleExpand}>
            {expand ? (
              intl.get('hzero.common.button.up').d('收起')
            ) : (
              <Icon type={expand ? 'up' : 'down'} />
            )}
          </a>
        )}
      </div>
    );
  }
}

TagSelect.Option = TagSelectOption;

export default TagSelect;
