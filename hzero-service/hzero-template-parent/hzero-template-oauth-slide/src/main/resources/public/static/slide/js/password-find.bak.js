const {Input, Button, Form, Icon} = window['choerodon-ui'];

// const server = 'http://api.staging.saas.hand-china.com'; //本地测试的时候打开此注释
const server = '';
const keyStr = 'ABCDEFGHIJKLMNOP' + 'QRSTUVWXYZabcdef' + 'ghijklmnopqrstuv'
    + 'wxyz0123456789+/' + '=';
const formItemLayout = {
  labelCol: {
    xs: {span: 24},
    sm: {span: 100},
  },
  wrapperCol: {
    xs: {span: 24},
    sm: {span: 9},
  },
};
const FormItem = Form.Item;

class App extends window.React.Component {
  state = {
    // 邮箱名
    currentUsername: '',
    step: 1,
    account: {},
    currentVCode: '',
    vCode: {},
    confirmDirty: false,
    passwdPolicy: {},
    policyPassed: false,
    passwdCheckedResult: '',
    errorMsg: '',
    errorState: false,
    captchaCD: 0,
  };

  // 验证码 倒计时 timer
  timer;

  componentDidMount() {
    this.setState({
      currentUsername: '',
      currentVCode: '',
    });
  }

  handleCaptchaButtonClick = () => {
    const {currentUsername, captchaCD} = this.state;
    if (currentUsername === '') {
      this.setState({
        account: {
          ...this.validateAccount(false),
        },
      });
      return;
    }
    this.clearTimer();
    this.setState({
      captchaCD: 60
    }, () => {
      this.timer = setInterval(() => {
        this.setState({
          captchaCD: this.state.captchaCD - 1,
        }, () => {
          if (this.state.captchaCD <= 0)
            this.clearTimer();
        });
      }, 1000);
    });

    $.post(`${server}/oauth/password/check_disable?emailAddress=${currentUsername}`,
        (results) => {
          this.setState({
            account: {
              ...this.validateAccount(results),
            },
          });
          if (results.success === true) {
            $.post(
                `${server}/oauth/password/send?emailAddress=${currentUsername}`,
                (results2) => {
                  this.setState({
                    account: {
                      ...this.validateAccount(results2),
                    },
                  });
            });
          }
    });
  };

  clearTimer = () => {
    this.setState({
      captchaCD: 0,
    });
    clearInterval(this.timer);
  };

  handleValueChange = (e) => {
    this.setState({
      currentUsername: e.target.value,
    });
  };

  handleCodeChange = (e) => {
    this.setState({
      currentVCode: e.target.value,
    });
  };

  componentDidUpdate() {

  }

  validateAccount = (results) => {
    const {captchaCD: CD} = this.state;
    if (!results) {
      return {
        validateStatus: 'error',
        errorMsg: '请输入用户邮箱',
      };
    }
    if (results.success && results.user) {
      this.setState({
        captchaCD: 60,
      }, () => {
        this.timer = setInterval(() => {
          this.setState({
            captchaCD: this.state.captchaCD - 1,
          }, () => {
            if (this.state.captchaCD <= 0)
              this.clearTimer();
          });
        }, 1000);
      });
      return {
        validateStatus: 'success',
        errorMsg: '验证码发送成功',
      };
    }
    return {
      validateStatus: 'error',
      errorMsg: results.msg,
    };
  };

  validateCode = (results) => {
    if (!results) {
      return {
        validateStatus: 'error',
        errorMsg: '',
      };
    }
    if (results.success) {
      this.setState({
        step: 2,
      });
      return {
        validateStatus: 'success',
        errorMsg: 'passed',
      };
    }
    return {
      validateStatus: 'error',
      errorMsg: '验证码错误',
    };
  };
  checkPassword = (passwordPolicy, value, userName) => {
    let compareResult = '';
    if (passwordPolicy) {
      const {
        enablePassword: check, minLength, maxLength,
        uppercaseCount: upcount, specialCharCount: spcount,
        lowercaseCount: lowcount, notUsername: notEqualsUsername,
        regularExpression: regexCheck, digitsCount,
      } = passwordPolicy;
      if (value && (check)) {
        let len = 0;
        let rs = '';
        let sp;
        let up = 0;
        let low = 0;
        let numLen = 0;
        for (let i = 0; i < value.length; i += 1) {
          const a = value.charAt(i);
          if (a.match(/[^\x00-\xff]/ig) != null) {
            len += 2;
          } else {
            len += 1;
          }
        }
        const pattern = new RegExp('[-~`@#$%^&*_=+|/()<>,.;:!]');
        for (let i = 0; i < value.length; i += 1) {
          rs += value.substr(i, 1).replace(pattern, '');
          sp = value.length - rs.length;
        }
        if (/[\d]/.test(value)) {
          const num = value.match(/\d/g);
          numLen = num ? num.length : 0;
        }
        if (/[A-Z]/i.test(value)) {
          const ups = value.match(/[A-Z]/g);
          up = ups ? ups.length : 0;
        }
        if (/[a-z]/i.test(value)) {
          const lows = value.match(/[a-z]/g);
          low = lows ? lows.length : 0;
        }
        if (minLength && (len < minLength)) {
          compareResult = `密码长度至少为${minLength}`;
        } else if (maxLength && (len > maxLength)) {
          compareResult = `密码长度最多为${maxLength}`;
        } else if (upcount && (up < upcount)) {
          compareResult = `大写字母至少为${upcount}`;
        } else if (lowcount && (low < lowcount)) {
          compareResult = `小写字母至少为${lowcount}`;
        } else if (notEqualsUsername && value === userName) {
          compareResult = '密码不能与账号相同';
        } else if (digitsCount && (numLen < digitsCount)) {
          compareResult = `数字至少为${digitsCount}个`;
        } else if (spcount && (sp < spcount)) {
          compareResult = `特殊字符至少为${spcount}`;
        } else if (regexCheck) {
          const regex = new RegExp(regexCheck);
          if (!regex.test(value)) {
            compareResult = '正则不匹配';
          }
        }
      }
    }
    return compareResult;
  };

