/**
 * 语言切换
 */
import React from 'react';
import { Select } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';

class DefaultLanguageSelect extends React.PureComponent {
  componentDidMount() {
    const { querySupportLanguage } = this.props;
    querySupportLanguage();
  }

  @Bind()
  handleLanguageSelectChange(language) {
    const { onUpdateDefaultLanguage } = this.props;
    Promise.all([onUpdateDefaultLanguage({ language })]).then(() => {
      window.location.reload();
    });
  }

  render() {
    const { language, supportLanguage = [], loading = false, className } = this.props;
    return (
      <Select
        size="small"
        className={classNames('select-no-border', 'default-language-select', className)}
        value={language}
        onChange={this.handleLanguageSelectChange}
        disabled={loading}
      >
        {supportLanguage.map((locale) => (
          <Select.Option
            key={locale.code}
            value={locale.code}
            className="hzero-normal-header-container-language"
          >
            {locale.name}
          </Select.Option>
        ))}
      </Select>
    );
  }
}

export default connect(
  ({ global = {}, loading = { effects: {} } }) => ({
    supportLanguage: global.supportLanguage, // 可供切换的语言
    language: global.language, // 当前的语言
    loading:
      loading.effects['global/changeLanguage'] ||
      loading.effects['global/updateDefaultLanguage'] ||
      loading.effects['global/querySupportLanguage'],
  }),
  (dispatch) => ({
    onChangeLanguage: (payload) =>
      dispatch({
        type: 'global/changeLanguage',
        payload,
      }),
    onUpdateDefaultLanguage: (payload) =>
      dispatch({
        type: 'global/updateDefaultLanguage',
        payload,
      }),
    querySupportLanguage: () =>
      dispatch({
        type: 'global/querySupportLanguage',
      }),
  })
)(DefaultLanguageSelect);
