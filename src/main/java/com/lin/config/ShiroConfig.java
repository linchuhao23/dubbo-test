package com.lin.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.AuthorizingAnnotationHandler;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.GuestAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.authz.aop.RoleAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.UserAnnotationMethodInterceptor;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.aop.SpringAnnotationResolver;
import org.apache.shiro.spring.security.interceptor.AopAllianceAnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationUtils;

@Configuration
public class ShiroConfig {

	/**
	 * 缓存
	 * 
	 * @return
	 */
	@Bean
	public org.apache.shiro.cache.ehcache.EhCacheManager ehCacheManager() {
		org.apache.shiro.cache.ehcache.EhCacheManager cacheManager = new EhCacheManager();
		cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
		return cacheManager;
	}

	/**
	 * 身份认证realm; (这个需要自己写，账号密码校验；权限等)
	 * 
	 * @return
	 */
	@Bean
	public ShiroDbRealm shiroDbRealm() {
		ShiroDbRealm myShiroRealm = new ShiroDbRealm();
		return myShiroRealm;
	}

	@Bean
	public org.apache.shiro.spring.LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new org.apache.shiro.spring.LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	@Bean
	public org.apache.shiro.mgt.SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(shiroDbRealm());
		securityManager.setCacheManager(ehCacheManager());
		SecurityUtils.setSecurityManager(securityManager);
		return securityManager;
	}

	/**
	 * 开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持; Controller才能使用@RequiresPermissions
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			@Qualifier("securityManager") org.apache.shiro.mgt.SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new MyAuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	/**
	 * ShiroFilterFactoryBean 处理拦截资源文件问题。 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
	 * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
	 *
	 * Filter Chain定义说明 1、一个URL可以配置多个Filter，使用逗号分隔 2、当设置多个过滤器时，全部验证通过，才视为通过
	 * 3、部分过滤器可指定参数，如perms，roles
	 *
	 */
	@Bean
	@Primary
	public ShiroFilterFactoryBean shirFilter(org.apache.shiro.mgt.SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);

		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/");
		// 未授权界面;
		// shiroFilterFactoryBean.setUnauthorizedUrl("/403");

		// 拦截器.
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

		// 配置不会被拦截的链接 顺序判断
		// 登录验证
		filterChainDefinitionMap.put("/login", "authc");

		// 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
		filterChainDefinitionMap.put("/logout", "logout");

		// 静态资源
		filterChainDefinitionMap.put("/static/**", "anon");

		// 后台管理
		// filterChainDefinitionMap.put("/admin", "roles[admin]");

		//
		filterChainDefinitionMap.put("/**", "user");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		Map<String, Filter> filters = new HashMap<String, Filter>();

		filters.put("authc", new MyFormAuthenticationFilter());

		filters.put("logout", new MyLogoutFilter());

		shiroFilterFactoryBean.setFilters(filters);

		return shiroFilterFactoryBean;
	}

	private class MyPermissionAnnotationHandler extends PermissionAnnotationHandler {

		public void assertAuthorized(MethodInvocation mi) throws AuthorizationException {
			Subject subject = getSubject();
			// 先校验类级别的注解
			RequiresPermissions classAnnotation = mi.getThis().getClass().getAnnotation(RequiresPermissions.class);
			if (classAnnotation != null) {
				String[] classPerms = classAnnotation.value();
				if (Logical.AND.equals(classAnnotation.logical())) {
					if (!subject.isPermittedAll(classPerms)) {
						throw new UnauthenticatedException("权限[" + Arrays.toString(classPerms) + "]匹配失败");
					}
				} else {
					boolean auth = false;
					for (String p : classPerms) {
						if (subject.isPermitted(p)) {
							auth = true;
							break;
						}
					}
					if (!auth) {
						throw new UnauthenticatedException("权限[" + Arrays.toString(classPerms) + "]匹配失败");
					}
				}
			}

			// 校验方法上面的
			RequiresPermissions methodAnnotation = mi.getMethod().getAnnotation(RequiresPermissions.class);
			if (methodAnnotation != null) {
				String[] methodPerms = methodAnnotation.value();
				if (Logical.AND.equals(methodAnnotation.logical())) {
					if (!subject.isPermittedAll(methodPerms)) {
						throw new UnauthenticatedException("权限[" + Arrays.toString(methodPerms) + "]匹配失败");
					}
				} else {
					boolean auth = false;
					for (String p : methodPerms) {
						if (subject.isPermitted(p)) {
							auth = true;
							break;
						}
					}
					if (!auth) {
						throw new UnauthenticatedException("权限[" + Arrays.toString(methodPerms) + "]匹配失败");
					}
				}
			}
		}
	}

	private class MyPermissionAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {

		public MyPermissionAnnotationMethodInterceptor() {
			super(new MyPermissionAnnotationHandler());
		}

		public MyPermissionAnnotationMethodInterceptor(AnnotationResolver resolver) {
			super(new MyPermissionAnnotationHandler(), resolver);
		}

		@Override
		public void assertAuthorized(MethodInvocation mi) throws AuthorizationException {
			try {
				AuthorizingAnnotationHandler handler = (AuthorizingAnnotationHandler) getHandler();
				if (handler instanceof MyPermissionAnnotationHandler) {
					((MyPermissionAnnotationHandler) handler).assertAuthorized(mi);
				} else {
					handler.assertAuthorized(getAnnotation(mi));
				}
			} catch (AuthorizationException ae) {
				// Annotation handler doesn't know why it was called, so add the information
				// here if possible.
				// Don't wrap the exception here since we don't want to mask the specific
				// exception, such as
				// UnauthenticatedException etc.
				if (ae.getCause() == null)
					ae.initCause(new AuthorizationException("Not authorized to invoke method: " + mi.getMethod()));
				throw ae;
			}
		}
	}

	private class MyAopAllianceAnnotationsAuthorizingMethodInterceptor
			extends AopAllianceAnnotationsAuthorizingMethodInterceptor implements MethodInterceptor {

		public MyAopAllianceAnnotationsAuthorizingMethodInterceptor() {
			List<AuthorizingAnnotationMethodInterceptor> interceptors = new ArrayList<>(5);
			// use a Spring-specific Annotation resolver - Spring's AnnotationUtils is nicer
			// than the
			// raw JDK resolution process.
			AnnotationResolver resolver = new SpringAnnotationResolver();
			// we can re-use the same resolver instance - it does not retain state:
			interceptors.add(new RoleAnnotationMethodInterceptor(resolver));
			interceptors.add(new MyPermissionAnnotationMethodInterceptor(resolver));
			interceptors.add(new AuthenticatedAnnotationMethodInterceptor(resolver));
			interceptors.add(new UserAnnotationMethodInterceptor(resolver));
			interceptors.add(new GuestAnnotationMethodInterceptor(resolver));
			setMethodInterceptors(interceptors);
		}
	}

	private class MyAuthorizationAttributeSourceAdvisor extends AuthorizationAttributeSourceAdvisor {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		private final Class<? extends Annotation>[] AUTHZ_ANNOTATION_CLASSES = new Class[] {
				RequiresPermissions.class, RequiresRoles.class, RequiresUser.class, RequiresGuest.class,
				RequiresAuthentication.class };

		public MyAuthorizationAttributeSourceAdvisor() {
			setAdvice(new MyAopAllianceAnnotationsAuthorizingMethodInterceptor());
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public boolean matches(Method method, Class targetClass) {
			Method m = method;

			if (isAuthzAnnotationPresent(m)) {
				return true;
			}

			// The 'method' parameter could be from an interface that doesn't have the
			// annotation.
			// Check to see if the implementation has it.
			if (targetClass != null) {
				try {
					m = targetClass.getMethod(m.getName(), m.getParameterTypes());
					if (isAuthzAnnotationPresent(m)) {
						return true;
					}
				} catch (NoSuchMethodException ignored) {
					// default return value is false. If we can't find the method, then obviously
					// there is no annotation, so just use the default return value.
				}
				//判断类上有没有注解
				return isAuthzAnnotationPresent(targetClass);
			}

			return false;
		}

		private boolean isAuthzAnnotationPresent(Method method) {
			for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
				Annotation a = AnnotationUtils.findAnnotation(method, annClass);
				if (a != null) {
					return true;
				}
			}
			return false;
		}
		
		private boolean isAuthzAnnotationPresent(Class<?> clazz) {
			for (Class<? extends Annotation> annClass : AUTHZ_ANNOTATION_CLASSES) {
				Annotation a = AnnotationUtils.findAnnotation(clazz, annClass);
				if (a != null) {
					return true;
				}
			}
			return false;
		}

	}
	
	/****************************************shiro**********************************************/
	public class ShiroDbRealm extends AuthorizingRealm {
		
		public static final String NO = "n";
		public static final String HASH_ALGORITHM = "SHA-1";
		
		/**
		 * 授权
		 */
		@Override
		protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			return info;
		}

		/**
		 * 身份验证
		 */
		@Override
		protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
				throws AuthenticationException {
			UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
			return new SimpleAuthenticationInfo(token.getUsername(), "123456", getName());
			//return new SimpleAuthenticationInfo(token.getUsername(), "pass", ByteSource.Util.bytes("123456789".getBytes()), getName());
		}

		/**
		 * 设定Password校验的Hash算法与迭代次数.
		 */
		/*@PostConstruct
		public void initCredentialsMatcher() {
			HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(HASH_ALGORITHM);
			matcher.setHashIterations(1024);
			setCredentialsMatcher(matcher);
		}*/

	}
	
	public class MyFormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter implements ApplicationContextAware {
		protected Logger logger = LoggerFactory.getLogger(this.getClass());
		
		ApplicationContext ac;
		/**
		 * 。
		 * @param token
		 * @param subject
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception  
		 * @see org.apache.shiro.web.filter.authc.FormAuthenticationFilter#onLoginSuccess(org.apache.shiro.authc.AuthenticationToken, org.apache.shiro.subject.Subject, javax.servlet.ServletRequest, javax.servlet.ServletResponse)
		 */
		@Override
	    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
	            ServletRequest request, ServletResponse response) throws Exception {
			WebUtils.issueRedirect(request, response, "/", null, true);
	    	HttpServletRequest _request = (HttpServletRequest)request;
	    	_request.getSession().setAttribute(ThreadContext.SUBJECT_KEY, subject);
	    	_request.getSession().setAttribute("user", subject.getPrincipal());
			postSuccess();
			return false;
		}
		
		protected void postSuccess() {
			
		}

		@Override
	    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
	            ServletRequest request, ServletResponse response) {
			request.setAttribute("username", request.getParameter("username"));
			request.setAttribute("password", request.getParameter("password"));
			setFailureAttribute(request, e);
			return true;
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			ac = applicationContext;
		}
	}
	
	public class MyLogoutFilter extends org.apache.shiro.web.filter.authc.LogoutFilter implements ApplicationContextAware {
		ApplicationContext ac;
		
		@Override
		protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
			return super.preHandle(request, response);
		}
		
		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			ac = applicationContext;
		}
	}
	/**************************************************************************************/

}