  handleButtonClick = () => {
    const {form} = this.props;
    const {step, currentUsername, currentVCode, userId, policyPassed} = this.state;
    form.validateFields((err, fields) => {
      if(!err) {
        if (step === 1) {
          $.post(
              `${server}/oauth/password/check?emailAddress=${currentUsername}&captcha=${currentVCode}`,
              (results) => {
                this.setState({
                  vCode: {
                    ...this.validateCode(results),
                  },
                  passwdPolicy: results.passwordPolicyDO,
                  userId: results.user && results.user.id,
                  loginName: results.user && results.user.loginName,
                });
              });
        }
        if (step === 2 && form.getFieldValue('password') ===
            form.getFieldValue('password1') && policyPassed) {
          $.post(
              `${server}/oauth/password/reset?userId=${userId}&emailAddress=${currentUsername}&captcha=${currentVCode}&password=${form.getFieldValue(
                  'password')}&password1=${form.getFieldValue('password1')}`,
              (results) => {
                if (results && results.success === true) {
                  this.setState({
                    step: 3,
                    password: form.getFieldValue('password'),
                  });
                } else if (results.success === false && results.msg ===
                    'error.password.policy.notRecent') {
                  this.setState({
                    errorMsg: '与近期密码相同',
                    errorState: true,
                  });
                  form.validateFields(['password'], {force: true});
                }
              });
        }
        if (step === 3) {
          const {loginName} = this.state;
          // const encodePasswd = this.encode(password);
          // $.post(`${server}/oauth/login?username=${currentUsername}&password=${encodePasswd}`)
          window.location.href = `/oauth/login?username=${loginName}`;
        }
      }
    })
  };

