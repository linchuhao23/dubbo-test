package com.lin.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.Arguments;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import com.lin.config.ThymeleafConfiguration.StaticSourceElementProcessor.StaticSourceType;

@Configuration
public class ThymeleafConfiguration {

	@Bean("springResourceTemplateResolver")
	public SpringResourceTemplateResolver springResourceTemplateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode("HTML5");
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCacheable(false);
		return resolver;
	}

	@Bean("springTemplateEngine")
	public SpringTemplateEngine springTemplateEngine() {
		SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
		springTemplateEngine.setTemplateResolver(springResourceTemplateResolver());
		return springTemplateEngine;
	}

	@Bean("thymeleafViewResolver")
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(springTemplateEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		return thymeleafViewResolver;
	}

	@Bean("StaticSourceDialect")
	public IDialect staticSourceDialect() {
		return new StaticSourceDialect();
	}

	/**
	 * 静态资源过滤
	 * 
	 * @author Administrator
	 *
	 */
	public class StaticSourceDialect extends AbstractDialect {
		@Override
		public String getPrefix() {
			return "";
		}

		@Override
		public Set<IProcessor> getProcessors() {
			final Set<IProcessor> processors = new HashSet<>();
			processors.add(new StaticSourceElementProcessor(StaticSourceType.JAVASCRIPT));
			processors.add(new StaticSourceElementProcessor(StaticSourceType.CSS));
			processors.add(new StaticSourceElementProcessor(StaticSourceType.IMG));
			return processors;
		}
	}

	/**
	 * 静态资源加上版本号，每次启动会重新生成版本号，浏览器会重新加载静态资源，解决浏览器缓存问题缓存
	 * 
	 * @author linchuhao
	 *
	 */
	public static class StaticSourceElementProcessor extends AbstractElementProcessor {

		private final long version = System.currentTimeMillis();

		/**
		 * 静态资源的类型，tag 为标签名称， sourceLinkArgumentName 为资源获取路径的属性标签，precedence
		 * 为执行的顺序，可根据需求自行拓展添加
		 *
		 */
		public enum StaticSourceType {
			CSS("link", "href", (Integer.MAX_VALUE - 8)), //
			IMG("img", "src", (Integer.MAX_VALUE - 9)), //
			JAVASCRIPT("script", "src", (Integer.MAX_VALUE - 10)); //
			private String sourceLinkArgumentName;
			private String tag;
			private int precedence;

			private StaticSourceType(String tag, String sourceLinkArgumentName, int precedence) {
				this.tag = tag;
				this.sourceLinkArgumentName = sourceLinkArgumentName;
				this.precedence = precedence;
			}

			public String getTag() {
				return this.tag;
			}

			public String getSourceLinkArgumentName() {
				return this.sourceLinkArgumentName;
			}

			public int getPrecedence() {
				return this.precedence;
			}
		}

		private final StaticSourceType type;

		public StaticSourceElementProcessor(StaticSourceType type) {
			super(type.getTag());
			this.type = type;
		}

		@Override
		protected ProcessorResult processElement(Arguments arguments, Element element) {
			String sourceLinkArgumentName = type.sourceLinkArgumentName;
			String src = element.getAttributeValue(sourceLinkArgumentName);
			if (src != null) {
				if (src.indexOf("?") != -1) {
					src = src + "&v=" + version;
				} else {
					src = src + "?v=" + version;
				}
				element.setAttribute(sourceLinkArgumentName, src);
			}
			return ProcessorResult.OK;
		}

		@Override
		public int getPrecedence() {
			return type.precedence;
		}

	}

}
