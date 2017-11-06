package com.dianrong.common.uniauth.cas.handler;

import com.dianrong.common.uniauth.cas.model.CasUsernamePasswordCredential;
import com.dianrong.common.uniauth.cas.service.UserLoginService;
import com.dianrong.common.uniauth.common.bean.dto.UserDto;
import com.dianrong.common.uniauth.common.enm.CasProtocol;
import com.dianrong.common.uniauth.common.util.StringUtil;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class UniauthAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

  @Autowired
  private UserLoginService userLoginService;

  @Override
  protected HandlerResult authenticateUsernamePasswordInternal(
      UsernamePasswordCredential credential) throws GeneralSecurityException, PreventedException {
    CasUsernamePasswordCredential casUserNameCredential = 
        (CasUsernamePasswordCredential) credential;
    String userName = StringUtil.trimCompatibleNull(casUserNameCredential.getUsername());
    String password = casUserNameCredential.getPassword();
    String tenancyCode = StringUtil.trimCompatibleNull(casUserNameCredential.getTenancyCode());
    
    // 登陆操作
    UserDto userInfo = userLoginService.login(userName, password, tenancyCode);
    Map<String, Object> attributes = new HashMap<String, Object>();
    attributes.put(CasProtocol.DianRongCas.getTenancyIdName(),
        StringUtil.translateIntegerToLong(userInfo.getTenancyId()));
    return createHandlerResult(credential,
        this.principalFactory.createPrincipal(userName, attributes), null);
  }

  @Override
  public boolean supports(final Credential credential) {
    return credential instanceof CasUsernamePasswordCredential;
  }
}
