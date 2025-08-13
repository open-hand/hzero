function _slicedToArray(arr, i) { return _arrayWithHoles(arr) || _iterableToArrayLimit(arr, i) || _nonIterableRest(); }

function _nonIterableRest() { throw new TypeError("Invalid attempt to destructure non-iterable instance"); }

function _iterableToArrayLimit(arr, i) { if (!(Symbol.iterator in Object(arr) || Object.prototype.toString.call(arr) === "[object Arguments]")) { return; } var _arr = []; var _n = true; var _d = false; var _e = undefined; try { for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) { _arr.push(_s.value); if (i && _arr.length === i) break; } } catch (err) { _d = true; _e = err; } finally { try { if (!_n && _i["return"] != null) _i["return"](); } finally { if (_d) throw _e; } } return _arr; }

function _arrayWithHoles(arr) { if (Array.isArray(arr)) return arr; }

$(document).ready(function () {
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
  var keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
  var globalData = {};
  var $templateData = $('#templateData');
  globalData.loginUrl = $templateData.data('loginurl') || '/oauth/static/main/img/logo.png';
  globalData.modifyPwd = $templateData.data('modifypwd');
  globalData.optionSucceeded = $templateData.data('optionsucceeded');
  globalData.optionFailed = $templateData.data('optionfailed');
  globalData.checkAccount = $templateData.data('checkaccount');
  globalData.resetPassword = $templateData.data('resetpassword');
  globalData.complete = $templateData.data('complete');
  globalData.validateAccountErr = $templateData.data('validateaccounterr');
  globalData.validateAccountFailed = $templateData.data('validateaccountfailed');
  globalData.userNameNotNull = $templateData.data('usernamenotnull');
  globalData.findAccount = $templateData.data('findaccount');
  globalData.captchaPlaceholder = $templateData.data('captchaplaceholder');
  globalData.captchaMessage = $templateData.data('captchamessage');
  globalData.cancel = $templateData.data('cancel');
  globalData.nextStep = $templateData.data('nextstep');
  globalData.preStep = $templateData.data('prestep');
  globalData.validatePwdRepeat = $templateData.data('validatepwdrepeat');
  globalData.captchaLoadErrgMsg = $templateData.data('captchaloaderrgmsg');
  globalData.getCaptcha = $templateData.data('getcaptcha');
  globalData.newPwdCanNotNul = $templateData.data('newpwdcannotnul');
  globalData.resetPwdErr = $templateData.data('resetpwderr');
  globalData.resetPwd = $templateData.data('resetpwd');
  globalData.pwdRepeatMsg = $templateData.data('pwdrepeatmsg');
  globalData.resetPwdSucceeded = $templateData.data('resetpwdsucceeded');
  globalData.loadingNow = $templateData.data('loadingnow');
  globalData.pwdRepeat = $templateData.data('pwdrepeat');
  globalData.copyright = $templateData.data('copyright') || 'Copyright © The HZERO Author®.All rights reserved.';
  globalData.publicKey = $templateData.data('publickey'); //  验证邮箱正则

  var REG_EMAIL = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/; //  验证手机号正则

  var REG_PHONE = /^134[0-8]\d{7}$|^13[^4]\d{8}$|^14[5-9]\d{8}$|^15[^4]\d{8}$|^16[6]\d{8}$|^17[0-8]\d{8}$|^18[\d]{9}$|^19[8,9]\d{8}$/; // 验证密码是否符合格式
  // const REG_PASSWORD = /^(?![0-9]+$)(?![a-zA-Z]+$)(?![a-z]+$)(?![!@#$%^&*=]+$)[0-9A-Za-z!@#$%^&*=]{6,30}$/;

  var message = {
    success: function success(config) {
      notification(Object.assign({
        placement: 'bottomRight',
        duration: 2,
        message: globalData.optionSucceeded,
        type: 'success',
      }, config));
    },
    error: function error(config) {
      notification(Object.assign({
        placement: 'bottomRight',
        duration: 2,
        message: globalData.optionFailed,
        type: 'error',
      }, config));
    }
  };

  function notification(params) {
    var message = params.message,
      duration = params.duration,
      type = params.type;
    var node = document.createElement("div");
    node.innerHTML = "<div class=\"password-alert\" role=\"alert\">\n    <span class=\"glyphicon ".concat(type == "success" ? 'glyphicon-ok' : 'glyphicon-exclamation-sign', "  password-notification-").concat(type, "\"></span>\n    <div class=\"password-notification-content\">").concat(message, "</div>\n    <span class=\"glyphicon glyphicon-remove\"></span>\n  </div>");
    $(".password-notification").append(node);
    setTimeout(function () {
      try {
        node.remove(node)
      } catch (err) {
        // 兼容ie									               
        node.removeNode(true);
      }
    }, duration * 1000);
  }

  $(document).on("click", ".password-alert .glyphicon-remove", function (e) {
    try {
      e.target.parentNode.remove();
    } catch (err) {
      // 兼容ie	
      e.target.parentNode.removeNode(true);
    }
  });

  function setLocalStorage(key, value) {
    localStorage.setItem('oauth-password_find' + key, JSON.stringify(value));
  }

  function getLocalStorage(key) {
    var value = localStorage.getItem('oauth-password_find' + key);
    return value ? JSON.parse(value) : undefined;
  }

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

  function validate(selector, form) {
    var flag = $(form).validate().element($(selector));

    if (flag) {
      $(selector).parents(".password-input-content").removeClass("has-error");
    } else {
      $(selector).parents(".password-input-content").addClass("has-error");
    }

    return flag;
  }

  function passwordValidate() {
    var _ref = getLocalStorage('passwordTipMsg') || {},
      digitsCount = _ref.digitsCount,
      lowercaseCount = _ref.lowercaseCount,
      maxLength = _ref.maxLength,
      minLength = _ref.minLength,
      notUsername = _ref.notUsername,
      specialCharCount = _ref.specialCharCount,
      uppercaseCount = _ref.uppercaseCount;

    var msg = '';
    var allFlag = false;
    var value = $("#password").val();
    var loginName = getLocalStorage('loginName');
    var node = document.createElement("div"); // node.className = "password-validate"

    if (minLength && maxLength) {
      var flag = value.length < minLength || value.length > maxLength;
      allFlag = !flag;

      if (!flag) {
        msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(minLength, "-").concat(maxLength, " ").concat(storeData.initData.validateLength, "</span>\n      </div>");
      } else {
        msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(minLength, "-").concat(maxLength, " ").concat(storeData.initData.validateLength, "</span>\n      </div>");
      }
    }

    if (lowercaseCount) {
      var regStr = [];

      for (var i = 0; i < lowercaseCount; i++) {
        regStr.push('([a-z].*)');
      }

      var lower = new RegExp("".concat(regStr.join('')));

      var _flag = lower.test(value);

      allFlag = _flag && allFlag;

      if (_flag) {
        msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(lowercaseCount, " ").concat(storeData.initData.validateLower, "</span>\n      </div>");
      } else {
        msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(lowercaseCount, " ").concat(storeData.initData.validateLower, "</span>\n      </div>");
      }
    }

    if (uppercaseCount) {
      var _regStr = [];

      for (var _i = 0; _i < uppercaseCount; _i++) {
        _regStr.push('([A-Z].*)');
      }

      var upper = new RegExp("".concat(_regStr.join('')));

      var _flag2 = upper.test(value);

      allFlag = _flag2 && allFlag;

      if (_flag2) {
        msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(uppercaseCount, " ").concat(storeData.initData.validateUpper, "</span>\n      </div>");
      } else {
        msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(uppercaseCount, " ").concat(storeData.initData.validateUpper, "</span>\n      </div>");
      }
    }

    if (notUsername === false) {
      var _flag3 = value === loginName;

      allFlag = !_flag3 && allFlag;

      if (!_flag3) {
        msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.validateUserName, "</span>\n      </div>");
      } else {
        msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.validateUserName, "</span>\n      </div>");
      }
    }

    if (digitsCount) {
      var _regStr2 = [];

      for (var _i2 = 0; _i2 < digitsCount; _i2++) {
        _regStr2.push('([0-9].*)');
      }

      var digits = new RegExp("".concat(_regStr2.join('')));

      var _flag4 = digits.test(value);

      allFlag = _flag4 && allFlag;

      if (_flag4) {
        msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(digitsCount, " ").concat(storeData.initData.validateDigit, "</span>\n      </div>");
      } else {
        msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(digitsCount, " ").concat(storeData.initData.validateDigit, "</span>\n      </div>");
      }
    }

    if (specialCharCount) {
      var _regStr3 = [];

      for (var _i3 = 0; _i3 < specialCharCount; _i3++) {
        _regStr3.push('([~`@#$%^&*/\\-_=+|/()<>,.;:!].*)');
      }

      var special = new RegExp("".concat(_regStr3.join('')));

      var _flag5 = special.test(value);

      allFlag = _flag5 && allFlag;

      if (_flag5) {
        msg += "<div class='password-validate-item' style=\"color: #52c41a\">\n        <i class=\"glyphicon glyphicon-ok\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(specialCharCount, " ").concat(storeData.initData.validateSpecial, "</span>\n      </div>");
      } else {
        msg += "<div class='password-validate-item' style=\"color: #f5222d\">\n        <i class=\"glyphicon glyphicon-remove\"></i>\n        <span>".concat(storeData.initData.atLeast, " ").concat(specialCharCount, " ").concat(storeData.initData.validateSpecial, "</span>\n      </div>");
      }
    }

    if (msg.length == 0 && !allFlag) {
      allFlag = true;
    }

    if (msg.length > 0 && !allFlag && value) {
      node.innerHTML = msg;
      $('.password-validate').html(node);
      $('.password-validate').addClass("password-validate-active");
    } else {
      $('.password-validate').html("<div></div>");
      $('.password-validate').removeClass("password-validate-active");
    }

    return allFlag;
  }

  function encryptMd5(password) {
    var output = "";
    var chr1,
      chr2,
      chr3 = "";
    var enc1,
      enc2,
      enc3,
      enc4 = "";
    var i = 0;

    do {
      chr1 = password.charCodeAt(i++);
      chr2 = password.charCodeAt(i++);
      chr3 = password.charCodeAt(i++);
      enc1 = chr1 >> 2;
      enc2 = (chr1 & 3) << 4 | chr2 >> 4;
      enc3 = (chr2 & 15) << 2 | chr3 >> 6;
      enc4 = chr3 & 63;

      if (isNaN(chr2)) {
        enc3 = enc4 = 64;
      } else if (isNaN(chr3)) {
        enc4 = 64;
      }

      output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2) + keyStr.charAt(enc3) + keyStr.charAt(enc4);
      chr1 = chr2 = chr3 = "";
      enc1 = enc2 = enc3 = enc4 = "";
    } while (i < password.length);

    return output;
  }

  function encryptPwd(password) {
    /* 有公钥 使用 rsa 加密, 否则使用 md5 加密 */
    if (globalData.publicKey) {
      // 初始化加密器
      var encrypt = new JSEncrypt(); // 设置公钥

      encrypt.setPublicKey(globalData.publicKey); // 加密

      return encrypt.encrypt(password);
    } else {
      return encryptMd5(password);
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

  function initValidate() {
    jQuery.validator.addMethod("isSame", function (value) {
      return value == $('#password').val();
    }, globalData.validatePwdRepeat);
    $('#formResetPassword').validate({
      errorPlacement: function errorPlacement(error, element) {
        error.addClass("has-error has-error-msg");
        error.appendTo(element.parent());
      },
      onkeyup: false,
      onclick: false,
      onfocusout: false,
      onsubmit: false,
      rules: {
        passwordCaptcha: "required",
        password: "required",
        passwordRepeat: {
          required: true,
          isSame: true
        }
      },
      messages: {
        passwordRepeat: {
          required: globalData.pwdRepeatMsg
        },
        password: globalData.newPwdCanNotNul,
        passwordCaptcha: globalData.captchaMessage
      }
    });
    jQuery.validator.addMethod("isAccount", function (value) {
      return REG_EMAIL.test(value) || REG_PHONE.test(value);
    }, globalData.validateAccountErr);
    $('#formConfirmAccount').validate({
      errorPlacement: function errorPlacement(error, element) {
        error.addClass("has-error has-error-msg");
        error.appendTo(element.parent());
      },
      onkeyup: false,
      onclick: false,
      onfocusout: false,
      onsubmit: false,
      rules: {
        account: {
          required: true,
          isAccount: true
        },
        captcha: "required"
      },
      messages: {
        account: {
          required: globalData.userNameNotNull
        },
        captcha: globalData.captchaMessage
      }
    });
  } // 由于 这边只会有一个组件的实例, 且 babel 转化的 class 有问题, 所以state放在这边


  var storeData = {
    initData: {},
    // 初始值
    rootEl: null,
    // 挂载的节点
    password: {
      steps: [{
        title: globalData.checkAccount
      }, {
        title: globalData.resetPassword
      }, {
        title: globalData.complete
      }],
      currentStep: 0
    } // 整体数据

  };
  $(".password-account-captcha").click(function () {
    updateCaptchaGetUrl();
  });
  $('#captcha').keyup(function () {
    validate('#captcha', '#formConfirmAccount');
  });
  $('#account').keyup(function () {
    validate('#account', '#formConfirmAccount');
  });
  $('#password').keyup(function () {
    if ($('#passwordRepeat').val()) {
      validate('#passwordRepeat', '#formResetPassword');
    }

    passwordValidate();
    var flag = validate('#password', '#formResetPassword');

    if (flag) {
      $(".look:first").css('display', 'block');
    } else {
      $(".look:first").css('display', 'none');
    }
  });
  $('#passwordRepeat').keyup(function () {
    validate('#passwordRepeat', '#formResetPassword');
    var flag = $('#passwordRepeat').val();

    if (flag) {
      $(".look:last").css('display', 'block');
    } else {
      $(".look:last").css('display', 'none');
    }
  });
  $('#passwordCaptcha').keyup(function () {
    validate('#passwordCaptcha', '#formResetPassword');
  });

  function toggleLook(selector) {
    $(selector).toggleClass("glyphicon-eye-open");
    $(selector).toggleClass("glyphicon-eye-close");
  }

  $(".look:first").mousedown(function () {
    $('#password').attr("type", "text");
    toggleLook(".look:first");
  });
  $(".look:first").mouseup(function () {
    $('#password').attr("type", "password");
    toggleLook(".look:first");
  });
  $(".look:first").mouseout(function () {
    if ($(".look:first").hasClass("glyphicon-eye-open")) {
      $('#password').attr("type", "password");
      toggleLook(".look:first");
    }
  });
  $(".look:last").mousedown(function () {
    $('#passwordRepeat').attr("type", "text");
    $(".look:last").toggleClass("glyphicon-eye-open");
  });
  $(".look:last").mouseup(function () {
    $('#passwordRepeat').attr("type", "password");
    $(".look:last").toggleClass("glyphicon-eye-open");
  });
  $(".look:last").mouseout(function () {
    if ($(".look:last").hasClass("glyphicon-eye-open")) {
      $('#passwordRepeat').attr("type", "password");
      $(".look:last").toggleClass("glyphicon-eye-open");
    }
  });
  $("#formConfirmAccount").submit(function (e) {
    return false;
  });
  $("#formResetPassword").submit(function (e) {
    return false;
  });
  $("#resetPasswordSuccess").submit(function (e) {
    return false;
  });
  $(".password-form-button-captcha").click(function () {
    sendCaptcha();
  });
  $(".password-btn-confirm-account").click(function (e) {
    var currentStep = storeData.password.currentStep;

    if (currentStep == 1) {
      var flag = $("#formResetPassword").valid();
      var allFlag = passwordValidate();
      validate('#password', '#formResetPassword');
      validate('#passwordCaptcha', '#formResetPassword');
      validate('#passwordRepeat', '#formResetPassword');

      if (flag && allFlag) {
        resetPassword();
      }
    } else {
      var _flag6 = $("#formConfirmAccount").valid();

      validate('#account', '#formConfirmAccount');
      validate('#captcha', '#formConfirmAccount');

      if (_flag6) {
        gotoNext();
      }
    }
  });
  $(".password-btn-cancel").click(function (e) {
    if (storeData.password.currentStep == 1) {
      backConfirmAccount();
    } else {
      backLoginPage();
    }
  });
  $(".button-login").click(function () {
    backLoginPage();
  });
  /**
   * 初始化 App 数据
   */

  function initInitialData() {
    // storeData.initData.logoSrc = '/oauth/static/main/img/logo.png'; // logo 地址
    storeData.initData.successImgSrc = '/oauth/static/main/img/illustrate_success.png'; // 成功图片地址

    storeData.initData.captchaGetUrl = '/oauth/password/captcha.jpg'; // 验证码 地址

    storeData.password.captchaGetUrl = "/oauth/password/captcha.jpg?t=".concat(new Date().getTime()); // 验证码 地址

    var nowDate = new Date().getTime();
    var captchaDate = getLocalStorage('captchaTime-date', new Date().getTime()) || nowDate;
    var captchaTime = getLocalStorage('captchaTime') || 0;
    storeData.password.captchaTime = Math.floor(captchaTime - (nowDate - captchaDate) / 1000);
    startCaptchaTime();
    storeData.password.captchaKey = getLocalStorage('captchaKey');
    var params = location.search.substr(1).split('&').reduce(function (prev, cur) {
      var _cur$split = cur.split('='),
        _cur$split2 = _slicedToArray(_cur$split, 2),
        name = _cur$split2[0],
        value = _cur$split2[1];

      prev[decodeURIComponent(name)] = decodeURIComponent(value);
      return prev;
    }, {});
    Object.assign(storeData.password, params); // 处理一些类型问题

    storeData.password.currentStep = +storeData.password.currentStep || 0;
  }
  /**
   * 更新找回密码的state
   * @param {Object} patchState
   */


  function updatePassword(patchState) {
    var password = storeData.password;
    var newPassword = Object.assign({}, password, patchState);
    storeData.password = newPassword;
  }
  /**
   * 更新验证码
   */


  function _updateCaptchaGetUrl() {
    $(".password-account-captcha-image").attr("src", storeData.initData.captchaGetUrl + '?t=' + new Date().getTime());
    updatePassword({
      captchaGetUrl: storeData.initData.captchaGetUrl + '?t=' + new Date().getTime()
    });
  }

  var updateCaptchaGetUrl = Throttle()(_updateCaptchaGetUrl);
  /**
   * 回退到登录页面
   */

  function backLoginPage() {
    window.location.href = storeData.initData.loginUrl + location.search.replace('?', "&");
  }
  /**
   * 下一步按钮点击
   */


  function gotoNext() {
    var t = $("#formConfirmAccount").serializeArray();
    var d = {};
    $.each(t, function () {
      d[this.name] = this.value;
    });
    var account = d.account,
      captcha = d.captcha;
    updatePassword({
      validateAccountLoading: true
    });
    $.ajax({
      url: '/oauth/password/next',
      type: 'get',
      dataType: 'json',
      data: {
        account: account,
        captcha: captcha
      },
      success: function success(data) {
        if (data.success) {
          sessionStorage.setItem('account_', account); //存储输入的账号
          // 跳转到 重置密码界面

          updatePassword({
            validateAccountLoading: false
          });

          if (data.data) {
            var _data$data = data.data,
              organizationId = _data$data.organizationId,
              loginName = _data$data.loginName;
            setLocalStorage('loginName', loginName);
            $.ajax({
              url: "/iam/v1/".concat(organizationId, "/password-policies/query"),
              type: 'get',
              success: function success(data) {
                if (data.failed == true) {
                  message.error({
                    message: data.message
                  });
                } else {
                  setLocalStorage('passwordTipMsg', data); // window.location.href = window.location.href += location.search+'&currentStep=1';

                  if (location.search) {
                    window.location.href = '/oauth/public/main/password_find.html' + location.search + '&currentStep=1';
                  } else {
                    window.location.href = '/oauth/public/main/password_find.html?currentStep=1';
                  }
                }
              },
              error: function error() { }
            });
          }
        } else {
          switch (data.code) {
            case 'error.account':
            case 'error.captcha':
              message.error({
                message: data.msg || data.message
              });
              updateCaptchaGetUrl();
              break;

            default:
              break;
          }
        }
      },
      error: function error() {
        message.error({
          message: globalData.validateAccountFailed
        });
        updatePassword({
          validateAccountLoading: false
        });
      }
    });
  }
  /**
   * 回退到确认账号界面
   */


  function backConfirmAccount() {
    window.location.href = '/oauth/public/main/password_find.html';
  }
  /**
   * 手机登录 发送验证吗
   */


  function _sendCaptcha() {
    $(".password-form-button-captcha").attr("disabled", "disabled");
    $(".password-form-button-captcha").text("..." + globalData.getCaptcha);
    $.ajax('/oauth/password/send-captcha', {
      false: 'false',
      method: 'GET',
      data: {businessScope:'oauth'},
      success: function success(data) {
        if (data.success) {
          message.success({
            message: data.msg || data.message
          });
          updatePassword({
            captchaKey: data.data,
            captchaLoading: false,
            captchaTime: data.interval || 60
          });
          $(".password-form-button-captcha").removeAttr("disabled");
          $(".password-form-button-captcha").text(globalData.getCaptcha);
          setLocalStorage('captchaKey', data.data);
          setLocalStorage('captchaTime', data.interval || 60);
          setLocalStorage('captchaTime-date', new Date().getTime());
          startCaptchaTime();
        } else {
          message.error({
            message: data.msg || data.message
          }); // 没有办法知道具体的 计时, 所以直接使用 60

          switch (data.code) {
            // case 'captcha.send.interval':
            default:
              var nextCaptchaTime = data.interval > 0 ? data.interval : 60;

              if ((getLocalStorage('captchaTime') || 0) == 0) {
                $(".password-form-button-captcha").removeAttr("disabled");
                $(".password-form-button-captcha").text(globalData.getCaptcha);
                updatePassword({
                  captchaLoading: false,
                  captchaTime: nextCaptchaTime
                });
                setLocalStorage('captchaTime', nextCaptchaTime);
                setLocalStorage('captchaTime-date', new Date().getTime());
                startCaptchaTime();
              }

              break;
          }
        }
      },
      error: function error() {
        message.error({
          message: globalData.captchaLoadErrgMsg
        });
        startCaptchaTime();
      }
    });
  }

  var sendCaptcha = Throttle()(_sendCaptcha);
  /**
   * 手机号发送验证码的计时器
   */

  var captchaTimer;
  /**
   * 定时更新 loginPhone 的state
   */

  function updateCaptchaTime() {
    var password = storeData.password;
    var _password$captchaTime = password.captchaTime,
      captchaTime = _password$captchaTime === void 0 ? 0 : _password$captchaTime;

    if (captchaTime <= 0) {
      stopCaptchaTime();
    } else {
      var newCaptchaTime = (captchaTime || 0) - 1;
      setLocalStorage('captchaTime', newCaptchaTime);
      setLocalStorage('captchaTime-date', new Date().getTime());
      updatePassword({
        captchaTime: newCaptchaTime
      });
      $(".password-form-button-captcha").attr("disabled", "disabled");
      $(".password-form-button-captcha").text(captchaTime - 1 + 's');
    }
  }
  /**
   * 开始倒计时, 需要先更新 captchaTime
   */


  function startCaptchaTime() {
    captchaTimer = setInterval(updateCaptchaTime, 1000);
  }
  /**
   * 结束倒计时
   */


  function stopCaptchaTime() {
    $(".password-form-button-captcha").removeAttr("disabled");
    $(".password-form-button-captcha").text(globalData.getCaptcha);
    clearInterval(captchaTimer);
  }
  /**
   * 重置密码
   * @param {Object} form
   * @param {String} captchaKey
   */


  function resetPassword() {
    var t = $("#formResetPassword").serializeArray();
    var d = {};
    $.each(t, function () {
      d[this.name] = this.value;
    });
    var password = d.password,
      captcha = d.passwordCaptcha;
    var captchaKey = storeData.password.captchaKey;
    var newPassword = encryptPwd($.trim(password));
    $.ajax({
      url: '/oauth/password/modify?businessScope=oauth',
      type: 'POST',
      dataType: 'json',
      data: {
        password: newPassword,
        captcha: captcha,
        captchaKey: captchaKey
      },
      success: function success(data) {
        if (data.success) {
          window.location.href = '/oauth/public/main/password_find.html?currentStep=2';
        } else {
          switch (data.code) {
            case 'error.invalid-captcha':
            case 'error.account-not-equals':
              backConfirmAccount();
              break;

            case 'captcha.validate.overdue': // 验证码过期

            default:
              message.error({
                message: data.msg || data.message
              });
              break;
          }
        }
      },
      error: function error(err) {
        message.error({
          message: globalData.optionFailed
        });
      }
    });
  }
  /**
   * 页面加载完成 执行 render 渲染页面
   */


  $(function () {
    var data = {};
    var $templateData = $('#templateData');
    data.loginUrl = $templateData.data('loginurl');
    data.modifyPwd = $templateData.data('modifypwd');
    data.optionSucceeded = $templateData.data('optionsucceeded');
    data.optionFailed = $templateData.data('optionfailed');
    data.checkAccount = $templateData.data('checkaccount');
    data.resetPassword = $templateData.data('resetpassword');
    data.complete = $templateData.data('complete');
    data.validateAccountErr = $templateData.data('validateaccounterr');
    data.validateAccountFailed = $templateData.data('validateaccountfailed');
    data.userNameNotNull = $templateData.data('usernamenotnull');
    data.findAccount = $templateData.data('findaccount');
    data.captchaPlaceholder = $templateData.data('captchaplaceholder');
    data.captchaMessage = $templateData.data('captchamessage');
    data.cancel = $templateData.data('cancel');
    data.nextStep = $templateData.data('nextstep');
    data.preStep = $templateData.data('prestep');
    data.validatePwdRepeat = $templateData.data('validatepwdrepeat');
    data.captchaLoadErrgMsg = $templateData.data('captchaloaderrgmsg');
    data.getCaptcha = $templateData.data('getcaptcha');
    data.newPwdCanNotNul = $templateData.data('newpwdcannotnul');
    data.resetPwdErr = $templateData.data('resetpwderr');
    data.resetPwd = $templateData.data('resetpwd');
    data.pwdRepeatMsg = $templateData.data('pwdrepeatmsg');
    data.resetPwdSucceeded = $templateData.data('resetpwdsucceeded');
    data.loadingNow = $templateData.data('loadingnow');
    data.pwdRepeat = $templateData.data('pwdrepeat');
    data.logoSrc = $templateData.data('logosrc') || '/oauth/static/main/img/logo.png';
    data.validateLength = $templateData.data('validatelength');
    data.validateLower = $templateData.data('validatelower');
    data.validateUpper = $templateData.data('validateupper');
    data.validateDigit = $templateData.data('validatedigit');
    data.validateSpecial = $templateData.data('validatespecial');
    data.atLeast = $templateData.data('atleast');
    data.validateUserName = $templateData.data('validateusername'); // // copyright
    // data.copyright = $templateData.data('copyright') || 'Copyright © The HZERO Author®.All rights reserved.';

    storeData.initData = data;
    storeData.rootEl = $('#app').get(0);
    initInitialData();
    initValidate();
  });
});