  encode = (password) => {
    var output = '';
    var chr1, chr2, chr3 = '';
    var enc1, enc2, enc3, enc4 = '';
    var i = 0;
    do {
      chr1 = password.charCodeAt(i++);
      chr2 = password.charCodeAt(i++);
      chr3 = password.charCodeAt(i++);
      enc1 = chr1 >> 2;
      enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
      enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
      enc4 = chr3 & 63;
      if (isNaN(chr2)) {
        enc3 = enc4 = 64;
      } else if (isNaN(chr3)) {
        enc4 = 64;
      }
      output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2)
          + keyStr.charAt(enc3) + keyStr.charAt(enc4);
      chr1 = chr2 = chr3 = '';
      enc1 = enc2 = enc3 = enc4 = '';
    } while (i < password.length);
    return output;
  };

  compareToFirstPassword = (rule, value, callback) => {
    const form = this.props.form;
    if (value && value !== form.getFieldValue('password')) {
      callback('您输入的密码与确认密码不一致!');
    } else {
      callback();
    }
  };

  validateToNextPassword = (rule, value, callback) => {
    const form = this.props.form;
    const {passwdPolicy, currentUsername, errorState, errorMsg} = this.state;
    let checkPasswdMsg = this.checkPassword(passwdPolicy, value,
        currentUsername);
    if (value && this.state.confirmDirty) {
      form.validateFields(['password1'], {force: true});
    }
    if (checkPasswdMsg || errorState) {
      this.setState({
        policyPassed: false,
        errorState: false,
      });
      callback(checkPasswdMsg ? checkPasswdMsg : errorMsg);
    } else {
      this.setState({
        policyPassed: true,
      });
      callback();
    }
  };

  handleConfirmBlur = (e) => {
    const value = e.target.value;
    this.setState({confirmDirty: this.state.confirmDirty || !!value});
  };

  renderStep1 = () => {
    const {form} = this.props;
    const {account, vCode, captchaCD} = this.state;
    const {getFieldDecorator} = form;
    return (
        <div>
          <span className="loginSpan">忘记密码</span>
          <Form layout="vertical" className="form-vertical login-form">
            <FormItem
                {...formItemLayout}
                validateStatus={account.validateStatus}
                help={account.errorMsg}
            >
              {getFieldDecorator('username', {
                rules: [
                  {
                    required: true,
                    message: '请输入邮箱',
                  }],
              })(
                  <Input
                      prefix={
                        <Icon
                            type="mail_outline"
                            style={{color: '#9f9f9f'}}
                        />
                      }
                      autoFocus
                      autoComplete="off"
                      label="登录邮箱"
                      name="username"
                      id="username"
                      onChange={e => this.handleValueChange(e)}
                      placeholder="请输入邮箱"
                      value={this.state.currentUsername}
                  />,
              )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                validateStatus={vCode.validateStatus}
                help={vCode.errorMsg}
            >
              {getFieldDecorator('captchaInput', {
                rules: [
                  {
                    required: true,
                    message: '请输入验证码',
                  }],
              })(
                  <div>
                    <Input
                        prefix={
                          <Icon
                              type="password"
                              style={{color: '#9f9f9f'}}
                          />
                        }
                        type="text"
                        style={{width: '265px'}}
                        autoComplete="off"
                        label="验证码"
                        id="captchaInput"
                        onChange={e => this.handleCodeChange(e)}
                        placeholder="请输入验证码"
                        value={this.state.currentVCode}
                    />
                    <Button
                        type="primary"
                        funcType="raised"
                        onClick={this.handleCaptchaButtonClick}
                        disabled={captchaCD > 0}
                        style={{
                          float: 'right'
                        }}
                    >
                      {captchaCD === 0 ? '获取验证码' : `${captchaCD}秒后重试`}
                    </Button>
                  </div>,
              )}
            </FormItem>
            <FormItem style={{marginTop: '60px'}}>
              <a className="back-to-login" href="/oauth/login"
                 style={{float: 'left'}}>返回登录</a>
              <Button
                  className="btn"
                  onClick={this.handleButtonClick}
                  loading={this.state.loading}
                  style={{
                    width: '150px',
                    float: 'right',
                    paddingTop: '4px',
                  }}
              >
                <span>下一步</span>
              </Button>
            </FormItem>
          </Form>
        </div>
    );
  };

  renderStep2 = () => {
    const {form} = this.props;
    const {getFieldDecorator} = form;
    return (
        <div>
          <span className="loginSpan">忘记密码</span>
          <Form layout="vertical" className="form-vertical login-form">
            <FormItem
                {...formItemLayout}
                label="新密码"
            >
              {getFieldDecorator('password', {
                rules: [
                  {
                    required: true, message: '请输入密码',
                  },
                  {
                    pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)(?![a-z]+$)(?![!@#$%^&*=]+$)[0-9A-Za-z!@#$%^&*=]{6,30}$/,
                    message: '至少包含数字/字母/字符2种组合,长度为6-30个字符',
                  },
                  {
                    validator: this.validateToNextPassword,
                  }
                  ],
              })(
                  <Input showPasswordEye label="新密码" type="password"/>,
              )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="确认密码"
            >
              {getFieldDecorator('password1', {
                rules: [
                  {
                    required: true, message: '请确认!',
                  }, {
                    validator: this.compareToFirstPassword,
                  }],
              })(
                  <Input showPasswordEye label="确认新密码" type="password"
                         onBlur={this.handleConfirmBlur}/>,
              )}
            </FormItem>
            <Button className="btn" onClick={this.handleButtonClick}
                    loading={this.state.loading}
                    style={{
                      paddingTop: '4px',
                      marginTop: '38px',
                    }}><span>下一步</span></Button>
          </Form>
        </div>
    );
  };

  renderStep3 = () => {
    const {loginName} = this.state;
    return (
        <div>
          <div className="congratulation"><Icon type="done"
                                                style={{
                                                  fontSize: 30,
                                                  color: '#3F51B5',
                                                  marginRight: '23.8px',
                                                }}/>恭喜
          </div>
          <div
              className="change-password-success">{`您的账号“${loginName}”重置密码成功`}</div>
          <Button className="btn" onClick={this.handleButtonClick}
                  loading={this.state.loading}
                  style={{
                    paddingTop: '4px',
                    marginTop: '80px',
                  }}><span>直接登录</span></Button>
        </div>
    );
  };

  render() {
    const {step} = this.state;
    switch (step) {
      case 1:
        return this.renderStep1();
      case 2:
        return this.renderStep2();
      case 3:
        return this.renderStep3();
    }
  }
}

App = Form.create({})(App);

ReactDOM.render(
    <App/>,
    document.getElementById('app'));