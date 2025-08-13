import React from 'react';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';
import { Input, Popover, Pagination, Button, Icon } from 'hzero-ui';

import { getEnvConfig } from 'utils/iocUtils';

import Icons from '../Icons';
import iconfont from '../Icons/iconfont.css';
import { HZERO_ORIGIN_ICON_NAME } from '../Icons/Status';
import styles from './index.less';

export default class IconsPicker extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedIcon: props.value,
      pickerVisible: false,
      icons: [],
      totalIcons: [],
      defaultPageSize: 30,
    };
    this.config = getEnvConfig();
  }

  componentDidMount() {
    this.loadAllIconsAsync();
  }

  @Bind()
  async loadAllIconsAsync(iconsPath = 'iconfont.css') {
    const { defaultPageSize = 30 } = this.state;
    const { CUSTOMIZE_ICON_NAME } = this.config;

    await import(`@/assets/icons/${iconsPath}`)
      .then((m1 = {}) => {
        const cssObj = m1.default || {};
        const icons = Object.keys(Object.assign(iconfont, cssObj))
          .map((item) =>
            item.replace(`${HZERO_ORIGIN_ICON_NAME}-`, '').replace(`${CUSTOMIZE_ICON_NAME}-`, '')
          )
          .filter((key) => key !== CUSTOMIZE_ICON_NAME && key !== HZERO_ORIGIN_ICON_NAME);
        const size = defaultPageSize < icons.length ? defaultPageSize : icons.length;
        this.setState({ totalIcons: icons, icons: icons.slice(0, size) });
      })
      .catch(() => {
        const icons = Object.keys(iconfont)
          .map((item) =>
            item.replace(`${HZERO_ORIGIN_ICON_NAME}-`, '').replace(`${CUSTOMIZE_ICON_NAME}-`, '')
          )
          .filter((key) => key !== CUSTOMIZE_ICON_NAME && key !== HZERO_ORIGIN_ICON_NAME);
        const size = defaultPageSize < icons.length ? defaultPageSize : icons.length;
        this.setState({ totalIcons: icons, icons: icons.slice(0, size) });
      });
  }

  handlePickerVisible(visible) {
    this.setState({ pickerVisible: visible });
  }

  selectIcon(icon) {
    const { form, field: fieldName } = this.props;
    const { totalIcons = [] } = this.state;
    if (form && fieldName) {
      form.setFieldsValue({ [fieldName]: icon });
    }
    this.setState({ selectedIcon: icon, pickerVisible: false, totalIcons });
  }

  handleChange({ target: { value } }) {
    const { totalIcons = [], defaultPageSize } = this.state;
    const size = defaultPageSize < totalIcons.length ? defaultPageSize : totalIcons.length;
    if (value) {
      const filterList = totalIcons.filter((icon) => icon.includes(value));
      this.setState({ icons: filterList, selectedIcon: value });
    } else {
      this.setState({ icons: totalIcons.slice(0, size), selectedIcon: '' });
    }
  }

  handleClear() {
    const { form, field: fieldName } = this.props;
    const { totalIcons = [], icons = [], defaultPageSize } = this.state;
    const size = defaultPageSize < icons.length ? defaultPageSize : icons.length;
    if (form && fieldName) {
      form.setFieldsValue({ [fieldName]: '' });
    }
    this.setState({ pickerVisible: false, selectedIcon: '', icons: totalIcons.slice(0, size) });
  }

  handlePagination(page, pageSize) {
    const { totalIcons = [] } = this.state;
    const size = pageSize * (page - 1);
    const pagIcons =
      size + pageSize >= totalIcons.length
        ? totalIcons.slice(size, totalIcons.length)
        : totalIcons.slice(size, size + pageSize);
    this.setState({ icons: pagIcons });
  }

  render() {
    const { isButton = false, allowClear = false } = this.props;
    const {
      selectedIcon,
      pickerVisible,
      totalIcons = [],
      icons = [],
      defaultPageSize,
    } = this.state;

    const suffix = (
      <>
        {allowClear && selectedIcon && (
          <Icon type="close" className="icons-picker-clear" onClick={this.handleClear.bind(this)} />
        )}
        <Icon type="scan" />
      </>
    );

    return (
      <>
        <Popover
          visible={pickerVisible}
          onVisibleChange={this.handlePickerVisible.bind(this)}
          trigger="click"
          placement="bottom"
          content={
            <>
              <div
                className={classNames({
                  [styles['icons-picker-wrapper']]: true,
                })}
              >
                {icons.map((icon) => (
                  <div
                    className={classNames({
                      [styles.icons]: true,
                    })}
                    key={icon}
                  >
                    <Icons type={icon} size={18} onClick={this.selectIcon.bind(this, icon)} />
                  </div>
                ))}
              </div>
              <Pagination
                simple
                style={{
                  display: 'flex',
                  justifyContent: 'flex-end',
                  marginTop: '10px',
                }}
                defaultPageSize={defaultPageSize}
                defaultCurrent={1}
                total={totalIcons.length}
                onChange={this.handlePagination.bind(this)}
              />
            </>
          }
        >
          {isButton ? (
            <Button className={styles['icon-picker-button']}>
              {selectedIcon ? (
                <Icons type={selectedIcon} size={18} />
              ) : (
                <Icon type="scan" style={{ fontSize: '18px' }} />
              )}
            </Button>
          ) : (
            <Input
              clearButton
              className={styles['icon-picker']}
              value={selectedIcon}
              prefix={selectedIcon && <Icons type={selectedIcon} size={18} />}
              suffix={suffix}
              onChange={this.handleChange.bind(this)}
            />
          )}
        </Popover>
      </>
    );
  }
}
