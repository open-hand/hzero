$(document).ready(function () {
  var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
  if (typeof Object.assign != 'function') {
    Object.assign = function (target) {
      'use strict';
      if (target == null) {
        throw new TypeError('Cannot convert undefined or null to object');
      }

      target = Object(target);
      for (var index = 1; index < arguments.length; index++) {
        var source = arguments[index];
        if (source != null) {
          for (var key in source) {
            if (Object.prototype.hasOwnProperty.call(source, key)) {
              target[key] = source[key];
            }
          }
        }
      }
      return target;
    };
  }
  var message = {
    success: function success(config) {
      notification(Object.assign({
        placement: 'bottomRight',
        duration: 2,
        message: '操作成功',
        type: 'success'
      }, config));
    },
    error: function error(config) {
      notification(Object.assign({
        placement: 'bottomRight',
        duration: 2,
        message: '操作失败',
        type: 'error',
      }, config));
    }
  };

  function notification(params) {
    var message = params.message,
      duration = params.duration,
      type = params.type;
    var node = document.createElement("div");
    node.innerHTML = "<div class=\"login-alert\" role=\"alert\">\n        <span class=\"glyphicon ".concat(type == "success" ? 'glyphicon-ok' : 'glyphicon-exclamation-sign', "  login-notification-").concat(type, "\"></span>\n        <div class=\"login-notification-content\">").concat(message, "</div>\n        <span class=\"glyphicon glyphicon-remove\"></span>\n      </div>");
    $(".login-notification").append(node);
    setTimeout(function () {

      try {
        node.remove(node)
      } catch (err) {
        // 兼容ie									               
        node.removeNode(true);
      }
    }, duration * 1000);
  }

  $(document).on("click", ".login-alert .glyphicon-remove", function (e) {
    try {
      e.target.parentNode.remove();
    } catch (err) {
      // 兼容ie	
      e.target.parentNode.removeNode(true);
    }
  });

  if ($('#username').val() == 'null') {
    $('#username').val('')
  }

  if ($('#phone').val() == 'null') {
    $('#phone').val('')
  }

  function setLocalStorage(key, value) {
    localStorage.setItem('oauth-login' + key, JSON.stringify(value));
  }

  function getLocalStorage(key) {
    var value = localStorage.getItem('oauth-login' + key);
    return value ? JSON.parse(value) : undefined;
  }

  function initValidate() {
    $('#loginFormAccount').validate({
      errorPlacement: function errorPlacement(error, element) {
        error.addClass("has-error has-error-msg");
        error.appendTo(element.parent());
      },
      onkeyup: false,
      onclick: false,
      onfocusout: false,
      rules: {
        username: "required",
        password: "required",
        captcha: "required"
      },
      messages: {
        username: storeData.initData.userNameMessage,
        password: storeData.initData.passwordMessage,
        captcha: storeData.initData.captchaMessage
      }
    });
    $('#loginFormPhone').validate({
      errorPlacement: function errorPlacement(error, element) {
        error.addClass("has-error has-error-msg");
        error.appendTo(element.parent());
      },
      onfocusout: false,
      onkeyup: false,
      onclick: false,
      rules: {
        phone: "required",
        phoneCaptcha: "required"
      },
      messages: {
        phone: storeData.initData.phoneMsg,
        phoneCaptcha: storeData.initData.captchaMessage
      }
    });
  }

  function validate(selector, form) {
    var flag = $(form).validate().element($(selector));

    if (flag) {
      $(selector).parents(".login-input-content").removeClass("has-error");
    } else {
      $(selector).parents(".login-input-content").addClass("has-error");
    }

    return flag;
  }
  /**
   * 根据传递进来的数据 初始化 store
   */


  function initInitialData() {
    // 获取验证码的地址
    storeData.loginAccount.captchaGetUrl = storeData.initData.captchaGetUrl; // 是否需要验证码

    storeData.loginAccount.isNeedCaptcha = storeData.initData.isNeedCaptcha || false; // 支持的语言;

    var nowDate = new Date().getTime();
    var captchaDate = getLocalStorage('captchaTime-date', new Date().getTime()) || nowDate;
    var captchaTime = getLocalStorage('captchaTime') || 0;
    storeData.loginPhone.captchaTime = Math.floor(captchaTime - (nowDate - captchaDate) / 1000);
    startLoginPhoneCaptchaTime();
    storeData.loginPhone.captchaKey = getLocalStorage('captchaKey');
  } // 由于 这边只会有一个组件的实例, 且 babel 转化的 class 有问题, 所以state放在这边


  var storeData = {
    initData: {},
    // 初始值
    rootEl: null,
    // 挂载的节点
    loginAccount: {},
    // 账号登录的状态
    loginPhone: {},
    // 手机登录的状态
    backValidation: false // 后端验证 用户名不存在

  };
  $(function () {
    var data = {};
    var $templateData = $('#templateData');
    data.logoSrc = $templateData.data('') || '/oauth/static/main/img/logo.png';
    data.captchaGetUrl = $templateData.data('captchageturl'); // 公钥
    data.publicKey = $templateData.data('publickey'); // need access after load
    data.isNeedCaptcha = $templateData.data('isneedcaptcha');
    data.userNameMessage = $templateData.data('usernamemessage');
    data.passwordMessage = $templateData.data('passwordmessage');
    data.captchaMessage = $templateData.data('captchamessage');
    data.captchaKeyMsg = $templateData.data('captchakeymsg');
    data.captchaLoadErrgMsg = $templateData.data('captchaloaderrgmsg');
    data.phoneMsg = $templateData.data('phonemsg');
    data.captchaLoadingMsg = $templateData.data('captchaloadingmsg');
    data.accountEncrypt = $templateData.data('accountencrypt') || false;
    data.passwordEncrypt = $templateData.data('passwordencrypt')|| false;
    storeData.initData = data;
    storeData.rootEl = $('#app').get(0);
    initInitialData();
    initValidate();
  });

  function toggleLook() {
    $(".look").toggleClass("glyphicon-eye-open");
    $(".look").toggleClass("glyphicon-eye-close");
  }

  $(".look").mousedown(function () {
    $('#password').attr("type", "text");
    toggleLook();
  });
  $(".look").mouseup(function () {
    $('#password').attr("type", "password");
    toggleLook();
  });
  $(".look").mouseout(function () {
    if ($(".look").hasClass("glyphicon-eye-open")) {
      $('#password').attr("type", "password");
      toggleLook();
    }
  });
  $(".lang-icon").click(function (e) {
    var newRegex = /\/oauth\/static\/main\/img\/(\S*)\.png/;
    var l = e.target.src.match(newRegex)[1];
    $.ajax({
      url: '/oauth/login/lang',
      type: 'post',
      dataType: 'json',
      data: {
        lang: l
      },
      success: function success(data) {
        if (data.success) {
          window.location.href = '/oauth/login';
        }
      }
    });
  }); // 先应急 后面改成bootstrap


  $(".login-tabs-tab-nav-icon>.glyphicon").click(function () {
    $(".login-tabs-tab-nav-icon").toggleClass("login-tabs-tab-nav-icon-active");
    $(".login-phone").toggleClass("login-tabs-tab-pane-active");
    $(".login-account").toggleClass("login-tabs-tab-pane-active");
  });
  $('.btn-raised').click(function (e) {
    var _this = $(this);

    var px = e.offsetX;
    var py = e.offsetY - 37;
    var id = parseInt(Math.random() * 1000);

    _this.append('<div class="water-btn-style" style="top:' + py + 'px;left:' + px + 'px;background:rgba(0, 0, 0, 0.14)' + '" id="wb_' + id + '"></div>');

    setTimeout(function () {

      try {
        _this.find('#wb_' + id).remove();
      } catch (err) {
        // 兼容ie	
        _this.find('#wb_' + id).removeNode(true);
      }
    }, 1000);
  });
  $('#username').keyup(function () {
    validate('#username', '#loginFormAccount');
  });
  $('#password').keyup(function () {
    var flag = validate('#password', '#loginFormAccount');
    if (flag) {
      $(".look").css('display', 'block');
    } else {
      $(".look").css('display', 'none');
    }
  });
  $('#captcha').keyup(function () {
    validate('#captcha', '#loginFormAccount');
  });
  $('#phone').keyup(function () {
    validate('#phone', '#loginFormPhone');
  });
  $('#phoneCaptcha').keyup(function () {
    validate('#phoneCaptcha', '#loginFormPhone');
  });
  $(".login-account-captcha").click(function () {
    updateCaptchaGetUrl();
  });
  $(".login-form-button-captcha").click(function () {
    var flag = validate('#phone', '#loginFormPhone');

    if (flag) {
      sendCaptcha(storeData.loginPhone.captchaKey);
    }

    return false;
  });
  $("#loginFormAccount").submit(function () {
    var flag = $("#loginFormAccount").valid();
    validate('#username', '#loginFormAccount');
    validate('#password', '#loginFormAccount');

    if (storeData.loginAccount.isNeedCaptcha) {
      validate('#captcha', '#loginFormAccount');
    }

    if (flag) {
      handleAccountLogin();
    }

    return false;
  });
  $("#loginFormPhone").submit(function () {
    var flag = $("#loginFormPhone").valid();
    validate('#phone', '#loginFormPhone');
    validate('#phoneCaptcha', '#loginFormPhone');

    if (flag) {
      handlePhoneLogin(storeData.loginPhone.captchaKey);
    }

    return false;
  });


  /**
   * 加密的密码
   * @param {String} password - 需要加密的密码
   */


  function encryptPwd(password) {
    /* 有公钥 使用 rsa 加密, 否则使用 md5 加密 */
    if (storeData.initData.publicKey) {
      // 初始化加密器
      var encrypt = new JSEncrypt(); // 设置公钥

      encrypt.setPublicKey(storeData.initData.publicKey); // 加密

      return encrypt.encrypt(password);
    } else {
      return password;
    }
  }

  function Throttle() {
    var time = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 200;
    var calling = false;
    var context;
    var args;
    var fn;

    function invokeSuccess() {
      calling = false;
    }

    return function (_fn) {
      fn = _fn;
      return function () {
        if (calling) {
          return;
        }

        calling = true;
        context = this;
        args = Array.prototype.slice.call(arguments);
        fn.apply(context, args);
        setTimeout(invokeSuccess, time);
      };
    };
  }
  /** login-account */

  /**
   * 更新账号登录的state
   * @param {Object} patchState
   */


  function updateLoginAccount(patchState) {
    var loginAccount = storeData.loginAccount;
    storeData.loginAccount = Object.assign({}, loginAccount, patchState);
  }
  /**
   * 更新验证码
   */


  function _updateCaptchaGetUrl() {
    $(".login-account-captcha-image").attr("src", storeData.initData.captchaGetUrl + '?t=' + new Date().getTime());
  }

  var updateCaptchaGetUrl = Throttle()(_updateCaptchaGetUrl);
  /**
   * @param {Object[]} params - 提交参数
   * @param {string} params.type - 输入框类型
   * @param {string} params.name - 表单名
   * @param {string} params.value - 表单值
   * @param {Object} formProps - 表单属性
   * @param {function} callback - 提交后的回调
   * 创建表单及 提交
   */

  function createFormAndSubmit() {
    var params = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : [];
    var formProps = arguments.length > 1 ? arguments[1] : undefined;
    var callback = arguments.length > 2 ? arguments[2] : undefined;
    var temp = document.createElement("form");
    Object.keys(formProps).forEach(function (key) {
      temp.setAttribute(key, formProps[key]);
    });
    temp.style.display = "none";

    if (params.length > 0) {
      for (var i = 0; i < params.length; i++) {
        var opt = document.createElement("input");
        opt.name = params[i].name;
        opt.type = params[i].type; //防止IE浏览器将null 自动转换为"null" 导致错误

        if (params[i].value !== null) {
          opt.value = params[i].value || '';
        }

        temp.appendChild(opt);
      }
    }

    document.body.appendChild(temp);

    function submitAndCallback() {
      temp.submit();
      callback();
    }
    /**
     * 销毁创建表单
     */


    function destroy() {
      document.removeChild(temp);
    }

    submitAndCallback();
    return {
      destroy: destroy
    };
  }
  /**
   * 登录行为触发
   * @param {Event} e - 表单提交事件
   * @param {Object} form - 查询表单
   */


  function _handleAccountLogin() {
    var t = $("#loginFormAccount").serializeArray();
    var d = {};
    $.each(t, function () {
      d[this.name] = this.value;
    });
    var username = d.username,
      password = d.password,
      captcha = d.captcha;
      if(storeData.initData.accountEncrypt) {
        var newUsername = encryptPwd($.trim(username));
      } else {
        var newUsername = $.trim(username);
      }
      if(storeData.initData.passwordEncrypt) {
        var newPassword = encryptPwd($.trim(password));
      } else {
        var newPassword = $.trim(password);
      }

    var _createFormAndSubmit = createFormAndSubmit([{
      name: 'username',
      value: newUsername,
      type: 'text'
    }, {
      name: 'password',
      value: newPassword,
      type: 'text'
    }, {
      name: 'captcha',
      value: captcha,
      type: 'text'
    }], {
      action: 'login',
      method: 'post',
      autocomplete: 'off'
    }, function () {
      if (destroy) {
        destroy();
      }
    }),
      destroy = _createFormAndSubmit.destroy;
  }

  var handleAccountLogin = Throttle()(_handleAccountLogin);
  /**
   * 更新手机号/邮箱登录的state
   * @param {Object} patchState
   */

  function updateLoginPhone(patchState) {
    var loginPhone = storeData.loginPhone;
    storeData.loginPhone = Object.assign({}, loginPhone, patchState);
  }
  /**
   * 登录行为触发
   * @param {Event} e - 表单提交事件
   * @param {Object} form - 查询表单
   * @param {String} captchaKey - 发送验证码之后的校验
   */


  function _handlePhoneLogin(captchaKey) {
    if (!captchaKey) {
      message.error({
        message: initData.captchaKeyMsg
      });
      return;
    }

    var t = $("#loginFormPhone").serializeArray();
    var d = {};
    $.each(t, function () {
      d[this.name] = this.value;
    });
    var phone = d.phone,
      phoneCaptcha = d.phoneCaptcha;
    var newPhone = $.trim(phone);
    var newCaptcha = $.trim(phoneCaptcha);

    var _createFormAndSubmit2 = createFormAndSubmit([{
      name: 'phone',
      value: newPhone,
      type: 'text'
    }, {
      name: 'captcha',
      value: newCaptcha,
      type: 'text'
    }, {
      name: 'captchaKey',
      value: captchaKey,
      type: 'text'
    }], {
      action: 'login/sms',
      method: 'post',
      autocomplete: 'off'
    }, function () {
      if (destroy) {
        destroy();
      }
    }),
      destroy = _createFormAndSubmit2.destroy;
  }

  var handlePhoneLogin = Throttle()(_handlePhoneLogin);
  /**
   * 手机登录 发送验证吗
   */

  function _sendCaptcha() {
    updateLoginPhone({
      captchaLoading: true
    });
    $(".login-form-button-captcha").attr("disabled", "disabled");
    $(".login-form-button-captcha").text("..." + storeData.initData.captchaLoadingMsg);
    var t = $("#loginFormPhone").serializeArray();
    var d = {};
    $.each(t, function () {
      d[this.name] = this.value;
    });
    var phone = d.phone;
    $.ajax('public/send-phone-captcha', {
      false: 'false',
      method: 'GET',
      data: {
        phone: phone
      },
      success: function success(data) {
        if (data.success) {
          message.success({
            message: data.msg || data.message
          });
          updateLoginPhone({
            captchaKey: data.captchaKey,
            captchaLoading: false,
            captchaTime: data.interval || 60
          });
          $(".login-form-button-captcha").removeAttr("disabled");
          $(".login-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
          setLocalStorage('captchaKey', data.captchaKey);
          setLocalStorage('captchaTime', data.interval || 60);
          setLocalStorage('captchaTime-date', new Date().getTime());
          startLoginPhoneCaptchaTime();
        } else {
          message.error({
            message: data.msg || data.message
          }); // 没有办法知道具体的 计时, 所以直接使用 60

          switch (data.code) {
            // case 'captcha.send.interval':
            default:
              var nextCaptchaTime = data.interval > 0 ? data.interval : 0;

              if ((getLocalStorage('captchaTime') || 0) == 0) {
                updateLoginPhone({
                  captchaLoading: false,
                  captchaTime: nextCaptchaTime
                });
                $(".login-form-button-captcha").removeAttr("disabled");
                $(".login-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
                setLocalStorage('captchaTime', nextCaptchaTime);
                setLocalStorage('captchaTime-date', new Date().getTime());
                clearInterval(loginPhoneCaptchaTimer);
                startLoginPhoneCaptchaTime();
              }

              break;
          }
        }
      },
      error: function error() {
        message.error({
          message: initData.captchaLoadErrgMsg
        });
        $(".login-form-button-captcha").removeAttr("disabled");
        $(".login-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
      }
    });
  }

  var sendCaptcha = Throttle()(_sendCaptcha);
  /**
   * 手机号发送验证码的计时器
   */

  var loginPhoneCaptchaTimer;
  /**
   * 定时更新 loginPhone 的state
   */

  function updateLoginPhoneCaptchaTime() {
    var loginPhone = storeData.loginPhone;
    var _loginPhone$captchaTi = loginPhone.captchaTime,
      captchaTime = _loginPhone$captchaTi === void 0 ? 0 : _loginPhone$captchaTi;

    if (captchaTime <= 0) {
      stopLoginPhoneCaptchaTime();
    } else {
      var newCaptchaTime = (captchaTime || 0) - 1;
      setLocalStorage('captchaTime', newCaptchaTime);
      setLocalStorage('captchaTime-date', new Date().getTime());
      updateLoginPhone({
        captchaTime: newCaptchaTime
      });
      $(".login-form-button-captcha").attr("disabled", "disabled");
      $(".login-form-button-captcha").text(captchaTime - 1 + 's');
    }
  }
  /**
   * 开始倒计时, 需要先更新 captchaTime
   */


  function startLoginPhoneCaptchaTime() {
    loginPhoneCaptchaTimer = setInterval(updateLoginPhoneCaptchaTime, 1000);
  }
  /**
   * 结束倒计时
   */


  function stopLoginPhoneCaptchaTime() {
    $(".login-form-button-captcha").removeAttr("disabled");
    $(".login-form-button-captcha").text(storeData.initData.captchaLoadingMsg);
    clearInterval(loginPhoneCaptchaTimer);
  }
});